package net.reini.mysql;

import java.sql.Statement;
import java.util.function.Consumer;

final class ImportAction implements Consumer<String> {
	private final Statement statement;
	private final StringBuilder buffer;
	private final ImportActionCallback actionCallback;

	private int processedStatements;

	ImportAction(Statement statement, ImportActionCallback actionCallback) {
		this.statement = statement;
		this.actionCallback = actionCallback;
		this.buffer = new StringBuilder();
	}

	@Override
	public void accept(String line) {
		if (line.isEmpty() || line.startsWith("--")) {
			return;
		}
		if (buffer.length() > 0) {
			buffer.append('\n');
		}
		buffer.append(line);
		if (line.endsWith(";")) {
			processedStatements++;
			String sqlStatement = buffer.toString();
			try {
				statement.execute(sqlStatement);
				actionCallback.processed(processedStatements, sqlStatement, null);
			} catch (Exception e) {
				actionCallback.processed(processedStatements, sqlStatement, e);
			}
			buffer.setLength(0);
		}
	}
}