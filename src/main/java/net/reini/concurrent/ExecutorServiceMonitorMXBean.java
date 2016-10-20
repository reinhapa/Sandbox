/**
 * File Name: ExecutorServiceMonitorMXBean.java
 * 
 * Copyright (c) 2016 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.concurrent;

public interface ExecutorServiceMonitorMXBean {
	public double getRequestPerSecondRetirementRate();

	public double getAverageServiceTime();

	public double getAverageTimeWaitingInPool();

	public double getAverageResponseTime();

	public double getEstimatedAverageNumberOfActiveRequests();

	public double getRatioOfDeadTimeToResponseTime();

	public double v();
}
