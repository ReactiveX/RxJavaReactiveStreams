/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx;

import org.reactivestreams.Publisher;

import rx.internal.reactivestreams.*;

/**
 * This type provides static factory methods for converting to and from RxJava types and Reactive Streams types.
 * <p/>
 * The <a href="http://www.reactive-streams.org">Reactive Streams</a> API provides a common API for interoperability
 * between different reactive streaming libraries, of which RxJava is one.
 * Using the methods of this class, RxJava can collaborate with other such libraries that also implement the standard.
 */
public abstract class RxReactiveStreams {

    private RxReactiveStreams() {
    }

    /**
     * Convert a Rx {@link Observable} into a Reactive Streams {@link Publisher}.
     * <p/>
     * Use this method when you have an RxJava observable, that you want to be consumed by another library.
     *
     * @param <T> the value type
     * @param observable the {@link Observable} to convert
     * @return the converted {@link Publisher}
     */
    public static <T> Publisher<T> toPublisher(Observable<T> observable) {
        return new PublisherAdapter<T>(observable);
    }

    /**
     * Convert a Reactive Streams {@link Publisher} into a Rx {@link Observable}.
     * <p/>
     * Use this method when you have a stream from another library, that you want to be consume as an RxJava observable.
     *
     * @param <T> the value type
     * @param publisher the {@link Publisher} to convert.
     * @return the converted {@link Observable}
     */
    public static <T> Observable<T> toObservable(final Publisher<T> publisher) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final rx.Subscriber<? super T> rxSubscriber) {
                publisher.subscribe(toSubscriber(rxSubscriber));
            }
        });
    }

    /**
     * Convert an RxJava {@link rx.Subscriber} into a Reactive Streams {@link org.reactivestreams.Subscriber}.
     *
     * @param <T> the value type
     * @param rxSubscriber an RxJava subscriber
     * @return a Reactive Streams subscriber
     */
    public static <T> org.reactivestreams.Subscriber<T> toSubscriber(final rx.Subscriber<T> rxSubscriber) {
        return new SubscriberAdapter<T>(rxSubscriber);
    }

    /**
     * Converts an RxJava Completable into a Publisher that emits only onError or onComplete.
     * @param <T> the target value type
     * @param completable the Completable instance to convert
     * @return the new Publisher instance
     * @since 1.1
     * @throws NullPointerException if completable is null
     */
    public static <T> Publisher<T> toPublisher(Completable completable) {
        if (completable == null) {
            throw new NullPointerException("completable");
        }
        return new CompletableAsPublisher<T>(completable);
    }
    
    /**
     * Converst a Publisher into a Completable by ignoring all onNext values and emitting
     * onError or onComplete only.
     * @param publisher the Publisher instance to convert
     * @return the Completable instance
     * @since 1.1
     * @throws NullPointerException if publisher is null
     */
    public static Completable toCompletable(Publisher<?> publisher) {
        if (publisher == null) {
            throw new NullPointerException("publisher");
        }
        return Completable.create(new PublisherAsCompletable(publisher));
    }
    
    /**
     * Converts a Single into a Publisher which emits an onNext+onComplete if
     * the source Single signals a non-null onSuccess; or onError if the source signals
     * onError(NullPointerException) or a null value.
     * @param <T> the value type
     * @param single the Single instance to convert
     * @return the Publisher instance
     * @since 1.1
     * @throws NullPointerException if single is null
     */
    public static <T> Publisher<T> toPublisher(Single<T> single) {
        if (single == null) {
            throw new NullPointerException("single");
        }
        return new SingleAsPublisher<T>(single);
    }
    
    /**
     * Converts a Publisher into a Single which emits onSuccess if the
     * Publisher signals an onNext+onComplete; or onError if the publisher signals an
     * onError, the source Publisher is empty (NoSuchElementException) or the
     * source Publisher signals more than one onNext (IndexOutOfBoundsException).
     * @param <T> the value type
     * @param publisher the Publisher instance to convert
     * @return the Single instance
     * @since 1.1
     * @throws NullPointerException if publisher is null
     */
    public static <T> Single<T> toSingle(Publisher<T> publisher) {
        if (publisher == null) {
            throw new NullPointerException("publisher");
        }
        return Single.create(new PublisherAsSingle<T>(publisher));
    }
}
