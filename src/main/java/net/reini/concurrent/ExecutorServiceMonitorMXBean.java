/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016, 2025 Patrick Reinhart
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

/** Defines all properties and methods to access using JMX. */
public interface ExecutorServiceMonitorMXBean {
  /**
   * Returns the request rate per second retirement rate.
   *
   * @return the value
   */
  public double getRequestPerSecondRetirementRate();

  /**
   * Returns the average service time.
   *
   * @return the value
   */
  public double getAverageServiceTime();

  /**
   * Returns the average waiting time in the pool.
   *
   * @return the value
   */
  public double getAverageTimeWaitingInPool();

  /**
   * Returns the average response time.
   *
   * @return the value
   */
  public double getAverageResponseTime();

  /**
   * Returns the estimated average number of active requests.
   *
   * @return the value
   */
  public double getEstimatedAverageNumberOfActiveRequests();

  /**
   * Returns the dead time/responsive time ratio.
   *
   * @return the value
   */
  public double getRatioOfDeadTimeToResponseTime();

  /**
   * Returns the value.
   *
   * @return the value
   */
  public double v();
}
