/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016, 2024 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
    return getRequestPerSecondRetirementRate()
        * (getAverageServiceTime() + getAverageTimeWaitingInPool());
  }

  @Override
  public double getRatioOfDeadTimeToResponseTime() {
    double poolTime = this.threadPool.getTotalPoolTime();
    return poolTime / (poolTime + threadPool.getTotalServiceTime());
  }

  @Override
  public double v() {
    return getEstimatedAverageNumberOfActiveRequests() / Runtime.getRuntime().availableProcessors();
  }
}
