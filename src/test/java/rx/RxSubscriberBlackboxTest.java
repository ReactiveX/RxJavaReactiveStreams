package rx;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.tck.SubscriberBlackboxVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;
import rx.internal.RxSubscriberToRsSubscriberAdapter;
import rx.observers.Subscribers;
import rx.test.IterableDecrementer;

@Test
public class RxSubscriberBlackboxTest extends SubscriberBlackboxVerification<Long> {

    public static final long DEFAULT_TIMEOUT_MILLIS = 300L;

    protected RxSubscriberBlackboxTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS));
    }

    @Override
    public Subscriber<Long> createSubscriber() {
        return new RxSubscriberToRsSubscriberAdapter<Long>(Subscribers.empty());
    }

    @Override
    public Publisher<Long> createHelperPublisher(long elements) {
        return RxReactiveStreams.toPublisher(Observable.from(new IterableDecrementer(elements)));
    }

}
