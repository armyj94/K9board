package com.armandodarienzo.k9board.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.armandodarienzo.k9board.R
import com.armandodarienzo.k9board.shared.ASSET_PACKS_BASE_NAME
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

@Composable
@Preview
fun LanguageListPreview() {
    val assetPackStates = emptyMap<String, AssetPackState>().toMutableMap()

    LanguagesList(
        selectedOption = "us-US",
        assetPackStates = assetPackStates,
        onSelected = {},
        onDownload = {},
        onCancel = {},
        onRemove = {},
    )
}

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

    val languageTags = remember{ SupportedLanguageTag.values().map{ it.value } }

    Column {
        languageTags.forEach { tag ->
            val packName = packName(tag)
            val locale = Locale.forLanguageTag(tag)
            val assetPackState = assetPackStates[packName]
            val assetPackStatus = assetPackState?.status() ?: AssetPackStatus.UNKNOWN
            val assetPackLocation = assetPackManager.getPackLocation(packName)

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
                RadioButton(
                    enabled = (assetPackLocation != null),
                    selected = (tag == selectedOption),
                    onClick = {
                        onSelected(tag)
                    }
                )
                Text(
                    text = locale.displayName,
                    style = MaterialTheme.typography.bodySmall.merge(),
                    modifier = Modifier.padding(start = 8.dp, top = 16.dp)
                )
                if (assetPackLocation == null) {
                    IconButton(
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
                } else if (assetPackStatus == AssetPackStatus.DOWNLOADING) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        strokeWidth = 4.dp,
                        progress = assetPackState?.transferProgressPercentage()?.toFloat() ?: 0F
                    )
                    IconButton(
                        onClick = {
                            onCancel(tag)
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            painter = painterResource(com.armandodarienzo.k9board.shared.R.drawable.round_cancel_24),
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

