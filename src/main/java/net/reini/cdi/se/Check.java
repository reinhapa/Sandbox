package net.reini.cdi.se;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;

/**
 * Marks a checked part of code using an interceptor.
 */
@InterceptorBinding
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Check {
  /**
   * Defines a non-binding name value
   *
   * @return the username
   */
  @Nonbinding
  String user();

  /**
   * Defines a non binding password value
   *
   * @return the password value
   */
  @Nonbinding
  String password() default "";
}
