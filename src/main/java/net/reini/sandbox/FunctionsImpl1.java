package net.reini.sandbox;

import java.io.IOException;
import java.io.Writer;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

@Default
@Stateless
public class FunctionsImpl1 implements Functions {
	@Inject
	@SystemProperty(name = "user.dir")
	String userDir;

	@Override
	public void output(Writer writer) throws IOException {
		writer.append(getClass().getName());
	}
}
