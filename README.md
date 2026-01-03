# File Manager Application

A full-stack file management system that allows users to upload, organize, search, and manage files through a clean web interface. This project was built as a capstone to demonstrate practical full-stack engineering, REST API design, and production-oriented architecture.

## Overview

The File Manager application provides core functionality similar to a lightweight cloud storage system. Users can upload files, organize them into folders, and retrieve or search file metadata. The backend handles persistence and file storage, while the frontend focuses on usability and responsiveness.

This project emphasizes clean architecture, clear separation of concerns, and real-world engineering patterns.

## Tech Stack

### Frontend
- React
- TypeScript
- Vite
- HTML5
- CSS3

### Backend
- Java
- Spring Boot
- RESTful APIs
- JPA / Hibernate
- SQLite

### Tooling & Infrastructure
- Docker
- Maven
- Git / GitHub

## Core Features

- File upload and storage
- Folder-based organization
- File metadata persistence (name, folder, timestamps)
- Search files by name
- REST API for file operations
- Responsive web interface
- Container-ready backend

## Architecture
Frontend (React + TypeScript)
|
| REST API
|
Backend (Spring Boot)
|
|
SQLite Database
|
Local File Storage

The frontend communicates with the backend via RESTful endpoints. File metadata is stored in a relational database while file contents are persisted on disk. The architecture is designed to be easily extended to cloud storage and authentication.

## API Endpoints

- `POST /files/upload` – Upload a file
- `GET /files` – Retrieve all files
- `GET /files?name=` – Search files by name
- `POST /folders` – Create a folder
- `GET /folders` – Retrieve all folders

## Running the Application Locally

### Prerequisites
- Node.js (v18+)
- Java 17+
- Docker (optional)

### Backend

```
cd backend
./mvnw spring-boot:run

Or using Docker:

docker build -t filemanager-backend .
docker run -p 8080:8080 filemanager-backend

It will be available at localhost:8080
```

### Frontend
cd frontend
npm install
npm run dev
```
frontend will run at localhost:5173
```

What This Project Demonstrates
- Full-stack development with React and Spring Boot
- REST API design and consumption
- File upload handling and persistence
- Relational database modeling
- Containerization with Docker
- Writing maintainable, production-oriented code
- Future Improvements
- User authentication and authorization
- Role-based access control
- Cloud storage integration
- Pagination and sorting
- Drag-and-drop file uploads
- Enhanced error handling and logging


Author<br>
Hans Francisco<br>
Software Engineer / Web Developer


