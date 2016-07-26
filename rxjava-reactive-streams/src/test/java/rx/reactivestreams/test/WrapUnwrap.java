/**
 * Copyright 2016 Netflix, Inc.
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

import org.reactivestreams.Publisher;
import org.testng.annotations.Test;

import rx.*;
import rx.functions.Func1;

public class WrapUnwrap {

    @Test
    public void wrapUnwrap() {
        
        Observable<Integer> o = Observable.range(1, 350);

        Observable<Publisher<Integer>> p = Observable.just(
                RxReactiveStreams.toPublisher(o)).asObservable();

        for (int u : p.flatMap(new Func1<Publisher<Integer>, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Publisher<Integer> v) {
                return RxReactiveStreams.toObservable(v);
            }
        })
                .toBlocking()
                .toIterable()) {
            System.out.println(u);
        }
    
    }
}
