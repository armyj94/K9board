package com.armandodarienzo.k9board.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold

import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.tooling.preview.devices.WearDevices
import com.armandodarienzo.k9board.model.MainMenuItem
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_LANGUAGE
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.armandodarienzo.k9board.shared.ui.base.rememberFlowWithLifecycle
import com.armandodarienzo.k9board.settings_app.ui.screens.home.HomeScreenReducer
import com.armandodarienzo.k9board.settings_app.ui.screens.home.HomeScreenViewModel
import com.armandodarienzo.k9board.settings_app.ui.screens.home.HomeScreenViewModel.Action

@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Composable
fun ContentPreview() {
    HomeScreenContentWear(sendAction = {})
}

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val effectFlow = rememberFlowWithLifecycle(viewModel.effect)

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is HomeScreenReducer.Effect.LaunchActivity -> context.startActivity(effect.intent)
                HomeScreenReducer.Effect.ShowImePicker -> {
                    val imeManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imeManager.showInputMethodPicker()
                }
                is HomeScreenReducer.Effect.NavigateTo -> navController.navigate(effect.route)
            }
        }
    }

    HomeScreenContentWear(sendAction = viewModel::processAction)
}

@Composable
fun HomeScreenContentWear(
    sendAction: (Action) -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val listState = rememberScalingLazyListState(initialCenterItemIndex = 0)

    val menuItems = listOf(
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_languages),
            optionKeyString = SHARED_PREFS_SET_LANGUAGE,
            iconID = R.drawable.ic_language_white_18dp,
            onClick = { sendAction(Action.NavigateToLanguage) }
        ),
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_enable_keyboard),
            optionKeyString = null,
            iconID = R.drawable.ic_keyboard_white_24dp,
            onClick = { sendAction(Action.EnableKeyboard) }
        ),
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_change_keyboard),
            optionKeyString = null,
            iconID = R.drawable.ic_baseline_compare_arrows_18,
            onClick = { sendAction(Action.ChangeKeyboard) }
        ),
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_settings),
            optionKeyString = null,
            iconID = R.drawable.ic_baseline_settings_18,
            onClick = { sendAction(Action.NavigateToPreferences) }
        ),
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_test_keyboard),
            optionKeyString = null,
            iconID = R.drawable.ic_baseline_edit_note_24,
            onClick = { sendAction(Action.NavigateToTestKeyboard) }
        ),
    )

    Scaffold(
        modifier = Modifier
            .background(Color.Black),
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            contentPadding = PaddingValues(
                top = (screenHeight * 0.21).dp,
                bottom = (screenHeight * 0.36).dp,
                start = (screenHeight * 0.05).dp,
                end = (screenHeight * 0.05).dp),
            state = listState,
        ) {
            items(menuItems) { menuItem ->
                OptionsListElementWear(modifier = Modifier.height(50.dp), menuItem = menuItem)
            }
        }
    }
}

@Composable
fun OptionsListElementWear(
    modifier: Modifier = Modifier,
    menuItem: MainMenuItem
) {
    Chip(
        onClick = { menuItem.onClick() },
        modifier = modifier
            .fillMaxWidth(),
        label = {
            Text(
                text = menuItem.name,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface
            )
        },
        icon = {
            Icon(
                painter = painterResource(menuItem.iconID),
                contentDescription = menuItem.name,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colors.onSurface
            )
        },
        colors = ChipDefaults.secondaryChipColors()
    )
}