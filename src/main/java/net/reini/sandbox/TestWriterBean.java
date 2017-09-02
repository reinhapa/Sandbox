/**
 * File Name: TestBean.java
 * 
 * Copyright (c) 2015 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.sandbox;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;
import javax.cache.Cache;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;

import org.slf4j.LoggerFactory;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

/**
 * TD2:patrick.reinhart Auto-generated comment for class
 *
 * @author patrick.reinhart
 */
@Startup
@Singleton
public class TestWriterBean {
  private static AtomicInteger counter = new AtomicInteger();

  @Inject
  private Cache<String,Integer> cache;

  @Resource
  TransactionSynchronizationRegistry reg;

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
  public void test() {
    cache.put("key", Integer.valueOf(counter.incrementAndGet()));
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    LoggerFactory.getLogger(getClass()).info("done");
  }

  static class Replication implements CacheEventListener, Synchronization {
    private final TransactionSynchronizationRegistry _reg;
    private Set<Element> values;

    Replication(TransactionSynchronizationRegistry reg) {
      _reg = reg;
    }

    @Override
    public void beforeCompletion() {}

    @Override
    public void afterCompletion(int status) {
      if (status == Status.STATUS_COMMITTED) {
        LoggerFactory.getLogger(getClass()).info("putting value {} to cache", values);
      }
    }

    @Override
    public void notifyRemoveAll(Ehcache cache) {}

    @Override
    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
      notify(element);
    }

    @Override
    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {}

    @Override
    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
      notify(element);
    }

    /**
     * @param element
     */
    protected void notify(Element element) {
      if (values == null) {
        values = new HashSet<>();
        _reg.registerInterposedSynchronization(this);
      }
      values.add(element);
    }

    @Override
    public void notifyElementExpired(Ehcache cache, Element element) {}

    @Override
    public void notifyElementEvicted(Ehcache cache, Element element) {}

    @Override
    public void dispose() {}

    @Override
    public Object clone() throws CloneNotSupportedException {
      return super.clone();
    }
  }
}
