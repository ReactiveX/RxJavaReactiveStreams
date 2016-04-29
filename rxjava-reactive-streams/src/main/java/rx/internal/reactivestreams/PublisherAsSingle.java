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

import java.util.NoSuchElementException;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import rx.*;

/**
 * Wraps a Publisher and exposes it as a Single, signalling NoSuchElementException
 * if the Publisher is empty or IndexOutOfBoundsExcepion if the Publisher produces
 * more than one element.
 * 
 * @param <T> the value type
 */
public final class PublisherAsSingle<T> implements Single.OnSubscribe<T> {
    
    final Publisher<T> publisher;

    public PublisherAsSingle(Publisher<T> publisher) {
        this.publisher = publisher;
    }
    
    @Override
    public void call(SingleSubscriber<? super T> t) {
        publisher.subscribe(new PublisherAsSingleSubscriber<T>(t));
    }
    
    static final class PublisherAsSingleSubscriber<T> implements Subscriber<T>, rx.Subscription {
        
        final SingleSubscriber<? super T> actual;
        
        Subscription s;
        
        T value;
        
        boolean hasValue;
        
        boolean done;

        public PublisherAsSingleSubscriber(SingleSubscriber<? super T> actual) {
            this.actual = actual;
        }
        
        @Override
        public void onSubscribe(Subscription s) {
            this.s = s;
            
            actual.add(this);
            
            s.request(Long.MAX_VALUE);
        }
        
        @Override
        public void onNext(T t) {
            if (done) {
                return;
            }
            if (hasValue) {
                done = true;
                s.cancel();
                actual.onError(new IndexOutOfBoundsException("The source Publisher emitted multiple values"));
            } else {
                value = t;
                hasValue = true;
            }
        }
        
        @Override
        public void onError(Throwable t) {
            if (done) {
                return;
            }
            actual.onError(t);
        }
        
        @Override
        public void onComplete() {
            if (done) {
                return;
            }
            if (hasValue) {
                T v = value;
                value = null;
                actual.onSuccess(v);
            } else {
                actual.onError(new NoSuchElementException("The source Publisher was empty"));
            }
        }
        
        @Override
        public boolean isUnsubscribed() {
            return actual.isUnsubscribed();
        }
        
        @Override
        public void unsubscribe() {
            s.cancel();
        }
    }
}
