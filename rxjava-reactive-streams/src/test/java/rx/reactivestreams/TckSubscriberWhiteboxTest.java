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

package rx.reactivestreams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.reactivestreams.tck.SubscriberWhiteboxVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.internal.reactivestreams.SubscriberAdapter;
import rx.reactivestreams.test.CountdownIterable;

import java.util.concurrent.atomic.AtomicBoolean;

@Test
public class TckSubscriberWhiteboxTest extends SubscriberWhiteboxVerification<Long> {

    public static final long DEFAULT_TIMEOUT_MILLIS = 300L;

    public TckSubscriberWhiteboxTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS));
    }

    private static class IgnoreSubscriber<T> extends rx.Subscriber<T> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(T t) {

        }
    }

    @Override
    public Subscriber<Long> createSubscriber(final WhiteboxSubscriberProbe<Long> probe) {
        return new SubscriberAdapter<Long>(new IgnoreSubscriber<Long>()) {
            @Override
            public void onSubscribe(final Subscription rsSubscription) {
                final AtomicBoolean cancelled = new AtomicBoolean();
                super.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        rsSubscription.request(n);
                    }

                    @Override
                    public void cancel() {
                        cancelled.set(true);
                        rsSubscription.cancel();
                    }
                });
                if (!cancelled.get()) {
                    probe.registerOnSubscribe(new SubscriberPuppet() {
                        @Override
                        public void triggerRequest(long elements) {
                            rsSubscription.request(elements);
                        }

                        @Override
                        public void signalCancel() {
                            rsSubscription.cancel();
                        }
                    });
                }
            }

            @Override
            public void onNext(Long aLong) {
                probe.registerOnNext(aLong);
                super.onNext(aLong);
            }

            @Override
            public void onError(Throwable t) {
                probe.registerOnError(t);
                super.onError(t);
            }

            @Override
            public void onComplete() {
                probe.registerOnComplete();
                super.onComplete();
            }
        };
    }

    @Override
    public Long createElement(int element) {
        return Long.valueOf(Integer.toString(element));
    }

    @Override
    public Publisher<Long> createHelperPublisher(long elements) {
        return RxReactiveStreams.toPublisher(Observable.from(new CountdownIterable(elements)));
    }

}
