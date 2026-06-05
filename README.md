# 🇿🇲 Zambia Quiz - Android App

An interactive educational quiz application about Zambia, featuring **1,294+ questions** loaded dynamically from a cloud API, with **509 offline questions** as a fallback. Built with modern Android architecture using Kotlin Coroutines, Retrofit, and the Singleton pattern.

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Language](https://img.shields.io/badge/Language-Kotlin-blue)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange)
![API](https://img.shields.io/badge/Architecture-Hybrid%20API-purple)

---

## ✨ Features

### **Core Quiz Features**
- ✅ **1,294+ Questions** - Dynamically loaded from cloud API
- ✅ **509 Offline Questions** - Local fallback for offline use
- ✅ **8+ Categories** - Geography, History, Sports, Culture, Economics, Politics, General Knowledge, and more
- ✅ **3 Difficulty Levels** - Easy, Medium, Hard
- ✅ **Timed Quizzes** - 20-second timer per question with visual countdown
- ✅ **Multiple Quiz Lengths** - Choose 10 or 20 questions per session
- ✅ **Smart Question Selection** - Random and category-based filtering

### **Network & Data**
- ✅ **REST API Integration** - Connects to `https://sangwapo.com/api/questions.json`
- ✅ **Hybrid Architecture** - Server-first with local fallback
- ✅ **Offline Mode** - Works without internet using local storage
- ✅ **Smart JSON Parsing** - Handles inconsistent data formats gracefully
- ✅ **Singleton Pattern** - Questions loaded once, shared across screens

### **User Experience**
- ✅ **Sound Effects** - Audio feedback for correct/wrong answers
- ✅ **Haptic Feedback** - Vibration on answer selection
- ✅ **Beautiful UI** - Zambian-themed design with flag colors
- ✅ **Background Slideshow** - Scenic Zambian imagery with smooth transitions
- ✅ **Smooth Animations** - Page transitions, button bounces, fade effects
- ✅ **Splash Screen** - Branded loading experience while fetching questions
- ✅ **Progress Indicators** - Visual progress bar and question counter
- ✅ **Result Sharing** - Share scores via any messaging app
- ✅ **Performance Stats** - Correct, wrong, skipped questions and time taken

---

## 🛠️ Technologies Used

### **Core Stack**
- **Language:** Kotlin
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)
- **Architecture:** MVVM-inspired with Singleton Repository pattern

### **Network Layer** NEW
- **Retrofit2** - Type-safe HTTP client for REST API calls
- **OkHttp3** - HTTP client with connection pooling and interceptors
- **Logging Interceptor** - Debug network requests and responses
- **Kotlin Coroutines** - Asynchronous programming without blocking UI

### **Data & Parsing**
- **Gson** - JSON serialization/deserialization
- **JsonParser** - Custom manual parsing for flexible data formats
- **Custom Type Adapters** - Handle multiple JSON structures

### **UI Components**
- **ViewPager2** - Image slideshow with custom page transformers
- **RecyclerView** - Efficient category grid display
- **CardView** - Material Design cards
- **Glide** - Image loading and caching
- **Material Design Components** - Modern UI elements

### **Android Features**
- **View Binding** - Type-safe view references
- **Lifecycle Components** - `lifecycleScope` for coroutines
- **MediaPlayer** - Sound effects playback
- **Vibrator** - Haptic feedback
- **CountDownTimer** - Quiz countdown functionality

---

## Architecture
app/
├── src/main/
│   ├── java/com/example/zambiaquiz/
│   │   ├── data/
│   │   │   ├── QuizManager.kt          # Singleton data repository
│   │   │   ├── RetrofitClient.kt       # HTTP client configuration
│   │   │   └── QuizApiService.kt       # API endpoint definitions
│   │   ├── models/
│   │   │   ├── Question.kt             # Question data class
│   │   │   ├── QuizCategory.kt         # Category data class
│   │   │   └── QuizResult.kt           # Result data class
│   │   └── ui/
│   │       ├── SplashActivity.kt       # Loads questions from API
│   │       ├── MainActivity.kt         # Welcome screen
│   │       ├── CategoryActivity.kt     # Category selection
│   │       ├── QuizActivity.kt         # Quiz gameplay
│   │       ├── ResultActivity.kt       # Score display
│   │       ├── CategoryAdapter.kt      # RecyclerView adapter
│   │       └── ImageSliderAdapter.kt   # ViewPager2 adapter
│   ├── assets/
│   │   └── questions.json              # 509 offline questions (fallback)
│   └── res/
│       ├── layout/                     # XML UI layouts
│       ├── drawable/                   # Images & vector graphics
│       ├── anim/                       # Custom animations
│       └── raw/                        # Sound effects (MP3)

### **Data Flow**
SplashActivity
↓ (loads questions during splash)
QuizManager.getInstance()
↓
┌─→ Try Server API (https://sangwapo.com/api/questions.json)
│       ↓ Success → Use 1,294 questions
│       ↓ Failure → Fallback to local
└─→ Local Storage (assets/questions.json)
↓ Use 509 offline questions
↓
MainActivity → CategoryActivity → QuizActivity → ResultActivity

---

## 🌐 API Integration Highlights

### **The Challenge**
The server returned data with:
- Different field naming conventions (capitalized vs lowercase)
- Inconsistent structures (arrays vs objects for options)
- 1,294 questions of varying quality

### **My Solution**
- **Hybrid Architecture** - Online questions with offline fallback
- **Custom JSON Parser** - Handles both data formats automatically
- **Singleton Pattern** - Loads questions only once, shares across activities
- **Splash Loading** - Eliminates UI flicker by pre-loading data

### **Result**
- ✅ Loaded all **1,294 questions** with 0 errors
- ✅ Seamless online/offline experience
- ✅ Professional, production-ready architecture
- ✅ 155% more content than local-only version

---

## Installation

### **Prerequisites**
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 24+ (Android 7.0)
- JDK 17+
- Internet connection (optional - works offline too!)

### **Steps**

1. **Clone the repository**
```bash
git clone https://github.com/Emmanuel-cpp/Zambia-Quiz-Mobile-App.git
cd Zambia-Quiz-Mobile-App
```

2. **Open in Android Studio**
   - File → Open → Select the project folder
   - Wait for Gradle sync to complete

3. **Run the app**
   - Connect an Android device (USB Debugging enabled) OR start an emulator
   - Click the **Run** button (▶️) or press `Shift + F10`

---

## How to Use

1. **Launch App** - Branded splash screen displays while questions load from API
2. **Main Menu** - Choose "Start Quiz" or "Quick Play" for instant random quiz
3. **Select Category** - Pick from 8+ categories (Geography, History, Sports, etc.)
4. **Choose Difficulty** - Easy, Medium, Hard, or All
5. **Select Quiz Length** - 10 or 20 questions
6. **Answer Questions** - 20-second timer per question with audio feedback
7. **View Results** - See your score, accuracy, and time taken
8. **Share** - Share your achievements with friends via any messaging app

---

## Key Learning Outcomes

### **Android Development**
- ✅ Android project architecture (MVVM-inspired)
- ✅ RecyclerView.Adapter pattern for lists and grids
- ✅ ViewPager2 with custom page transformers
- ✅ View Binding for type-safe view access
- ✅ Intent navigation and data passing between activities
- ✅ Custom animations (XML and code-based)
- ✅ Material Design principles
- ✅ Lifecycle management

### **Network Programming**  NEW
- ✅ REST API integration with Retrofit2
- ✅ HTTP client configuration with OkHttp
- ✅ Asynchronous programming with Kotlin Coroutines
- ✅ JSON parsing (automatic with Gson + manual with JsonParser)
- ✅ Error handling and fallback strategies
- ✅ Network logging and debugging

### **Software Engineering**  NEW
- ✅ Singleton design pattern
- ✅ Repository pattern for data access
- ✅ Fallback pattern for resilience
- ✅ Defensive programming (handling malformed data)
- ✅ Separation of concerns (data layer vs UI layer)
- ✅ Code organization and project structure

### **Multimedia & Hardware**
- ✅ MediaPlayer for sound effects
- ✅ Vibrator for haptic feedback
- ✅ CountDownTimer for quiz timing
- ✅ Image loading and caching with Glide

---

## Statistics

| Metric | Value |
|--------|-------|
| **Total Questions (Online)** | 1,294 |
| **Total Questions (Offline)** | 509 |
| **Categories** | 8+ (dynamic from API) |
| **Difficulty Levels** | 3 |
| **Activities** | 5 |
| **Adapters** | 2 |
| **XML Layouts** | 10+ |
| **Custom Animations** | 6 |
| **Languages** | Kotlin |
| **Lines of Code** | ~2,000+ |
| **Min Android Version** | 7.0 (API 24) |
| **Network Libraries** | 4 (Retrofit, OkHttp, Coroutines, Gson) |

---

## 🌐 API Endpoint

This app integrates with the following REST API:
GET https://sangwapo.com/api/questions.json

**Returns:** JSON array of question objects with fields:
- `Number` - Question ID
- `Question` - Question text
- `Options` - Array of answer choices
- `Answer` - Correct answer
- `Category` - Question category
- `Difficulty` - Easy/Medium/Hard

---

## Permissions Required

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.VIBRATE" />
```

---

## Screenshots

> _Screenshots coming soon!_

| Splash | Main Menu | Categories | Quiz | Results |
|--------|-----------|------------|------|---------|
| _Loading_ | _Welcome_ | _Selection_ | _Gameplay_ | _Score_ |

---

## Future Enhancements

- [ ] Local caching of server questions for faster subsequent loads
- [ ] User authentication and personalized recommendations
- [ ] Leaderboard with global rankings
- [ ] Daily challenge mode with streaks
- [ ] Achievement badges system
- [ ] Quiz review mode (see correct answers after quiz)
- [ ] Multiple language support (English, Bemba, Nyanja, Tonga)
- [ ] Dark mode toggle
- [ ] Image-based questions

---

## 👨‍💻 Author

**Emmanuel Siamoonga**
- 📧 Email: [emmanuelsiamoonga@gmail.com](mailto:emmanuelsiamoonga@gmail.com)
- 🐙 GitHub: [@Emmanuel-cpp](https://github.com/Emmanuel-cpp)
- 🎓 Computer Science Student, The Copperbelt University

---

## License

This project was created as an academic assignment for Mobile Programming coursework.

---

## Acknowledgments

- **API Endpoint** - Provided by project supervisor (sangwapo.com)
- **Local Questions** - Generated with AI assistance and manual curation
- **Sound Effects** - Free resources from open libraries
- **Images** - Scenic Zambian photography
- **Course Instructor** - For guidance and the API endpoint
- **The Copperbelt University** - For the educational opportunity

---

## Technical Documentation

For a detailed technical discussion of the API integration approach, design patterns used, and challenges overcome, see the project documentation or contact the author.

**Key Topics Covered:**
- Hybrid architecture design (server + local fallback)
- Custom JSON parsing for inconsistent data formats
- Singleton pattern implementation
- Kotlin Coroutines for asynchronous operations
- Error handling and graceful degradation strategies

---

⭐ **If you found this project helpful or interesting, please star the repository!** ⭐

---
