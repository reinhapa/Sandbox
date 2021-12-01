package net.reini.cdi.se;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundConstruct;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

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
