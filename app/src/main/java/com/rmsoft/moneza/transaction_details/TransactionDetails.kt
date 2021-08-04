package com.rmsoft.moneza.transaction_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rmsoft.moneza.R

// refer:
// https://github.com/material-components/material-components-android/blob/master/docs/components/Chip.md
class TransactionDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)
    }
}