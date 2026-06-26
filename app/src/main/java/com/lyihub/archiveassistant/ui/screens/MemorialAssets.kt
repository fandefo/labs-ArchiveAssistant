package com.lyihub.archiveassistant.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import com.lyihub.archiveassistant.R
import kotlin.math.roundToInt

internal class MemorialAssets(context: Context) {
    private val resources = context.resources

    val paperTexture: Bitmap? = BitmapFactory.decodeResource(
        resources,
        R.drawable.memorial_xuan_paper,
    )

    private val fallbackCoverTexture: Bitmap? = BitmapFactory.decodeResource(
        resources,
        R.drawable.memorial_cover_pattern,
    )

    private val coverTextures: List<Bitmap?> = listOf(
        BitmapFactory.decodeResource(resources, R.drawable.memorial_cover_01),
        BitmapFactory.decodeResource(resources, R.drawable.memorial_cover_02),
        BitmapFactory.decodeResource(resources, R.drawable.memorial_cover_03),
        BitmapFactory.decodeResource(resources, R.drawable.memorial_cover_04),
        BitmapFactory.decodeResource(resources, R.drawable.memorial_cover_05),
    )

    val buttonTexture: Bitmap? = BitmapFactory.decodeResource(
        resources,
        R.drawable.memorial_button_bg,
    )

    val buttonAspectRatio: Float = buttonTexture?.let { texture ->
        texture.width.toFloat() / texture.height.toFloat()
    } ?: (413f / 141f)

    val completionTexture: Bitmap? = BitmapFactory.decodeResource(
        resources,
        R.drawable.memorial_completion_bg,
    )

    val coverCornerTexture: Bitmap? = BitmapFactory.decodeResource(
        resources,
        R.drawable.memorial_cover_corner,
    )

    val heritageTypeface: Typeface = runCatching {
        Typeface.createFromAsset(context.assets, "fonts/ma_shan_zheng_regular.ttf")
    }.getOrElse {
        Typeface.create(Typeface.SERIF, Typeface.BOLD)
    }

    private val stampLikeTexture: Bitmap? = BitmapFactory.decodeResource(
        resources,
        R.drawable.memorial_stamp_like,
    )?.let(::buildCinnabarStampTexture)

    private val stampDislikeTexture: Bitmap? = BitmapFactory.decodeResource(
        resources,
        R.drawable.memorial_stamp_dislike,
    )?.let(::buildCinnabarStampTexture)

    fun coverTextureFor(coverSequenceIndex: Int): Bitmap? {
        if (coverSequenceIndex <= 0) return fallbackCoverTexture
        if (coverTextures.isEmpty()) return fallbackCoverTexture
        val textureIndex = coverSequenceIndex - 1
        return coverTextures[positiveModulo(textureIndex, coverTextures.size)] ?: fallbackCoverTexture
    }

    fun stampTextureFor(stamp: MemorialStamp): Bitmap? {
        return when (stamp) {
            MemorialStamp.Approve,
            MemorialStamp.Keep,
            MemorialStamp.Like -> stampLikeTexture
            MemorialStamp.Reject,
            MemorialStamp.Dislike -> stampDislikeTexture
            MemorialStamp.Collapse -> null
        }
    }

    private fun buildCinnabarStampTexture(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val input = IntArray(width * height)
        val output = IntArray(width * height)
        source.getPixels(input, 0, width, 0, 0, width, height)
        for (index in input.indices) {
            val color = input[index]
            val red = AndroidColor.red(color)
            val green = AndroidColor.green(color)
            val blue = AndroidColor.blue(color)
            val luminance = red * 0.299f + green * 0.587f + blue * 0.114f
            val maskAlpha = ((luminance - 8f) / 247f * 255f).roundToInt().coerceIn(0, 255)
            output[index] = if (maskAlpha <= 2) {
                AndroidColor.TRANSPARENT
            } else {
                AndroidColor.argb(
                    maskAlpha,
                    AndroidColor.red(STAMP_RED),
                    AndroidColor.green(STAMP_RED),
                    AndroidColor.blue(STAMP_RED),
                )
            }
        }
        return Bitmap.createBitmap(output, width, height, Bitmap.Config.ARGB_8888)
    }
}

private fun positiveModulo(value: Int, modulo: Int): Int {
    val remainder = value % modulo
    return if (remainder >= 0) remainder else remainder + modulo
}
