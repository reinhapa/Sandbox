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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;

public class ScopeDemo {
  private static final Logger LOGGER = LoggerFactory.getLogger(ScopeDemo.class);

  public static void main(String[] args) {
    SeContainerInitializer initialzer = SeContainerInitializer.newInstance();
    initialzer.addExtensions(new ScopeExtension());
    try (SeContainer container = initialzer.initialize()) {
      DemoContext context = container.select(DemoContext.class).get();
      context.activate();

      final Instance<HelloApplication> select = container.select(HelloApplication.class);

      handle(select.get());

      handle(select.get());

      context.deactivate();
    }
  }


  private static void handle(HelloApplication app) {
    app.helloWorld(helloWorld -> LOGGER.info("{} -> helloWorld() -> {}", app, helloWorld));
  }

  static class ScopeExtension implements Extension {
    void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager beanManager) {
      final DemoContext context = new DemoContextImpl();

      event.addContext(context);

      event.addBean().addType(DemoContext.class)
          .createWith(ctx -> new InjectableContext(context, beanManager))
          .addQualifier(Default.Literal.INSTANCE).scope(Dependent.class)
          .beanClass(ScopeExtension.class);
    }
  }
}
