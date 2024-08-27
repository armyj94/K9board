package com.armandodarienzo.k9board.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.armandodarienzo.k9board.shared.LANGUAGE_TAG_ENGLISH_AMERICAN
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_LANGUAGE
import com.armandodarienzo.k9board.shared.model.SupportedLanguageTag
import com.armandodarienzo.k9board.shared.packName
import com.armandodarienzo.k9board.shared.repository.dataStore
import com.armandodarienzo.k9board.shared.viewmodel.LanguageViewModel
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import java.util.Locale

@Composable
fun LanguageSelectionScreen(
    navController: NavController,
    viewModel: LanguageViewModel = hiltViewModel()
) {

    val selectedOption by viewModel.languageState
    val assetPackStates by viewModel.assetPackStatesMapState

    LanguagesList(
        selectedOption = selectedOption,
        assetPackStates = assetPackStates,
        onSelected = { viewModel.setLanguage(it) },
        onDownload = { viewModel.downloadLanguagePack(it) },
        onCancel = {},
        onRemove = {},
    )
}

//@Composable
//@Preview
//fun LanguageListPreview() {
//    val assetPackStates = emptyMap<String, AssetPackState>().toMutableMap()
//
//    LanguagesList(
//        selectedOption = "us-US",
//        assetPackStates = assetPackStates,
//        onSelected = {},
//        onDownload = {},
//        onCancel = {},
//        onRemove = {},
//    )
//}

@Composable
fun LanguagesList(
    selectedOption: String,
    assetPackStates: Map<String, AssetPackState>,
    onSelected: (String) -> Unit,
    onDownload: (String) -> Unit,
    onCancel: (String) -> Unit,
    onRemove: () -> Unit
){
    val TAG = object {}::class.java.enclosingMethod?.name

    val context = LocalContext.current
    val assetPackManager = remember{ AssetPackManagerFactory.getInstance(context) }

    val languageTags = remember{ SupportedLanguageTag.entries.map{ it.value } }

    LazyColumn(){
        items(languageTags) { tag ->
            val packName = packName(tag)
            val assetPackState = assetPackStates[packName]
            val assetPackStatus = assetPackState?.status() ?: AssetPackStatus.UNKNOWN
            val assetPackLocation = assetPackManager.getPackLocation(packName)?.path()

            var downloaded by remember {
                mutableLongStateOf( assetPackState?.bytesDownloaded() ?: 0 )
            }
            val totalSize: Long = assetPackState?.totalBytesToDownload() ?: 1
            var downloadProgress by remember {
                mutableFloatStateOf ( (100L * downloaded / totalSize) / 100F )
            }

            LaunchedEffect(assetPackState?.bytesDownloaded()) {
                assetPackState?.bytesDownloaded()?.let {
                    downloaded = it
                }
                downloadProgress = (100L * downloaded / totalSize) / 100F
            }

            Row(
                Modifier
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

@Composable
@Preview
fun LanguageRowPreview() {
    LanguageRow(
        assetPackLocation = "null",
        tag = LANGUAGE_TAG_ENGLISH_AMERICAN,
        selectedOption = LANGUAGE_TAG_ENGLISH_AMERICAN,
        assetPackStatus = AssetPackStatus.DOWNLOADING,
        downloadProgress = 60F,
        onSelected = {},
        onDownload = {},
        onCancel = {},
        onRemove = {}
    )
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
    onRemove: () -> Unit
){
    val locale = Locale.forLanguageTag(tag)

    Card(
        modifier = Modifier
            .fillMaxWidth()
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
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    modifier = Modifier.weight(1f),
                    enabled = (assetPackLocation != null),
                    selected = (tag == selectedOption),
                    onClick = {
                        onSelected(tag)
                    }
                )
                Text(
                    text = locale.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .weight(8f)
                        .padding(start = 8.dp)
                )
                if (assetPackStatus == AssetPackStatus.DOWNLOADING) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator(
                            modifier = Modifier.size(35.dp),
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
                                modifier = Modifier.size(25.dp),
                                painter = painterResource(com.armandodarienzo.k9board.shared.R.drawable.round_cancel_24),
                                contentDescription = "Download",
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                } else if (assetPackLocation != null) {
                    IconButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onRemove()
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            painter = painterResource(com.armandodarienzo.k9board.shared.R.drawable.rounded_delete_forever_24),
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
                            painter = painterResource(com.armandodarienzo.k9board.shared.R.drawable.round_download_24),
                            contentDescription = "Download",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }


}

suspend fun changeLanguage(context: Context, value: String){
    val languageSetKey = stringPreferencesKey(SHARED_PREFS_SET_LANGUAGE)
    context.dataStore.edit {
        it[languageSetKey] = value
    }
}

