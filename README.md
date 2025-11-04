# WaterFlow

This is an Android application that helps manage water flow. The app allows users to log in, insert, search, and generate reports about water flow data.

## Features

* **User Authentication:** Secure login for authorized users.
* **Data Entry:** Easily insert new water flow data.
* **Search:** Quickly find specific water flow records.
* **Reporting:** Generate reports from the water flow data.
* **Main Dashboard:** A central hub to access all features after logging in.

## Default User
username: admin
password: admin


## Application Context

The application is designed to be a comprehensive tool for managing water flow information. It provides a simple and intuitive interface for users to perform their tasks efficiently. The core functionality revolves around collecting, retrieving, and analyzing water flow data.

The application follows a standard user workflow:
1.  The user is first presented with a **Login Screen** to ensure data security.
2.  Upon successful authentication, the user is taken to the **Main Dashboard**.
3.  The dashboard provides access to the main features of the app:
    *   **Insert Data:** Navigates to a screen where new water flow records can be added.
    *   **Search Data:** Opens a search interface to find existing records.
    *   **Generate Reports:** Allows the user to create and view reports based on the collected data.

## Project Structure

The project is a standard Android Studio project. Here are some of the key directories:

*   `app/`: The main application module.
*   `app/src/main/java/`: Contains the Kotlin source code for the application.
*   `app/src/main/res/`: Contains all the application resources, such as layouts, strings, and images.
*   `app/build.gradle.kts`: The build script for the application module.
*   `build.gradle.kts`: The top-level build script for the project.
*   `gradle/`: Contains the Gradle wrapper files.

## Technologies Used

*   **Kotlin:** The primary programming language for the application.
*   **Android SDK:** The software development kit for building Android apps.
*   **Gradle:** The build automation tool used for the project.
*   **XML:** Used for defining the user interface layouts and other resources.

## Building and Running

To build and run the application, follow these steps:

1.  Clone the repository to your local machine.
2.  Open the project in Android Studio.
3.  Let Android Studio sync the project with the Gradle files.
4.  Run the app on an Android emulator or a physical device.

