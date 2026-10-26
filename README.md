# Assistive Button & Volume Control (Android 8.1+)

An Android utility app built specifically for devices with broken physical power and volume buttons (e.g. Xolo Era 5X, Android 8.1 Oreo).

## Features
- **Floating Assistive Bubble**: A draggable on-screen button with quick access to:
  - 🔊 Volume Up
  - 🔉 Volume Down
  - 🔒 Lock Screen
  - ⚡ Power Menu (Power off / Restart)
- **Notification Bar Controls**: Always-accessible buttons in the notification drawer.
- **Volume Sliders**: Direct sliders for Media, Ringtone, and Call audio.
- **Android 8.1 Device Administrator**: Allows one-tap screen turn off & lock without needing a working power button.
- **Accessibility Service**: Triggers the system Power Off / Restart dialog.

---

## How to Build the APK on GitHub (Free & Automatic)

Follow these simple steps:

### 1. Initialize Git and Commit
Open Terminal in this folder (`/Users/malu/Downloads/My apps`) and run:
```bash
git init
git add .
git commit -m "Initial commit for Assistive Button app"
```

### 2. Create a new GitHub Repository
1. Go to [github.com/new](https://github.com/new).
2. Name it `assistive-touch` (can be **Public** or **Private**).
3. Do **not** check "Initialize with README". Click **Create repository**.

### 3. Push the Code
Copy the commands shown on GitHub (or run):
```bash
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/assistive-touch.git
git push -u origin main
```

### 4. Download Your APK
1. Go to your repository on GitHub and click on the **Actions** tab.
2. You will see the **Build Android APK** workflow running.
3. Once completed (usually 1–2 minutes, marked with a green checkmark `✓`), click on the workflow run.
4. Scroll to the bottom under **Artifacts** and click **AssistiveTouch-Debug-APK** to download your ready-to-install `.apk`!

---

## How to Install and Set Up on Your Phone
1. Transfer or download the `.apk` on your phone.
2. Tap the file to install it (enable "Install from unknown sources" if prompted).
3. Open the **Assistive Button** app.
4. Grant the 3 permissions shown on the screen:
   - **Floating Overlay**: Allows the floating on-screen bubble.
   - **Accessibility Service**: Enables the Power Menu (Power Off / Restart dialog).
   - **Device Admin (Screen Lock)**: Enables one-tap screen turn off & lock on Android 8.1.
5. Turn on the **Floating Screen Button** and/or **Notification Shade Controls**.
