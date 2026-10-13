# CareAtHome 🩺

An Android app, built in **Kotlin**, that connects patients with doctors and caregivers for care at home. Patients can find a doctor, book an appointment, chat with them, and ask an AI health assistant questions, all in one app.

> Team project

---

## ✨ Features

| Feature | What it does |
|---|---|
| 🔐 **Accounts** | Firebase sign up and login, forgot and change password, change email, add a phone number |
| 👥 **Two roles** | Separate profiles for **patients** and **doctors/caregivers** |
| 🔎 **Discover** | Browse available doctors and caregivers in a scrollable list with pull-to-refresh |
| 📅 **Bookings** | Pick a date and time to book an appointment; view **upcoming** and **past** appointments |
| 💬 **Private chat** | Real-time patient–doctor messaging stored in Firestore |
| 🤖 **AI health assistant** | In-app chatbot powered by **Google Gemini 1.5 Flash** for quick health questions |
| 👤 **Profile** | Update profile details, plus Terms of Service and Privacy Policy screens |

---

## 🏗 Architecture

- **MVVM:** `DoctorViewModel` and `DoctorRepository`, with LiveData feeding the UI.
- **Navigation:** a single `Home` activity with a `ViewPager2` and fragments (Home, Discover, Chats, Profile).
- **Data:**
  - **Firebase Realtime Database:** doctors, users and bookings.
  - **Cloud Firestore:** chats and messages.
- **Adapters:** `RecyclerView` adapters for doctors, bookings, users and chat messages.

```text
app/src/main/java/com/example/careathome/
├── SplashActivity, Login, SignUp        # onboarding and auth
├── Home, MainPagerAdapter               # main screen with tabs
├── HomeFragment, DiscoverFragment       # appointments and doctor discovery
├── Chats, PrivateChat, ChatAdapter      # messaging and AI assistant
├── Profile                              # account settings
├── DoctorViewModel, DoctorRepository    # MVVM layer
└── Booking, Doctor, User, Message       # data models
```

---

## 💻 Tech Stack

**Kotlin** · Android SDK (min 25, target 35) · Material Design · AndroidX (ViewModel, LiveData, ConstraintLayout, SwipeRefreshLayout) · **Firebase** (Auth, Firestore, Realtime Database, Analytics) · **Google Gemini** (Generative AI SDK)

---

## 🚀 Run

1. Clone the repo and open it in **Android Studio**.
2. Add your own `google-services.json` from a Firebase project to `app/`.
3. Add your Gemini API key.
4. Run the app on an emulator or a device (Android 7.1+).

A prebuilt APK is also available at `app/release/app-release.apk`.
