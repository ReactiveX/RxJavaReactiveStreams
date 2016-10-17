# RxJava Reactive Streams

<a href='https://travis-ci.org/ReactiveX/RxJavaReactiveStreams/builds'><img src='https://travis-ci.org/ReactiveX/RxJavaReactiveStreams.svg?branch=1.x'></a>
[![codecov.io](http://codecov.io/github/ReactiveX/RxJavaReactiveStreams/coverage.svg?branch=1.x)](http://codecov.io/github/ReactiveX/RxJavaReactiveStreams?branch=1.x)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.reactivex/rxjava-reactive-streams/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.reactivex/rxjava-reactive-streams)

This library provides adapters between RxJava and [Reactive Streams](http://www.reactive-streams.org).
The Reactive Streams standard defines an API and runtime semantics for reactive data streams, that different tools can implement in order to interoperate with each other.
This library allows RxJava to interoperate with other implementors of the Reactive Streams API.

It provides the following API:

```java
package rx;

import org.reactivestreams.Publisher;

public abstract class RxReactiveStreams {

    public static <T> Publisher<T> toPublisher(Observable<T> observable) { … }

    public static <T> Observable<T> toObservable(Publisher<T> publisher) { … }

    public static <T> Publisher<T> toPublisher(Single<T> observable) { … }

    public static <T> Single<T> toSingle(Publisher<T> publisher) { … }

    public static <T> Publisher<T> toPublisher(Completable observable) { … }

    public static Completable toCompletable(Publisher<?> publisher) { … }

}
```

These methods can be used to convert between the Reactive Streams `Publisher` type, and RxJava's `Observable` type.

Some [examples of this library being used for interop](https://github.com/ReactiveX/RxJavaReactiveStreams/tree/0.x/examples) are available as part of this repository.

See the [Reactive Streams](http://www.reactive-streams.org) website for links to other libraries that implement the Reactive Streams API,
and can therefore interoperate with RxJava via these methods.

Learn more about RxJava on the <a href="https://github.com/ReactiveX/RxJava/wiki">Wiki Home</a> and the <a href="http://techblog.netflix.com/2013/02/rxjava-netflix-api.html">Netflix TechBlog post</a> where RxJava was introduced.

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
