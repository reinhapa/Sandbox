package net.reini.cdi.se;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

@InterceptorBinding
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Check {
  @Nonbinding
  String user();

  @Nonbinding
  String password() default "";
}
