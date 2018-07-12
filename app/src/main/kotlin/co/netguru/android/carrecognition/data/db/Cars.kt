package co.netguru.android.carrecognition.data.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "cars")
data class Cars(
        @PrimaryKey
        val id: String,
        val brand: String,
        val model: String,
        val description: String,
        val stars: Int,
        val acceleration_mph: Double,
        val speed_mph: Int,
        val power: Int,
        val engine: Int,
        val brand_logo_image: String,
        val brand_logo_image_locked: String,
        val image: String,
        val image_locked: String) {

    @Expose(deserialize = false, serialize = false)
    var seen: Boolean = false
}
