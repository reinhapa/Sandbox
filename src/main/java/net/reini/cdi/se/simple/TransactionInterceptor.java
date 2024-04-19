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

package net.reini.cdi.se.simple;

import java.lang.reflect.Method;

import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.reini.cdi.se.EjbTransactional;

/**
 * @author Patrick Reinhart
 */
@Interceptor
@EjbTransactional
public class TransactionInterceptor {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionInterceptor.class);

  @AroundInvoke
  public Object handleTransaction(InvocationContext context) throws Exception {
    LOGGER.debug("before invoke {}", context.getMethod());
    try {
      return context.proceed();
    } finally {
      LOGGER.debug("after invoke {}", context.getMethod());
    }
  }

  TransactionAttributeType getTxType(Method method, InvocationContext context) {
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
