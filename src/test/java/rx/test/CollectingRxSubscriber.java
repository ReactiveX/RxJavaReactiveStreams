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
package rx.test;

import rx.Subscriber;

import java.util.LinkedList;
import java.util.List;

public class CollectingRxSubscriber<T> extends Subscriber<T> {

    public final List<T> received = new LinkedList<T>();
    private final long initialRequest;
    public Throwable error;
    public boolean complete;

    public CollectingRxSubscriber(long initialRequest) {
        this.initialRequest = initialRequest;
    }

    @Override
    public void onStart() {
        if (initialRequest >= 0) {
            request(initialRequest);
        }
    }

    @Override
    public void onCompleted() {
        complete = true;
    }

    @Override
    public void onError(Throwable e) {
        error = e;
    }

    @Override
    public void onNext(T t) {
        received.add(t);
    }

    public void makeRequest(long n) {
        request(n);
    }

}
