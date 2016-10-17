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

import rx.*;

/**
 * Wraps a Completable and exposes it as a Publisher.
 *
 * @param <T> the value type of the publisher
 */
public final class CompletableAsPublisher<T> implements Publisher<T> {
    
    final Completable completable;

    public CompletableAsPublisher(Completable completable) {
        this.completable = completable;
    }
    
    @Override
    public void subscribe(Subscriber<? super T> s) {
        if (s == null) {
            throw new NullPointerException();
        }
        completable.subscribe(new CompletableAsPublisherSubscriber<T>(s));
    }
    
    static final class CompletableAsPublisherSubscriber<T>
    implements CompletableSubscriber, Subscription {

        final Subscriber<? super T> actual;

        rx.Subscription d;
        
        public CompletableAsPublisherSubscriber(Subscriber<? super T> actual) {
            this.actual = actual;
        }
        
        @Override
        public void onSubscribe(rx.Subscription d) {
            this.d = d;
            actual.onSubscribe(this);
        }
        
        @Override
        public void onError(Throwable e) {
            actual.onError(e);
        }
        
        @Override
        public void onCompleted() {
            actual.onComplete();
        }
        
        @Override
        public void request(long n) {
            // No values will be emitted
        }
        
        @Override
        public void cancel() {
            d.unsubscribe();
        }
    }
}
