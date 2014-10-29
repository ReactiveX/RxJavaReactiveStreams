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
        return new RxSubscriberToRsSubscriberAdapter<Long>(new rx.Subscriber<Long>() {

            @Override
            public void onStart() {
                super.request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                request(1);
            }
        });
    }

    @Override
    public Publisher<Long> createHelperPublisher(long elements) {
        return RxReactiveStreams.toPublisher(Observable.from(new IterableDecrementer(elements)));
    }



}
