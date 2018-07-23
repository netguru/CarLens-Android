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

        fun prepareBitmap(
            context: Context, image: Image, width: Int,
            height: Int, rotation: Int, inputSize: Int
        ): IntArray {
            return rotateAndScaleBitmap(
                camera2apiImageToBitmap(context, image, width, height),
                -rotation, inputSize
            ).getPixels(inputSize * inputSize)
        }

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

            if (rotation != 0) {
                matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f)
            }

            if (inWidth != dstWidth || inHeight != dstHeight) {
                val scaleFactorX = dstWidth / inWidth.toFloat()
                val scaleFactorY = dstHeight / inHeight.toFloat()
                val scaleFactor = Math.max(scaleFactorX, scaleFactorY)
                matrix.postScale(scaleFactor, scaleFactor)
            }

            return matrix
        }

        private fun Bitmap.getPixels(size: Int): IntArray {
            val intArray = IntArray(size)
            getPixels(intArray, 0, width, 0, 0, width, height)
            return intArray
        }

        private fun camera2apiImageToBitmap(
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
