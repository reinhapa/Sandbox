/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * File Name: TestBean.java
 * 
 * Copyright (c) 2015 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.sandbox;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;
import javax.cache.event.EventType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
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
    private static final long serialVersionUID = 1L;
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
