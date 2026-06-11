package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreenView(onFinish: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    
    // Scale animation for the logo
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 0.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "LogoScale"
    )

    // Spin animation for the outer tech ring
    val infiniteTransition = rememberInfiniteTransition(label = "SpinRing")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RingRotation"
    )

    // Alpha/Fade animation for the branding texts
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.0f,
        animationSpec = tween(1200, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "TextAlpha"
    )

    // Run splash screen timeline
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500) // Beautiful 2.5 second delay showing animated transitions
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CyberSlateBg.copy(alpha = 0.95f),
                        CyberSlateBg,
                        if (currentThemeActive == 2) Color(0xFFEADDFF) else CyberCardSurface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated Outer Ring and Core Logo
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(scale),
                contentAlignment = Alignment.Center
            ) {
                // The futuristic dynamic spinning shield ring
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation)
                ) {
                    drawCircle(
                        color = CyberPrimaryTeal.copy(alpha = 0.15f),
                        radius = size.minDimension / 2f
                    )
                    
                    // Technical dashed style outer ring
                    drawArc(
                        color = CyberPrimaryTeal,
                        startAngle = 0f,
                        sweepAngle = 100f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), miter = 4f)
                    )
                    drawArc(
                        color = CyberPrimaryTeal,
                        startAngle = 120f,
                        sweepAngle = 100f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), miter = 4f)
                    )
                    drawArc(
                        color = CyberPrimaryTeal,
                        startAngle = 240f,
                        sweepAngle = 80f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), miter = 4f)
                    )
                }

                // Inner core with padlock icon as the emblem
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(CyberCardSurface, CircleShape)
                        .scale(0.85f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Authenticator Logo",
                        tint = CyberPrimaryTeal,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Branding details
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(textAlpha)
            ) {
                Text(
                    text = "المصادق الآمن",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightSlateText,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "رمز أمانك ثنائي الحماية الذكي والتشفير المتكامل",
                    fontSize = 12.sp,
                    color = SoftGreySub,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    lineHeight = 18.sp
                )
            }
        }
    }
}
