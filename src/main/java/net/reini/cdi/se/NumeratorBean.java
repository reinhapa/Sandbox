package net.reini.cdi.se;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;

public class NumeratorBean implements Numerator {
  @Inject
  Logger logger;

  @PostConstruct
  public void postConstruct() {
    logger.debug("postConstruct()");
  }

  @PreDestroy
  public void preDestroy() {
    logger.debug("preDestroy()");
  }
  
  @Override
  public String nextValue() {
    logger.debug("nextValue()");
    return UUID.randomUUID().toString();
  }
}
