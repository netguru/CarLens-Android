package co.netguru.android.carrecognition.common

class LimitedList<T>(private val limit: Int) : Iterable<T> {

    private val list = ArrayList<T>(limit)

    fun add(element: T) {
        list.add(element)
        if (list.size > limit) list.removeAt(0)
    }

    fun addAll(toAdd: List<T>) {
        list.addAll(toAdd)
        while (list.size > limit) list.removeAt(0)
    }

    fun size() = list.size

    operator fun get(index: Int) = list[index]

    override fun iterator() = list.iterator()
}
