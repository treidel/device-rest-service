package com.fancypants.storm.processing.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.fluent.GroupedStream;
import storm.trident.spout.IOpaquePartitionedTridentSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

import com.fancypants.common.CommonScanMe;
import com.fancypants.data.DataScanMe;
import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.data.repository.HourlyRecordRepository;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.message.MessageScanMe;
import com.fancypants.storm.StormScanMe;
import com.fancypants.storm.device.record.mapping.EnergyConsumptionEntityMapper;
import com.fancypants.storm.device.record.mapping.EnergyConsumptionTupleMapper;
import com.fancypants.storm.device.record.mapping.RawRecordTupleMapper;
import com.fancypants.storm.processing.StormProcessingScanMe;
import com.fancypants.storm.processing.aggregate.HourlyEnergyCalculationAggregator;
import com.fancypants.storm.processing.aggregate.UsageAggregator;
import com.fancypants.storm.processing.bolt.DuplicateDetectionBolt;
import com.fancypants.storm.processing.state.TopicNotifierStateFactory;
import com.fancypants.storm.processing.state.TopicNotifierStateUpdater;
import com.fancypants.storm.processing.state.UsageStateFactory;
import com.fancypants.storm.processing.state.UsageStateUpdater;
import com.fancypants.usage.UsageScanMe;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, DataScanMe.class,
		DeviceScanMe.class, UsageScanMe.class, MessageScanMe.class,
		StormScanMe.class, StormProcessingScanMe.class })
public class RecordsConfig {
	private final static Logger LOG = LoggerFactory
			.getLogger(RecordsConfig.class);

	private final static String STORM_TOPOLOGY = "raw_records_topology";
	private final static String TRIDENT_TOPOLOGY = "filtered_records_topology";
	private final static String RAW_RECORDS_SPOUT = "raw_record_spout";
	private final static String DUPLICATE_DETECTION_BOLT = "duplicate_detection_bolt";
	private final static String PERSIST_BOLT = "persist_bolt";
	private final static String FILTERED_RECORDS_TXID = "filtered_records";

	@Autowired
	private HourlyRecordRepository hourlyRepository;

	@Autowired
	private UsageStateFactory usageStateFactory;

	@Autowired
	private DuplicateDetectionBolt duplicateDetectionBolt;

	@Autowired
	private TopicNotifierStateFactory topicNotifierStateFactory;

	@Autowired
	private UsageStateUpdater usageStateUpdater;

	@Autowired
	private TopicNotifierStateUpdater topicNotifierUpdater;

	@Autowired
	private HourlyEnergyCalculationAggregator hourlyEnergyCalculationAggregator;

	@Autowired
	private UsageAggregator usageAggregator;

	@Autowired
	private IRichSpout kinesisSpout;

	@Autowired
	private IRichBolt filteredBolt;

	@SuppressWarnings("rawtypes")
	@Autowired
	private IOpaquePartitionedTridentSpout filteredSpout;

	@Autowired
	private Config filteredConfig;

	@PostConstruct
	public void init() throws Exception {
		LOG.trace("RecordsConfig.init enter");

		// config object for all topologies
		Config conf = new Config();

		// populate the filtered config
		conf.putAll(filteredConfig);

		// create the regular storm topology
		TopologyBuilder stormTopology = new TopologyBuilder();

		// setup the raw topology
		stormTopology.setSpout(RAW_RECORDS_SPOUT, kinesisSpout);
		// duplicate detection takes records from the spout
		stormTopology.setBolt(DUPLICATE_DETECTION_BOLT, duplicateDetectionBolt)
				.shuffleGrouping(RAW_RECORDS_SPOUT);
		// the kafka bolt takes records from the duplicate detection
		stormTopology.setBolt(PERSIST_BOLT, filteredBolt).shuffleGrouping(
				DUPLICATE_DETECTION_BOLT);

		// create the trident topology
		TridentTopology tridentTopology = new TridentTopology();

		// setup the filtered stream
		Stream filteredStream = tridentTopology.newStream(
				FILTERED_RECORDS_TXID, filteredSpout);
		Stream node1 = filteredStream.partitionBy(new Fields(
				RawRecordEntity.DEVICE_ATTRIBUTE));
		Stream node2 = node1.aggregate(RawRecordTupleMapper.getOutputFields(),
				hourlyEnergyCalculationAggregator,
				EnergyConsumptionTupleMapper.getOutputFields());
		GroupedStream node3 = node2.groupBy(new Fields(
				EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE,
				EnergyConsumptionRecordEntity.DATE_ATTRIBUTE));
		Stream node4 = node3.aggregate(new Fields(
				EnergyConsumptionEntityMapper.ATTRIBUTES), usageAggregator,
				new Fields("result"));
		node4.partitionPersist(usageStateFactory, new Fields("result"),
				usageStateUpdater);
		node4.partitionPersist(topicNotifierStateFactory, new Fields("result"),
				topicNotifierUpdater);

		// for now create a local cluster
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(STORM_TOPOLOGY, conf,
				stormTopology.createTopology());
		cluster.submitTopology(TRIDENT_TOPOLOGY, conf, tridentTopology.build());

		LOG.trace("RecordsConfig.init exit");
	}
}
