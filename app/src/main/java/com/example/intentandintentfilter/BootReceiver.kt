package com.example.intentandintentfilter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Máy đã khởi động lại, khôi phục báo thức...")
            // Trong thực tế, bạn sẽ truy vấn Database và đặt lại các AlarmManager ở đây
        }
    }
}
