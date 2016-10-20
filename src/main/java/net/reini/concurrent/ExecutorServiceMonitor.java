package net.reini.concurrent;

import java.util.concurrent.TimeUnit;

public class ExecutorServiceMonitor implements ExecutorServiceMonitorMXBean {
	InstrumentedThreadPoolExecutor threadPool;

	long fromNanoToSeconds(long nanos) {
		return TimeUnit.NANOSECONDS.toSeconds(nanos);
	}

	@Override
	public double getRequestPerSecondRetirementRate() {
		return (double) this.threadPool.getNumberOfRequestsRetired()
				/ fromNanoToSeconds(threadPool.getAggregateInterRequestArrivalTime());
	}

	@Override
	public double getAverageServiceTime() {
		return fromNanoToSeconds(threadPool.getTotalServiceTime())
				/ (double) this.threadPool.getNumberOfRequestsRetired();
	}

	@Override
	public double getAverageTimeWaitingInPool() {
		return fromNanoToSeconds(this.threadPool.getTotalPoolTime())
				/ (double) this.threadPool.getNumberOfRequestsRetired();
	}

	@Override
	public double getAverageResponseTime() {
		return this.getAverageServiceTime() + this.getAverageTimeWaitingInPool();
	}

	@Override
	public double getEstimatedAverageNumberOfActiveRequests() {
		return getRequestPerSecondRetirementRate() * (getAverageServiceTime() + getAverageTimeWaitingInPool());
	}

	@Override
	public double getRatioOfDeadTimeToResponseTime() {
		double poolTime = (double) this.threadPool.getTotalPoolTime();
		return poolTime / (poolTime + (double) threadPool.getTotalServiceTime());
	}

	@Override
	public double v() {
		return getEstimatedAverageNumberOfActiveRequests() / (double) Runtime.getRuntime().availableProcessors();
	}
}