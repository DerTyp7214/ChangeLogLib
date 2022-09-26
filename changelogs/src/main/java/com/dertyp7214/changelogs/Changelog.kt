@file:Suppress("unused")

package com.dertyp7214.changelogs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.graphics.Typeface.BOLD
import android.net.Uri
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.LayoutParams
import android.widget.LinearLayout.VERTICAL
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.edit
import com.dertyp7214.changelogs.core.dpToPxRounded
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Suppress("MemberVisibilityCanBePrivate")
class Changelog private constructor(
    private val context: Context,
    private val versions: List<Version>,
    private val closeListener: (which: Int) -> Unit
) {
    companion object {
        operator fun invoke(context: Context, block: ChangelogBuilder.() -> Unit) =
            ChangelogBuilder(context).also(block).build()
    }

    @SuppressLint("SetTextI18n")
    fun show(
        customUrlHandler: (url: String) -> Unit = {
            context.startActivity(Intent(ACTION_VIEW, Uri.parse(it)))
        }
    ) = MaterialAlertDialogBuilder(context)
        .setTitle(R.string.text_changes)
        .setCancelable(false)
        .setView(ScrollView(context).apply {
            isVerticalFadingEdgeEnabled = true
            setFadingEdgeLength(150)
            addView(LinearLayout(context).apply {
                val paddings = 32.dpToPxRounded(context)
                val top = 4.dpToPxRounded(context)
                orientation = VERTICAL
                layoutParams = LayoutParams(
                    MATCH_PARENT,
                    WRAP_CONTENT
                )
                setPadding(paddings, paddings / 4, paddings, paddings / 4)

                versions.forEach { version ->
                    addView(LinearLayout(context).apply {
                        orientation = VERTICAL
                        layoutParams = LayoutParams(
                            MATCH_PARENT,
                            WRAP_CONTENT
                        )
                        setPadding(0, top * 2, 0, 0)

                        addView(
                            TextView(context).apply {
                                appearance(context, R.style.TextAppearance_Material3_TitleMedium)
                                text =
                                    "${version.versionName} ${if (version.versionCode.isNotBlank()) "(${version.versionCode})" else ""}"
                            })
                        addView(LinearLayout(context).apply {
                            orientation = VERTICAL
                            layoutParams = LayoutParams(
                                MATCH_PARENT,
                                WRAP_CONTENT
                            )

                            version.changes.forEach { change ->
                                addView(LinearLayout(context).apply {
                                    orientation = HORIZONTAL
                                    layoutParams = LayoutParams(
                                        MATCH_PARENT,
                                        WRAP_CONTENT
                                    )
                                    addView(
                                        TextView(context).apply {
                                            appearance(
                                                context,
                                                R.style.TextAppearance_Material3_BodyMedium
                                            )
                                            val spannable = SpannableStringBuilder()
                                                .append(
                                                    SpannableString(
                                                        "${
                                                            change.type.getName(
                                                                context
                                                            )
                                                        }: "
                                                    ).also {
                                                        it.setSpan(
                                                            StyleSpan(BOLD),
                                                            0,
                                                            it.length,
                                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                                        )
                                                    })
                                                .append(SpannableString(change.change).also {
                                                    if (change.url != null) {
                                                        it.setSpan(
                                                            clickableSpan { customUrlHandler(change.url) },
                                                            0,
                                                            it.length,
                                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                                        )
                                                    }
                                                })
                                            movementMethod = LinkMovementMethod.getInstance()
                                            setText(spannable, TextView.BufferType.SPANNABLE)
                                            layoutParams = LayoutParams(
                                                WRAP_CONTENT,
                                                WRAP_CONTENT
                                            ).apply {
                                                setMargins(0, top, 0, 0)
                                            }
                                        })
                                })
                            }
                        })
                    })
                }
            })
        })
        .setPositiveButton(android.R.string.ok) { dialog, button ->
            dialog.dismiss()
            closeListener(button)
        }
        .setNegativeButton(android.R.string.cancel) { dialog, button ->
            dialog.dismiss()
            closeListener(button)
        }
        .create().also { it.show() }

    fun showDialogOnVersionChange(
        customUrlHandler: (url: String) -> Unit = {
            context.startActivity(Intent(ACTION_VIEW, Uri.parse(it)))
        }
    ): Boolean {
        return showDialogOnVersionChange(-1, "null", customUrlHandler)
    }

    fun showDialogOnVersionChange(
        VERSION_CODE: Int, VERSION_NAME: String, customUrlHandler: (url: String) -> Unit = {
            context.startActivity(Intent(ACTION_VIEW, Uri.parse(it)))
        }
    ): Boolean {
        val sharedPreferences =
            context.getSharedPreferences("changeLogDialogCache", Context.MODE_PRIVATE)
        val lastVersionName = sharedPreferences.getString("versionName", "null")
        val lastVersionCode = sharedPreferences.getInt("versionCode", -1)
        val versionCode = try {
            getBuildConfigValue(context, "VERSION_CODE") as Int
        } catch (e: Exception) {
            VERSION_CODE
        }
        val versionName = try {
            getBuildConfigValue(context, "VERSION_NAME") as String
        } catch (e: Exception) {
            VERSION_NAME
        }

        val ret = if (lastVersionName == "null" && lastVersionCode == -1) {
            show(customUrlHandler)
            true
        } else if (lastVersionName != versionName || lastVersionCode < versionCode) {
            show(customUrlHandler)
            true
        } else false

        sharedPreferences.edit {
            putInt("versionCode", versionCode)
            putString("versionName", versionName)
        }

        return ret
    }

    private fun getBuildConfigValue(context: Context, fieldName: String): Any? {
        return try {
            val clazz = Class.forName(
                "${
                    if (context.packageName.endsWith(".debug")) context.packageName.replace(
                        ".debug",
                        ""
                    ) else context.packageName
                }.BuildConfig"
            )
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

    private fun clickableSpan(onClick: (widget: View) -> Unit) = object : ClickableSpan() {
        override fun onClick(widget: View) {
            onClick(widget)
        }
    }

    @Suppress("DEPRECATION")
    private fun TextView.appearance(context: Context, resId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) setTextAppearance(resId)
        else setTextAppearance(context, resId)
    }

    class ChangelogBuilder internal constructor(private val context: Context) {
        private val versions = arrayListOf<Version>()
        private var closeListener: (which: Int) -> Unit = {}

        fun addVersion(block: Version.VersionBuilder.() -> Unit) =
            versions.add(Version(block))

        fun onClose(listener: (which: Int) -> Unit) {
            closeListener = listener
        }

        internal fun build() = Changelog(context, versions, closeListener)
    }
}