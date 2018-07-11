package co.netguru.android.carrecognition.data.db

import com.google.gson.annotations.SerializedName
import java.util.*

data class CarsList(
        @SerializedName("cars")
        val cars: Array<Cars>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CarsList

        if (!Arrays.equals(cars, other.cars)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(cars)
    }
}
