# PetPal - Lost & Found Pet Companion 🐾

**PetPal** is a mobile application for the Android operating system, built with the goal of connecting the community to help find and reunite lost pets with their families. Users can easily report a missing pet, post about a found pet, and view a map of nearby cases.

This project was developed as part of a course at **RMIT University Vietnam**.

---
## 📸 UI & Key Features

| Splash Screen | Home Screen |
| :---: |:---:|
| ![SplashScreen](https://github.com/user-attachments/assets/5201ff0f-506c-455f-a06c-d15701050ecc)|![PetDetailScreen](https://github.com/user-attachments/assets/38402489-0b87-4d12-a0ea-647927fe5ab6)|

| Pet Detail Screen | Map Screen | Report Screen |
|:---:|:---:|:---:|
|![HomeScreen](https://github.com/user-attachments/assets/a107762b-f33e-4595-9c60-964de037213e)|![MapsScreen](https://github.com/user-attachments/assets/b133264e-975e-40aa-ae92-0cd74363250a)|![ReportLostPet](https://github.com/user-attachments/assets/92e80546-8594-429f-87ba-7956d1855b5f)|

---
## ✨ Core Features

* **🏠 Central Dashboard:** The main screen provides quick access to core functionalities:
    * **Home:** View a list of lost and found pets.
    * **Map:** Explore a visual map of pet locations.
    * **Report Lost/Found:** Quickly create a new report.

* **🐾 Smart Home Screen:**
    * Displays two separate lists: **"Lost Pets"** and **"Found Pets"**.
    * Features a powerful **search bar** to filter by name, breed, or color.
    * **Filters** allow users to view all cases, only lost pets, or only found pets.
    * Pet information is displayed in a modern, horizontally scrollable card format.

* **🗺️ Interactive Map:**
    * Shows the location of all reported pets on Google Maps.
    * Uses different colored **markers** to distinguish between statuses (Lost, Found).
    * Automatically identifies and displays the **user's current location** for easy navigation.
    * Requests location permissions to enhance the user experience.

* **❗ Detailed Reporting System:**
    * Provides two separate report forms for **"Lost Pet"** and **"Found Pet"** cases.
    * Allows users to enter detailed identification information: name, breed, color, features, accessories...
    * Includes a feature to **select photos** from the phone's gallery.
    * Enables users to **select the exact location on a map** (Map Selector) where the pet was last seen or found.

---
## 🛠️ Technology & Architecture

The project is built entirely with **Kotlin** and adheres to modern Android development best practices.

* **Language:** **Kotlin**
* **User Interface (UI):** **Jetpack Compose** - For building a declarative UI that is concise and easy to manage.
* **Architecture:** **MVVM (Model-View-ViewModel)** - Separates UI logic from business logic, making the codebase easier to maintain and test.
* **State Management:** **Kotlin Coroutines** & **StateFlow** - For handling asynchronous tasks and managing UI state efficiently.
* **Dependency Injection:** **Hilt** - Simplifies the management and provision of dependencies within the application.
* **Navigation:** **Navigation Component** - Manages the flow between different screens.
* **Mapping & Location:**
    * **Google Maps Compose Library:** Integrates Google Maps into the Compose UI.
    * **Google Play Services Location:** Fetches the user's current location.
* **Image Loading:** **Coil** - A modern and efficient image loading library for Kotlin.

---
## 🚀 How to Install and Run

You'll need **Android Studio Giraffe** (or newer) to build this project.

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/TrongTruong145/A1PelPal.git](https://github.com/TrongTruong145/A1PelPal.git)
    ```
2.  Open the project in **Android Studio**.
3.  Wait for Gradle to build and sync the project.
4.  Press **Run** ▶️.

---
## 👨‍💻 Author

**Trọng Trương (Adrian Truong)**

* GitHub: [@TrongTruong145](https://github.com/TrongTruong145)
* LinkedIn: [Trọng Trương](https://www.linkedin.com/in/trong-truong-555704220/)

---
## 📄 License

This project is licensed under the MIT License. See the `LICENSE` file for more details.

---
## 🏆 Acknowledgements

Special thanks to the following open-source libraries that made this project possible:

* **Coil**: The image loading library.
* **Hilt**: The Dependency Injection library.
* **Google Maps Compose Library**: For integrating Google Maps with Jetpack Compose.

Thanks to **RMIT University Vietnam** for providing the opportunity and knowledge to develop this project.
