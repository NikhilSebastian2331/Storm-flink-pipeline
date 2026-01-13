package com.test;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

public class Topology {
 
    public static void main(String[] args) throws Exception {
        
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("integer-spout", new IntegerSpout());
        builder.setBolt("multiplier-bolt", new MultiplierBolt()).shuffleGrouping("integer-spout");

        Config config = new Config();
        config.setDebug(true);

        LocalCluster cluster = new LocalCluster();
        try{
            cluster.submitTopology("integer-multiplier-topology", config, builder.createTopology());
            Thread.sleep(10000);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            cluster.shutdown(); 
        }
        
    }
}
