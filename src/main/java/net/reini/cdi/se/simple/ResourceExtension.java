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

import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.ProcessBean;
import jakarta.enterprise.inject.spi.ProcessManagedBean;
import jakarta.enterprise.inject.spi.ProcessSessionBean;
import jakarta.enterprise.inject.spi.WithAnnotations;
import jakarta.enterprise.inject.spi.configurator.AnnotatedTypeConfigurator;
import jakarta.inject.Qualifier;
import jakarta.jms.JMSConnectionFactory;
import jakarta.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.reini.cdi.se.TestApplication;

public class ResourceExtension implements Extension {
  private static final Logger logger = LoggerFactory.getLogger(ResourceExtension.class);

  private final Consumer<StartupBean> startupBeans;

  public ResourceExtension(Consumer<StartupBean> startupBeans) {
    this.startupBeans = startupBeans;
  }

  public <T> void processAnnotatedType(
      @Observes
          @WithAnnotations({
            Stateless.class,
            Stateful.class,
            Singleton.class,
            MessageDriven.class,
            EJB.class,
            Resource.class,
            PersistenceContext.class,
            JMSConnectionFactory.class
          })
          ProcessAnnotatedType<T> pat) {

    AnnotatedTypeConfigurator<T> configureAnnotatedType = pat.configureAnnotatedType();
    configureAnnotatedType
        .filterFields(f -> f.isAnnotationPresent(Resource.class))
        .forEach(
            configurator -> {
              configurator
                  .add(InjectionAnnotation.INSTANCE)
                  .add(
                      ResourceQualifierAnnotation.of(
                          configurator.getAnnotated().getAnnotation(Resource.class)));
            });
    configureAnnotatedType
        .filterMethods(m -> m.isAnnotationPresent(Resource.class))
        .forEach(
            configurator -> {
              configurator
                  .add(InjectionAnnotation.INSTANCE)
                  .add(
                      ResourceQualifierAnnotation.of(
                          configurator.getAnnotated().getAnnotation(Resource.class)));
            });

    AnnotatedType<T> annotated = configureAnnotatedType.getAnnotated();
    if (annotated.isAnnotationPresent(Singleton.class)) {
      configureAnnotatedType.add(ApplicationScoped.Literal.INSTANCE);
    }

    if (annotated.isAnnotationPresent(Startup.class)) {
      startupBeans.accept(
          StartupBean.create(
              pat.getAnnotatedType().getJavaClass(),
              annotated.getAnnotations().stream()
                  .filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class))
                  .collect(Collectors.toList())));
    }
  }

  public <T> void processEjb(
      @Observes
          @WithAnnotations({
            Stateless.class,
            Stateful.class,
            Singleton.class,
            MessageDriven.class,
          })
          ProcessAnnotatedType<T> pat) {
    pat.configureAnnotatedType().add(EjbTransactionalAnnotation.INSTANCE);
  }

  public void processSessionBean(@Observes ProcessSessionBean<?> event) {
    System.err.println(event.getEjbName());
  }

  public void processManagedBean(@Observes ProcessManagedBean<?> event) {
    Bean<?> bean = event.getBean();
    for (InjectionPoint injectionPoint : bean.getInjectionPoints()) {

      StringBuilder sb = new StringBuilder();
      sb.append("  Found injection point ");
      sb.append(injectionPoint.getType());
      if (injectionPoint.getMember() != null && injectionPoint.getMember().getName() != null) {
        sb.append(": ");
        sb.append(injectionPoint.getMember().getName());
      }
      for (Annotation annotation : injectionPoint.getQualifiers()) {
        sb.append(" ");
        sb.append(annotation);
      }
      logger.trace(sb.toString());
    }
  }

  public void observe(@Observes ProcessBean<?> bean) {
    if (TestApplication.class.equals(bean.getBean().getBeanClass())) {
      System.err.println(bean.getBean().getInjectionPoints());
    }
  }
}
