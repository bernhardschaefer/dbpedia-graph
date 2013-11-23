package de.unima.dws.dbpediagraph.graphdb.search;

/**
 * Wraps the default {@link aima.core.search.local.Scheduler}.
 * 
 * @author bernhard
 * 
 */
class AimaScheduler implements Scheduler {
	private final aima.core.search.local.Scheduler scheduler;

	AimaScheduler(int limit) {
		int k = 20;
		double lam = 0.045;
		scheduler = new aima.core.search.local.Scheduler(k, lam, limit);
	}

	public AimaScheduler() {
		scheduler = new aima.core.search.local.Scheduler();
	}

	@Override
	public double getTemperature(int time) {
		return scheduler.getTemp(time);
	}

}
