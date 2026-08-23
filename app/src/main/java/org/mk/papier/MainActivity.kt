package org.mk.papier

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.mk.papier.ui.home.HomeScreen
import org.mk.papier.ui.tags.TagsScreen
import org.mk.papier.ui.theme.PapierTheme
import org.mk.papier.ui.words.FlashcardsScreen
import org.mk.papier.ui.words.WordListScreen
import org.mk.papier.ui.words.WordsHubScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PapierTheme {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(navController = navController, startDestination = "home") {

                        composable("home") {
                            HomeScreen(onTopicClick = { topic ->
                                when (topic.id) {
                                    "words" -> navController.navigate("words_hub")
                                    "tags" -> navController.navigate("tags")
                                }
                            })
                        }

                        composable("tags") {
                            TagsScreen(
                                onBack = { navController.popBackStack() },
                                onThemeClick = { theme ->
                                    navController.navigate("word_list?theme=${Uri.encode(theme)}")
                                }
                            )
                        }

                        composable("words_hub") {
                            WordsHubScreen(
                                onBack = { navController.popBackStack() },
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable(
                            route = "word_list?filter={filter}&theme={theme}",
                            arguments = listOf(
                                navArgument("filter") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                },
                                navArgument("theme") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) {
                            WordListScreen(onBack = { navController.popBackStack() })
                        }

                        composable(
                            route = "flashcards?filter={filter}",
                            arguments = listOf(navArgument("filter") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            })
                        ) {
                            FlashcardsScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
