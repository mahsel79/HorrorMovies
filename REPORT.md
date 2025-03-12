**IT Security for Java Developers - Vulnerability Analysis Report**  
**Course: JAVA24 - Task 1**

---  

## **1. SQL Injection Vulnerability**
### **Location in Code:**
- Found in `HorrorMovieApplication.java` inside the `getMovies` method.

```java
if (search != null && !search.isEmpty()) {
    query += " WHERE title LIKE '%" + search + "%'"; // Vulnerable to SQL Injection
}
```

### **OWASP Top 10 Classification:** A03:2021 - Injection
### **CWE Identifier:** CWE-89: SQL Injection

### **Exploitation Example:**
Using cURL, an attacker can manipulate the query to extract all database entries:
```sh
curl "http://localhost:8080/movies?search=' OR '1'='1"
```
This will return all movies regardless of the intended search criteria.

### **Impact:**
- Attackers can execute arbitrary SQL queries.
- Data leakage, modification, or deletion.
- Potential access to sensitive information.

### **Mitigation:**
- Use **prepared statements** with parameterized queries:
```java
String query = "SELECT * FROM horror_movies WHERE title LIKE ?";
PreparedStatement stmt = conn.prepareStatement(query);
stmt.setString(1, "%" + search + "%");
```

---

## **2. Cross-Site Scripting (XSS) Vulnerability**
### **Location in Code:**
- Found in `HorrorMovieApplication.java` inside the `addReview` and `getReviews` methods.

```java
stmt.setString(2, review); // No sanitization, allows XSS
reviews.add(rs.getString("review")); // Outputs user input directly
```

### **OWASP Top 10 Classification:** A07:2021 - Cross-Site Scripting (XSS)
### **CWE Identifier:** CWE-79: Improper Neutralization of Input

### **Exploitation Example:**
A user can inject malicious JavaScript code in a review:
```sh
curl -X POST "http://localhost:8080/movies/review" -d "movieId=1&review=<script>alert('XSS Attack!')</script>"
```
When another user views the reviews, the script will execute.

### **Impact:**
- Stealing user session cookies.
- Defacing web pages.
- Executing malicious actions on behalf of a user.

### **Mitigation:**
- Encode user input before displaying it:
```java
import org.apache.commons.text.StringEscapeUtils;
String safeReview = StringEscapeUtils.escapeHtml4(review);
```

---

## **3. Directory Traversal Vulnerability**
### **Location in Code:**
- Found in `HorrorMovieApplication.java` inside the `getMoviePoster` method.

```java
Path path = Paths.get("posters/" + filename); // No validation of file path
```

### **OWASP Top 10 Classification:** A05:2021 - Security Misconfiguration
### **CWE Identifier:** CWE-22: Improper Limitation of a Pathname

### **Exploitation Example:**
An attacker can access sensitive files outside the `posters` folder:
```sh
curl "http://localhost:8080/movies/poster?filename=../../etc/passwd"
```
This allows retrieval of system files.

### **Impact:**
- Exposure of sensitive system files.
- Compromising application configurations.
- Potential remote code execution if used with other exploits.

### **Mitigation:**
- Restrict access to files within a specific directory:
```java
if (filename.contains("..")) {
    return ResponseEntity.badRequest().build();
}
```

---
