CREATE KEYSPACE IF NOT EXISTS storm_flink_ks 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS storm_flink_ks.word_counts (
    word text,
    total_count bigint,
    PRIMARY KEY (word)
);
