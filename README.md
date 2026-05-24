
# Task Manager
A small-to-medium Java console application for managing tasks.
This project was built as a personal portfolio project to demonstrate clean OOP design, clear separation of responsibilities, and basic file persistence in Java.
## Features
- Create tasks
- Show all tasks
- Delete tasks
- Update full task details
- Update task status
- Filter tasks by status
- Filter tasks by priority
- Save tasks to a file
- Load tasks from a file when the program starts


## Task Fields
Each task includes:
- ID
- Title
- Description
- Status
- Priority
- Due date (optional)


## Technologies
- Java 17
- IntelliJ IDEA
- File-based persistence using plain text
- Console-based user interface


## Project Structure
```text
src/
└── taskmanager/
    ├── Main.java
    ├── model/
    │   ├── Task.java
    │   ├── TaskPriority.java
    │   └── TaskStatus.java
    ├── service/
    │   └── TaskManager.java
    ├── storage/
    │   └── TaskFileRepository.java
    └── ui/
        └── ConsoleUI.java



Architecture, The project is divided into a few simple layers:

model -
Contains the domain objects and enums used by the application.

service -
Contains the main business logic for managing tasks in memory.

storage -
Handles saving tasks to a text file and loading them back into the application.

ui -
Contains the console interface and user interaction flow.

Main -
Responsible only for wiring the application together and starting it.

File Persistence -
Tasks are saved in a plain text file called tasks.txt.
Each task is stored in one line using this format:

id|title|description|status|priority|dueDate

If a task has no due date, the value none is used.

____________________________________________________________________________________________

How to Run:

Run the application from IntelliJ IDEA by executing Main.java.
If you want to run it manually from the terminal:

javac -d out $(find src -name "*.java")
java -cp out taskmanager.Main

Example Menu:

1. Create task
2. Show all tasks
3. Delete task
4. Update task status
5. Update task
6. Filter tasks
7. Exit

Design Goals

This project was intentionally kept small and focused.

Main goals:

* Write clean and readable Java code
* Practice OOP and responsibility separation
* Create a polished GitHub project for student developer applications

Future Improvements

Possible next steps for the project:

* Better input validation
* Sorting tasks by due date or priority
* Mark task completion automatically through a shortcut action
* Better file format handling for special characters
* Unit tests
* GUI version
* Database persistence instead of a text file

Notes

For this version, the character | is not allowed in task title or description because it's used as the file separator.

## Author

Built by Gil Rozen as a personal Java project for learning and portfolio development.