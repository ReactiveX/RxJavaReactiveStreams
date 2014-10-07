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
import rx.Producer;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * The {@link PublisherSubscriber} wraps an Rx {@link rx.Subscriber} and forwards it all events from the corresponding
 * {@link Subscriber}.
 *
 * Note that it also supports backpressure and subscription management.
 */
public class PublisherSubscriber<T> implements Subscriber<T> {

    /**
     * The wrapped Rx {@link rx.Subscriber}.
     */
    private final rx.Subscriber<? super T> rxSubscriber;

    /**
     * Creates a new {@link PublisherSubscriber}.
     *
     * @param rxSubscriber the Rx {@link rx.Subscriber} that gets wrapped and notified.
     */
    public PublisherSubscriber(rx.Subscriber<? super T> rxSubscriber) {
        this.rxSubscriber = rxSubscriber;
    }

    @Override
    public void onSubscribe(final Subscription rsSubscription) {
        rxSubscriber.setProducer(new Producer() {
            @Override
            public void request(long n) {
                rsSubscription.request(n);
            }
        });

        rxSubscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                rsSubscription.cancel();
            }
        }));
    }

    @Override
    public void onNext(T t) {
        rxSubscriber.onNext(t);
    }

    @Override
    public void onError(Throwable t) {
        rxSubscriber.onError(t);
    }

    @Override
    public void onComplete() {
        rxSubscriber.onCompleted();
    }
}
