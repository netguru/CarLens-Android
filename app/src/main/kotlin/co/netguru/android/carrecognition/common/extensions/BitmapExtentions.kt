package co.netguru.android.carrecognition.common.extensions

import android.graphics.Bitmap
import android.graphics.Matrix


fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.getPixels(size: Int): IntArray {
    val intArray = IntArray(size)
    getPixels(intArray, 0, width, 0, 0, width, height)
    return intArray
}

fun ByteArray.yuvNv21toRgb(width: Int, height: Int): IntArray {
    val frameSize = width * height
    val argb = IntArray(width * height)

    val ii = 0
    val ij = 0
    val di = +1
    val dj = +1

    var a = 0
    var i = 0
    var ci = ii
    while (i < height) {
        var j = 0
        var cj = ij
        while (j < width) {
            var y = 0xff and this[ci * width + cj].toInt()
            val v = 0xff and this[frameSize + (ci shr 1) * width + (cj and 1.inv()) + 0].toInt()
            val u = 0xff and this[frameSize + (ci shr 1) * width + (cj and 1.inv()) + 1].toInt()
            y = if (y < 16) 16 else y

            var b = (1.164f * (y - 16) + 1.596f * (v - 128)).toInt()
            var g = (1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128)).toInt()
            var r = (1.164f * (y - 16) + 2.018f * (u - 128)).toInt()

            r = if (r < 0) 0 else if (r > 255) 255 else r
            g = if (g < 0) 0 else if (g > 255) 255 else g
            b = if (b < 0) 0 else if (b > 255) 255 else b

            argb[a++] = -0x1000000 or (r shl 16) or (g shl 8) or b
            ++j
            cj += dj
        }
        ++i
        ci += di
    }
    return argb
}