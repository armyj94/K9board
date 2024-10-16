package com.armandodarienzo.k9board.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold

import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.items
import androidx.wear.tooling.preview.devices.WearDevices
import com.armandodarienzo.k9board.model.MainMenuItem
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_LANGUAGE
import com.armandodarienzo.k9board.shared.ui.navigation.Screens
import com.armandodarienzo.k9board.shared.viewmodel.HomeScreenViewModel

@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Composable
fun ContentPreview() {
    val menuItems = listOf(
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_languages),
            optionKeyString = SHARED_PREFS_SET_LANGUAGE,
            iconID = R.drawable.ic_language_white_18dp,
        ),
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_enable_keyboard),
            optionKeyString = null,
            iconID = R.drawable.ic_keyboard_white_24dp,
        ),
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_change_keyboard),
            optionKeyString = null,
            iconID = R.drawable.ic_baseline_compare_arrows_18,
        ),
//        MainMenuItem(
//            name = stringResource(id = R.string.main_activity_privacy_policy),
//            optionKeyString = null,
//            iconID = R.drawable.ic_security_white_18dp,
//        ),
//        MainMenuItem(
//            name = stringResource(id = R.string.main_activity_tutorial),
//            optionKeyString = null,
//            iconID = R.drawable.ic_help_outline_white_18dp,
//        ),
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_settings),
            optionKeyString = null,
            iconID = R.drawable.ic_baseline_settings_18,
//            navigationRoute = Screens.PreferencesScreen.name
        )
        //@TODO: enable this again after wearOS change
//        MainMenuItem(
//            name = stringResource(id = R.string.main_activity_sync),
//            optionKeyString = null,
//            iconID = R.drawable.ic_sync_white_12dp
//        )
//    menuItems.add(MainMenuAdapter.MenuItem(R.drawable.ic_edit_white_18dp, this.getString(R.string.main_activity_test)))
    )

    HomeScreenContentWear(menuItems = menuItems)
}

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val menuItems = listOf(
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_languages),
            optionKeyString = SHARED_PREFS_SET_LANGUAGE,
            iconID = R.drawable.ic_language_white_18dp,
            onClick = {
                navController.navigate(route = Screens.LanguageSelectionScreen.name)
            }
        ),
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_enable_keyboard),
            optionKeyString = null,
            iconID = R.drawable.ic_keyboard_white_24dp,
            onClick = {
                viewModel.startEnableActivity()
            }
        ),
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_change_keyboard),
            optionKeyString = null,
            iconID = R.drawable.ic_baseline_compare_arrows_18,
            onClick = {
                viewModel.changeKeyboard()
            }
        ),
//        MainMenuItem(
//            name = stringResource(id = R.string.main_activity_privacy_policy),
//            optionKeyString = null,
//            iconID = R.drawable.ic_security_white_18dp,
//        ),
//        MainMenuItem(
//            name = stringResource(id = R.string.main_activity_tutorial),
//            optionKeyString = null,
//            iconID = R.drawable.ic_help_outline_white_18dp,
//        ),
//        MainMenuItem(
//            name = stringResource(id = R.string.main_activity_settings),
//            optionKeyString = null,
//            iconID = R.drawable.ic_baseline_settings_18,
//            onClick = {
//                navController.navigate(route = Screens.PreferencesScreen.name)
//            }
////            navigationRoute = Screens.PreferencesScreen.name
//        ),
    )

    HomeScreenContentWear(menuItems = menuItems)
}

@Composable
fun HomeScreenContentWear(
    menuItems: List<MainMenuItem>
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val listState = rememberScalingLazyListState(initialCenterItemIndex = 0)

    Scaffold(
        modifier = Modifier
            .background(Color.Black),
        timeText = { TimeText() }, // Display current time
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }, // Add vignette effect
        positionIndicator = {
            PositionIndicator(
                scalingLazyListState = listState)
        } // Show position indicator
    ) {
        ScalingLazyColumn(
            contentPadding = PaddingValues(
                top = (screenHeight * 0.21).dp,
                bottom = (screenHeight * 0.36).dp,
                start = (screenHeight * 0.05).dp,
                end = (screenHeight * 0.05).dp),
            state = listState
        ) {
            items(menuItems) { menuItem ->
                OptionsListElementWear(modifier = Modifier.height(80.dp), menuItem = menuItem) // Custom list element for Wear OS
            }
        }
    }
}

@Composable
fun OptionsListElementWear(
    modifier: Modifier = Modifier,
    menuItem: MainMenuItem
) {
    // Use a Chip for a more compact and visually appealing list item on Wear OS
    Chip(
        onClick = { menuItem.onClick() }, // Trigger the menuItem's onClick action
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp), // Adjust padding for Wear OS
        label = {
            Text(
                text = menuItem.name,
                style = MaterialTheme.typography.body1, // Use a suitable text style for Wear OS
                color = MaterialTheme.colors.onSurface
            )
        },
        icon = {
            Icon(
                painter = painterResource(menuItem.iconID),
                contentDescription = menuItem.name,
                modifier = Modifier.size(24.dp), // Adjust icon size for Wear OS
                tint = MaterialTheme.colors.onSurface
            )
        },
        colors = ChipDefaults.secondaryChipColors()
    )
}