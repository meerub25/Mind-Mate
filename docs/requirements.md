# Software Requirements Specification (SRS) - MindEase 🌿

## 1. Project Overview

**MindEase** (internal package `com.mindmate.wellness`) is a native Android mental well-being companion application created specifically for university students in Pakistan. University life in Pakistan involves intense academic competition, CGPA anxiety, hostel isolation, financial hurdles, and high family expectations. Stigma around mental health often prevents students from seeking human counseling early. 

MindEase provides a calm, private "safe room" on a student's phone. It combines an intelligent on-device multilingual AI companion, daily mood check-ins, visual emotional pattern analytics, interactive coping tools (Box Breathing, 5-4-3-2-1 Sensory Grounding, Focus Timer), an anonymous student support circle, and a verified Pakistani student support directory.

> **Important Boundary & Clinical Disclaimer**: MindEase is a wellness and self-reflection companion. It **does not** provide medical diagnoses, psychiatric evaluations, or clinical therapy, and it does not replace a licensed psychologist, psychiatrist, or medical doctor. In crisis scenarios, it directs users immediately to verified human helplines.

---

## 2. Target Users & User Personas

| Target User Group | Characteristics & Pain Points | Primary App Benefits |
|---|---|---|
| **Undergraduate & Graduate University Students** | Enrolled in Pakistani universities (e.g., NUST, FAST-NUCES, LUMS, IBA, PU, COMSATS). Faces high CGPA anxiety, semester finals, and project deadlines. | Daily mood tracking, exam countdown, Pomodoro study timer with reflection logging, and motivational quotes. |
| **Hostel & Relocated Students** | Living away from home in university hostels. Struggles with homesickness, isolation, and peer adjustment. | Anonymous student circle, multilingual conversational AI companion in English, Urdu, or Roman Urdu. |
| **Privacy-Conscious Students** | Hesitant to discuss feelings due to societal or family stigma; fear of peers finding out. | 100% anonymous usage (no real name required), 4-digit PIN App Lock, offline-first local storage, and one-tap local data wipe. |
| **Students in Acute Distress** | Experiencing sudden anxiety spikes, panic attacks, or overwhelming emotional pressure. | Quick emergency SOS trigger, 4-4-4-4 Box Breathing pacer with haptic cues, and direct-dial emergency hotlines (Umang, Rozan, Taskeen, 1122). |

---

## 3. Functional Requirements

### Screen 1: Splash & Launch Routing
- **FR-1.1**: The app shall display a branded splash screen (`SplashActivity`) featuring the MindEase logo and tagline.
- **FR-1.2**: The app shall verify whether the user has completed the onboarding flow; if first launch, it shall navigate to `OnboardingActivity`.
- **FR-1.3**: The app shall check if the App Lock PIN feature is enabled. If enabled and the session is locked, it shall navigate to `AppLockActivity` before granting access to dashboard data.
- **FR-1.4**: If onboarding is complete and the session is unlocked, it shall navigate directly to the main container (`MainActivity`).

### Screen 2: Onboarding Carousel
- **FR-2.1**: The app shall present a 3-slide introductory carousel (`OnboardingActivity`) explaining:
  1. Multilingual AI companion (English, Urdu, Roman Urdu).
  2. Mood logging & stress pattern tracking.
  3. Anonymous peer support circle and verified student helplines.
- **FR-2.2**: The app shall display a prominent privacy assurance: *"Your entries are only visible to you."*
- **FR-2.3**: The app shall allow users to skip or finish the onboarding carousel, persisting the completion flag in `SharedPreferences`.

### Screen 3: Authentication & Anonymity
- **FR-3.1**: The app shall allow students to register (`SignUpActivity`) using an anonymous Nickname, student Email, and Password via Firebase Authentication.
- **FR-3.2**: The app shall **not** require real legal names, CNIC, or phone numbers during registration.
- **FR-3.3**: The app shall allow registered students to log in (`LoginActivity`) with Email and Password.
- **FR-3.4**: The app shall provide a password reset flow (`ForgotPasswordActivity`) sending a reset link via Firebase Auth.
- **FR-3.5**: The app shall offer a "Continue as Guest" option for immediate offline access without an account.
- **FR-3.6**: The app shall synchronize initial user metadata (UID, nickname, campus) to Firebase Realtime Database upon registration.

### Screen 4: Home Dashboard
- **FR-4.1**: The app shall show a personalized time-aware greeting (*"Good morning"*, *"Good afternoon"*, *"Good evening"*) along with the student's nickname and avatar initial.
- **FR-4.2**: The app shall feature a hero card with a one-tap shortcut to open the Mood Check-in dialog.
- **FR-4.3**: The app shall provide quick navigation cards to "Talk it out" (AI Companion) and "Feel calmer" (Self-Care).
- **FR-4.4**: The app shall provide one-tap topic prompt chips (*Exams, Career, Family, Money, Lonely*) that launch the AI chat with relevant pre-filled context.
- **FR-4.5**: The app shall fetch and display a daily inspirational student quote from the ZenQuotes API with graceful local fallback.
- **FR-4.6**: The app shall fetch live weather and wellness suggestions from Open-Meteo API for selected Pakistani university cities (Lahore, Karachi, Islamabad, Peshawar, Quetta, Faisalabad) with offline fallback.
- **FR-4.7**: The app shall display an Exam Countdown card with a configurable target date.
- **FR-4.8**: The app shall render a floating warm-orange SOS button providing instant access to emergency helplines.

### Screen 5: AI Emotional Companion
- **FR-5.1**: The app shall provide a conversational chat interface (`AiCompanionFragment`) backed by the on-device `AiCompanionEngine`.
- **FR-5.2**: The companion shall support multilingual inputs in English, Urdu (اردو in Arabic script), and Roman Urdu.
- **FR-5.3**: The companion shall classify student emotional states including Exam/CGPA Anxiety, Career Uncertainty, Family Expectations, Hostel Loneliness, Burnout, and Financial Pressure.
- **FR-5.4**: The companion shall provide actionable suggestion chips below responses (e.g., *"Try Box Breathing"*, *"Write in Journal"*, *"Helplines"*).
- **FR-5.5**: **Crisis Intervention**: The companion shall perform real-time keyword safety detection for self-harm and crisis phrases. When triggered, it shall bypass ordinary chat responses and provide immediate emergency helpline guidance.
- **FR-5.6**: The app shall save user and AI chat messages in local SQLite storage (`MindmateDatabaseHelper`).

### Screen 6: Mood Tracker & Spot Check-ins
- **FR-6.1**: The app shall allow users to log their mood (`LogMoodBottomSheetDialog`) selecting from 5 discrete states: *Great, Okay, Meh, Low, Awful*.
- **FR-6.2**: The app shall provide a continuous stress level slider from 1 (Relaxed) to 10 (Overwhelmed).
- **FR-6.3**: The app shall provide multi-select academic/lifestyle trigger chips: *Exams, Career, Family, Money, Friends, Sleep, Health, Loneliness*.
- **FR-6.4**: The app shall allow an optional short reflection note with each check-in.
- **FR-6.5**: The app shall persist mood entries in local SQLite and synchronize them with Firebase Realtime Database when online.
- **FR-6.6**: The app shall display recent check-in history cards in `MoodTrackerFragment`.

### Screen 7: Private Student Journal
- **FR-7.1**: The app shall provide a private notebook space (`JournalFragment`) with today's date and rotating prompts (e.g., *"What is one thing that weighed on you today?"*).
- **FR-7.2**: The app shall provide a multi-line text input field for free-form reflection with a dedicated "Save entry" action.
- **FR-7.3**: The app shall display previous journal entries in a chronological list with expandable text previews.
- **FR-7.4**: The app shall allow users to delete individual journal entries with immediate database removal.

### Screen 8: Emotional Insights & Reports
- **FR-8.1**: The app shall render a custom Canvas-drawn 7-day mood trend curve (`MoodTrendChartView`) displaying daily mood values with interactive emoji markers.
- **FR-8.2**: The app shall compute and display the student's consecutive check-in streak (`getCheckinStreak`).
- **FR-8.3**: The app shall compute and display the lifetime average stress score and qualitative stress status (Low/Calm, Moderate Strain, High Stress/Near Burnout).
- **FR-8.4**: The app shall analyze and display the student's top recurring stress triggers based on historical check-in data.

### Screen 9: Personalized Self-Care & Guided Coping
- **FR-9.1**: The app shall provide a visual Box Breathing pacer (`BreathingExerciseActivity`, `BreathingPacerView`) running a 4-4-4-4 second cycle (*Inhale, Hold, Exhale, Hold*) with animated scale expansion and synchronized haptic pulses.
- **FR-9.2**: The app shall provide an interactive 5-4-3-2-1 Sensory Grounding tool (`GroundingExerciseActivity`) guiding students through 5 sight, 4 touch, 3 sound, 2 smell, and 1 taste sensory anchors.
- **FR-9.3**: The app shall provide Progressive Muscle Relaxation (PMR) guidance.
- **FR-9.4**: The app shall include a dedicated 25-minute Pomodoro Focus Timer (`FocusTimerActivity`) with a 5-minute break mode and post-session reflection logging into the journal.

### Screen 10: Student Support Circle
- **FR-10.1**: The app shall provide an anonymous community board (`SupportCircleFragment`) where students can share short thoughts and academic struggles.
- **FR-10.2**: The app shall automatically assign whimsical pseudonyms (e.g., *"Anonymous Owl 🦉"*, *"Quiet Pine 🌲"*, *"Silent Star ⭐"*) to prevent identification.
- **FR-10.3**: The app shall provide topic filter chips: *All, Exams, Hostel, Career, Loneliness*.
- **FR-10.4**: The app shall provide an empathy reaction button (*"Me too" ❤️*) with count tracking and spring animation.
- **FR-10.5**: The app shall deliberately **omit** open text comments and direct messaging to prevent harassment, trolling, and cyberbullying.
- **FR-10.6**: The app shall inspect new circle posts for crisis/self-harm keywords, aborting the post and launching emergency SOS support if detected.

### Screen 11: Student Support Finder & Directory
- **FR-11.1**: The app shall maintain a verified directory (`StudentResourcesFragment`) of Pakistani mental health services categorized by: *24/7 Helplines*, *On-Campus Centers*, and *Professional Organizations*.
- **FR-11.2**: The app shall support one-tap phone dialer launching (`Intent.ACTION_DIAL`) for verified services:
  - **Umang Pakistan 24/7**: `0311-7786264`
  - **Rozan Counseling**: `0800-22444`
  - **Taskeen Health Initiative**: `0316-8275336`
  - **Talk2Me Pakistan**: `0333-5577666`
  - **National Emergency / Rescue**: `1122`
- **FR-11.3**: The app shall list prominent on-campus counseling departments (e.g., LUMS, NUST C3A, FAST-NUCES, IBA Karachi, Punjab University).
- **FR-11.4**: The app shall provide a standalone Panic Mode activity (`PanicModeActivity`) and quick SOS panel (`SosBottomSheetDialog`) with rapid grounding and direct calling.

### Screen 12: Profile & Security Settings
- **FR-12.1**: The app shall display the student's profile summary: avatar initial, nickname, campus name, lifetime check-in count, journal count, and average stress.
- **FR-12.2**: The app shall allow users to edit their nickname and campus name at any time.
- **FR-12.3**: The app shall allow users to choose their preferred companion conversation language (English, Urdu, Roman Urdu).
- **FR-12.4**: The app shall allow users to configure, change, or disable a 4-digit numeric App Lock PIN.
- **FR-12.5**: The app shall provide a destructive "Delete all my data" option (`clearAllData()`) with a confirmation dialog that securely wipes all local SQLite tables.
- **FR-12.6**: The app shall provide a logout button that terminates the Firebase Auth session and returns the user to the Login screen.

### Missing / Pending Features (Not Implemented Yet)
- **Audio/Voice Guided Meditation**: Not implemented yet (guided exercises are currently visual and text-based).
- **Direct In-App Counselor Appointment Booking**: Not implemented yet (campus contact information and phone numbers are provided instead).
- **Cloud LLM Generative Streaming API**: Not implemented yet (companion currently operates on a fast, private, on-device rule-based heuristic NLP engine).

---

## 4. Non-Functional Requirements (NFR)

| Category | Identifier | Requirement Specification |
|---|---|---|
| **Privacy** | **NFR-1.1** | No real legal name, CNIC, or home address shall ever be required or stored. |
| **Privacy** | **NFR-1.2** | Student Support Circle posts shall only display anonymous aliases. |
| **Security** | **NFR-2.1** | App Lock PIN shall be stored securely in private SharedPreferences and checked before revealing app screens. |
| **Security** | **NFR-2.2** | Passwords must be at least 6 characters; authentication handled via secure Firebase Auth tokens. |
| **Security** | **NFR-2.3** | Destructive data wipe must require explicit student confirmation through a warning dialog. |
| **Performance** | **NFR-3.1** | The app shall launch and become interactive within 2.5 seconds on modern devices. |
| **Performance** | **NFR-3.2** | Custom Canvas views (`MoodTrendChartView`, `BreathingPacerView`) shall animate smoothly at 60 frames per second without UI stutter. |
| **Performance** | **NFR-3.3** | Local database queries (SQLite) shall execute on background threads or return synchronously in under 50ms. |
| **Usability** | **NFR-4.1** | Follow Material Design 3 guidelines with a soothing indigo/lavender color palette and warm emojis. |
| **Usability** | **NFR-4.2** | Full support for multilingual interaction in English, Urdu script, and Roman Urdu. |
| **Offline Capability** | **NFR-5.1** | All core tools (Mood tracking, Journaling, AI Companion heuristics, Box Breathing, Grounding, Focus Timer, App Lock, and Support Directory) shall function completely without an active internet connection. |
| **Offline Capability** | **NFR-5.2** | When internet connectivity is absent, remote APIs (ZenQuotes, Open-Meteo) shall fall back to cached or local default data without error popups. |

---

## 5. Constraints

- **Platform Target**: Android OS versions 7.0 (API Level 24, Nougat) through Android 14 (API Level 34).
- **Language & SDK**: Kotlin 1.9+ with AndroidX libraries and Material Components 1.12.0.
- **Device Hardware**: Requires smartphone with standard touch screen, haptic vibration actuator (for breathing pacing), and telephony capabilities (for emergency hotline dialer intents).
- **Network**: Cellular or Wi-Fi data required only for initial Firebase authentication, quote/weather updates, and cloud sync; all other features function offline.

---

## 6. Out-of-Scope Items

- **Clinical Psychiatric Diagnosis**: Generating formal DSM-5 or ICD-11 medical diagnoses or prescribing pharmaceuticals.
- **Live Human Telehealth Consultations**: In-app video/voice streaming sessions with psychiatrists.
- **Unmoderated Public Group Chat**: Direct open messaging or private user-to-user DMs that could expose vulnerable students to harassment.
- **Cross-Platform Compilation**: iOS and Web versions are not part of this native Android release.
