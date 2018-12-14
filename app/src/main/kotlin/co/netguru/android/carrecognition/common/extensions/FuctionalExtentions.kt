package co.netguru.android.carrecognition.common.extensions

inline fun <A, B, OUT> Pair<A, B>.map(f: (Pair<A, B>) -> OUT): OUT {
    return f(this)
}
