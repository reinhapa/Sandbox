package net.reini.sandbox;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Singleton
@Startup
public class SimpleSingleton {
	@SpecialVersion
	@Inject
	Event<TestEvent> event;

	@Schedule(persistent = false, second = "*/5", minute = "*", hour = "*")
	public void timer() {
		event.fire(new TestEvent());
	}
}