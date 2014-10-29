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
public class RxIdentityProcessorTest extends IdentityProcessorVerification<Long> {

    public static final long DEFAULT_TIMEOUT_MILLIS = 300L;
    public static final long PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS = 1000L;

    public RxIdentityProcessorTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS), PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS);
    }

    @Override
    public Processor<Long, Long> createIdentityProcessor(int bufferSize) {
        return new SubjectProcessor<Long, Long>(PublishSubject.<Long>create());
    }

    @Override
    public long maxElementsFromPublisher() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Publisher<Long> createHelperPublisher(long elements) {
        return RxReactiveStreams.toPublisher(Observable.from(new IterableDecrementer(elements)));
    }

    @Override
    public Publisher<Long> createErrorStatePublisher() {
        return RxReactiveStreams.toPublisher(Observable.<Long>error(new Exception("!")));
    }

}
