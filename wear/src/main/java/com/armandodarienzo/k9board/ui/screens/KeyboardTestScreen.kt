package com.armandodarienzo.k9board.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.armandodarienzo.k9board.shared.R

@Composable
fun KeyboardTestScreen(navController: NavController) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val listState = rememberScalingLazyListState(initialCenterItemIndex = 0)

    Scaffold(
        modifier = Modifier.background(Color.Black),
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        ScalingLazyColumn(
            contentPadding = PaddingValues(
                top = (screenHeight * 0.21).dp,
                bottom = (screenHeight * 0.36).dp,
                start = (screenHeight * 0.05).dp,
                end = (screenHeight * 0.05).dp
            ),
            state = listState,
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.main_activity_test_keyboard),
                    style = MaterialTheme.typography.title3,
                    color = MaterialTheme.colors.onSurface
                )
            }
            item {
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    textStyle = TextStyle(
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                ) { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colors.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colors.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (text.isEmpty()) {
                            Text(
                                text = "Tap to type…",
                                style = TextStyle(
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                                    fontSize = 16.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
