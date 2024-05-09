package com.armandodarienzo.k9board.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.armandodarienzo.k9board.shared.model.KeyboardSize
import com.armandodarienzo.k9board.shared.viewmodel.PreferencesViewModel
import kotlinx.coroutines.launch

import kotlinx.coroutines.selects.ProcessResultFunction
import java.util.prefs.Preferences

@Preview
@Composable
fun PreferencesScreenPreview() {
    PreferencesScreen(navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    navController: NavController,
    viewModel : PreferencesViewModel = hiltViewModel()
){
    val radioOptions = KeyboardSize.values()


    val selectedOption = viewModel.keyboardSizeState.value

    val onOptionSelected : (KeyboardSize) -> Unit = {
        viewModel.setKeyboardSize(it)
    }


//    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[2]) }



    Scaffold() {
        Column(modifier = Modifier.padding(it)) {

            radioOptions.forEach { option ->
                Row(
                    Modifier
                        // using modifier to add max
                        // width to our radio button.
                        .fillMaxWidth()
                        // below method is use to add
                        // selectable to our radio button.
                        .selectable(
                            // this method is called when
                            // radio button is selected.
                            selected = (option == selectedOption),
                            // below method is called on
                            // clicking of radio button.
                            onClick = { onOptionSelected(option) }
                        )
                        // below line is use to add
                        // padding to radio button.
                        .padding(horizontal = 16.dp)
                ) {
                    // below line is use to get context.
                    val context = LocalContext.current

                    // below line is use to
                    // generate radio button
                    RadioButton(
                        // inside this method we are
                        // adding selected with a option.
                        selected = (option == selectedOption),modifier = Modifier.padding(all = Dp(value = 8F)),
                        onClick = {
                            // inside on click method we are setting a
                            // selected option of our radio buttons.
                            onOptionSelected(option)

                            // after clicking a radio button
                            // we are displaying a toast message.
                            Toast.makeText(context, option.value.toString(), Toast.LENGTH_LONG).show()
                        }
                    )
                    // below line is use to add
                    // option to our radio buttons.
                    Text(
                        text = option.name.toString(),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }

        }
    }
}