package net.reini.cdi.se;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

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
