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

import java.util.*;

import org.reactivestreams.Subscription;

public final class RxJavaSynchronizedProducer implements rx.Producer, rx.Subscription {
    private final Subscription subscription;
    private volatile boolean unsubscribed;
    /** Guarded by this. */
    private boolean emitting;
    /** Guarded by this. */
    private List<Long> requests;
    
    public RxJavaSynchronizedProducer(Subscription subscription) {
        if (subscription == null) {
            throw new NullPointerException("subscription");
        }
        this.subscription = subscription;
    }
    @Override
    public boolean isUnsubscribed() {
        return unsubscribed;
    }
    @Override
    public void request(long n) {
        if (n > 0 && !unsubscribed) {
            synchronized (this) {
                if (unsubscribed) {
                    return;
                }
                if (emitting) {
                    if (requests == null) {
                        requests = new ArrayList<Long>(4);
                    }
                    requests.add(n);
                    return;
                }
                emitting = true;
            }
            boolean skipFinal = false;
            try {
                subscription.request(n);
                for (;;) {
                    List<Long> list;
                    synchronized (this) {
                        list = requests;
                        requests = null;
                        if (list == null) {
                            emitting = false;
                            skipFinal = true;
                            return;
                        }
                    }
                    for (Long v : list) {
                        if (v.longValue() == 0L) {
                            unsubscribed = true;
                            subscription.cancel();
                            skipFinal = true;
                            return;
                        } else {
                            subscription.request(v);
                        }
                    }
                }
            } finally {
                if (!skipFinal) {
                    synchronized (this) {
                        emitting = false;
                    }
                }
            }
        }
    }
    @Override
    public void unsubscribe() {
        if (!unsubscribed) {
            synchronized (this) {
                if (unsubscribed) {
                    return;
                }
                if (emitting) {
                    // replace all pending requests with this single cancel indicator
                    requests = new ArrayList<Long>(4);
                    requests.add(0L);
                    return;
                }
                emitting = true;
            }
            unsubscribed = true;
            subscription.cancel();
            // no need to leave emitting as this is a terminal state
        }
    }
}
