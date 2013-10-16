package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Set;

public class WeightedSenseAssignments implements Comparable<WeightedSenseAssignments> {
	private final Set<String> senseAssignments;
	private final double weight;

	public WeightedSenseAssignments(Set<String> senseAssignments, double weight) {
		this.senseAssignments = senseAssignments;
		this.weight = weight;
	}

	@Override
	public int compareTo(WeightedSenseAssignments o) {
		// TODO change and test
		// return Double.compare(weight, o.weight);
		double epsilon = 0.001;
		if (Math.abs(weight - o.weight) < epsilon) {
			return 0;
		}
		return weight < o.weight ? -1 : 1;
	}

	public Set<String> getSenseAssignments() {
		return senseAssignments;
	}

	public double getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return senseAssignments + "->" + weight;
	}
}
