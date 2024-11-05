package mobappdev.example.nback_cimpl

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mobappdev.example.nback_cimpl.ui.screens.HomeScreen
import mobappdev.example.nback_cimpl.ui.screens.GameScreen
import mobappdev.example.nback_cimpl.ui.theme.NBack_CImplTheme
import mobappdev.example.nback_cimpl.ui.viewmodels.GameVM
import java.util.Locale

/**
 * This is the MainActivity of the application
 *
 * Your navigation between the two (or more) screens should be handled here
 * For this application you need at least a homescreen (a start is already made for you)
 * and a gamescreen (you will have to make yourself, but you can use the same viewmodel)
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */


class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private var isTTSInitializedState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        setContent {
            NBack_CImplTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Instantiate the viewmodel
                    val navController = rememberNavController()
                    val gameViewModel: GameVM = viewModel(
                        factory = GameVM.Factory
                    )

                    NavigationHost(navController, gameViewModel, tts, isTTSInitializedState.value)
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            isTTSInitializedState.value = true
        }
    }

    override fun onDestroy() {
        // Release TTS resources
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    gameViewModel: GameVM,
    tts : TextToSpeech,
    isTTSInitialized: Boolean
) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(vm = gameViewModel, nc = navController)
        }

        composable("game") {
            GameScreen(vm = gameViewModel, nc = navController, tts = tts, isTTSInitialized = isTTSInitialized)
        }
    }
}