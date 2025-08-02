# 💰 Android Expense Tracker

A beautiful, modern Android expense tracking app built with Jetpack Compose and Material Design 3. Track your expenses, visualize spending patterns, and manage your finances with an intuitive dark-themed interface.

## ✨ Features

### 🎨 **Modern UI/UX**
- **Dark Theme**: Eye-friendly dark interface with gradient backgrounds
- **Material Design 3**: Latest Android design principles
- **Smooth Animations**: Fluid transitions and interactions
- **Responsive Design**: Works perfectly on all screen sizes

### 📊 **Expense Management**
- **Add Expenses**: Quick expense entry with description, amount, and category
- **Category System**: Pre-defined categories with emoji icons
- **Real-time Updates**: Instant balance calculations
- **Transaction History**: Scrollable list of all expenses

### 📈 **Analytics & Insights**
- **Spending Chart**: Interactive pie chart showing spending by category
- **Category Breakdown**: Visual representation with percentages
- **Total Balance**: Real-time balance display with color coding
- **Trend Analysis**: Track spending patterns over time

### 🏷️ **Categories Included**
- 🛒 Groceries
- 🎬 Entertainment  
- 🍽️ Eat Out
- 🍕 Take Out
- 🚗 Transport
- ⚡ Utilities
- 📝 Other

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (API level 24)
- Target SDK 35
- Kotlin 1.9+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/android-expense-tracker.git
   cd android-expense-tracker
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory and select it

3. **Build and Run**
   - Connect an Android device or start an emulator
   - Click the "Run" button (green play icon) in Android Studio
   - Or run from terminal: `./gradlew installDebug`

## 🛠️ Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Compose
- **Design System**: Material Design 3
- **Build System**: Gradle with Kotlin DSL
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15)

## 📱 Screenshots

### Main Interface
- Dark gradient background with floating cards
- Total balance display with color-coded amounts
- Interactive spending pie chart
- Category-based expense input form

### Features
- **Expense Entry**: Clean form with emoji icons
- **Category Selection**: Dropdown with emoji indicators
- **Transaction List**: Card-based expense history
- **Spending Analytics**: Visual category breakdown

## 🎯 Key Features Explained

### **Spending Chart**
- Custom-drawn pie chart using Canvas
- Real-time updates when expenses are added
- Color-coded categories matching the theme
- Percentage breakdown for each category

### **Dark Theme**
- Gradient background: `#1a1a2e` to `#16213e`
- Card background: `#0f3460` with transparency
- Accent color: `#e94560` (vibrant red-pink)
- High contrast text for excellent readability

### **Responsive Design**
- LazyColumn for smooth scrolling
- Adaptive layouts for different screen sizes
- Proper spacing and typography hierarchy
- Touch-friendly interactive elements

## 🔧 Customization

### Adding New Categories
Edit the `categories` list in `MainActivity.kt`:
```kotlin
val categories = listOf("Groceries", "Entertainment", "Eat Out", "Take Out", "Transport", "Utilities", "Other")
```

### Modifying Colors
Update the `categoryColor()` function:
```kotlin
fun categoryColor(category: String): Color = when (category) {
    "Groceries" -> Color(0xFF4CAF50)
    // Add your custom colors here
}
```

### Changing Theme
Modify the gradient colors in the Box background:
```kotlin
Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1a1a2e),  // Top color
        Color(0xFF16213e)   // Bottom color
    )
)
```

## 📈 Future Enhancements

- [ ] Data persistence with Room database
- [ ] Light theme toggle
- [ ] Export functionality (CSV/PDF)
- [ ] Budget limits and alerts
- [ ] Photo receipt capture
- [ ] Multiple currency support
- [ ] Cloud backup integration
- [ ] Widget support
- [ ] Biometric authentication

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Jetpack Compose**: Modern Android UI toolkit
- **Material Design 3**: Google's design system
- **Android Studio**: Development environment
- **Kotlin**: Programming language

## 📞 Support

If you have any questions or need help with the app, please:
- Open an issue on GitHub
- Check the existing issues for solutions
- Review the code comments for implementation details

---

**Made with ❤️ using Jetpack Compose and Material Design 3** 