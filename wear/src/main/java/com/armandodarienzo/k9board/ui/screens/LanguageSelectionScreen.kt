package com.armandodarienzo.k9board.ui.screens


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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import com.armandodarienzo.k9board.shared.model.SupportedLanguageTag
import com.armandodarienzo.k9board.shared.packName
import com.armandodarienzo.k9board.shared.viewmodel.LanguageViewModel
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
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
fun LanguageListPreview() {
    val assetPackStates = emptyMap<String, AssetPackState>().toMutableMap()
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.Black),
    ) {
        LanguagesScreenContent(
            selectedOption = "us-US",
            assetPackStates = assetPackStates,
            onSelected = {},
            onDownload = {},
            onCancel = {},
            onRemove = {},
        )
    }

}


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
    val listState = rememberScalingLazyListState(initialCenterItemIndex = 0)
    val screenHeight = LocalConfiguration.current.screenHeightDp

    Scaffold(
    ) {
        ScalingLazyColumn(
            //https://developer.android.com/design/ui/wear/guides/components/lists?hl=it
            contentPadding = PaddingValues(
                top = (screenHeight * 0.21).dp,
                bottom = (screenHeight * 0.36).dp,
                start = (screenHeight * 0.05).dp,
                end = (screenHeight * 0.05).dp),
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
                        .height(60.dp)
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
        modifier = Modifier
            .height(100.dp)
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
                    if (assetPackStatus == AssetPackStatus.DOWNLOADING
                        || assetPackStatus == AssetPackStatus.TRANSFERRING) {
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
                                modifier = Modifier.fillMaxSize(),
                                onClick = { onCancel(tag) },
                                colors = ButtonDefaults.secondaryButtonColors(
                                    contentColor = MaterialTheme.colors.onSurface
                                )
                            ) {
                                Icon(
                                    painter = painterResource(K9BOARD_DRAWABLES.round_cancel_24),
                                    contentDescription = "Cancel"
                                )
                            }
                        }
                    } else if (assetPackStatus == AssetPackStatus.COMPLETED
                        || assetPackLocation != null) {
                        Button(
                            modifier = Modifier.fillMaxSize(),
                            onClick = { onRemove(tag) },
                            colors = ButtonDefaults.secondaryButtonColors()

                        ) {
                            Icon(
                                painter = painterResource(K9BOARD_DRAWABLES.rounded_delete_forever_24),
                                contentDescription = "Delete"
                            )
                        }
                    } else {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { onDownload(tag) },
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

