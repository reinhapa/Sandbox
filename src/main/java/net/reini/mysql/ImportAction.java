/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
