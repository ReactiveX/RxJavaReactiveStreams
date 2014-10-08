package rx;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.IdentityProcessorVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;

import rx.internal.SubjectProcessor;
import rx.subjects.PublishSubject;
import rx.test.IterableDecrementer;

@Test
public class RxIdentityProcessorTest extends IdentityProcessorVerification<Integer> {

    public static final long DEFAULT_TIMEOUT_MILLIS = 300L;
    public static final long PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS = 1000L;

    public RxIdentityProcessorTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS), PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS);
    }

    @Override
    public Processor<Integer, Integer> createIdentityProcessor(int bufferSize) {
        return new SubjectProcessor<Integer, Integer>(PublishSubject.<Integer> create());
    }

    @Override
    public long maxElementsFromPublisher() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Publisher<Integer> createHelperPublisher(long elements) {
        return RxReactiveStreams.<Integer>toPublisher(Observable.from(new IterableDecrementer(elements)));
    }

    @Override
    public Publisher<Integer> createErrorStatePublisher() {
        return RxReactiveStreams.<Integer>toPublisher(Observable.<Integer> error(new Exception("!")));
    }

}
