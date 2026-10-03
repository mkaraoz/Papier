package org.mk.papier.ui.scroll

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.mk.papier.model.ScrollExample
import org.mk.papier.model.ScrollWord
import org.mk.papier.ui.speech.DutchTts
import org.mk.papier.ui.speech.rememberDutchTts

private val storyInk = Color(0xFF15213A)
private val storySecondary = Color(0xFF40506B)
private val storyOrange = Color(0xFFE66532)
private val storyGreen = Color(0xFF388565)
private val storyCream = Color(0xFFFFF8EE)

@Composable
fun ScrollV4Screen(
    onBack: () -> Unit,
    onPractice: (String) -> Unit,
    viewModel: ScrollV4ViewModel = viewModel()
) {
    val savedIds by viewModel.savedIds.collectAsStateWithLifecycle()
    val knownIds by viewModel.knownIds.collectAsStateWithLifecycle()
    val pager = rememberPagerState(pageCount = { Int.MAX_VALUE })
    val scope = rememberCoroutineScope()
    val tts = rememberDutchTts()
    LaunchedEffect(pager.currentPage) { tts.stop() }

    Column(Modifier.fillMaxSize().background(storyCream).systemBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = storyInk)
            }
            Text("Dutch Scroll · Stories", color = storyInk, fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold)
        }
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(5) { segment ->
                Box(
                    Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp))
                        .background(if (segment <= pager.currentPage % 5) storyOrange
                        else Color(0xFFD9D4CC))
                )
            }
        }

        HorizontalPager(state = pager, key = { it }, modifier = Modifier.fillMaxSize()) { page ->
            val item = viewModel.wordAt(page)
            Box(Modifier.fillMaxSize()) {
                StoryPage(
                    item = item,
                    tts = tts,
                    isSaved = item.word.id in savedIds,
                    isKnown = item.word.id in knownIds,
                    onPractice = { onPractice(item.word.id) },
                    onSave = { viewModel.save(item.word.id) },
                    onKnow = { viewModel.toggleKnown(item.word.id) }
                )
                // Narrow edge zones leave every sentence and action fully tappable.
                Box(
                    Modifier.align(Alignment.CenterStart).fillMaxHeight().width(24.dp)
                        .semantics { contentDescription = "Previous word" }
                        .clickable(enabled = page > 0) {
                            scope.launch { pager.animateScrollToPage(page - 1) }
                        }
                )
                Box(
                    Modifier.align(Alignment.CenterEnd).fillMaxHeight().width(24.dp)
                        .semantics { contentDescription = "Next word" }
                        .clickable {
                            scope.launch { pager.animateScrollToPage(page + 1) }
                        }
                )
            }
        }
    }
}

@Composable
private fun StoryPage(
    item: ScrollWord,
    tts: DutchTts,
    isSaved: Boolean,
    isKnown: Boolean,
    onPractice: () -> Unit,
    onSave: () -> Unit,
    onKnow: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(storyCream, Color(0xFFFFFDF8)))
        ).padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 18.dp)
    ) {
        val metadata = listOfNotNull(item.word.type, item.cefr).joinToString(" · ")
        Text(metadata.uppercase(), color = storyOrange, fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
        Row(
            Modifier.fillMaxWidth().padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val headline = buildAnnotatedString {
                item.word.article?.let { article ->
                    withStyle(SpanStyle(fontSize = 29.sp, color = storySecondary,
                        fontWeight = FontWeight.Medium)) { append("$article ") }
                }
                append(item.word.dutch)
            }
            Text(headline, modifier = Modifier.weight(1f), color = storyInk,
                fontFamily = FontFamily.Serif, fontSize = 42.sp, lineHeight = 48.sp,
                fontWeight = FontWeight.Bold, maxLines = 2,
                overflow = TextOverflow.Ellipsis)
            IconButton(onClick = { tts.speak(item.word.id, item.word.dutch) },
                enabled = tts.ready, modifier = Modifier.size(44.dp)) {
                Icon(if (tts.ready) Icons.AutoMirrored.Filled.VolumeUp
                    else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = if (tts.ready) "Pronounce ${item.word.dutch}"
                    else "Dutch voice not installed",
                    tint = when {
                        !tts.ready -> Color(0xFFA8ADB0)
                        tts.speakingId == item.word.id -> storyOrange
                        else -> storyInk
                    })
            }
        }
        item.word.sense?.let { Text("($it)", color = storySecondary, fontSize = 12.sp) }
        Text(item.word.english, color = storyInk, fontSize = 24.sp,
            fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp))

        StoryNounArt(item.word.id)

        Column(Modifier.fillMaxWidth().weight(1f).padding(top = 10.dp)) {
            item.examples.forEachIndexed { index, example ->
                StoryExample(
                    example = example,
                    tts = tts,
                    utteranceId = "${item.word.id}:story:$index",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StoryAction("Practice", Icons.Default.EditNote, storyOrange, Color.White,
                Modifier.weight(1.3f), onPractice)
            StoryAction(if (isSaved) "Saved" else "Save",
                if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                if (isSaved) storyGreen.copy(alpha = 0.16f) else Color(0xFFF4EEE4),
                if (isSaved) storyGreen else storyInk,
                Modifier.weight(1f), onClick = onSave, enabled = !isSaved)
            StoryAction(if (isKnown) "Known" else "Know",
                if (isKnown) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                if (isKnown) storyGreen.copy(alpha = 0.16f) else Color(0xFFF4EEE4),
                if (isKnown) storyGreen else storyInk,
                Modifier.weight(1f), onKnow)
        }
    }
}

@Composable
private fun StoryNounArt(wordId: String) {
    val icon: ImageVector = when (wordId) {
        "1" -> Icons.Default.Home
        "2" -> Icons.AutoMirrored.Filled.DirectionsBike
        "3" -> Icons.AutoMirrored.Filled.MenuBook
        "9" -> Icons.Default.WaterDrop
        else -> null
    } ?: return
    Box(
        Modifier.padding(top = 14.dp).size(92.dp).clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFFE5CD)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = storyOrange, modifier = Modifier.size(52.dp))
    }
}

@Composable
private fun StoryExample(
    example: ScrollExample,
    tts: DutchTts,
    utteranceId: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.fillMaxWidth().clickable(enabled = tts.ready,
            onClickLabel = "Play Dutch sentence") {
            tts.speak(utteranceId, example.dutch)
        },
        contentAlignment = Alignment.CenterStart
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(storyHighlight(example.dutch, example.highlight), color = storyInk,
                fontFamily = FontFamily.Serif, fontSize = 18.sp, lineHeight = 23.sp,
                fontWeight = FontWeight.Medium)
            Text(example.english, color = storySecondary, fontSize = 16.sp, lineHeight = 21.sp)
        }
    }
}

@Composable
private fun StoryAction(
    label: String,
    icon: ImageVector,
    background: Color,
    contentColor: Color,
    modifier: Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier.height(50.dp).clip(RoundedCornerShape(16.dp))
            .background(background).clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(3.dp))
        Text(label, color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun storyHighlight(sentence: String, target: String) = buildAnnotatedString {
    val start = sentence.indexOf(target, ignoreCase = true)
    if (start < 0) append(sentence) else {
        append(sentence.substring(0, start))
        withStyle(SpanStyle(color = storyOrange, fontWeight = FontWeight.Bold)) {
            append(sentence.substring(start, start + target.length))
        }
        append(sentence.substring(start + target.length))
    }
}
