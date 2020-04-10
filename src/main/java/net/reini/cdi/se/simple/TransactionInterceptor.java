/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
