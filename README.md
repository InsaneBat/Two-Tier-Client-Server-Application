SQL Command Executor - Java Application
This project consists of two Java applications that allow end-users to execute SQL commands against a remote MySQL database. The first application supports general end-users, while the second is a specialized version for an accountant client.

Overview
General Application
Purpose: A Java GUI-based application that allows users to execute any MySQL DDL (Data Definition Language) or DML (Data Manipulation Language) command.
Features:
Users can connect to a MySQL database using their credentials.
The application supports any SQL DDL or DML command, provided the user has the correct permissions.
The GUI displays the results of the SQL commands.
A background transaction logging operation tracks the number of queries and updates per user. This data is stored in a separate operational logging database, which is not accessible by end-users.
Accountant Application
Purpose: A specialized version of the general application, restricted to accountant-level users.
Features:
Accountant users can only query the transaction logging database.
This application provides a secure interface for viewing database interaction logs.
Application Details
JDBC Integration: Both applications use JDBC to connect to MySQL databases.
executeQuery() is used for SELECT commands.
executeUpdate() is used for other DDL and DML commands.
User Authentication:
User credentials are entered via the GUI and verified through the database.
Connection details are maintained in properties files, with secure access levels.
Simultaneous Connections: The application supports multiple users connecting simultaneously, with a default connection limit of 151.
User Roles
Root User: Has all permissions on the database.
Client Users: Have limited permissions on specific databases.
Accountant User: Has restricted access, limited to querying the transaction logging database.
Logging and Tracking
Each user operation is logged, tracking the number of queries and updates per user.
This logging is handled in a separate database with restricted access.
Setup Instructions
Clone the repository.
Configure the properties files with your MySQL credentials and database details.
Compile and run the Java applications.
Use the GUI to connect to your MySQL database and execute SQL commands.
