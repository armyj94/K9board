package com.armandodarienzo.k9board.shared.ui.elements

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun K9BoardTopAppBar(
    title: String,
    icon: AppBarIcon? = null
) {
    TopAppBar(
        title = { Text(title) },
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
                        contentDescription = null
                    )
                }
            }
        }
    )
}