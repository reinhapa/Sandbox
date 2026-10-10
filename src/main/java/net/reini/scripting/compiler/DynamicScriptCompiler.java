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

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import net.reini.scripting.IScriptCodeExecutable;

/**
 * Utility for dynamically compiling and instantiating {@link IScriptCodeExecutable} instances in
 * memory.
 */
public final class DynamicScriptCompiler {
  private static final AtomicLong SCRIPT_ID_COUNTER = new AtomicLong();

  private DynamicScriptCompiler() {}

  /**
   * Compiles the given full Java source code and instantiates an {@link IScriptCodeExecutable}.
   *
   * @param fullClassName the fully qualified class name of the script
   * @param sourceCode the full Java source code defining the class
   * @return an executable instance of {@link IScriptCodeExecutable}
   * @throws IllegalArgumentException if the class name is not a valid Java identifier
   * @throws ScriptCompilationException if compilation fails, the class is not assignable, or
   *     instantiation fails
   */
  public static IScriptCodeExecutable compile(String fullClassName, String sourceCode)
      throws ScriptCompilationException {
    Objects.requireNonNull(fullClassName, "fullClassName must not be null");
    Objects.requireNonNull(sourceCode, "sourceCode must not be null");
    if (!SourceVersion.isName(fullClassName)) {
      throw new IllegalArgumentException("Invalid class name: " + fullClassName);
    }
    return compileInternal(fullClassName, sourceCode, 0, 0);
  }

  /**
   * Compiles a concise script method body wrapped inside a boilerplate class and instantiates it.
   *
   * @param scriptBody the code snippet to place inside the {@code execute} method body
   * @return an executable instance of {@link IScriptCodeExecutable}
   * @throws ScriptCompilationException if compilation fails, the class is not assignable, or
   *     instantiation fails
   */
  public static IScriptCodeExecutable compileBody(String scriptBody)
      throws ScriptCompilationException {
    Objects.requireNonNull(scriptBody, "scriptBody must not be null");
    String fullClassName =
        "net.reini.scripting.dynamic.Script_" + SCRIPT_ID_COUNTER.incrementAndGet();
    String sourceCode = createScriptSource(fullClassName, scriptBody);
    int headerLines = getHeaderLineCount(fullClassName);
    int bodyLines = countLines(scriptBody);
    return compileInternal(fullClassName, sourceCode, headerLines, bodyLines);
  }

  /**
   * Wraps a script method body into a complete Java source file implementing {@link
   * IScriptCodeExecutable}.
   *
   * @param fullClassName the target class name (may be qualified or simple)
   * @param methodBody the body of the {@code execute} method
   * @return the complete Java class source string
   * @throws IllegalArgumentException if the class name is not a valid Java identifier
   */
  public static String createScriptSource(String fullClassName, String methodBody) {
    Objects.requireNonNull(fullClassName, "fullClassName must not be null");
    Objects.requireNonNull(methodBody, "methodBody must not be null");
    if (!SourceVersion.isName(fullClassName)) {
      throw new IllegalArgumentException("Invalid class name: " + fullClassName);
    }

    int lastDot = fullClassName.lastIndexOf('.');
    String packageName = lastDot > -1 ? fullClassName.substring(0, lastDot) : null;
    String simpleClassName = lastDot > -1 ? fullClassName.substring(lastDot + 1) : fullClassName;

    StringBuilder sb = new StringBuilder();
    if (packageName != null && !packageName.isEmpty()) {
      sb.append("package ").append(packageName).append(";\n\n");
    }
    sb.append("import java.util.*;\n");
    sb.append("import net.reini.scripting.*;\n\n");
    sb.append("public class ")
        .append(simpleClassName)
        .append(" implements IScriptCodeExecutable {\n");
    sb.append("  @Override\n");
    sb.append("  public Object execute(SessionContext session, Map<String, Object> vars) {\n");
    sb.append(methodBody).append("\n");
    sb.append("  }\n");
    sb.append("}\n");
    return sb.toString();
  }

  private static IScriptCodeExecutable compileInternal(
      String fullClassName, String sourceCode, int headerLines, int bodyLines)
      throws ScriptCompilationException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    if (compiler == null) {
      throw new IllegalStateException(
          "System Java compiler not available. Ensure that the application is running on a JDK.");
    }

    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    Map<String, byte[]> byteCodes;

    try (StandardJavaFileManager standardFileManager =
        compiler.getStandardFileManager(diagnostics, null, null)) {
      InMemoryJavaFileManager fileManager = new InMemoryJavaFileManager(standardFileManager);
      CharSequenceJavaFileObject sourceFile =
          new CharSequenceJavaFileObject(fullClassName, sourceCode);

      List<String> options = new ArrayList<>();
      options.add("-proc:none");
      String classpath = System.getProperty("java.class.path");
      if (classpath != null && !classpath.isBlank()) {
        options.add("-classpath");
        options.add(classpath);
      }

      StringWriter compilerOut = new StringWriter();
      JavaCompiler.CompilationTask task =
          compiler.getTask(
              compilerOut, fileManager, diagnostics, options, null, List.of(sourceFile));

      boolean success = Boolean.TRUE.equals(task.call());
      if (!success) {
        throw new ScriptCompilationException(
            formatDiagnostics(
                fullClassName,
                diagnostics.getDiagnostics(),
                compilerOut.toString(),
                headerLines,
                bodyLines),
            diagnostics.getDiagnostics());
      }

      byteCodes = fileManager.getAllByteCodes();
    } catch (IOException e) {
      throw new ScriptCompilationException("I/O error during compilation of " + fullClassName, e);
    }

    if (!byteCodes.containsKey(fullClassName)) {
      throw new ScriptCompilationException(
          "No bytecode was generated for class: " + fullClassName, diagnostics.getDiagnostics());
    }

    ClassLoader parentClassLoader = IScriptCodeExecutable.class.getClassLoader();
    for (String emittedClassName : byteCodes.keySet()) {
      boolean parentCollision = false;
      try {
        Class.forName(emittedClassName, false, parentClassLoader);
        parentCollision = true;
      } catch (ClassNotFoundException ignored) {
        if (parentClassLoader != null
            && parentClassLoader.getResource(emittedClassName.replace('.', '/') + ".class")
                != null) {
          parentCollision = true;
        }
      } catch (LinkageError ignored) {
        parentCollision = true;
      }
      if (parentCollision) {
        throw new ScriptCompilationException(
            "Class name conflicts with an existing class on the parent classpath: "
                + emittedClassName);
      }
    }

    try {
      InMemoryClassLoader classLoader = new InMemoryClassLoader(parentClassLoader, byteCodes);
      Class<?> compiledClass = Class.forName(fullClassName, false, classLoader);
      if (compiledClass.getClassLoader() != classLoader) {
        throw new ScriptCompilationException(
            "Class name conflicts with an existing class on the parent classpath: "
                + fullClassName);
      }
      if (!IScriptCodeExecutable.class.isAssignableFrom(compiledClass)) {
        throw new ScriptCompilationException(
            "Compiled class '"
                + fullClassName
                + "' does not implement "
                + IScriptCodeExecutable.class.getName());
      }
      Object instance = compiledClass.getDeclaredConstructor().newInstance();
      return (IScriptCodeExecutable) instance;
    } catch (ClassNotFoundException e) {
      throw new ScriptCompilationException(
          "Compiled class '" + fullClassName + "' could not be loaded: " + e.getMessage(), e);
    } catch (ReflectiveOperationException | LinkageError e) {
      throw new ScriptCompilationException(
          "Failed to instantiate compiled class '" + fullClassName + "': " + e.getMessage(), e);
    }
  }

  private static int getHeaderLineCount(String fullClassName) {
    int lastDot = fullClassName.lastIndexOf('.');
    return lastDot > -1 ? 8 : 6;
  }

  private static int countLines(String text) {
    if (text == null || text.isEmpty()) {
      return 0;
    }
    int lines = 1;
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) == '\n') {
        lines++;
      }
    }
    return lines;
  }

  private static String formatDiagnostics(
      String fullClassName,
      List<Diagnostic<? extends JavaFileObject>> diagnostics,
      String compilerOutput,
      int headerLines,
      int bodyLines) {
    StringBuilder sb =
        new StringBuilder("Compilation failed for ").append(fullClassName).append(":");
    if (diagnostics != null && !diagnostics.isEmpty()) {
      for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
        sb.append(System.lineSeparator()).append(" - [").append(diagnostic.getKind()).append("] ");
        long line = diagnostic.getLineNumber();
        long col = diagnostic.getColumnNumber();
        if (line <= 0) {
          sb.append(diagnostic.getMessage(Locale.ROOT));
        } else {
          if (headerLines > 0) {
            if (line > headerLines && line <= headerLines + bodyLines) {
              long bodyLine = line - headerLines;
              sb.append("Line ").append(bodyLine);
            } else {
              sb.append("Line ").append(line).append(" (wrapper)");
            }
          } else {
            sb.append("Line ").append(line);
          }

          if (col > 0) {
            sb.append(", col ").append(col);
          }
          sb.append(": ").append(diagnostic.getMessage(Locale.ROOT));
        }
      }
    } else if (compilerOutput != null && !compilerOutput.isBlank()) {
      sb.append(System.lineSeparator()).append(" - ").append(compilerOutput.trim());
    } else {
      sb.append(" no diagnostic details available");
    }
    return sb.toString();
  }
}
