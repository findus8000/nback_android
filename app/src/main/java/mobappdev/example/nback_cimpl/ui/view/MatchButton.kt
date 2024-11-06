package mobappdev.example.nback_cimpl.ui.view

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobappdev.example.nback_cimpl.ui.viewmodels.GameState
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun MatchButton(vm: GameViewModel, gameState: GameState) {
    var animationTrigger by remember { mutableStateOf(false) }
    var nrClicksRound by remember { mutableStateOf(0) }
    var isMatch by remember { mutableStateOf(null as Boolean?) }
    val color = remember { Animatable(Color.DarkGray) }

    Button(
        onClick = {
            if (nrClicksRound == 0) {
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

    LaunchedEffect(gameState.roundCounter) {
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