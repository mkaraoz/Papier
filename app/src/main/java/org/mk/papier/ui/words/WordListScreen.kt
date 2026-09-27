package org.mk.papier.ui.words

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.mk.papier.model.Word
import org.mk.papier.ui.speech.DutchTts
import org.mk.papier.ui.speech.rememberDutchTts

@Composable
fun WordListScreen(
    onBack: () -> Unit,
    viewModel: WordListViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortMode by viewModel.sortMode.collectAsStateWithLifecycle()
    val words by viewModel.filteredWords.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val tts = rememberDutchTts()
    val snackbarHostState = remember { SnackbarHostState() }

    // Only one word can be expanded at a time
    var expandedWordId by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingRemovalId by rememberSaveable { mutableStateOf<String?>(null) }

    fun onSwipe(word: Word) {
        if (viewModel.isNewWords) {
            pendingRemovalId = word.id
        } else {
            val added = viewModel.addToNewWords(word)
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    if (added) "${word.dutch} added to New Words"
                    else "${word.dutch} is already in New Words"
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4FF))
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    text = viewModel.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${words.size} words",
                    fontSize = 13.sp,
                    color = Color(0xFF888888)
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text("Search words...") },
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

            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = sortMode == SortMode.SORTED,
                    onClick = {
                        viewModel.setSortMode(SortMode.SORTED)
                        scope.launch { listState.scrollToItem(0) }
                    },
                    label = { Text("Sorted") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4A90D9),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = sortMode == SortMode.RANDOM,
                    onClick = {
                        viewModel.setSortMode(SortMode.RANDOM)
                        scope.launch { listState.scrollToItem(0) }
                    },
                    label = { Text("Random") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4A90D9),
                        selectedLabelColor = Color.White
                    )
                )
            }

            Text(
                text = if (viewModel.isNewWords) "Swipe right to remove a word"
                    else "Swipe right to add to New Words",
                fontSize = 12.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            if (words.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotBlank()) "No matching words"
                            else if (viewModel.isNewWords)
                                "No new words to study.\nSwipe right on a word in All Words to add it here."
                            else "No words yet",
                        color = Color(0xFF666666)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = listState,
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(words, key = { it.id }) { word ->
                        SwipeWordItem(
                            isNewWords = viewModel.isNewWords,
                            onSwipe = { onSwipe(word) }
                        ) {
                            WordItem(
                                word = word,
                                expanded = word.id == expandedWordId,
                                tts = tts,
                                onClick = {
                                    expandedWordId = if (expandedWordId == word.id) null else word.id
                                }
                            )
                        }
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    val pendingWord = pendingRemovalId?.let(viewModel::wordById)
    if (pendingWord != null) {
        AlertDialog(
            onDismissRequest = { pendingRemovalId = null },
            title = { Text("Remove from New Words?") },
            text = { Text("Remove \"${pendingWord.dutch}\" from your study list? It will stay in All Words.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeFromNewWords(pendingWord.id)
                    if (expandedWordId == pendingWord.id) expandedWordId = null
                    pendingRemovalId = null
                }) { Text("Remove") }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemovalId = null }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeWordItem(
    isNewWords: Boolean,
    onSwipe: () -> Unit,
    content: @Composable () -> Unit
) {
    val currentOnSwipe by rememberUpdatedState(onSwipe)
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd) currentOnSwipe()
            // A swipe is an action, never a dismissal: return the row to its place.
            // New Words removes it only after the user confirms the dialog.
            false
        },
        positionalThreshold = { distance -> distance * 0.35f }
    )
    val actionLabel = if (isNewWords) "Remove from New Words" else "Add to New Words"
    // The requested gesture is physically left-to-right, including on RTL devices.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        SwipeToDismissBox(
            state = swipeState,
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = false,
            modifier = Modifier.semantics {
                customActions = listOf(CustomAccessibilityAction(actionLabel) {
                    currentOnSwipe()
                    true
                })
            },
            backgroundContent = {
                Row(
                    modifier = Modifier.fillMaxSize()
                        .graphicsLayer {
                            // Expansion/collapse can expose the space behind the card.
                            // Show the action background only during a horizontal swipe.
                            alpha = if (swipeState.dismissDirection == SwipeToDismissBoxValue.Settled) 0f else 1f
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isNewWords) Color(0xFFB3261E) else Color(0xFF2E7D32))
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (isNewWords) Icons.Default.DeleteOutline else Icons.Default.BookmarkAdd,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(if (isNewWords) "Remove" else "Add", color = Color.White)
                }
            }
        ) { content() }
    }
}

@Composable
private fun WordItem(
    word: Word,
    expanded: Boolean,
    tts: DutchTts,
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
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (word.article != null) {
                            ArticleBadge(article = word.article)
                        }
                        Text(
                            text = word.dutch,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        if (word.sense != null) {
                            Text(
                                text = "(${word.sense})",
                                fontSize = 13.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = word.english,
                        fontSize = 15.sp,
                        color = Color(0xFF555555)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                SpeakButton(
                    enabled = tts.ready,
                    speaking = tts.speakingId == word.id,
                    onClick = { tts.speak(word.id, word.dutch) }
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "\"${word.example}\"",
                    fontSize = 14.sp,
                    color = Color(0xFF333333),
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = word.exampleTranslation,
                    fontSize = 13.sp,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

/**
 * Experimental on-device pronunciation. Fixed 48dp footprint in every state so the
 * card never resizes, whether or not the device has a Dutch voice.
 */
@Composable
private fun SpeakButton(
    enabled: Boolean,
    speaking: Boolean,
    onClick: () -> Unit
) {
    val accent = Color(0xFF4A90D9)
    val container by animateColorAsState(
        targetValue = when {
            !enabled -> Color(0xFFF4F4F4)
            speaking -> accent
            else -> accent.copy(alpha = 0.12f)
        },
        label = "speakContainer"
    )
    val tint by animateColorAsState(
        targetValue = when {
            !enabled -> Color(0xFFCFCFCF)
            speaking -> Color.White
            else -> accent
        },
        label = "speakTint"
    )
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(48.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(container),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (enabled) Icons.AutoMirrored.Filled.VolumeUp
                else Icons.AutoMirrored.Filled.VolumeOff,
                contentDescription = if (enabled) "Play pronunciation"
                else "Dutch voice not installed",
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ArticleBadge(article: String) {
    val color = if (article == "de") Color(0xFF4A90D9) else Color(0xFF4CAF50)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = article,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}
