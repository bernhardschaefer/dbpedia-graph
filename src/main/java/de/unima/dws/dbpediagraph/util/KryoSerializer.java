package de.unima.dws.dbpediagraph.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.google.common.base.Stopwatch;

/**
 * Helper to serialize and deserialize {@link Map}s.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class KryoSerializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(KryoSerializer.class);

	private static final Kryo kryoStringIntegerMap;
	static {
		kryoStringIntegerMap = new Kryo();
		kryoStringIntegerMap.register(HashMap.class);

		MapSerializer serializer = new MapSerializer();
		kryoStringIntegerMap.register(HashMap.class, serializer);
		serializer.setKeyClass(String.class, kryoStringIntegerMap.getSerializer(String.class));
		serializer.setKeysCanBeNull(false);
		serializer.setValueClass(Integer.class, kryoStringIntegerMap.getSerializer(Integer.class));
		serializer.setValuesCanBeNull(false);
	}

	public static void serializeStringIntegerMap(Map<String, Integer> map, File file) throws FileNotFoundException {
		Stopwatch stopwatch = Stopwatch.createStarted();
		Output output = new Output(new FileOutputStream(file));
		kryoStringIntegerMap.writeObject(output, map);
		output.close();
		LOGGER.info("Serialized map in {} to file {} ", stopwatch, file);
	}

	public static Map<String, Integer> deserializeStringIntegerMap(File file) throws FileNotFoundException {
		LOGGER.info("Start loading " + file.getName());
		Stopwatch stopwatch = Stopwatch.createStarted();
		Input input = new Input(new FileInputStream(file));
		@SuppressWarnings("unchecked")
		Map<String, Integer> map = kryoStringIntegerMap.readObject(input, HashMap.class);
		input.close();
		LOGGER.info("Deserialized map {} with {} entries in {}", file.getName(), map.size(), stopwatch);
		return map;
	}

}
