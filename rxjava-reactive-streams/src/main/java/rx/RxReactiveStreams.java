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
import rx.internal.reactivestreams.ObservableToPublisherAdapter;
import rx.internal.reactivestreams.RxSubscriberToRsSubscriberAdapter;

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
     * @param observable the {@link Observable} to convert
     * @return the converted {@link Publisher}
     */
    public static <T> Publisher<T> toPublisher(Observable<T> observable) {
        return new ObservableToPublisherAdapter<T>(observable);
    }

    /**
     * Convert a Reactive Streams {@link Publisher} into a Rx {@link Observable}.
     * <p/>
     * Use this method when you have a stream from another library, that you want to be consume as an RxJava observable.
     *
     * @param publisher the {@link Publisher} to convert.
     * @return the converted {@link Observable}
     */
    public static <T> Observable<T> toObservable(final Publisher<T> publisher) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final rx.Subscriber<? super T> rxSubscriber) {
                publisher.subscribe(new RxSubscriberToRsSubscriberAdapter<T>(rxSubscriber));
            }
        });
    }

}
