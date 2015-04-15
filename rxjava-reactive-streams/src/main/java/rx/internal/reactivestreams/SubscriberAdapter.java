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
package rx.internal.reactivestreams;

import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.*;

public class SubscriberAdapter<T> implements Subscriber<T> {

    private final rx.Subscriber<? super T> rxSubscriber;
    private final AtomicBoolean started = new AtomicBoolean();

    public SubscriberAdapter(rx.Subscriber<? super T> rxSubscriber) {
        this.rxSubscriber = rxSubscriber;
    }

    @Override
    public void onSubscribe(final Subscription rsSubscription) {
        if (rsSubscription == null) {
            throw new NullPointerException("onSubscribe(null)");
        }

        if (started.compareAndSet(false, true)) {
            RxJavaSynchronizedProducer sp = new RxJavaSynchronizedProducer(rsSubscription);
            rxSubscriber.add(sp);
            rxSubscriber.onStart();
            rxSubscriber.setProducer(sp);
        } else {
            rsSubscription.cancel();
        }
    }

    @Override
    public void onNext(T t) {
        if (t == null) {
            throw new NullPointerException("onNext(null)");
        }
        rxSubscriber.onNext(t);
    }

    @Override
    public void onError(Throwable t) {
        if (t == null) {
            throw new NullPointerException("onError(null)");
        }
        rxSubscriber.onError(t);
    }

    @Override
    public void onComplete() {
        rxSubscriber.onCompleted();
    }
}
