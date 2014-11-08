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

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import rx.Observable;
import rx.functions.Action0;

import java.util.concurrent.atomic.AtomicBoolean;

public class RsSubscriberToRxSubscriberAdapter<T> extends rx.Subscriber<T> {

    private final Subscriber<? super T> rsSubscriber;
    private final AtomicBoolean done = new AtomicBoolean();
    private final Action0 onDone;

    public static <T> void adapt(Observable<T> observable, Subscriber<? super T> subscriber, Action0 onDone) {
        observable.serialize().subscribe(new RsSubscriberToRxSubscriberAdapter<T>(subscriber, onDone));
    }

    private RsSubscriberToRxSubscriberAdapter(Subscriber<? super T> rsSubscriber, Action0 onDone) {
        this.rsSubscriber = rsSubscriber;
        this.onDone = onDone;
    }

    @Override
    public void onStart() {
        final AtomicBoolean requested = new AtomicBoolean();
        rsSubscriber.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                if (n < 1) {
                    throw new IllegalArgumentException("3.9 While the Subscription is not cancelled, Subscription.request(long n) MUST throw a java.lang.IllegalArgumentException if the argument is <= 0.");
                }

                requested.set(true);
                RsSubscriberToRxSubscriberAdapter.this.request(n);
            }

            @Override
            public void cancel() {
                unsubscribe();
                fireDone();
            }
        });

        if (!requested.get()) {
            request(0);
        }
    }

    private void fireDone() {
        if (done.compareAndSet(false, true)) {
            onDone.call();
        }
    }

    @Override
    public void onCompleted() {
        rsSubscriber.onComplete();
    }

    @Override
    public void onError(Throwable e) {
        rsSubscriber.onError(e);
        fireDone();
    }

    @Override
    public void onNext(T t) {
        rsSubscriber.onNext(t);
        fireDone();
    }
}
