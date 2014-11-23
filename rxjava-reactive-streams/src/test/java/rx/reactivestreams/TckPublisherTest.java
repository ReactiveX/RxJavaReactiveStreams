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

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.reactivestreams.test.CountdownIterable;
import rx.schedulers.Schedulers;

@Test
public class TckPublisherTest extends PublisherVerification<Long> {

    public static final long DEFAULT_TIMEOUT_MILLIS = 300L;
    public static final long PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS = 1000L;

    public TckPublisherTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS), PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS);
    }

    @Override
    public Publisher<Long> createPublisher(long elements) {
        return RxReactiveStreams.toPublisher(
            Observable.from(new CountdownIterable(elements))
                .observeOn(Schedulers.computation())
        );
    }

    @Override
    public Publisher<Long> createErrorStatePublisher() {
        // Null because we always successfully subscribe.
        // If the observable is in error state, it will subscribe and then emit the error as the first item
        // This is not an “error state” publisher as defined by RS
        return null;
    }

}
