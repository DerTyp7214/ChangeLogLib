package com.dertyp7214.changelogs

import android.content.Context
import java.util.*

class Version private constructor(private val versionName: String, private val versionCode: String, private val changes: List<Change>, private val context: Context) {

    val html: String
        get() {
            val stringBuilder = StringBuilder()

            stringBuilder.append("<h3>").append(context.getString(R.string.version)).append(" ")
                    .append(versionName)
            if (versionCode != "") stringBuilder.append(" (").append(versionCode).append(")")
            stringBuilder.append("</h3>\n\n")
            stringBuilder.append("<ol>\n")

            for (change in changes)
                stringBuilder.append("<li><b>").append(mapChangeType(change.type)).append(": </b>")
                        .append(change.change).append("</li>\n")

            stringBuilder.append("</ol>\n\n")
            return stringBuilder.toString()
        }

    private fun mapChangeType(changeType: Change.ChangeType): String {
        return when (changeType) {
            Version.Change.ChangeType.ADD -> context.getString(R.string.add)
            Version.Change.ChangeType.FIX -> context.getString(R.string.fix)
            Version.Change.ChangeType.REMOVE -> context.getString(R.string.remove)
            Version.Change.ChangeType.IMPROVEMENT -> context.getString(R.string.improvemnt)
        }
    }

    class Builder(private val context: Context) {

        private val changeList = ArrayList<Change>()
        private var versionName = ""
        private var versionCode = ""

        fun addChange(change: Change): Builder {
            changeList.add(change)
            return this
        }

        fun setVersionName(versionName: String): Builder {
            this.versionName = versionName
            return this
        }

        fun setVersionCode(versionCode: String): Builder {
            this.versionCode = versionCode
            return this
        }

        fun build(): Version {
            return Version(versionName, versionCode, changeList, context)
        }
    }

    class Change(internal val type: ChangeType, internal val change: String) {

        enum class ChangeType {
            FIX,
            ADD,
            IMPROVEMENT,
            REMOVE
        }
    }
}
