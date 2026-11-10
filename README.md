<<<<<<< HEAD
# 🌿 MindEase (Mindmate Wellness)
### AI-Powered Mental Well-being Platform for University Students in Pakistan

**MindEase** is a calm, soft, and trustworthy Android mobile application specifically designed to address the emotional, psychological, and academic challenges faced by university students in Pakistan. It combines an intelligent multilingual AI emotional companion with daily mood check-ins, emotional pattern analysis, personalized self-care tools, an anonymous student peer support circle, and a verified Pakistani support directory.

---

## 🎨 Design Language & Philosophy

- **Safe Room Feeling**: Designed to feel like a warm, supportive private room rather than a clinical hospital tool.
- **Color Palette**: Deep indigo-purple (`#5B4FE0`) on a light lavender background (`#F8F7FF`) with pastel accents (mint, peach, soft yellow, sky, and pink) and warm orange (`#FF7A00`) reserved for SOS highlights.
- **Rounded & Pill-Shaped**: Generous 20dp–28dp rounded card corners and pill-shaped touch targets.
- **Emoji-First Typography**: Warm, expressive emojis act as primary visual indicators.
- **Floating Bottom Navigation**: 5 core tabs: **Home**, **Chat**, **Mood**, **Journal**, and **More**.

---

## 📱 The 12 MindEase Screens

### 1. Splash Screen (`SplashActivity`)
- Solid indigo-purple background with a centered circular leaf logo, bold white branding, and pale lavender tagline: *"A calm place to check in with yourself."*
- Smooth fade & scale entry that verifies authentication and PIN lock status before seamlessly routing to Onboarding, App Lock, or the Home dashboard.

### 2. Onboarding Carousel (`OnboardingActivity`)
- 3 focused slides:
  1. Multilingual AI companion in English, Urdu (اردو), and Roman Urdu.
  2. Mood & stress pattern tracking.
  3. Guided self-care, anonymous peer circle, and verified support contacts.
- Includes a prominent privacy guarantee: *"Your entries are only visible to you."*

### 3. Login & Signup (`LoginActivity`, `SignUpActivity`, `ForgotPasswordActivity`)
- Curved purple header with white input sheet.
- Asks only for a **Nickname**, **Email**, and **Password**—no real names required.
- Full Firebase Authentication integration (Email/Password, Password Reset, Guest / Anonymous login) and Realtime Database user sync.

### 4. Home Dashboard (`HomeFragment`)
- Top bar with round purple avatar, time-aware greeting (*"Good morning / afternoon / evening"*), and student nickname.
- Indigo hero card: *"Track your mood, transform your day"* with one-tap *"Check in now"* button.
- Pastel side-by-side shortcut cards: mint *"Talk it out"* and peach *"Feel calmer"*.
- Horizontal topic buttons (*Exams, Career, Family, Money, Lonely*) that launch AI chat with pre-loaded context.
- Daily affirmation quote (ZenQuotes Retrofit API), live campus weather (Open-Meteo Retrofit API), exam countdown, and a floating warm-orange Emergency SOS button.

### 5. AI Emotional Companion (`AiCompanionFragment`)
- Modern messenger with purple rounded student bubbles and white AI responses.
- Empathetic conversational AI supporting English, Urdu, and Roman Urdu (*"Mujhe exams ka bohot darr lag raha hai...", "Aap akele nahi hain..."*).
- Actionable chips beneath AI replies (*"Try Box Breathing"*, *"Write in Journal"*).
- **Crisis Detection**: Instantly detects self-harm/crisis keywords and replaces regular dialogue with emergency helpline access.

### 6. Mood Tracker (`MoodTrackerFragment`, `LogMoodBottomSheetDialog`)
- Top purple banner with a prominent dynamic emoji reflecting current feelings.
- 5 mood choices: *Great, Okay, Meh, Low, Awful*.
- 1–10 stress slider.
- University trigger chips (*Exams, Career, Family, Money, Friends, Sleep, Health, Loneliness*).
- Offline-first SQLite persistence with Firebase cloud sync.

### 7. Private Journal (`JournalFragment`)
- Safe, quiet notebook with today's date and gentle rotating reflection prompts (*"What is one thing that weighed on you today?"*).
- Multi-line free-form writing space with a purple pill *"Save entry"* button.
- Previous entries list with expandable previews and delete actions.

### 8. Emotional Insights (`InsightsFragment`, `MoodTrendChartView`)
- Visual analytics dashboard.
- Custom canvas-rendered wavy 7-day mood trend curve with colored dots and mood emojis.
- Stress level progress bar with average score calculation.
- Breakdown of frequent emotions and recurring stress triggers.

### 9. Personalized Self-Care (`SelfCareFragment`, `BreathingPacerView`)
- Large glowing indigo breathing orb with synchronized 4-4-4-4 Box Breathing (*Inhale... Hold... Exhale... Hold*) and haptic feedback.
- Interactive coping tools:
  - **5-4-3-2-1 Sensory Grounding** (`GroundingExerciseActivity`)
  - **Progressive Muscle Relaxation (PMR)**
  - **Healthy Study Routine** (links directly to Focus Timer)

### 10. Student Support Circle (`SupportCircleFragment`)
- Kind, judgment-free anonymous peer space.
- Student posts displayed with auto-assigned aliases (*"Anonymous Owl 🦉"*, *"Quiet Pine 🌲"*, *"Silent Star ⭐"*).
- Topic filter chips (*All, Exams, Hostel, Career, Loneliness*).
- **Me too** heart button with count and bounce animation (no comments or direct messages to prevent cyberbullying).
- Built-in keyword safety check that blocks self-harm posts and immediately triggers emergency helpline access.

### 11. Student Support Finder (`StudentResourcesFragment`)
- Comprehensive mental health directory tailored for Pakistani students.
- Filter chips: *Helplines (24/7)*, *On-Campus*, *Professionals*.
- Verified hotlines with direct telephone dialer intents:
  - **Umang Pakistan 24/7 Helpline**: `0311-7786264`
  - **Rozan Counseling Helpline**: `0800-22444`
  - **Taskeen Health Initiative**: `0316-8275336`
  - **Talk2Me Pakistan**: `0333-5577666`
  - **National Emergency**: `1122`
  - Campus centers: LUMS, NUST (C3A), FAST-NUCES, IBA Karachi, PU.
- Clear confidentiality reminder: *"Leaves no footprint in MindEase."*

### 12. Profile & Settings (`ProfileFragment`)
- Purple avatar with initial, nickname, and campus name.
- Summary strip showing lifetime stats: total check-ins, journal entries, and 30-day average stress.
- Edit nickname & campus dialog.
- Companion language preference selector.
- **App Lock (4-digit PIN)** toggle.
- Logout confirmation.
- **"Delete all my data"** option with destructive confirmation dialog.

---

## ⚡ Additional Integrated Features

- **App Lock PIN Screen (`AppLockActivity`)**: Plain purple screen with a custom 3x4 numeric keypad and 4 animated PIN dots. Supports Setup and Verification modes with "Forgot PIN?" recovery.
- **Focus Timer (`FocusTimerActivity`)**: 25-minute Pomodoro study countdown with a circular progress indicator, 5-minute break mode, and post-session reflection prompt that logs study check-ins.
- **Emergency SOS & Panic Mode (`SosBottomSheetDialog`, `PanicModeActivity`)**: Rapid grounding, 4-7-8 breathing, and immediate direct call buttons to 1122 and Umang.
- **Explore Hub (`MoreFragment`)**: Unified tab linking to Insights, Self-Care, Peer Circle, Support Finder, Focus Timer, and Settings.

---

## 🛠️ Tech Stack & Integrations

- **Language & Framework**: Kotlin, Android SDK 34 (Android 14), Material Design 3.
- **Architecture**: MVVM / Offline-First Repository pattern with ViewBinding.
- **Local Persistence**: SQLite database helper (`MindmateDatabaseHelper`) with tables for moods, chat, journals, and circle posts.
- **Cloud Backend**: Google Firebase SDK (`firebase-auth-ktx`, `firebase-database-ktx`) via `google-services.json`.
- **Remote APIs (Retrofit 2 + Gson + OkHttp)**:
  - **ZenQuotes API**: Daily inspirational student affirmations with local fallback.
  - **Open-Meteo API**: Live weather conditions and wellness tips for Pakistani university cities (Lahore, Islamabad, Karachi, Peshawar, Quetta, Faisalabad).




adb install -r app\build\outputs\apk\debug\app-debug.apk
```
=======
# MindEase
>>>>>>> 268036e53abbe610f1b233e455105820e468bc06
