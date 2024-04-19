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

package net.reini.cdi.scope;

import java.lang.annotation.Annotation;

import jakarta.enterprise.context.ContextNotActiveException;
import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.Vetoed;
import jakarta.enterprise.inject.spi.BeanManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Vetoed
class InjectableContext implements DemoContext {
  private static final Logger LOGGER = LoggerFactory.getLogger(InjectableContext.class);

  private final DemoContext delegate;
  private final BeanManager beanManager;

  private boolean isActivator;

  InjectableContext(DemoContext delegate, BeanManager beanManager) {
    this.delegate = delegate;
    this.beanManager = beanManager;
    this.isActivator = false;
  }

  @Override
  public void destroy(Contextual<?> contextual) {
    delegate.destroy(contextual);
  }

  @Override
  public Class<? extends Annotation> getScope() {
    return delegate.getScope();
  }

  @Override
  public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
    return delegate.get(contextual, creationalContext);
  }

  @Override
  public <T> T get(Contextual<T> contextual) {
    return delegate.get(contextual);
  }

  @Override
  public boolean isActive() {
    return delegate.isActive();
  }

  @Override
  public void activate() {
    try {
      beanManager.getContext(delegate.getScope());
      LOGGER.info("Command context already active");
    } catch (ContextNotActiveException e) { // NOSONAR ignored as there is no way to check for
      // existing scope
      // Only activate the context if not already active
      delegate.activate();
      isActivator = true;
    }
  }

  @Override
  public void deactivate() {
    if (isActivator) {
      delegate.deactivate();
    } else {
      LOGGER.info("Command context not activated by this bean");
    }
  }
}
