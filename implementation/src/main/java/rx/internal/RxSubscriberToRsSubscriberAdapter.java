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

import java.util.concurrent.atomic.AtomicBoolean;

public class RxSubscriberToRsSubscriberAdapter<T> implements Subscriber<T> {

    private final rx.Subscriber<? super T> rxSubscriber;
    private final AtomicBoolean started = new AtomicBoolean();

    public RxSubscriberToRsSubscriberAdapter(rx.Subscriber<? super T> rxSubscriber) {
        this.rxSubscriber = rxSubscriber;
    }

    @Override
    public void onSubscribe(final Subscription rsSubscription) {
        if (started.compareAndSet(false, true)) {
            rxSubscriber.add(Subscriptions.create(new Action0() {
                @Override
                public void call() {
                    rsSubscription.cancel();
                }
            }));
            rxSubscriber.onStart();
            rxSubscriber.setProducer(new Producer() {
                @Override
                public void request(long n) {
                    if (n > 0) {
                        rsSubscription.request(n);
                    }
                }
            });
        } else {
            rsSubscription.cancel();
        }
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
