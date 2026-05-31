package com.example.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sound.MemeSynth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemeStageScreen() {
    val scope = rememberCoroutineScope()
    
    var isEvilLaughing by remember { mutableStateOf(false) }
    var isPhonking by remember { mutableStateOf(false) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "animation")
    
    // Evil Laugh Pulse
    val evilScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isEvilLaughing) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "evilScale"
    )
    val evilRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isEvilLaughing) 3f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "evilRotation"
    )

    // Phonk Intensity Mode
    val phonkScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPhonking) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(250, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phonkScale"
    )
    val phonkShake by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isPhonking) 15f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(40, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phonkShake"
    )
    val phonkTouchesColor by infiniteTransition.animateColor(
        initialValue = Color.Transparent,
        targetValue = if (isPhonking) Color(0x66FF0000) else Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phonkColor"
    )
    val evilGlowColor by infiniteTransition.animateColor(
        initialValue = Color.Transparent,
        targetValue = if (isEvilLaughing) Color(0x448B0000) else Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "evilColor"
    )

    val phonkLaserPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isPhonking) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phonkLaser"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E0000), Color(0xFF050000))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.fillMaxSize().background(phonkTouchesColor))
        Box(modifier = Modifier.fillMaxSize().background(evilGlowColor))
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .graphicsLayer {
                        if (isEvilLaughing) {
                            scaleX = evilScale
                            scaleY = evilScale
                            rotationZ = evilRotation
                            shadowElevation = 40f
                            ambientShadowColor = Color.Red
                            spotShadowColor = Color.Red
                        } else if (isPhonking) {
                            scaleX = phonkScale
                            scaleY = phonkScale
                            translationX = phonkShake
                            shadowElevation = 50f
                            ambientShadowColor = Color.Yellow
                            spotShadowColor = Color.Yellow
                        } else {
                            shadowElevation = 20f
                            ambientShadowColor = Color.Black
                        }
                    }
                    .border(
                        width = if (isEvilLaughing) 4.dp else if (isPhonking) 6.dp else 2.dp,
                        color = if (isEvilLaughing) Color.Red else if (isPhonking) Color.Yellow else Color(0xFF330000),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.img_meme_stage),
                    contentDescription = "Meme Stage",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                if (isPhonking) {
                    // HUD Overlays
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val widthOffset = size.width
                        val heightOffset = size.height
                        
                        // --- HUD Elements ---
                        val cx = widthOffset / 2f
                        val cy = heightOffset / 2f
                        
                        // Central Crosshair
                        drawLine(
                            color = Color.Red.copy(alpha = 0.7f),
                            start = Offset(cx - 30f, cy),
                            end = Offset(cx + 30f, cy),
                            strokeWidth = 2f
                        )
                        drawLine(
                            color = Color.Red.copy(alpha = 0.7f),
                            start = Offset(cx, cy - 30f),
                            end = Offset(cx, cy + 30f),
                            strokeWidth = 2f
                        )
                        drawCircle(
                            color = Color.Red.copy(alpha = 0.4f),
                            radius = 60f,
                            center = Offset(cx, cy),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                        )
                        
                        // Target Box
                        val boxX = widthOffset * 0.125f
                        val boxY = heightOffset * 0.22f
                        val boxSize = widthOffset * 0.12f
                        drawRect(
                            color = Color.Red.copy(alpha = 0.8f),
                            topLeft = Offset(boxX - boxSize/2f, boxY - boxSize/2f),
                            size = androidx.compose.ui.geometry.Size(boxSize, boxSize),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                        )
                        
                        // Coordinate estimates for the Trollface in the yellow vest (left side)
                        val leftEye = Offset(widthOffset * 0.105f, heightOffset * 0.22f)
                        val rightEye = Offset(widthOffset * 0.145f, heightOffset * 0.22f)
                        
                        // Lasers shoot outwards towards the viewer (screen)
                        val targetLeft = Offset(-widthOffset * 0.3f, heightOffset * 1.2f)
                        val targetRight = Offset(widthOffset * 0.6f, heightOffset * 1.2f)
                        
                        val strokeCore = 3f + (6f * phonkLaserPulse)
                        val strokeGlow = strokeCore * 3f
                        
                        // Left Laser Glow
                        drawLine(
                            color = Color.Red.copy(alpha = 0.5f),
                            start = leftEye,
                            end = targetLeft,
                            strokeWidth = strokeGlow,
                            cap = StrokeCap.Round
                        )
                        // Left Laser Core
                        drawLine(
                            color = Color.Yellow,
                            start = leftEye,
                            end = targetLeft,
                            strokeWidth = strokeCore,
                            cap = StrokeCap.Round
                        )
                        
                        // Right Laser Glow
                        drawLine(
                            color = Color.Red.copy(alpha = 0.5f),
                            start = rightEye,
                            end = targetRight,
                            strokeWidth = strokeGlow,
                            cap = StrokeCap.Round
                        )
                        // Right Laser Core
                        drawLine(
                            color = Color.Yellow,
                            start = rightEye,
                            end = targetRight,
                            strokeWidth = strokeCore,
                            cap = StrokeCap.Round
                        )
                    }
                    
                    // HUD Texts
                    Text(
                        text = "SYS:OVERRIDE\nTHREAT:MAX\n[ENGAGED]",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = Shadow(color = Color.Red, blurRadius = 8f)
                        )
                    )
                    
                    Text(
                        text = "TGT_LOCK_01",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = Shadow(color = Color.Red, blurRadius = 8f)
                        )
                    )
                    
                    Text(
                        text = "REC \n" + (if (phonkLaserPulse > 0.5f) "•" else " "),
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = Shadow(color = Color.Red, blurRadius = 8f)
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = {
                    if (!isEvilLaughing && !isPhonking) {
                        isEvilLaughing = true
                        scope.launch {
                            MemeSynth.playSound("evil_laugh")
                            delay(1500)
                            isEvilLaughing = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(
                        elevation = if (isEvilLaughing) 24.dp else 8.dp,
                        ambientColor = Color.Red,
                        spotColor = Color.Red,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B0000), // Darker red background
                    contentColor = Color.White
                ),
                border = BorderStroke(
                    width = if (isEvilLaughing) 2.dp else 1.dp, 
                    color = if (isEvilLaughing) Color.Red else Color(0xFFB71C1C)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "👺 ЗЛОВЕЩИЙ СМЕХ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    if (!isEvilLaughing && !isPhonking) {
                        isPhonking = true
                        scope.launch {
                            MemeSynth.playSound("rampage_phonk")
                            delay(3000)
                            isPhonking = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(
                        elevation = if (isPhonking) 24.dp else 8.dp,
                        ambientColor = Color.Yellow,
                        spotColor = Color.Yellow,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF222222), // Dark gritty background
                    contentColor = Color(0xFFFFD700)
                ),
                border = BorderStroke(
                    width = if (isPhonking) 2.dp else 1.dp,
                    color = if (isPhonking) Color.Yellow else Color(0xFF887700)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "⚡ RAMPAGE ФОНК",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
            }
        }
    }
}
