package net.reini.autoclose;

public class Closer {

  @SuppressWarnings("unchecked")
  public static <X extends Throwable> void close(AutoCloseable... closeables) throws X {
    Exception rootException = null;
    for (AutoCloseable closeable : closeables) {
      try {
        closeable.close();
      } catch (Exception e) {
        if (rootException == null) {
          rootException = e;
        } else {
          rootException.addSuppressed(e);
        }
      }
    }
    if (rootException != null) {
      throw (X) rootException;
    }
  }
}
