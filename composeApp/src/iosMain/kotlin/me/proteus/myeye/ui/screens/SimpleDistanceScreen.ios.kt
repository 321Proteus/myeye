package me.proteus.myeye.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import swiftSrc.DistanceViewController

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun SimpleDistanceScreen(
    countdown: Boolean,
    testID: String
) {

    UIKitViewController(
        factory = { DistanceViewController() },
        modifier = Modifier.fillMaxSize()
    )
//    var distance by remember { mutableStateOf("Odległość: -- cm") }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        CameraPreview(onFaceDetected = { measuredDistance ->
//            distance = "Odległość: %.1f cm".format(measuredDistance)
//        })
//
//        Text(
//            text = distance,
//            fontSize = 24.sp,
//            color = androidx.compose.ui.graphics.Color.White,
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(16.dp)
//                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f))
//                .padding(8.dp)
//        )
//    }
//}
//
//@Composable
//fun CameraPreview(onFaceDetected: (Double) -> Unit) {
//    UIKitView(factory = {
//        val viewController = FaceDistanceViewController(onFaceDetected)
//        viewController.view
//    })
//}
//
//class FaceDistanceViewController(private val onFaceDetected: (Double) -> Unit) : UIViewController(), AVCaptureVideoDataOutputSampleBufferDelegateProtocol {
//    private val session = AVCaptureSession()
//    private val previewLayer = AVCaptureVideoPreviewLayer(session)
//
//    override fun viewDidLoad() {
//        super.viewDidLoad()
//        setupCamera()
//    }
//
//    @OptIn(ExperimentalForeignApi::class)
//    private fun setupCamera() {
//        val camera = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) ?: return
//        val input = AVCaptureDeviceInput.deviceInputWithDevice(camera, null) ?: return
//        session.addInput(input)
//
//        val output = AVCaptureVideoDataOutput()
//        output.setSampleBufferDelegate(this, DispatchQueue.mainQueue)
//        session.addOutput(output)
//
//        previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
//        previewLayer.frame = view.bounds
//        view.layer.addSublayer(previewLayer)
//
//        session.startRunning()
//    }
//
//    @OptIn(ExperimentalForeignApi::class)
//    override fun captureOutput(
//        output: AVCaptureOutput,
//        sampleBuffer: CMSampleBuffer,
//        connection: AVCaptureConnection
//    ) {
//        val pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) ?: return
//        val request = VNDetectFaceRectanglesRequest { request, _ ->
//            val faces = request.results as? List<VNFaceObservation>
//            val face = faces?.firstOrNull() ?: return@VNDetectFaceRectanglesRequest
//
//            val faceWidth = face.boundingBox.size.width * view.frame.width // Szerokość twarzy na ekranie
//            val distance = calculateDistance(faceWidth)
//
//            onFaceDetected(distance)
//        }
//
//        val handler = VNImageRequestHandler(pixelBuffer, NSDictionary())
//        handler.performRequests(listOf(request), null)
//    }
//
//    private fun calculateDistance(faceWidth: Double): Double {
//        val knownFaceWidth = 16.0 // cm (przykładowa szerokość twarzy)
//        val focalLength = 4.25 // mm (ogniskowa iPhone'a)
//
//        return (focalLength * knownFaceWidth * 4000) / faceWidth // 4000 to przykładowy sensorWidth
//    }
}