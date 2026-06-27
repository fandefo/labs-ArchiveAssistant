package com.lyihub.archiveassistant.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lyihub.archiveassistant.R
import com.lyihub.archiveassistant.ui.theme.ImperialBronze
import com.lyihub.archiveassistant.ui.theme.ImperialCinnabar
import com.lyihub.archiveassistant.ui.theme.ImperialIvory
import com.lyihub.archiveassistant.ui.theme.ImperialLightGold
import com.lyihub.archiveassistant.ui.theme.ImperialParchment
import com.lyihub.archiveassistant.ui.theme.ImperialUmber
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val MemorialCoverAspect = 514f / 725f

@Composable
fun MemorialBriefingPane(
    pendingCount: Int,
    onOpenMemorialDemo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(ImperialIvory)
            .clickable(onClick = onOpenMemorialDemo),
    ) {
        val expanded = maxWidth >= 620.dp
        Image(
            painter = painterResource(id = R.drawable.memorial_xuan_paper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.36f,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ImperialIvory.copy(alpha = 0.1f),
                            ImperialParchment.copy(alpha = 0.45f),
                            ImperialIvory.copy(alpha = 0.82f),
                        ),
                        center = Offset.Infinite,
                        radius = 900f,
                    ),
                ),
        )
        MemorialCoverWheel(
            coverResources = MemorialCoverResources,
            modifier = Modifier.fillMaxSize(),
        )
        BriefingCopy(
            pendingCount = pendingCount,
            expanded = expanded,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(if (expanded) 30.dp else 20.dp),
        )
        RingCenterHint(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = if (expanded) 48.dp else 24.dp),
        )
    }
}

@Composable
private fun MemorialCoverWheel(
    coverResources: List<Int>,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val expanded = maxWidth >= 620.dp
        val panelMin = min(maxWidth.value, maxHeight.value).dp
        val radius = panelMin * if (expanded) 1.26f else 1.18f
        val centerX = maxWidth * if (expanded) 1.22f else 1.16f
        val centerY = maxHeight * if (expanded) 1.06f else 1.02f
        val cardWidth = if (expanded) 110.dp else 84.dp
        val startDegrees = if (expanded) 205f else 210f
        val stepDegrees = if (expanded) 10.6f else 12.2f

        repeat(18) { index ->
            val degrees = startDegrees + index * stepDegrees
            MemorialWheelCover(
                resId = coverResources[index % coverResources.size],
                degrees = degrees,
                centerX = centerX,
                centerY = centerY,
                radius = radius,
                width = cardWidth,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun MemorialWheelCover(
    resId: Int,
    degrees: Float,
    centerX: Dp,
    centerY: Dp,
    radius: Dp,
    width: Dp,
    modifier: Modifier = Modifier,
) {
    val radians = Math.toRadians(degrees.toDouble())
    val height = width / MemorialCoverAspect
    val x = centerX + radius * cos(radians).toFloat() - width / 2f
    val y = centerY + radius * sin(radians).toFloat() - height / 2f
    Box(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .offset(x = x, y = y)
                .width(width)
                .aspectRatio(MemorialCoverAspect)
                .graphicsLayer(rotationZ = degrees + 90f)
                .background(ImperialParchment, RoundedCornerShape(3.dp))
                .border(1.dp, ImperialBronze.copy(alpha = 0.58f), RoundedCornerShape(3.dp)),
        ) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.05f),
                                ImperialUmber.copy(alpha = 0.12f),
                            ),
                        ),
                    ),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(4.dp, ImperialIvory.copy(alpha = 0.18f), RoundedCornerShape(3.dp)),
            )
        }
    }
}

@Composable
private fun BriefingCopy(
    pendingCount: Int,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(if (expanded) 10.dp else 8.dp),
    ) {
        Text(
            text = "奏章",
            style = if (expanded) MaterialTheme.typography.displayLarge else MaterialTheme.typography.displayMedium,
            color = ImperialUmber,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = "门下既审，呈于御前",
            style = MaterialTheme.typography.titleMedium,
            color = ImperialUmber.copy(alpha = 0.82f),
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "今日尚有 $pendingCount 封待批。轻触此页，展开奏章堆叠，准、驳、留中皆可一笔批下。",
            style = MaterialTheme.typography.bodyMedium,
            color = ImperialUmber.copy(alpha = 0.72f),
            modifier = Modifier.fillMaxWidth(if (expanded) 0.44f else 0.62f),
        )
    }
}

@Composable
private fun RingCenterHint(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .size(154.dp)
            .background(ImperialIvory.copy(alpha = 0.72f))
            .border(1.dp, ImperialParchment)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.dashboard_placeholder),
            contentDescription = null,
            modifier = Modifier.size(42.dp),
            colorFilter = ColorFilter.tint(ImperialBronze),
            alpha = 0.82f,
        )
        Text(
            text = "批阅",
            style = MaterialTheme.typography.titleLarge,
            color = ImperialUmber,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "奏折在此",
            style = MaterialTheme.typography.bodySmall,
            color = ImperialUmber.copy(alpha = 0.64f),
            textAlign = TextAlign.Center,
        )
    }
}
