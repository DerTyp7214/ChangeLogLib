[![](https://jitpack.io/v/DerTyp7214/ChangeLogLib.svg)](https://jitpack.io/#DerTyp7214/ChangeLogLib)

### Requirements

You need mdc (`com.google.android.material:material`)
Your theme have to extend from `Theme.Material3`

## To import the lib

### 1. Add it in your root build.gradle at the end of repositories
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. Add the dependency:
```gradle
dependencies {
    implementation 'com.github.DerTyp7214:ChangeLogLib:<LATEST_VERSION>'
}
```

## Usage:

```kotlin
val changelog = Changelog(this) {
    addVersion {
        versionName = "3"
        versionCode = "300"

        addChange {
            type = add()
                change = "Test"
            }
            addChange {
            type = fix()
            addLink("Test Link", "https://google.com")
            }
        }
    addVersion {
        versionName = "2"
        versionCode = "200"

        addChange {
            type = fix()
            change = "Fixed stuff"
        }
    }
    onClose {
        Log.d("ButtonId", "$it")
    }
}

changelog.show()
```
