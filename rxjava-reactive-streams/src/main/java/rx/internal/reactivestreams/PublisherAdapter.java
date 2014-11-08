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

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import rx.Observable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class PublisherAdapter<T> implements Publisher<T> {

    private final Observable<T> observable;

    private final Set<Subscriber<?>> subscribers = new HashSet<Subscriber<?>>();

    public PublisherAdapter(final Observable<T> observable) {
        this.observable = observable.serialize();
    }

    @Override
    public void subscribe(final Subscriber<? super T> s) {
        if (subscribers.add(s)) {
            observable.subscribe(new rx.Subscriber<T>() {
                private final AtomicBoolean done = new AtomicBoolean();

                private void doRequest(long n) {
                    request(n);
                }

                @Override
                public void onStart() {
                    final AtomicBoolean requested = new AtomicBoolean();
                    s.onSubscribe(new Subscription() {
                        @Override
                        public void request(long n) {
                            if (n < 1) {
                                throw new IllegalArgumentException("3.9 While the Subscription is not cancelled, Subscription.request(long n) MUST throw a java.lang.IllegalArgumentException if the argument is <= 0.");
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

                private void fireDone() {
                    if (done.compareAndSet(false, true)) {
                        subscribers.remove(s);
                    }
                }

                @Override
                public void onCompleted() {
                    s.onComplete();
                }

                @Override
                public void onError(Throwable e) {
                    s.onError(e);
                    fireDone();
                }

                @Override
                public void onNext(T t) {
                    s.onNext(t);
                    fireDone();
                }
            });
        } else {
            s.onError(new IllegalArgumentException("1.10 Subscriber cannot subscribe more than once"));
        }
    }

}
