package co.netguru.android.carrecognition.data.rest

data class Response(
        val image: Image,
        val objects: List<SHObject>,
        val requestId: String
)

data class Attributes(
        val system: SHSystem
)

data class Bounding(
        val vertices: List<Vertice>
)

data class Color(
        val confidence: Double,
        val name: String
)

data class Image(
        val width: Int,
        val orientation: Int,
        val height: Int
)

data class Make(
        val confidence: Double,
        val name: String
)

data class Model(
        val confidence: Double,
        val name: String
)

data class SHObject(
        val vehicleAnnotation: VehicleAnnotation,
        val objectId: String,
        val objectType: String
)

data class SHSystem(
        val color: Color,
        val make: Make,
        val model: Model,
        val vehicleType: String
)

data class VehicleAnnotation(
        val bounding: Bounding,
        val attributes: Attributes,
        val recognitionConfidence: Double
)

data class Vertice(
        val y: Int,
        val x: Int
)