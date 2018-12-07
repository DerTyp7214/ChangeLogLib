@file:Suppress("NAME_SHADOWING")

package com.dertyp7214.changelogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.view.LayoutInflater
import android.webkit.WebView
import com.afollestad.materialdialogs.MaterialDialog
import org.json.JSONArray
import org.json.JSONObject

class ChangeLog private constructor(private val context: Context, private val versions: List<Version>, private val linkColor: String, private val closeListeners: ArrayList<() -> Unit>) {
    private var dialog: MaterialDialog? = null

    private fun buildHtml(): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append("<html>\n<head>\n<style type=\"text/css\">\n")
                .append("* {\nword-wrap: break-word;\n}\na {\ncolor: #").append(linkColor)
                .append(";\n}\nol {\n")
                .append("list-style-position: inside;\npadding-left: 0;\npadding-right: 0;\n}\n")
                .append("li {\npadding-top: 8px;\n}\n</style>\n</head>\n<body>\n\n")

        for (version in versions)
            stringBuilder.append(version.html)

        stringBuilder.append("</body>\n</html>")
        return stringBuilder.toString()
    }

    fun buildDialog(): ChangeLog {
        return buildDialog(context.getString(R.string.text_changes))
    }

    @SuppressLint("InflateParams", "SetJavaScriptEnabled")
    fun buildDialog(title: String): ChangeLog {
        val customView = LayoutInflater.from(context).inflate(R.layout.dialog_web_view, null)
        val mime = "text/html"
        val encoding = "utf-8"

        val webView = customView.findViewById<WebView>(R.id.web_view)
        webView.settings.javaScriptEnabled = true
        webView.loadDataWithBaseURL(null, buildHtml(), mime, encoding, null)
        dialog = MaterialDialog.Builder(context)
                .title(title)
                .customView(customView, false)
                .positiveText(android.R.string.ok)
                .dismissListener {
                    closeListeners.forEach { it() }
                }
                .build()
        return this
    }

    fun showDialog() {
        if (dialog != null) dialog!!.show()
    }

    fun showDialogOnVersionChange(): Boolean {
        val sharedPreferences = context.getSharedPreferences("changeLogDialogCache", Context.MODE_PRIVATE)
        val lastVersionName = sharedPreferences.getString("versionName", "null")
        val lastVersionCode = sharedPreferences.getInt("versionCode", -1)
        val versionCode = getBuildConfigValue(context, "VERSION_CODE") as Int
        val versionName = getBuildConfigValue(context, "VERSION_NAME") as String

        val ret = if (lastVersionName == "null" && lastVersionCode == -1) {
            showDialog()
            true
        } else if (lastVersionName != versionName || lastVersionCode < versionCode) {
            showDialog()
            true
        } else false

        sharedPreferences.edit()
                .putInt("versionCode", versionCode)
                .putString("versionName", versionName)
                .apply()

        return ret
    }

    private fun getBuildConfigValue(context: Context, fieldName: String): Any? {
        return try {
            val clazz = Class.forName("${if (context.packageName.endsWith(".debug")) context.packageName.replace(".debug", "") else context.packageName}.BuildConfig")
            val field = clazz.getField(fieldName)
            field.get(null)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    companion object {
        fun generateLink(text: String, url: String): String {
            return """<a href=\"$url\">$text</a>"""
        }
    }

    class Builder(private val context: Context) {

        private val versions = ArrayList<Version>()
        private val listeners = ArrayList<() -> Unit>()
        private var linkColor = "FFFFFF"

        fun addVersion(version: Version): Builder {
            versions.add(version)
            return this
        }

        fun addCloseListener(closeListener: () -> Unit): Builder {
            listeners.add(closeListener)
            return this
        }

        fun setLinkColor(@ColorInt color: Int): Builder {
            this.linkColor = String.format("%06X", 0xFFFFFF and color)
            return this
        }

        fun build(): ChangeLog {
            return ChangeLog(context, versions, linkColor, listeners)
        }

        fun buildFromText(jsonChangeLogObject: String): ChangeLog {
            val changeLog = ChangeLog.Builder(context)
            val changeLogObject = JSONObject(jsonChangeLogObject)
            val array = if (changeLogObject.has("versions")) changeLogObject.getJSONArray("versions") else JSONArray("[]")

            changeLog.setLinkColor(if (changeLogObject.has("linkColor")) changeLogObject.getInt("linkColor") else Color.RED)

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val version = Version.Builder(context)
                version.setVersionCode(if (obj.has("versionCode")) obj.getString("versionCode") else "")
                version.setVersionName(if (obj.has("versionName")) obj.getString("versionName") else "")

                val changes = if (obj.has("changes")) obj.getJSONArray("changes") else JSONArray("[]")

                for (y in 0 until changes.length()) {
                    val obj = changes.getJSONObject(y)
                    val change = Version.Change(getTypeByString(if (obj.has("type")) obj.getString("type") else "")
                            ?: Version.Change.ChangeType.FIX, if (obj.has("change")) obj.getString("change") else "")
                    version.addChange(change)
                }

                changeLog.addVersion(version.build())
            }

            return changeLog.build().buildDialog(if (changeLogObject.has("title")) changeLogObject.getString("title") else "")
        }

        private fun getTypeByString(type: String): Version.Change.ChangeType? {
            return when (type.toLowerCase()) {
                Version.Change.ChangeType.FIX.name.toLowerCase() -> Version.Change.ChangeType.FIX
                Version.Change.ChangeType.ADD.name.toLowerCase() -> Version.Change.ChangeType.ADD
                Version.Change.ChangeType.IMPROVEMENT.name.toLowerCase() -> Version.Change.ChangeType.IMPROVEMENT
                Version.Change.ChangeType.REMOVE.name.toLowerCase() -> Version.Change.ChangeType.REMOVE
                else -> null
            }
        }
    }
}
