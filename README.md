Storm-flink pipeline to read file and send word count in kafka using storm, read kafka and aggregate data using flink.

To run
  1) Run mvn clean package 
  2) Execute deploy.sh script
  3) docker exec -it storm-nimbus storm jar /wordcount.jar com.wordCount.Topology my-wordcount-topology -> To run storm (Read from file and produce word count in kafka)
  4) docker exec -it jobmanager ./bin/flink run -py /opt/flink/count_agg.py -> To run flink (Reads from kafka and aggregates the word count)
