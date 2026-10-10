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

import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/** Exception thrown when dynamic compilation or instantiation of a script fails. */
public class ScriptCompilationException extends Exception {
  private static final long serialVersionUID = 1L;

  private final transient List<Diagnostic<? extends JavaFileObject>> diagnostics;

  public ScriptCompilationException(String message) {
    super(message);
    this.diagnostics = List.of();
  }

  public ScriptCompilationException(String message, Throwable cause) {
    super(message, cause);
    this.diagnostics = List.of();
  }

  public ScriptCompilationException(Throwable cause) {
    super(cause);
    this.diagnostics = List.of();
  }

  public ScriptCompilationException(
      String message, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
    super(message);
    this.diagnostics = diagnostics != null ? List.copyOf(diagnostics) : List.of();
  }

  public ScriptCompilationException(
      String message, Throwable cause, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
    super(message, cause);
    this.diagnostics = diagnostics != null ? List.copyOf(diagnostics) : List.of();
  }

  /**
   * Returns the compiler diagnostics captured during compilation, if any.
   *
   * @return list of compiler diagnostics
   */
  public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
    return diagnostics != null ? diagnostics : List.of();
  }
}
