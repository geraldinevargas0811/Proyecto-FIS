# 🏋️ Gym Management System

## 📌 Project Overview

The **Gym Management System** is a full-stack web application designed to optimize and automate the administrative and operational processes of a gym.  
The system centralizes the management of clients, instructors, memberships, payments, training routines, and physical progress tracking through an intuitive and scalable platform.

This project was developed as part of a Software Engineering academic initiative, applying agile methodologies, database modeling, system analysis, and modern web development technologies.

---

# 🚀 Main Features

## 🔐 Authentication & Authorization
- Secure login system using JWT authentication.
- Role-based access control:
  - Administrator
  - Instructor
  - Client

## 👤 Client Management
- Client registration and profile management.
- Storage of personal and physical information.
- Automatic BMI (Body Mass Index) calculation.
- Physical progress tracking and history.

## 🏋️ Training Routine Management
- Automatic routine generation based on:
  - Training goals
  - Physical data
  - Training frequency
- Routine history management.
- PDF routine download support.

## 💳 Membership & Payment Management
- Membership registration and tracking.
- Payment history management.
- Automatic membership status validation.

## 👨‍🏫 Instructor Management
- Instructor registration and assignment.
- Client consultation for assigned instructors.
- Instructor schedules and specialties management.

## 📊 Administrative Features
- User administration panel.
- Client and instructor management.
- User activation/deactivation.
- General system monitoring.

---

# 🛠️ Technologies Used

## Frontend
- React
- Vite
- JavaScript
- Axios
- React Router

## Backend
- Spring Boot
- Java
- Maven
- JWT Authentication

## Database
- PostgreSQL

## Development Methodology
- Extreme Programming (XP)

---

# 📂 Repository Structure

```txt
gym-management-system/
│
├── README.md
│
├── docs/
│   ├── informes/
│   ├── requerimientos/
│   ├── diagramas/
│   └── capturas/
│
├── frontend/
│
├── backend/
│
├── database/
│
└── resources/
```

---

# 📁 Folder Description

## `/docs`
Contains all project documentation.

### `/docs/informes`
- Main project report
- Design manual
- User manual
- Programmer manual

### `/docs/requerimientos`
- User stories
- Requirements gathering
- Functional and non-functional requirements

### `/docs/diagramas`
- UML diagrams
- Use case diagrams
- Sequence diagrams
- Entity-relationship diagrams
- Gantt charts

### `/docs/capturas`
Contains screenshots of the system interface.

---

# ⚙️ Installation & Execution

## 1️⃣ Clone the repository

```bash
git clone https://github.com/your-username/gym-management-system.git
```

---

## 2️⃣ Backend Setup

```bash
cd backend
```

Install dependencies and run the Spring Boot application.

---

## 3️⃣ Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

---

## 4️⃣ Database Setup

1. Create a PostgreSQL database.
2. Execute the SQL scripts located in:

```txt
/database
```

---

# 🧠 System Roles

## 👑 Administrator
- Manage users
- Manage instructors
- Monitor memberships and payments
- Assign instructors

## 🏋️ Instructor
- View assigned clients
- Review routines and client progress
- Provide guidance

## 👤 Client
- Manage profile
- View routines
- Track physical progress
- Review membership status

---

# 📈 Development Methodology

This project follows the **Extreme Programming (XP)** agile methodology.

XP emphasizes:
- Continuous feedback
- Incremental development
- Simplicity
- Frequent releases
- Code quality
- Adaptability to changing requirements

The methodology was selected to facilitate iterative development and continuous improvement throughout the project lifecycle.

---

# 🔒 Non-Functional Requirements

- Secure password encryption
- Role-based authorization
- Responsive and user-friendly interface
- Data consistency and integrity
- Scalable architecture
- Local development environment support

---

# 📸 System Screenshots

> Screenshots of the application interface will be added in the `/docs/capturas` folder.

---

# 🎯 Project Scope

The project focuses on the development of an academic web application for gym management.  
It does not include:
- Real payment gateway integration
- Mobile applications
- Biometric device integration
- Production deployment

---

# 👨‍💻 Authors

- Geraldine Alejandra Vargas Moreno
- Jhon Jairo O'Meara Muñoz

---

# 📄 License

This project is licensed under the MIT License.

---

# ⭐ Academic Purpose

This repository was created for educational and academic purposes as part of the Software Engineering course project.


## License
This project is licensed under the MIT License.
