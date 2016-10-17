/**
 * Copyright 2016 Netflix, Inc.
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

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import rx.Single;
import rx.SingleSubscriber;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wraps a Single and exposes it as a Publisher.
 *
 * @param <T> the value type
 */
public final class SingleAsPublisher<T> implements Publisher<T> {

    final Single<T> single;

    public SingleAsPublisher(Single<T> single) {
        this.single = single;
    }

    @Override
    public void subscribe(Subscriber<? super T> s) {
        SingleAsPublisherSubscriber<T> parent = new SingleAsPublisherSubscriber<T>(s);
        s.onSubscribe(parent);

        single.subscribe(parent);
    }

    static final class SingleAsPublisherSubscriber<T> extends SingleSubscriber<T>
    implements Subscription {

        final Subscriber<? super T> actual;

        final AtomicInteger state;

        T value;

        volatile boolean cancelled;

        static final int NO_REQUEST_NO_VALUE = 0;
        static final int NO_REQUEST_HAS_VALUE = 1;
        static final int HAS_REQUEST_NO_VALUE = 2;
        static final int HAS_REQUEST_HAS_VALUE = 3;

        public SingleAsPublisherSubscriber(Subscriber<? super T> actual) {
            this.actual = actual;
            this.state = new AtomicInteger();
        }

        @Override
        public void onSuccess(T value) {
            if (cancelled) {
                return;
            }
            if (value == null) {
                state.lazySet(HAS_REQUEST_HAS_VALUE);
                actual.onError(new NullPointerException("value"));
                return;
            }
            for (;;) {
                int s = state.get();

                if (s == NO_REQUEST_HAS_VALUE || s == HAS_REQUEST_HAS_VALUE || cancelled) {
                    break;
                } else
                if (s == HAS_REQUEST_NO_VALUE) {
                    actual.onNext(value);
                    if (!cancelled) {
                        actual.onComplete();
                    }
                    return;
                } else {
                    this.value = value;
                    if (state.compareAndSet(s, NO_REQUEST_HAS_VALUE)) {
                        break;
                    }
                }
            }
        }

        @Override
        public void onError(Throwable error) {
            if (cancelled) {
                return;
            }
            state.lazySet(HAS_REQUEST_HAS_VALUE);
            actual.onError(error);
        }

        @Override
        public void request(long n) {
            if (n > 0) {
                for (;;) {
                    int s = state.get();
                    if (s == HAS_REQUEST_HAS_VALUE || s == HAS_REQUEST_NO_VALUE || cancelled) {
                        break;
                    }
                    if (s == NO_REQUEST_HAS_VALUE) {
                        if (state.compareAndSet(s, HAS_REQUEST_HAS_VALUE)) {
                            T v = value;
                            value = null;

                            actual.onNext(v);
                            if (!cancelled) {
                                actual.onComplete();
                            }
                        }
                        break;
                    }
                    if (state.compareAndSet(NO_REQUEST_NO_VALUE, HAS_REQUEST_NO_VALUE)) {
                        break;
                    }
                }
            }
        }

        @Override
        public void cancel() {
            if (!cancelled) {
                cancelled = true;
                if (state.getAndSet(HAS_REQUEST_HAS_VALUE) == NO_REQUEST_HAS_VALUE) {
                    value = null;
                }
                unsubscribe();
            }
        }
    }
}
