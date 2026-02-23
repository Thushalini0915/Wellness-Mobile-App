# Dark and Light Mode Theme Implementation

## Overview
This document describes the comprehensive dark and light mode theme implementation for the Wellness App, matching the orange/peach color scheme shown in the provided UI mockups.

## Features Implemented

### 1. Color System
- **Light Theme**: Clean white backgrounds with orange (#FF6B35) primary colors and peach gradients
- **Dark Theme**: Dark backgrounds (#121212, #1E1E1E) with lighter orange (#FF8A65) accents
- **System Theme**: Follows device system preference automatically

### 2. Theme Management
- `ThemeManager.kt`: Centralized theme management utility
- Persistent theme storage using SharedPreferences
- Automatic theme initialization on app startup
- Support for Light, Dark, and System theme modes

### 3. UI Components
All UI components have been updated to support both themes:
- **Cards**: Material 3 CardView with proper elevation and corner radius
- **Buttons**: Primary, outline, and floating action buttons
- **Text Input**: TextInputLayout with proper theming
- **Navigation**: Bottom navigation with theme-aware colors
- **Status Bar**: Proper light/dark status bar handling

### 4. Settings Integration
- Enhanced settings screen with comprehensive theme options
- Radio button selection for Light/Dark/System themes
- Quick toggle switch for legacy compatibility
- Theme test activity for debugging and demonstration

## File Structure

### Color Resources
- `values/colors.xml`: Light theme colors
- `values-night/colors.xml`: Dark theme colors

### Theme Resources
- `values/themes.xml`: Light theme definitions
- `values-night/themes.xml`: Dark theme definitions

### Layouts
- `fragment_settings.xml`: Enhanced settings with theme options
- `activity_theme_test.xml`: Theme testing interface

### Code
- `ThemeManager.kt`: Theme management utility
- `SettingsFragment.kt`: Updated with theme controls
- `ThemeTestActivity.kt`: Theme testing activity

## Color Palette

### Light Theme
- Primary: #FF6B35 (Orange)
- Background: #FAFAFA (Light Gray)
- Surface: #FFFFFF (White)
- Text Primary: #212121 (Dark Gray)
- Text Secondary: #757575 (Medium Gray)

### Dark Theme
- Primary: #FF8A65 (Light Orange)
- Background: #121212 (Very Dark Gray)
- Surface: #1E1E1E (Dark Gray)
- Text Primary: #FFFFFF (White)
- Text Secondary: #B3FFFFFF (Light Gray with Alpha)

## Usage

### Setting Theme Programmatically
```kotlin
// Apply specific theme
ThemeManager.applyTheme(context, ThemeManager.THEME_DARK)

// Get current theme
val currentTheme = ThemeManager.getCurrentTheme(context)

// Check if dark theme is active
val isDark = ThemeManager.isDarkTheme(context)
```

### Theme Attributes in Layouts
Use theme attributes instead of hardcoded colors:
```xml
<TextView
    android:textColor="?attr/colorOnSurface"
    android:background="?attr/colorSurface" />
```

## Testing
1. Open the app and go to Settings
2. Use the theme radio buttons to switch between Light, Dark, and System
3. Use the "Test Theme (Debug)" button to open the theme test activity
4. Verify all UI components adapt properly to theme changes

## Future Enhancements
- Custom accent color selection
- Theme-specific animations
- More granular theme customization options
- Theme preview functionality

## Notes
- All existing UI components have been updated to use theme attributes
- The implementation follows Material 3 design guidelines
- Theme changes are applied immediately without requiring app restart
- System theme mode automatically follows device dark mode settings
