package me.proteus.myeye

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController
import platform.UIKit.addChildViewController
import platform.UIKit.didMoveToParentViewController
import platform.UIKit.willMoveToParentViewController

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

@OptIn(ExperimentalForeignApi::class)
fun MainViewController() = UIViewControllerWrapper(
    controller = ComposeUIViewController {
        App()
    },
    onDisappear = { println("again") }
)