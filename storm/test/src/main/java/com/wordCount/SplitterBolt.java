package com.wordCount;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.tuple.Fields;

public class SplitterBolt extends BaseBasicBolt{

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String sentence = tuple.getStringByField("line");
        String[] words = sentence.split("\\s+");
        for (String word : words) {
            word = word.toLowerCase().replaceAll("[^a-zA-Z]", "");
            if (!word.isEmpty()) {
                collector.emit(new Values(word));
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }
}
