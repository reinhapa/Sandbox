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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;

import net.reini.scripting.IScriptCodeExecutable;

class InMemoryClassLoaderTest {

  @Test
  void testNullArguments() {
    assertThrows(
        NullPointerException.class,
        () -> new InMemoryClassLoader(ClassLoader.getSystemClassLoader(), null));
    assertThrows(NullPointerException.class, () -> new InMemoryClassLoader(null));
  }

  @Test
  void testParentDelegationDefaultConstructor() throws Exception {
    InMemoryClassLoader loader = new InMemoryClassLoader(Map.of());

    Class<?> loadedInterface = loader.loadClass(IScriptCodeExecutable.class.getName());
    assertSame(IScriptCodeExecutable.class, loadedInterface);

    Class<?> stringClass = loader.loadClass("java.lang.String");
    assertSame(String.class, stringClass);
  }

  @Test
  void testParentDelegationExplicitParent() throws Exception {
    InMemoryClassLoader loader = new InMemoryClassLoader(getClass().getClassLoader(), Map.of());

    Class<?> loadedInterface = loader.loadClass(IScriptCodeExecutable.class.getName());
    assertSame(IScriptCodeExecutable.class, loadedInterface);

    Class<?> stringClass = loader.loadClass("java.lang.String");
    assertSame(String.class, stringClass);
  }

  @Test
  void testExplicitNullParentUsesBootstrapClassLoader() throws Exception {
    InMemoryClassLoader loader = new InMemoryClassLoader(null, Map.of());

    // Bootstrap class loader can load standard core classes
    Class<?> stringClass = loader.loadClass("java.lang.String");
    assertSame(String.class, stringClass);

    // Bootstrap class loader cannot load application classes
    assertThrows(
        ClassNotFoundException.class,
        () -> loader.loadClass(IScriptCodeExecutable.class.getName()));
  }

  @Test
  void testClassNotFound() {
    InMemoryClassLoader loader = new InMemoryClassLoader(getClass().getClassLoader(), Map.of());
    assertThrows(ClassNotFoundException.class, () -> loader.loadClass("net.reini.NonExistent"));
  }

  @Test
  void testCompileAndLoadExecutableWithNestedAndAnonymousClasses() throws Exception {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    assertNotNull(compiler, "Java compiler must be available in JDK");

    StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);

    String className = "net.reini.scripting.compiler.SampleDynamicExecutable";
    String sourceCode =
        """
        package net.reini.scripting.compiler;
        import java.util.Map;
        import java.util.function.Supplier;
        import net.reini.scripting.IScriptCodeExecutable;
        import net.reini.scripting.SessionContext;
        public class SampleDynamicExecutable implements IScriptCodeExecutable {
          public static class NestedHelper {
            public String prefix() { return "result:"; }
          }
          @Override
          public Object execute(SessionContext session, Map<String, Object> vars) {
            NestedHelper helper = new NestedHelper();
            Supplier<String> supplier = new Supplier<>() {
              @Override
              public String get() {
                return helper.prefix() + vars.get("value");
              }
            };
            return supplier.get();
          }
        }
        """;

    CharSequenceJavaFileObject sourceObject = new CharSequenceJavaFileObject(className, sourceCode);

    try (InMemoryJavaFileManager fileManager = new InMemoryJavaFileManager(standardFileManager)) {
      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
      String classpath = System.getProperty("java.class.path");
      List<String> options = List.of("-classpath", classpath);

      JavaCompiler.CompilationTask task =
          compiler.getTask(null, fileManager, diagnostics, options, null, List.of(sourceObject));

      boolean success = task.call();
      assertTrue(success, "Compilation must succeed: " + diagnostics.getDiagnostics());

      Map<String, byte[]> byteCodes = fileManager.getAllByteCodes();
      // Should have generated main class, nested class, and anonymous class
      assertTrue(byteCodes.containsKey(className));
      assertTrue(byteCodes.containsKey(className + "$NestedHelper"));
      assertTrue(byteCodes.containsKey(className + "$1"));

      InMemoryClassLoader classLoader =
          new InMemoryClassLoader(getClass().getClassLoader(), byteCodes);

      Class<?> compiledClass = classLoader.loadClass(className);
      assertTrue(IScriptCodeExecutable.class.isAssignableFrom(compiledClass));

      Object instance = compiledClass.getDeclaredConstructor().newInstance();
      assertInstanceOf(IScriptCodeExecutable.class, instance);

      IScriptCodeExecutable executable = (IScriptCodeExecutable) instance;
      Object result = executable.execute(null, Map.of("value", "42"));
      assertEquals("result:42", result);
    }
  }
}
