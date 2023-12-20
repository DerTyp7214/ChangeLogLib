import androidx.compose.ui.window.ComposeUIViewController
import de.dertyp7214.changeloglib.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
