package cache;

import java.net.URI;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;
import javax.cache.spi.CachingProvider;

import org.junit.Test;

public class CacheTest {
  @Test
  public void testCache() throws Exception {
    URI uri = getClass().getResource("CacheTest.xml").toURI();
    try (CachingProvider provider = Caching.getCachingProvider();
        CacheManager manager = provider.getCacheManager(uri, getClass().getClassLoader());
        Cache<Long, String> cache = manager.getCache("testcache", Long.class, String.class)) {

      cache.registerCacheEntryListener(new MutableCacheEntryListenerConfiguration<>(
          new MyListenerFactory<>(), null, false, true));


      System.out.println(manager);
      Long key = Long.valueOf(10);
      System.out.println(cache.get(key));
      cache.put(key, "the10");
      System.out.println(cache.get(key));
      cache.removeAll();
    }
  }

  interface SelectiveCacheEventListener<K, V> extends CacheEntryUpdatedListener<K, V>,
      CacheEntryExpiredListener<K, V>, CacheEntryRemovedListener<K, V> {
  }

  static class MyListenerFactory<K, V> implements Factory<MyListener<K, V>> {
    private static final long serialVersionUID = 1L;

    @Override
    public MyListener<K, V> create() {
      return new MyListener<>();
    }
  }

  static class MyListener<K, V> implements SelectiveCacheEventListener<K, V> {
    @Override
    public void onUpdated(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
        throws CacheEntryListenerException {
      events.forEach(event -> {
        System.out.println("onUpdated: " + event.getKey() + " " + event.getValue());
      });
    }

    @Override
    public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
        throws CacheEntryListenerException {
      events.forEach(event -> {
        System.out.println("onExpired: " + event.getKey() + " " + event.getValue());
      });
    }

    @Override
    public void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
        throws CacheEntryListenerException {
      events.forEach(event -> {
        System.out.println("onRemoved: " + event.getKey() + " " + event.getValue());
      });
    }
  }
}
