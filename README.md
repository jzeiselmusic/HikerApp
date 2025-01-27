# HikerApp

Hiker is a simple and fun Android app  for viewing your recent Strava activities

Hiker uses the Strava API to fetch and display the recent activity list of the logged-in athlete. The activities are presented in a colorful and fun way to inspire and motivate users.

## Features
- Connects to your Strava account to fetch your recent activities.
- Displays activities with a vibrant and engaging user interface.
- Supports deep linking for Strava account verification.

## Requirements
- Internet connection for Strava API authentication and data retrieval.
- Strava app installed on your device for deep linking (optional).

## Notes
- Application has only been tested on **Pixel 6a phone with Android 13 and Galaxy Tab A7 with Android 12**
- If **not** deep-linking, authorization will use default browser. Known bug occurs when not using Chrome for default browser. 

## Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/jzeiselmusic/HikerApp.git
   cd HikerApp
   ```
2. **Build and Run**
   Open the project in Android Studio, sync Gradle, and run the app on a device or emulator.

3. **Strava Authorization**
   - Allow all requested permissions.
   - When prompted to redirect after account authorization, choose the **"Hiker"** app to complete the process.



## Future Improvements
1. Implement refresh token 
1. Create a timeout for the loading spinner in case internet connectivity is slow, or unresponsive
2. Improve the UI components for displaying text information to the user
3. Increase privacy and security by storing keys and private information in build files
4. Store access tokens in persistent storage for saved login status between application restarts
5. Handle more error cases - network unavailable, incorrect scope, permission denied
6. Ideally, the app could be extended to display more user information in fun ways
  - Map out user's route
  - Show elevation gain in GUI
  - Show friend interaction and comments
