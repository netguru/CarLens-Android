package co.netguru.android.carrecognition.data.ar

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.TextView
import co.netguru.android.carrecognition.R
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable

@RequiresApi(Build.VERSION_CODES.N)
class StickerNode(
    private val text: String,
    private val context: Context
) : AnchorNode() {

    override fun onActivate() {
        ViewRenderable.builder()
            .setView(context, R.layout.sticker).build()
            .thenAccept {
                val view = it.view as TextView
                view.text = text
                renderable = it
            }
    }

    override fun onUpdate(frameTime: FrameTime?) {
        if (scene == null) {
            return
        }

        //rotate note so it always faces camera
        val direction = Vector3.subtract(scene.camera.worldPosition, worldPosition)
        worldRotation = Quaternion.lookRotation(direction, Vector3.up())
    }

}