# RxJava Reactive Streams

This library provides adapters between RxJava and [Reactive Streams](http://www.reactive-streams.org).
The Reactive Streams standard defines an API and runtime semantics for reactive data streams, that other tools can implement in order to interoperate with other tools.
This library allows RxJava to interoperate with other implementors of the Reactive Streams API.

It provides one class, `rx.RxReactiveStreams`:

```language-java
package rx;

import org.reactivestreams.Publisher;
import rx.internal.reactivestreams.PublisherAdapter;
import rx.internal.reactivestreams.SubscriberAdapter;

/**
 * This type provides static factory methods for converting to and from RxJava types and Reactive Streams types.
 * <p/>
 * The <a href="http://www.reactive-streams.org">Reactive Streams</a> API provides a common API for interoperability
 * between different reactive streaming libraries, of which RxJava is one.
 * Using the methods of this class, RxJava can collaborate with other such libraries that also implement the standard.
 */
public abstract class RxReactiveStreams {

    /**
     * Convert a Rx {@link Observable} into a Reactive Streams {@link Publisher}.
     * <p/>
     * Use this method when you have an RxJava observable, that you want to be consumed by another library.
     *
     * @param observable the {@link Observable} to convert
     * @return the converted {@link Publisher}
     */
    public static <T> Publisher<T> toPublisher(Observable<T> observable) {
        // (implementation omitted)
    }

    /**
     * Convert a Reactive Streams {@link Publisher} into a Rx {@link Observable}.
     * <p/>
     * Use this method when you have a stream from another library, that you want to be consume as an RxJava observable.
     *
     * @param publisher the {@link Publisher} to convert.
     * @return the converted {@link Observable}
     */
    public static <T> Observable<T> toObservable(final Publisher<T> publisher) {
      // (implementation omitted)
    }

}
```

These methods can be used to convert between the Reactive Streams `Publisher` type, and RxJava's `Observable` type.

Some [examples of this library being used for interop](https://github.com/ReactiveX/RxJavaReactiveStreams/tree/0.x/examples) are available as part of this repository.

See the [Reactive Streams](http://www.reactive-streams.org) website for links to other libraries that implement the Reactive Streams API,
and can therefore interoperate with RxJava via these methods.

Learn more about RxJava on the <a href="https://github.com/ReactiveX/RxJava/wiki">Wiki Home</a> and the <a href="http://techblog.netflix.com/2013/02/rxjava-netflix-api.html">Netflix TechBlog post</a> where RxJava was introduced.

## Master Build Status

<a href='https://travis-ci.org/ReactiveX/RxJavaReactiveStreams/builds'><img src='https://travis-ci.org/ReactiveX/RxJavaReactiveStreams.svg?branch=0.x'></a>

## Communication

- Google Group: [RxJava](http://groups.google.com/d/forum/rxjava)
- Twitter: [@RxJava](http://twitter.com/RxJava)
- [GitHub Issues](https://github.com/ReactiveX/RxJava/issues)

## Binaries

Binaries and dependency information for Maven, Ivy, Gradle and others can be found at [http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cio.reactivex.rxjava-reactive-streams).

Example for Maven:

```xml
<dependency>
    <groupId>io.reactivex</groupId>
    <artifactId>rxjava-reactive-streams</artifactId>
    <version>x.y.z</version>
</dependency>
```
and for Ivy:

```xml
<dependency org="io.reactivex" name="rxjava-reactive-streams" rev="x.y.z" />
```

## Build

To build:

```
$ git clone git@github.com:ReactiveX/RxJavaReactiveStreams.git
$ cd RxJavaReactiveStreams/
$ ./gradlew build
```

## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/ReactiveX/RxJavaReactiveStreams/issues).

## LICENSE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
