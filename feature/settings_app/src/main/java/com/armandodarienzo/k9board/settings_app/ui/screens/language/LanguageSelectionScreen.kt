package com.armandodarienzo.k9board.settings_app.ui.screens.language

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.armandodarienzo.k9board.model.SupportedLanguageTag
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.model.DatabaseStatus
import com.armandodarienzo.k9board.shared.ui.elements.AppBarIcon
import com.armandodarienzo.k9board.shared.ui.elements.K9BoardTopAppBar
import com.armandodarienzo.k9board.settings_app.ui.base.rememberFlowWithLifecycle
import com.armandodarienzo.k9board.settings_app.ui.screens.language.LanguageSelectionReducer.LanguageSelectionState
import com.armandodarienzo.k9board.settings_app.ui.screens.language.LanguageSelectionViewModel.Action
import java.util.Locale
import com.armandodarienzo.k9board.shared.R.drawable as K9BOARD_DRAWABLES

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

    LanguageSelectionContent(state = state, sendAction = viewModel::processAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionContent(
    state: LanguageSelectionState,
    sendAction: (Action) -> Unit,
) {
    val languageTags = SupportedLanguageTag.entries.map { it.value }

    Scaffold(
        topBar = {
            K9BoardTopAppBar(
                title = stringResource(id = R.string.main_activity_languages),
                icon = AppBarIcon(Icons.Default.ArrowBack) { sendAction(Action.NavigateBack) }
            )
        }
    ) { paddingValues ->
        LazyColumn(Modifier.padding(paddingValues)) {
            items(languageTags) { tag ->
                val status = state.downloadStatus[tag]
                Row(
                    Modifier
                        .height(100.dp)
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
                        databaseStatus = status?.state ?: DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED,
                        downloadProgress = status?.progress ?: 0F,
                        sendAction = sendAction,
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageRow(
    tag: String,
    selectedOption: String,
    databaseStatus: DatabaseStatus.Companion.Statuses = DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED,
    downloadProgress: Float = 0F,
    sendAction: (Action) -> Unit,
) {
    val locale = Locale.forLanguageTag(tag)

    Card(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        .weight(8f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.padding(start = 12.dp),
                        text = locale.displayName.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
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
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        strokeWidth = 4.dp,
                                        progress = downloadProgress
                                    )
                                    IconButton(onClick = { sendAction(Action.CancelDownload(tag)) }) {
                                        Icon(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(6.dp),
                                            painter = painterResource(K9BOARD_DRAWABLES.round_cancel_24),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                            }
                            DatabaseStatus.Companion.Statuses.DOWNLOADED -> {
                                IconButton(onClick = { sendAction(Action.RemoveLanguagePack(tag)) }) {
                                    Icon(
                                        modifier = Modifier.size(40.dp),
                                        painter = painterResource(K9BOARD_DRAWABLES.rounded_delete_forever_24),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                            else -> {
                                IconButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = { sendAction(Action.Download(tag)) }
                                ) {
                                    Icon(
                                        modifier = Modifier.size(40.dp),
                                        painter = painterResource(K9BOARD_DRAWABLES.round_download_24),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
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