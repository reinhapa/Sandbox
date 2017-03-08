package net.reini.sandbox;

import java.lang.reflect.Field;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class PropertyProvider {
	@Produces
	@SystemProperty
	public String getStringProperty(InjectionPoint ip) {
		String propertyName = ip.getAnnotated().getAnnotation(SystemProperty.class).name();
		if (propertyName.isEmpty()){
			propertyName = ((Field)ip.getMember()).getName();
		}
		return System.getProperty(propertyName);
	}
}
