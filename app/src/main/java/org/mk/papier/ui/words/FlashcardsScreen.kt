package org.mk.papier.ui.words

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.mk.papier.model.Word

@Composable
fun FlashcardsScreen(
    onBack: () -> Unit,
    viewModel: FlashcardsViewModel = viewModel()
) {
    val currentWord by viewModel.currentWord.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4FF))
            .statusBarsPadding()
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
                text = "Flashcards",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${currentIndex + 1} / ${viewModel.totalCount}",
                fontSize = 13.sp,
                color = Color(0xFF888888)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (currentWord != null) {
                FlipCard(word = currentWord!!)
            } else {
                Text("No words available.", color = Color(0xFF888888))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                enabled = currentIndex > 0,
                onClick = viewModel::previous
            )
            NavButton(
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                enabled = currentIndex < viewModel.totalCount - 1,
                onClick = viewModel::next
            )
        }
    }
}

@Composable
private fun FlipCard(word: Word) {
    var flipped by remember(word.id) { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "flip"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
            .clickable { flipped = !flipped },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            CardFront(word = word)
        } else {
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                CardBack(word = word)
            }
        }
    }
}

@Composable
private fun CardFront(word: Word) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (word.article != null) {
                val badgeColor = if (word.article == "de") Color(0xFF4A90D9) else Color(0xFF4CAF50)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = word.article,
                        fontSize = 14.sp,
                        color = badgeColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Text(
                text = word.dutch,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "tap to reveal",
                fontSize = 13.sp,
                color = Color(0xFFCCCCCC)
            )
        }
    }
}

@Composable
private fun CardBack(word: Word) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4A90D9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = word.english,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "\"${word.example}\"",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = word.exampleTranslation,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NavButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(if (enabled) Color.White else Color.White.copy(alpha = 0.4f))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) Color(0xFF4A90D9) else Color(0xFFCCCCCC)
        )
    }
}
