package co.netguru.android.carrecognition.data

import android.content.SharedPreferences
import co.netguru.android.carrecognition.application.scope.AppScope
import co.netguru.android.carrecognition.common.extensions.edit
import javax.inject.Inject

@AppScope
class SharedPreferencesController @Inject constructor(
        private val sharedPreferences: SharedPreferences) {

    fun setOnboardingCompleted(isCompleted: Boolean) {
        sharedPreferences.edit { this.putBoolean(KEY_IS_ONBOARDING_COMPLETED, isCompleted) }
    }

    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_ONBOARDING_COMPLETED, false)
    }

    companion object {
        private const val KEY_IS_ONBOARDING_COMPLETED = "isOnboardingCompleted"
    }
}