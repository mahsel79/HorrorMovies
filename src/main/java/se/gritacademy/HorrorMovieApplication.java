package se.gritacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
@RequestMapping("/movies")
@CrossOrigin(origins = "*")
public class HorrorMovieApplication {

    private final DataSource dataSource;

    public HorrorMovieApplication(DataSource dataSource) {
        this.dataSource = dataSource;
        initializeDatabase();
    }

    public static void main(String[] args) {
        SpringApplication.run(HorrorMovieApplication.class, args);
    }

    private void initializeDatabase() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS horror_movies (id INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255), release_year INT, director VARCHAR(255), poster VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS reviews (id INT AUTO_INCREMENT PRIMARY KEY, movie_id INT, review TEXT, FOREIGN KEY(movie_id) REFERENCES horror_movies(id))");
            stmt.execute("INSERT INTO horror_movies (title, release_year, director, poster) VALUES ('The Exorcist', 1973, 'William Friedkin', 'exorcist.jpg'), ('Halloween', 1978, 'John Carpenter', 'halloween.jpg'), ('A Nightmare on Elm Street', 1984, 'Wes Craven', 'elm_street.jpg'), ('The Shining', 1980, 'Stanley Kubrick', 'shining.jpg')");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SQL Injection Vulnerability
    @GetMapping
    public List<Map<String, Object>> getMovies(@RequestParam(value = "search", required = false) String search) {
        List<Map<String, Object>> movies = new ArrayList<>();
        String query = "SELECT * FROM horror_movies";
        if (search != null && !search.isEmpty()) {
            query += " WHERE title LIKE '%" + search + "%'"; // Vulnerable to SQL Injection
        }
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> movie = new HashMap<>();
                movie.put("id", rs.getInt("id"));
                movie.put("title", rs.getString("title"));
                movie.put("release_year", rs.getInt("release_year"));
                movie.put("director", rs.getString("director"));
                movie.put("poster", rs.getString("poster"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    // Cross-Site Scripting (XSS) Vulnerability
    @PostMapping("/review")
    public ResponseEntity<String> addReview(@RequestParam int movieId, @RequestParam String review) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO reviews (movie_id, review) VALUES (?, ?)");) {
            stmt.setInt(1, movieId);
            stmt.setString(2, review); // No sanitization, allows XSS
            stmt.executeUpdate();
            return ResponseEntity.ok("Review added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error adding review");
        }
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<String>> getReviews(@RequestParam int movieId) {
        List<String> reviews = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT review FROM reviews WHERE movie_id = ?")) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(rs.getString("review")); // Outputs user input directly, causing XSS
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok(reviews);
    }

    // Directory Traversal Vulnerability
    @GetMapping("/poster")
    public ResponseEntity<byte[]> getMoviePoster(@RequestParam String filename) {
        try {
            Path path = Paths.get("posters/" + filename); // No validation of file path
            byte[] image = Files.readAllBytes(path);
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("image/jpeg"))
                    .body(image);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
