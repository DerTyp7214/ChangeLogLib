package com.dertyp7214.changeloglibexampleapp

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast

import com.dertyp7214.changelogs.ChangeLog
import com.dertyp7214.changelogs.Version

class MainActivity : Activity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val changeLog = ChangeLog.Builder(this)
                .addVersion(Version.Builder(this)
                        .setVersionName("2")
                        .setVersionCode("200")
                        .addChange(Version.Change(Version.Change.ChangeType.FIX, ChangeLog.generateLink("Fix stuff", "http://www.google.de/search?q=Fix%20stuff")))
                        .addChange(Version.Change(Version.Change.ChangeType.ADD, "Add stuff"))
                        .build())
                .addVersion(Version.Builder(this)
                        .setVersionName("1")
                        .setVersionCode("100")
                        .addChange(Version.Change(Version.Change.ChangeType.FIX, "Fix stuff"))
                        .addChange(Version.Change(Version.Change.ChangeType.ADD, "Add stuff"))
                        .build())
                .setLinkColor(Color.RED)
                .addCloseListener {
                    Toast.makeText(this, "CLOSE", Toast.LENGTH_LONG).show()
                }
                .build()
        changeLog.buildDialog("Changes").showDialog()

        val changeLogFromText = ChangeLog.Builder(this)
                .buildFromText("""{title: "Test", linkColor: ${Color.RED}, versions: [
                    {versionCode: "200", versionName: "2", changes: [
                           {type: "fix", change: "${ChangeLog.generateLink("Fix stuff", "http://www.google.de/search?q=Fix%20stuff")}"},
                           {type: "ADD", change: "Add stuff"}]},
                    {versionCode: "100", versionName: "1", changes: [
                           {type: "FIX", change: "Fix stuff"},
                           {type: "add", change: "Add stuff"}]}]}""".trimMargin())
    }
}
