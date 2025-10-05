package com.example.eng_letter_cuonter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LetterCounterScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun LetterCounterScreen(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }
    var sortedWords by remember { mutableStateOf(emptyList<WordStats>()) }

    // Функция для подсчета гласных букв в слове
    fun countVowels(word: String): Int {
        val vowels = setOf('a', 'e', 'i', 'o', 'u', 'y') // Английские гласные
        return word.lowercase().count { it in vowels }
    }

    // Функция для обработки текста и сортировки слов
    fun processText(inputText: String): List<WordStats> {
        if (inputText.isBlank()) return emptyList()

        // Разбиваем текст на слова, убирая всё, что не буквы
        val words = inputText.split("\\s+".toRegex())
            .map { it.replace(Regex("[^\\p{L}\\p{M}]"), "") }
            .filter { it.isNotBlank() && it.any { char -> char.isLetter() } }

        return words.map { word ->
            val totalLetters = word.count { it.isLetter() }
            val vowelCount = countVowels(word)
            val vowelRatio = if (totalLetters > 0) vowelCount.toDouble() / totalLetters else 0.0

            WordStats(
                word = word,
                vowelCount = vowelCount,
                totalLetters = totalLetters,
                vowelRatio = vowelRatio
            )
        }.sortedBy { it.vowelRatio } // Сортируем по возрастанию отосительного количества гласных
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Поле ввода текста
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                showResult = false
            },
            label = { Text("Введите текст") },
            placeholder = { Text("Напишите предложение или несколько слов...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = false,
            maxLines = 5
        )

        // Кнопка для обработки и вывода результата
        Button(
            onClick = {
                sortedWords = processText(text)
                showResult = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = text.isNotEmpty()
        ) {
            Text("Показать результат")
        }

        // Если пользователь жмёт на кнопку - выводим результат
        if (showResult) {
            if (sortedWords.isEmpty()) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Не найдено слов для анализа",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Слова в порядке возрастания относительного количества гласных:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        sortedWords.forEachIndexed { index, wordStats ->
                            WordItem(
                                wordStats = wordStats,
                                position = index + 1,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Общая статистика
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            //в задании не было, но пусть будет
                            InfoChip("Всего слов: ${sortedWords.size}")
                        }
                    }
                }
            }
        }
    }
}

// Data class для хранения статистики по слову
data class WordStats(
    val word: String,
    val vowelCount: Int,
    val totalLetters: Int,
    val vowelRatio: Double
)
//красивая карточка для слова и статистики по нему
@Composable
fun WordItem(wordStats: WordStats, position: Int, modifier: Modifier = Modifier) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Номер и слово
            Column {
                Text(
                    text = "$position. ${wordStats.word}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "гласных: ${wordStats.vowelCount}/${wordStats.totalLetters}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Относительное количество гласных
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "%.1f%%".format(wordStats.vowelRatio * 100),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LetterCounterPreview() {
    LetterCounterScreen()
}