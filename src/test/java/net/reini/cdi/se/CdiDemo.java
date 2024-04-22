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

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.reini.cdi.se.simple.ResourceExtension;
import net.reini.cdi.se.simple.StartupBean;

public class CdiDemo {

  @Test
  @Disabled
  void testInjection() {
    SeContainerInitializer initializer = SeContainerInitializer.newInstance();
    List<StartupBean> startupBeans = new ArrayList<>();
    initializer.addExtensions(new ResourceExtension(startupBeans::add));
    // initializer.addExtensions(new EjbExtension());
    try (SeContainer container = initializer.initialize()) {
      // provoke initialization for startup beans
      startupBeans.forEach(startupBean -> startupBean.start(container));
      // invoke method on main application
      System.err.println(container.select(TestApplication.class).get().helloWorld());
    }
  }
}
