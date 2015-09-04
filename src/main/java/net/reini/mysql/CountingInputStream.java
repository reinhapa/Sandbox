package net.reini.mysql;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.IntConsumer;

final class CountingInputStream extends FilterInputStream {
	private final CalculationFunction readBytesFunction;

	protected CountingInputStream(InputStream in, IntConsumer readConsumer) {
		super(in);
		readBytesFunction = read -> {
			if (read > 0) {
				readConsumer.accept(read);
			}
			return read;
		};
	}

	@Override
	public int read() throws IOException {
		return readBytesFunction.apply(super.read());
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return readBytesFunction.apply(super.read(b, off, len));
	}

	@FunctionalInterface
	interface CalculationFunction {
		int apply(int value);
	}
}