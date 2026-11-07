# PingMe Messenger 💬📱

PingMe is an ultra-premium, full-featured real-time instant messaging and calling application engineered for Android 15. Built using **Clean Architecture** patterns under the strict **MVVM (Model-View-ViewModel)** design philosophy, the app leverages **Jetpack Compose (Material 3)** to deliver a fluid, high-fidelity user experience resembling modern platforms like Signal or WhatsApp.

---

## 📸 Premium UI Aesthetic
The app features a custom wireframe-matched design utilizing an elegant luxury **WhatsApp Green & Light Palette**. 
- Fully responsive **Dynamic Top App Bar** with real-time contact filtering search bar and bottom navigation tabs (Chats, Updates/Status, Calls).
- Sleek interactive **Material Card Layouts** mapping active user states, active contact indicators, and custom avatar profiles.
- Clean asymmetrical **Chat Bubbles** mapping clear text typography, double-tick delivery status, and structural media frames.

---

## 🚀 Key Features & Industrial Workarounds

- **Secure OTP Authentication Engine:** Full deployment of Firebase Phone Authentication supporting regional SMS policies, country-code matching (`+91`), dynamic loading animations, and automated credential listener handshakes.
- **Reactive Real-Time Messaging:** Instant one-to-one text messaging orchestrated entirely over custom Cloud Firestore reactive listeners (`.addSnapshotListener`), bypassing expensive long-polling setups.
- **Paywall-Bypassed Media Sharing:** Avoids strict cloud storage paywall tiers (Firebase Blaze upgrade requirements) by executing an elegant client-side **Base64 image conversion pipeline**. Photos from the gallery launcher are compressed via native Bitmaps down to 30% resolution, encoded into textual streams, dispatched safely into Firestore, and seamlessly reconstructed into custom pixel-perfect UI image view blocks on destination screens.
- **Cost-Free Heads-Up Push Alerts:** Bypasses the deprecated legacy FCM Server Keys and costly server functions through a custom **Reactive Database Notifications Stream**. Incoming texts instantly push tiny payloads to a temporary Firestore hub, which target devices pick up dynamically to trigger system heads-up notifications, completely free of charge.
- **High-Fidelity Audio & Video Calling:** End-to-end full-duplex calling channels wired up via the **Agora RTC SDK**, allowing instant cross-device connections with custom ring overlay dialog boxes (Accept / Decline actions) and one-click floating disconnect consoles.

---

## 🛡️ Target SDK 37 & Android 15 Memory Optimizations
The application is fully tailored for the newly strict security and performance requirements of Android 15:
- **16 KB Memory Page Size Alignment:** Packaged explicitly using Agora Core native library binaries, aligning deep internal `.so` segments directly on 16 KB hardware boundaries to prevent dynamic runtime architecture crashes on flagship Android 15 devices.
- **Runtime Notification Handlers:** Enforces proactive API 33+ `POST_NOTIFICATIONS` asynchronous runtime alert permissions checks right inside the central navigation router.

---

## 🏗️ Technical Architecture & Directory Blueprint

The project follows a strict three-tier layer isolation ensuring testability, maintainability, and clean decoupling:

```text
com.portfolio.pingme
│
├── 📁 data            # Data Layer: Network models, repositories execution layer, Firestore nodes mapping
│   └── 📁 repository
│
├── 📁 domain          # Domain Layer (Pure Business Logic): Structural interfaces, core data blueprints
│   ├── 📁 model       # User.kt, Message.kt entities
│   └── 📁 repository
│
├── 📁 di              # Dependency Injection Layer: Strict Dagger-Hilt modules binding interface routing abstractions
│
├── 📁 service         # Background Services Layer: Processing long-running background tasks
│
├── 📁 ui              # Presentation UI Layer: Pure Jetpack Compose views and stateful ViewModels
│   ├── 📁 auth        # LoginScreen, ProfileSetupScreen UI components
│   ├── 📁 chat        # WhatsAppMainScreen, UsersListScreen, ChatScreen, StatusScreen, CallsScreen
│   └── 📁 theme       # Material 3 WhatsApp color palettes and type definitions
│
└── 📁 util            # Utility Infrastructure: Image processing pipelines, calling controls wrapper
```

---

## 🛠️ Complete Tech Stack
- **Core Programming Language:** Kotlin 2.x (with integrated lightweight reactive compilers)
- **UI Architecture Engine:** Jetpack Compose (Material 3 standard)
- **Compilation Processing System:** KSP (Kotlin Symbol Processing) - replaced deprecated Kapt pipelines for 2x faster build speeds.
- **Dependency Management Provider:** Dagger-Hilt (Scoped singletons injection layout)
- **Database Backend Providers:** Cloud Firestore, Firebase Auth, Firebase Messaging Extensions
- **Network Dispatch Engine:** OkHttp3 & native background streams orchestration

---

## 📲 How to Run and Test
1. Clone the repository to your local directory machine.
2. In Android Studio, sync the project using Gradle.
3. Add your own `google-services.json` file inside the `app/` folder directory.
4. Replace the `APP_ID` field placeholder within `com.portfolio.pingme.util.CallManager` with your unique Agora Console Testing App ID string.
5. Deploy the application to two separate devices (or an emulator and a real device) using separate phone numbers to experience the live real-time bidirectional chatting and call streaming capabilities.
