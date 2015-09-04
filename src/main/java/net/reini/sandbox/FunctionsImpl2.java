package net.reini.sandbox;

import java.io.IOException;
import java.io.Writer;

import javax.ejb.Stateless;

@SpecialVersion
@Stateless
public class FunctionsImpl2 implements Functions {
	@Override
	public void output(Writer writer) throws IOException {
		writer.append(getClass().getName());
	}
}
