package net.reini.sandbox;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/")
public class TestServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Inject
  @SpecialVersion
  Functions functions;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doOutput(response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doOutput(response);
  }

  private void doOutput(HttpServletResponse response) throws IOException {
    try (PrintWriter writer = response.getWriter()) {
      writer.append("<html><body>");
      functions.output(writer);
      writer.println("</body></html>");
    }
  }
}
