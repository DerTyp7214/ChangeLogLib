package com.dertyp7214.changeloglibexampleapp

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.dertyp7214.changelogs.Changelog

class MainActivity : Activity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                Log.d("REEEE", "$it")
            }
        }

        if (changelog.showDialogOnVersionChange())
            Log.d("DIALOGSHOWN", "TRUE")
        else {
            Log.d("DIALOGSHOWN", "FALSE")
            changelog.show()
        }
    }
}
