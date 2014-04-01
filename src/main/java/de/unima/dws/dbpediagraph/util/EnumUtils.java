package de.unima.dws.dbpediagraph.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

/**
 * Helper class for reading enum values from a Configuration file.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class EnumUtils {
	public static <E extends Enum<E>> E fromConfig(Class<E> enumClass, Configuration config, String configKey,
			boolean returnNullIfNotPresent) {
		String enumName = config.getString(configKey);
		if (returnNullIfNotPresent && enumName == null)
			return null;
		return fromString(enumClass, enumName);
	}

	public static <E extends Enum<E>> E fromString(Class<E> enumClass, String enumName) {
		try {
			return Enum.valueOf(enumClass, enumName);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(String.format("Unknown enum name '%s' specified for enum '%s'.",
					enumName, enumClass.getName()), e);
		}
	}

	public static <E extends Enum<E>> List<E> enumsfromConfig(Class<E> enumClass, Configuration config, String configKey) {
		@SuppressWarnings("unchecked")
		// apache commons config does not support generics
		List<String> enumNames = config.getList(configKey);

		List<E> enumList = new ArrayList<>();
		for (String enumName : enumNames)
			enumList.add(Enum.valueOf(enumClass, enumName.trim()));
		return enumList;
	}

	// Suppress default constructor for non-instantiability
	private EnumUtils() {
	}

}
