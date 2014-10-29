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
import org.reactivestreams.Subscriber;
import org.reactivestreams.tck.SubscriberBlackboxVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;
import rx.internal.RxSubscriberToRsSubscriberAdapter;
import rx.test.IterableDecrementer;

@Test
public class RxSubscriberBlackboxTest extends SubscriberBlackboxVerification<Long> {

    public static final long DEFAULT_TIMEOUT_MILLIS = 300L;

    protected RxSubscriberBlackboxTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS));
    }

    @Override
    public Subscriber<Long> createSubscriber() {
        return new RxSubscriberToRsSubscriberAdapter<Long>(new rx.Subscriber<Long>() {

            @Override
            public void onStart() {
                super.request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                request(1);
            }
        });
    }

    @Override
    public Publisher<Long> createHelperPublisher(long elements) {
        return RxReactiveStreams.toPublisher(Observable.from(new IterableDecrementer(elements)));
    }



}
