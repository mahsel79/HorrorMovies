# Horror Movie Application

The **Horror Movie Application** is a **Java web server** designed to educate developers on **security vulnerabilities in web applications**.  
It intentionally includes **SQL Injection, Cross-Site Scripting (XSS), and Directory Traversal** to demonstrate how attackers exploit these flaws and how to mitigate them.

---

## 📌 Purpose  
This project serves as an **IT security education tool**, helping developers:  
- Understand security risks in Java web applications.  
- Learn how common attacks are performed.  
- Implement proper security measures to prevent them.  

By working with this project, developers gain **hands-on experience** in **ethical hacking** and **secure coding**.  

---

## 🏗️ Technologies Used  

- **Java 17** – Backend implementation  
- **Spring Boot** – Web server framework  
- **H2 Database** – In-memory database for storing movies & reviews  
- **HTML, CSS, JavaScript** – Frontend UI  
- **cURL/Postman** – For API testing  
- **OWASP Top 10** – Security principles  

---

## 📂 Folder and File Structure  

```
HorrorMovieApplication/
│── src/
│   ├── main/
│   │   ├── java/se/gritacademy/
│   │   │   ├── HorrorMovieApplication.java   # Main Spring Boot application
│   │   │   ├── model/
│   │   │   │   ├── Movie.java                # Movie entity class
│   │   ├── resources/
│   │   │   ├── static/
│   │   │   │   ├── posters/                  # Movie poster images
│   │   │   ├── templates/
│   │   │   │   ├── index.html                # Frontend web page
│── README.md                                 # Project documentation

```

---

## 🚀 Building and Running the Application  

### 🛠 1. Clone the Repository  
```sh
git https://github.com/mahsel79/HorrorMovies.git  
cd HorrorMovies 
```

### 🏗 2. Build and Run the Application  
```sh
./mvnw spring-boot:run  
```
**OR** (if using Maven installed on your system)  
```sh
mvn spring-boot:run  
```

### 🌍 3. Access the Application  
- **Frontend:** Open `http://localhost:8080/` in a browser.  
- **API Endpoints:**  

| Endpoint  | HTTP Method | Description |
|-----------|------------|-------------|
| `/movies` | `GET` | Retrieve all movies |
| `/movies?search=title` | `GET` | Search movies by title |
| `/movies/review` | `POST` | Add a review |
| `/movies/reviews?movieId=1` | `GET` | Get reviews for a movie |
| `/movies/poster?filename=poster.jpg` | `GET` | Fetch movie poster |

---

## 🔥 Security Vulnerabilities Demonstrated  

This project **intentionally includes** the following vulnerabilities for educational purposes:

### 1️⃣ SQL Injection  
- **Location:** `HorrorMovieApplication.java` → `getMovies()` method  
- **How to exploit:**  
```sh
curl "http://localhost:8080/movies?search=' OR 1=1 --"
```
- **Effect:** Returns all movies regardless of search input.  

---

### 2️⃣ Cross-Site Scripting (XSS)  
- **Location:** `HorrorMovieApplication.java` → `addReview()` & `getReviews()`  
- **How to exploit:**  
```sh
curl -X POST "http://localhost:8080/movies/review" -d "movieId=1&review=<script>alert('XSS!')</script>"
```
- **Effect:** JavaScript executes when displaying reviews.  

---

### 3️⃣ Directory Traversal  
- **Location:** `HorrorMovieApplication.java` → `getMoviePoster()`  
- **How to exploit:**  
```sh
curl "http://localhost:8080/movies/poster?filename=../../../../etc/passwd"
```
- **Effect:** Can read system files outside the `posters/` directory.  

---

## 📖 Security Report  

For a **detailed explanation of vulnerabilities, exploitation, and fixes**, refer to: REPORT.md
---

## 📌 Next Steps  

- **Test the vulnerabilities** using `cURL` and Postman.  
- **Analyze the security flaws** using the provided report.  
- **Fix the security vulnerabilities** by implementing the recommended mitigations.  

---

### ⚠️ **Disclaimer**  
This project is **for educational purposes only**. Do not deploy or exploit similar vulnerabilities in real-world applications.  

---

