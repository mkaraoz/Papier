package org.mk.papier.ui.scroll

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.mk.papier.model.ScrollExample
import org.mk.papier.model.ScrollWord
import org.mk.papier.ui.speech.DutchTts
import org.mk.papier.ui.speech.rememberDutchTts

private val v1Ink = Color(0xFF101D38)
private val v1Muted = Color(0xFF687388)
private val v1Orange = Color(0xFFF16828)

@Composable
fun ScrollV1Screen(onBack: () -> Unit, viewModel: ScrollV1ViewModel = viewModel()) {
    val words = viewModel.words
    val ratings by viewModel.ratings.collectAsStateWithLifecycle()
    val pager = rememberPagerState(pageCount = { words.size })
    val tts = rememberDutchTts()
    LaunchedEffect(pager.currentPage) { tts.stop() }

    Column(Modifier.fillMaxSize().background(Color(0xFFFFF8EF)).systemBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(end = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = v1Ink)
            }
            Text("Dutch Scroll · V1", color = v1Ink, fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold, fontSize = 25.sp, modifier = Modifier.weight(1f))
            Text("${pager.currentPage + 1} / ${words.size}", color = v1Muted, fontSize = 13.sp)
        }
        VerticalPager(state = pager, modifier = Modifier.fillMaxSize(), key = { words[it].word.id }) { page ->
            val item = words[page]
            Column(
                Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        Modifier.fillMaxSize().padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val metadata = listOfNotNull(item.cefr, item.word.type).joinToString(" · ")
                        Box(
                            Modifier.clip(RoundedCornerShape(14.dp)).background(Color(0xFFFFEBD7))
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(metadata, color = v1Ink, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                    item.word.article?.let { article ->
                                        Box(Modifier.clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFFFEBD7))
                                            .padding(horizontal = 7.dp, vertical = 4.dp)) {
                                            Text(article, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                                                color = v1Orange)
                                        }
                                    }
                                    Text(item.word.dutch, color = v1Ink, fontFamily = FontFamily.Serif,
                                        fontSize = if (item.word.dutch.length > 11) 32.sp else 39.sp,
                                        fontWeight = FontWeight.Bold, lineHeight = 42.sp,
                                        modifier = Modifier.weight(1f))
                                    V1SpeakButton(tts, item.word.id, item.word.dutch,
                                        "Pronounce ${item.word.dutch}")
                                }
                                item.word.sense?.let { Text("($it)", color = v1Muted, fontSize = 12.sp) }
                                Text(item.word.english, color = v1Ink, fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            }
                            V1WordArt(item.word.id)
                        }
                        HorizontalDivider(color = Color(0xFFEDE8E2))
                        item.examples.forEachIndexed { index, example ->
                            V1Example(index + 1, example, item.word.id, tts)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    V1Rating("Know it", Icons.Default.Check, "KNOW_IT", Color(0xFF4D9B70),
                        ratings[item.word.id], Modifier.weight(1f)) { viewModel.rate(item.word.id, it) }
                    V1Rating("Again", Icons.Default.Replay, "AGAIN", Color(0xFFF3BA7B),
                        ratings[item.word.id], Modifier.weight(1f)) { viewModel.rate(item.word.id, it) }
                    V1Rating("Hard", Icons.Default.Close, "HARD", Color(0xFFCD6A5B),
                        ratings[item.word.id], Modifier.weight(1f)) { viewModel.rate(item.word.id, it) }
                }
                Text(if (page < words.lastIndex) "Swipe up for next word" else "End of starter feed",
                    modifier = Modifier.fillMaxWidth(), color = v1Muted, fontSize = 12.sp,
                    textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun V1WordArt(id: String) {
    val icon = when (id) {
        "1" -> Icons.Default.Home
        "2" -> Icons.AutoMirrored.Filled.DirectionsBike
        "3" -> Icons.AutoMirrored.Filled.MenuBook
        "9" -> Icons.Default.WaterDrop
        else -> null
    } ?: return
    Box(Modifier.size(92.dp).clip(RoundedCornerShape(20.dp))
        .background(Color(0xFFFFEBD7)), contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = v1Orange, modifier = Modifier.size(52.dp))
    }
}

@Composable
private fun V1Example(number: Int, example: ScrollExample, wordId: String, tts: DutchTts) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFAF8F5)).padding(9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(Modifier.size(27.dp).clip(CircleShape).background(Color(0xFFF4EBDF)),
            contentAlignment = Alignment.Center) {
            Text("$number", color = v1Ink, fontSize = 12.sp)
        }
        Column(Modifier.weight(1f)) {
            Text(v1Highlight(example.dutch, example.highlight), color = v1Ink,
                fontFamily = FontFamily.Serif, fontSize = 15.sp, lineHeight = 18.sp)
            Text(example.english, color = v1Muted, fontSize = 12.sp, lineHeight = 16.sp)
        }
        V1SpeakButton(tts, "$wordId:$number", example.dutch, "Play example $number")
    }
}

private fun v1Highlight(sentence: String, target: String) = buildAnnotatedString {
    val start = sentence.indexOf(target, ignoreCase = true)
    if (start < 0) append(sentence) else {
        append(sentence.substring(0, start))
        withStyle(SpanStyle(color = v1Orange, fontWeight = FontWeight.Bold)) {
            append(sentence.substring(start, start + target.length))
        }
        append(sentence.substring(start + target.length))
    }
}

@Composable
private fun V1SpeakButton(tts: DutchTts, id: String, text: String, label: String) {
    IconButton(onClick = { tts.speak(id, text) }, enabled = tts.ready,
        modifier = Modifier.size(40.dp)) {
        Icon(if (tts.ready) Icons.AutoMirrored.Filled.VolumeUp
            else Icons.AutoMirrored.Filled.VolumeOff,
            contentDescription = if (tts.ready) label else "Dutch voice not installed",
            tint = when {
                !tts.ready -> Color(0xFFBBBBBB)
                tts.speakingId == id -> v1Orange
                else -> v1Ink
            }, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun V1Rating(label: String, icon: ImageVector, value: String, color: Color,
    selected: String?, modifier: Modifier, onRate: (String) -> Unit) {
    val active = selected == value
    Row(
        modifier.height(52.dp).clip(RoundedCornerShape(22.dp))
            .background(if (active) color else color.copy(alpha = 0.75f))
            .clickable { onRate(value) }.padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val contentColor = if (value == "AGAIN") v1Ink else Color.White
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(3.dp))
        Text(label, color = contentColor, fontWeight = if (active) FontWeight.Bold
            else FontWeight.Medium, fontSize = 12.sp)
    }
}
