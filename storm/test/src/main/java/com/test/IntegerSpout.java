package com.test;

import java.util.Map;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class IntegerSpout extends BaseRichSpout {

    SpoutOutputCollector spoutOutputCollector;
    private Integer index = 0;

    @Override
    public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
        // Initialization code here
        this.spoutOutputCollector = collector;
    }
    
    @Override
    public void nextTuple() {
        // Emit integers here
        if (index < 100) {
            this.spoutOutputCollector.emit(new Values(index));
            index++;
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("field"));
    }
}
