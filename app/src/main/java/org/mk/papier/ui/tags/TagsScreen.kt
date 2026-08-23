package org.mk.papier.ui.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.mk.papier.ui.theme.PapierTheme

@Composable
fun TagsScreen(
    onBack: () -> Unit,
    onThemeClick: (String) -> Unit = {},
    viewModel: TagsViewModel = viewModel()
) {
    TagsContent(
        themeNames = viewModel.themes.map { it.name },
        onBack = onBack,
        onThemeClick = onThemeClick
    )
}

@Composable
private fun TagsContent(
    themeNames: List<String>,
    onBack: () -> Unit,
    onThemeClick: (String) -> Unit
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Themes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${themeNames.size} themes",
                fontSize = 13.sp,
                color = Color(0xFF888888)
            )
        }

        TagCloud(
            themeNames = themeNames,
            onThemeClick = onThemeClick,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagCloud(
    themeNames: List<String>,
    onThemeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        themeNames.forEach { name ->
            TagChip(name = name, onClick = { onThemeClick(name) })
        }
    }
}

@Composable
private fun TagChip(name: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(
            text = name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TagsScreenPreview() {
    PapierTheme {
        TagsContent(
            themeNames = listOf(
                "personal info", "people", "places", "time",
                "free time", "greetings", "describing", "everyday verbs"
            ),
            onBack = {},
            onThemeClick = {}
        )
    }
}
