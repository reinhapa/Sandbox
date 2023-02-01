package net.reini.cdi.se;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class LoggerProducer {
  @Produces
  public Logger logger(InjectionPoint io) {
    return LoggerFactory.getLogger(io.getBean().getBeanClass());
  }
}
