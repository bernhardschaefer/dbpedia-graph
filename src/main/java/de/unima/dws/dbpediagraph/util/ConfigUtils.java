package de.unima.dws.dbpediagraph.util;

import org.apache.commons.configuration.Configuration;

public class ConfigUtils {
	public static <E extends Enum<E>> E enumFromConfig(Class<E> enumClass, Configuration config, String configKey) {
		E enumType;
		String edgeWeightsImplName = config.getString(configKey);
		try {
			enumType = Enum.valueOf(enumClass, edgeWeightsImplName);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
					String.format(
							"Unknown edge weight type '%s' specified in config for key '%s'.",
							edgeWeightsImplName, configKey), e);
		}
		return enumType;
	}

	// Suppress default constructor for non-instantiability
	private ConfigUtils() {
	}
}
