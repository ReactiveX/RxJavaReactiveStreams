package rx.reactivestreams.example.ratpack;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import ratpack.http.ResponseChunks;
import ratpack.rx.RxRatpack;
import ratpack.stream.Streams;
import ratpack.test.embed.EmbeddedApp;
import rx.Observable;
import rx.RxReactiveStreams;

import java.util.Arrays;
import java.util.List;

public class RatpackExamples {

    private Iterable<Integer> createIntRange(int upper) {
        return ContiguousSet.create(Range.closedOpen(0, upper), DiscreteDomain.integers());
    }

    /**
     * Tests rendering an observable as a chunked response.
     * <p/>
     * Data flow:
     * <p/>
     * 1. Observable of ints
     * 2. Convert to Publisher
     * 3. Use Ratpack Publisher transform
     * 4. Convert to Observable
     * 5. Use Observable transform
     * 6. Convert to Publisher
     * 7. Render Publisher
     * <p/>
     * Back pressure is being applied but it's hard to test for, as it's being applied by the HTTP client ultimately.
     */
    @Test
    public void testChunkStreaming() {
        Iterable<Integer> ints = createIntRange(100);
        EmbeddedApp.fromHandler(ctx -> {
            // Create a  publisher
            Observable<Integer> observable = Observable.from(ints);
            Publisher<Integer> publisher = RxReactiveStreams.toPublisher(observable);

            // Use one of Ratpack's transforms to convert them into strings
            Publisher<String> strings = Streams.map(publisher, Object::toString);

            // Convert back to an Rx Observable to do further transforms
            Observable<String> lines = RxReactiveStreams.toObservable(strings).map(s -> s + "\n");

            // Now render the observable by going back to a publisher
            Publisher<String> linesPublisher = RxReactiveStreams.toPublisher(lines);

            ctx.render(ResponseChunks.stringChunks(linesPublisher));
        }).test(httpClient -> {
            String text = httpClient.getText().trim();
            List<String> strings = Arrays.asList(text.split("\n"));

            List<Integer> expectedInts = Lists.newArrayList(ints);
            List<Integer> receivedInts = Lists.transform(strings, Integer::new);

            Assert.assertEquals(expectedInts, receivedInts);
        });
    }

}
