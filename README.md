# Media Library System 🎓

![Java](https://img.shields.io/badge/Java-17-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Design Patterns](https://img.shields.io/badge/patterns-factory%20|%20observer%20|%20strategy%20|%20filter-orange)
![Status](https://img.shields.io/badge/status-complete-brightgreen)

A comprehensive Java-based media management system implementing core design patterns for educational resource management in university environments.

## 📋 Table of Contents
- [Overview](#-overview)
- [✨ Key Features](#-key-features)
- [🏗️ Architecture & Design Patterns](#️-architecture--design-patterns)
- [📁 Project Structure](#-project-structure)
- [🚀 Getting Started](#-getting-started)
- [👥 User Roles](#-user-roles)
- [💾 Data Management](#-data-management)
- [📊 Reporting](#-reporting)

## 🎯 Overview

A Java desktop application for managing educational media resources in university settings. The system supports multiple media types (documents, videos, quizzes) with separate interfaces for administrators and students, implementing fundamental software design patterns for maintainability and extensibility.

**Primary Objectives:**
- Manage diverse educational media types
- Implement role-based access control
- Demonstrate practical design pattern applications
- Provide robust filtering and reporting capabilities

## ✨ Key Features

### **Media Management**
- Support for **DocumentMedia**, **VideoSession**, and **OnlineQuiz** types
- Factory-based media creation and registration
- Advanced filtering by author, subject, title with composite filters
- Export functionality to CSV and XML formats

### **User Management**
- Dual-role system: **Administrator** and **Student**
- Separate authentication interfaces
- Specialty-based student organization
- Usage tracking and analytics

### **System Features**
- Observer pattern for notification systems
- Strategy pattern for export functionality
- XML-based data persistence
- Comprehensive reporting dashboard

## 🏗️ Architecture & Design Patterns

### **Factory Pattern** (`MediaFactory` Hierarchy)
- **`MediaFactory`** - Abstract factory interface
- **`DocumentMediaFactory`** - Document instance creation
- **`VideoFactory`** - Video session creation  
- **`QuizFactory`** - Quiz instance creation
- **`MediaFactoryRegistry`** - Centralized factory management

### **Filter Pattern** (`FilterCriteria` Hierarchy)
- **`FilterCriteria`** - Filter interface contract
- **`FilterComposite`** - Logical filter combinations (AND/OR)
- **`AuthorFilter`** - Author-based filtering
- **`SubjectFilter`** - Subject-based filtering
- **`TitleFilter`** - Title-based filtering

### **Observer Pattern**
- **`Observable`** - Subject interface for state changes
- **`Observer`** - Observer interface for notifications
- Event-driven updates for system changes

### **Strategy Pattern**
- **`Exporter`** - Export strategy interface
- **`CSVExporter`** - CSV format export strategy
- **`XMLExporter`** - XML format export strategy

## 📁 Project Structure

```
src/
├── Models/
│   ├── Media/                     # Media type implementations
│   │   ├── DocumentMedia.java
│   │   ├── VideoSession.java
│   │   └── OnlineQuiz.java
│   ├── Factories/                 # Factory pattern implementations
│   │   ├── MediaFactory.java
│   │   ├── DocumentMediaFactory.java
│   │   ├── VideoFactory.java
│   │   ├── QuizFactory.java
│   │   └── MediaFactoryRegistry.java
│   ├── Filters/                   # Filter pattern implementations
│   │   ├── FilterCriteria.java
│   │   ├── FilterComposite.java
│   │   ├── AuthorFilter.java
│   │   ├── SubjectFilter.java
│   │   └── TitleFilter.java
│   ├── Patterns/                  # Additional patterns
│   │   ├── Observable.java
│   │   ├── Observer.java
│   │   ├── Exporter.java
│   │   ├── CSVExporter.java
│   │   └── XMLExporter.java
│   ├── Users/                     # User management
│   │   ├── Administrator.java
│   │   └── Student.java
│   └── Core/                      # Core system components
│       ├── MediaLibrary.java
│       ├── UniversityXMLManager.java
│       ├── Specialty.java
│       └── Subject.java
├── UI/                            # User interface classes
│   ├── AdminLoginFrame.java
│   ├── StudentLoginFrame.java
│   ├── AdminMainFrame.java
│   ├── StudentMainFrame.java
│   └── NewStudentFrame.java
├── Reports/                       # Reporting system
│   ├── StatisticsReport.java
│   └── MostAccessedBySpecialtyReport.java
└── Demo.java                      # Application entry point
```

## 🚀 Getting Started

### **Prerequisites**
- Java JDK 17 or higher
- BlueJ IDE (recommended) or any Java IDE
- Basic understanding of design patterns

### **Compilation & Execution**
```bash
# Compile all Java files
javac -d bin src/**/*.java

# Run the application
java -cp bin Demo
```

### **BlueJ Specific**
1. Open the project folder in BlueJ
2. Compile all classes using the "Compile" button
3. Right-click `Demo` class and select "void main(String[] args)"
4. Follow the login prompts

## 👥 User Roles

### **Administrator**
- **Access**: Full system control
- **Features**:
  - User management (add/remove students)
  - Media library management
  - System statistics and reporting
  - Data export functionality
  - System monitoring
- **Interface**: `AdminMainFrame`

### **Student**  
- **Access**: Media consumption only
- **Features**:
  - Browse available media resources
  - Search and filter functionality
  - Access learning materials
  - Track personal access history
  - Participate in online quizzes
- **Interface**: `StudentMainFrame`

## 💾 Data Management

### **Persistence Layer**
- **`UniversityXMLManager`** - Handles all data persistence
- XML-based storage for users, media, and access logs
- Configurable file paths and backup systems

### **Media Library Core**
- **`MediaLibrary`** - Central repository singleton
- Manages all media instances and metadata
- Provides search and retrieval operations
- Tracks access statistics and usage patterns

## 📊 Reporting System

### **Available Reports**
1. **`StatisticsReport`** - General system usage statistics
   - Total media count by type
   - User engagement metrics
   - Access frequency trends

2. **`MostAccessedBySpecialtyReport`** - Specialty-based analytics
   - Media popularity across specialties
   - Student engagement patterns
   - Resource effectiveness metrics

### **Export Options**
- **CSV Export** - Spreadsheet-compatible format via `CSVExporter`
- **XML Export** - Structured data format via `XMLExporter`
- Custom export strategies easily extendable

## 🎨 Design Principles

### **Separation of Concerns**
- Clear distinction between UI, business logic, and data layers
- Modular components with single responsibilities
- Interface-based contracts between modules

### **Extensibility**
- Factory pattern enables easy addition of new media types
- Filter system supports custom criteria implementation
- Export strategies pluggable without core modifications

### **Maintainability**
- Consistent design pattern implementations
- Comprehensive documentation in code
- Modular testing capabilities

### **Scalability**
- Support for multiple concurrent users
- Efficient media retrieval algorithms
- Expandable reporting and analytics

## 🔧 Technical Specifications

- **Language**: Java 17+
- **Storage**: XML-based persistence
- **Patterns**: Factory, Observer, Strategy, Filter
- **Interface**: Swing-based GUI
- **Build**: BlueJ project compatible

## 🤝 Contribution

This educational project demonstrates design pattern implementations. For academic or learning purposes:

1. Study the pattern implementations in respective packages
2. Experiment with adding new media types using the factory pattern
3. Extend filter criteria for enhanced search capabilities
4. Implement additional export strategies

## 📄 License

MIT License - see LICENSE file for details.

## 🏫 Educational Context

Developed as a practical demonstration of software design patterns in Java, showcasing real-world application of:
- Creational patterns (Factory)
- Behavioral patterns (Observer, Strategy)
- Structural patterns (Filter as specialized Composite)
- Architectural separation of concerns

---

*This system serves as both a functional media management tool and an educational resource for understanding enterprise Java application design with patterns.*
