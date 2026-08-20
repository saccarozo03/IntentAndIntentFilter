package com.example.intentandintentfilter

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    // Phần E: Khai báo Dynamic Broadcast Receiver
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.EVENT_UPDATED") {
                Toast.makeText(context, "Danh sách sự kiện vừa được cập nhật!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Đăng ký Dynamic Receiver
        val filter = IntentFilter("com.example.EVENT_UPDATED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(updateReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(updateReceiver, filter)
        }

        setContent {
            val context = LocalContext.current
            var events by remember { mutableStateOf(listOf<Event>()) }
            
            // Phần B: Activity Result API
            val addEventLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    val name = data?.getStringExtra("event_name") ?: ""
                    val desc = data?.getStringExtra("event_desc") ?: ""
                    val id = data?.getIntExtra("event_id", 0) ?: 0
                    val time = data?.getLongExtra("event_time", 0) ?: 0
                    
                    val newEvent = Event(id, name, desc, time)
                    events = events + newEvent
                    
                    // Phần D: Đặt báo thức
                    scheduleNotification(context, newEvent)

                    // Gửi broadcast để kích hoạt Dynamic Receiver
                    sendBroadcast(Intent("com.example.EVENT_UPDATED"))
                }
            }

            // Phần D: Xin quyền thông báo cho Android 13+
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(context, "Bạn cần cấp quyền thông báo để nhận nhắc nhở!", Toast.LENGTH_LONG).show()
                }
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        val intent = Intent(this, AddEventActivity::class.java)
                        addEventLauncher.launch(intent)
                    }) {
                        Text("+", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            ) { padding ->
                Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                    Text("FPT Event Hub", style = MaterialTheme.typography.headlineLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyColumn {
                        items(events) { event ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    .clickable {
                                        // Phần A: Explicit Intent
                                        val intent = Intent(context, EventDetailActivity::class.java).apply {
                                            putExtra("event_name", event.name)
                                            putExtra("event_desc", event.description)
                                        }
                                        startActivity(intent)
                                    }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(event.name, style = MaterialTheme.typography.titleMedium)
                                    Text(event.description, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun scheduleNotification(context: Context, event: Event) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("event_name", event.name)
            putExtra("event_id", event.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, event.id, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, event.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, event.timeInMillis, pendingIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hủy đăng ký Dynamic Receiver để tránh rò rỉ bộ nhớ
        unregisterReceiver(updateReceiver)
    }
}
