package de.unima.dws.dbpediagraph.graphdb.disambiguate;

public class WeightedSense implements Comparable<WeightedSense> {
	private String sense;
	private double weight;

	public WeightedSense(String sense, double weight) {
		super();
		this.sense = sense;
		this.weight = weight;
	}

	@Override
	public int compareTo(WeightedSense o) {
		double epsilon = 0.001;
		if (Math.abs(weight - o.weight) < epsilon) {
			return 0;
		}
		return weight < o.weight ? -1 : 1;
	}

	public String getSense() {
		return sense;
	}

	public double getWeight() {
		return weight;
	}

	public void setSense(String sense) {
		this.sense = sense;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return sense + "->" + weight;
	}
}
