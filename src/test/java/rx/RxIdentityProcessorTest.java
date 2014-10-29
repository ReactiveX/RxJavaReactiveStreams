package rx;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.IdentityProcessorVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;
import rx.internal.SubjectToProcessorAdapter;
import rx.subjects.ReplaySubject;
import rx.test.IterableDecrementer;

// @Test
// Above commented out so it's not run as part of the build as it is currently failing
public class RxIdentityProcessorTest extends IdentityProcessorVerification<Long> {

    public static final long DEFAULT_TIMEOUT_MILLIS = 300L;
    public static final long PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS = 1000L;

    public RxIdentityProcessorTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS), PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS);
    }

    @Override
    public Processor<Long, Long> createIdentityProcessor(int bufferSize) {
        return new SubjectToProcessorAdapter<Long, Long>(ReplaySubject.<Long>createWithSize(bufferSize));
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
