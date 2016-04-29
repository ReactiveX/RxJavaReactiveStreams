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

import org.reactivestreams.*;

import rx.Completable.CompletableSubscriber;

/**
 * Wraps an arbitrary Publisher and exposes it as a Completable, ignoring any onNext events.
 */
public final class PublisherAsCompletable implements rx.Completable.CompletableOnSubscribe {

    final Publisher<?> publisher;

    public PublisherAsCompletable(Publisher<?> publisher) {
        this.publisher = publisher;
    }
    
    @Override
    public void call(CompletableSubscriber t) {
        publisher.subscribe(new PublisherAsCompletableSubscriber(t));
    }
    
    static final class PublisherAsCompletableSubscriber implements Subscriber<Object>, rx.Subscription {
        
        final CompletableSubscriber actual;

        Subscription s;
        
        volatile boolean unsubscribed;
        
        public PublisherAsCompletableSubscriber(CompletableSubscriber actual) {
            this.actual = actual;
        }
        
        @Override
        public void onSubscribe(Subscription s) {
            this.s = s;
            actual.onSubscribe(this);
            s.request(Long.MAX_VALUE);
        }
        
        @Override
        public void onNext(Object t) {
            // values are ignored
        }
        
        @Override
        public void onError(Throwable t) {
            actual.onError(t);
        }
        
        @Override
        public void onComplete() {
            actual.onCompleted();
        }
        
        @Override
        public boolean isUnsubscribed() {
            return unsubscribed;
        }
        
        @Override
        public void unsubscribe() {
            if (!unsubscribed) {
                unsubscribed = true;
                s.cancel();
            }
        }
    }
}
