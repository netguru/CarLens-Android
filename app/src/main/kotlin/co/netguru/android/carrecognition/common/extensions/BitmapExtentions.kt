package co.netguru.android.carrecognition.common.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.Image
import android.renderscript.*
import co.netguru.android.carrecognition.ScriptC_yuv420888


class ImageUtils {

    companion object {

        /**
         * Transforms passed [yuvImage] (YUV420888 format) with given [width] and [height]
         * to RGB image, rotates it with for [rotation] degrees and
         * scales to size of [inputSize] x [inputSize] pixels
         */
        fun prepareBitmap(
            context: Context, image: Image, width: Int,
            height: Int, rotation: Int, inputSize: Int
        ): IntArray {
            return rotateAndScaleBitmap(
                yuv420888toRbgBitmap(context, image, width, height),
                -rotation, inputSize
            ).getPixels(inputSize * inputSize)
        }

        /**
         * Transforms passed [yuvImage] (NV21 format) with given [width] and [height]
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

        private fun yuv420888toRbgBitmap(
            context: Context,
            image: Image,
            width: Int,
            height: Int
        ): Bitmap {
            // Get the three image planes
            val planes = image.planes
            var buffer = planes[0].buffer
            val y = ByteArray(buffer.remaining())
            buffer.get(y)

            buffer = planes[1].buffer
            val u = ByteArray(buffer.remaining())
            buffer.get(u)

            buffer = planes[2].buffer
            val v = ByteArray(buffer.remaining())
            buffer.get(v)

            // get the relevant RowStrides and PixelStrides
            // (we know from documentation that PixelStride is 1 for y)
            val yRowStride = planes[0].rowStride
            val uvRowStride =
                planes[1].rowStride  // we know from   documentation that RowStride is the same for u and v.
            val uvPixelStride =
                planes[1].pixelStride  // we know from   documentation that PixelStride is the same for u and v.


            // rs creation just for demo. Create rs just once in onCreate and use it again.
            val rs = RenderScript.create(context)
            //RenderScript rs = MainActivity.rs;
            val mYuv420 = ScriptC_yuv420888(rs)

            // Y,U,V are defined as global allocations, the out-Allocation is the Bitmap.
            // Note also that uAlloc and vAlloc are 1-dimensional while yAlloc is 2-dimensional.
            val typeUcharY = Type.Builder(rs, Element.U8(rs))
            typeUcharY.setX(yRowStride).setY(height)
            val yAlloc = Allocation.createTyped(rs, typeUcharY.create())
            yAlloc.copyFrom(y)
            mYuv420._ypsIn = yAlloc

            val typeUcharUV = Type.Builder(rs, Element.U8(rs))
            // note that the size of the u's and v's are as follows:
            //      (  (width/2)*PixelStride + padding  ) * (height/2)
            // =    (RowStride                          ) * (height/2)
            // but I noted that on the S7 it is 1 less...
            typeUcharUV.setX(u.size)
            val uAlloc = Allocation.createTyped(rs, typeUcharUV.create())
            uAlloc.copyFrom(u)
            mYuv420._uIn = uAlloc

            val vAlloc = Allocation.createTyped(rs, typeUcharUV.create())
            vAlloc.copyFrom(v)
            mYuv420._vIn = vAlloc

            // handover parameters
            mYuv420._picWidth = width.toLong()
            mYuv420._uvRowStride = uvRowStride.toLong()
            mYuv420._uvPixelStride = uvPixelStride.toLong()

            val outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val outAlloc = Allocation.createFromBitmap(
                rs,
                outBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
            )

            val lo = Script.LaunchOptions()
// by this we ignore the y’s padding zone, i.e. the right side of x between width and yRowStride
            lo.setX(0, width)
            lo.setY(0, height)

            mYuv420.forEach_doConvert(outAlloc, lo)
            outAlloc.copyTo(outBitmap)

            return outBitmap
        }
    }

}