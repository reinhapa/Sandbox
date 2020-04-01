/**
 * File Name: WeldDemo.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

import org.jboss.weld.bootstrap.api.CDI11Bootstrap;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeldDemo {
  private static Logger logger = LoggerFactory.getLogger(WeldDemo.class);

  public static void main(String[] args) {
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


      Instance<Numerator> instance = CDI.current().select(Numerator.class);
      for (int i = 0; i < 10; i++) {
        Numerator numerator = instance.get();
        System.out.println(numerator.nextValue());
        instance.destroy(numerator);
      }
    }
  }

  static class WeldExtension implements Extension {
    private final List<PooledInjectionTarget<?>> pooledTargets;

    WeldExtension() {
      pooledTargets = new ArrayList<>();
    }

    <X> void processInjectionTarget(@Observes ProcessInjectionTarget<X> pit) {
      AnnotatedType<X> at = pit.getAnnotatedType();
      logger.info("processInjectionTarget({})", at);
      if (Numerator.class.isAssignableFrom(at.getJavaClass())) {
        PooledInjectionTarget<X> injectionTarget =
            new PooledInjectionTarget<>(pit.getInjectionTarget());
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
    private Deque<X> available;

    PooledInjectionTarget(InjectionTarget<X> delegate) {
      this.delegate = delegate;
      this.available = new ArrayDeque<>();
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
      X instance = available.poll();
      if (instance == null) {
        instance = delegate.produce(ctx);
        delegate.inject(instance, ctx);
        delegate.postConstruct(instance);
        logger.debug("Created instance {}", instance);
      } else {
        logger.debug("Re-using instance {}", instance);
      }
      return instance;
    }

    @Override
    public void dispose(X instance) {
      if (!available.add(instance)) {
        cleanupInstance(instance);
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
