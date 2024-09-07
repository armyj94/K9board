package com.armandodarienzo.k9board.ui.screens

import android.content.Context
import android.util.Log
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.model.SupportedLanguageTag
import com.armandodarienzo.k9board.shared.packName
import com.armandodarienzo.k9board.shared.viewmodel.LanguageViewModel
import com.armandodarienzo.k9board.ui.elements.AppBarIcon
import com.armandodarienzo.k9board.ui.elements.K9BoardTopAppBar
import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import com.armandodarienzo.k9board.shared.R.drawable as K9BOARD_DRAWABLES
import java.util.Locale

@Composable
fun LanguageSelectionScreen(
    navController: NavController,
    viewModel: LanguageViewModel = hiltViewModel()
) {
    val onBackIconClicked : () -> Unit = {
        navController.popBackStack()
    }

    val context = LocalContext.current

    val selectedOption by viewModel.languageState
    val assetPackStates by viewModel.assetPackStatesMapState
    val assetPackManager = remember{ AssetPackManagerFactory.getInstance(context) }

    LanguagesScreenContent(
        onBackIconClicked = onBackIconClicked,
        selectedOption = selectedOption,
        assetPackStates = assetPackStates,
        assetPackManager = assetPackManager,
        getDownloadProgress = { viewModel.getDownloadProgress(it) },
        onSelected = { viewModel.setLanguage(it) },
        onDownload = { viewModel.downloadLanguagePack(it) },
        onCancel = { viewModel.cancelDownload(it) },
        onRemove = { viewModel.removePack(it) },
    )
}

@Composable
@Preview
fun LanguageListPreview() {
    val assetPackStates = emptyMap<String, AssetPackState>().toMutableMap()

    LanguagesScreenContent(
        selectedOption = "us-US",
        assetPackStates = assetPackStates,
        onSelected = {},
        onDownload = {},
        onCancel = {},
        onRemove = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagesScreenContent(
    onBackIconClicked: () -> Unit = {},
    selectedOption: String,
    assetPackStates: Map<String, AssetPackState>,
    assetPackManager: AssetPackManager? = null,
    getDownloadProgress: (String) -> Float = { 0F },
    onSelected: (String) -> Unit,
    onDownload: (String) -> Unit,
    onCancel: (String) -> Unit,
    onRemove: (String) -> Unit
){
    val TAG = object {}::class.java.enclosingMethod?.name

    val languageTags = remember{ SupportedLanguageTag.entries.map{ it.value } }

    Scaffold(
        topBar = {
            K9BoardTopAppBar(
                title = stringResource(id = R.string.main_activity_languages),
                icon = AppBarIcon(
                    imageVector = Icons.Default.ArrowBack,
                ) {
                    onBackIconClicked()
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            Modifier.padding(paddingValues)
        ) {
            items(languageTags) { tag ->
                val packName = packName(tag)
                val assetPackState = assetPackStates[packName]
                val assetPackStatus = assetPackState?.status() ?: AssetPackStatus.UNKNOWN
                val assetPackLocation = assetPackManager?.getPackLocation(packName)?.path()


                var downloadProgress by remember {
                    mutableFloatStateOf(
                        getDownloadProgress(tag)
                    )
                }

                LaunchedEffect(getDownloadProgress(tag)) {
                    downloadProgress = getDownloadProgress(tag)
                }

                Row(
                    Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .selectable(
                            enabled = (assetPackLocation != null),
                            selected = (tag == selectedOption),
                            onClick = {
                                //onOptionSelected(tag)
                            }
                        )
                        .padding(horizontal = 16.dp)
                ) {
                    LanguageRow(
                        assetPackLocation = assetPackLocation,
                        tag = tag,
                        selectedOption = selectedOption,
                        assetPackStatus = assetPackStatus,
                        downloadProgress = downloadProgress,
                        onDownload = onDownload,
                        onSelected = onSelected,
                        onCancel = onCancel,
                        onRemove = onRemove
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
        modifier = Modifier.height(100.dp)
    ) {
        LanguageRow(
            assetPackLocation = "null",
            tag = SupportedLanguageTag.AMERICAN.value,
            selectedOption = SupportedLanguageTag.AMERICAN.value,
            assetPackStatus = AssetPackStatus.DOWNLOADING,
            downloadProgress = 60F,
            onSelected = {},
            onDownload = {},
            onCancel = {},
            onRemove = {}
        )
    }
}

@Composable
fun LanguageRow(
    assetPackLocation: String?,
    tag: String,
    selectedOption: String,
    assetPackStatus: Int,
    downloadProgress: Float = 0F,
    onSelected: (String) -> Unit?,
    onDownload: (String) -> Unit,
    onCancel: (String) -> Unit,
    onRemove: (String) -> Unit
){
    val locale = Locale.forLanguageTag(tag)

    Card(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
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
                        enabled = (assetPackLocation != null),
                        selected = (tag == selectedOption),
                        onClick = {
                            onSelected(tag)
                        }
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
                    if (assetPackStatus == AssetPackStatus.DOWNLOADING
                        || assetPackStatus == AssetPackStatus.TRANSFERRING) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ){
                            CircularProgressIndicator(
                                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                                color = MaterialTheme.colorScheme.onSurface,
                                strokeWidth = 4.dp,
                                progress = downloadProgress
                            )
                            IconButton(
                                onClick = {
                                    onCancel(tag)
                                }
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp),
                                    painter = painterResource(K9BOARD_DRAWABLES.round_cancel_24),
                                    contentDescription = "Download",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    } else if (assetPackStatus == AssetPackStatus.COMPLETED
                        || assetPackLocation != null) {
                        IconButton(
                            onClick = {
                                onRemove(tag)
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(40.dp),
                                painter = painterResource(
                                    K9BOARD_DRAWABLES.rounded_delete_forever_24
                                ),
                                contentDescription = "Download",
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    } else {
                        IconButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onDownload(tag)
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(40.dp),
                                painter = painterResource(K9BOARD_DRAWABLES.round_download_24),
                                contentDescription = "Download",
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }


}

