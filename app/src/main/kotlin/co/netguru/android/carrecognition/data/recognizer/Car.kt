package co.netguru.android.carrecognition.data.recognizer

enum class Car(
    val id: String
) {

    OTHER_CAR("OtherCar"),
    NOT_A_CAR("NotCar"),
    FORD_FIESTA("FordFiesta"),
    HONDA_CIVIC("HondaCivic"),
    NISSAN_QASHQAI("NissanQashqai"),
    TOYOTA_CAMRY("ToyotaCamry"),
    TOYOTA_COROLLA("ToyotaCorolla"),
    VOLKSWAGEN_GOLF("VolkswagenGolf"),
    VOLKSWAGEN_PASSAT("VolkswagenPassat"),
    VOLKSWAGEN_TIGUAN("VolkswagenTiguan");

    companion object {
        const val TOP_SPEED_MAX = 200
        const val ZERO_TO_SIXTY_MIN = 2.9f
        const val ZERO_TO_SIXTY_MAX = 20f
        const val HORSEPOWER_MAX = 320
        const val ENGINE_MAX = 4000
        fun of(text: String) = valueOf(text.replace(" ", "_").toUpperCase())
    }
}
