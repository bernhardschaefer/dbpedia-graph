package de.unima.dws.dbpediagraph.weights;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.google.common.base.Stopwatch;

public class KryoMap {
	private static final Logger logger = LoggerFactory.getLogger(KryoMap.class);
	
	private static final Kryo mapKryo;
	static {
		mapKryo = new Kryo();
		mapKryo.register(HashMap.class);

		MapSerializer serializer = new MapSerializer();
		mapKryo.register(HashMap.class, serializer);
		serializer.setKeyClass(String.class, mapKryo.getSerializer(String.class));
		serializer.setKeysCanBeNull(false);
		serializer.setValueClass(Integer.class, mapKryo.getSerializer(Integer.class));
		serializer.setValuesCanBeNull(false);
	}

	static void serializeMap(Map<String, Integer> map, Configuration config) throws FileNotFoundException {
		Stopwatch stopwatch = Stopwatch.createStarted();
		String fileName = config.getString(OccurrenceCounts.CONFIG_EDGE_COUNTS_FILE);
		Kryo kryo = KryoMap.getDefault();
		Output output = new Output(new FileOutputStream(fileName));
		kryo.writeObject(output, map);
		output.close();
		logger.info("Serialized map in {} to file {} ", stopwatch, fileName);
	}
	
	static Kryo getDefault() {
		return mapKryo;
	}
}
