package com.armandodarienzo.k9board.ui.screens


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.RadioButton
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.armandodarienzo.k9board.model.SupportedLanguageTag
import com.armandodarienzo.k9board.shared.model.DatabaseStatus
import com.armandodarienzo.k9board.shared.ui.base.rememberFlowWithLifecycle
import com.armandodarienzo.k9board.settings_app.ui.screens.language.LanguageSelectionReducer
import com.armandodarienzo.k9board.settings_app.ui.screens.language.LanguageSelectionReducer.LanguageSelectionState
import com.armandodarienzo.k9board.settings_app.ui.screens.language.LanguageSelectionViewModel
import com.armandodarienzo.k9board.settings_app.ui.screens.language.LanguageSelectionViewModel.Action
import com.armandodarienzo.k9board.shared.R.drawable as K9BOARD_DRAWABLES
import java.util.Locale

@Composable
fun LanguageSelectionScreen(
    navController: NavController,
    viewModel: LanguageSelectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val effectFlow = rememberFlowWithLifecycle(viewModel.effect)

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                LanguageSelectionReducer.Effect.NavigateBack -> navController.popBackStack()
            }
        }
    }

    LanguagesScreenContent(state = state, sendAction = viewModel::processAction)
}

@Composable
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
fun LanguageListPreview() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.Black),
    ) {
        LanguagesScreenContent(
            state = LanguageSelectionState.initial(),
            sendAction = {},
        )
    }
}


@Composable
fun LanguagesScreenContent(
    state: LanguageSelectionState,
    sendAction: (Action) -> Unit,
){
    val languageTags = remember{ SupportedLanguageTag.entries.map{ it.value } }
    val listState = rememberScalingLazyListState(initialCenterItemIndex = 0)
    val screenHeight = LocalConfiguration.current.screenHeightDp

    Scaffold(
    ) {
        ScalingLazyColumn(
            contentPadding = PaddingValues(
                top = (screenHeight * 0.21).dp,
                bottom = (screenHeight * 0.36).dp,
                start = (screenHeight * 0.05).dp,
                end = (screenHeight * 0.05).dp),
        ) {
            items(languageTags) { tag ->
                val status = state.downloadStatus[tag]
                val downloadState = status?.state ?: DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED
                val progress = status?.progress ?: 0F

                Log.d("LanguageSelectionScreen", "progress is $progress")

                Row(
                    Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                        .selectable(
                            selected = (tag == state.selectedLanguage),
                            onClick = {}
                        )
                        .padding(horizontal = 16.dp)
                ) {
                    LanguageRow(
                        tag = tag,
                        selectedOption = state.selectedLanguage,
                        databaseStatus = downloadState,
                        downloadProgress = progress,
                        sendAction = sendAction,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun LanguageRowPreview() {
    Column(
        modifier = Modifier
            .height(100.dp)
    ) {
        LanguageRow(
            tag = SupportedLanguageTag.ITALIAN.value,
            selectedOption = SupportedLanguageTag.ITALIAN.value,
            downloadProgress = 60F,
            sendAction = {},
        )
    }
}

@Composable
fun LanguageRow(
    tag: String,
    selectedOption: String,
    databaseStatus: DatabaseStatus.Companion.Statuses = DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED,
    downloadProgress: Float = 0F,
    sendAction: (Action) -> Unit,
){
    val locale = Locale.forLanguageTag(tag)

    Log.d("LanguageSelectionScreen", "progress in LanguageRow is $downloadProgress")

    Card(
        onClick = {},
        backgroundPainter = CardDefaults.cardBackgroundPainter(
            startBackgroundColor = MaterialTheme.colors.surface,
            endBackgroundColor = MaterialTheme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    RadioButton(
                        modifier = Modifier.fillMaxSize(),
                        enabled = (databaseStatus == DatabaseStatus.Companion.Statuses.DOWNLOADED),
                        selected = (tag == selectedOption),
                        onClick = { sendAction(Action.SelectLanguage(tag)) }
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(4f)
                        .padding(start = 10.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = locale.displayLanguage.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = locale.displayCountry.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.caption3,
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    if (tag != SupportedLanguageTag.AMERICAN.value) {
                        when (databaseStatus) {
                            DatabaseStatus.Companion.Statuses.DOWNLOADING -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ){
                                    CircularProgressIndicator(
                                        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                                        indicatorColor = MaterialTheme.colors.primary,
                                        strokeWidth = 4.dp,
                                        progress = downloadProgress
                                    )
                                    Button(
                                        modifier = Modifier
                                            .fillMaxWidth(0.7f)
                                            .aspectRatio(1f),
                                        onClick = { sendAction(Action.CancelDownload(tag)) },
                                        colors = ButtonDefaults.secondaryButtonColors(
                                            backgroundColor = Color.Transparent,
                                            contentColor = MaterialTheme.colors.onSurface
                                        )
                                    ) {
                                        Icon(
                                            painter = painterResource(K9BOARD_DRAWABLES.round_cancel_24),
                                            contentDescription = "Cancel"
                                        )
                                    }
                                }
                            }
                            DatabaseStatus.Companion.Statuses.DOWNLOADED -> {
                                Button(
                                    modifier = Modifier.fillMaxSize(),
                                    onClick = { sendAction(Action.RemoveLanguagePack(tag)) },
                                    colors = ButtonDefaults.secondaryButtonColors()
                                ) {
                                    Icon(
                                        painter = painterResource(K9BOARD_DRAWABLES.rounded_delete_forever_24),
                                        contentDescription = "Delete"
                                    )
                                }
                            }
                            else -> {
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = { sendAction(Action.Download(tag)) },
                                    colors = ButtonDefaults.secondaryButtonColors()
                                ) {
                                    Icon(
                                        painter = painterResource(K9BOARD_DRAWABLES.round_download_24),
                                        contentDescription = "Download"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

