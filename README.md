# Apps-aplication-for-Java# Apps-aplication-for-Java — Student Info Manager

A lightweight Java app to **store, update, delete, search** student information in a **JSON file** and **export to PDF**.


---

## ✨ Features 

* Add / Update / Delete / Search student records
* Persist data in a local **JSON** file (no DB required)
* **Export to PDF** (single record or full list)
* Simple CLI/GUI (adapt text below to your version)
* Clean code structure; easy to extend

---

## 🧰 Tech Stack

* **Java** (8+ recommended)
* **JSON** handling: `org.json` / `Gson` / `Jackson` (use the one you used)
* **PDF export**: `iText` / `OpenPDF` / `Apache PDFBox` (the one you used)

> Replace with the exact libraries and versions you used.

---

## 📦 Project Structure (example)

```
JavaApplication11/
├─ src/
│  ├─ main/java/
│  │  ├─ app/App.java
│  │  ├─ model/Student.java
│  │  ├─ service/StudentService.java
│  │  ├─ storage/JsonRepository.java
│  │  └─ export/PdfExporter.java
│  └─ main/resources/
│     └─ students.json   ← data file (auto-created if missing)
├─ lib/                  ← external jars (if not using Maven/Gradle)
├─ README.md
└─ (pom.xml | build.gradle)  ← if using Maven/Gradle
```

---

## 🚀 Getting Started

### Prerequisites

* Java 8+ installed (`java -version`)
* If using Maven: `mvn -v`  | If using Gradle: `gradle -v`

### Clone

```bash
git clone https://github.com/Ahad-19-s/Apps-aplication-for-Java.git
cd Apps-aplication-for-Java
```

### Build & Run

**Option A — Maven**

```bash
mvn clean package
java -jar target/student-app.jar
```

**Option B — Gradle**

```bash
gradle build
java -jar build/libs/student-app.jar
```

**Option C — Plain Java + JARs**

```bash
javac -cp "lib/*" -d out src/main/java/**/**/*.java
java -cp "out;lib/*" app.App  # Windows
# or
java -cp "out:lib/*" app.App  # Linux/Mac
```

---

## 🔧 Configuration

* **Data file path:** `src/main/resources/students.json` (or configurable via `config.properties` / environment variable).
* **PDF output folder:** `exports/` (auto-created).



---

## 🗂️ Data Model (JSON Schema — example)

```json
{
  "id": "S-0001",
  "name": "Ahad Ahmed",
  "department": "CSE",
  "semester": 5,
  "email": "ahad@example.com",
  "phone": "+8801XXXXXXXXX"
}
```

* **id**: unique string (e.g., `S-0001`)
* **semester**: integer

> প্রয়োজনে extra fields (address, guardian, GPA) যোগ করুন।

---

## ▶️ Usage (CLI example)

```
1) Add Student
2) Update Student
3) Delete Student
4) Search Student (by id/name)
5) List All
6) Export PDF (single/all)
0) Exit
```

### Add

```
Enter ID: S-0002
Enter Name: Tanvir Hasan
...
Saved to students.json ✅
```

### Export to PDF

* **Single**: `Export -> by ID` → `exports/S-0002.pdf`
* **All**: `Export -> All` → `exports/students-YYYYMMDD.pdf`

---

## 🧪 Tests (optional)

```bash
mvn test
# or
gradle test
```

---

## 📸 Screenshots

Add images under `docs/` and reference here.

```
![Home](docs/home.png)
![Export PDF](docs/export.png)
```

---



## 🗺️ Roadmap

* CSV import/export
* Sorting & filtering
* Validation & better error messages
* GUI (JavaFX/Swing) / REST API

---

## 🤝 Contributing

Pull Request welcome! Create an issue describing your change.

---

## 📄 License

MIT (or your preferred license). Add `LICENSE` file.

---

## 📝 Author

**Ahad Ahmed**
GitHub: [@Ahad-19-s](https://github.com/Ahad-19-s)

---

### Replace me checklist

* [ ] Update library names/versions (JSON & PDF)
* [ ] Update run command (GUI/CLI)
* [ ] Add real screenshots
* [ ] Confirm data paths
* [ ] Choose a license
