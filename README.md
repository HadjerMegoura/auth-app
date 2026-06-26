# Full-Stack Authentication & Security System

A lightweight, secure authentication platform featuring a decoupled architecture: a **Spring Boot / Spring Security** backend, an **Angular** frontend styled with **Tailwind CSS**, and a **PostgreSQL** database.

The primary goal of this project was migrating from vulnerable local-storage JWT setups to a production-grade session management workflow using **HttpOnly cookies** to mitigate Cross-Site Scripting (XSS) risks.

---

## 🚀 Features

* **User Lifecycle:** Full user registration, secure credential validation (login), and stateless logout.


* **HttpOnly Token Storage:** Refactored architecture to sign and store JWTs inside secure, encrypted `HttpOnly` cookies, making them completely inaccessible to frontend JavaScript.


* **Endpoint Guarding:** Implemented custom Spring Security filters to protect sensitive API endpoints.


* **Secured Resource REST API:** Built a protected `/api/products` endpoint that restricts data retrieval exclusively to successfully authenticated sessions.



---

## 🔑 Security Workflow

1. **Login:** Angular client sends credentials.


2. **Issue:** Spring Boot validates the user, generates a JWT, and attaches it via a response header:
```http
Set-Cookie: token=YOUR_JWT_TOKEN; Secure; HttpOnly; SameSite=Strict; Path=/;

```


3. **Consumption:** The browser securely persists the cookie and automatically appends it to subsequent request headers (e.g., fetching from the products API).



---

## 🛠️ Tech Stack

* **Backend:** Java, Spring Boot, Spring Security, PostgreSQL, JJWT


* **Frontend:** Angular (Standalone Components), Tailwind CSS, HttpClient (with credentials enabled)



---

## 📝 Core API Endpoints

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `POST` | `/register` | Registers a new user account

 | No |
| `POST` | `/login` | Validates user and drops HttpOnly cookie

 | No |
| `POST` | `/logout` | Clears the session cookie from the browser

 | Yes |
| `GET` | `/products` | Retrieves data from the secured products list

 | **Yes (Valid Cookie)**<br> |
