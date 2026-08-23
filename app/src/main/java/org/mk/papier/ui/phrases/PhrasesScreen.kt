package org.mk.papier.ui.phrases

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.mk.papier.model.Phrase

@Composable
fun PhrasesScreen(
    onBack: () -> Unit,
    viewModel: PhrasesViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val phrases by viewModel.filteredPhrases.collectAsStateWithLifecycle()

    // Only one phrase can be expanded at a time
    var expandedPhraseId by rememberSaveable { mutableStateOf<Int?>(null) }

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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Idioms",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${phrases.size} phrases",
                fontSize = 13.sp,
                color = Color(0xFF888888)
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            placeholder = { Text("Search phrases...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF4A90D9),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(phrases, key = { it.id }) { phrase ->
                PhraseItem(
                    phrase = phrase,
                    expanded = phrase.id == expandedPhraseId,
                    onClick = {
                        expandedPhraseId = if (expandedPhraseId == phrase.id) null else phrase.id
                    }
                )
            }
        }
    }
}

@Composable
private fun PhraseItem(
    phrase: Phrase,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = phrase.phrase,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = phrase.translation,
                fontSize = 14.sp,
                color = Color(0xFF555555)
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    LanguageBadge(label = "en")
                    Text(
                        text = phrase.en,
                        fontSize = 14.sp,
                        color = Color(0xFF4A90D9),
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = phrase.explanation,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF333333)
                )

                phrase.examples.forEach { example ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "\"${example.nl}\"",
                        fontSize = 14.sp,
                        color = Color(0xFF1A1A1A),
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = example.tr,
                        fontSize = 13.sp,
                        color = Color(0xFF888888)
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageBadge(label: String) {
    val color = Color(0xFF4A90D9)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}
