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

package cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.management.ManagementFactory;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;
import javax.cache.management.CacheStatisticsMXBean;
import javax.cache.spi.CachingProvider;
import javax.management.JMX;
import javax.management.ObjectName;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.junit.Test;

public class CacheTest {
  @Test
  public void testCache() throws Exception {
    URI uri = getClass().getResource("CacheTest.xml").toURI();
    try (CachingProvider provider = Caching.getCachingProvider();
        CacheManager manager = provider.getCacheManager(uri, getClass().getClassLoader());
        Cache<Long, String> cache = manager.getCache("testcache", Long.class, String.class)) {

      System.out.println(provider.getDefaultURI());

      Cache<Object, Object> testcache = manager.getCache("testcache");
      @SuppressWarnings("unchecked")
      var configuration = testcache.getConfiguration(Configuration.class);
      System.out.println(configuration.getKeyType());
      System.out.println(configuration.getValueType());

      CacheConfiguration<String, String> cacheConfiguration =
          CacheConfigurationBuilder.newCacheConfigurationBuilder(
                  String.class, String.class, ResourcePoolsBuilder.heap(20))
              .withExpiry(
                  ExpiryPolicyBuilder.timeToIdleExpiration(Duration.of(5, ChronoUnit.SECONDS)))
              .build();

      manager.createCache(
          "myCache", Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration));

      cache.registerCacheEntryListener(
          new MutableCacheEntryListenerConfiguration<>(
              new MyListenerFactory<>(), null, false, true));

      assertThat(manager).isNotNull();
      System.out.println(manager);

      for (int count = 0; count < 20; count++) {
        final Long key = Long.valueOf(count);
        cache.put(key, "xxx" + count);
        cache.get(key);
      }

      cache.remove(Long.valueOf(13));

      for (int count = 0; count < 20; count++) {
        final Long key = Long.valueOf(count);
        System.out.println(key + " -> " + cache.get(key));
      }

      // Long key = Long.valueOf(10);
      // for (int count = 0; count < 10; count++) {
      // TimeUnit.SECONDS.sleep(1);
      // String value = cache.get(key);
      // if (value == null) {
      // value = cache.get(key);
      // }
      // System.out.println(count + " -> " + value);
      // }

      // cache.removeAll();

      var table = new Hashtable<String, String>();
      table.put("type", "CacheStatistics");
      table.put("Cache", "testcache");
      table.put("CacheManager", cache.getCacheManager().getURI().toString().replace(':', '.'));
      var objectName = ObjectName.getInstance("javax.cache", table);
      var mbeanServer = ManagementFactory.getPlatformMBeanServer();
      if (mbeanServer.isRegistered(objectName)) {
        var statisticsMXBean =
            JMX.newMXBeanProxy(mbeanServer, objectName, CacheStatisticsMXBean.class);
        System.out.println(statisticsMXBean.getCacheGets());
      }
    }
  }

  interface SelectiveCacheEventListener<K, V>
      extends CacheEntryCreatedListener<K, V>,
          CacheEntryUpdatedListener<K, V>,
          CacheEntryExpiredListener<K, V>,
          CacheEntryRemovedListener<K, V> {}

  static class MyListenerFactory<K, V> implements Factory<MyListener<K, V>> {
    private static final long serialVersionUID = 1L;

    @Override
    public int hashCode() {
      return MyListenerFactory.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof MyListenerFactory;
    }

    @Override
    public MyListener<K, V> create() {
      return new MyListener<>();
    }
  }

  static class MyListener<K, V> implements SelectiveCacheEventListener<K, V> {
    private static final ExecutorService VIRTUAL_THREAD_EXECUTOR =
        Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void onCreated(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
        throws CacheEntryListenerException {
      events.forEach(
          event -> {
            System.out.println(
                "onCreated: "
                    + event.getKey()
                    + " "
                    + event.getValue()
                    + " "
                    + event.getOldValue());
          });
    }

    @Override
    public void onUpdated(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
        throws CacheEntryListenerException {
      events.forEach(
          event -> {
            System.out.println(
                "onUpdated: "
                    + event.getKey()
                    + " "
                    + event.getValue()
                    + " "
                    + event.getOldValue());
          });
    }

    @Override
    public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
        throws CacheEntryListenerException {
      events.forEach(
          event -> {
            System.out.println(
                "onExpired: "
                    + event.getKey()
                    + " "
                    + event.getValue()
                    + " "
                    + event.getOldValue());
            @SuppressWarnings("unchecked")
            Cache<Object, Object> sourceCache = event.getSource();
            VIRTUAL_THREAD_EXECUTOR.submit(() -> sourceCache.put(event.getKey(), event.getValue()));
          });
    }

    @Override
    public void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
        throws CacheEntryListenerException {
      events.forEach(
          event -> {
            System.out.println(
                "onRemoved: "
                    + event.getKey()
                    + " "
                    + event.getValue()
                    + " "
                    + event.getOldValue());
          });
    }
  }
}
