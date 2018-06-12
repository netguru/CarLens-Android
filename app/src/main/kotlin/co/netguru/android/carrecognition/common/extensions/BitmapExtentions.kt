package co.netguru.android.carrecognition.common.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.renderscript.*


class ImageUtils {

    companion object {

        /**
         * Transforms passed [yuvImage] with given [width] and [height]
         * to RGB image, rotates it with for [rotation] degrees and
         * scales to size of [inputSize] x [inputSize] pixels
         */
        fun prepareBitmap(
                context: Context, yuvImage: ByteArray, width: Int,
                height: Int, rotation: Int, inputSize: Int
        ): IntArray {
            return rotateAndScaleBitmap(
                    yuvToRgbBitmap(context, yuvImage, width, height),
                    -rotation, inputSize
            ).getPixels(inputSize * inputSize)
        }

        /**
         * Converts passed NV21 [yuvImage] with given [width] and [height] to RGB bitmap
         */
        private fun yuvToRgbBitmap(context: Context, yuvImage: ByteArray, width: Int, height: Int): Bitmap {
            val rs = RenderScript.create(context)

            val yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))

            val yuvType = Type.Builder(rs, Element.U8(rs)).setX(yuvImage.size)
            val input = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT)

            val rgbaType = Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height)
            val output = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT)

            input.copyFrom(yuvImage)

            yuvToRgbIntrinsic.setInput(input)
            yuvToRgbIntrinsic.forEach(output)

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            output.copyTo(bitmap)

            return bitmap
        }

        /**
         * Rotates [inputBitmap] for [rotation] degrees and scales it to size of [desiredSize] x [desiredSize]
         */
        private fun rotateAndScaleBitmap(
                inputBitmap: Bitmap,
                rotation: Int,
                desiredSize: Int
        ): Bitmap {
            val matrix =
                    getTransformationMatrix(
                            inputBitmap.width, inputBitmap.height, desiredSize, desiredSize, rotation
                    )
            val cropToFrameTransform = Matrix()
            matrix.invert(cropToFrameTransform)
            val croppedBitmap = Bitmap.createBitmap(desiredSize, desiredSize, Bitmap.Config.RGB_565)
            val canvas = Canvas(croppedBitmap)
            canvas.drawBitmap(inputBitmap, matrix, null)
            return croppedBitmap
        }

        /**
         * Returns a transformation matrix from one reference frame into another.
         * Handles cropping maintaining aspect ratio and rotation.
         *
         * @param srcWidth Width of source frame.
         * @param srcHeight Height of source frame.
         * @param dstWidth Width of destination frame.
         * @param dstHeight Height of destination frame.
         * @param rotation Amount of rotation to apply from one frame to another.\
         * @return The transformation fulfilling the desired requirements.
         */
        private fun getTransformationMatrix(
                srcWidth: Int,
                srcHeight: Int,
                dstWidth: Int,
                dstHeight: Int,
                rotation: Int
        ): Matrix {
            val matrix = Matrix()

            if (rotation != 0) {
                matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f)
                matrix.postRotate(rotation.toFloat())
            }

            val transpose = (Math.abs(rotation) + 90) % 180 == 0

            val inWidth = if (transpose) srcHeight else srcWidth
            val inHeight = if (transpose) srcWidth else srcHeight

            if (inWidth != dstWidth || inHeight != dstHeight) {
                val scaleFactorX = dstWidth / inWidth.toFloat()
                val scaleFactorY = dstHeight / inHeight.toFloat()
                val scaleFactor = Math.max(scaleFactorX, scaleFactorY)
                matrix.postScale(scaleFactor, scaleFactor)
            }

            if (rotation != 0) {
                matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f)
            }

            return matrix
        }

        private fun Bitmap.getPixels(size: Int): IntArray {
            val intArray = IntArray(size)
            getPixels(intArray, 0, width, 0, 0, width, height)
            return intArray
        }
    }

}