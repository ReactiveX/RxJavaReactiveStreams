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
package rx.internal;

import org.reactivestreams.Publisher;
import rx.Observable;
import rx.RxReactiveStreams;

/**
 * The {@link PublisherObservableOnSubscribe} wraps a Reactive Streams {@link Publisher} and allows to subscribe with
 * a Rx {@link rx.Subscriber}.
 */
public class PublisherObservableOnSubscribe<T> implements Observable.OnSubscribe<T> {

    /**
     * The wrapped {@link Publisher}.
     */
    private final Publisher<T> publisher;

    /**
     * Creates a new {@link PublisherObservableOnSubscribe}.
     *
     * @param publisher the publisher where a Rx {@link rx.Subscriber} can subscribe.
     */
    public PublisherObservableOnSubscribe(final Publisher<T> publisher) {
        this.publisher = publisher;
    }

    @Override
    public void call(final rx.Subscriber<? super T> rxSubscriber) {
        RxReactiveStreams.subscribe(publisher, rxSubscriber);
    }

}
