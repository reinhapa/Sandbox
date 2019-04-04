/**
 * File Name: SingletonBean.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.slf4j.Logger;

@Startup
@Singleton
@Default
public class SingletonBean {
  @Inject
  Logger logger;


  @PostConstruct
  public void postConstruct() {
    logger.info("postConstruct()");
  }

  @PreDestroy
  public void preDestroy() {
    logger.info("preDestroy()");
  }

}
