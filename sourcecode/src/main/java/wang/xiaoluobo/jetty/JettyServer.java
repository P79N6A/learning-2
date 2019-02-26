package wang.xiaoluobo.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * http://localhost:8200/hello/
 *
 * @see <a href="https://www.eclipse.org/jetty/documentation/current/">https://www.eclipse.org/jetty/documentation/current/</a>
 */
public class JettyServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8200);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/hello");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new HelloServlet()), "/*");
        server.start();
    }


    public static class HelloServlet extends HttpServlet {

        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("Hello Jetty");
        }
    }
}
