package net.reini.cdi.se;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundConstruct;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.slf4j.Logger;

@Check(user = "")
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 200)
public class CheckHandler {
  private final ConcurrentMap<Object, UUID> uuids;

  @Inject
  Logger logger;
  @Inject
  TestDataProducer testDataProducer;

  public CheckHandler() {
    uuids = new ConcurrentHashMap<>();
  }

  @AroundConstruct
  void aroundConstruct(InvocationContext ctx) throws Exception {
    UUID uuid = UUID.randomUUID();
    logger.info("constuct({}, {})", ctx.getConstructor().getAnnotatedReturnType(), uuid);
    ctx.proceed();
    Object target = ctx.getTarget();
    uuids.put(target, uuid);
    logger.info("constructed({}, {})", target, uuid);
  }

  @PreDestroy
  void preDestroy(InvocationContext ctx) {
    logger.info("preDestroy({}, {})", ctx.getTarget(), uuids.remove(ctx.getTarget()));
  }
}
