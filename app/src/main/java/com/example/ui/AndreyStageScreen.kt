package com.example.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun AndreyStageScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "aura_anim")
    
    val auraGlow by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraGlow"
    )
    
    val slideAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleAnim"
    )

    val context = LocalContext.current
    val musicResId = androidx.compose.runtime.remember(context) {
        var resId = context.resources.getIdentifier("music_andrey", "raw", context.packageName)
        if (resId == 0) {
            resId = context.resources.getIdentifier("music_meme_stage", "raw", context.packageName)
        }
        resId
    }

    androidx.compose.runtime.DisposableEffect(musicResId) {
        var mediaPlayer: android.media.MediaPlayer? = null
        if (musicResId != 0) {
            try {
                mediaPlayer = android.media.MediaPlayer.create(context, musicResId)
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        onDispose {
            try {
                mediaPlayer?.stop()
            } catch (e: Exception) {}
            try {
                mediaPlayer?.release()
            } catch (e: Exception) {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030008)) // Very dark deep purple
    ) {
        
        // Aura background effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            
            // Draw gradient aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6A1B9A).copy(alpha = 0.4f * auraGlow),
                        Color(0xFF311B92).copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width
                ),
                center = center
                // Removed blendMode as it can cause crashes on certain emulators
            )
            
            // Vertical moving grid lines or particles
            for (i in 0..10) {
                val y = (size.height * ((i / 10f + slideAnim) % 1f))
                drawLine(
                    color = Color(0xFFAA00FF).copy(alpha = 0.15f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 3f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "POV: YOU ENCOUNTER A TRUE SIGMA",
                color = Color(0xFFB388FF),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(0.8f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "AURA DETECTED",
                color = Color(0xFFE040FB),
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = Color(0xFFD500F9),
                        blurRadius = 15f * auraGlow,
                        offset = Offset(0f, 0f)
                    )
                ),
                modifier = Modifier.scale(auraGlow * 0.05f + 0.95f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Box(
                modifier = Modifier
                    .background(
                        Color(0xFFFFD700).copy(alpha = 0.2f),
                        RoundedCornerShape(4.dp)
                    )
                    .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "MAX LEVEL SIGMA MALE",
                    color = Color(0xFFFFD700),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = Shadow(
                            color = Color(0xFFFFD700),
                            blurRadius = 10f * auraGlow,
                        )
                    )
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            val imageResId = androidx.compose.runtime.remember {
                var resId = context.resources.getIdentifier("img_andrey", "drawable", context.packageName)
                if (resId == 0) {
                    resId = context.resources.getIdentifier("img_meme_stage", "drawable", context.packageName)
                }
                resId
            }
            
            if (imageResId != 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .scale(1f + (auraGlow - 1f) * 0.03f) // subtle pulse
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFE040FB),
                                    Color(0xFF7C4DFF),
                                    Color(0xFF00E5FF)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(Color(0xFF1A1A1A)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageResId,
                        contentDescription = "Andrey",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Inner shadow/gradient for dramatic effect
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xFF030008).copy(alpha = 0.8f)
                                    ),
                                    startY = 300f
                                )
                            )
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            width = 2.dp,
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Image Not Found",
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1.5f))
        }
    }
}
