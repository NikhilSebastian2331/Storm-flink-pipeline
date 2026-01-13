package com.wordCount;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class FIleReadSpout extends BaseRichSpout{

    private SpoutOutputCollector collector;
    private BufferedReader reader;

    @SuppressWarnings("rawtypes")
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        try {
            //this.reader = new BufferedReader(new FileReader("/home/nikhil1332/storm_flink/notes.txt"));
            //cluster
            this.reader = new BufferedReader(new FileReader("/tmp/notes.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void nextTuple() {
        try {
            String line = reader.readLine();
            if (line != null) {
                collector.emit(new Values(line));
            } else {
                Thread.sleep(1000); // Sleep for a while if no more lines       
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("line"));
    }

}
