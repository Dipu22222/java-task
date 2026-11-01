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
