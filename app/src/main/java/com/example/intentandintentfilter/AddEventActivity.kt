package com.example.intentandintentfilter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class AddEventActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var name by remember { mutableStateOf("") }
            var desc by remember { mutableStateOf("") }

            Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                Text("Thêm Sự Kiện Mới", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = name, onValueChange = { name = it }, label = { Text("Tên sự kiện") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = desc, onValueChange = { desc = it }, label = { Text("Mô tả") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val resultIntent = Intent().apply {
                            putExtra("event_name", name)
                            putExtra("event_desc", desc)
                            putExtra("event_id", (0..1000).random())
                            putExtra("event_time", System.currentTimeMillis() + 10000) // Mặc định nhắc sau 10s để test
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lưu & Đặt Nhắc Nhở")
                }
            }
        }
    }
}
