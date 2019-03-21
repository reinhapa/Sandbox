/**
 * File Name: ResourceExtension.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJBExtension implements Extension {
  private static final Logger logger = LoggerFactory.getLogger(EJBExtension.class);

  private final Consumer<Class<?>> entityClasses;
  private final Consumer<Class<?>> timerClasses;
  private final Consumer<Class<?>> startupSingletons;

  public EJBExtension() {
    entityClasses = System.out::println;
    timerClasses = System.out::println;
    startupSingletons = System.out::println;
  }

  public void observe(@Observes ProcessBean<?> bean) {
    if (TestApplication.class.equals(bean.getBean().getBeanClass())) {
      System.out.println(bean.getBean().getInjectionPoints());
    }
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

  public <T> void processAnnotatedType(@Observes @WithAnnotations({Stateless.class, Stateful.class,
      Singleton.class, MessageDriven.class, EJB.class, Resource.class, PersistenceContext.class,
      JMSConnectionFactory.class,}) ProcessAnnotatedType<T> pat) {
    System.err.println(pat);
    boolean modified = false;
    AnnotatedType<T> annotatedType = pat.getAnnotatedType();
    AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(annotatedType);

    boolean scopeIsPresent = annotatedType.isAnnotationPresent(ApplicationScoped.class)
        || annotatedType.isAnnotationPresent(Dependent.class)
        || annotatedType.isAnnotationPresent(RequestScoped.class)
        || annotatedType.isAnnotationPresent(SessionScoped.class);

    Entity entity = annotatedType.getAnnotation(Entity.class);
    if (entity != null) {
      entityClasses.accept(annotatedType.getJavaClass());
    }
    MappedSuperclass mappedSuperclass = annotatedType.getAnnotation(MappedSuperclass.class);
    if (mappedSuperclass != null) {
      entityClasses.accept(annotatedType.getJavaClass());
    }

    for (AnnotatedMethod<? super T> method : annotatedType.getMethods()) {
      EJB ejb = method.getAnnotation(EJB.class);
      if (ejb != null) {
        builder.removeFromMethod(method, EJB.class);
        modified = true;
        if (!beanNameOrName(ejb).isEmpty()) {
          builder.addToMethod(method, new EjbName.EjbNameLiteral(beanNameOrName(ejb)));
        } else {
          builder.addToMethod(method, DefaultLiteral.INSTANCE);
        }
      }
    }
    boolean makeApplicationScoped = false;
    for (AnnotatedField<? super T> field : annotatedType.getFields()) {
      boolean addInject = false;
      EJB ejb = field.getAnnotation(EJB.class);
      if (ejb != null) {
        modified = true;
        addInject = true;
        if (field.getJavaMember().getType().isAssignableFrom(annotatedType.getJavaClass())) {
          makeApplicationScoped = true;
          if (!scopeIsPresent || annotatedType.isAnnotationPresent(ApplicationScoped.class)) {
            logger.warn(
                "Self injection of EJB Type {} in field {} of Class {} simulated by ioc-unit-ejb only as ApplicationScoped",
                field.getJavaMember().getType().getName(), field.getJavaMember().getName(),
                field.getJavaMember().getDeclaringClass().getName());
          } else {
            logger.error(
                "Self injection of EJB Type {} in field {} of Class {} cannot be simulated by ioc-unit-ejb with the current scope",
                field.getJavaMember().getType().getName(), field.getJavaMember().getName(),
                field.getJavaMember().getDeclaringClass().getName());
          }
        }

        builder.removeFromField(field, EJB.class);
        if (!beanNameOrName(ejb).isEmpty()) {
          builder.addToField(field, new EjbName.EjbNameLiteral(beanNameOrName(ejb)));
        } else {
          builder.addToField(field, DefaultLiteral.INSTANCE);
        }
      }
      Resource resource = field.getAnnotation(Resource.class);
      if (resource != null) { // all Resources will be set injected. The Tester must provide
                              // anything for them.
        // this means that MessageDrivenContexts, SessionContext and JMS-Resources will be expected
        // to be injected.
        addInject = true;
      }
      if (field.getAnnotation(PersistenceContext.class) != null) {
        addInject = true;
        builder.removeFromField(field, PersistenceContext.class);
      }
      if (field.getAnnotation(JMSConnectionFactory.class) != null) {
        addInject = true;
        builder.removeFromField(field, JMSConnectionFactory.class);
      }
      if (addInject) {
        modified = true;
        builder.addToField(field, new AnnotationLiteral<Inject>() {
          private static final long serialVersionUID = 1L;
        });
        Produces produces = field.getAnnotation(Produces.class);
        if (produces != null) {
          builder.removeFromField(field, Produces.class);
        }


        final String typeName = field.getBaseType().getTypeName();
        if (!typeName.startsWith("javax.jms")) {
          switch (typeName) {
            case "java.lang.String":
              builder.addToField(field, new ResourceQualifier.ResourceQualifierLiteral(
                  resource.name(), resource.lookup(), resource.mappedName()));
              break;
            case "java.sql.DataSource":
              if (!(resource.name().isEmpty() && resource.mappedName().isEmpty()
                  && resource.lookup().isEmpty())) {
                builder.addToField(field, new ResourceQualifier.ResourceQualifierLiteral(
                    resource.name(), resource.lookup(), resource.mappedName()));
              }
              break;
            case "javax.ejb.EJBContext":
            case "javax.ejb.SessionContext":
              builder.addToField(field, new ResourceQualifier.ResourceQualifierLiteral(
                  "javax.ejb.SessionContext", "", ""));
              break;
            case "javax.ejb.MessageDrivenContext":
            case "javax.ejb.EntityContext":
              builder.addToField(field,
                  new ResourceQualifier.ResourceQualifierLiteral(typeName, "", ""));
              break;

            default:
              if (resource != null && !(resource.name().isEmpty() && resource.mappedName().isEmpty()
                  && resource.lookup().isEmpty())) {
                builder.addToField(field, new ResourceQualifier.ResourceQualifierLiteral(
                    resource.name(), resource.lookup(), resource.mappedName()));
              }
              break;
          }
        }
      }
    }
    Stateless stateless = findAnnotation(annotatedType.getJavaClass(), Stateless.class);

    if (stateless != null) {
      processClass(builder, stateless.name(), makeApplicationScoped || false, scopeIsPresent);
      modified = true;
    }

    Stateful stateful = findAnnotation(annotatedType.getJavaClass(), Stateful.class);

    if (stateful != null) {
      processClass(builder, stateful.name(), makeApplicationScoped || false, scopeIsPresent);
      modified = true;
    }
    try {
      Singleton singleton = findAnnotation(annotatedType.getJavaClass(), Singleton.class);
      if (singleton != null) {
        processClass(builder, singleton.name(), true, scopeIsPresent);
        modified = true;
        if (annotatedType.getAnnotation(Startup.class) != null) {
          startupSingletons.accept(annotatedType.getJavaClass());
        }
      }
    } catch (NoClassDefFoundError e) {
      // EJB 3.0
    }
    if (modified) {
      pat.setAnnotatedType(builder.create());
    }
  }


  public <T> void initializeSelfInit(@Observes ProcessInjectionTarget<T> pit) {

    boolean needToWrap = false;
    for (AnnotatedField<? super T> f : pit.getAnnotatedType().getFields()) {
      if (f.getJavaMember().getType().equals(pit.getAnnotatedType().getJavaClass())) {
        needToWrap = true;
        break;
      }
    }

    if (needToWrap) {
      final InjectionTarget<T> it = pit.getInjectionTarget();
      final Set<AnnotatedField<? super T>> annotatedTypeFields = pit.getAnnotatedType().getFields();
      final Class<?> annotatedTypeJavaClass = pit.getAnnotatedType().getJavaClass();
      InjectionTarget<T> wrapped = new InjectionTarget<T>() {

        @Override
        public void inject(final T instance, CreationalContext<T> ctx) {
          HashMap<AnnotatedField<? super T>, Object> orgValues =
              fetchOriginalValuesOfSelfFields(instance);
          it.inject(instance, ctx);
          // After injection replace all fields of self-type by enhanced ones which make sure
          // interception is handled.
          wrapDifferingValuesOfSelfFields(instance, orgValues);
        }


        @Override
        public void postConstruct(T instance) {
          it.postConstruct(instance);
        }

        @Override
        public void preDestroy(T instance) {
          it.dispose(instance);
        }

        @Override
        public void dispose(T instance) {
          it.dispose(instance);
        }

        @Override
        public Set<InjectionPoint> getInjectionPoints() {
          return it.getInjectionPoints();
        }

        @Override
        public T produce(CreationalContext<T> ctx) {
          return it.produce(ctx);
        }

        private void wrapDifferingValuesOfSelfFields(T instance,
            HashMap<AnnotatedField<? super T>, Object> orgValues) {
          for (AnnotatedField<? super T> f : annotatedTypeFields) {
            if (f.getJavaMember().getType().equals(annotatedTypeJavaClass)) {
              try {
                final Field javaMember = f.getJavaMember();
                javaMember.setAccessible(true);
                final Object currentInstance = javaMember.get(instance);
                if (currentInstance != null && currentInstance != orgValues.get(f)) {
                  // Enhancer enhancer = new Enhancer();
                  // enhancer.setSuperclass(currentInstance.getClass());
                  // enhancer.setCallback(new InvocationHandler() {
                  // @Override
                  // public Object invoke(Object o, Method method, Object[] objects)
                  // throws Throwable {
                  // WeldSetupClass.getWeldStarter().startInterceptionDecorationContext();
                  // try {
                  // return method.invoke(currentInstance, objects);
                  // } catch (Throwable thw) {
                  // if (thw instanceof InvocationTargetException) {
                  // throw thw.getCause();
                  // } else {
                  // throw thw;
                  // }
                  // } finally {
                  // WeldSetupClass.getWeldStarter().endInterceptorContext();
                  // }
                  // }
                  // });
                  // javaMember.setAccessible(true);
                  // javaMember.set(instance, enhancer.create());
                }
              } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
              }
            }
          }
        }

        private HashMap<AnnotatedField<? super T>, Object> fetchOriginalValuesOfSelfFields(
            T instance) {
          HashMap<AnnotatedField<? super T>, Object> orgValues = new HashMap<>();
          for (AnnotatedField<? super T> f : annotatedTypeFields) {
            if (f.getJavaMember().getType().equals(annotatedTypeJavaClass)) {
              final Field javaMember = f.getJavaMember();
              javaMember.setAccessible(true);
              try {
                orgValues.put(f, javaMember.get(instance));
              } catch (IllegalAccessException e) {
                new RuntimeException(e);
              }
            }
          }
          return orgValues;
        }

      };
      pit.setInjectionTarget(wrapped);
    }
  }

  private static AnnotationLiteral<Default> createDefaultAnnotation() {
    return new AnnotationLiteral<Default>() {
      private static final long serialVersionUID = 1L;
    };
  }

  private static AnnotationLiteral<Dependent> createDependentAnnotation() {
    return new AnnotationLiteral<Dependent>() {
      private static final long serialVersionUID = 1L;
    };
  }

  private static AnnotationLiteral<ApplicationScoped> createApplicationScopedAnnotation() {
    return new AnnotationLiteral<ApplicationScoped>() {
      private static final long serialVersionUID = 1L;
    };
  }

  private static String beanNameOrName(EJB ejb) {
    if (!ejb.name().isEmpty()) {
      return ejb.name();
    } else {
      return ejb.beanName();
    }
  }

  private static <T extends Annotation> T findAnnotation(Class<?> annotatedType,
      Class<T> annotation) {
    if (annotatedType.equals(Object.class)) {
      return null;
    }
    return annotatedType.getAnnotation(annotation);
  }

  private static <T extends Annotation> boolean isAnnotationPresent(Class<?> annotatedType,
      Class<T> annotation) {
    if (annotatedType.equals(Object.class)) {
      return false;
    }
    return annotatedType.isAnnotationPresent(annotation);
  }

  private static <T extends Annotation, X> boolean isAnnotationPresent(ProcessAnnotatedType<X> pat,
      Class<T> annotation) {
    return isAnnotationPresent(pat.getAnnotatedType().getJavaClass(), annotation);
  }

  private static <T> void processClass(AnnotatedTypeBuilder<T> builder, String name,
      boolean makeApplicationScoped, boolean scopeIsPresent) {
    logger.trace("processing class: {} singleton: {} scopeIsPresent: {}", name,
        makeApplicationScoped, scopeIsPresent);
    if (!scopeIsPresent) {
      if (!makeApplicationScoped || builder.getJavaClass().getFields().length > 0) {
        builder.addToClass(createDependentAnnotation());
      } else {
        builder.addToClass(createApplicationScopedAnnotation()); // For Singleton normally only
                                                                 // ApplicationScoped
      }
    }

    builder.addToClass(createDefaultAnnotation());
    if (!name.isEmpty()) {
      builder.addToClass(new EjbName.EjbNameLiteral(name));
    } else {
      builder.addToClass(DefaultLiteral.INSTANCE);
    }
  }

  private <X> boolean possiblyAsynchronous(final AnnotatedType<X> at) {

    boolean isTimer = false;
    boolean isAsynch = false;
    if (at.isAnnotationPresent(Asynchronous.class)) {
      return true;
    }

    for (AnnotatedMethod<? super X> m : at.getMethods()) {
      if (!isTimer && (m.isAnnotationPresent(Timeout.class) || m.isAnnotationPresent(Schedule.class)
          || m.isAnnotationPresent(Schedules.class))) {
        timerClasses.accept(m.getJavaMember().getDeclaringClass());
        isTimer = true;
      }
      if (!isAsynch && m.isAnnotationPresent(Asynchronous.class)) {
        isAsynch = true;
      }
    }

    return isAsynch;
  }

  private <X> void createEJBWrapper(ProcessAnnotatedType<X> pat, final AnnotatedType<X> at) {
    EjbAsynchronous ejbAsynchronous = AnnotationInstanceProvider.of(EjbAsynchronous.class);

    EjbTransactional transactionalAnnotation =
        AnnotationInstanceProvider.of(EjbTransactional.class);

    AnnotatedTypeBuilder<X> builder = new AnnotatedTypeBuilder<X>().readFromType(at);
    builder.addToClass(transactionalAnnotation);
    if (possiblyAsynchronous(at)) {
      builder.addToClass(ejbAsynchronous);
    }

    // by annotating let CDI set Wrapper to this Bean
    pat.setAnnotatedType(builder.create());
  }
}
