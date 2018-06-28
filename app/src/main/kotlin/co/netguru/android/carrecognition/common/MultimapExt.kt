typealias Multimap<K, V> = Map<K, List<V>>

fun <K, V> mutableMultimapOf(vararg pair: Pair<K, List<V>>): MutableMap<K, List<V>> =
    mutableMapOf(*pair)

fun <K, V> multimapOf(vararg pair: Pair<K, List<V>>): Multimap<K, V> = mapOf(*pair)

fun <K, V> Multimap<K, V>.partition(
    predicate: (V) -> Boolean
): Pair<Multimap<K, V>, Multimap<K, V>> {
    val first = mutableMultimapOf<K, V>()
    val second = mutableMultimapOf<K, V>()
    for (k in this.keys) {
        val (firstValuePartition, secondValuePartition) = this[k]!!.partition(predicate)
        first[k] = firstValuePartition
        second[k] = secondValuePartition
    }
    return Pair(first, second)
}

fun <K, V> Multimap<K, V>.toPairsList(): List<Pair<K, V>> = this.flatMap { (k, v) ->
    v.map { k to it }
}

fun <K, V> Multimap<K, V>.flattenValues() = this.values.flatten()

operator fun <K, V> Multimap<K, V>.plus(pair: Pair<K, V>): Multimap<K, V> = if (this.isEmpty()) {
    multimapOf(pair.first to listOf(pair.second))
} else {
    val mutableMap = this.toMutableMap()
    val keyValues = mutableMap[pair.first]
    if (keyValues == null) {
        mutableMap[pair.first] = listOf(pair.second)
    } else {
        mutableMap[pair.first] = keyValues + listOf(pair.second)
    }
    mutableMap.toMap()
}

operator fun <K, V> Multimap<K, V>.minus(pair: Pair<K, V>): Multimap<K, V> = if (this.isEmpty()) {
    emptyMap()
} else {
    val valuesForKey = this[pair.first]
        ?.filter { it != pair.second }
        ?.let { if (it.isEmpty()) null else it }
    if (valuesForKey == null) {
        this.filterKeys { it == pair.first }
    } else {
        this.toMutableMap().apply {
            this[pair.first] = valuesForKey - pair.second
        }
            .toMap()
    }
}

fun <K, V> List<Pair<K, V>>.toMultiMap(): Multimap<K, V> {
    var result = multimapOf<K, V>()
    forEach {
        result += it
    }
    return result
}

fun <K, V, I> List<I>.toMultiMap(mapper: (I) -> Pair<K, V>): Multimap<K, V> {
    var result = multimapOf<K, V>()
    forEach {
        result += mapper(it)
    }
    return result
}