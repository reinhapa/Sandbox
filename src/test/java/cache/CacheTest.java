package cache;

import java.net.URI;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.junit.Test;

public class CacheTest {
  @Test
  public void testCache() throws Exception {
    URI uri = getClass().getResource("CacheTest.xml").toURI();
    try (CachingProvider provider = Caching.getCachingProvider();
        CacheManager manager = provider.getCacheManager(uri, getClass().getClassLoader());
        Cache<Long, String> cache = manager.getCache("testcache", Long.class, String.class)) {
      System.out.println(manager);
      Long key = Long.valueOf(10);
      System.out.println(cache.get(key));
      cache.put(key, "the10");
      System.out.println(cache.get(key));
    }
  }
}
