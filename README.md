# SIT305 Task 9.1P - Lost & Found App with Geolocation

This mobile application is built for **SIT305 - Mobile Application Development** at Deakin University.  
It extends **Assessment Task 7.1P** by adding **Google Maps geolocation** support to a Lost & Found tracking system backed by SQLite.

## 📱 Features

- Report **lost or found items** with name, description, location, and date.
- Enter location manually using **Google Places Autocomplete** or automatically via **GPS**.
- View saved item locations on a map (Task 9.1P enhancement).
- Offline persistence with **SQLite**.
- Clean and modern UI for intuitive use.

## 🗺️ New in Task 9.1P

- ✅ Google Places Autocomplete for address search.
- ✅ "Get Current Location" button to fetch device’s live GPS location.
- ✅ Locations saved as human-readable addresses in the database.
- ✅ Foundation laid for future map view integration using Google Maps SDK.

## 🛠 Tech Stack

- **Language**: Java
- **Database**: SQLite
- **Layout**: XML (LinearLayout)
- **APIs**:
   - Google Places API
   - Fused Location Provider (GPS)
- **IDE**: Android Studio

## 📸 Screenshots

_Add screenshots of the main screen, autocomplete, and location result preview here._

## 🚀 Getting Started

1. Clone this repo:
   ```bash
   git clone https://github.com/phanvuminhtrung/SIT305-Ontrack-9.1P-VuPhan.git

2. Open it in Android Studio.
3. Ensure you have a valid Google Maps API Key in local.properties: MAPS_API_KEY=your_api_key_here
   Connect an emulator or device with location enabled.
4. Run the project.

## 🔐 Permissions Required
_ACCESS_FINE_LOCATION – for retrieving current GPS position._

## 👤 Author
- Vu Phan
- Student ID: 221438973
- Deakin University