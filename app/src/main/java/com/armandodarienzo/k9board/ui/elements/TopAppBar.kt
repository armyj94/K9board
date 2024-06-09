package com.armandodarienzo.k9board.ui.elements

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.armandodarienzo.k9board.shared.R

@Composable
@Preview
fun K9BoardTopAppBarPreview() {
    K9BoardTopAppBar(
        stringResource(id = R.string.app_name),
        icon = AppBarIcon(Icons.Default.ArrowBack) {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun K9BoardTopAppBar(
    title: String,
    icon: AppBarIcon? = null
) {
    TopAppBar(
        title = {
            Text(title)
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.inverseOnSurface,
            actionIconContentColor = MaterialTheme.colorScheme.inverseOnSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.inverseOnSurface,
        ),
        navigationIcon = {
            icon?.let {
                IconButton(onClick = icon.onClick) {
                    Icon(
                        imageVector = icon.imageVector,
                        contentDescription = "Localized description"
                    )
                }
            }
        }
    )
}