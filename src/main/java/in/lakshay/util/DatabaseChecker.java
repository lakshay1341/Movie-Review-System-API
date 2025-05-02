package in.lakshay.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

// runs at startup to check if db is properly setup
@Component
public class DatabaseChecker implements CommandLineRunner {

    @Autowired // todo: switch to constructor injection someday
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // print all tables first
        System.out.println("\n\n=== DATABASE TABLES ===");
        List<Map<String, Object>> tables = jdbcTemplate.queryForList("SHOW TABLES");
        tables.forEach(table -> {
            System.out.println(table.values().iterator().next());
        });

        // check roles
        System.out.println("\n=== ROLES TABLE ===");
        try {
            List<Map<String, Object>> roles = jdbcTemplate.queryForList("SELECT * FROM roles");
            if (roles.isEmpty()) {
                System.out.println("No roles found in the database");
            } else {
                roles.forEach(role -> System.out.println(role));
            }
        } catch (Exception e) {
            System.out.println("Error querying roles table: " + e.getMessage());
        }

        // check users
        System.out.println("\n=== USERS TABLE ===");
        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT * FROM users");
            if (users.isEmpty()) {
                System.out.println("No users found in the database");
            } else {
                users.forEach(user -> System.out.println(user));
            }
        } catch (Exception e) {
            System.out.println("Error querying users table: " + e.getMessage());
        }

        // check movies - just print count cuz there might be lots
        System.out.println("\n=== MOVIES TABLE ===");
        try {
            List<Map<String, Object>> movies = jdbcTemplate.queryForList("SELECT * FROM movies");
            if (movies.isEmpty()) {
                System.out.println("No movies found in the database");
            } else {
                System.out.println("Found " + movies.size() + " movies");
            }
        } catch (Exception e) {
            System.out.println("Error querying movies table: " + e.getMessage());
        }

        // check theaters
        System.out.println("\n=== THEATERS TABLE ===");
        try {
            List<Map<String, Object>> theaters = jdbcTemplate.queryForList("SELECT * FROM theaters");
            if (theaters.isEmpty()) {
                System.out.println("No theaters found in the database");
            } else {
                theaters.forEach(theater -> System.out.println(theater));
            }
        } catch (Exception e) {
            System.out.println("Error querying theaters table: " + e.getMessage());
        }

        // check showtimes - just count
        System.out.println("\n=== SHOWTIMES TABLE ===");
        try {
            List<Map<String, Object>> showtimes = jdbcTemplate.queryForList("SELECT * FROM showtimes");
            if (showtimes.isEmpty()) {
                System.out.println("No showtimes found in the database");
            } else {
                System.out.println("Found " + showtimes.size() + " showtimes");
            }
        } catch (Exception e) {
            System.out.println("Error querying showtimes table: " + e.getMessage());
        }

        // all done!
        System.out.println("\n=== DATABASE CHECK COMPLETE ===\n\n");
    }
}
