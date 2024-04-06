package com.example.shoppinglist.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class MainViewModel: ViewModel() {
    val countDownFlow = flow<Int> {
        val initValue = 10
        var currentValue = initValue
        emit(initValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }
}