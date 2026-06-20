# Media Library Management System

A comprehensive media library management application built with Java Swing. This system allows students and administrators to manage educational media resources such as documents, video sessions, and online quizzes.

## 📖 Description

The **Media Library** is a desktop application designed for educational institutions. It provides a centralized platform for storing, organizing, and accessing learning materials. The system supports two user roles:

- **Students** can browse, search, filter, and access media content relevant to their subjects and specialty.
- **Administrators** have full CRUD (Create, Read, Update, Delete) capabilities, export data, view statistics, and manage user accounts.

The application uses persistent storage via XML files and includes modern software design patterns for maintainability and scalability.

## ✨ Features

### 🔐 User Management
- Secure login for students and administrators with SHA‑256 password hashing.
- Self‑registration for students (with specialty and subject selection).
- Administrator account creation.

### 📚 Media Management
- Add, edit, delete, and view media items.
- Supported media types:
  - **Document** (with page count)
  - **Video Session** (with duration in minutes)
  - **Online Quiz** (with estimated duration and difficulty level)
- Each media can be associated with multiple subjects.

### 🔍 Search & Filter
- Search by title, author, or ID.
- Advanced filtering using a composite filter pattern (e.g., subject‑based filtering).
- Students can quickly filter media related to their enrolled subjects.

### 📊 Statistics & Reporting
- View total media and student counts.
- Top 5 most‑accessed media items.
- Export media list to **CSV** or **XML** format.

### 📧 Notifications
- Simulated email notification when new media is added, targeting students interested in the associated subjects.

### 💾 Data Persistence
- All data is stored in an `universite.xml` file using DOM parsing and JAXB.
- Automatic loading and saving on application start/stop.

## 🛠️ Technologies Used

- **Java 8+** – Core language
- **Swing** – Graphical user interface
- **JAXB** – XML data binding (for some parts)
- **DOM Parser** – Custom XML read/write
- **SHA‑256** – Password hashing (via `MessageDigest`)
- **Design Patterns**:
  - Factory (`MediaFactory`, `MediaFactoryRegistry`)
  - Composite (`FilterComposite`, `FilterCriteria`)
  - Singleton (`MediaFactoryRegistry`)
  - Strategy (exporters: `CSVExporter`, `XMLExporter`)

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Any IDE (IntelliJ IDEA, Eclipse, NetBeans) or command‑line build tool

### Running the Application

1. **Clone the repository**  
   ```bash
   git clone https://github.com/yourusername/medialibrary.git
   cd medialibrary
   ```

2. **Compile and run**  
   You can run the project directly from your IDE by opening the `com.isae.medialibrary.Main` class.

   Alternatively, if you have a build tool (e.g., Maven), use:
   ```bash
   mvn compile exec:java -Dexec.mainClass="com.isae.medialibrary.Main"
   ```

   For plain javac:
   ```bash
   javac -d bin src/**/*.java
   java -cp bin com.isae.medialibrary.Main
   ```

3. **First run**  
   On first launch, the application will automatically create an `universite.xml` file with default sample data:
   - **Student**: `etudiant1` / `pass123`
   - **Administrator**: `admin` / `admin123`

   You can use these credentials to log in.

### Build and Package (Optional)
If you want to create a JAR file:
```bash
jar cf medialibrary.jar -C bin .
java -jar medialibrary.jar
```

## 📖 Usage

### Student Mode
- Login with your credentials.
- Browse all media or filter by your enrolled subjects.
- View media details and increment access counts.
- You can add, edit, or delete media **only if you are enrolled in at least one of its subjects**.
- Use the **"My Subjects"** button to quickly see media relevant to your courses.

### Administrator Mode
- Login with admin credentials.
- Full control over media: add, edit, delete.
- Export the entire media collection to CSV or XML.
- View statistics (total media, students, top accessed).
- Create new administrator accounts.

## 📁 Project Structure

```
NFP121/
├── com/isae/medialibrary/
│   ├── Main.java                    – Application entry point
│   ├── exception/                   – Custom exceptions
│   ├── model/                       – Domain entities (Media, Student, etc.)
│   ├── persistence/                 – Data persistence and exporters
│   ├── service/                     – Business logic, factories, filters
│   ├── util/                        – Constants, logging, validation
│   └── view/                        – Swing GUI frames and dialogs
└── README.md
```

## 🤝 Contributing

Contributions are welcome! Feel free to open issues or submit pull requests for improvements, bug fixes, or new features.

## 📄 License

This project is intended for educational purposes. You may use and modify it under the terms of the [MIT License](LICENSE).
