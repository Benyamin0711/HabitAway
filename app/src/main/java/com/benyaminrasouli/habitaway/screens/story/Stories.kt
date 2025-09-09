@file:OptIn(ExperimentalMaterial3Api::class)

package com.benyaminrasouli.habitaway.stories

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ----------------- Models -----------------
sealed class StorySlide {
    data class Image(val url: String) : StorySlide()
    data class Cta(val url: String, val buttonText: String, val route: String) : StorySlide()
}

data class StoryModel(
    val id: String,
    val title: String,
    val avatarUrl: String,
    val slides: List<StorySlide>,
    val seen: Boolean = false,
    val lastSeenIndex: Int = 0
)

// ----------------- Repository (StateFlow) -----------------
interface StoryRepository {
    fun storiesFlow(): StateFlow<List<StoryModel>>
    suspend fun addStory(story: StoryModel)
    suspend fun removeStory(id: String)
    suspend fun updateProgress(id: String, lastIndex: Int, seen: Boolean)
    suspend fun replaceAll(list: List<StoryModel>)
}

class InMemoryStoryRepository(initial: List<StoryModel> = emptyList()) : StoryRepository {
    private val _state = MutableStateFlow(initial)
    override fun storiesFlow(): StateFlow<List<StoryModel>> = _state
    override suspend fun addStory(story: StoryModel) { _state.value = _state.value + story }
    override suspend fun removeStory(id: String) { _state.value = _state.value.filterNot { it.id == id } }
    override suspend fun updateProgress(id: String, lastIndex: Int, seen: Boolean) {
        _state.value = _state.value.map {
            if (it.id == id) it.copy(lastSeenIndex = lastIndex, seen = seen) else it
        }
    }
    override suspend fun replaceAll(list: List<StoryModel>) { _state.value = list }
}

// ----------------- Public composables -----------------

/**
 * StoriesBar:
 * - نمایش آواتارها با حلقهٔ گرادیانت (برای unseen) یا خاکستری (seen)
 * - روی کلیک آواتار -> Dialog تمام‌صفحه ویور باز میشه (فول‌اسکرین + input consumed)
 */
@Composable
fun StoriesBar(
    repo: StoryRepository,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    unseenColors: List<Color> = listOf(Color(0xFFDE0046), Color(0xFFF7A34B)), // اینستا-مانند
    seenColor: Color = Color.Gray
) {
    val stories by repo.storiesFlow().collectAsState()
    var active by remember { mutableStateOf<StoryModel?>(null) }

    Box(modifier = modifier) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(stories, key = { it.id }) { s ->
                StoryAvatar(
                    story = s,
                    gradientColors = unseenColors,
                    seenColor = seenColor,
                    onClick = { active = s }
                )
            }
        }
    }

    // وقتی active != null، Dialog فول‌اسکرین باز می‌کنیم تا تحتِ لایه‌ی زیرین اسکرولی صورت نگیره
    if (active != null) {
        Dialog(
            onDismissRequest = { active = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            // Dialog خودش overlay ایجاد می‌کنه؛ StoryViewer کل صفحه را می‌گیره
            active?.let { story ->
                StoryViewerDialogContainer(
                    story = story,
                    repo = repo,
                    onClose = { active = null },
                    onNavigate = { route ->
                        onNavigate(route)
                        active = null
                    }
                )
            }
        }
    }
}

@Composable
private fun StoryAvatar(
    story: StoryModel,
    gradientColors: List<Color>,
    seenColor: Color,
    onClick: () -> Unit
) {
    val ringBrush = if (!story.seen) Brush.linearGradient(colors = gradientColors)
    else Brush.linearGradient(colors = listOf(seenColor, seenColor))

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(84.dp)) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(width = 3.dp, brush = ringBrush, shape = CircleShape)
                .padding(3.dp)
                .clip(CircleShape)
                .pointerInput(story.id) {
                    detectTapGestures(onTap = { onClick() })
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(story.avatarUrl).build(),
                contentDescription = story.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = story.title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ----------------- Dialog container wrapper -----------------
@Composable
private fun StoryViewerDialogContainer(
    story: StoryModel,
    repo: StoryRepository,
    onClose: () -> Unit,
    onNavigate: (String) -> Unit
) {
    // یک container ساده که فضای سیستم‌بارها رو حفظ می‌کند و خودش Viewer را می‌سنجد
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        StoryViewer(
            story = story,
            repo = repo,
            onClose = onClose,
            onNavigate = onNavigate
        )
    }
}

// ----------------- Fullscreen viewer (اینستا-مانند) -----------------
@Composable
private fun StoryViewer(
    story: StoryModel,
    repo: StoryRepository,
    onClose: () -> Unit,
    onNavigate: (String) -> Unit,
    slideDurationMs: Long = 4500L
) {
    val ctx = LocalContext.current
    val loader = remember { ImageLoader(ctx) }
    var index by remember { mutableStateOf(story.lastSeenIndex.coerceIn(0, story.slides.lastIndex)) }
    var paused by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // preload cur + next
    LaunchedEffect(story.id, index) {
        listOfNotNull(story.slides.getOrNull(index), story.slides.getOrNull(index + 1)).forEach { s ->
            val url = when (s) {
                is StorySlide.Image -> s.url
                is StorySlide.Cta -> s.url
            }
            loader.enqueue(ImageRequest.Builder(ctx).data(url).build())
        }
    }

    // progress anim
    val progress = remember { Animatable(0f) }
    var animationJob: Job? by remember { mutableStateOf(null) }

    LaunchedEffect(index, paused) {
        animationJob?.cancel()
        progress.snapTo(0f)
        if (!paused) {
            animationJob = scope.launch {
                try {
                    progress.animateTo(1f, animationSpec = tween(durationMillis = slideDurationMs.toInt()))
                    // finished
                    if (index < story.slides.lastIndex) {
                        val newIndex = index + 1
                        scope.launch { repo.updateProgress(story.id, newIndex, false) }
                        index = newIndex
                    } else {
                        scope.launch { repo.updateProgress(story.id, index, true) }
                        onClose()
                    }
                } catch (_: Exception) { /* cancelled */ }
            }
        }
    }

    // UI: full screen + input consumers
    Surface(color = Color.Black.copy(alpha = 0.97f)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                // first pointerInput: vertical drag to dismiss (consume changes)
                .pointerInput(story.id) {
                    detectDragGestures { change: PointerInputChange, dragAmount ->
                        val (_, dy) = dragAmount
                        if (dy > 30f) {
                            change.consumeAllChanges()
                            onClose()
                        } else {
                            // consume small moves too so underlying doesn't scroll
                            change.consumeAllChanges()
                        }
                    }
                }
                // second pointerInput: tap/long-press actions
                .pointerInput(story.id) {
                    detectTapGestures(
                        onLongPress = { paused = true },
                        onTap = { pos ->
                            val w = size.width
                            if (pos.x < w / 3f) {
                                // prev
                                if (index > 0) {
                                    val newIndex = index - 1
                                    index = newIndex
                                    scope.launch { repo.updateProgress(story.id, newIndex, false) }
                                }
                            } else {
                                // next / finish
                                if (index < story.slides.lastIndex) {
                                    val newIndex = index + 1
                                    index = newIndex
                                    scope.launch { repo.updateProgress(story.id, newIndex, false) }
                                } else {
                                    scope.launch { repo.updateProgress(story.id, index, true) }
                                    onClose()
                                }
                            }
                        },
                        onPress = {
                            val released = tryAwaitRelease()
                            // resume when released
                            if (released) paused = false
                        }
                    )
                }
        ) {
            // current slide image
            val slide = story.slides[index]
            val imageUrl = when (slide) {
                is StorySlide.Image -> slide.url
                is StorySlide.Cta -> slide.url
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
                contentDescription = story.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // top progress bars + header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    story.slides.forEachIndexed { i, _ ->
                        val p = when {
                            i < index -> 1f
                            i == index -> progress.value
                            else -> 0f
                        }
                        LinearProgressIndicator(
                            progress = p,
                            modifier = Modifier.weight(1f).height(3.dp),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.25f)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.DarkGray)) {
                        AsyncImage(
                            model = story.avatarUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {
                        scope.launch { repo.updateProgress(story.id, index, true) }
                        onClose()
                    }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "close", tint = Color.White)
                    }
                }
            }

            // CTA button on CTA slides
            if (slide is StorySlide.Cta) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    Button(
                        onClick = { onNavigate(slide.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(text = slide.buttonText)
                    }
                }
            }
        }
    }
}

// ----------------- sample data builder -----------------
fun sampleInstagramStyleStories(): List<StoryModel> {
    return listOf(
        StoryModel(
            id = "st1",
            title = "چالش 60 روزه",
            avatarUrl = "https://picsum.photos/200/200?avatar=1",
            slides = listOf(
                StorySlide.Image("https://picsum.photos/1080/1920?img=1"),
                StorySlide.Image("https://picsum.photos/1080/1920?img=2"),
                StorySlide.Image("https://picsum.photos/1080/1920?img=3"),
                StorySlide.Cta("https://picsum.photos/1080/1920?img=4", "شروع چالش", "Challenge60")
            )
        ),
        StoryModel(
            id = "st2",
            title = "حال‌خوبی",
            avatarUrl = "https://picsum.photos/200/200?avatar=2",
            slides = listOf(
                StorySlide.Image("https://picsum.photos/1080/1920?img=5"),
                StorySlide.Image("https://picsum.photos/1080/1920?img=6"),
                StorySlide.Image("https://picsum.photos/1080/1920?img=7"),
                StorySlide.Cta("https://picsum.photos/1080/1920?img=8", "برو به اتاق", "study_room")
            )
        )
    )
}
