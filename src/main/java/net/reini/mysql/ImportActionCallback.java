package net.reini.mysql;

@FunctionalInterface
public interface ImportActionCallback {
  void processed(int stmtNumber, String statement, Exception error);
}
