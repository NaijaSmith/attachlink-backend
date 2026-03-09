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

Institutional Dashboard: Centralized access for coordinators to monitor overall progress and generate institutional reports.

Authenticated Submissions: Ensures all entries are verified, timestamped, and tamper-proof.

🏗️ Project Scope

✅ In Scope

Android application for mobile student logging.

Web-based dashboard for supervisors and institutional coordinators.

Digital submission and archival of performance reports.

Real-time tracking to eliminate delays inherent in handwritten reports.

❌ Out of Scope

Automated placement or matching with companies.

Integration with non-academic professional networking platforms.

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

JWT (JSON Web Tokens), Network Security Configuration (HTTPS)

Database

MySQL

Methodology

Agile (Iterative Development)

Development Environments

Android Studio (Mobile) & VS Code/IntelliJ (Spring Boot Backend)

📦 Setup & Installation

1. Mobile App Setup

Clone the repository:

git clone [https://github.com/your-username/attachlink-android.git](https://github.com/your-username/attachlink-android.git)


Add your google-services.json to the app/ folder.

Update the BASE_URL in RetrofitClient.java to point to your hosted backend.

2. Backend Setup

Ensure you have the AttachLink Spring Boot server running.

Configure your MySQL database credentials in application.properties.

Build and run using ./mvnw spring-boot:run.

🛡️ Risk Management & Integrity

Data Integrity: Regular backups are maintained via GitHub to prevent code and configuration loss.

Connectivity: The system is designed with offline work capability to allow students to log activities without a constant internet connection.

📄 License

Copyright 2026 Nicholas Kariuki Wambui Institution: KCA University

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
