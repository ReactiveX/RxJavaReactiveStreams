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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import rx.Observable;
import rx.internal.operators.BackpressureUtils;

public class PublisherAdapter<T> implements Publisher<T> {

    private final Observable<T> observable;

    private final ConcurrentMap<Subscriber<?>, Object> subscribers = new ConcurrentHashMap<Subscriber<?>, Object>();

    public PublisherAdapter(final Observable<T> observable) {
        this.observable = observable.serialize();
    }

    @Override
    public void subscribe(final Subscriber<? super T> s) {
        if (subscribers.putIfAbsent(s, s) == null) {
            observable.subscribe(new rx.Subscriber<T>() {
                private final AtomicBoolean done = new AtomicBoolean();
                private final AtomicLong childRequested = new AtomicLong();
                private void doRequest(long n) {
                    if (!done.get()) {
                        BackpressureUtils.getAndAddRequest(childRequested, n);
                        request(n);
                    }
                }

                @Override
                public void onStart() {
                    final AtomicBoolean requested = new AtomicBoolean();
                    s.onSubscribe(new Subscription() {
                        @Override
                        public void request(long n) {
                            if (n < 1) {
                                unsubscribe();
                                onError(new IllegalArgumentException("3.9 While the Subscription is not cancelled, Subscription.request(long n) MUST throw a java.lang.IllegalArgumentException if the argument is <= 0."));
                                return;
                            }

                            requested.set(true);
                            doRequest(n);
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

                private boolean fireDone() {
                    boolean first = done.compareAndSet(false, true);
                    if (first) {
                        subscribers.remove(s);
                    }
                    return first;
                }

                @Override
                public void onCompleted() {
                    if (fireDone()) {
                        s.onComplete();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if (fireDone()) {
                        s.onError(e);
                    }
                }

                @Override
                public void onNext(T t) {
                    if (!done.get()) {
                        if (childRequested.get() > 0) {
                            s.onNext(t);
                            childRequested.decrementAndGet();
                        } else {
                            try {
                                onError(new IllegalStateException("1.1 source doesn't respect backpressure"));
                            } finally {
                                unsubscribe();
                            }
                        }
                    }
                }
            });
        } else {
            s.onError(new IllegalArgumentException("1.10 Subscriber cannot subscribe more than once"));
        }
    }

}
