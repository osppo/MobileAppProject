# Remaining things for your mobile app project

## 1. Multi-language support Arabic/English

Required by the guide.

You still need:

```text
Language switch button/screen
Arabic translations
English translations
All labels/buttons/titles/messages connected to language state
Switch language from inside the app, not phone settings
```

Current status:

```text
SettingsScreen exists, but it does not actually switch language yet.
```

---

## 2. Splash screen

Required by the guide.

You still need:

```text
Splash screen with app logo/name
5-second timer
Automatic navigation to main screen
Smooth transition
```

Current status:

```text
Missing.
```

---

## 3. Edit/update note feature

Required because the guide requires SQLite `Update`.

You already have database functions:

```kotlin
updateNormalNote()
updateQaNote()
```

But you still need UI:

```text
Edit button
Open editor with existing note data
Save updated note
Update normal notes
Update Q&A notes
```

Current status:

```text
Database update exists, but user cannot update from UI yet.
```

---

## 4. Delete confirmation dialog

Not directly listed as a separate requirement, but important for UI/UX.

You should add:

```text
AlertDialog before deleting a note
Confirm button
Cancel button
```

Current status:

```text
Delete happens immediately from trash icon.
```

---

## 5. Make sure LazyColumn shows at least 10 items

Required by the guide.

You need to test/demo:

```text
At least 10 notes saved and shown in the list
Smooth scrolling
Data loaded from SQLite
```

Current status:

```text
LazyColumn exists, but final demo must clearly show 10+ notes.
```

---

## 6. Improve database error handling

Required by the guide.

You should add simple checks:

```text
If insert fails, show error message
If update fails, show error message
If delete fails, do not silently fail
```

Example:

```kotlin
val result = dbHelper.insertNormalNote(...)
if (result == -1L) {
    errorMessage = "Could not save note."
}
```

Current status:

```text
Basic form validation exists, but database failure handling is weak.
```

---

## 7. Clean file structure/code organization

Required under code quality.

Current issue:

```text
PlaceholderScreens.kt contains many real screens.
```

Better final structure:

```text
MainActivity.kt
Data.kt
NotesDatabaseHelper.kt
NotesApp.kt
Language.kt

screens/
    MainNotesScreen.kt
    CreateNoteTypeScreen.kt
    NormalNoteEditorScreen.kt
    QaNoteEditorScreen.kt
    NoteDetailScreen.kt
    PracticeScreen.kt
    SettingsScreen.kt
    SplashScreen.kt
```

Current status:

```text
Works, but not clean enough for final quality.
```

---

## 8. Decide what to do with PracticeScreen

Current situation:

```text
Q&A practice logic is inside NoteDetailScreen.
PracticeScreen is still mostly placeholder.
```

You have two choices:

```text
Option A: Keep Q&A practice inside NoteDetailScreen and remove/ignore PracticeScreen.
Option B: Move Q&A practice into PracticeScreen and navigate there from Q&A notes.
```

Best for clarity:

```text
Option B
```

Fastest:

```text
Option A
```

---

## 9. UI polish

For better UI/UX grade, improve:

```text
Top bars on all screens
Back buttons
Clear spacing
Consistent button styles
Readable Arabic/English layout
Better note cards
Score display
Empty-state messages
```

Current status:

```text
Functional but still basic.
```

---

## 10. Final report, screenshots, and submission ZIP

Required by the guide.

You still need:

```text
Brief report explaining implementation
Screenshots of running app
Source code
Database/assets
PDF containing proposal + report + screenshots
ZIP/RAR folder
Folder name: GroupX_MobileAppProject
```

Current status:

```text
Not done yet.
```

---

# Priority order

Do them in this order:

```text
1. Multi-language support
2. Splash screen
3. Edit/update UI
4. Delete confirmation dialog
5. Ensure 10+ notes in LazyColumn
6. Improve database error handling
7. Clean file structure
8. Decide PracticeScreen structure
9. UI polish
10. Final report + screenshots + ZIP
```

# Minimum remaining requirements to pass

If time is short, focus on:

```text
1. Arabic/English switch
2. Splash screen
3. Edit/update UI
4. 10+ notes shown in LazyColumn
5. Final PDF/screenshots/ZIP
```
