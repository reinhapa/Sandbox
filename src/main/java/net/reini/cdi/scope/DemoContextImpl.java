/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 Patrick Reinhart
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

package net.reini.cdi.scope;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ContextNotActiveException;
import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.context.spi.CreationalContext;

public class DemoContextImpl implements DemoContext {
  private static final Logger LOGGER = LoggerFactory.getLogger(DemoContextImpl.class);

  // It's a normal scope so there may be no more than one mapped instance per contextual type per
  // thread
  private final ThreadLocal<Map<Contextual<?>, ContextualInstance<?>>> currentContext;

  public DemoContextImpl() {
    currentContext = new ThreadLocal<>();
  }

  @Override
  public Class<? extends Annotation> getScope() {
    return DemoScoped.class;
  }

  @Override
  public boolean isActive() {
    return currentContext.get() != null;
  }

  @Override
  public <T> T get(Contextual<T> contextual) {
    return get(contextual, null);
  }

  @Override
  public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
    Map<Contextual<?>, ContextualInstance<?>> ctx = currentContext.get();
    if (ctx == null) {
      // Thread local not set - context is not active!
      throw new ContextNotActiveException();
    }
    @SuppressWarnings("unchecked")
    ContextualInstance<T> beanInstance = (ContextualInstance<T>) ctx.get(contextual);
    if (beanInstance != null) {
      final T instance = beanInstance.instance();
      LOGGER.info("Returned cached bean instance: {}", instance);
      return instance;
    } else if (creationalContext != null) {
      T instance = contextual.create(creationalContext);
      if (instance != null) {
        ctx.put(contextual, new ContextualInstance<>(instance, creationalContext, contextual));
      }
      LOGGER.info("Returned newly created cached bean instance: {}", instance);
      return instance;
    } else {
      return null;
    }
  }

  @Override
  public void destroy(Contextual<?> contextual) {
    Map<Contextual<?>, ContextualInstance<?>> ctx = currentContext.get();
    if (ctx != null) {
      ContextualInstance<?> contextualInstance = ctx.remove(contextual);
      if (contextualInstance != null) {
        LOGGER.info("Destroing cached bean instance: {}", contextualInstance.instance());
        contextualInstance.destroy();
      }
    }
  }

  @Override
  public void activate() {
    currentContext.set(new HashMap<>());
  }

  @Override
  public void deactivate() {
    Map<Contextual<?>, ContextualInstance<?>> ctx = currentContext.get();
    if (ctx != null) {
      for (ContextualInstance<?> contextualInstance : ctx.values()) {
        try {
          contextualInstance.destroy();
        } catch (Exception e) {
          LOGGER.warn("Unable to destroy instance {} for bean: {}", contextualInstance.instance(),
              contextualInstance.contextual(), e);
        }
      }
      ctx.clear();
      currentContext.remove();
    }
  }
}
