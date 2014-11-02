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

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.functions.Action0;
import rx.subjects.Subject;

import java.util.concurrent.atomic.AtomicInteger;

public class SubjectToProcessorAdapter<T, R> implements Processor<T, R> {

    private final Subject<T, R> subject;
    private final AtomicInteger subscribers = new AtomicInteger(-1);
    private Subscription subscription;

    public SubjectToProcessorAdapter(final Subject<T, R> subject) {
        this.subject = subject;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        if (subscribers.get() != 0) {
            s.request(Long.MAX_VALUE);
        }
    }

    @Override
    public void onNext(T t) {
        subject.onNext(t);
    }

    @Override
    public void onError(Throwable t) {
        subject.onError(t);
    }

    @Override
    public void onComplete() {
        subject.onCompleted();
    }

    @Override
    public void subscribe(Subscriber<? super R> s) {
        Observable<R> observable = subject
                .onBackpressureBuffer()
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (!subscribers.compareAndSet(-1, 1)) {
                            subscribers.incrementAndGet();
                        }
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (subscribers.decrementAndGet() == 0) {
                            subscription.cancel();
                        }
                    }
                });

        RxReactiveStreams.subscribe(observable, s);
    }

}
