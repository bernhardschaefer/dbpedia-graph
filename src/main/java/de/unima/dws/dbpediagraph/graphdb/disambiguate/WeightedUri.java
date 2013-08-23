package de.unima.dws.dbpediagraph.graphdb.disambiguate;

public class WeightedUri implements Comparable<WeightedUri> {
	private String uri;
	private double weight;

	public WeightedUri(String uri, double weight) {
		super();
		this.uri = uri;
		this.weight = weight;
	}

	@Override
	public int compareTo(WeightedUri o) {
		double epsilon = 0.001;
		if (Math.abs(weight - o.weight) < epsilon) {
			return 0;
		}
		return weight < o.weight ? -1 : 1;
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
