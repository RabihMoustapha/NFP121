# Media Library System

A comprehensive Java-based media management system for educational institutions, implementing multiple design patterns and GUI interfaces.

## Overview

This Media Library System is designed to manage educational resources for a university, supporting various media types including documents, videos, and quizzes. The system provides separate interfaces for administrators and students with different access levels and functionalities.

## Design Patterns Implemented

### 1. **Factory Pattern**
- **MediaFactory** - Abstract factory interface
- **DocumentMediaFactory** - Creates document media instances
- **VideoFactory** - Creates video session instances
- **QuizFactory** - Creates online quiz instances
- **MediaFactoryRegistry** - Manages factory registrations

### 2. **Filter Pattern**
- **FilterCriteria** - Interface for filter criteria
- **FilterComposite** - Combines multiple filters with logical operators
- **AuthorFilter** - Filters by author
- **SubjectFilter** - Filters by subject
- **TitleFilter** - Filters by title

### 3. **Observer Pattern**
- **Observable** - Subject interface for observer pattern
- **Observer** - Observer interface for notifications

### 4. **Strategy Pattern**
- **Exporter** - Interface for export strategies
- **CSVExporter** - Exports data to CSV format
- **XMLExporter** - Exports data to XML format

## Features

### Administrative Features
- User management (add/remove students)
- Media library management
- Statistics and reporting
- Data export functionality
- System monitoring

### Student Features
- Browse available media
- Search and filter resources
- Access learning materials
- Track accessed content
- Quiz participation

## Media Types

1. **DocumentMedia** - Text-based educational materials
2. **VideoSession** - Video content with playback tracking
3. **OnlineQuiz** - Interactive assessments with scoring

## User Interfaces

### Login Interfaces
- **AdminLoginFrame** - Administrator authentication
- **StudentLoginFrame** - Student authentication

### Main Interfaces
- **AdminMainFrame** - Administrative dashboard with full control
- **StudentMainFrame** - Student portal for media access

### Management Interfaces
- **NewStudentFrame** - Student registration interface

## Reporting System

1. **StatisticsReport** - General usage statistics
2. **MostAccessedBySpecialtyReport** - Specialty-based access analytics

## Data Management

- **MediaLibrary** - Central repository for all media
- **UniversityXMLManager** - XML-based data persistence
- **Administrator** - Admin user management
- **Student** - Student user model

## Additional Components

- **Specialty** - Student specialization tracking
- **Subject** - Course subject management
- **Demo** - Main application entry point

## Technical Details

### File Structure
- `.java` files - Source code
- `.ctxt` files - BlueJ context files
- `.class` files - Compiled Java classes (removed in refactor)

### Build & Run
- Developed for BlueJ IDE
- Java-based implementation
- XML data storage

## Usage

1. Launch the application via `Demo.java`
2. Choose admin or student login
3. Admin: Manage system, users, and media
4. Student: Browse, search, and access learning materials

## Design Principles

- **Separation of Concerns** - Clear division between UI, business logic, and data
- **Extensibility** - Easy addition of new media types and features
- **Maintainability** - Clean architecture with design patterns
- **Scalability** - Support for multiple users and media types

This system demonstrates practical application of software design patterns in an educational context, providing a robust platform for managing digital learning resources.
