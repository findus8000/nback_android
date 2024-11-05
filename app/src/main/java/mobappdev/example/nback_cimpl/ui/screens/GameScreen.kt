package mobappdev.example.nback_cimpl.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import mobappdev.example.nback_cimpl.ui.viewmodels.FakeVM
import mobappdev.example.nback_cimpl.ui.viewmodels.GameState
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel


@Composable
fun GameScreen(
    vm: GameViewModel,
    nc: NavHostController,
    tts : TextToSpeech,
    isTTSInitialized: Boolean
) {
    val gameState by vm.gameState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val score by vm.score.collectAsState()
    val nrMatches by vm.nrMatches.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LaunchedEffect(Unit) { vm.startGame() }

            Text(text = "Current Event Value: ${gameState.eventValue}")
            Text(text = "Current Round: ${gameState.roundCounter}")
            Text(text = "Correct matches: $nrMatches")
            Text(text = "Score: $score")

            when (gameState.gameType) {
                GameType.Visual -> {
                    VisualGameContent(gameState, vm)
                }
                GameType.Audio -> {
                    AudioGameContent(gameState, vm, tts, isTTSInitialized)
                }
                GameType.AudioVisual -> TODO()
            }

            if (gameState.roundCounter == 10){
                Button(onClick = { nc.navigate("home") }) {
                    Text("End Game")
                }
            }

        }
    }
}

@Composable
fun VisualGameContent(gameState: GameState, vm: GameViewModel) {
    Grid3x3(gameState)
    MatchButton(vm, gameState)
}

@Composable
fun AudioGameContent(gameState: GameState, vm: GameViewModel, tts: TextToSpeech, isTTSInitialized: Boolean) {
    val lettersList = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I')
    if (isTTSInitialized) {
        LaunchedEffect(gameState.roundCounter) {
            val letter = lettersList.getOrNull(gameState.eventValue - 1)?.toString()
            if (letter != null) {
                tts.speak(letter, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }else{
        Text(text = "TTS not initialized")
    }
    MatchButton(vm, gameState)
}

@Composable
fun Grid3x3(gameState: GameState) {
    val row = (gameState.eventValue - 1) / 3
    val col = (gameState.eventValue - 1) % 3

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(3) { rowIndex ->
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(3) { columnIndex ->

                    val isTargetCell = rowIndex == row && columnIndex == col
                    val color = remember { Animatable(Color.LightGray) }

                    LaunchedEffect(gameState.roundCounter){
                        if (isTargetCell) {
                            color.animateTo(
                                targetValue = Color.DarkGray,
                                animationSpec = tween(1000)
                            )
                            color.animateTo(
                                targetValue = Color.LightGray,
                                animationSpec = tween(1000)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .size(100.dp)
                            .background(color.value),
                    )
                }
            }
        }
    }
}

@Composable
fun MatchButton(vm: GameViewModel, gameState: GameState) {
    var animationTrigger by remember { mutableStateOf(false) }
    var nrClicksRound by remember { mutableStateOf(0) }
    var isMatch by remember { mutableStateOf(null as Boolean?) }
    val color = remember { Animatable(Color.DarkGray) }

    Button(
        onClick = {
            if (nrClicksRound == 0){
                animationTrigger = !animationTrigger
                isMatch = vm.checkMatch()
            }
            nrClicksRound++
        },
        modifier = Modifier
            .padding(16.dp)
            .width(250.dp)
            .height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.value // Use the animated color for the button
        )
    ) {
        Text(
            text = "N-back match",
            fontSize = 20.sp
        )
    }

    LaunchedEffect(gameState.roundCounter){
        nrClicksRound = 0
    }

    LaunchedEffect(animationTrigger) {
        if (isMatch != null) {
            color.animateTo(
                targetValue = if (isMatch as Boolean) Color.Green else Color.Red,
                animationSpec = tween(400)
            )
            color.animateTo(
                targetValue = Color.DarkGray,
                animationSpec = tween(400)
            )
        }
    }
}


@Preview
@Composable
fun GameScreenPreview() {
    Surface(){
        GameScreen(
            FakeVM(),
            nc = TODO(),
            tts = TODO(),
            isTTSInitialized = TODO()
        )
    }
}