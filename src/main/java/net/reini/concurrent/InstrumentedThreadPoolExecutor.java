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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InstrumentedThreadPoolExecutor extends ThreadPoolExecutor {
  // Keep track of all of the request times
  private final ConcurrentHashMap<Runnable, Long> timeOfRequest = new ConcurrentHashMap<>();
  private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();
  private long lastArrivalTime;

  private final AtomicInteger numberOfRequestsRetired = new AtomicInteger();
  private final AtomicInteger numberOfRequests = new AtomicInteger();
  private final AtomicLong totalServiceTime = new AtomicLong();
  private final AtomicLong totalPoolTime = new AtomicLong();
  private final AtomicLong aggregateInterRequestArrivalTime = new AtomicLong();

  /**
   * @param corePoolSize the number of threads to keep in the pool, even if they are idle, unless
   *     {@code allowCoreThreadTimeOut} is set
   * @param maximumPoolSize the maximum number of threads to allow in the pool
   * @param keepAliveTime when the number of threads is greater than the core, this is the maximum
   *     time that excess idle threads will wait for new tasks before terminating.
   * @param unit the time unit for the {@code keepAliveTime} argument
   * @param workQueue the queue to use for holding tasks before they are executed. This queue will
   *     hold only the {@code Runnable} tasks submitted by the {@code execute} method.
   * @param threadFactory the factory to use when the executor creates a new thread
   * @param handler the handler to use when execution is blocked because the thread bounds and queue
   *     capacities are reached
   * @throws IllegalArgumentException if one of the following holds:<br>
   *     {@code corePoolSize < 0}<br>
   *     {@code keepAliveTime < 0}<br>
   *     {@code maximumPoolSize <= 0}<br>
   *     {@code maximumPoolSize < corePoolSize}
   * @throws NullPointerException if {@code workQueue} or {@code threadFactory} or {@code handler}
   *     is null
   */
  public InstrumentedThreadPoolExecutor(
      int corePoolSize,
      int maximumPoolSize,
      long keepAliveTime,
      TimeUnit unit,
      BlockingQueue<Runnable> workQueue,
      ThreadFactory threadFactory,
      RejectedExecutionHandler handler) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
  }

  @Override
  protected void beforeExecute(Thread worker, Runnable task) {
    super.beforeExecute(worker, task);
    startTime.set(Long.valueOf(System.nanoTime()));
  }

  @Override
  protected void afterExecute(Runnable task, Throwable t) {
    try {
      long start = startTime.get().longValue();
      totalServiceTime.addAndGet(System.nanoTime() - start);
      totalPoolTime.addAndGet(start - timeOfRequest.remove(task).longValue());
      numberOfRequestsRetired.incrementAndGet();
    } finally {
      super.afterExecute(task, t);
    }
  }

  @Override
  public void execute(Runnable task) {
    long now = System.nanoTime();
    numberOfRequests.incrementAndGet();
    synchronized (this) {
      if (lastArrivalTime != 0L) {
        aggregateInterRequestArrivalTime.addAndGet(now - lastArrivalTime);
      }
      lastArrivalTime = now;
      timeOfRequest.put(task, Long.valueOf(now));
    }
    super.execute(task);
  }

  /**
   * @return the aggregateInterRequestArrivalTime
   */
  public long getAggregateInterRequestArrivalTime() {
    return aggregateInterRequestArrivalTime.get();
  }

  /**
   * @return the totalPoolTime
   */
  public long getTotalPoolTime() {
    return totalPoolTime.get();
  }

  /**
   * @return the totalServiceTime
   */
  public long getTotalServiceTime() {
    return totalServiceTime.get();
  }

  /**
   * @return the numberOfRequests
   */
  public int getNumberOfRequests() {
    return numberOfRequests.get();
  }

  /**
   * @return the numberOfRequestsRetired
   */
  public int getNumberOfRequestsRetired() {
    return numberOfRequestsRetired.get();
  }
}
