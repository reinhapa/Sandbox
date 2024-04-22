/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021, 2024 Patrick Reinhart
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

import static javax.net.ssl.SSLEngineResult.Status.BUFFER_OVERFLOW;

import java.util.Formatter;

import javax.crypto.SecretKey;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

import org.slf4j.Logger;

@ApplicationScoped
public class TestDataProducer {
  @Inject Logger logger;

  @Produces
  TestData testData(InjectionPoint ip) {
    Class<?> beanClass = ip.getBean().getBeanClass();
    logger.info("createTestData for {} {}", beanClass);
    return new TestData(null);
  }

  SecretKey test() {
    if (BUFFER_OVERFLOW != null) {
      logger.debug("gugus {}", Formatter.class);
    }
    return null;
  }
}
