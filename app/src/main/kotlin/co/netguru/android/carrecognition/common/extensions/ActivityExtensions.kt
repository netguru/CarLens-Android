package co.netguru.android.carrecognition.common.extensions

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

fun AppCompatActivity.replaceFragment(@IdRes containerViewId: Int, fragment: Fragment, TAG: String) {
    supportFragmentManager
            .beginTransaction()
            .replace(containerViewId, fragment, TAG)
            .commit()
}