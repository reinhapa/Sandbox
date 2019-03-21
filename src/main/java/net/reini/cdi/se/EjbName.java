package net.reini.cdi.se;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface EjbName {
  String name() default "";

  public class EjbNameLiteral extends AnnotationLiteral<EjbName> implements EjbName {

    private static final long serialVersionUID = 7107494117787642445L;
    private final String name;

    public EjbNameLiteral(String name) {
      this.name = name;
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public String toString() {
      return "EJbQualifierLiteral [name=" + name + "]";
    }

  }
}
