package me.proteus.myeye.ui

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors

class SimpleDistanceActivity : ComponentActivity() {

    private val sredniaSzerokoscTwarzy = 150f

    private lateinit var camera: LifecycleCameraController
    private lateinit var imageSize: Pair<Float, Float>
    private val executor = Executors.newSingleThreadExecutor()
    private val faces = mutableStateOf<List<Face>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            println(permission)
        }.launch(Manifest.permission.CAMERA)

        camera = LifecycleCameraController(this)

        setContent {

            imageSize = getDeviceSize()

            camera.imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
            camera.imageAnalysisResolutionSelector = createSelector()

            CameraView()
        }

    }

    @OptIn(ExperimentalGetImage::class)
    @Composable
    private fun CameraView() {

        val context = LocalContext.current
        val preview = remember { PreviewView(context) }
        val camInfo by remember { mutableStateOf(cameraInfo(context)) }

        camera.bindToLifecycle(LocalLifecycleOwner.current)
        camera.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        preview.controller = camera

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                var canvasWidth by remember { mutableIntStateOf(0) }
                var canvasHeight by remember { mutableIntStateOf(0) }

                Box(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxSize()
                        .onGloballyPositioned { pos ->
                            canvasWidth = pos.size.width
                            canvasHeight = pos.size.height
                        }
                ) {
                    AndroidView(factory = { preview }, modifier = Modifier.fillMaxSize())
//                    FaceCanvas(faces.value, canvasWidth, canvasHeight)
                }

                var text: String

                if (faces.value.isNotEmpty()) {

                    val ogniskowa = camInfo.first
                    val sensorWidth = camInfo.second

                    val imageWidth = faces.value[0].boundingBox.width().toFloat()
                    val sensorSize = imageWidth * sensorWidth / imageSize.first

                    text = (sredniaSzerokoscTwarzy * ogniskowa / sensorSize / 10).toString()

                } else {
                    text = "Oczekiwanie na wykrycie twarzy"
                }

                Text(text, modifier = Modifier.weight(0.2f), fontSize = 48.sp)
            }

            camera.setImageAnalysisAnalyzer(executor) { imageProxy ->
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                val mediaImage = imageProxy.image

                println("${mediaImage?.width} ${mediaImage?.height}")

                if (mediaImage != null) {

                    imageSize = Pair(mediaImage.width.toFloat(), mediaImage.height.toFloat())

                    val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

                    detectFaces(inputImage) { detectedFaces ->
                        faces.value = detectedFaces
                        imageProxy.close()
                    }
                } else {
                    imageProxy.close()
                }
            }
        }

    }

    fun createSelector(): ResolutionSelector {
//        val devSize = Size(width, height)
//        val strategy = ResolutionStrategy(boundSize = devSize, fallbackRule = ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER)
        val rs = ResolutionSelector.Builder().setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY).build()

        return rs
    }

    private fun detectFaces(image: InputImage, callback: (List<Face>) -> Unit) {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .build()

        val detector = FaceDetection.getClient(options)

        detector.process(image)
            .addOnSuccessListener { faces ->
                callback(faces)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    @Composable
    fun getDeviceSize(): Pair<Float, Float> {

        var config = LocalConfiguration.current
        var density = LocalDensity.current.density
        var widthDp: Int
        var heightDp: Int

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            widthDp = config.screenWidthDp
            heightDp = config.screenHeightDp
        } else {
            widthDp = config.screenHeightDp
            heightDp = config.screenWidthDp
        }

        return Pair(widthDp/density, heightDp/density)

    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
        camera.unbind()
    }

    fun cameraInfo(context: Context): Pair<Float, Float> {
        try {
            val cameraManager = context.getSystemService(CAMERA_SERVICE) as CameraManager

            val cameraId = cameraManager.cameraIdList.firstOrNull { cameraId ->
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                facing == CameraCharacteristics.LENS_FACING_FRONT
            } ?: cameraManager.cameraIdList[0]

            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val focalLength = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)?.firstOrNull() ?: 0f
            val sensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)!!.width.toFloat()
            return Pair(focalLength, sensorSize)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            return Pair(-1f, -1f)
        }
    }

    @Composable
    fun FaceCanvas(faces: List<Face>, width: Int, height: Int) {

        val scaleX = width / imageSize.first
        val scaleY = height / imageSize.second

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (face in faces) {
                val b = face.boundingBox

                val left = width - (b.right * scaleX)
                val right = width - (b.left * scaleX)

                val top = b.top * scaleY
                val bottom = b.bottom * scaleY

                drawLine(
                    brush = SolidColor(Color.Red),
                    strokeWidth = 5f,
                    start = Offset(left, (top + bottom) / 2),
                    end = Offset(right, (top + bottom) / 2)
                )

            }
        }
    }

}