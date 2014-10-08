package rx;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

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
        int elementsInt = (int) elements;
        List<Integer> list = new ArrayList<Integer>(elementsInt);
        for (int i = 0; i < elementsInt; i++) {
            list.add(0);
        }
        return RxReactiveStreams.toPublisher(Observable.from(list));
    }

    @Override
    public Publisher<Integer> createErrorStatePublisher() {
        return RxReactiveStreams.toPublisher(Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                throw new RuntimeException("!");
            }
        }));
    }

}
