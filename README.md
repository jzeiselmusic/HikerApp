# HikerApp

Hiker is a simple and fun Android app  for viewing your recent Strava activities

Hiker uses the Strava API to fetch and display the recent activity list of the logged-in athlete. The activities are presented in a colorful and fun way to inspire and motivate users.

## Features
- Connects to your Strava account to fetch your recent activities.
- Displays activities with a vibrant and engaging user interface.
- Supports deep linking for Strava account verification.

## Requirements
- Strava app installed on your device for deep linking (optional).
- Internet connection for Strava API authentication and data retrieval.
- Application has only been tested on a Pixel 6a phone.

## Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/jzeiselmusic/HikerApp.git
   cd HikerApp
   ```

2. **Gradle Sync**
   Before building the app, ensure that you perform a Gradle sync in Android Studio to fetch all required dependencies.

3. **Build and Run**
   Open the project in Android Studio, sync Gradle, and run the app on a device or emulator.

4. **Strava Authorization**
   - Allow all requested permissions.
   - When prompted to redirect after account authorization, choose the **"Hiker"** app to complete the process.


