[![](https://jitpack.io/v/DerTyp7214/ChangeLogLib.svg)](https://jitpack.io/#DerTyp7214/ChangeLogLib)

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
```gralde
dependencies {
	        implementation 'com.github.DerTyp7214:ChangeLogLib:v1.1'
	}
```

## Usage:

```java
ChangeLog changeLog = new ChangeLog.Builder(this)
                .addVersion(new Version.Builder(this)
                        .setVersionName("2.3")
                        .setVersionCode("234")
                        .addChange(new Version.Change(Version.Change.ChangeType.REMOVE, "Something"))
                        .build())
                .addVersion(new Version.Builder(this)
                        .setVersionName("2.2")
                        .setVersionCode("223")
                        .addChange(new Version.Change(Version.Change.ChangeType.ADD, "Something"))
                        .build())
                .setLinkColor(Color.GREEN)
                .build();
        changeLog.buildDialog("Changes").showDialog();
```
