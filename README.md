# Java Secure Intelligent Password Manager

## Overview
The Java Secure Intelligent Password Manager is an application designed to securely manage and store passwords, ensuring users can maintain a high level of security for their sensitive information.

## Features
- Password Generation
- Secure Storage
- User-friendly Interface
- Encryption of Stored Data
- Cross-platform Compatibility

## Installation
To install the Password Manager, follow these steps:
1. Clone the repository:
   ```bash
   git clone https://github.com/N-AVTEJ/Java_Secure-Intelligent-Password-Manager.git
   ```
2. Navigate to the project directory:
   ```bash
   cd Java_Secure-Intelligent-Password-Manager
   ```
3. Compile the application:
   ```bash
   javac -d bin src/*.java
   ```
4. Run the application:
   ```bash
   java -cp bin com.passwordmanager.Main
   ```

## Usage
Upon starting the application, you will be able to create, read, update, and delete password entries. Ensure that you have a strong master password to protect your database.

## Contributing
We welcome contributions! Please fork this repository and submit a pull request for any changes you'd like to propose.

## Setup and Run (PowerShell, no Maven required)
1. Ensure MySQL Server is running.
2. Setup database:
   - `.\setup-db.ps1`
3. Run app:
   - `.\run-app.ps1`

The run script auto-downloads MySQL JDBC driver jar into `lib/` and starts the app with correct classpath.

## Optional Maven Run
If Maven is installed:
- `mvn clean compile`
- `mvn exec:java`

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
