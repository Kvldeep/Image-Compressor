# 📸 Image Compressor

A simple and modern Android image compression app built with **Kotlin**. The app allows users to select an image from the gallery or capture one using the camera, adjust the compression level, preview the compressed image, view the size reduction, and save the compressed image to their device.

## ✨ Features

- 🖼️ Pick images from the device gallery
- 📷 Capture images using the camera
- 📏 Display original image dimensions
- 💾 Display original image file size
- 🎚️ Adjustable compression percentage
- 📊 Estimated compressed size and reduction before compression
- ⚡ Compress images using Kotlin and Android Bitmap APIs
- 🖼️ Preview the compressed image
- 📦 Display actual compressed image size and dimensions
- 📉 Calculate actual file-size reduction
- 💾 Save compressed images to the device Gallery/Pictures folder
- 🔄 Uses Coroutines for background image processing
- 🧩 Follows MVVM architecture with Repository Pattern

## 🏗️ Architecture

The project follows a simple **MVVM + Repository Pattern** architecture:

```text
MainActivity
     │
     │ User interaction
     ↓
ViewModel
     │
     │ viewModelScope + Coroutines
     ↓
Repository
     │
     │ Image processing / file operations
     ↓
Compressed Image
     │
     ↓
StateFlow
     │
     ↓
MainActivity
     │
     ↓
Updated UI
```

### Main Components

**MainActivity**
- Handles UI interactions
- Gallery and camera image selection
- Reads the selected image information
- Updates the UI
- Observes compression results
- Handles the save/download action

**ViewModel**
- Acts as the bridge between UI and Repository
- Starts image compression using `viewModelScope`
- Exposes compression results using `StateFlow`

**Repository**
- Reads and decodes the selected image
- Performs image compression
- Creates the compressed file
- Returns the compressed image URI

## 🛠️ Tech Stack

- **Kotlin**
- **Android SDK**
- **XML Layouts**
- **ViewBinding**
- **MVVM Architecture**
- **Repository Pattern**
- **Kotlin Coroutines**
- **StateFlow**
- **Android Bitmap API**
- **MediaStore**
- **Activity Result APIs**
- **FileProvider**

## 📱 How It Works

1. Select an image from the **Gallery** or capture one using the **Camera**.
2. The app displays the original image, dimensions, and file size.
3. Choose the desired compression percentage using the SeekBar.
4. The app displays an estimated compressed size.
5. Press **Compress Image**.
6. The ViewModel starts the compression process using a coroutine.
7. The Repository performs the actual image compression.
8. The compressed image is returned to the ViewModel and exposed through `StateFlow`.
9. The Activity displays the compressed image along with its actual size and dimensions.
10. Press **Download/Save** to save the compressed image to the device.

## 📂 Project Structure

```text
app/
└── src/
    └── main/
        ├── java/
        │   └── com.example.simple_image_compressor/
        │       ├── MainActivity.kt
        │       ├── ImageCompressorViewModel.kt
        │       ├── ImageCompressorViewModelFactory.kt
        │       └── data/
        │           └── repository/
        │               └── ImageRepository.kt
        │
        └── res/
            ├── drawable/
            ├── mipmap/
            ├── layout/
            └── values/
```

## 🚀 Getting Started

### Requirements

- Android Studio
- Kotlin
- Android SDK
- An Android device or emulator

### Run the Project

1. Clone the repository:

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPOSITORY_NAME.git
```

2. Open the project in **Android Studio**.
3. Allow Gradle to sync.
4. Connect an Android device or start an emulator.
5. Run the application.

## 🔐 Permissions

The app uses Android's modern APIs for selecting and capturing images.

Depending on the Android version and implementation:

- Gallery access is handled through the system photo picker.
- Camera access requires camera permission.
- Saving the compressed image uses `MediaStore`.

The app avoids unnecessary legacy storage permissions on modern Android versions.

## 🎯 Learning Goals

This project was built to practice:

- MVVM architecture
- Repository Pattern
- Kotlin Coroutines
- StateFlow
- ViewModel and ViewModel Factory
- Activity Result APIs
- Bitmap image processing
- File handling
- MediaStore
- Android runtime permissions
- Clean separation of UI and business/data logic

## 📌 Future Improvements

- Support for multiple image formats
- Batch image compression
- Better compression-size estimation
- Image resizing alongside compression
- Before/after image comparison
- Compression history
- Share compressed images directly
- More advanced compression algorithms

## 👨‍💻 Author

**Kuldeep Sain**

Built as an Android development project to learn and apply **Kotlin, MVVM, Coroutines, Repository Pattern, and Android image processing**.

---

⭐ If you find this project useful, consider giving the repository a star!
