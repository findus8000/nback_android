package mobappdev.example.nback_cimpl.ui.view

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
                    Grid3x3(gameState)
                    MatchButton(vm, gameState)
                }

                GameType.Audio -> {
                    if (isTTSInitialized) {
                        LaunchedEffect(gameState.roundCounter) {
                            val letter = vm.getLetterForEvent()
                            tts.speak(letter, TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    }else{ Text(text = "TTS not initialized") }
                    MatchButton(vm, gameState)
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