package co.netguru.android.carrecognition.common.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.annotation.AttrRes
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.util.TypedValue

inline fun <reified T : Activity> Context.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

fun Context.getDrawableCompat(@DrawableRes drawable: Int) =
        ContextCompat.getDrawable(this, drawable)

fun Context.getAttributeColor(@AttrRes attrColor: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrColor, typedValue, true)
    return typedValue.data
}

fun Context.getAttributeDrawable(@AttrRes attrDrawableRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrDrawableRes, typedValue, true)
    return typedValue.resourceId
}

fun Context.getResourceIdentifier(name: String, defType: String) = resources.getIdentifier(name,
        defType, packageName)

fun Context.getMipMapIdentifier(name: String) = getResourceIdentifier(name, "mipmap")
