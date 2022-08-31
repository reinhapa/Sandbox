package net.reini.cdi.se;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.slf4j.Logger;

@Check(user = "a")
@Dependent
public class SimpleBean {
  private final Logger logger;
  private final TestData data;

  @Inject
  public SimpleBean(Logger logger, TestData data) {
    this.logger = logger;
    this.data = data;
  }

  public void doSomething() {
    logger.info("doSomething");
  }

  @PreDestroy
  public void preDestroy() {
    logger.info("preDestroy() {}", data);
  }
}
