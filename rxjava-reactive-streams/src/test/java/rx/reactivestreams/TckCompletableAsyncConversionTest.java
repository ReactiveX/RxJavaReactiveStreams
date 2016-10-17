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

import java.io.IOException;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.*;
import org.testng.annotations.Test;

import rx.*;
import rx.schedulers.Schedulers;

@Test
public class TckCompletableAsyncConversionTest extends PublisherVerification<Long> {

    public TckCompletableAsyncConversionTest() {
        super(new TestEnvironment(300L));
    }

    @Override
    public Publisher<Long> createPublisher(long elements) {
        return RxReactiveStreams.toPublisher(Completable.complete().observeOn(Schedulers.computation()));
    }

    @Override
    public long maxElementsFromPublisher() {
        return 0L;
    }
    
    @Override
    public Publisher<Long> createFailedPublisher() {
        return RxReactiveStreams.toPublisher(Completable.error(new IOException()).observeOn(Schedulers.computation()));
    }

}
