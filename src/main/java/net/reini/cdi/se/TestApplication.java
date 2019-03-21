/**
 * File Name: TestBean.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se;

import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.slf4j.Logger;

// @Stateless
@Dependent
public class TestApplication {
  @Resource(lookup = "java:global/env/jdbc/CustomerDatasource")
  DataSource dataSource;

  @Inject
  Logger logger;

  public String helloWorld() {
    logger.info("Called helloWorld() - dataSource: {}", dataSource);
    return "Hello, world " + dataSource;
  }
}
