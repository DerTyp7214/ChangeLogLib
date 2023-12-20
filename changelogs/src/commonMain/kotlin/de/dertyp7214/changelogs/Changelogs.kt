package de.dertyp7214.changelogs

/**
 * Created by Dertyp7214 on 20.12.2023.
 */

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.russhwolf.settings.Settings
import kotlin.math.min

/**
 * @param useEmoji use emoji for changes
 * @param useText use text for changes
 * @param useColors use colors for changes
 * @param maxVersions max versions to show (-1 = all)
 * @param onOpenUrl open url callback
 * @param appVersionCode app version code (used for changelog version check, if -1 it will be ignored)
 * @param closeText close button text
 * @param changeTypeTranslationMap translation map for change types
 * @constructor Create Changelog config
 */
data class ChangelogConfig(
    var useEmoji: Boolean = true,
    var useText: Boolean = true,
    var useColors: Boolean = true,
    var maxVersions: Int = -1,
    var onOpenUrl: (String?) -> Unit = {},
    var appVersionCode: Int = -1,
    var closeText: String? = null,
    var changeTypeTranslationMap: Map<ChangeType, String> = mapOf(
        ChangeType.ADDED to "Added",
        ChangeType.IMPROVED to "Improved",
        ChangeType.REMOVED to "Removed",
        ChangeType.FIXED to "Fixed"
    )
)

val settings: Settings = Settings()
val LocalChangelogConfig = compositionLocalOf { ChangelogConfig() }

class ChangeLog {
    val versions = mutableListOf<@Composable () -> Unit>()

    /**
     * @param versionName version name
     * @param versionCode version code
     * @param changes changes
     */
    fun version(
        versionName: String,
        versionCode: Int,
        changes: @Composable () -> Unit = {}
    ) {
        versions.add {
            Version(
                versionName = versionName,
                versionCode = versionCode,
                changes = changes
            )
        }
    }
}

fun checkChangelogVersion(versionCode: Int): Boolean {
    val lastVersion = settings.getInt("last_changelog_version", 0)
    return lastVersion < versionCode
}

fun Modifier.fadingEdges(
    scrollState: ScrollState,
    topEdgeHeight: Dp = 72.dp,
    bottomEdgeHeight: Dp = 72.dp
): Modifier = this.then(
    Modifier
        .graphicsLayer { alpha = 0.99F }
        .drawWithContent {
            drawContent()

            val topColors = listOf(Color.Transparent, Color.Black)
            val topStartY = scrollState.value.toFloat()
            val topGradientHeight = min(topEdgeHeight.toPx(), topStartY)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = topColors,
                    startY = topStartY,
                    endY = topStartY + topGradientHeight
                ),
                blendMode = BlendMode.DstIn
            )

            val bottomColors = listOf(Color.Black, Color.Transparent)
            val bottomEndY = size.height - scrollState.maxValue + scrollState.value
            val bottomGradientHeight =
                min(bottomEdgeHeight.toPx(), scrollState.maxValue.toFloat() - scrollState.value)
            if (bottomGradientHeight != 0f) drawRect(
                brush = Brush.verticalGradient(
                    colors = bottomColors,
                    startY = bottomEndY - bottomGradientHeight,
                    endY = bottomEndY
                ),
                blendMode = BlendMode.DstIn
            )
        }
)

/**
 * @param open open state
 * @param config changelog config
 * @param onReady on ready callback
 * @param onClose on close callback
 * @param onOpenUrl on open url callback
 * @param content changelog content
 */
@Composable
fun Changelogs(
    open: MutableState<Boolean>,
    config: ChangelogConfig = ChangelogConfig(),
    onReady: () -> Unit = {},
    onClose: () -> Unit = {},
    onOpenUrl: (String?) -> Unit = {},
    content: ChangeLog.() -> Unit
) {
    config.onOpenUrl = onOpenUrl

    if (checkChangelogVersion(config.appVersionCode)) {
        onReady()
        settings.putInt("last_changelog_version", config.appVersionCode)
    }

    CompositionLocalProvider(LocalChangelogConfig provides config) {
        AnimatedVisibility(
            visible = open.value,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Dialog(
                onDismissRequest = onClose
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(
                                top = 16.dp,
                                start = 24.dp,
                                end = 24.dp,
                                bottom = 16.dp
                            )
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "Changelog",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .fadingEdges(scrollState)
                                .weight(1f, false)
                        ) {
                            ChangeLog().apply(content).versions.let {
                                if (config.maxVersions > 0) it.take(config.maxVersions) else it
                            }.forEach {
                                it()
                            }
                        }
                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .align(Alignment.End)
                        ) {
                            TextButton(
                                modifier = Modifier
                                    .padding(end = 8.dp),
                                onClick = {
                                    onClose()
                                }
                            ) {
                                Text(
                                    text = config.closeText ?: "Close",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * @param versionName version name
 * @param versionCode version code
 * @param changes changes
 */
@Composable
fun Version(
    versionName: String,
    versionCode: Int,
    changes: @Composable () -> Unit
) {
    Column {
        Text(
            text = "$versionName ($versionCode)",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
        changes()
    }
}

fun Color.blendWith(color: Color, ratio: Float): Color {
    val inverseRatio = 1f - ratio
    val r = red * ratio + color.red * inverseRatio
    val g = green * ratio + color.green * inverseRatio
    val b = blue * ratio + color.blue * inverseRatio
    val a = alpha * ratio + color.alpha * inverseRatio
    return Color(r, g, b, a)
}

/**
 * @param type change type
 * @param change change text
 * @param url change url
 */
@Composable
fun Change(
    type: ChangeType,
    change: String,
    url: String? = null,
) {
    val config = LocalChangelogConfig.current

    val prefix = config.useEmoji.let {
        if (it) "${type.toEmoji()}${
            if (config.useText) " " else ""
        }" else ""
    } + config.useText.let {
        if (it) type.toText().uppercase() else ""
    }

    val onSurface = MaterialTheme.colorScheme.onSurface

    val text = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = config.useColors.let {
                    if (it) type.toColor().blendWith(onSurface, .5f) else onSurface
                }
            )
        ) {
            append("$prefix: ")
        }
        if (url != null)
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                append(change)
            }
        else
            append(change)
    }
    if (url != null)
        Text(
            text = text,
            modifier = Modifier
                .padding(top = 2.dp)
                .clickable {
                    config.onOpenUrl(url)
                }
        ) else
        Text(
            text = text,
            modifier = Modifier.padding(top = 2.dp)
        )
}

enum class ChangeType {
    ADDED, IMPROVED, REMOVED, FIXED;

    fun toEmoji(): String = when (this) {
        ADDED -> "➕"
        IMPROVED -> "🔧"
        REMOVED -> "➖"
        FIXED -> "⚒️"
    }

    @Composable
    fun toColor(): Color = when (this) {
        ADDED -> Color.Blue
        IMPROVED -> Color.Yellow
        REMOVED -> Color.Red
        FIXED -> Color.Green
    }

    @Composable
    fun toText(): String = LocalChangelogConfig.current.changeTypeTranslationMap[this] ?: ""
}