package co.netguru.android.carrecognition.common.extensions

import io.reactivex.*

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun Completable.applyIoSchedulers() = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun Completable.applyComputationSchedulers() = this.subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Maybe<T>.applyIoSchedulers() = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Maybe<T>.applyComputationSchedulers() = this.subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.applyIoSchedulers() = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.applyComputationSchedulers() = this.subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.applyIoSchedulers() = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.applyComputationSchedulers() = this.subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.applyIoSchedulers(): Flowable<T> = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.applyComputationSchedulers(): Flowable<T> =
    this.subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
