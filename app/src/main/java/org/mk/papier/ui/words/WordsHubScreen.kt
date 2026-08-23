package org.mk.papier.ui.words

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class HubItem(
    val title: String,
    val subtitle: String,
    val color: Color,
    val icon: ImageVector,
    val route: String
)

private val hubItems = listOf(
    HubItem("All Words", "Full sorted list", Color(0xFF4A90D9), Icons.AutoMirrored.Filled.List, "word_list"),
    HubItem("Verbs", "Verb forms only", Color(0xFF4CAF50), Icons.Default.Spellcheck, "word_list?filter=verb"),
    HubItem("Flashcards", "Flip and learn", Color(0xFFFF9800), Icons.Default.Style, "flashcards"),
    HubItem("Verb Cards", "Cards, verbs only", Color(0xFF9C27B0), Icons.Default.FlashOn, "flashcards?filter=verb"),
    HubItem("Idioms", "Everyday expressions", Color(0xFF00897B), Icons.AutoMirrored.Filled.Chat, "phrases"),
    HubItem("Themes", "Words grouped by topic", Color(0xFFE91E63), Icons.Default.LocalOffer, "tags")
)

@Composable
fun WordsHubScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4FF))
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Words",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            hubItems.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    rowItems.forEach { item ->
                        HubCard(
                            item = item,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(item.route) }
                        )
                    }
                    if (rowItems.size == 1) {
                        androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun HubCard(
    item: HubItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = item.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = item.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}
