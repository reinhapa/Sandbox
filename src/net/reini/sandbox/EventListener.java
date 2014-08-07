package net.reini.sandbox;

import javax.enterprise.event.Observes;

public interface EventListener {
	public void onMessage(@Observes @SpecialVersion TestEvent event);
}
