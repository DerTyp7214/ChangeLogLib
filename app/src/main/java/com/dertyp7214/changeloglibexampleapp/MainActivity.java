package com.dertyp7214.changeloglibexampleapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.dertyp7214.changelogs.ChangeLog;
import com.dertyp7214.changelogs.Version;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    }
}
