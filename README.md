# 🌱 Charifit

A full-stack donation and volunteer management platform that connects donors with meaningful causes, streamlines volunteer sign-ups, and automatically generates donation certificates.

🔗 **Live Demo:** [https://charifit-project.onrender.com](https://charifit-project.onrender.com)

> ⚠️ Hosted on a free tier — the app may take 30–60 seconds to load if it's been inactive.

---

## ✨ Features

- 🔐 **User Authentication** — Register and log in securely
- 💚 **Browse Causes** — View active causes and their funding progress
- 💳 **Donations** — Contribute to causes and track donation history
- 📜 **Auto-Generated Certificates** — Downloadable PDF certificates for every donation
- 🙋 **Volunteer Sign-Up** — Register interest in volunteering
- ✉️ **Contact Form** — Reach out with queries or feedback
- 🛠️ **Admin Dashboard** — Manage causes, donations, volunteers, and contact queries

---

## 🧰 Tech Stack

| Layer          | Technology                              |
|----------------|-------------------------------------------|
| Backend        | Java, Servlets, Embedded Apache Tomcat    |
| Frontend       | HTML, CSS, JavaScript, Bootstrap 5        |
| Database       | MySQL                                     |
| PDF Generation | iText                                     |
| Build Tool     | Maven                                     |
| Deployment     | Docker, Render (app), Railway (database)  |

---

## 🚀 Running Locally

### Prerequisites
- Java 17+
- Maven
- MySQL (or use a hosted instance)
- Docker *(optional, for containerized run)*

### Option 1 — Run with Maven
```bash
git clone https://github.com/Kadambari1702/Charifit-Project.git
cd Charifit-Project
mvn compile
mvn exec:java -Dexec.mainClass="com.org.server.WebServer"
```
App runs at `http://localhost:8080`

### Option 2 — Run with Docker
```bash
docker build -t charifit .
docker run -p 8080:8080 \
  -e DB_HOST=your_db_host \
  -e DB_PORT=3306 \
  -e DB_NAME=your_db_name \
  -e DB_USER=your_db_user \
  -e DB_PASSWORD=your_db_password \
  charifit
```

### Environment Variables
| Variable      | Description                          |
|---------------|----------------------------------------|
| `DB_HOST`     | MySQL host                             |
| `DB_PORT`     | MySQL port (default `3306`)            |
| `DB_NAME`     | Database name                          |
| `DB_USER`     | Database username                      |
| `DB_PASSWORD` | Database password                      |
| `PORT`        | Server port (auto-set by most hosts)   |

---

## 📁 Project Structure
```
Charifit-Project/
├── Dockerfile
├── pom.xml
├── src/main/java/com/org/
│   ├── Cause.java, Donation.java, User.java, Volunteer.java, ContactQuery.java   → Model classes
│   ├── dao/            → Database access layer (CauseDAO, DonationDAO, UserDAO, VolunteerDAO, ContactQueryDAO, DBConnection)
│   ├── servlet/         → API endpoints
│   │   ├── AuthServlet.java          → /api/auth/* (login, session status)
│   │   ├── RegisterServlet.java      → /register
│   │   ├── CauseServlet.java         → /api/causes
│   │   ├── DonationServlet.java      → /api/donations
│   │   ├── VolunteerServlet.java     → /api/volunteers
│   │   ├── ContactServlet.java       → /api/contacts
│   │   ├── ProfileServlet.java       → /api/profile
│   │   └── CertificateServlet.java   → /certificate (PDF generation)
│   ├── server/
│   │   └── WebServer.java   → Embedded Tomcat setup & servlet registration
│   └── listener/
│       └── AppContextListener.java
└── src/main/webapp/
    ├── index.html, about.html, causes.html, donate.html, contact.html, register.html, login.html,
    │   user-login.html, my-donations.html, profile.html, admin.html
    ├── css/style.css
    ├── js/
    │   ├── app.js, auth.js, donate.js, admin.js, my-donations.js
    └── images/logo.png, signature.png
```

## 👩‍💻 Author

**Kadambari Kale**
📧 kalekadambari03@gmail.com
🔗 [GitHub](https://github.com/Kadambari1702)
💼 [LinkedIn](https://www.linkedin.com/in/kadambari-k-75a519259/)

## 📄 License
© 2026 Kadambari Kale. All rights reserved. This project was built for educational and portfolio purposes and is not licensed for commercial use.
