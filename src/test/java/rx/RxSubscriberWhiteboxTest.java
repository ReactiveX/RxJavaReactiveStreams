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
public class RxSubscriberWhiteboxTest extends SubscriberWhiteboxVerification<Integer> {

    public static final long DEFAULT_TIMEOUT_MILLIS = 300L;

    protected RxSubscriberWhiteboxTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS));
    }

    @Override
    public Subscriber<Integer> createSubscriber(final WhiteboxSubscriberProbe<Integer> probe) {
        return new PublisherSubscriber<Integer>(Subscribers.empty()) {
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
            public void onNext(Integer t) {
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
    public Publisher<Integer> createHelperPublisher(long elements) {
        return RxReactiveStreams.toPublisher(Observable.from(new IterableDecrementer(elements)));
    }

}
