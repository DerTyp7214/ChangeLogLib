package com.dertyp7214.changeloglibexampleapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dertyp7214.changelogs.ChangeLog;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChangeLog changeLog = new ChangeLog.Builder(this)
                .addCloseListener(new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        Toast.makeText(Test.this, "CLOSE", Toast.LENGTH_LONG).show();
                        return null;
                    }
                }).build();
        changeLog.buildDialog().showDialogOnVersionChange();
    }
}
