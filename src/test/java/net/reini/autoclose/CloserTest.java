package net.reini.autoclose;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CloserTest {
  private AutoCloseable closeable1;
  private AutoCloseable closeable2;
  private AutoCloseable closeable3;

  @Before
  public void setUp() {
    closeable1 = createStrictMock(AutoCloseable.class);
    closeable2 = createStrictMock(AutoCloseable.class);
    closeable3 = createStrictMock(AutoCloseable.class);
  }

  @Test
  public void closeAllWithoutError() throws Exception {
    closeable3.close();
    closeable2.close();
    closeable1.close();

    replayAll();
    Closer.close(closeable1, closeable2, closeable3);
    verifyAll();
  }

  @Test
  public void closeAllWithErrorOnFirst() throws Exception {
    Exception expected = new Exception("error1");

    closeable3.close();
    expectLastCall().andThrow(expected);
    closeable2.close();
    closeable1.close();

    replayAll();
    try {
      Closer.close(closeable1, closeable2, closeable3);
      fail("exception expected");
    } catch (Exception e) {
      assertEquals(expected, e);
    }
    verifyAll();
  }

  @Test
  public void closeAllWithErrorOnSubsequent() throws Exception {
    closeable3.close();
    closeable2.close();
    closeable1.close();

    replayAll();
    Closer.close(closeable1, closeable2, closeable3);
    verifyAll();
  }

  private void verifyAll() {
    verify(closeable1);
    verify(closeable2);
    verify(closeable3);
  }

  private void replayAll() {
    replay(closeable1);
    replay(closeable2);
    replay(closeable3);
  }
}
