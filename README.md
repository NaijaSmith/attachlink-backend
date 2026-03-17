AttachLink 🔗

The Digital Bridge for Seamless Internship Management

AttachLink is a digital solution developed to modernize the industrial attachment monitoring process at KCA University. By replacing manual paper logbooks with an authenticated, real-time mobile and web platform, it creates a transparent, automated reporting workflow between Students, Supervisors, and Employers.

🚀 Core Features

🎓 For Students

Digital Logbook: Submit daily/weekly activities with photo/document evidence.

Progress Analytics: Visualize approval stages using interactive MPAndroidChart pie charts.

Automated Reports: Export official attachment reports as PDF documents directly from the app.

Offline Capability: Logs can be drafted during internet downtime to ensure no data is lost.

Live Updates: Receive instant Firebase (FCM) notifications when logs are reviewed.

🔍 For Supervisors & Employers

Supervisor Review Portal: A dedicated workflow for faculty supervisors to approve or reject logs with structured feedback.

Employer Evaluations: On-site managers can monitor daily progress and provide 5-star performance ratings.

Institutional Dashboard: Centralized access for coordinators to monitor progress and generate institutional reports.

Authenticated Submissions: Ensures all entries are verified, timestamped, and tamper-proof.

🛠 Technical Stack

Layer

Technology

Mobile Frontend

Android (Java), Retrofit 2, MPAndroidChart

Real-time Services

Firebase Cloud Messaging (FCM)

Backend API

Spring Boot (Java), JPA/Hibernate

Security

JWT (JSON Web Tokens), HTTPS

Database

MySQL / PostgreSQL

DevOps

Docker, Docker Compose

📦 Setup & Installation

1. Mobile App Setup (Android Studio / VS Code)

Clone the repository:

git clone [https://github.com/your-username/attachlink-android.git](https://github.com/your-username/attachlink-android.git)


Add your google-services.json to the app/ folder.

Update the BASE_URL in RetrofitClient.java to point to your hosted backend URL.

2. Backend Setup (Traditional)

Ensure you have Java 17 and MySQL installed.

Configure your database credentials in src/main/resources/application.properties.

Build and run:

./mvnw clean spring-boot:run


3. Docker Deployment (Recommended for Cloud)

To deploy the backend and database seamlessly using Docker:

Build the Image:

docker build -t attachlink-backend .


Run with Docker Compose:
Create a docker-compose.yml in your root directory and run:

docker-compose up -d


This will spin up both the Spring Boot API and a managed database instance automatically.

🛡️ Risk Management & Integrity

Data Integrity: Regular backups are maintained via GitHub to prevent code and configuration loss.

Connectivity: The system is designed with offline work capability to allow students to log activities without a constant internet connection.

Security: JWT implementation ensures that only authorized users can modify logbook entries.

📄 License

Copyright © 2026 Nicholas Kariuki Wambui

Institution: KCA University

Licensed under the Apache License, Version 2.0. See http://www.apache.org/licenses/LICENSE-2.0 for details.