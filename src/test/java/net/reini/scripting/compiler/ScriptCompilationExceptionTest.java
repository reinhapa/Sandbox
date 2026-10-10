/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.reini.scripting.compiler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.jupiter.api.Test;

class ScriptCompilationExceptionTest {

  @Test
  void testConstructorWithMessage() {
    ScriptCompilationException ex = new ScriptCompilationException("error message");
    assertThat(ex.getMessage()).isEqualTo("error message");
    assertThat(ex.getCause()).isNull();
    assertThat(ex.getDiagnostics()).isEmpty();
  }

  @Test
  void testConstructorWithMessageAndCause() {
    RuntimeException cause = new RuntimeException("root cause");
    ScriptCompilationException ex = new ScriptCompilationException("error message", cause);
    assertThat(ex.getMessage()).isEqualTo("error message");
    assertThat(ex.getCause()).isSameAs(cause);
    assertThat(ex.getDiagnostics()).isEmpty();
  }

  @Test
  void testConstructorWithCause() {
    RuntimeException cause = new RuntimeException("root cause");
    ScriptCompilationException ex = new ScriptCompilationException(cause);
    assertThat(ex.getCause()).isSameAs(cause);
    assertThat(ex.getDiagnostics()).isEmpty();
  }

  @Test
  void testConstructorWithMessageAndDiagnostics() {
    List<Diagnostic<? extends JavaFileObject>> diagnostics = null;
    ScriptCompilationException ex =
        new ScriptCompilationException("error with diagnostics", diagnostics);
    assertThat(ex.getMessage()).isEqualTo("error with diagnostics");
    assertThat(ex.getDiagnostics()).isEmpty();
  }

  @Test
  void testConstructorWithMessageCauseAndDiagnostics() {
    RuntimeException cause = new RuntimeException("root cause");
    List<Diagnostic<? extends JavaFileObject>> diagnostics = null;
    ScriptCompilationException ex =
        new ScriptCompilationException("error with diagnostics", cause, diagnostics);
    assertThat(ex.getMessage()).isEqualTo("error with diagnostics");
    assertThat(ex.getCause()).isSameAs(cause);
    assertThat(ex.getDiagnostics()).isEmpty();
  }
}
