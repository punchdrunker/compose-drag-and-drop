package com.example.myapplication

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DragAndDropScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DragAndDropScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "DRAG AND DROP") },
            )
        },
    ) { paddingValues ->
        DragAndDropContent(
            modifier = Modifier.padding(paddingValues)
        )
    }
}


@Composable
fun DragAndDropContent(modifier: Modifier = Modifier) {
    val viewModel: DragAndDropViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier = modifier) {
        val label = remember { "Drag me" }
        Row() {
            Box(
                modifier =
                    Modifier
                        .dragAndDropSource { _ ->
                            DragAndDropTransferData(
                                clipData = ClipData.newPlainText(label, label),
                                flags = View.DRAG_FLAG_GLOBAL,
                            )
                        }
                        .border(
                            border =
                                BorderStroke(
                                    width = 4.dp,
                                    brush = Brush.linearGradient(
                                        listOf(
                                            Color.Magenta,
                                            Color.Magenta
                                        )
                                    )
                                ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp),
            ) {
                Text(modifier = Modifier.align(Alignment.Center), text = label)
            }
            Spacer(modifier = Modifier.width(16.dp))

            Image(
                modifier = Modifier
                    .size(80.dp)
                    .dragAndDropSource { _ ->
                        DragAndDropTransferData(
                            clipData = ClipData.newPlainText("image", "text for image"),
                            flags = View.DRAG_FLAG_GLOBAL,
                        )
                    },
                painter = painterResource(R.drawable.ic_android_black_24dp),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextDragAndDropTargetSample(uiState.summary) { event ->
            viewModel.updateState(event)
        }
    }
}

@Composable
fun TextDragAndDropTargetSample(
    eventSummary: String?,
    onDragAndDropEventDropped: (DragAndDropEvent) -> Unit,
) {
    val validMimeTypePrefixes = remember {
        setOf(
            ClipDescription.MIMETYPE_TEXT_INTENT,
            "image/",
            "text/",
            "video/",
            "audio/",
        )
    }
    var backgroundColor by remember { mutableStateOf(Color.Transparent) }
    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onStarted(event: DragAndDropEvent) {
                backgroundColor = Color.DarkGray.copy(alpha = 0.2f)
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                onDragAndDropEventDropped(event)
                return true
            }

            override fun onEnded(event: DragAndDropEvent) {
                backgroundColor = Color.Transparent
            }
        }
    }
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .dragAndDropTarget(
                    shouldStartDragAndDrop = accept@{ startEvent ->
                        val hasValidMimeType =
                            startEvent.mimeTypes().any { eventMimeType ->
                                validMimeTypePrefixes.any(eventMimeType::startsWith)
                            }
                        hasValidMimeType
                    },
                    target = dragAndDropTarget,
                )
                .background(backgroundColor)
                .border(width = 4.dp, color = Color.Magenta, shape = RoundedCornerShape(16.dp)),
    ) {
        when (eventSummary) {
            null -> Text(modifier = Modifier.align(Alignment.Center), text = "Drop anything here")
            else ->
                Text(
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                            .verticalScroll(rememberScrollState()),
                    text = eventSummary
                )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        DragAndDropScreen()
    }
}