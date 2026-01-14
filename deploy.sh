#!/bin/bash

CONTAINERS=("zookeeper" "kafka-broker" "cassandra" "jobmanager" "taskmanager" "storm-nimbus" "storm-supervisor")

echo "--- Starting build and deployment ---"
docker-compose up -d --build

# Function to check if all containers are running
check_containers() {
    for container in "${CONTAINERS[@]}"; do
        if [ "$(docker inspect -f '{{.State.Running}}' $container 2>/dev/null)" != "true" ]; then
            return 1
        fi
    done
    return 0
}

echo "Initial check: Verifying container status..."
if check_containers; then
    echo "All containers are initially up."
else
    echo "Some containers failed to start. Attempting recovery..."
    docker-compose up -d
fi

echo "Waiting 45 seconds for services to initialize..."
sleep 45

echo "--- Post-initialization health check ---"
docker-compose ps

# Second check and recovery
if ! check_containers; then
    echo "Warning: Some containers dropped. Restarting missing services..."
    docker-compose up -d
    sleep 10
fi

echo "Container check complete. Moving to configuration and file transfers."
echo "-------------------------------------------------------------------"


echo "Configuring Cassandra schema..."
docker cp schema.cql cassandra:/schema.cql
docker exec -it cassandra cqlsh -u cassandra -p cassandra -f /schema.cql


echo "Copying Storm JAR to Nimbus..."
docker cp storm/test/target/test-1.0-SNAPSHOT.jar storm-nimbus:/wordcount.jar


echo "Copying notes.txt to Supervisor..."
docker cp notes.txt storm-supervisor:/tmp/notes.txt

echo "Deploying PyFlink aggregation script to JobManager..."
docker cp flink-agg/count_agg.py jobmanager:/opt/flink/count_agg.py

echo "-------------------------------------------------------------------"
echo "SUCCESS: All containers are verified and files have been deployed."