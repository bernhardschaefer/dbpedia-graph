package de.unima.dws.dbpediagraph.util;

import java.io.*;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ForwardingMap;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.*;
import com.sleepycat.util.RuntimeExceptionWrapper;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.graph.GraphWeightsFactory;

/**
 * A class to wrap a Berkeley DB as a Map
 * 
 * @author ponzetto, Bernhard Schäfer
 * 
 * @param <K>
 *            class of the keys
 * @param <V>
 *            class of the values
 */
public class BerkeleyDB<K, V> extends ForwardingMap<K, V> implements PersistentMap<K, V> {
	private transient Environment dbEnv;
	private transient Database db;
	private transient Database classCatalogDb;

	private transient EntryBinding<K> keyDatabinding;
	private transient EntryBinding<V> valueDatabinding;

	private transient StoredMap<K, V> mapView;

	public BerkeleyDB(final File location, final String dbName, final Class<K> keyClass, final Class<V> valueClass,
			boolean allowSortedDuplicates, boolean readOnly) {
		createDBEnvironment(location);
		initDB(dbName, keyClass, valueClass, allowSortedDuplicates, readOnly);
	}

	@Override
	protected Map<K, V> delegate() {
		return mapView;
	}

	private void createDBEnvironment(final File location) {
		if (!location.exists())
			location.mkdirs();

		try {
			final EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setCachePercent(60);
			envConfig.setAllowCreate(true);
			// log files are 100 MB each
			envConfig.setConfigParam(EnvironmentConfig.LOG_FILE_MAX, "100000000");
			this.dbEnv = new Environment(location, envConfig);
		} catch (DatabaseException dbe) {
			dbe.printStackTrace();
		}
	}

	private void initDB(final String dbName, final Class<K> keyClass, final Class<V> valueClass,
			boolean allowSortedDuplicates, boolean readOnly) {
		try {
			final DatabaseConfig dbConfig = new DatabaseConfig();
			if (!readOnly)
				dbConfig.setAllowCreate(true);
			dbConfig.setReadOnly(readOnly);

			/* Create the class db */
			this.classCatalogDb = dbEnv.openDatabase(null, dbName + "_class_catalog", dbConfig);

			/* Create the class catalog */
			final StoredClassCatalog classCatalog = new StoredClassCatalog(classCatalogDb);

			/* Create the data bindings */
			this.keyDatabinding = TupleBinding.getPrimitiveBinding(keyClass);
			if (keyDatabinding == null)
				keyDatabinding = new SerialBinding<K>(classCatalog, keyClass);

			this.valueDatabinding = TupleBinding.getPrimitiveBinding(valueClass);
			if (valueDatabinding == null)
				new SerialBinding<V>(classCatalog, valueClass);

			dbConfig.setSortedDuplicates(allowSortedDuplicates);

			/* Create the db */
			this.db = dbEnv.openDatabase(null, dbName, dbConfig);

			/* Create the map view */
			this.mapView = new StoredMap<K, V>(db, keyDatabinding, valueDatabinding, true);
		} catch (DatabaseException dbe) {
			dbe.printStackTrace();
		}
	}

	public Collection<V> getAll(final K key) {
		try {
			return mapView.duplicates(key);
		} catch (RuntimeExceptionWrapper dbe) {
			dbe.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		try {
			this.db.close();
			this.classCatalogDb.close();
			this.dbEnv.close();
		} catch (DatabaseException dbe) {
			dbe.printStackTrace();
		}
	}

	public StoredMap<K, V> getAsMap() {
		return mapView;
	}

	public Environment getDBEnvironment() {
		return dbEnv;
	}

	public static class Builder<K, V> {
		// required parameters
		private final File location;
		private final String dbName;
		private final Class<K> keyClass;
		private final Class<V> valueClass;

		// optional parameters - initialized to default values
		private boolean allowSortedDuplicates = false;
		private boolean readOnly = false;

		public Builder(File location, String dbName, Class<K> keyClass, Class<V> valueClass) {
			this.location = location;
			this.dbName = dbName;
			this.keyClass = keyClass;
			this.valueClass = valueClass;
		}

		public Builder<K, V> allowSortedDuplicates(boolean allowSortedDuplicates) {
			this.allowSortedDuplicates = allowSortedDuplicates;
			return this;
		}

		public Builder<K, V> readOnly(boolean readOnly) {
			this.readOnly = readOnly;
			return this;
		}

		public BerkeleyDB<K, V> build() {
			return new BerkeleyDB<>(location, dbName, keyClass, valueClass, allowSortedDuplicates, readOnly);
		}
	}

	public static <V> void queryContent(BerkeleyDB<String, V> db) {
		String line = "";

		while (true) {
			System.out.println("Please enter a key, then press <return> (type \"exit\" to quit)");
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

			try {
				line = input.readLine();
				if (line.startsWith("exit")) {
					System.out.println("QUITTING, thank you ... ");
					break;
				}
				System.out.println("SHOWING DB RECORD: " + line + " => " + db.getAll(line));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		boolean clear = false;
		boolean readOnly = true;
		PersistentMap<String, Integer> persistentMap = GraphWeightsFactory.loadPersistentWeightsMap(
				GraphConfig.config(), clear, readOnly);
		if (persistentMap instanceof BerkeleyDB) {
			BerkeleyDB<String, Integer> db = (BerkeleyDB<String, Integer>) persistentMap;
			queryContent(db);
		}
	}

}
