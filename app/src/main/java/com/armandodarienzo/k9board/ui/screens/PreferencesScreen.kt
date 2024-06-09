package com.armandodarienzo.k9board.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.model.KeyboardSize
import com.armandodarienzo.k9board.shared.viewmodel.PreferencesViewModel
import com.armandodarienzo.k9board.ui.elements.AppBarIcon
import com.armandodarienzo.k9board.ui.elements.K9BoardTopAppBar
import com.armandodarienzo.k9board.ui.elements.K9BoardTopAppBarPreview
import com.armandodarienzo.k9board.ui.elements.RadioDialog
import com.armandodarienzo.k9board.ui.elements.RadioOption

@Preview
@Composable
fun PreferencesScreenPreview() {
    PreferencesScreenContent(
        onBackIconClicked = {},
        keyboardSizeSelected = KeyboardSize.MEDIUM,
        onKeyboardSizeSelected = {}
    )
}

@Composable
fun PreferencesScreen(
    navController: NavController,
    viewModel : PreferencesViewModel = hiltViewModel()
){

    val onBackIconClicked : () -> Unit = {
        navController.popBackStack()
    }

    val keyboardSizeSelected = viewModel.keyboardSizeState.value
    val onKeyboardSizeSelected : (KeyboardSize) -> Unit = {
        viewModel.setKeyboardSize(it)
    }

    PreferencesScreenContent(
        onBackIconClicked,
        keyboardSizeSelected,
        onKeyboardSizeSelected
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreenContent(
    onBackIconClicked : () -> Unit,
    keyboardSizeSelected : KeyboardSize,
    onKeyboardSizeSelected : (KeyboardSize) -> Unit
) {
    val openAlertDialog = remember { mutableStateOf(false) }
    val radioOptions = KeyboardSize.values().map {
        val label = when (it) {
            KeyboardSize.SMALL -> "Small"
            KeyboardSize.MEDIUM -> "Medium"
            KeyboardSize.LARGE -> "Large"
            KeyboardSize.VERY_SMALL -> "Very small"
            KeyboardSize.VERY_LARGE -> "Very Large"
        }
        RadioOption(label, keyboardSizeSelected == it, it)
    }.toTypedArray()


    when {
        // ...
        openAlertDialog.value -> {
            RadioDialog(
                title = "Keyboard Size" ,
                options = radioOptions,
                onDismissRequest = {
//                    onKeyboardSizeSelected
                    openAlertDialog.value = false
                }) {
                onKeyboardSizeSelected(it.value)
            }
        }
    }



    Scaffold (
        topBar = {
            K9BoardTopAppBar(
                title = stringResource(id = R.string.main_activity_settings),
                icon = AppBarIcon(
                    imageVector = Icons.Default.ArrowBack,
                ) {
                    onBackIconClicked()
                }
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                SectionText(text = "Layout and aspect")
            }

            OptionRow(
                optionName = "Keyboard size",
                onClick = { openAlertDialog.value = true }) {
                
            }

//            radioOptions.forEach { option ->
//                Row(
//                    Modifier
//                        // using modifier to add max
//                        // width to our radio button.
//                        .fillMaxWidth()
//                        // below method is use to add
//                        // selectable to our radio button.
//                        .selectable(
//                            // this method is called when
//                            // radio button is selected.
//                            selected = (option == keyboardSizeSelected),
//                            // below method is called on
//                            // clicking of radio button.
//                            onClick = { onKeyboardSizeSelected(option) }
//                        )
//                        // below line is use to add
//                        // padding to radio button.
//                        .padding(horizontal = 16.dp)
//                ) {
//                    // below line is use to get context.
//                    val context = LocalContext.current
//
//                    // below line is use to
//                    // generate radio button
//                    RadioButton(
//                        // inside this method we are
//                        // adding selected with a option.
//                        selected = (option == keyboardSizeSelected),modifier = Modifier.padding(all = Dp(value = 8F)),
//                        onClick = {
//                            // inside on click method we are setting a
//                            // selected option of our radio buttons.
//                            onKeyboardSizeSelected(option)
//
//                            // after clicking a radio button
//                            // we are displaying a toast message.
//                            Toast.makeText(context, option.value.toString(), Toast.LENGTH_LONG).show()
//                        }
//                    )
//                    // below line is use to add
//                    // option to our radio buttons.
//                    Text(
//                        text = option.name.toString(),
//                        modifier = Modifier.padding(start = 16.dp)
//                    )
//                }
//            }

        }
    }
}

@Composable
fun SectionText(
    text: String
) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 16.dp),
        style = MaterialTheme.typography.headlineMedium.plus(
            TextStyle(
                color = MaterialTheme.colorScheme.primary
            )
        )
    )
}

@Composable
fun OptionNameText (
    text: String
) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 16.dp),
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
fun OptionRow (
    optionName: String,
    onClick: () -> Unit,
    optionDisplay: @Composable () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .height(40.dp)
            .clickable { onClick() },
    ) {
        Column(
            Modifier.weight(5f)
        ) {
            OptionNameText(text = optionName)
        }
        Column(
            Modifier
                .weight(2f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            optionDisplay()
        }

    }
}

@Composable
fun OptionValueText (
    text: String
) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 16.dp),
        style = MaterialTheme.typography.bodyLarge,
    )
}