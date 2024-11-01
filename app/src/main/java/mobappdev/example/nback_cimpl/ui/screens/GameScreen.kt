package mobappdev.example.nback_cimpl.ui.screens

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.navigation.NavHostController
import mobappdev.example.nback_cimpl.ui.viewmodels.FakeVM
import mobappdev.example.nback_cimpl.ui.viewmodels.GameState
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel


@Composable
fun GameScreen(
    vm: GameViewModel,
    nc: NavHostController
) {
    val highscore by vm.highscore.collectAsState()  // Highscore is its own StateFlow
    val gameState by vm.gameState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val roundCounter by vm.roundCounter.collectAsState()


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

            when (gameState.gameType) {
                GameType.Visual -> {
                    Text("Visual Game")
                    Text(text = "Current Event Value: ${gameState.eventValue}")
                    Text(text = "Current : ${roundCounter}")
                    VisualGameContent(gameState.eventValue, roundCounter)
                }
                GameType.Audio -> {
                    Text("Audio Game")
                    AudioGameContent()
                }
                GameType.AudioVisual -> TODO()
            }

            Button(
                onClick = {
                    nc.navigate("home");
                }) {
                Text("Return to Home")
            }
        }
    }
}

@Composable
fun VisualGameContent(currentEventValue : Int, roundCounter : Int) {

    Grid3x3(currentEventValue, roundCounter)
}

@Composable
fun AudioGameContent() {
    // Audio n-back game logic here, based on the `currentAudioCue`
}


@Composable
fun Grid3x3(currentEventValue: Int, roundCounter: Int) {
    val row = (currentEventValue - 1) / 3
    val col = (currentEventValue - 1) % 3

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(3) { rowIndex ->
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(3) { columnIndex ->

                    val isTargetCell = rowIndex == row && columnIndex == col
                    val color = remember { Animatable(Color.LightGray) }

                    LaunchedEffect(roundCounter){
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
                        contentAlignment = Alignment.Center
                    ) {
                        Text("(row $rowIndex, col $columnIndex)")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GameScreenPreview() {
    Surface(){
        GameScreen(
            FakeVM(),
            nc = TODO())
    }
}