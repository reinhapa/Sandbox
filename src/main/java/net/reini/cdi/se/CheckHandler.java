/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016, 2024 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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

  @Inject Logger logger;
  @Inject TestDataProducer testDataProducer;

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
