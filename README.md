# 📱 SubTrack - Smart Subscription Manager & AI Financial Advisor

![Language](https://img.shields.io/badge/Language-Kotlin-orange)
![UI](https://img.shields.io/badge/UI-Jetpack_Compose-green)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue)
![AI](https://img.shields.io/badge/AI-Gemini_Pro-purple)

> **SubTrack** is a modern Android application designed to help users track their recurring expenses, manage subscriptions, and receive personalized financial advice using Generative AI.

## 🚀 Key Features
### 💰 Subscription Management
- **Expense Tracking:** Visual dashboard of monthly and yearly expenses.
- **Smart Calculations:** Handles different billing cycles (Monthly/Yearly) and trial periods.
- **Local Persistence:** Data is securely stored using **Room Database**.

### 🤖 AI Financial Advisor (Powered by Gemini)
- Integrated **Google's Gemini 3 Pro model** to provide personalized financial tips.
- **Context-Aware:** The AI analyzes the user's current subscription list to offer tailored advice on how to save money.
- **Generative UI:** Chat interface built entirely with Jetpack Compose.

### 📈 Smart User Retention & Engagement
Implemented advanced logic to keep users engaged (Retention Loops):
- **Habit Loops:** Daily `WorkManager` notifications encourage users to log expenses ("Did you spend money today?").
- **Reactivation Campaigns:** Automated push notifications to bring back inactive users with targeted upgrade offers.

### ⭐ Intelligent Rating System
A custom algorithm to maximize store ratings:
- **Filtering Logic:** Users who rate 4-5 stars are redirected to the Play Store.
- **Feedback Loop:** Users who rate 1-3 stars are kept in-app to send feedback, preventing negative public reviews.
- **Cool-down Periods:** Uses `DataStore` to manage "Don't ask again for 48h" logic.

### 📊 Analytics & Monetization
- **Firebase Analytics:** Tracks key events like `upsell_attempt`, `subscription_view`, and funnel completion.
- **AdMob Integration:** Implements Native Ads in the feed and Interstitial Ads for monetization.

## 🛠 Tech Stack

- **Language:** Kotlin (100%)
- **UI Toolkit:** Jetpack Compose (Material3)
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture principles
- **Dependency Injection:** Hilt (Dagger)
- **Asynchronous Programming:** Coroutines & Kotlin Flow
- **Local Data:** - Room Database (SQL)
  - DataStore (Preferences)
- **Background Tasks:** WorkManager (for scheduled notifications)
- **AI & Cloud:** Google Generative AI SDK (Gemini)
- **Network & Analytics:** Firebase Analytics, Google AdMob

## 🏗 Architecture & Design Patterns
The app follows the **MVVM** architecture to ensure separation of concerns and testability:
1.  **Repository Pattern:** Acts as a single source of truth, mediating between the local database (Room) and remote data sources.
2.  **ViewModel:** Manages UI state using `StateFlow` and handles business logic (e.g., calculating totals, processing AI responses).
3.  **UI (Compose):** Reactive UI that observes state changes and renders the screen accordingly.


## 🔧 Setup & Installation

Follow these steps to get the project running on your local machine:

1.  **Clone the repository**
    Open your terminal and run:
    ```bash
    git clone [https://github.com/NirAvraham1/SubTrack.git](https://github.com/NirAvraham1/SubTrack.git)
    ```

2.  **Open in Android Studio**
    * Open Android Studio.
    * Select **Open an existing Android Studio project**.
    * Navigate to the cloned folder.

3.  **Configure API Keys**
    The project uses **Google Gemini AI**. To make it work, you need to add your API key:
    * Open the `local.properties` file in the root directory (if it doesn't exist, create it).
    * Add the following line:
    ```properties
    GEMINI_API_KEY="YOUR_API_KEY_HERE"
    ```

4.  **Build & Run**
    * Wait for Gradle to finish syncing.
    * Select an Emulator or connect a physical Android device.
    * Click the **Run** ▶️ button.

## 👨‍💻 Author
Nir Avraham Computer Science Student @ Afeka College

