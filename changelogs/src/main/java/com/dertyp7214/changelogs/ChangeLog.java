package com.dertyp7214.changelogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

public class ChangeLog {

    private final Context context;
    private final List<Version> versions;
    private String linkColor;
    private MaterialDialog dialog;

    private ChangeLog(Context context, List<Version> versions, String linkColor) {
        this.context = context;
        this.versions = versions;
        this.linkColor = linkColor;
    }

    private String buildHtml() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<html>\n<head>\n<style type=\"text/css\">\n")
                .append("* {\nword-wrap: break-word;\n}\na {\ncolor: #").append(linkColor)
                .append(";\n}\nol {\n")
                .append("list-style-position: inside;\npadding-left: 0;\npadding-right: 0;\n}\n")
                .append("li {\npadding-top: 8px;\n}\n</style>\n</head>\n<body>\n\n");

        for (Version version : versions)
            stringBuilder.append(version.getHtml());

        stringBuilder.append("</body>\n</html>");
        return stringBuilder.toString();
    }

    public ChangeLog buildDialog() {
        return buildDialog(context.getString(R.string.text_changes));
    }

    @SuppressLint({"InflateParams", "SetJavaScriptEnabled"})
    public ChangeLog buildDialog(String title) {
        View customView = LayoutInflater.from(context).inflate(R.layout.dialog_web_view, null);
        String mime = "text/html";
        String encoding = "utf-8";

        WebView webView = customView.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL(null, buildHtml(), mime, encoding, null);
        dialog = new MaterialDialog.Builder(context)
                .title(title)
                .customView(customView, false)
                .positiveText(android.R.string.ok)
                .build();
        return this;
    }

    public void showDialog() {
        if (dialog != null) dialog.show();
    }

    public static class Builder {
        private final Context context;

        private List<Version> versions = new ArrayList<>();
        private String linkColor = "FFFFFF";

        public Builder(Context context) {
            this.context = context;
        }

        public Builder addVersion(Version version) {
            versions.add(version);
            return this;
        }

        public Builder setLinkColor(@ColorInt int color) {
            this.linkColor = String.format("%06X", (0xFFFFFF & color));
            return this;
        }

        public ChangeLog build() {
            return new ChangeLog(context, versions, linkColor);
        }
    }
}
