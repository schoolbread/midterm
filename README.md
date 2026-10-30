# College Student Records App
Midterm Exam: Android App + Custom REST API + External API

**Topic:** School / College (Student Records Management)

**Framework:** Kotlin (Jetpack Compose)  **Backend:** PHP + MySQL

## Third-Party API
**ZenQuotes (Random Quote):** https://zenquotes.io/api/random

Displayed as a quote banner at the top of the home screen, with a "New quote" button.

## Custom REST API (`backend/students.php`)
| Method | URL | Purpose |
|---|---|---|
| GET | `/students.php` | List all students |
| POST | `/students.php` | Create student (JSON body) |
| PUT | `/students.php?id=1` | Update (JSON body) |
| DELETE | `/students.php?id=1` | Delete |

## Setup
1. Install XAMPP; start Apache and MySQL.
2. Import `backend/database.sql` in phpMyAdmin.
3. Copy the `backend/` folder to `C:\xampp\htdocs\midterm_api\`.
4. Test in browser: http://localhost/midterm_api/students.php
5. Open this project in Android Studio and run on an emulator (uses `10.0.2.2`).
   For a real device, edit `CUSTOM_BASE_URL` in `data/Network.kt` to your PC's LAN IP.

## Features
- List with detail screen, add form with validation, pre-filled edit form, delete confirmation dialog
- Daily inspirational quote banner from external API

## Student
Name: ________  Topic: School / College
