package rx;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.tck.SubscriberBlackboxVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;

import rx.internal.PublisherSubscriber;
import rx.observers.Subscribers;
import rx.test.IterableDecrementer;

@Test
public class RxSubscriberBlackboxTest extends SubscriberBlackboxVerification<Integer> {

    public static final long DEFAULT_TIMEOUT_MILLIS = 300L;

    protected RxSubscriberBlackboxTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS));
    }

    @Override
    public Subscriber<Integer> createSubscriber() {
        return new PublisherSubscriber<Integer>(Subscribers.empty());
    }

    @Override
    public Publisher<Integer> createHelperPublisher(long elements) {
        return RxReactiveStreams.<Integer>toPublisher(Observable.from(new IterableDecrementer(elements)));
    }

}
