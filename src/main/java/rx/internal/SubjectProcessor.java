package rx.internal;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import rx.observers.Subscribers;
import rx.subjects.Subject;

/**
 * The {@link SubjectProcessor} wraps a {@link Subject}.
 */
public class SubjectProcessor<T, R> implements Processor<T, R> {

    /**
     * The wrapped Rx {@link Subject} as a {@link Subscriber}.
     */
    private final Subscriber<T> subscriber;

    /**
     * The wrapped Rx {@link Subject} as a {@link Publisher}.
     */
    private final Publisher<R> publisher;

    /**
     * Creates a new {@link SubjectProcessor}.
     *
     * @param subject the wrapped {@link Subject}.
     */
    public SubjectProcessor(final Subject<T, R> subject) {
        this.subscriber = new PublisherSubscriber<T>(Subscribers.from(subject));
        this.publisher = new ObservablePublisher<R>(subject);
    }

    @Override
    public void onSubscribe(Subscription s) {
        subscriber.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        subscriber.onNext(t);
    }

    @Override
    public void onError(Throwable t) {
        subscriber.onError(t);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }

    @Override
    public void subscribe(Subscriber<? super R> s) {
        publisher.subscribe(s);
    }

}
