@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class) // Add it here

package com.cpx.habitaway.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.navigation.NavHostController

// ---------------- DataStore top-level delegate ----------------
private const val DATASTORE_NAME = "challenge_prefs"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)
private val KEY_PROGRESS = stringPreferencesKey("challenge_progress_v1")

// ---------------- categories ----------------
private val CATEGORIES = listOf(
    Category("exercise", "🏋️", "ورزش"),
    Category("control", "⚙️", "کنترل"),
    Category("nutrition", "🍎", "تغذیه"),
    Category("focus", "🧠", "تمرکز"),
    Category("journal", "📝", "ژورنال")
)

data class Category(val key: String, val emoji: String, val label: String)

// ---------------- serialization helpers ----------------
private fun buildDefaultProgressString(days: Int = 60, categories: Int = CATEGORIES.size): String {
    return List(days) { "0".repeat(categories) }.joinToString(separator = ",")
}

private fun parseProgressString(str: String?, days: Int = 60, categories: Int = CATEGORIES.size): List<List<Boolean>> {
    val default = List(days) { List(categories) { false } }
    if (str.isNullOrEmpty()) return default
    val blocks = str.split(",")
    return (0 until days).map { dayIndex ->
        val block = blocks.getOrNull(dayIndex) ?: "0".repeat(categories)
        block.padEnd(categories, '0').substring(0, categories).map { it == '1' }
    }
}

private fun buildProgressStringFromState(state: List<List<Boolean>>): String {
    return state.joinToString(",") { day ->
        day.joinToString("") { if (it) "1" else "0" }
    }
}

// ---------------- main composable ----------------
@Composable
fun Challenge60(
    navController: NavHostController,
    title: String = "چالش ۶۰ روزه"
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // read DataStore value as state
    val defaultString = remember { buildDefaultProgressString() }
    val progressStringFlow = remember {
        context.dataStore.data.map { prefs ->
            prefs[KEY_PROGRESS] ?: defaultString
        }
    }
    val progressString by progressStringFlow.collectAsState(initial = defaultString)

    // convert to mutable state lists
    val initialState = remember(progressString) {
        parseProgressString(progressString).map { dayList ->
            dayList.map { checked -> mutableStateOf(checked) }
        }
    }
    // daysState: SnapshotStateList of MutableState<Boolean> lists
    val daysState: SnapshotStateList<SnapshotStateList<MutableState<Boolean>>> = remember {
        initialState.map { it.toMutableStateList() }.toMutableStateList()
    }

    // persistence helper
    suspend fun persistState() {
        val plain: List<List<Boolean>> = daysState.map { day -> day.map { it.value } }
        val s = buildProgressStringFromState(plain)
        context.dataStore.edit { prefs ->
            prefs[KEY_PROGRESS] = s
        }
    }

    // derived stats
    val totalBoxes = daysState.size * CATEGORIES.size
    val checkedBoxes by remember { derivedStateOf { daysState.sumOf { day -> day.count { it.value } } } }
    val successRate by remember { derivedStateOf { if (totalBoxes == 0) 0 else (checkedBoxes * 100 / totalBoxes) } }
    val completedDays by remember { derivedStateOf { daysState.count { day -> day.count { it.value } >= 3 } } }
    val bestStreak by remember {
        derivedStateOf {
            var cur = 0; var best = 0
            daysState.forEach { day ->
                if (day.count { it.value } >= 3) {
                    cur++; if (cur > best) best = cur
                } else cur = 0
            }
            best
        }
    }

    val animatedSuccessRate by animateFloatAsState(targetValue = successRate / 100f)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, fontWeight = FontWeight.SemiBold) }
            )

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // header card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${(animatedSuccessRate * 100).roundToInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("پیشرفت کلی چالش", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("$completedDays از ${daysState.size} روز تکمیل شده", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(progress = animatedSuccessRate, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("هر روز برای هر دسته روی مربع کلیک کن. حداقل ۳ فعالیت در روز برای شمردن به عنوان تکمیل‌شده.", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(label = "روزهای تکمیل شده", value = completedDays.toString())
                Spacer(modifier = Modifier.width(8.dp))
                StatCard(label = "میزان موفقیت", value = "$successRate%")
                Spacer(modifier = Modifier.width(8.dp))
                StatCard(label = "بیشترین استریک", value = bestStreak.toString())
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(daysState) { index, day ->
                    DayCard(
                        dayIndex = index + 1,
                        checks = day,
                        onToggle = { catIndex ->
                            val current = day[catIndex]
                            current.value = !current.value
                            scope.launch { persistState() }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    daysState.forEach { day -> day.forEach { it.value = false } }
                    scope.launch { persistState() }
                }, modifier = Modifier.weight(1f)) {
                    Text("ریست")
                }

                OutlinedButton(onClick = {
                    for (i in 0 until daysState.size) {
                        if (i < 3) {
                            for (j in 0 until CATEGORIES.size) daysState[i][j].value = (j < 3)
                        }
                    }
                    scope.launch { persistState() }
                }, modifier = Modifier.weight(1f)) {
                    Text("پر کردن نمونه")
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) { // Add modifier parameter
    Card(
        modifier = modifier, // Apply the passed modifier here
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun DayCard(dayIndex: Int, checks: List<MutableState<Boolean>>, onToggle: (catIndex: Int) -> Unit) {
    Card(modifier = Modifier
        .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "روز $dayIndex", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(8.dp))

            // categories row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                checks.forEachIndexed { idx, state ->
                    val bg = when {
                        !state.value -> Color.White
                        else -> when (checks.count { it.value }) {
                            in 0..2 -> Color(0xFFE74C3C)
                            in 3..4 -> Color(0xFFF39C12)
                            else -> Color(0xFF27AE60)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (state.value) bg else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (state.value) bg else Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { onToggle(idx) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = CATEGORIES[idx].emoji, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            val checkedCount = checks.count { it.value }
            val percent = checkedCount / CATEGORIES.size.toFloat()
            LinearProgressIndicator(progress = percent, modifier = Modifier
                .fillMaxWidth()
                .height(6.dp), color = when {
                percent < 0.3f -> Color(0xFFE74C3C)
                percent < 0.7f -> Color(0xFFF39C12)
                else -> Color(0xFF27AE60)
            })
        }
    }
}
