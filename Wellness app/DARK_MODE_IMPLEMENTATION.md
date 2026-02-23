# Dark Mode Implementation Guide

This document describes the comprehensive dark mode implementation for the Wellness App.

## Overview

The app now supports both light and dark themes with automatic switching based on user preference. The implementation includes:

- **ThemeManager**: Centralized theme management
- **Dark theme resources**: Complete set of dark mode colors and styles
- **Automatic theme application**: Themes are applied on app startup
- **Real-time theme switching**: Users can toggle themes without restarting the app
- **Theme-aware UI components**: All layouts and components adapt to the current theme

## Files Modified/Created

### New Files
- `app/src/main/java/com/example/myapp1/ThemeManager.kt` - Central theme management
- `app/src/main/java/com/example/myapp1/ThemeTestActivity.kt` - Test activity for theme verification
- `app/src/main/res/values-night/colors.xml` - Dark theme colors
- `app/src/main/res/values-night/themes.xml` - Dark theme styles

### Modified Files
- `app/src/main/java/com/example/myapp1/MainActivity.kt` - Added theme application on startup
- `app/src/main/java/com/example/myapp1/SettingsFragment.kt` - Updated theme switching logic
- `app/src/main/java/com/example/myapp1/SharedPreferencesManager.kt` - Deprecated old theme methods
- `app/src/main/res/layout/fragment_settings.xml` - Added theme test button
- `app/src/main/res/layout/fragment_mood.xml` - Updated to use theme-aware colors
- `app/src/main/res/layout/fragment_habits.xml` - Updated to use theme-aware colors
- `app/src/main/res/layout/fragment_sleep_tracking.xml` - Updated to use theme-aware colors
- `app/src/main/res/layout/fragment_habit_tracker.xml` - Updated to use theme-aware colors
- `app/src/main/res/layout/item_mood_entry.xml` - Updated to use theme-aware colors
- `app/src/main/res/drawable/bottom_nav_selector.xml` - Updated to use theme-aware colors
- `app/src/main/AndroidManifest.xml` - Added test activity

## ThemeManager Usage

### Basic Usage
```kotlin
// Apply theme on app startup (in MainActivity.onCreate())
ThemeManager.applyTheme(this)

// Set a specific theme
ThemeManager.setTheme(context, ThemeManager.THEME_DARK)
ThemeManager.setTheme(context, ThemeManager.THEME_LIGHT)
ThemeManager.setTheme(context, ThemeManager.THEME_SYSTEM)

// Get current theme
val currentTheme = ThemeManager.getCurrentTheme(context)

// Check if dark mode is active
val isDarkMode = ThemeManager.isDarkMode(context)

// Toggle between light and dark
ThemeManager.toggleTheme(context)
```

### Available Themes
- `THEME_LIGHT`: Force light mode
- `THEME_DARK`: Force dark mode  
- `THEME_SYSTEM`: Follow system theme

## Theme Resources

### Light Theme (values/colors.xml)
- Primary colors: Blue tones
- Background: Light gray (#F5F5F5)
- Surface: White (#FFFFFF)
- Text: Dark colors for good contrast

### Dark Theme (values-night/colors.xml)
- Primary colors: Lighter blue tones for better visibility
- Background: Dark gray (#121212)
- Surface: Darker gray (#1E1E1E)
- Text: Light colors for good contrast

## Testing

### Manual Testing
1. Open the app and go to Settings
2. Toggle the "Dark Mode" switch
3. Verify that all UI elements change to dark theme
4. Navigate through all fragments to ensure consistent theming
5. Use the "Test Theme (Debug)" button to access the test activity

### Test Activity
The `ThemeTestActivity` provides a simple interface to test theme switching:
- Shows current theme information
- Provides a toggle button to switch themes
- Displays a test card that adapts to the current theme

## Key Features

### 1. Automatic Theme Application
- Themes are applied on app startup before `setContentView()`
- No need to restart the app to see theme changes

### 2. Real-time Theme Switching
- Users can toggle themes in settings
- Changes are applied immediately
- All fragments and activities respect the current theme

### 3. System Theme Support
- App can follow system theme preference
- Automatically switches when system theme changes

### 4. Theme-aware Components
- All layouts use theme attributes instead of hardcoded colors
- Material Design components automatically adapt
- Custom components respect theme colors

## Best Practices

### 1. Use Theme Attributes
Instead of hardcoded colors, use theme attributes:
```xml
<!-- Good -->
android:textColor="?attr/textColorPrimary"
android:background="?attr/colorBackground"

<!-- Avoid -->
android:textColor="#000000"
android:background="#FFFFFF"
```

### 2. Test Both Themes
Always test your UI in both light and dark modes to ensure:
- Good contrast ratios
- Readable text
- Proper color usage

### 3. Use Material Design Components
Material Design components automatically adapt to themes:
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardBackgroundColor="?attr/colorSurface" />
```

## Troubleshooting

### Theme Not Applying
1. Ensure `ThemeManager.applyTheme()` is called before `setContentView()`
2. Check that the theme resources are properly defined
3. Verify that the app is using the correct theme in the manifest

### Colors Not Updating
1. Make sure you're using theme attributes (`?attr/colorName`) instead of hardcoded colors
2. Check that the dark theme resources are properly defined
3. Verify that the theme is being applied correctly

### Performance Issues
1. Theme switching recreates activities, which is normal behavior
2. For better performance, consider using `AppCompatDelegate.setDefaultNightMode()` directly
3. Avoid frequent theme switching in production code

## Future Enhancements

1. **Custom Theme Colors**: Allow users to customize accent colors
2. **Theme Scheduling**: Automatically switch themes based on time of day
3. **Accessibility**: Enhanced support for high contrast themes
4. **Theme Transitions**: Smooth animations when switching themes

## Conclusion

The dark mode implementation provides a comprehensive theming solution that enhances user experience and follows Material Design guidelines. The modular approach makes it easy to maintain and extend in the future.
