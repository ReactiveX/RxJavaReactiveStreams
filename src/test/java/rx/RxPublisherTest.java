package rx;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;
import rx.test.IterableDecrementer;

@Test // needed for Gradle to find this as a test
public class RxPublisherTest extends PublisherVerification<Integer> {

    public static final long DEFAULT_TIMEOUT_MILLIS = 300L;
    public static final long PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS = 1000L;

    public RxPublisherTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS), PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS);
    }

    @Override
    public long maxElementsFromPublisher() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return RxReactiveStreams.toPublisher(Observable.from(new IterableDecrementer(elements)));
    }

    @Override
    public Publisher<Integer> createErrorStatePublisher() {
        return RxReactiveStreams.toPublisher(Observable.<Integer>error(new Exception("!")));
    }

}
