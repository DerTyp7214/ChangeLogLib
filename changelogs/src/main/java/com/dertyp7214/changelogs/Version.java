package com.dertyp7214.changelogs;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Version {

    private final Context context;
    private final String versionName, versionCode;
    private final List<Change> changes;

    private Version(String versionName, String versionCode, List<Change> changes, Context context) {
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.changes = changes;
        this.context = context;
    }

    public String getHtml() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<h3>").append(context.getString(R.string.version)).append(" ")
                .append(versionName);
        if (! versionCode.equals("")) stringBuilder.append(" (").append(versionCode).append(")");
        stringBuilder.append("</h3>\n\n");
        stringBuilder.append("<ol>\n");

        for (Change change : changes)
            stringBuilder.append("<li><b>").append(mapChangeType(change.type)).append(": </b>")
                    .append(change.change).append("</li>\n");

        stringBuilder.append("</ol>\n\n");
        return stringBuilder.toString();
    }

    private String mapChangeType(Change.ChangeType changeType) {
        switch (changeType) {
            case ADD:
                return context.getString(R.string.add);
            case FIX:
                return context.getString(R.string.fix);
            case REMOVE:
                return context.getString(R.string.remove);
            case IMPROVEMENT:
                return context.getString(R.string.improvemnt);
        }
        return "";
    }

    public static class Builder {
        private final Context context;

        private List<Change> changeList = new ArrayList<>();
        private String versionName = "";
        private String versionCode = "";

        public Builder(Context context) {
            this.context = context;
        }

        public Builder addChange(Change change) {
            changeList.add(change);
            return this;
        }

        public Builder setVersionName(String versionName) {
            this.versionName = versionName;
            return this;
        }

        public Builder setVersionCode(String versionCode) {
            this.versionCode = versionCode;
            return this;
        }

        public Version build() {
            return new Version(versionName, versionCode, changeList, context);
        }
    }

    public static class Change {
        private final ChangeType type;
        private final String change;

        public Change(ChangeType type, String change) {
            this.type = type;
            this.change = change;
        }

        public enum ChangeType {
            FIX,
            ADD,
            IMPROVEMENT,
            REMOVE
        }
    }
}
