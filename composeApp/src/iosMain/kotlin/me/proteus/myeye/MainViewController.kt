package me.proteus.myeye

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.allStringResources
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import platform.UIKit.UIViewController
import platform.UIKit.addChildViewController
import platform.UIKit.didMoveToParentViewController
import platform.UIKit.willMoveToParentViewController
import swiftSrc.ResourceManager

class UIViewControllerWrapper(
    private val controller: UIViewController, // compose view controller
    private val onDisappear : () -> Unit,
) : UIViewController(null,null) {

    @OptIn(ExperimentalForeignApi::class)
    override fun loadView() {
        super.loadView()
        controller.willMoveToParentViewController(this)
        println("loadView() detected")
        controller.view.setFrame(view.frame)
        view.addSubview(controller.view)
        addChildViewController(controller)
        controller.didMoveToParentViewController(this)
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        controller.view.setFrame(view.bounds)
    }

    override fun viewDidLoad() {
        super.viewDidLoad()
        println("view did load")
    }

    override fun viewDidDisappear(animated: Boolean) {
        super.viewDidDisappear(animated)
        println("view did disappear")
        onDisappear()
    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        println("Controller will disappear (self.dismiss detected?)")
    }

}

@OptIn(ExperimentalResourceApi::class, ExperimentalForeignApi::class)
@Composable
fun setupResources() {

    for (el in Res.allStringResources) {
        val res = stringResource(el.value)
        ResourceManager.shared().addResourceWithKey(el.key, res)
    }
}

fun MainViewController() = UIViewControllerWrapper(
    controller = ComposeUIViewController {
        setupResources()
        App()
    },
    onDisappear = { println("again") }
)