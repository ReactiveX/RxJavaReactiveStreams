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
package rx.internal;

import org.reactivestreams.Subscriber;

public class RsSubscriberToRxSubscriberAdapter<T> extends rx.Subscriber<T> {

    private final ManagedSubscription<T> subscription;

    public RsSubscriberToRxSubscriberAdapter(Subscriber<? super T> rsSubscriber) {
        // Reactive streams contract requires not sending anything until an explicit request is made
        subscription = new ManagedSubscription<T>(rsSubscriber) {
            @Override
            protected void doRequest(long n) {
                RsSubscriberToRxSubscriberAdapter.this.request(n);
            }

            @Override
            protected void doCancel() {
                unsubscribe();
            }
        };
    }

    public void postSubscribe() {
        subscription.start();
    }

    @Override
    public void onStart() {
        request(0);
    }

    @Override
    public void onCompleted() {
        subscription.onComplete();
    }

    @Override
    public void onError(Throwable e) {
        subscription.onError(e);
    }

    @Override
    public void onNext(T t) {
        subscription.onNext(t);
    }
}
