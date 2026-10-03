package org.mk.papier.ui.scroll

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ScrollPracticeScreen(onBack: () -> Unit, viewModel: ScrollPracticeViewModel = viewModel()) {
    val item = viewModel.item
    var draft by rememberSaveable { mutableStateOf("") }
    var showExample by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(Color(0xFFFFF8EF)).systemBarsPadding()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Practice", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        if (item == null) {
            Text("This word is not available.", modifier = Modifier.padding(24.dp))
            return@Column
        }

        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val headword = listOfNotNull(item.word.article, item.word.dutch).joinToString(" ")
            Text(headword, color = Color(0xFF15213A), fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold, fontSize = 38.sp)
            Text(item.word.english, color = Color(0xFF40506B), fontSize = 20.sp)
            Text("Write a Dutch sentence using this word or one of its forms.",
                color = Color(0xFF15213A), fontSize = 16.sp)
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it; showExample = false },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Your sentence...") },
                minLines = 3,
                shape = RoundedCornerShape(16.dp)
            )
            Button(onClick = { showExample = true }, enabled = draft.isNotBlank()) {
                Text("Compare with an example")
            }
            if (showExample) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.fillMaxWidth().padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("One natural example", color = Color(0xFF6D7683), fontSize = 13.sp)
                        Text(item.examples.first().dutch, color = Color(0xFF15213A),
                            fontFamily = FontFamily.Serif, fontSize = 19.sp)
                        Text(item.examples.first().english, color = Color(0xFF40506B),
                            fontSize = 15.sp)
                    }
                }
                TextButton(onClick = { draft = ""; showExample = false }) {
                    Text("Try another sentence")
                }
            }
        }
    }
}
