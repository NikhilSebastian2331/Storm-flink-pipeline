import json
import time
import random
from kafka import KafkaProducer

# Connect to Kafka (Use localhost if running script on your VM host)
producer = KafkaProducer(
    bootstrap_servers=['localhost:9092'],
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

topics = "storm-request-topic"

print("Starting stream...")
try:
    while True:
        data = {
            "transaction_id": random.randint(1000, 9999),
            "amount": round(random.uniform(10.0, 500.0), 2),
            "timestamp": time.time()
        }
        producer.send(topics, value=data)
        print(f"Sent: {data}")
        time.sleep(10) # Send one message per 10 seconds
except KeyboardInterrupt:
    print("Stopped.")
finally:
    producer.close()