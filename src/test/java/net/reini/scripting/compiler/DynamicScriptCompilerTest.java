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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.reini.scripting.IScriptCodeExecutable;
import net.reini.scripting.SessionContext;

class DynamicScriptCompilerTest {

  @Test
  void testCompileFullClass() throws Exception {
    String source =
        """
        package test.script;
        import java.util.Map;
        import net.reini.scripting.IScriptCodeExecutable;
        import net.reini.scripting.SessionContext;

        public class FullScript implements IScriptCodeExecutable {
          @Override
          public Object execute(SessionContext session, Map<String, Object> vars) {
            int a = (Integer) vars.get("a");
            int b = (Integer) vars.get("b");
            return a + b;
          }
        }
        """;

    IScriptCodeExecutable executable =
        DynamicScriptCompiler.compile("test.script.FullScript", source);
    assertThat(executable).isNotNull();

    SessionContext session = mock();
    Map<String, Object> vars = Map.of("a", 10, "b", 32);
    Object result = executable.execute(session, vars);
    assertThat(result).isEqualTo(42);
  }

  @Test
  void testCompileBody() throws Exception {
    String body =
        """
        String greeting = (String) vars.get("greeting");
        String name = (String) vars.get("name");
        return greeting + ", " + name + "!";
        """;

    IScriptCodeExecutable executable = DynamicScriptCompiler.compileBody(body);
    assertThat(executable).isNotNull();

    SessionContext session = mock();
    Map<String, Object> vars = Map.of("greeting", "Hello", "name", "World");
    Object result = executable.execute(session, vars);
    assertThat(result).isEqualTo("Hello, World!");
  }

  @Test
  void testCompileBodyUniqueClasses() throws Exception {
    IScriptCodeExecutable exec1 = DynamicScriptCompiler.compileBody("return 1;");
    IScriptCodeExecutable exec2 = DynamicScriptCompiler.compileBody("return 2;");

    assertThat(exec1.getClass()).isNotEqualTo(exec2.getClass());
    SessionContext session = mock();
    Map<String, Object> vars = new HashMap<>();

    assertThat(exec1.execute(session, vars)).isEqualTo(1);
    assertThat(exec2.execute(session, vars)).isEqualTo(2);
  }

  @Test
  void testCreateScriptSourceWithPackage() {
    String source =
        DynamicScriptCompiler.createScriptSource("com.example.TestScript", "return 123;");
    assertThat(source)
        .contains("package com.example;")
        .contains("public class TestScript implements IScriptCodeExecutable")
        .contains("return 123;");
  }

  @Test
  void testCreateScriptSourceDefaultPackage() {
    String source = DynamicScriptCompiler.createScriptSource("SimpleScript", "return 123;");
    assertThat(source)
        .doesNotContain("package ")
        .contains("public class SimpleScript implements IScriptCodeExecutable")
        .contains("return 123;");
  }

  @Test
  void testCompileSyntaxErrorThrowsScriptCompilationException() {
    String invalidSource =
        """
        package test.script;
        import net.reini.scripting.IScriptCodeExecutable;
        import net.reini.scripting.SessionContext;
        import java.util.Map;

        public class BadScript implements IScriptCodeExecutable {
          @Override
          public Object execute(SessionContext session, Map<String, Object> vars) {
            invalid syntax line here;
          }
        }
        """;

    assertThatThrownBy(() -> DynamicScriptCompiler.compile("test.script.BadScript", invalidSource))
        .isInstanceOf(ScriptCompilationException.class)
        .hasMessageContaining("Compilation failed for test.script.BadScript")
        .satisfies(
            throwable -> {
              ScriptCompilationException sce = (ScriptCompilationException) throwable;
              assertThat(sce.getDiagnostics()).isNotEmpty();
            });
  }

  @Test
  void testCompileClassNotImplementingInterface() {
    String source =
        """
        package test.script;
        public class NotAnExecutable {
          public NotAnExecutable() {}
        }
        """;

    assertThatThrownBy(() -> DynamicScriptCompiler.compile("test.script.NotAnExecutable", source))
        .isInstanceOf(ScriptCompilationException.class)
        .hasMessageContaining("does not implement net.reini.scripting.IScriptCodeExecutable");
  }

  @Test
  void testCompileClassWithoutDefaultConstructor() {
    String source =
        """
        package test.script;
        import java.util.Map;
        import net.reini.scripting.IScriptCodeExecutable;
        import net.reini.scripting.SessionContext;

        public class NoDefaultConstructor implements IScriptCodeExecutable {
          public NoDefaultConstructor(String argument) {}

          @Override
          public Object execute(SessionContext session, Map<String, Object> vars) {
            return null;
          }
        }
        """;

    assertThatThrownBy(
            () -> DynamicScriptCompiler.compile("test.script.NoDefaultConstructor", source))
        .isInstanceOf(ScriptCompilationException.class)
        .hasMessageContaining("Failed to instantiate compiled class");
  }

  public static final java.util.concurrent.atomic.AtomicBoolean UNINITIALIZED_HELPER_INITIALIZED =
      new java.util.concurrent.atomic.AtomicBoolean(false);

  public static class UninitializedParentHelper {
    static {
      UNINITIALIZED_HELPER_INITIALIZED.set(true);
    }
  }

  @Test
  void testCompileMismatchClassName() {
    String source =
        """
        package test.script;
        import java.util.Map;
        import net.reini.scripting.IScriptCodeExecutable;
        import net.reini.scripting.SessionContext;

        public class ActualName implements IScriptCodeExecutable {
          @Override
          public Object execute(SessionContext session, Map<String, Object> vars) {
            return null;
          }
        }
        """;

    // Requesting ExpectedName when ActualName is in source
    assertThatThrownBy(() -> DynamicScriptCompiler.compile("test.script.ExpectedName", source))
        .isInstanceOf(ScriptCompilationException.class)
        .hasMessageContaining("should be declared in a file named")
        .hasMessageContaining("ActualName.java")
        .satisfies(
            throwable -> {
              ScriptCompilationException sce = (ScriptCompilationException) throwable;
              assertThat(sce.getDiagnostics()).isNotEmpty();
            });
  }

  @Test
  void testCompileClassShadowingParentClasspathThrowsScriptCompilationException() {
    String existingClassName = UninitializedParentHelper.class.getName();
    String source =
        """
        package net.reini.scripting.compiler;
        import java.util.Map;
        import net.reini.scripting.IScriptCodeExecutable;
        import net.reini.scripting.SessionContext;
        public class DynamicScriptCompilerTest$UninitializedParentHelper implements IScriptCodeExecutable {
          @Override
          public Object execute(SessionContext session, Map<String, Object> vars) {
            return "dynamic";
          }
        }
        """;

    assertThatThrownBy(() -> DynamicScriptCompiler.compile(existingClassName, source))
        .isInstanceOf(ScriptCompilationException.class)
        .hasMessageContaining(
            "Class name conflicts with an existing class on the parent classpath: "
                + existingClassName);

    // Verify parent class was not initialized during collision check
    assertThat(UNINITIALIZED_HELPER_INITIALIZED.get()).isFalse();
  }

  @Test
  void testCompileAuxiliaryClassShadowingParentClasspathThrowsScriptCompilationException() {
    String auxiliaryClassName = CharSequenceJavaFileObjectTest.class.getName();
    String source =
        """
        package net.reini.scripting.compiler;
        import java.util.Map;
        import net.reini.scripting.IScriptCodeExecutable;
        import net.reini.scripting.SessionContext;

        class CharSequenceJavaFileObjectTest {
          public static String probe() { return "shadow"; }
        }

        public class AuxiliaryShadowScript implements IScriptCodeExecutable {
          @Override
          public Object execute(SessionContext session, Map<String, Object> vars) {
            return CharSequenceJavaFileObjectTest.probe();
          }
        }
        """;

    assertThatThrownBy(
            () ->
                DynamicScriptCompiler.compile(
                    "net.reini.scripting.compiler.AuxiliaryShadowScript", source))
        .isInstanceOf(ScriptCompilationException.class)
        .hasMessageContaining(
            "Class name conflicts with an existing class on the parent classpath: "
                + auxiliaryClassName);
  }

  @Test
  void testCompileBodyDiagnosticReportsBodyRelativeLine() {
    String singleLineBody = "return (Integer) vars.get(\"a\") + ;";
    assertThatThrownBy(() -> DynamicScriptCompiler.compileBody(singleLineBody))
        .isInstanceOf(ScriptCompilationException.class)
        .hasMessageContaining("Line 1, col")
        .hasMessageNotContaining("Line 9")
        .hasMessageNotContaining("Line -1");

    String multiLineBody = "int x = 10;\nreturn x + ;";
    assertThatThrownBy(() -> DynamicScriptCompiler.compileBody(multiLineBody))
        .isInstanceOf(ScriptCompilationException.class)
        .hasMessageContaining("Line 2, col")
        .hasMessageNotContaining("Line 10")
        .hasMessageNotContaining("Line -1");
  }

  @Test
  void testCompileBodyDiagnosticReportsWrapperLine() {
    // Missing return statement is detected on the closing brace in the wrapper
    String missingReturnBody = "int x = 1;";
    assertThatThrownBy(() -> DynamicScriptCompiler.compileBody(missingReturnBody))
        .isInstanceOf(ScriptCompilationException.class)
        .hasMessageContaining("(wrapper)")
        .hasMessageContaining("missing return statement");
  }

  @Test
  void testCompileStaticInitializerFailureWrappedInScriptCompilationException() {
    String source =
        """
        package test.script;
        import java.util.Map;
        import net.reini.scripting.IScriptCodeExecutable;
        import net.reini.scripting.SessionContext;

        public class StaticInitFailure implements IScriptCodeExecutable {
          static {
            if (true) {
              throw new RuntimeException("static initializer error");
            }
          }

          @Override
          public Object execute(SessionContext session, Map<String, Object> vars) {
            return null;
          }
        }
        """;

    assertThatThrownBy(() -> DynamicScriptCompiler.compile("test.script.StaticInitFailure", source))
        .isInstanceOf(ScriptCompilationException.class)
        .hasMessageContaining("Failed to instantiate compiled class")
        .hasCauseInstanceOf(ExceptionInInitializerError.class);
  }

  @Test
  void testExecutionRuntimeExceptionPropagates() throws Exception {
    IScriptCodeExecutable executable =
        DynamicScriptCompiler.compileBody("throw new IllegalStateException(\"runtime error\");");
    assertThatThrownBy(() -> executable.execute(null, Map.of()))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("runtime error");
  }

  @Test
  void testInvalidClassNames() {
    assertThatThrownBy(() -> DynamicScriptCompiler.compile("", "class A {}"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid class name");

    assertThatThrownBy(() -> DynamicScriptCompiler.compile("com.example.", "class A {}"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid class name");

    assertThatThrownBy(() -> DynamicScriptCompiler.compile(".com.example", "class A {}"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid class name");

    assertThatThrownBy(() -> DynamicScriptCompiler.compile("123Class", "class A {}"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid class name");

    assertThatThrownBy(() -> DynamicScriptCompiler.compile("int", "class A {}"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid class name");

    assertThatThrownBy(() -> DynamicScriptCompiler.createScriptSource("", "return 1;"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid class name");

    assertThatThrownBy(() -> DynamicScriptCompiler.createScriptSource("com.example.", "return 1;"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid class name");
  }

  @Test
  void testNullArguments() {
    assertThatThrownBy(() -> DynamicScriptCompiler.compile(null, "class A {}"))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("fullClassName");

    assertThatThrownBy(() -> DynamicScriptCompiler.compile("A", null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("sourceCode");

    assertThatThrownBy(() -> DynamicScriptCompiler.compileBody(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("scriptBody");

    assertThatThrownBy(() -> DynamicScriptCompiler.createScriptSource(null, "return 1;"))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("fullClassName");

    assertThatThrownBy(() -> DynamicScriptCompiler.createScriptSource("A", null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("methodBody");
  }
}
