/**
 * File Name: TransactionInterceptor.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se.simple;

import java.lang.reflect.Method;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;

import net.reini.cdi.se.EjbTransactional;

/**
 * @author Patrick Reinhart
 */
@Interceptor
@EjbTransactional
public class TransactionInterceptor {
  @Inject
  Logger logger;

  @AroundInvoke
  public Object handleTransaction(InvocationContext context) throws Exception {
    logger.debug("before invoke {}", context.getMethod());
    try {
      return context.proceed();
    } finally {
      logger.debug("after invoke {}", context.getMethod());
    }
  }

  private TransactionAttributeType getTxType(Method method, InvocationContext context) {
    TransactionAttribute txAttribute = method.getAnnotation(TransactionAttribute.class);
    if (txAttribute == null) {
      txAttribute = context.getTarget().getClass().getAnnotation(TransactionAttribute.class);
      if (txAttribute == null) {
        return TransactionAttributeType.REQUIRED;
      }
    }
    return txAttribute.value();
  }
}
