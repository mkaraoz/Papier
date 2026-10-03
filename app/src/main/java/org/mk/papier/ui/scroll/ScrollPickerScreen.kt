package org.mk.papier.ui.scroll

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Version(val title: String, val description: String, val route: String)

private val versions = listOf(
    Version("Version 1 · Cards", "Original full-card layout with ratings", "scroll_v1"),
    Version("Version 2 · Feed", "Next-word peek and quiet example rows", "scroll_v2"),
    Version("Version 3 · Actions", "Compact feed with useful side actions", "scroll_v3")
)

@Composable
fun ScrollPickerScreen(onBack: () -> Unit, onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF0F4FF))
            .systemBarsPadding().padding(horizontal = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Dutch Scroll", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Text("Choose a version to compare", color = Color(0xFF596477),
            fontSize = 14.sp, modifier = Modifier.padding(start = 12.dp, bottom = 20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            versions.forEach { version ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigate(version.route) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(version.title, fontSize = 17.sp, fontWeight = FontWeight.Bold,
                                color = Color(0xFF15213A))
                            Text(version.description, fontSize = 13.sp,
                                color = Color(0xFF596477))
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null,
                            tint = Color(0xFF7B8795))
                    }
                }
            }
        }
    }
}
