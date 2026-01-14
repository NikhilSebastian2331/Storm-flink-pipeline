package com.wordCount;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
public class CounterBolt extends BaseBasicBolt {

    private Map<String, Integer> counts = new HashMap<>();
    private transient KafkaProducer<String, String> producer; // Use 'transient' for serialization
    private String topic = "flink-response-topic";

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context) {
        // 1. Initialize Kafka Producer Properties
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka-broker:29092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.producer = new KafkaProducer<>(props);
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String word = tuple.getStringByField("word");
        
        String json = String.format("{\"word\": \"%s\", \"count\": 1}", word);

        producer.send(new ProducerRecord<>(topic, word, json));

        //System.out.println("RESULT: " + word + " -> " + count);


        // Top 3 words
        /*List<Map.Entry<String, Integer>> list = new ArrayList<>(counts.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        System.out.println("\nTOP 3 WORDS");
        for (int i = 0; i < Math.min(3, list.size()); i++) {
            Map.Entry<String, Integer> entry = list.get(i);
            System.out.println((i + 1) + ". " + entry.getKey() + ": " + entry.getValue());
        }*/

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {}

}
