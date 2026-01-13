package com.wordCount;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

/* Read from file in container and send word count to kafka repsonse topic
    TODO: Replace read from file with read from Kafka request topic
*/

public class Topology {

    public static void main(String[] args) throws Exception {
        
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("file-reader-spout", new FIleReadSpout());
        builder.setBolt("splitter-bolt", new SplitterBolt()).shuffleGrouping("file-reader-spout");
        builder.setBolt("counter-bolt", new CounterBolt()).fieldsGrouping("splitter-bolt", new org.apache.storm.tuple.Fields("word"));

        Config config = new Config();

        // CLuster mode
        if (args != null && args.length > 0) {
            // CLUSTER MODE (Docker)
            // We set the number of workers (Docker processes) to use
            config.setNumWorkers(3); 
            // Turn off debug in cluster to keep logs clean
            config.setDebug(false); 
            
            // args[0] will be the name you give the topology in the command line
            StormSubmitter.submitTopology(args[0], config, builder.createTopology());
        }else{
            config.setDebug(true);
            LocalCluster cluster = new LocalCluster();
            try{
                cluster.submitTopology("word-count-topology", config, builder.createTopology());
                Thread.sleep(10000);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                cluster.shutdown(); 
            }
        }
    }
}
