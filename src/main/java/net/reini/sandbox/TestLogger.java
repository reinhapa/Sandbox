package net.reini.sandbox;

import java.io.IOException;
import java.io.Writer;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;

@Singleton
@Startup
public class TestLogger implements Functions, EventListener {
	@Override
	public void onMessage(@Observes @SpecialVersion TestEvent event) {
		System.out.println(event);
	}

	@Override
	public void output(Writer writer) throws IOException {
		writer.append(getClass().getName());
	}
}