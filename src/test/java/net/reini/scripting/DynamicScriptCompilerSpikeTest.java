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

package net.reini.scripting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.reini.scripting.compiler.DynamicScriptCompiler;
import net.reini.scripting.compiler.ScriptCompilationException;

public class DynamicScriptCompilerSpikeTest {

  public record TestCustomSession(String sessionId) implements SessionContext {}

  public record SimplePoint(int x, int y) {}

  public record UserProfile(String username, int age, List<String> roles) {}

  SessionContext session = new TestCustomSession("someId");

  @Test
  void testBasicArithmeticScriptBody() throws Exception {
    String script =
        """
        int a = (Integer) vars.get("a");
        int b = (Integer) vars.get("b");
        int c = (Integer) vars.get("c");
        return a * b + c;
        """;

    IScriptCodeExecutable executable = DynamicScriptCompiler.compileBody(script);
    Map<String, Object> vars = Map.of("a", 6, "b", 7, "c", 3);

    Object result = executable.execute(session, vars);
    assertThat(result).isEqualTo(45);
  }

  @Test
  void testStringManipulationScriptBody() throws Exception {
    String script =
        """
        String first = (String) vars.get("first");
        String last = (String) vars.get("last");
        return (first + " " + last).toUpperCase(Locale.ROOT);
        """;

    IScriptCodeExecutable executable = DynamicScriptCompiler.compileBody(script);
    Map<String, Object> vars = Map.of("first", "john", "last", "doe");

    Object result = executable.execute(session, vars);
    assertThat(result).isEqualTo("JOHN DOE");
  }

  @Test
  void testMapAccessAndMutation() throws Exception {
    String script =
        """
        var user = vars.get("user");
        int accessCount = (Integer) vars.getOrDefault("accessCount", 0);
        vars.put("accessCount", accessCount + 1);
        vars.put("status", "ACTIVE");
        return "User: " + user + " (visit #" + (accessCount + 1) + ")";
        """;

    IScriptCodeExecutable executable = DynamicScriptCompiler.compileBody(script);
    Map<String, Object> vars = new HashMap<>();
    vars.put("user", "Alice");
    vars.put("accessCount", 4);

    Object result = executable.execute(session, vars);
    assertThat(result).isEqualTo("User: Alice (visit #5)");
    assertThat(vars)
        .containsEntry("accessCount", 5)
        .containsEntry("status", "ACTIVE")
        .containsEntry("user", "Alice");
  }

  @Test
  void testSessionContextIdentityAndCasting() throws Exception {
    String script =
        """
        if (session instanceof net.reini.scripting.DynamicScriptCompilerSpikeTest.TestCustomSession customSession) {
          return customSession.sessionId();
        }
        return session;
        """;

    IScriptCodeExecutable executable = DynamicScriptCompiler.compileBody(script);

    TestCustomSession customSession = new TestCustomSession("session-xyz-987");
    Object customResult = executable.execute(customSession, Map.of());
    assertThat(customResult).isEqualTo("session-xyz-987");

    SessionContext anonymousSession = mock();
    Object anonymousResult = executable.execute(anonymousSession, Map.of());
    assertThat(anonymousResult).isSameAs(anonymousSession);
  }

  @Test
  void testCollectionsCreationAndReturn() throws Exception {
    String script =
        """
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");

        Set<Integer> set = Set.of(10, 20, 30);
        Map<String, Object> map = Map.of("list", list, "set", set, "count", 3);
        return map;
        """;

    IScriptCodeExecutable executable = DynamicScriptCompiler.compileBody(script);

    Object result = executable.execute(session, Map.of());
    assertThat(result).isInstanceOf(Map.class);

    @SuppressWarnings("unchecked")
    Map<String, Object> resultMap = (Map<String, Object>) result;
    assertThat(resultMap.get("list")).isEqualTo(List.of("one", "two", "three"));
    assertThat(resultMap.get("set")).isEqualTo(Set.of(10, 20, 30));
    assertThat(resultMap.get("count")).isEqualTo(3);
  }

  @Test
  void testPojoCreationAndReturn() throws Exception {
    String script =
        """
        int x = (Integer) vars.get("x");
        int y = (Integer) vars.get("y");
        return new net.reini.scripting.DynamicScriptCompilerSpikeTest.SimplePoint(x, y);
        """;

    IScriptCodeExecutable executable = DynamicScriptCompiler.compileBody(script);
    Map<String, Object> vars = Map.of("x", 15, "y", 25);

    Object result = executable.execute(session, vars);
    assertThat(result).isEqualTo(new SimplePoint(15, 25));
  }

  @Test
  void testCustomRecordReturn() throws Exception {
    String script =
        """
        String name = (String) vars.get("name");
        int age = (Integer) vars.get("age");
        List<String> roles = List.of("ADMIN", "USER");
        return new net.reini.scripting.DynamicScriptCompilerSpikeTest.UserProfile(name, age, roles);
        """;

    IScriptCodeExecutable executable = DynamicScriptCompiler.compileBody(script);
    Map<String, Object> vars = Map.of("name", "Bob", "age", 35);

    Object result = executable.execute(session, vars);
    assertThat(result).isEqualTo(new UserProfile("Bob", 35, List.of("ADMIN", "USER")));
  }

  @Test
  void testNullReturnValue() throws Exception {
    String script =
        """
        if (vars.isEmpty()) {
          return null;
        }
        return "not empty";
        """;

    IScriptCodeExecutable executable = DynamicScriptCompiler.compileBody(script);

    Object nullResult = executable.execute(session, Map.of());
    assertThat(nullResult).isNull();

    Object notNullResult = executable.execute(session, Map.of("key", "val"));
    assertThat(notNullResult).isEqualTo("not empty");
  }

  @Test
  void testLoopAndAlgorithmBody() throws Exception {
    String script =
        """
        int n = (Integer) vars.get("n");
        if (n <= 0) return 0L;
        if (n == 1) return 1L;

        long prev = 0L;
        long current = 1L;
        for (int i = 2; i <= n; i++) {
          long next = prev + current;
          prev = current;
          current = next;
        }
        return current;
        """;

    IScriptCodeExecutable executable = DynamicScriptCompiler.compileBody(script);

    assertThat(executable.execute(session, Map.of("n", 0))).isEqualTo(0L);
    assertThat(executable.execute(session, Map.of("n", 1))).isEqualTo(1L);
    assertThat(executable.execute(session, Map.of("n", 2))).isEqualTo(1L);
    assertThat(executable.execute(session, Map.of("n", 10))).isEqualTo(55L);
  }

  @Test
  void testFullClassCompilationWorkflow() throws Exception {
    String sourceCode =
        """
        package test.spike;

        import java.util.List;
        import java.util.Map;
        import net.reini.scripting.IScriptCodeExecutable;
        import net.reini.scripting.SessionContext;

        public class OrderDiscountCalculator implements IScriptCodeExecutable {
          private static final double DISCOUNT_RATE = 0.15;

          private double calculateDiscount(double amount) {
            return amount * DISCOUNT_RATE;
          }

          @Override
          public Object execute(SessionContext session, Map<String, Object> vars) {
            double total = (Double) vars.getOrDefault("total", 0.0);
            double discount = calculateDiscount(total);
            return total - discount;
          }
        }
        """;

    IScriptCodeExecutable executable =
        DynamicScriptCompiler.compile("test.spike.OrderDiscountCalculator", sourceCode);
    assertThat(executable).isNotNull();

    Map<String, Object> vars = Map.of("total", 200.0);

    Object result = executable.execute(session, vars);
    assertThat(result).isEqualTo(170.0);
  }

  @Test
  void testDiagnosticSyntaxErrorReportsLineAndMessage() {
    String invalidScript =
        """
        int x = 10;
        int y = ;
        return x + y;
        """;

    assertThatThrownBy(() -> DynamicScriptCompiler.compileBody(invalidScript))
        .isInstanceOf(ScriptCompilationException.class)
        .hasMessageContaining("Line 2, col")
        .hasMessageContaining("illegal start of expression")
        .satisfies(
            throwable -> {
              ScriptCompilationException ex = (ScriptCompilationException) throwable;
              assertThat(ex.getDiagnostics()).isNotEmpty();
              assertThat(ex.getDiagnostics().getFirst().getKind())
                  .isEqualTo(javax.tools.Diagnostic.Kind.ERROR);
            });
  }

  @Test
  void testDiagnosticTypeMismatchReportsError() {
    String typeMismatchScript =
        """
        String text = 12345;
        return text;
        """;

    assertThatThrownBy(() -> DynamicScriptCompiler.compileBody(typeMismatchScript))
        .isInstanceOf(ScriptCompilationException.class)
        .hasMessageContaining("Line 1")
        .hasMessageContaining("incompatible types")
        .satisfies(
            throwable -> {
              ScriptCompilationException ex = (ScriptCompilationException) throwable;
              assertThat(ex.getDiagnostics()).isNotEmpty();
            });
  }

  @Test
  void testExecutionRuntimeExceptionPropagatesDirectly() throws Exception {
    String failingScript =
        """
        String mode = (String) vars.get("mode");
        if ("illegal".equals(mode)) {
          throw new IllegalArgumentException("Invalid mode provided: " + mode);
        }
        int divisor = (Integer) vars.get("divisor");
        return 100 / divisor;
        """;

    IScriptCodeExecutable executable = DynamicScriptCompiler.compileBody(failingScript);

    assertThatThrownBy(() -> executable.execute(session, Map.of("mode", "illegal")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid mode provided: illegal");

    assertThatThrownBy(() -> executable.execute(session, Map.of("divisor", 0)))
        .isInstanceOf(ArithmeticException.class)
        .hasMessageContaining("/ by zero");
  }

  @Test
  void testExecutionRuntimeTypeMismatchPropagatesClassCastException() throws Exception {
    String script =
        """
        String val = (String) vars.get("key");
        return val;
        """;
    IScriptCodeExecutable executable = DynamicScriptCompiler.compileBody(script);
    assertThatThrownBy(() -> executable.execute(session, Map.of("key", 123)))
        .isInstanceOf(ClassCastException.class);
  }
}
