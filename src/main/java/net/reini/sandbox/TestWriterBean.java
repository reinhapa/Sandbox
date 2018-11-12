/**
 * File Name: TestBean.java
 * 
 * Copyright (c) 2015 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.sandbox;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.*;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;

import org.slf4j.LoggerFactory;


/**
 * My simple test bean
 *
 * @author patrick.reinhart
 */
@Startup
@Singleton
public class TestWriterBean {
  private static AtomicInteger counter = new AtomicInteger();

  private Cache<String,Integer> cache;

  @Inject
  Event<ValueEvent> eventSink;

  @Resource
  TransactionSynchronizationRegistry reg;

  @PostConstruct
  public void init() {
    CacheManager manager = Caching.getCachingProvider().getCacheManager();
    cache = manager.createCache("test", new MutableConfiguration<>());
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
  public void test() {
    int counterValue = counter.incrementAndGet();
    cache.registerCacheEntryListener(new TestCacheEntryListenerConfiguration<>());
    cache.put("key", Integer.valueOf(counterValue));
    eventSink.fire(ValueEvent.create(String.valueOf(counterValue)));
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    LoggerFactory.getLogger(getClass()).info("done");
  }

  static class TestCacheEntryListenerConfiguration<K, V>  implements CacheEntryListenerConfiguration<K, V> {
    private static final EnumSet<EventType> ACTIVE_EVENTS = EnumSet.of(EventType.UPDATED, EventType.REMOVED);

    @Override
    public Factory<CacheEntryEventFilter<? super K, ? super V>> getCacheEntryEventFilterFactory() {
      return () -> event -> {
        return ACTIVE_EVENTS.contains(event.getEventType());
      };
    }

    @Override
    public Factory<CacheEntryListener<? super K, ? super V>> getCacheEntryListenerFactory() {
      return null;
    }

    @Override
    public boolean isOldValueRequired() {
      return false;
    }

    @Override
    public boolean isSynchronous() {
      return false;
    }
  }

  static class EventListener<K, V> implements CacheEntryRemovedListener<K, V>, CacheEntryUpdatedListener<K, V> {
    @Override
    public void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
      cacheEntryEvents.forEach(System.out::println);
    }

    @Override
    public void onUpdated(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
      cacheEntryEvents.forEach(System.out::println);
    }
  }
}
