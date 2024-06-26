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

package net.reini.cdi.se;

import javax.sql.DataSource;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.TransactionManager;

import org.slf4j.Logger;

// @Stateless
// @Singleton
@ApplicationScoped // geht
// @Singleton // geht nicht
// @Dependent // geht
// @ManagedBean // geht nicht
public class TestApplication {
  @Inject Logger logger;
  @Inject SimpleBean simpleBean;

  @Resource(lookup = "java:global/env/jdbc/CustomerDatasource")
  DataSource dataSource;

  TransactionManager transactionManager;

  @Resource
  public void setTransactionManager(TransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  @PostConstruct
  public void postConstruct() {
    logger.info("postConstruct()");
  }

  @PreDestroy
  public void preDestroy() {
    logger.info("preDestroy()");
  }

  @EjbTransactional
  public String helloWorld() {
    logger.info("Called helloWorld() - dataSource: {}", dataSource);
    simpleBean.doSomething();
    return "Hello, world datasource: " + dataSource + ", transactionManager: " + transactionManager;
  }
}
