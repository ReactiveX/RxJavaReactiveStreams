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

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import rx.Observable;
import rx.RxReactiveStreams;

public class ObservableToPublisherAdapter<T> implements Publisher<T> {

    private final Observable<T> observable;

    public ObservableToPublisherAdapter(final Observable<T> observable) {
        this.observable = observable;
    }

    @Override
    public void subscribe(final Subscriber<? super T> s) {
        RxReactiveStreams.subscribe(observable, s);
    }

}
