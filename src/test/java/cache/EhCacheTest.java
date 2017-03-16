package cache;

import java.net.URL;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.junit.Test;

public class EhCacheTest {

  @Test
  public void testEhCache() throws Exception {
    URL myUrl = getClass().getResource("EhCacheTest.xml");
    XmlConfiguration xmlConfig = new XmlConfiguration(myUrl);
    try (CacheManager manager = CacheManagerBuilder.newCacheManager(xmlConfig)){
      manager.init();
      // init caches
      Cache<Long, String> cache = manager.getCache("testcache", Long.class, String.class);
      System.out.println(manager);
      Long key = Long.valueOf(10);
      System.out.println(cache.get(key));
      cache.put(key, "the10");
      System.out.println(cache.get(key));
      
    }
  }
}
