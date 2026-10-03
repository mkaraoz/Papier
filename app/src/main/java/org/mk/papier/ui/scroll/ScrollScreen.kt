package org.mk.papier.ui.scroll

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.random.Random
import org.mk.papier.model.ScrollExample
import org.mk.papier.model.ScrollWord
import org.mk.papier.ui.speech.DutchTts
import org.mk.papier.ui.speech.rememberDutchTts

private val ink = Color(0xFF15213A)
private val translation = Color(0xFF40506B)
private val accent = Color(0xFFB95734)
private val divider = Color(0xFFCED1D2)
private val surround = Color(0xFFEAE5DD)
private val pageColors = listOf(
    Color(0xFFFFFAF2),
    Color(0xFFF4F8F5),
    Color(0xFFF4F7FC),
    Color(0xFFFFF7F1)
)

@Composable
fun ScrollV2Screen(onBack: () -> Unit, viewModel: ScrollViewModel = viewModel()) {
    val words = viewModel.words
    val pagerState = rememberPagerState(pageCount = { Int.MAX_VALUE })
    val tts = rememberDutchTts()

    // A spoken example should not continue over the next word.
    LaunchedEffect(pagerState.currentPage) { tts.stop() }

    Column(Modifier.fillMaxSize().background(surround).systemBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(48.dp).padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ink)
            }
            Text("Dutch Scroll · V2", color = translation, fontSize = 14.sp,
                fontWeight = FontWeight.Medium)
        }

        BoxWithConstraints(Modifier.fillMaxSize()) {
            val peek = if (maxHeight < 640.dp) 64.dp else 96.dp
            val pageHeight = (maxHeight - peek).coerceAtLeast(360.dp)
            VerticalPager(
                state = pagerState,
                pageSize = PageSize.Fixed(pageHeight),
                pageSpacing = 8.dp,
                key = { it },
                modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp)
            ) { page ->
                val item = wordForPage(words, page)
                FeedPage(item, pageColors[page % pageColors.size], tts)
            }
        }
    }
}

/** Keep the first pass predictable, then vary the order without interrupting the feed. */
internal fun wordForPage(words: List<ScrollWord>, page: Int): ScrollWord {
    val round = page / words.size
    if (round == 0) return words[page]
    val order = words.shuffled(Random(round)).toMutableList()
    val previousLast = if (round == 1) words.last() else words.shuffled(Random(round - 1)).last()
    if (order.first().word.id == previousLast.word.id) {
        val first = order.removeAt(0)
        order.add(1, first)
    }
    return order[page % words.size]
}

@Composable
private fun FeedPage(item: ScrollWord, pageColor: Color, tts: DutchTts) {
    Column(
        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp))
            .background(pageColor).padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(item.word.type.uppercase(), color = translation, fontSize = 12.sp,
                letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold)
            item.cefr?.let {
                Text("· $it", color = translation, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val headline = buildAnnotatedString {
                item.word.article?.let { article ->
                    withStyle(SpanStyle(color = translation, fontSize = 23.sp,
                        fontWeight = FontWeight.Normal)) { append("$article ") }
                }
                append(item.word.dutch)
            }
            Text(
                headline,
                modifier = Modifier.weight(1f).clickable(
                    enabled = tts.ready,
                    onClickLabel = "Pronounce ${item.word.dutch}"
                ) { tts.speak(item.word.id, item.word.dutch) },
                color = ink, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                fontSize = 44.sp, lineHeight = 50.sp, maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(
                onClick = { tts.speak(item.word.id, item.word.dutch) },
                enabled = tts.ready,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    if (tts.ready) Icons.AutoMirrored.Filled.VolumeUp
                    else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = if (tts.ready) "Pronounce ${item.word.dutch}"
                    else "Dutch voice not installed",
                    tint = when {
                        !tts.ready -> Color(0xFF9DA5AE)
                        tts.speakingId == item.word.id -> accent
                        else -> translation
                    },
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        item.word.sense?.let {
            Text("($it)", color = translation, fontSize = 12.sp)
        }
        Text(item.word.english, color = ink, fontSize = 23.sp,
            fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp, bottom = 14.dp))
        HorizontalDivider(color = divider)

        Column(Modifier.fillMaxWidth().weight(1f)) {
            item.examples.forEachIndexed { index, example ->
                ExampleLine(
                    example = example,
                    wordId = item.word.id,
                    index = index,
                    tts = tts,
                    modifier = Modifier.weight(1f)
                )
                if (index < item.examples.lastIndex) HorizontalDivider(color = divider)
            }
        }
    }
}

@Composable
private fun ExampleLine(
    example: ScrollExample,
    wordId: String,
    index: Int,
    tts: DutchTts,
    modifier: Modifier = Modifier
) {
    val utteranceId = "$wordId:$index"
    Box(
        modifier = modifier.fillMaxWidth().clickable(
            enabled = tts.ready,
            onClickLabel = "Play Dutch example"
        ) { tts.speak(utteranceId, example.dutch) }
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart).fillMaxWidth().padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                highlighted(example.dutch, example.highlight),
                color = ink, fontFamily = FontFamily.Serif, fontSize = 19.sp,
                lineHeight = 24.sp, fontWeight = FontWeight.Medium
            )
            Text(example.english, color = translation, fontSize = 16.sp, lineHeight = 21.sp)
        }
    }
}

private fun highlighted(sentence: String, target: String) = buildAnnotatedString {
    val start = sentence.indexOf(target, ignoreCase = true)
    if (start < 0) {
        append(sentence)
    } else {
        append(sentence.substring(0, start))
        withStyle(SpanStyle(color = accent, fontWeight = FontWeight.Bold)) {
            append(sentence.substring(start, start + target.length))
        }
        append(sentence.substring(start + target.length))
    }
}
