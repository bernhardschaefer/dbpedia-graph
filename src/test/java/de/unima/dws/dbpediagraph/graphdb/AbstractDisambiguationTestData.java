package de.unima.dws.dbpediagraph.graphdb;

public abstract class AbstractDisambiguationTestData {
	protected static final double DELTA = 0.005;

	public AbstractDisambiguationTestData() {
	}

	public abstract void checkDisambiguationResults();

}
