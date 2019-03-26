/**
 * File Name: ResourceExtension.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se.simple;

import java.lang.annotation.Annotation;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.enterprise.inject.spi.configurator.AnnotatedTypeConfigurator;
import javax.jms.JMSConnectionFactory;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.reini.cdi.se.TestApplication;

public class ResourceExtension implements Extension {
  private static final Logger logger = LoggerFactory.getLogger(ResourceExtension.class);

  public <T> void processAnnotatedType(@Observes @WithAnnotations({Stateless.class, Stateful.class,
      Singleton.class, MessageDriven.class, EJB.class, Resource.class, PersistenceContext.class,
      JMSConnectionFactory.class,}) ProcessAnnotatedType<T> pat) {

    AnnotatedTypeConfigurator<T> configureAnnotatedType = pat.configureAnnotatedType();
    configureAnnotatedType.filterFields(f -> f.isAnnotationPresent(Resource.class))
        .forEach(configurator -> {
          configurator.add(InjectionAnnotation.INSTANCE).add(ResourceQualifierAnnotation
              .of(configurator.getAnnotated().getAnnotation(Resource.class)));
        });
    configureAnnotatedType.filterMethods(m -> m.isAnnotationPresent(Resource.class))
        .forEach(configurator -> {
          configurator.add(InjectionAnnotation.INSTANCE).add(ResourceQualifierAnnotation
              .of(configurator.getAnnotated().getAnnotation(Resource.class)));
        });

    AnnotatedType<T> annotatedType = pat.getAnnotatedType();
    System.err.println("processAnnotatedType " + annotatedType.getFields());
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
      System.err.println("processManagedBean " + sb);
      // logger.trace(sb.toString());
    }
  }

  public void observe(@Observes ProcessBean<?> bean) {
    if (TestApplication.class.equals(bean.getBean().getBeanClass())) {
      System.err.println(bean.getBean().getInjectionPoints());
    }
  }

}
