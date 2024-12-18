package mobappdev.example.nback_cimpl.ui.viewmodels

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.GameApplication
import mobappdev.example.nback_cimpl.NBackHelper
import mobappdev.example.nback_cimpl.data.UserPreferencesRepository

/**
 * This is the GameViewModel.
 *
 * It is good practice to first make an interface, which acts as the blueprint
 * for your implementation. With this interface we can create fake versions
 * of the viewmodel, which we can use to test other parts of our app that depend on the VM.
 *
 * Our viewmodel itself has functions to start a game, to specify a gametype,
 * and to check if we are having a match
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */


interface GameViewModel {
    val gameState: StateFlow<GameState>
    val score: StateFlow<Int>
    val nrMatches: StateFlow<Int>
    val highscore: StateFlow<Int>
    val nBack: Int

    fun setGameType(gameType: GameType)
    fun startGame()
    fun checkMatch() : Boolean
    fun getLetterForEvent() : String
}

class GameVM(
    private val userPreferencesRepository: UserPreferencesRepository
): GameViewModel, ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    override val gameState: StateFlow<GameState>
        get() = _gameState.asStateFlow()

    private val _score = MutableStateFlow(0)
    override val score: StateFlow<Int>
        get() = _score

    private val _highscore = MutableStateFlow(0)
    override val highscore: StateFlow<Int>
        get() = _highscore

    // nBack is currently hardcoded
    override val nBack: Int = 2

    private var job: Job? = null  // coroutine job for the game event
    private val eventInterval: Long = 2000L  // 2000 ms (2s)

    private val nBackHelper = NBackHelper()  // Helper that generate the event array
    private var events = emptyArray<Int>()  // Array with all events

    private var hasCheckedMatch = false
    private var nrClicks = 0

    private val _nrMatches = MutableStateFlow(0)
    override val nrMatches: StateFlow<Int> = _nrMatches.asStateFlow()

    private val lettersList = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I')



    override fun setGameType(gameType: GameType) {
        // update the gametype in the gamestate
        _gameState.value = _gameState.value.copy(gameType = gameType)
    }

    override fun startGame() {
        job?.cancel()  // Cancel any existing game loop

        // Get the events from our C-model (returns IntArray, so we need to convert to Array<Int>)
        events = nBackHelper.generateNBackString(10, 9, 30, nBack).toList().toTypedArray()  // Todo Higher Grade: currently the size etc. are hardcoded, make these based on user input
        Log.d("GameVM", "The following sequence was generated: ${events.contentToString()}")
        _gameState.value = _gameState.value.copy(roundCounter = 0)
        _score.value = 0
        _nrMatches.value = 0
        nrClicks = 0

        job = viewModelScope.launch {
            when (gameState.value.gameType) {
                GameType.Audio -> runAudioGame(events)
                GameType.AudioVisual -> runAudioVisualGame()
                GameType.Visual -> runVisualGame(events)
            }

           if (_score.value  > _highscore.value){
               _highscore.value = _score.value
               userPreferencesRepository.saveHighScore(_highscore.value)
           }

        }
    }

    override fun checkMatch(): Boolean {
        nrClicks++
        if (hasCheckedMatch){
            _score.value = _nrMatches.value*((_nrMatches.value.toDouble() / nrClicks) * 100).toInt()
            return false
        }

        if (gameState.value.roundCounter > nBack){
            hasCheckedMatch = true

            val match : Boolean = events.get(gameState.value.roundCounter-1) ==
                    events.get(gameState.value.roundCounter-1 - nBack)

            if (match){
                _nrMatches.value++;
                _score.value = _nrMatches.value*((_nrMatches.value.toDouble() / nrClicks) * 100).toInt()
                return true;
            }else{
                _score.value = _nrMatches.value*((_nrMatches.value.toDouble() / nrClicks) * 100).toInt()
                return false;
            }
        }
        _score.value = _nrMatches.value*((_nrMatches.value.toDouble() / nrClicks) * 100).toInt()
        return false;
    }

    private suspend fun runAudioGame(events: Array<Int>) {
        for (value in events) {
            hasCheckedMatch = false
            _gameState.value = _gameState.value.copy(roundCounter = _gameState.value.roundCounter + 1, eventValue = value)
            delay(eventInterval)
        }
    }

    private suspend fun runVisualGame(events: Array<Int>){
        for (value in events) {
            hasCheckedMatch = false
            _gameState.value = _gameState.value.copy(roundCounter = _gameState.value.roundCounter + 1, eventValue = value)
            delay(eventInterval)
        }
    }

 override fun getLetterForEvent() : String{
     return lettersList[gameState.value.eventValue - 1].toString()
 }

    private fun runAudioVisualGame(){
        // Todo: Make work for Higher grade
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GameApplication)
                GameVM(application.userPreferencesRespository)
            }
        }
    }

    init {
        // Code that runs during creation of the vm
        viewModelScope.launch {
            userPreferencesRepository.highscore.collect {
                _highscore.value = it
            }
        }
    }
}








// Class with the different game types
enum class GameType{
    Audio,
    Visual,
    AudioVisual
}

data class GameState(
    // You can use this state to push values from the VM to your UI.
    val gameType: GameType = GameType.Visual,  // Type of the game
    val eventValue: Int = -1,  // The value of the array string
    val roundCounter: Int = 0
)





class FakeVM: GameViewModel{
    override val gameState: StateFlow<GameState>
        get() = MutableStateFlow(GameState()).asStateFlow()
    override val score: StateFlow<Int>
        get() = MutableStateFlow(2).asStateFlow()
    override val nrMatches: StateFlow<Int>
        get() = TODO("Not yet implemented")
    override val highscore: StateFlow<Int>
        get() = MutableStateFlow(42).asStateFlow()
    override val nBack: Int
        get() = 2

    override fun setGameType(gameType: GameType) {
    }

    override fun startGame() {
    }

    override fun checkMatch(): Boolean {
        return true
    }

    override fun getLetterForEvent(): String {
        TODO("Not yet implemented")
    }
}