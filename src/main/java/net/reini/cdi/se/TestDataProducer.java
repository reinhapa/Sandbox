package net.reini.cdi.se;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

@ApplicationScoped
public class TestDataProducer {
  @Inject
  Logger logger;

  @Produces
  TestData testData(InjectionPoint ip) {
    Class<?> beanClass = ip.getBean().getBeanClass();
    logger.info("createTestData for {} {}", beanClass);
    return new TestData(null);
  }

}
