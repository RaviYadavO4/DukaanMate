package com.portfolio.dukaanmate.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.portfolio.dukaanmate.ui.localization.LocalStrings
import java.util.concurrent.Executors

@Composable
fun BarcodeScannerView(
    modifier: Modifier = Modifier,
    onBarcodeScanned: (String) -> Unit
) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    var lastScanTime by remember { mutableLongStateOf(0L) }
    val toneGen = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }

    DisposableEffect(Unit) {
        onDispose { toneGen.release() }
    }

    // Smooth up-down animation for the laser line
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    if (hasCameraPermission) {
        Box(
            modifier = modifier.clip(RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }
                        val scanner = BarcodeScanning.getClient()
                        val analysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(1280, 720))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val image = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.imageInfo.rotationDegrees
                                )
                                scanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        val code = barcodes.firstOrNull()?.rawValue
                                        val now = System.currentTimeMillis()
                                        if (!code.isNullOrBlank() && (now - lastScanTime > 1500)) {
                                            lastScanTime = now
                                            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                                            onBarcodeScanned(code)
                                        }
                                    }
                                    .addOnCompleteListener { imageProxy.close() }
                            } else {
                                imageProxy.close()
                            }
                        }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                analysis
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Premium Viewfinder Overlays (corner brackets + laser line)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val padX = 48.dp.toPx()
                val padY = 24.dp.toPx()
                val cornerLen = 24.dp.toPx()
                val stroke = 3.5.dp.toPx()
                val bracketColor = Color.White

                // Top-Left
                drawLine(bracketColor, Offset(padX, padY), Offset(padX + cornerLen, padY), stroke, StrokeCap.Round)
                drawLine(bracketColor, Offset(padX, padY), Offset(padX, padY + cornerLen), stroke, StrokeCap.Round)

                // Top-Right
                drawLine(bracketColor, Offset(size.width - padX, padY), Offset(size.width - padX - cornerLen, padY), stroke, StrokeCap.Round)
                drawLine(bracketColor, Offset(size.width - padX, padY), Offset(size.width - padX, padY + cornerLen), stroke, StrokeCap.Round)

                // Bottom-Left
                drawLine(bracketColor, Offset(padX, size.height - padY), Offset(padX + cornerLen, size.height - padY), stroke, StrokeCap.Round)
                drawLine(bracketColor, Offset(padX, size.height - padY), Offset(padX, size.height - padY - cornerLen), stroke, StrokeCap.Round)

                // Bottom-Right
                drawLine(bracketColor, Offset(size.width - padX, size.height - padY), Offset(size.width - padX - cornerLen, size.height - padY), stroke, StrokeCap.Round)
                drawLine(bracketColor, Offset(size.width - padX, size.height - padY), Offset(size.width - padX, size.height - padY - cornerLen), stroke, StrokeCap.Round)

                // Glowing laser line
                val currentY = size.height * laserProgress
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color(0xFF00E676), Color.Transparent),
                        startX = padX,
                        endX = size.width - padX
                    ),
                    start = Offset(padX + 8.dp.toPx(), currentY),
                    end = Offset(size.width - padX - 8.dp.toPx(), currentY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Bottom guide badge (Bilingual)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(50),
                color = Color.Black.copy(alpha = 0.55f)
            ) {
                Text(
                    text = strings.scanGuide,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    } else {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.cameraPermissionMsg,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text(strings.grantPermission)
                }
            }
        }
    }
}