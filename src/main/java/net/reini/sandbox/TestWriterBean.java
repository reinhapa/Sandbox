/**
 * File Name: TestBean.java
 * 
 * Copyright (c) 2015 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.sandbox;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.slf4j.LoggerFactory;

/**
 * TD2:patrick.reinhart Auto-generated comment for class
 *
 * @author patrick.reinhart
 */
@Startup
@Singleton
public class TestWriterBean {
  private static AtomicInteger counter = new AtomicInteger();

  private Ehcache cache;

  @Resource
  TransactionSynchronizationRegistry reg;

  public TestWriterBean() {
    cache = CacheManager.getInstance().getEhcache("TxTest");
  }

  @PostConstruct
  public void init() {
    cache.getCacheEventNotificationService().registerListener(new Replication(reg));
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
  public void test() {
    Element element = new Element("key", Integer.toString(counter.incrementAndGet()));
    LoggerFactory.getLogger(getClass()).info("adding value {} to cache", element.getObjectValue());
    cache.put(element);
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
    public void beforeCompletion() {
    }

    @Override
    public void afterCompletion(int status) {
      if (status == Status.STATUS_COMMITTED) {
        LoggerFactory.getLogger(getClass()).info("putting value {} to cache", values);
      }
    }

    @Override
    public void notifyRemoveAll(Ehcache cache) {
    }

    @Override
    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
      notify(element);
    }

    @Override
    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
    }

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
    public void notifyElementExpired(Ehcache cache, Element element) {
    }

    @Override
    public void notifyElementEvicted(Ehcache cache, Element element) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
      return super.clone();
    }
  }
}