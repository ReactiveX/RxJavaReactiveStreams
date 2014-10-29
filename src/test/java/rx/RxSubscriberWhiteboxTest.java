package rx;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.reactivestreams.tck.SubscriberWhiteboxVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;
import rx.internal.PublisherSubscriber;
import rx.observers.Subscribers;
import rx.test.IterableDecrementer;

@Test
public class RxSubscriberWhiteboxTest extends SubscriberWhiteboxVerification<Long> {

    public static final long DEFAULT_TIMEOUT_MILLIS = 300L;

    protected RxSubscriberWhiteboxTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS));
    }

    @Override
    public Subscriber<Long> createSubscriber(final WhiteboxSubscriberProbe<Long> probe) {
        return new PublisherSubscriber<Long>(Subscribers.empty()) {
            @Override
            public void onSubscribe(final Subscription rsSubscription) {
                probe.registerOnSubscribe(new SubscriberPuppet() {
                    @Override
                    public void triggerRequest(long elements) {
                        rsSubscription.request(elements);
                    }

                    @Override
                    public void signalCancel() {
                        rsSubscription.cancel();
                    }
                });
            }

            @Override
            public void onNext(Long t) {
                probe.registerOnNext(t);
            }

            @Override
            public void onError(Throwable t) {
                probe.registerOnError(t);
            }

            @Override
            public void onComplete() {
                probe.registerOnComplete();
            }
        };
    }

    @Override
    public Publisher<Long> createHelperPublisher(long elements) {
        return RxReactiveStreams.toPublisher(Observable.from(new IterableDecrementer(elements)));
    }

}
