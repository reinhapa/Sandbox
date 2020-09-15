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

package net.reini.cdi.se;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.InterceptionFactory;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

import org.jboss.weld.bootstrap.api.CDI11Bootstrap;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeldDemo {
  private static Logger logger = LoggerFactory.getLogger(WeldDemo.class);

  public static void main(String[] args) throws Exception {
    Weld weld = new Weld() {
      @Override
      protected Deployment createDeployment(ResourceLoader resourceLoader,
          CDI11Bootstrap bootstrap) {
        return super.createDeployment(resourceLoader, bootstrap);
      }
    };
    weld.addExtension(new WeldExtension());
    try (SeContainer container = weld.initialize()) {
      System.out.println(container.select(TestApplication.class).get().helloWorld());

      int threads = 5;
      ExecutorService service = Executors.newFixedThreadPool(threads);
      CountDownLatch countDown = new CountDownLatch(1);
      Callable<Set<String>> test = () -> {
        Set<String> ids = new HashSet<>();
        Instance<Numerator> instance = CDI.current().select(Numerator.class);
        countDown.await();
        for (int i = 0; i < 10; i++) {
          Numerator numerator = instance.get();
          String id = numerator.nextValue();
          reportId(ids, id);
          instance.destroy(numerator);
        }
        return ids;
      };
      List<Future<Set<String>>> tasks=new ArrayList<>();
      for (int i = 0; i < threads; i++) {
        tasks.add( service.submit(test));
      }
      service.shutdown();
      countDown.countDown();
      service.awaitTermination(5, TimeUnit.MINUTES);

      Set<String> ids = new HashSet<>();
      for (Future<Set<String>> f : tasks) {
        f.get().forEach(id -> reportId(ids, id));
      }
    }
  }

  private static void reportId(Set<String> ids, String id) {
    if (ids.add(id)) {
      System.out.println(id);
    } else {
      System.err.println(id);
    }
  }


  static class WeldExtension implements Extension {
    private final List<PooledInjectionTarget<?>> pooledTargets;

    WeldExtension() {
      pooledTargets = new ArrayList<>();
    }

    <X> void processInjectionTarget(@Observes ProcessInjectionTarget<X> pit, BeanManager beanManager) {
      AnnotatedType<X> at = pit.getAnnotatedType();
      if (Numerator.class.isAssignableFrom(at.getJavaClass())) {
        logger.info("processInjectionTarget({}, {})", at, beanManager);
        PooledInjectionTarget<X> injectionTarget =
            new PooledInjectionTarget<>(pit.getInjectionTarget(), ctx -> beanManager.createInterceptionFactory(ctx, at.getJavaClass()));
        pooledTargets.add(injectionTarget);
        pit.setInjectionTarget(injectionTarget);
      }
    }

    void beforeShutdown(@Observes BeforeShutdown event) {
      pooledTargets.forEach(PooledInjectionTarget::disposePool);
    }
  }

  static class PooledInjectionTarget<X> implements InjectionTarget<X> {
    private InjectionTarget<X> delegate;
    private Function<CreationalContext<X>, InterceptionFactory<X>> interceptorFunction;
    private Deque<X> available;
    private Deque<X> inUse;

    PooledInjectionTarget(InjectionTarget<X> delegate, Function<CreationalContext<X>, InterceptionFactory<X>> interceptorFunction) {
      this.delegate = delegate;
      this.interceptorFunction = interceptorFunction;
      this.available = new ArrayDeque<>();
      this.inUse = new ArrayDeque<>();
    }

    void disposePool() {
      available.removeIf(this::cleanupInstance);
    }

    private boolean cleanupInstance(X instance) {
      logger.debug("Disposing {} instance", instance);
      delegate.preDestroy(instance);
      delegate.dispose(instance);
      return true;
    }

    @Override
    public X produce(CreationalContext<X> ctx) {
      X instance;
      synchronized (available) {
        instance = available.poll();
      }
      if (instance == null) {
        InterceptionFactory<X> interceptionFactory = interceptorFunction.apply(ctx);
        instance = interceptionFactory.createInterceptedInstance(delegate.produce(ctx));
        delegate.inject(instance, ctx);
        delegate.postConstruct(instance);
        logger.debug("Created instance {}", instance);
      } else {
        logger.debug("Re-using instance {}", instance);
      }
      synchronized (inUse) {
        inUse.add(instance);
      }
      return instance;
    }

    @Override
    public void dispose(X instance) {
      synchronized (inUse) {
        inUse.remove(instance);
      }
      synchronized (available) {
        available.add(instance);
      }
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
      return delegate.getInjectionPoints();
    }

    @Override
    public void inject(X instance, CreationalContext<X> ctx) {
      // done after construction
    }

    @Override
    public void postConstruct(X instance) {
      // done after construction
    }

    @Override
    public void preDestroy(X instance) {
      // done in clean up
    }
  }
}
