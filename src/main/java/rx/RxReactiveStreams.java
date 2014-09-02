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

public class RxReactiveStreams {

    public static <T> Publisher<T> toPublisher(Observable<T> observable) {
        return new ObservablePublisher<T>(observable);
    }

    public static <T> Observable<T> toObservable(final Publisher<T> publisher) {
        return Observable.create(new PublisherObservableOnSubscribe<T>(publisher));
    }

    // Make subscriber contravariant after https://github.com/reactive-streams/reactive-streams/issues/104 is fixed
    public static <T> void subscribe(Observable<T> observable, Subscriber<T> subscriber) {
        observable.subscribe(new ObservableSubscriber<T>(subscriber));
    }

    public static <T> void subscribe(Publisher<T> publisher, rx.Subscriber<? super T> subscriber) {
        publisher.subscribe(new PublisherSubscriber<T>(subscriber));
    }

}
