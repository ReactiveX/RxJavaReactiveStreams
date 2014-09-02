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
import org.reactivestreams.Subscription;

public class ObservableSubscriber<T> extends rx.Subscriber<T> {
    private final Subscriber<T> rsSubscriber;

    public ObservableSubscriber(Subscriber<T> rsSubscriber) {
        this.rsSubscriber = rsSubscriber;

        // Reactive streams contract requires not sending anything until an explict request is made
        request(0);

        rsSubscriber.onSubscribe(new Subscription() {
            @Override
            public void request(int n) {
                ObservableSubscriber.this.request(n);
            }

            @Override
            public void cancel() {
                unsubscribe();
            }
        });
    }

    @Override
    public void onCompleted() {
        rsSubscriber.onComplete();
    }

    @Override
    public void onError(Throwable e) {
        rsSubscriber.onError(e);
    }

    @Override
    public void onNext(T t) {
        rsSubscriber.onNext(t);
    }
}
