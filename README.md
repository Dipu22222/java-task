<h1 align="center">🎓 IT24040 (Dipu Mondol)</h1>
<h3 align="center">Java Course Assignment: JDBC, Apache Tomcat &amp; Servlets</h3>

<p align="center">
  <img src="https://img.shields.io/badge/Student-IT24040-blue" alt="Student ID">
  <img src="https://img.shields.io/badge/Java-17%2B-orange" alt="Java">
  <img src="https://img.shields.io/badge/Apache%20Tomcat-11-yellow" alt="Tomcat">
  <img src="https://img.shields.io/badge/Database-H2-lightgrey" alt="H2">
</p>

---

## 📋 Tasks

| # | Task | Status |
|---|------|--------|
| 1 | JDBC test: insert the nth prime number into a database | ✅ Done |
| 2 | Configure Apache Tomcat and run a Hello World servlet | ✅ Done |
| 3 | Check primality from the browser using a servlet | ✅ Done |

## 🛠️ Tools Used

- Java (JDK 17 or newer)
- IntelliJ IDEA
- Apache Tomcat 11 (uses the `jakarta.servlet` package)
- H2 Database (embedded, no installation needed) with JDBC

## 📁 Project Structure

```
IT24040-Java-Assignment/
├── README.md
├── images/                      # screenshots
├── task1-jdbc/
│   └── PrimeJDBC.java
└── task2-3-tomcat/
    ├── compile.bat
    ├── src/
    │   ├── HelloServlet.java
    │   └── PrimeServlet.java
    └── myapp/                   # copied into Tomcat's webapps folder
        ├── index.html
        └── WEB-INF/classes/     # compiled .class files go here
```

---

## ✅ Task 1: JDBC, insert the nth prime into a database

The program asks for `n`, calculates the nth prime number, inserts it into an H2 database table (`primes`), and prints the table contents.

**How to run:**

1. Download the [H2 jar](https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar) and add it to the project as a library.
2. Run `PrimeJDBC.java` and enter a number.

**Output:** the 500th prime number is **3571**, inserted into the database and read back from the table.

<p align="center">
  <img src="images/task1-jdbc.png" alt="Task 1 - JDBC output" width="90%">
</p>

#### 📄 Code: PrimeJDBC.java

```java
import java.sql.*;
import java.util.Scanner;

public class PrimeJDBC {

    // H2 embedded database: no installation needed, it creates a file called primesdb.mv.db
    // (To use MySQL instead, see the note in the instructions.)
    static final String URL  = "jdbc:h2:./primesdb";
    static final String USER = "sa";
    static final String PASS = "";

    // Returns the nth prime number (1st = 2, 2nd = 3, 3rd = 5, ...)
    static long nthPrime(int n) {
        int count = 0;
        long candidate = 1;
        while (count < n) {
            candidate++;
            if (isPrime(candidate)) count++;
        }
        return candidate;
    }

    static boolean isPrime(long x) {
        if (x < 2) return false;
        for (long i = 2; i * i <= x; i++) {
            if (x % i == 0) return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter n (to find the nth prime): ");
        int n = sc.nextInt();

        long prime = nthPrime(n);
        System.out.println("The " + n + "th prime number is " + prime);

        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.println("Connected to database.");

            // 1. create table if it does not exist
            try (Statement st = con.createStatement()) {
                st.execute("CREATE TABLE IF NOT EXISTS primes (n INT PRIMARY KEY, prime_value BIGINT)");
            }

            // 2. insert (MERGE = insert, or update if n already exists)
            try (PreparedStatement ps = con.prepareStatement(
                    "MERGE INTO primes (n, prime_value) KEY(n) VALUES (?, ?)")) {
                ps.setInt(1, n);
                ps.setLong(2, prime);
                ps.executeUpdate();
                System.out.println("Inserted into database: n=" + n + ", prime=" + prime);
            }

            // 3. read the table back to prove it worked
            System.out.println("\nContents of table 'primes':");
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT n, prime_value FROM primes ORDER BY n")) {
                while (rs.next()) {
                    System.out.println("  n = " + rs.getInt("n") + "  ->  prime = " + rs.getLong("prime_value"));
                }
            }
        }
    }
}
```


---

## ✅ Task 2: Apache Tomcat and Hello World servlet

Tomcat 11 is installed, and the `myapp` web application is deployed in its `webapps` folder. The servlet is reachable at:

```
http://localhost:8080/myapp/hello
```

<p align="center">
  <img src="images/task2-hello-servlet.png" alt="Task 2 - Hello World servlet" width="90%">
</p>

#### 📄 Code: HelloServlet.java

```java
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        out.println("<h1>Hello, World!</h1>");
        out.println("<p>My first servlet is running on Apache Tomcat.</p>");
        out.println("</body></html>");
    }
}
```


---

## ✅ Task 3: Check primality from the browser using a servlet

The servlet shows a form, reads the number entered by the user, and displays whether it is prime. It is reachable at:

```
http://localhost:8080/myapp/prime
```

You can also pass the number directly in the address, for example `http://localhost:8080/myapp/prime?number=97`.

**Output:** 97 is reported as a **PRIME** number.

<p align="center">
  <img src="images/task3-prime-checker.png" alt="Task 3 - Prime checker servlet" width="90%">
</p>

#### 📄 Code: PrimeServlet.java

```java
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
```


---

---

<p align="center"><b>Dipu Mondol · IT24040</b></p>
