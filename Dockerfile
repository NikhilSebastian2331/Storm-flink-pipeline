FROM apache/flink:1.18.0-java11

# Install Python and Pip
RUN apt-get update -y && \
    apt-get install -y python3 python3-pip python3-dev && \
    rm -rf /var/lib/apt/lists/*

RUN ln -s /usr/bin/python3 /usr/bin/python

# Install PyFlink
RUN pip3 install apache-flink==1.18.0 cassandra-driver

# Install kafka jar
RUN wget -P /opt/flink/lib/ https://repo1.maven.org/maven2/org/apache/flink/flink-sql-connector-kafka/3.0.1-1.18/flink-sql-connector-kafka-3.0.1-1.18.jar

# Install Cassandra connector jars
#RUN wget -P /opt/flink/lib/ https://repo1.maven.org/maven2/org/apache/flink/flink-connector-cassandra_2.12/3.1.0-1.17/flink-connector-cassandra_2.12-3.1.0-1.17.jar
#RUN wget -P /opt/flink/lib/ https://repo1.maven.org/maven2/com/datastax/cassandra/cassandra-driver-core/3.11.2/cassandra-driver-core-3.11.2.jar
#RUN wget -P /opt/flink/lib/ https://repo1.maven.org/maven2/com/google/guava/guava/30.1.1-jre/guava-30.1.1-jre.jar
#RUN wget -P /opt/flink/lib/ https://repo1.maven.org/maven2/io/dropwizard/metrics/metrics-core/3.1.2/metrics-core-3.1.2.jar