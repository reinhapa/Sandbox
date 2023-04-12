package net.reini.jfr;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("net.reini.jfr")
@Label("Test Transaction")
@Description("this is a special event for testing")
@Category({"reinis app", "operations", "test"})
final class MyEvent extends Event {
	@Label("transaction id")
	String theId;

	@Label("some value")
	String someValue;
}