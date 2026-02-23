# Wellness App

A comprehensive Android wellness tracking application built with Kotlin, featuring habit tracking, mood journaling, and personalized settings.

## 📱 Overview

The Wellness App is designed to help users maintain healthy habits and track their emotional well-being through an intuitive and modern interface. The app combines habit tracking with mood journaling to provide a holistic approach to personal wellness.

## ✨ Features

### 🎯 Habit Tracking
- **Create & Manage Habits**: Add, edit, and delete personal habits
- **Progress Tracking**: Visual progress bars and completion counters
- **Flexible Targets**: Set custom target counts for each habit
- **Frequency Options**: Daily, Weekly, or Monthly habit tracking
- **Smart Completion**: Intuitive toggle system for habit completion
- **Swipe to Refresh**: Pull down to reload habits

### 😊 Mood Journal
- **Mood Tracking**: Record daily mood with emoji support
- **Mood Categories**: Happy, Sad, Angry, Tired, Calm
- **Notes Support**: Add personal notes to mood entries
- **Visual Statistics**: Track mood patterns over time
- **Date-based Filtering**: View mood entries by specific dates

### ⚙️ Settings & Customization
- **Hydration Reminders**: Configurable water intake notifications
- **Dark Mode**: Toggle between light and dark themes
- **Reminder Intervals**: Customize notification frequency
- **App Preferences**: Personalized app behavior settings

## 🏗️ Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Android Views + Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Navigation Component
- **Data Storage**: SharedPreferences with Gson serialization
- **Dependency Injection**: Manual dependency management
- **Async Operations**: Kotlin Coroutines

### Project Structure
```
app/
├── src/main/
│   ├── java/com/example/myapp1/
│   │   ├── ui/theme/           # Compose theming
│   │   ├── binding/            # Data binding classes
│   │   ├── *.kt               # Main application classes
│   │   └── ...
│   ├── res/
│   │   ├── layout/            # XML layouts
│   │   ├── values/            # Strings, colors, themes
│   │   ├── drawable/          # Icons and images
│   │   └── navigation/        # Navigation graph
│   └── AndroidManifest.xml
├── build.gradle.kts
└── proguard-rules.pro
```

## 📦 Dependencies

### Core Dependencies
- **AndroidX Core**: `1.17.0`
- **AppCompat**: `1.7.0`
- **Lifecycle**: `2.9.4`
- **Navigation**: `2.9.5`
- **Material Design**: `1.10.0`

### UI Dependencies
- **Jetpack Compose**: `2024.09.00`
- **Material3**: Latest
- **SwipeRefreshLayout**: `1.1.0`
- **CardView**: `1.0.0`

### Data & Storage
- **Gson**: `2.10.1` (JSON serialization)
- **SharedPreferences**: Built-in Android
- **WorkManager**: `2.9.0` (Background tasks)

### Development
- **Kotlin**: `2.0.21`
- **Android Gradle Plugin**: `8.12.3`
- **JUnit**: `4.13.2`
- **Espresso**: `3.7.0`

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17 or later
- Android SDK 24+ (minimum), 35+ (target)

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device or emulator

### Build Configuration
```kotlin
android {
    compileSdk = 36
    defaultConfig {
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
```

## 📱 Screens & Navigation

### Main Navigation
The app uses a bottom navigation bar with three main sections:

1. **Habits** (`HabitsFragment`)
   - List of all habits with progress tracking
   - Add/Edit habit dialog
   - Swipe refresh functionality

2. **Mood Journal** (`MoodFragment`)
   - Mood entry interface
   - Mood history and statistics
   - Mood entry dialog

3. **Settings** (`SettingsFragment`)
   - App preferences
   - Notification settings
   - Theme selection

### Navigation Graph
```xml
<navigation app:startDestination="@id/navigation_habits">
    <fragment android:id="@+id/navigation_habits" />
    <fragment android:id="@+id/navigation_mood" />
    <fragment android:id="@+id/navigation_settings" />
    <dialog android:id="@+id/addEditHabitFragment" />
    <dialog android:id="@+id/moodEntryDialog" />
</navigation>
```

## 🏛️ Architecture Components

### ViewModels
- **HabitViewModel**: Manages habit data and operations
- **MoodViewModel**: Handles mood entries and statistics
- **SettingsViewModel**: Manages app preferences

### Data Models
- **Habit**: Represents a habit with progress tracking
- **MoodEntry**: Stores mood data with timestamps
- **SharedPreferencesManager**: Centralized data persistence

### Key Classes
- **MainActivity**: Main activity with navigation setup
- **WellnessApp**: Application class with initialization
- **NotificationHelper**: Handles notification management
- **HydrationReminderManager**: Manages water reminders

## 🔧 Data Management

### Data Storage
- **Primary Storage**: SharedPreferences
- **Serialization**: Gson for complex objects
- **Data Format**: JSON strings in SharedPreferences

### Data Flow
1. User interacts with UI
2. ViewModel processes the action
3. Data is saved via SharedPreferencesManager
4. UI is updated through StateFlow/LiveData

### Example Data Structure
```kotlin
data class Habit(
    val id: Long = 0L,
    val name: String,
    val targetCount: Int = 1,
    val completedCount: Int = 0,
    val frequency: String = "Daily",
    val date: Date = Date(),
    val isCompleted: Boolean = false
)
```

## 🎨 UI/UX Design

### Design System
- **Material Design 3**: Modern Material Design principles
- **Color Scheme**: Custom color palette with primary/secondary colors
- **Typography**: Material Design typography scale
- **Icons**: Material Design icons and custom drawables

### Layout Structure
- **Main Layout**: CoordinatorLayout with FAB
- **Habit Items**: MaterialCardView with progress indicators
- **Dialogs**: Full-screen dialogs for data entry
- **Empty States**: Informative empty state layouts

### Responsive Design
- **Adaptive Layouts**: Works on phones and tablets
- **Dynamic Sizing**: Flexible layouts for different screen sizes
- **Accessibility**: Content descriptions and proper focus handling

## 🔔 Notifications

### Hydration Reminders
- **Background Processing**: WorkManager for reliable scheduling
- **Customizable Intervals**: User-defined reminder frequency
- **Notification Channels**: Proper Android notification management
- **Boot Handling**: Reminders persist after device restart

### Notification Features
- **Channel Management**: Organized notification categories
- **User Control**: Enable/disable reminders in settings
- **Persistent Notifications**: Reliable reminder delivery

## 🧪 Testing

### Test Structure
- **Unit Tests**: JUnit tests for business logic
- **Instrumented Tests**: AndroidJUnitRunner for UI tests
- **Test Data**: Sample data for testing scenarios

### Testing Dependencies
```kotlin
testImplementation("junit:junit:4.13.2")
androidTestImplementation("androidx.test.ext:junit:1.3.0")
androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
```

## 🚀 Performance

### Optimization Features
- **View Binding**: Type-safe view references
- **Data Binding**: Efficient UI updates
- **Coroutines**: Non-blocking async operations
- **StateFlow**: Reactive data streams
- **RecyclerView**: Efficient list rendering

### Memory Management
- **Lifecycle Awareness**: Proper cleanup of resources
- **Weak References**: Avoiding memory leaks
- **Efficient Serialization**: Optimized JSON handling

## 🔒 Security & Privacy

### Data Protection
- **Local Storage Only**: No external data transmission
- **Encrypted Storage**: SharedPreferences security
- **Permission Management**: Minimal required permissions

### Privacy Features
- **No Analytics**: No user tracking
- **Local Processing**: All data stays on device
- **User Control**: Full control over data deletion

## 📋 Recent Updates

### Version 1.0.0 (Current)
- ✅ Fixed habit edit dialog functionality
- ✅ Improved habit completion logic
- ✅ Added swipe refresh to habits list
- ✅ Enhanced data persistence
- ✅ Fixed spinner selection in edit mode
- ✅ Improved error handling

### Bug Fixes
- Fixed edit habit dialog spinner not showing correct frequency
- Fixed habit completion count being reset during editing
- Fixed suspend function handling in ViewModels
- Added proper error handling throughout the app

## 🤝 Contributing

### Development Guidelines
1. Follow Kotlin coding conventions
2. Use meaningful variable and function names
3. Add proper documentation for public APIs
4. Write unit tests for new features
5. Test on multiple device sizes

### Code Style
- **Kotlin**: Follow official Kotlin style guide
- **Android**: Follow Android coding standards
- **Comments**: Document complex logic
- **Naming**: Use descriptive names

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support, feature requests, or bug reports, please create an issue in the project repository.

---

**Wellness App** - Track your habits, monitor your mood, and maintain your well-being! 🌟
