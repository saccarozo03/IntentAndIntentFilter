package com.example.intentandintentfilter

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Nhận dữ liệu từ Intent (Extra hoặc Deep Link)
        val eventName = intent.getStringExtra("event_name") ?: intent.data?.getQueryParameter("id") ?: "Không có tên"
        val eventDesc = intent.getStringExtra("event_desc") ?: "Sự kiện được mở từ Deep Link"

        setContent {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Chi Tiết Sự Kiện", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tên: $eventName", style = MaterialTheme.typography.bodyLarge)
                Text("Mô tả: $eventDesc")
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Tham gia sự kiện $eventName nhé! - $eventDesc")
                    }
                    startActivity(Intent.createChooser(shareIntent, "Chia sẻ sự kiện"))
                }) {
                    Text("Chia sẻ (Implicit Intent)")
                }
            }
        }
    }
}
