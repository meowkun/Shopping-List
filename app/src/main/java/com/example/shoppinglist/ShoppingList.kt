package com.example.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ShoppingList() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        var shoppingItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
        var showDialog by remember { mutableStateOf(false) }
        var itemName by remember { mutableStateOf("") }
        var qty by remember { mutableStateOf("") }

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
                }
            }
        }
        if (showDialog) {
            AlertDialog(onDismissRequest = { showDialog = false },
                confirmButton = {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            if (itemName.isNotBlank()) {
                                val newItem = ShoppingItem(
                                    id = shoppingItems.size + 1,
                                    name = itemName,
                                    quantity = qty.toInt()
                                )
                                shoppingItems += newItem
                                showDialog = false
                                itemName = ""
                                qty = ""
                            }
                        }) {
                            Text(text = "Add")
                        }

                        Button(onClick = {
                            showDialog = false
                            itemName = ""
                            qty = ""
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
                            onValueChange = { itemName = it },
                            label = { Text(text = "Item") }
                        )

                        OutlinedTextField(
                            value = qty,
                            onValueChange = { qty = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(text = "Qty") }
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    ShoppingList()
}