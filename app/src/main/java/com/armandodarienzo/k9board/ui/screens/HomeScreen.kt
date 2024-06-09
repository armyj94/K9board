package com.armandodarienzo.k9board.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_LANGUAGE

import com.armandodarienzo.k9board.model.MainMenuItem
import com.armandodarienzo.k9board.ui.navigation.Screens
import com.armandodarienzo.k9board.viewmodel.HomeScreenViewModel


@Preview
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

    HomeScreenContent(menuItems = menuItems)
}

@Preview
@Composable
fun OptionsListElementPreview() {
    OptionsListElement(
        modifier = Modifier.height(80.dp),
        menuItem =
        MainMenuItem(
            name = "Lingua",
            optionKeyString = "Italiano",
            iconID = R.drawable.ic_language_white_18dp,
            onClick = {}))
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
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_settings),
            optionKeyString = null,
            iconID = R.drawable.ic_baseline_settings_18,
            onClick = {
                navController.navigate(route = Screens.PreferencesScreen.name)
            }
//            navigationRoute = Screens.PreferencesScreen.name
        ),
        //@TODO: enable this again after wearOS change
//        MainMenuItem(
//            name = stringResource(id = R.string.main_activity_sync),
//            optionKeyString = null,
//            iconID = R.drawable.ic_sync_white_12dp
//        )
//    menuItems.add(MainMenuAdapter.MenuItem(R.drawable.ic_edit_white_18dp, this.getString(R.string.main_activity_test)))
    )

    HomeScreenContent(menuItems = menuItems)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    menuItems: List<MainMenuItem>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.inverseOnSurface,
                ),
            )
        }
    ) { paddingValues ->
        Column(
            Modifier.padding(paddingValues)
        ) {
            menuItems.forEach{ menuItem ->
                OptionsListElement(modifier = Modifier.height(80.dp), menuItem = menuItem)
            }
        }
    }
}



@Composable
fun OptionsListElement(
    modifier: Modifier = Modifier,
    menuItem: MainMenuItem) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp)
            .clickable {
                menuItem.onClick()
            },
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = modifier.size(40.dp),
                painter = painterResource(menuItem.iconID),
                contentDescription = menuItem.name,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = menuItem.name,
                style = MaterialTheme.typography.headlineSmall,

                )

            //TODO: recover data correctly once Language selection is implemented
//            if (menuItem.optionKeyString != null) {
//                val optionKey = stringPreferencesKey(menuItem.optionKeyString!!)
//                var languageSetState = flow{
//                    context.dataStore.data.map {
//                        it[optionKey]
//                    }.collect(collector = {
//                        if (it!=null){
//                            this.emit(it)
//                        }
//                    })
//                }.collectAsState(initial = "us-US")
//                Text(
//                    text = languageSetState.value,
//                    style = MaterialTheme.typography.bodyMedium,
//                )
//            }
        }
    }
}