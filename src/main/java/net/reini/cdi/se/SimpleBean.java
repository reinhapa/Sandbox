package net.reini.cdi.se;

import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Check(user = "a")
@Dependent
public class SimpleBean {
  private final Logger logger;
  private final TestData data;

  @Timed
  @Inject
  public SimpleBean(Logger logger, TestData data) {
    this.logger = logger;
    this.data = data;
  }

  @Timed
  public void doSomething() {
    logger.info("doSomething");
  }

  @PreDestroy
  public void preDestroy() {
    logger.info("preDestroy() {}", data);
  }
}
