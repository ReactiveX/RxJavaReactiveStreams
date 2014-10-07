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
import org.reactivestreams.Subscriber;
import rx.internal.ObservablePublisher;
import rx.internal.ObservableSubscriber;
import rx.internal.PublisherObservableOnSubscribe;
import rx.internal.PublisherSubscriber;

/**
 * The {@link RxReactiveStreams} helper class provides static utility methods to convert to and from
 * {@link Observable}s and {@link Publisher}s.
 */
public class RxReactiveStreams {

    /**
     * Convert a Rx {@link Observable} into a Reactive Streams {@link Publisher}.
     *
     * @param observable the {@link Observable} to convert.
     * @return the converted {@link Publisher}.
     */
    public static <T> Publisher<T> toPublisher(Observable<T> observable) {
        return new ObservablePublisher<T>(observable);
    }

    /**
     * Convert a Reactive Streams {@link Publisher} into a Rx {@link Observable}.
     *
     * @param publisher the {@link Publisher} to convert.
     * @return the converted {@link Observable}.
     */
    public static <T> Observable<T> toObservable(final Publisher<T> publisher) {
        return Observable.create(new PublisherObservableOnSubscribe<T>(publisher));
    }

    /**
     * Subscribe to the given Rx {@link Observable} with a Reactive Streams {@link Subscriber}.
     *
     * TODO: The {@link Subscriber} needs to be made contravariant after
     * https://github.com/reactive-streams/reactive-streams/issues/104 is fixed.
     *
     * @param observable the {@link Observable} to subscribe to.
     * @param subscriber the {@link Subscriber} which subscribes.
     */
    public static <T> void subscribe(Observable<T> observable, Subscriber<T> subscriber) {
        observable.subscribe(new ObservableSubscriber<T>(subscriber));
    }

    /**
     * Subscribe to the given {@link Publisher} with a Rx {@link Subscriber}.
     *
     * @param publisher the {@link Publisher} to subscribe to.
     * @param subscriber the {@link rx.Subscriber} which subscribes.
     */
    public static <T> void subscribe(Publisher<T> publisher, rx.Subscriber<? super T> subscriber) {
        publisher.subscribe(new PublisherSubscriber<T>(subscriber));
    }

}
