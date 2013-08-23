package de.unima.dws.dbpediagraph.graphdb.disambiguate;

public class WeightedUri {
	private String uri;
	private double weight;

	public WeightedUri(String uri, double weight) {
		super();
		this.uri = uri;
		this.weight = weight;
	}

	public String getUri() {
		return uri;
	}

	public double getWeight() {
		return weight;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
}
