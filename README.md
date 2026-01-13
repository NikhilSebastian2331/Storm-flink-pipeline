Storm-flink pipeline to read file and send word count in kafka using storm, read kafka and aggregate data using flink.
To run
  docker-compose up -d
  mvn clean package -> Create FAT jar
  docker cp target/test-1.0-SNAPSHOT.jar storm-nimbus:/wordcount.jar
  docker cp ../../notes.txt storm-supervisor:/tmp/notes.txt
  docker exec -it storm-nimbus storm jar /wordcount.jar com.wordCount.Topology my-wordcount-topology
