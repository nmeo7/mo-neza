package com.rmsoft.moneza.notifications

import android.app.Activity
import com.tapadoo.alerter.Alerter

class AlertNotification (var context: Activity) {

    fun onCreate() {
        Alerter.create(context)
            .setTitle("Payment Completed")
            .setText("Payment to xxx")
            .show()
    }
}