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

import com.google.common.util.concurrent.Monitor;
import org.reactivestreams.Publisher;
import org.testng.annotations.Test;
import rx.Observable;
import rx.Subscriber;
import rx.reactivestreams.test.IterablePublisher;
import rx.reactivestreams.test.RsSubscriber;
import rx.reactivestreams.test.RxSubscriber;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static rx.RxReactiveStreams.toObservable;
import static rx.RxReactiveStreams.toPublisher;

public class NonTckTest {

    private <T> RsSubscriber<T> subscribe(Observable<T> observable) {
        return subscribe(toPublisher(observable));
    }

    private <T> RsSubscriber<T> subscribe(Publisher<T> publisher) {
        RsSubscriber<T> subscriber = new RsSubscriber<T>();
        publisher.subscribe(subscriber);
        return subscriber;
    }

    @Test
    public void canSubscribeToObservableAsPublisher() {
        RsSubscriber<Integer> subscriber = subscribe(Observable.just(1, 2, 3));

        assertEquals("no items sent", 0, subscriber.received.size());
        subscriber.subscription.request(1);
        assertEquals("one item sent", 1, subscriber.received.size());
        subscriber.subscription.request(2);
        assertEquals("two items sent", 3, subscriber.received.size());
        assertTrue(subscriber.complete);
        assertNull(subscriber.error);
    }

    @Test
    public void canSubscribeToPublisherAsObservable() {
        Publisher<Integer> publisher = new IterablePublisher<Integer>(Arrays.asList(1, 2, 3));
        Observable<Integer> observable = toObservable(publisher);
        RxSubscriber<Integer> subscriber = new RxSubscriber<Integer>(0);
        observable.subscribe(subscriber);

        assertEquals("no items sent", 0, subscriber.received.size());
        subscriber.makeRequest(1);
        assertEquals("one item sent", 1, subscriber.received.size());
        subscriber.makeRequest(2);
        assertEquals("two items sent", 3, subscriber.received.size());
        assertTrue(subscriber.complete);
        assertNull(subscriber.error);
    }

    @Test
    public void rxSubscriberNotMakingInitialRequestConsumesPublisher() {
        Publisher<Integer> publisher = new IterablePublisher<Integer>(Arrays.asList(1, 2, 3));
        Observable<Integer> observable = toObservable(publisher);
        RxSubscriber<Integer> subscriber = new RxSubscriber<Integer>(-1); // -1 means no initial request
        observable.subscribe(subscriber);

        assertEquals("all items sent", 3, subscriber.received.size());
        assertTrue(subscriber.complete);
        assertNull(subscriber.error);
    }

    @Test
    void errorStatePublisherSendsSingleErrorPostSubscribe() {
        RsSubscriber<Integer> subscriber = subscribe(Observable.<Integer>error(new RuntimeException("!")));

        // An error state observable always allows subscription, but then immediately sends onError.
        // The spec suggests not trying to subscribe but immediately firing onError without an onSubscribe.
        // However, this isn't a spec violation.
        assertNotNull(subscriber.subscription);

        assertFalse(subscriber.complete);
        assertEquals(subscriber.error.getClass(), RuntimeException.class);
        assertEquals(subscriber.error.getMessage(), "!");
    }

    @Test
    void rxFailingOnSubscribeSendsSingleErrorPostSubscribe() {
        RsSubscriber<Integer> subscriber = subscribe(Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                throw new RuntimeException("!");
            }
        }));

        // An error state observable always allows subscription, but then immediately sends onError.
        // The spec suggests not trying to subscribe but immediately firing onError without an onSubscribe.
        // However, this isn't a spec violation.
        assertNotNull(subscriber.subscription);

        assertFalse(subscriber.complete);
        assertEquals(subscriber.error.getClass(), RuntimeException.class);
        assertEquals(subscriber.error.getMessage(), "!");
    }

    @Test(enabled = false)
    void subscribingToHotObservableWithNoBackpressureStrategy() throws InterruptedException {
        final RsSubscriber<Long> subscriber = subscribe(Observable.interval(1, TimeUnit.NANOSECONDS));
        Monitor monitor = new Monitor();
        monitor.enter();
        try {
            monitor.waitFor(new Monitor.Guard(monitor) {
                @Override
                public boolean isSatisfied() {
                    return subscriber.received.size() > 1 || subscriber.error != null;
                }
            });

            assertEquals(subscriber.received.size(), 0);
            assertNotNull(subscriber.error);
        } finally {
            monitor.leave();
        }
    }

}