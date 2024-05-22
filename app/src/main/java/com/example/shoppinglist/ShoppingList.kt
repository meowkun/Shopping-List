package com.example.shoppinglist

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.locationapp.LocationUtils

@Composable
fun ShoppingListApp(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    navController: NavController,
    context: Context,
    address: String
) {
    val requestsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            ) {
                locationUtils.requestLocationUpdate(viewModel = viewModel)
            } else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                if (rationaleRequired) {
                    Toast.makeText(
                        context,
                        "Location Permission is required for this feature to work",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Location Permission is required. Please enable it in android setting",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        var shoppingItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
        var showDialog by remember { mutableStateOf(false) }
        var itemName by remember { mutableStateOf("") }
        var qty by remember { mutableStateOf("") }

        val onAddClick = {
            if (itemName.isNotBlank()) {
                val newItem = ShoppingItem(
                    id = shoppingItems.size + 1,
                    name = itemName,
                    quantity = qty.toIntOrNull() ?: 1,
                    address = address
                )
                shoppingItems += newItem
                showDialog = false
                itemName = ""
                qty = ""
            }
        }
        val onEditClick: (Int) -> Unit = { id ->
            shoppingItems = shoppingItems.map { it.copy(isEditing = it.id == id) }
        }
        val onDeleteClick: (ShoppingItem) -> Unit = {
            shoppingItems -= it
        }
        val onSaveClick: (Int, String, String) -> Unit =
            { id: Int, name: String, quantity: String ->
                shoppingItems.find { shoppingItem -> shoppingItem.id == id }?.let {
                    it.name = name
                    it.quantity = quantity.toIntOrNull() ?: 1
                }
                shoppingItems = shoppingItems.map { it.copy(isEditing = false) }
            }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(text = "Add Item")
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(shoppingItems) {
                    ShoppingListItem(
                        shoppingItem = it,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick,
                        onSaveClick = onSaveClick
                    )
                }
            }
        }
        AddItemDialog(
            showDialog = showDialog,
            itemName = itemName,
            qty = qty,
            onDismiss = { showDialog = false },
            onAddClick = {
                onAddClick.invoke()
            },
            onCancelClick = {
                showDialog = false
                itemName = ""
                qty = ""
            },
            onItemNameChange = {
                itemName = it
            },
            onItemQtyChange = {
                qty = it
            },
            locationUtils = locationUtils,
            context = context,
            viewModel = viewModel,
            navController = navController,
            requestsPermission = {
                requestsPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        )
    }
}

@Composable
fun ShoppingListItem(
    shoppingItem: ShoppingItem,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (ShoppingItem) -> Unit,
    onSaveClick: (Int, String, String) -> Unit
) {
    if (shoppingItem.isEditing) {
        ShoppingItemEditor(
            shoppingItem = shoppingItem,
            onSaveClick = onSaveClick
        )
    } else {
        ReadOnlyItemRow(
            shoppingItem = shoppingItem,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick
        )
    }
}

@Composable
fun ReadOnlyItemRow(
    shoppingItem: ShoppingItem,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (ShoppingItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Row {
                Text(text = shoppingItem.name)
                Text(text = "Qty: ${shoppingItem.quantity}")
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                Text(text = shoppingItem.address)
            }
        }

        Row {
            IconButton(onClick = { onEditClick(shoppingItem.id) }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Button"
                )
            }
            IconButton(onClick = { onDeleteClick.invoke(shoppingItem) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Button"
                )
            }
        }
    }
}

@Composable
fun ShoppingItemEditor(
    shoppingItem: ShoppingItem,
    onSaveClick: (Int, String, String) -> Unit
) {
    var editingName by remember { mutableStateOf(shoppingItem.name) }
    var editingQty by remember { mutableStateOf(shoppingItem.quantity.toString()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicTextField(
                value = shoppingItem.name,
                onValueChange = { editingName = it }
            )
            BasicTextField(
                value = shoppingItem.quantity.toString(),
                onValueChange = { editingQty = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        Button(onClick = {
            onSaveClick.invoke(
                shoppingItem.id,
                editingName,
                editingQty
            )
        }) {
            Text(text = "Save")
        }
    }
}

@Composable
fun AddItemDialog(
    showDialog: Boolean,
    itemName: String,
    qty: String,
    locationUtils: LocationUtils,
    context: Context,
    viewModel: LocationViewModel,
    navController: NavController,
    onDismiss: () -> Unit,
    onAddClick: () -> Unit,
    onCancelClick: () -> Unit,
    onItemNameChange: (String) -> Unit,
    onItemQtyChange: (String) -> Unit,
    requestsPermission: () -> Unit
) {
    if (showDialog) {
        AlertDialog(onDismissRequest = { onDismiss.invoke() },
            confirmButton = {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        onAddClick.invoke()
                    }) {
                        Text(text = "Add")
                    }

                    Button(onClick = {
                        onCancelClick.invoke()
                    }) {
                        Text("Cancel")
                    }
                }
            },
            title = {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = "Add Shopping Item"
                    )
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { onItemNameChange.invoke(it) },
                        label = { Text(text = "Item") }
                    )

                    OutlinedTextField(
                        value = qty,
                        onValueChange = { onItemQtyChange.invoke(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text(text = "Qty") }
                    )
                    Button(onClick = {
                        if(locationUtils.hasLocationPermission(context = context)) {
                            locationUtils.requestLocationUpdate(viewModel = viewModel)
                            navController.navigate("locationScreen") {
                                this.launchSingleTop
                            }
                        } else {
                            requestsPermission()
                        }
                    }) {
                        Text(text = "Address")
                    }
                }
            }
        )
    }
}