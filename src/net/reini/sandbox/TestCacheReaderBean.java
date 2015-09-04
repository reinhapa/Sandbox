/**
 * File Name: TestBean.java
 * 
 * Copyright (c) 2015 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.sandbox;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.slf4j.LoggerFactory;

/**
 * TD2:patrick.reinhart Auto-generated comment for class
 *
 * @author patrick.reinhart
 */
@Startup
@Singleton
public class TestCacheReaderBean {
  private Ehcache _cache;

  public TestCacheReaderBean() {
    _cache = CacheManager.getInstance().getEhcache("TxTest");
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  @Schedule(second = "*/1", minute = "*", hour = "*", persistent = false)
  public void test() {
    LoggerFactory.getLogger(getClass()).info("Value: {}", _cache.get("key").getObjectValue());
  }
}
