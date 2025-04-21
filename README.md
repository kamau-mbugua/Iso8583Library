# Iso8583Library

A modular ISO 8583 messaging library for Android, complete with message builders, network transport, transaction management, and a ready‑to‑use facade. Includes a sample app module (`app/`) showing how to integrate and use the library (`iso8583lib/`).

---

## Table of Contents

- [Prerequisites](#prerequisites)  
- [Project Structure](#project-structure)  
- [Setup & Configuration](#setup--configuration)  
  - [Clone the Repository](#clone-the-repository)  
  - [Gradle Plugin Configuration](#gradle-plugin-configuration)  
  - [Version Catalog (`libs.versions.toml`)](#version-catalog-libsversionstoml)  
  - [`settings.gradle`](#settingsgradle)  
- [Integration](#integration)  
  - [Add Module Dependencies](#add-module-dependencies)  
  - [Initialize `Iso8583Core`](#initialize-iso8583core)  
  - [Generating & Sending Messages](#generating--sending-messages)  
- [API Reference](#api-reference)  
- [Flow Overview](#flow-overview)  
- [License](#license)  

---

## Prerequisites

- **Android Studio** Flamingo or later  
- **JDK 11**  
- **Kotlin** 1.9+  
- **Android Gradle Plugin** 8.1+  
- **Gradle** 8+

---

## Project Structure

```
Iso8583Library/
├── app/                    # Sample “consumer” app module
│   ├── build.gradle
│   └── src/main/java/com/rbs/iso8583corelibrary/
│       ├── App.kt          # Application subclass: calls Iso8583Core.init()
│       └── MainActivity.kt # Example usage: generate & send messages
├── iso8583lib/             # Core ISO8583 library
│   ├── build.gradle
│   └── src/main/java/com/rbs/iso8583lib/
│       ├── communication/   # JPOSTCPHandler for TCP transport
│       ├── iso/             # Message builders & pack_iso (IsoMessageUtils)
│       ├── model/           # `Iso8583Message`, `TransactionTypes`, etc.
│       ├── storage/         # Room DB & DAOs (`AppDatabase`, entities)
│       ├── transaction/     # `TransactionManager`
│       ├── utils/           # Prefs, LoggerInit, packager provider, helpers
│       └── expose/          # `Iso8583Core` facade object
├── gradle/                  # Gradle wrapper & version catalog support
├── build.gradle             # Root Gradle config
├── settings.gradle          # Includes modules & pluginManagement
├── gradle.properties
├── gradle/libs.versions.toml# Version catalog
└── README.md
```

---

## Setup & Configuration

### Clone the Repository

```bash
git clone https://github.com/kamau-mbugua/Iso8583Library.git
cd Iso8583Library
```

### Gradle Plugin Configuration

**Avoid** specifying plugin versions in your module `plugins { … }` blocks. Instead, declare them once in `settings.gradle`:

```groovy
// settings.gradle
pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
  plugins {
    id 'com.android.application'    version '8.1.1'
    id 'com.android.library'        version '8.1.1'
    id 'org.jetbrains.kotlin.android' version '1.9.21'
  }
}

rootProject.name = "Iso8583Library"
include(":app", ":iso8583lib")
```

Then in **module** `build.gradle` files simply write:

```groovy
// app/build.gradle
plugins {
  id 'com.android.application'
  id 'org.jetbrains.kotlin.android'
}

// iso8583lib/build.gradle
plugins {
  id 'com.android.library'
  id 'org.jetbrains.kotlin.android'
  id 'kotlin-kapt'
}
```

### Version Catalog (`libs.versions.toml`)

Centralize all dependency and plugin versions here:

```toml
[versions]
agp       = "8.1.1"
kotlin    = "1.9.21"
jpos      = "2.1.10"
gson      = "2.10.1"
xercesimpl= "2.12.1"
# ...etc.

[plugins]
application       = { id = "com.android.application",    version.ref = "agp" }
library           = { id = "com.android.library",       version.ref = "agp" }
kotlin-android    = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }

[libraries]
jpos            = { module = "org.jpos:jpos",               version.ref = "jpos" }
xercesimpl      = { module = "xerces:xercesImpl",         version.ref = "xercesimpl" }
gson            = { module = "com.google.code.gson:gson", version.ref = "gson" }
# ...etc.
```

### `settings.gradle`

```groovy
pluginManagement {
  repositories { gradlePluginPortal(); google(); mavenCentral() }
  plugins {
    id 'com.android.application'    version '8.1.1'
    id 'com.android.library'        version '8.1.1'
    id 'org.jetbrains.kotlin.android' version '1.9.21'
  }
}
rootProject.name = "Iso8583Library"
include(":app", ":iso8583lib")
```

---

## Integration

### Add Module Dependencies

In your **app** (`app/build.gradle`):

```groovy
dependencies {
  // Pull in the core ISO8583 library
  implementation project(":iso8583lib")
  // ... your other dependencies
}
```

### Initialize `Iso8583Core`

Create an `Application` subclass (`App.kt`):

```kotlin
package com.rbs.iso8583corelibrary

import android.app.Application
import com.rbs.iso8583lib.expose.Iso8583Core

class App : Application() {
  override fun onCreate() {
    super.onCreate()
    Iso8583Core.init(
      app           = this,
      debug         = BuildConfig.DEBUG,
      connectionIp  = "196.13.200.254",
      connectionPort= "3010"
    )
  }
}
```

Register it in **`AndroidManifest.xml`**:

```xml
<application
    android:name=".App"
    ...>
  ...
</application>
```

### Generating & Sending Messages

In any Activity or ViewModel:

```kotlin
import com.rbs.iso8583lib.expose.Iso8583Core
import com.rbs.iso8583lib.iso.generateSignOnRequest
import com.rbs.iso8583lib.iso.createPurchaseRequests
import com.rbs.iso8583lib.communication.CardOutputData

// 1) Build your payload:
val signOnMsg    = generateSignOnRequest()
val purchaseMsg  = createPurchaseRequests(cardOutputData)

// 2) Send it:
Iso8583Core.sendTransaction(signOnMsg)
// or
Iso8583Core.sendTransaction(purchaseMsg)
```

All packing, TCP send/receive, database persistence, retries, and callbacks are managed for you.

---

## API Reference

### `object Iso8583Core`

| Method                                            | Description                                                   |
|---------------------------------------------------|---------------------------------------------------------------|
| `init(app: Application, debug: Boolean, connectionPort: String, connectionIp: String)` | Initialize DB, prefs, packager, JPOS handler & transaction manager |
| `sendTransaction(message: Iso8583Message)`         | Pack & send an ISO8583 request                                |
| `getTransactionManager(): TransactionManager`      | Access the underlying transaction manager                     |
| `getRepository(): TransactionRepository`           | Access the Room‑backed repository                             |
| `getViewModel(): TciViewModel`                     | Access the ViewModel for observing transaction status/events  |

### Message Builders (`com.rbs.iso8583lib.iso`)

- `generateSignOnRequest()`  
- `createPurchaseRequests(...)`  
- `createBalanceInquiryRequest(...)`  
- `createRefundRequest(...)`  
- `createVoidCardPresentRequest(...)`  
- `createMerchantBatchUploadRequest(...)`  
- …and many more. See the **`iso`** package for full list.

---

## Flow Overview

1. **App Start** → `Iso8583Core.init()`  
2. **User Action** → call a generator (e.g. `generateSignOnRequest()`)  
3. **Send** → `Iso8583Core.sendTransaction(msg)`  
4. **Pack & Transmit** → `IsoMessageUtils` + `JPOSTCPHandler` (JPOS)  
5. **Receive** → handler feeds `TransactionManager.handleResponse(...)`  
6. **Persist** → Room saves request/response, ViewModel emits events  
7. **UI Update** → observe `TciViewModel` for success/failure  

---

## License

This project is released under the **MIT License**. See [LICENSE](LICENSE) for details.
