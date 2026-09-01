package org.mk.papier.ui.pronouns

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.mk.papier.model.Pronoun
import org.mk.papier.model.PronounGroup
import org.mk.papier.ui.theme.PapierTheme

@Composable
fun PronounsScreen(
    onBack: () -> Unit,
    viewModel: PronounsViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val groups by viewModel.filteredGroups.collectAsStateWithLifecycle()

    PronounsContent(
        groups = groups,
        searchQuery = searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onBack = onBack
    )
}

@Composable
private fun PronounsContent(
    groups: List<PronounGroup>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBack: () -> Unit
) {
    // Only one row can be expanded at a time, across all groups
    var expandedPronounId by rememberSaveable { mutableStateOf<String?>(null) }

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
                text = "Pronouns",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${groups.sumOf { it.pronouns.size }} forms",
                fontSize = 13.sp,
                color = Color(0xFF888888)
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search pronouns...") },
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
            groups.forEach { group ->
                item(key = "header-${group.id}") {
                    GroupHeader(group = group)
                }
                items(group.pronouns, key = { it.id }) { pronoun ->
                    PronounItem(
                        pronoun = pronoun,
                        expanded = pronoun.id == expandedPronounId,
                        onClick = {
                            expandedPronounId =
                                if (expandedPronounId == pronoun.id) null else pronoun.id
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupHeader(group: PronounGroup) {
    Text(
        text = group.title,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A1A1A),
        modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)
    )
}

/**
 * One line of the paradigm: Dutch on the left, English on the right. The reduced spoken
 * form sits inline after the Dutch word rather than on its own line, so every collapsed
 * row is the same height whether or not the pronoun has one. Tapping reveals the example.
 */
@Composable
private fun PronounItem(
    pronoun: Pronoun,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pronoun.dutch,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    if (pronoun.unstressed != null) {
                        Text(
                            text = " / ${pronoun.unstressed}",
                            fontSize = 15.sp,
                            color = Color(0xFF999999)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = pronoun.english,
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    color = Color(0xFF555555),
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "\"${pronoun.example}\"",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pronoun.exampleTranslation,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PronounsScreenPreview() {
    PapierTheme {
        PronounsContent(
            groups = listOf(
                PronounGroup(
                    id = "subject",
                    title = "Subject",
                    pronouns = listOf(
                        Pronoun("sub-ik", "ik", null, "I", "Ik woon in Nederland.", "I live in the Netherlands."),
                        Pronoun("sub-jij", "jij", "je", "you (informal)", "Jij spreekt goed Nederlands.", "You speak Dutch well.")
                    )
                )
            ),
            searchQuery = "",
            onSearchQueryChange = {},
            onBack = {}
        )
    }
}
