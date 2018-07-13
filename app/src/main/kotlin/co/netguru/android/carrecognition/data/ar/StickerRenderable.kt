package co.netguru.android.carrecognition.data.ar

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.common.extensions.getDrawableIdentifier
import co.netguru.android.carrecognition.data.db.Cars
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable

class StickerNode(
    private val car: Cars,
    private val context: Context,
    private val onClickCallback: () -> Unit
) : AnchorNode() {

    override fun onActivate() {
        ViewRenderable.builder()
            .setView(context, R.layout.sticker).build()
            .thenAccept {
                val view = it.view
                view.setOnClickListener { onClickCallback() }

                val textView = view.findViewById<TextView>(R.id.model_label)
                textView.text = context.getString(
                    R.string.maker_model_template,
                    car.brand,
                    car.model
                )

                val logoImage = view.findViewById<ImageView>(R.id.model_maker_logo_image)
                logoImage.setImageResource(context.getDrawableIdentifier(car.brand_logo_image))
                renderable = it
            }

        localPosition = Vector3(0f, 0.5f, -1.5f)
        localScale = Vector3(2f, 2f, 2f)
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
