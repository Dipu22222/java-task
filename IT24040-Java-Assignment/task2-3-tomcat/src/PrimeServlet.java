import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/prime")
public class PrimeServlet extends HttpServlet {

    static boolean isPrime(long x) {
        if (x < 2) return false;
        for (long i = 2; i * i <= x; i++) {
            if (x % i == 0) return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html><body>");
        out.println("<h2>Prime Number Checker</h2>");
        out.println("<form method='get' action='prime'>");
        out.println("Enter a number: <input type='text' name='number'> ");
        out.println("<input type='submit' value='Check'>");
        out.println("</form><hr>");

        String input = req.getParameter("number");
        if (input != null && !input.trim().isEmpty()) {
            try {
                long num = Long.parseLong(input.trim());
                if (isPrime(num)) {
                    out.println("<h3 style='color:green'>" + num + " is a PRIME number.</h3>");
                } else {
                    out.println("<h3 style='color:red'>" + num + " is NOT a prime number.</h3>");
                }
            } catch (NumberFormatException e) {
                out.println("<h3 style='color:red'>Please enter a valid whole number.</h3>");
            }
        }
        out.println("</body></html>");
    }
}
