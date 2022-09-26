package com.dertyp7214.changelogs

import android.content.Context

class Version private constructor(
    val changes: List<Change>,
    val versionName: String,
    val versionCode: String
) {

    companion object {
        operator fun invoke(block: VersionBuilder.() -> Unit) =
            VersionBuilder().also(block).build()
    }

    class VersionBuilder internal constructor() {
        private val changes = arrayListOf<Change>()

        var versionName: String = ""
        var versionCode: String = ""

        fun addChange(block: Change.ChangeBuilder.() -> Unit) =
            changes.add(Change(block))

        internal fun build() = Version(changes, versionName, versionCode)
    }

    class Change private constructor(
        val type: ChangeType,
        val change: CharSequence,
        internal val url: String?
    ) {

        companion object {
            operator fun invoke(block: ChangeBuilder.() -> Unit) =
                ChangeBuilder().also(block).build()
        }

        @Suppress("unused")
        class ChangeBuilder internal constructor() {
            var type: ChangeType = ChangeType.ADD
            var change: CharSequence = ""

            private var url: String? = null

            fun addLink(text: String, url: String) {
                change = text
                this.url = url
            }

            fun add() = ChangeType.ADD
            fun fix() = ChangeType.FIX
            fun improvement() = ChangeType.IMPROVEMENT
            fun remove() = ChangeType.REMOVE

            internal fun build() = Change(type, change, url)
        }

        enum class ChangeType {
            FIX,
            ADD,
            IMPROVEMENT,
            REMOVE;

            fun getName(context: Context): String {
                return when (this) {
                    ADD -> context.getString(R.string.add)
                    FIX -> context.getString(R.string.fix)
                    REMOVE -> context.getString(R.string.remove)
                    IMPROVEMENT -> context.getString(R.string.improvement)
                }
            }
        }
    }
}