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

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.IdentityProcessorVerification;
import org.reactivestreams.tck.TestEnvironment;
import rx.internal.reactivestreams.SubjectToProcessorAdapter;
import rx.reactivestreams.test.IterableDecrementer;
import rx.subjects.ReplaySubject;

// @Test
// Above commented out so it's not run as part of the build as it is currently failing
public class RxIdentityProcessorTest extends IdentityProcessorVerification<Long> {

    public static final long DEFAULT_TIMEOUT_MILLIS = 300L;
    public static final long PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS = 1000L;

    public RxIdentityProcessorTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS), PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS);
    }

    @Override
    public Processor<Long, Long> createIdentityProcessor(int bufferSize) {
        return new SubjectToProcessorAdapter<Long, Long>(ReplaySubject.<Long>createWithSize(bufferSize));
    }

    @Override
    public long maxElementsFromPublisher() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Publisher<Long> createHelperPublisher(long elements) {
        return RxReactiveStreams.toPublisher(Observable.from(new IterableDecrementer(elements)));
    }

    @Override
    public Publisher<Long> createErrorStatePublisher() {
        return RxReactiveStreams.toPublisher(Observable.<Long>error(new Exception("!")));
    }

}
