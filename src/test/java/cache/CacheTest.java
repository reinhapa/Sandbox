package cache;

import java.util.Arrays;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

import org.junit.Test;

public class CacheTest {
  @Test
  public void testDummy() {
    Configuration<String, List<String>> configuration = new MutableConfiguration<>();
    try (CachingProvider provider = Caching.getCachingProvider();
        CacheManager manager = provider.getCacheManager();
        Cache<String, List<String>> cache = manager.createCache("test", configuration)) {

      System.out.println(cache.get("key"));

      cache.put("key", Arrays.asList("a", "b", "c"));

      System.out.println(cache.get("key"));
    }
  }
}
