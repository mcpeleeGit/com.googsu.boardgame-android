package com.googsu.boardgame

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.googsu.boardgame.ui.theme.BoardgameTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoardgameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.height(64.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier.height(64.dp),
                text = { Text("주사위", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier.height(64.dp),
                text = { Text("스톱워치", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            )
        }
        
        when (selectedTab) {
            0 -> DiceScreen()
            1 -> StopwatchScreen()
        }
    }
}

@Composable
fun DiceScreen() {
    var diceCount by remember { mutableStateOf(1) }
    var isRolling by remember { mutableStateOf(false) }
    var diceValues by remember { mutableStateOf(listOf(1, 1)) }
    var rotationAngles by remember { mutableStateOf(listOf(0f, 0f)) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "dice")
    val diceRotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dice rotation"
    )
    
    LaunchedEffect(isRolling) {
        if (isRolling) {
            delay(1000)
            diceValues = List(2) { Random.nextInt(1, 7) }
            rotationAngles = List(2) { Random.nextFloat() * 360f }
            isRolling = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "주사위 개수:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                RadioButton(
                    selected = diceCount == 1,
                    onClick = { diceCount = 1 },
                    modifier = Modifier.size(32.dp)
                )
                Text("1개", fontSize = 18.sp)
                RadioButton(
                    selected = diceCount == 2,
                    onClick = { diceCount = 2 },
                    modifier = Modifier.size(32.dp)
                )
                Text("2개", fontSize = 18.sp)
            }
        }

        Row(
            modifier = Modifier.height(160.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(diceCount) { index ->
                DiceView(
                    value = diceValues[index],
                    rotation = if (isRolling) diceRotation.value else rotationAngles[index],
                    modifier = Modifier.size(140.dp)
                )
            }
        }
        
        Button(
            onClick = { isRolling = true },
            enabled = !isRolling,
            modifier = Modifier
                .height(56.dp)
                .width(200.dp)
        ) {
            Text(
                if (isRolling) "굴리는 중..." else "주사위 굴리기",
                fontSize = 20.sp
            )
        }

        if (diceCount == 2) {
            Text(
                text = "합계: ${diceValues[0] + diceValues[1]}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DiceView(value: Int, rotation: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        rotate(rotation) {
            // 주사위 테두리
            drawRect(
                color = Color.White,
                topLeft = Offset.Zero,
                size = size
            )
            drawRect(
                color = Color.Black,
                topLeft = Offset.Zero,
                size = size,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
            )

            val dotRadius = size.minDimension / 8
            val center = Offset(size.width / 2, size.height / 2)
            
            // 주사위 점 그리기
            when (value) {
                1 -> {
                    drawCircle(Color.Black, dotRadius, center)
                }
                2 -> {
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.25f, size.height * 0.25f))
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.75f, size.height * 0.75f))
                }
                3 -> {
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.25f, size.height * 0.25f))
                    drawCircle(Color.Black, dotRadius, center)
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.75f, size.height * 0.75f))
                }
                4 -> {
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.25f, size.height * 0.25f))
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.75f, size.height * 0.25f))
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.25f, size.height * 0.75f))
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.75f, size.height * 0.75f))
                }
                5 -> {
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.25f, size.height * 0.25f))
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.75f, size.height * 0.25f))
                    drawCircle(Color.Black, dotRadius, center)
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.25f, size.height * 0.75f))
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.75f, size.height * 0.75f))
                }
                6 -> {
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.25f, size.height * 0.25f))
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.75f, size.height * 0.25f))
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.25f, size.height * 0.5f))
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.75f, size.height * 0.5f))
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.25f, size.height * 0.75f))
                    drawCircle(Color.Black, dotRadius, Offset(size.width * 0.75f, size.height * 0.75f))
                }
            }
        }
    }
}

@Composable
fun StopwatchScreen() {
    var isRunning by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(0L) }
    var elapsedTime by remember { mutableStateOf(0L) }
    
    // ToneGenerator 생성
    val toneGen = remember { ToneGenerator(AudioManager.STREAM_ALARM, 100) }
    
    // 깜빡임 애니메이션
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val blinkAlpha = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink alpha"
    )
    
    // cleanup
    DisposableEffect(Unit) {
        onDispose {
            toneGen.release()
        }
    }
    
    // 현재 시간을 밀리초 단위로 업데이트
    LaunchedEffect(isRunning) {
        if (isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime
            while (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime
                val seconds = (elapsedTime / 1000) % 60
                // 57초 이상일 때 (3초 전부터) 경고음 재생
                if (seconds >= 57) {
                    // 띵똥 소리 구현 (높은 음과 낮은 음을 연속 재생)
                    toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 200)
                    delay(250)  // 첫 번째 음이 끝날 때까지 대기
                    toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
                }
                delay(10) // UI 업데이트를 위한 짧은 딜레이
            }
        }
    }

    // 시간 계산
    val centiseconds = (elapsedTime / 10) % 100
    val seconds = (elapsedTime / 1000) % 60
    val minutes = (elapsedTime / 60000) % 60
    
    // 55초 이상일 때 깜빡임 효과 적용 (5초 전부터)
    val shouldBlink = seconds >= 55
    // 57초 이상일 때는 더 빠른 깜빡임 (3초 전부터)
    val isNearEnd = seconds >= 57
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.alpha(if (shouldBlink) blinkAlpha.value else 1f)
            ) {
                // 분
                Text(
                    text = String.format("%02d", minutes),
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isNearEnd) MaterialTheme.colorScheme.error 
                           else if (shouldBlink) MaterialTheme.colorScheme.tertiary
                           else MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = ":",
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isNearEnd) MaterialTheme.colorScheme.error 
                           else if (shouldBlink) MaterialTheme.colorScheme.tertiary
                           else MaterialTheme.colorScheme.onBackground
                )
                // 초
                Text(
                    text = String.format("%02d", seconds),
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isNearEnd) MaterialTheme.colorScheme.error 
                           else if (shouldBlink) MaterialTheme.colorScheme.tertiary
                           else MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = ".",
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isNearEnd) MaterialTheme.colorScheme.error 
                           else if (shouldBlink) MaterialTheme.colorScheme.tertiary
                           else MaterialTheme.colorScheme.onBackground
                )
                // 1/100초
                Text(
                    text = String.format("%02d", centiseconds),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isNearEnd) MaterialTheme.colorScheme.error 
                           else if (shouldBlink) MaterialTheme.colorScheme.tertiary
                           else MaterialTheme.colorScheme.onBackground
                )
            }
            
            Text(
                text = when {
                    isNearEnd -> "1분까지 ${60 - seconds}초 남음! (경고)"
                    shouldBlink -> "1분까지 ${60 - seconds}초 남음"
                    else -> "분:초.1/100초"
                },
                fontSize = 16.sp,
                color = if (isNearEnd) MaterialTheme.colorScheme.error
                        else if (shouldBlink) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp),
                fontWeight = if (shouldBlink) FontWeight.Bold else FontWeight.Normal
            )
        }
        
        Spacer(modifier = Modifier.height(64.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Button(
                onClick = { isRunning = !isRunning },
                modifier = Modifier
                    .width(160.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isNearEnd) MaterialTheme.colorScheme.error
                                   else if (shouldBlink) MaterialTheme.colorScheme.tertiary
                                   else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    if (isRunning) "정지" else "시작",
                    fontSize = 20.sp
                )
            }
            
            Button(
                onClick = { 
                    isRunning = false
                    elapsedTime = 0
                },
                modifier = Modifier
                    .width(160.dp)
                    .height(56.dp)
            ) {
                Text(
                    "리셋",
                    fontSize = 20.sp
                )
            }
        }
    }
}