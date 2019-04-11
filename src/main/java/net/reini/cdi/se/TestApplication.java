/**
 * File Name: TestBean.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import org.slf4j.Logger;

// @Stateless
// @Singleton
@ApplicationScoped // geht
// @Singleton // geht nicht
// @Dependent // geht
// @ManagedBean // geht nicht
public class TestApplication {
  @Inject
  Logger logger;

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
    return "Hello, world datasource: " + dataSource + ", transactionManager: " + transactionManager;
  }
}
