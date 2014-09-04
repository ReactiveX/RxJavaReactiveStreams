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

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import rx.Observable;

/**
 * The {@link ObservableSubscriber} wraps a Reactive Streams {@link Subscriber} and allows to subscribe to a Rx
 * {@link Observable}.
 *
 * This implementation properly forwards all emitted events and also handles subscriptions.
 */
public class ObservableSubscriber<T> extends rx.Subscriber<T> {

    /**
     * The wrapped Reactive Streams {@link Subscriber}.
     */
    private final Subscriber<? super T> rsSubscriber;

    /**
     * Creates a new {@link ObservableSubscriber}.
     *
     * Note that because the Reactive Streams contract requires not sending anything until an explicit request is made,
     * the {@link #request(long)} call is issued immediately.
     *
     * @param rsSubscriber the subscriber to wrap.
     */
    public ObservableSubscriber(final Subscriber<? super T> rsSubscriber) {
        this.rsSubscriber = rsSubscriber;

        // Reactive streams contract requires not sending anything until an explicit request is made
        request(0);

        rsSubscriber.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                ObservableSubscriber.this.request(n);
            }

            @Override
            public void cancel() {
                unsubscribe();
            }
        });
    }

    @Override
    public void onCompleted() {
        rsSubscriber.onComplete();
    }

    @Override
    public void onError(Throwable e) {
        rsSubscriber.onError(e);
    }

    @Override
    public void onNext(T t) {
        rsSubscriber.onNext(t);
    }
}
