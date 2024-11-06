package mobappdev.example.nback_cimpl.ui.view

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mobappdev.example.nback_cimpl.ui.viewmodels.GameState

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

                    LaunchedEffect(gameState.roundCounter) {
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