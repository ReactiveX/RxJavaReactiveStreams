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
import org.reactivestreams.Subscriber;
import rx.Observable;
import rx.RxReactiveStreams;

/**
 * The {@link ObservablePublisher} wraps a Rx {@link Observable} and allows to subscribe to it with a Reactive Streams
 * {@link Subscriber}.
 */
public class ObservablePublisher<T> implements Publisher<T> {

    /**
     * The wrapped Rx {@link Observable}.
     */
    private final Observable<T> observable;

    /**
     * Creates a new {@link ObservablePublisher}.
     *
     * @param observable the {@link Observable} to wrap.
     */
    public ObservablePublisher(final Observable<T> observable) {
        this.observable = observable;
    }

    @Override
    public void subscribe(final Subscriber<T> s) {
        RxReactiveStreams.subscribe(observable, s);
    }

}
