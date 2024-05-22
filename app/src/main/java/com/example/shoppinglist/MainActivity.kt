package com.example.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.locationapp.LocationUtils
import com.example.shoppinglist.ui.theme.ShoppingListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
                // A surface container using the 'background' color from the theme
                Navigation()
            }
        }
    }
}

@Composable
fun Navigation() {
    val navHostController = rememberNavController()
    val viewModel: LocationViewModel = viewModel()
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)

    NavHost(navController = navHostController, startDestination = "shoppingListScreen") {
        composable(route = "shoppingListScreen") {
            ShoppingListApp(
                locationUtils = locationUtils,
                viewModel = viewModel,
                navController = navHostController,
                context = context,
                address = viewModel.address.value.firstOrNull()?.formattedAddress ?: "No Address"
            )
        }

        dialog("locationScreen") { backStack ->
            viewModel.location.value?.let { locationData ->
                LocationSelectionScree(locationData = locationData, onLocationSelected = {data->
                    viewModel.fetchAddress("${data.latitude},${data.longitude}")
                    navHostController.popBackStack()
                })
            }
        }
    }
}