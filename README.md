Full-Stack Library Management System

This project is a complete library management application featuring a Spring Boot backend API and a Next.js frontend client. It demonstrates secure user authentication, role-based access control, and transactional database operations.

Tech Stack

Backend (Server):


- Java Spring Boot

- Spring Security

- MySQL

- Flyway

- JPA / Hibernate

Frontend (Client):

- Next.js

- Tailwind CSS

- Context API

Key Features 

Secure Authentication: Implements a complete flow with BCrypt password hashing, secure HTTP sessions, and CORS configuration to allow cross-origin credential sharing.

Role-Based Access Control (RBAC):

Public: Anyone can browse the book catalog.

User: Only logged-in users can reserve books.

Admin: Only administrators can view full reservation logs.

Transactional Integrity: The ReservationService uses @Transactional to ensure that book inventory counts and reservation records always stay in sync, even in the event of failures.

Quick Start Guide

Follow these steps to run the full stack locally.

Step 1: Start the Backend Server

The server must be running first. It handles all data and authentication.

Wait until you see Started LibraryManagementSystemBackendApplication in the logs. The server is now running at http://localhost:8080.

Step 2: Start the Frontend Client

Clone the frontend repository

git clone https://github.com/udokaokoye/library-managment-system-client frontend-client

cd frontend-client

Install dependencies and start the dev server:

npm install

npm run dev
