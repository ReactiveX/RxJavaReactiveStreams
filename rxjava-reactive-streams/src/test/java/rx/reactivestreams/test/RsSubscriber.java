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
package rx.reactivestreams.test;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class RsSubscriber<T> implements Subscriber<T> {

    public final Queue<T> received = new ConcurrentLinkedQueue<T>();
    public volatile Subscription subscription;
    public volatile Throwable error;
    public volatile boolean complete;

    private static class Wait {
        private final long count;
        private final CountDownLatch latch;

        public Wait(long count, CountDownLatch latch) {
            this.count = count;
            this.latch = latch;
        }
    }

    private final List<Wait> waits = new CopyOnWriteArrayList<Wait>();

    @Override
    public void onSubscribe(Subscription s) {
        if (s == null) {
            throw new NullPointerException("onSubscribe(null)");
        }

        subscription = s;
    }

    @Override
    public void onNext(T t) {
        if (t == null) {
            throw new NullPointerException("onNext(null)");
        }
        received.add(t);
        for (Wait wait : waits) {
            if (received.size() >= wait.count) {
                wait.latch.countDown();
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        if (t == null) {
            throw new NullPointerException("onError(null)");
        }
        error = t;
        unwaitAll();
    }

    @Override
    public void onComplete() {
        complete = true;
        unwaitAll();
    }

    public void waitForNumItems(long n) throws InterruptedException {
        if (complete || error != null) {
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        waits.add(new Wait(n, latch));

        if (complete || error != null) {
            unwaitAll();
        }

        if (received.size() >= n) {
            latch.countDown();
        }

        latch.await();
    }

    private void unwaitAll() {
        while (!waits.isEmpty()) {
            waits.remove(0).latch.countDown();
        }
    }
}
