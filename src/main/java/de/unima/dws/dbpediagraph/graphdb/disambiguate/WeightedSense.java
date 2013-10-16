package de.unima.dws.dbpediagraph.graphdb.disambiguate;

/**
 * Immutable weighted sense class consisting of a sense (DBpedia Uri) of a word and an according weight.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class WeightedSense implements Comparable<WeightedSense> {
	private final String sense;
	private final double weight;

	public WeightedSense(String sense, double weight) {
		this.sense = sense;
		this.weight = weight;
	}

	@Override
	public int compareTo(WeightedSense o) {
		// TODO change and test
		// return Double.compare(weight, o.weight);
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

	@Override
	public String toString() {
		return sense + "->" + weight;
	}
}
