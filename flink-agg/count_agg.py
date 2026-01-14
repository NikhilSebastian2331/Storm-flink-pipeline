import os
from pyflink.datastream import StreamExecutionEnvironment, MapFunction
from pyflink.table import StreamTableEnvironment
from pyflink.common import Types
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider

# 1. Define the Sink Logic as a MapFunction
# This ensures the connection is opened on the worker, avoiding "Pickle" errors.
class CassandraSinkFunction(MapFunction):
    def __init__(self):
        self.cluster = None
        self.session = None
        self.stmt = None

    def open(self, runtime_context):
        # This runs once on each worker thread
        auth_provider = PlainTextAuthProvider(username='cassandra', password='cassandra')
        # Use the service name 'cassandra' as defined in docker-compose
        self.cluster = Cluster(['cassandra'], port=9042, auth_provider=auth_provider)
        self.session = self.cluster.connect('storm_flink_ks')
        
        # Prepare the statement for efficiency
        self.stmt = self.session.prepare(
            "INSERT INTO word_counts (word, total_count) VALUES (?, ?)"
        )
        print("Connected to Cassandra successfully.")

    def map(self, row):
        # Row data from the changelog stream: row[0] = word, row[1] = total_count
        word = row[0]
        count = row[1]
        
        try:
            self.session.execute(self.stmt, [word, count])
        except Exception as e:
            print(f"Error writing to Cassandra: {str(e)}")
            
        return f"Processed: {word} -> {count}"

    def close(self):
        # Clean up connection when the job is stopped
        if self.cluster:
            self.cluster.shutdown()

def run_word_count_aggregator():
    # 2. Initialize Environments
    env = StreamExecutionEnvironment.get_execution_environment()
    # Explicitly set the python executable path
    env.set_python_executable("/usr/bin/python")
    
    t_env = StreamTableEnvironment.create(env)

    # 3. Define Source Table (Kafka)
    # Ensure 'flink-sql-connector-kafka' is in /opt/flink/lib
    t_env.execute_sql("""
        CREATE TABLE kafka_source (
            word STRING,
            `count` INT
        ) WITH (
            'connector' = 'kafka',
            'topic' = 'flink-response-topic',
            'properties.bootstrap.servers' = 'kafka-broker:29092',
            'properties.group.id' = 'flink-aggregator-group',
            'scan.startup.mode' = 'earliest-offset',
            'format' = 'json'
        )
    """)

    # 4. Process and Aggregate
    # SUM(count) results in a BIGINT, which matches Cassandra's bigint
    result_table = t_env.sql_query("""
        SELECT word, SUM(`count`) as total_count
        FROM kafka_source
        GROUP BY word
    """)

    # 5. Convert Table to DataStream and Sink to Cassandra
    # We use to_changelog_stream because GROUP BY creates updates (retractions)
    ds = t_env.to_changelog_stream(result_table)
    
    # Route the stream through our custom Cassandra sink
    ds.map(CassandraSinkFunction(), output_type=Types.STRING())

    # 6. Execute the Pipeline
    print("Starting Flink Job...")
    env.execute("PyFlink Kafka-to-Cassandra Aggregator")

if __name__ == '__main__':
    run_word_count_aggregator()