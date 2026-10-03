package org.mk.papier.ui.scroll

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import org.mk.papier.model.ScrollExample
import org.mk.papier.model.ScrollWord
import org.mk.papier.ui.speech.DutchTts
import org.mk.papier.ui.speech.rememberDutchTts

private val v3Ink = Color(0xFF17243C)
private val v3Secondary = Color(0xFF384B62)
private val v3Accent = Color(0xFF9F593D)
private val v3Background = Color(0xFFE8E6E2)
private val v3Page = Color(0xFFFFFCF7)

@Composable
fun ScrollV3Screen(onBack: () -> Unit, viewModel: ScrollV3ViewModel = viewModel()) {
    val words = viewModel.words
    val savedIds by viewModel.savedWordIds.collectAsStateWithLifecycle()
    val pager = rememberPagerState(pageCount = { Int.MAX_VALUE })
    val tts = rememberDutchTts()
    val context = LocalContext.current
    LaunchedEffect(pager.currentPage) { tts.stop() }

    Column(Modifier.fillMaxSize().background(v3Background).systemBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = v3Ink)
            }
            Text("Dutch Scroll · V3", fontSize = 14.sp, color = v3Secondary,
                fontWeight = FontWeight.Medium)
        }
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val pageHeight = if (maxHeight < 640.dp) maxHeight - 78.dp else maxHeight * 0.78f
            VerticalPager(
                state = pager,
                pageSize = PageSize.Fixed(pageHeight),
                pageSpacing = 8.dp,
                key = { it },
                modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp)
            ) { page ->
                val item = wordForPage(words, page)
                V3Page(
                    item = item,
                    tts = tts,
                    isSaved = item.word.id in savedIds,
                    onSave = { viewModel.saveWord(item.word.id) },
                    onShare = {
                        val headword = listOfNotNull(item.word.article, item.word.dutch).joinToString(" ")
                        val message = buildString {
                            append("$headword — ${item.word.english}\n\n")
                            item.examples.forEach { example ->
                                append("${example.dutch}\n${example.english}\n\n")
                            }
                        }.trim()
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, message)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Dutch word"))
                    }
                )
            }
        }
    }
}

@Composable
private fun V3Page(
    item: ScrollWord,
    tts: DutchTts,
    isSaved: Boolean,
    onSave: () -> Unit,
    onShare: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp))
            .background(v3Page).padding(horizontal = 22.dp, vertical = 18.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(item.word.type.uppercase(), color = v3Secondary, fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                    item.cefr?.let {
                        Text("· $it", color = v3Secondary, fontSize = 12.sp)
                    }
                }
                val headline = buildAnnotatedString {
                    item.word.article?.let { article ->
                        withStyle(SpanStyle(fontSize = 29.sp, color = v3Secondary,
                            fontWeight = FontWeight.Medium)) { append("$article ") }
                    }
                    append(item.word.dutch)
                }
                Text(headline, color = v3Ink, fontFamily = FontFamily.Serif,
                    fontSize = 41.sp, lineHeight = 45.sp, fontWeight = FontWeight.Bold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 12.dp))
                item.word.sense?.let { Text("($it)", color = v3Secondary, fontSize = 12.sp) }
                Text(item.word.english, color = v3Ink, fontSize = 22.sp,
                    fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 3.dp))
            }
            Column(Modifier.width(58.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                V3Action(
                    icon = if (tts.ready) Icons.AutoMirrored.Filled.VolumeUp
                        else Icons.AutoMirrored.Filled.VolumeOff,
                    label = "Listen",
                    description = if (tts.ready) "Pronounce ${item.word.dutch}"
                        else "Dutch voice not installed",
                    enabled = tts.ready,
                    active = tts.speakingId == item.word.id,
                    onClick = { tts.speak(item.word.id, item.word.dutch) }
                )
                V3Action(
                    icon = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    label = if (isSaved) "Saved" else "Save",
                    description = if (isSaved) "Already in New Words" else "Save to New Words",
                    enabled = !isSaved,
                    active = isSaved,
                    onClick = onSave
                )
                V3Action(Icons.Default.Share, "Share", "Share this word", onClick = onShare)
            }
        }

        Column(Modifier.fillMaxWidth().weight(1f).padding(top = 10.dp)) {
            item.examples.forEachIndexed { index, example ->
                V3Example(
                    example = example,
                    wordId = item.word.id,
                    index = index,
                    tts = tts,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun V3Action(
    icon: ImageVector,
    label: String,
    description: String,
    enabled: Boolean = true,
    active: Boolean = false,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick, enabled = enabled, modifier = Modifier.size(44.dp)) {
            Icon(icon, contentDescription = description,
                tint = when {
                    active -> v3Accent
                    enabled -> v3Ink
                    else -> Color(0xFF9CA4AA)
                }, modifier = Modifier.size(23.dp))
        }
        Text(label, fontSize = 10.sp, color = v3Secondary)
    }
}

@Composable
private fun V3Example(
    example: ScrollExample,
    wordId: String,
    index: Int,
    tts: DutchTts,
    modifier: Modifier = Modifier
) {
    val utteranceId = "$wordId:$index"
    Row(
        modifier.fillMaxWidth().clickable(enabled = tts.ready, onClickLabel = "Play Dutch example") {
            tts.speak(utteranceId, example.dutch)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            if (tts.ready) Icons.AutoMirrored.Filled.VolumeUp
            else Icons.AutoMirrored.Filled.VolumeOff,
            contentDescription = if (tts.ready) "Play Dutch example" else "Dutch voice not installed",
            tint = when {
                !tts.ready -> Color(0xFF9CA4AA)
                tts.speakingId == utteranceId -> v3Accent
                else -> v3Secondary
            }, modifier = Modifier.size(20.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(v3Highlight(example.dutch, example.highlight), color = v3Ink,
                fontFamily = FontFamily.Serif, fontSize = 18.sp, lineHeight = 23.sp,
                fontWeight = FontWeight.Medium)
            Text(example.english, color = v3Secondary, fontSize = 17.sp, lineHeight = 22.sp)
        }
    }
}

private fun v3Highlight(sentence: String, target: String) = buildAnnotatedString {
    val start = sentence.indexOf(target, ignoreCase = true)
    if (start < 0) append(sentence) else {
        append(sentence.substring(0, start))
        withStyle(SpanStyle(color = v3Accent, fontWeight = FontWeight.Bold)) {
            append(sentence.substring(start, start + target.length))
        }
        append(sentence.substring(start + target.length))
    }
}
