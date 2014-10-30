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
package rx;

import org.reactivestreams.Publisher;
import org.testng.annotations.Test;
import rx.reactivestreams.test.CollectingRsSubscriber;
import rx.reactivestreams.test.CollectingRxSubscriber;
import rx.reactivestreams.test.IterablePublisher;

import java.util.Arrays;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class RxReactiveStreamsTest {

    @Test
    public void canSubscribeToObservableAsPublisher() {
        Observable<Integer> observable = Observable.just(1, 2, 3);
        Publisher<Integer> publisher = RxReactiveStreams.toPublisher(observable);
        CollectingRsSubscriber<Integer> subscriber = new CollectingRsSubscriber<Integer>();
        publisher.subscribe(subscriber);

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
        Observable<Integer> observable = RxReactiveStreams.toObservable(publisher);
        CollectingRxSubscriber<Integer> subscriber = new CollectingRxSubscriber<Integer>(0);
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
    public void rxSubscriberNotMakingInitialRequestConumesPublisher() {
        Publisher<Integer> publisher = new IterablePublisher<Integer>(Arrays.asList(1, 2, 3));
        Observable<Integer> observable = RxReactiveStreams.toObservable(publisher);
        CollectingRxSubscriber<Integer> subscriber = new CollectingRxSubscriber<Integer>(-1); // -1 means no initial request
        observable.subscribe(subscriber);

        assertEquals("all items sent", 3, subscriber.received.size());
        assertTrue(subscriber.complete);
        assertNull(subscriber.error);
    }

}