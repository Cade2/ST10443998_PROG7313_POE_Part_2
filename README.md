# Stash — Personal Budget Tracker

A personal budget tracking Android app built with Kotlin and RoomDB that 
makes managing finances engaging through gamification elements including 
XP points, level badges, and daily streaks.

---

## Features

- User registration and login with SHA-256 hashed passwords
- Create, edit, and delete expense categories with custom colour coding
- Add expenses with amount, date, start and end time, description and category
- Attach a photo receipt to any expense using camera or gallery
- Set minimum and maximum monthly budget goals per category
- View all expenses filtered by a user-selectable date range
- View total amount spent per category for any period
- Progress dashboard showing budget status with colour coded progress bars
- Spending graph showing daily totals for the current month
- All data persisted locally with RoomDB for offline use
- Automated unit tests with GitHub Actions CI

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Database | Room (SQLite) |
| Architecture | MVVM (ViewModel + LiveData) |
| Navigation | Jetpack Navigation Component |
| Charts | MPAndroidChart |
| Annotation Processing | KSP |
| Build System | Gradle with version catalog |

---

## How to Run

1. Clone this repository
2. Open in Android Studio Hedgehog or later
3. Wait for Gradle sync to complete
4. Run on emulator (API 24+) or physical Android device
5. Register a new account on the login screen to get started

---

## Running Tests

To run the unit tests locally:

```bash
./gradlew test
```

---

## GitHub Actions CI

Every push to main automatically:
- Runs all unit tests
- Builds the debug APK
- Uploads the APK as a build artifact

![Android CI](https://github.com/EMGPSD/prog7313-g1-2026-part2-galacticos/actions/workflows/build.yml/badge.svg)

---

## Demo Video

[Watch the Stash demo video here](VIDEO_LINK_HERE)

---

## Team — Group 5

| Student Number | Name | Feature Committed |
|---|---|---|
| ST10443998 | Cade Brink | Project setup, ViewModels, Tests, GitHub Actions, README |
| ST10437445 | Mustafa Malik | Login and Register with real authentication |
| ST10435238 | Su-Mian Jia | RoomDB layer — entities, DAOs, AppDatabase, Repository |
| ST10318188 | Ronald Fell | Add expense screen and photo attachment |
| ST10368925 | Reabetsoe Lemmy Ledwaba | Expense list, categories, budget goals, spending graph |

---

## Custom Features for Final POE

### Feature 1 — Gamification System
Users earn XP points for logging expenses consistently, keeping daily 
streaks, and meeting budget goals. XP unlocks levels and badges such as 
"Week Warrior" and "Budget Master" shown on the Achievements screen.

### Feature 2 — Spending Graph with Daily Breakdown
A bar graph displays daily spending totals for the current month, built 
using MPAndroidChart. The graph shows real expense data grouped by date 
and will include minimum and maximum goal lines in the final POE.
