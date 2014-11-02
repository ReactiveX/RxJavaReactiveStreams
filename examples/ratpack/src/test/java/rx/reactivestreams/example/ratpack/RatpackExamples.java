package rx.reactivestreams.example.ratpack;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import ratpack.http.ResponseChunks;
import ratpack.http.client.ReceivedResponse;
import ratpack.sse.ServerSentEvents;
import ratpack.stream.Streams;
import ratpack.test.embed.EmbeddedApp;
import ratpack.test.http.TestHttpClient;
import ratpack.websocket.WebSockets;
import rx.Observable;
import rx.RxReactiveStreams;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

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

    /**
     * Test streaming Server Sent Events
     */
    @Test
    public void testServerSentEvents() {
        Iterable<Integer> ints = createIntRange(5);

        EmbeddedApp.fromHandler(ctx -> {
            Observable<Integer> observable = Observable.from(ints);
            Publisher<Integer> publisher = RxReactiveStreams.toPublisher(observable);

            ctx.render(
                ServerSentEvents.serverSentEvents(publisher, e ->
                        e.event("counter").data("event " + e.getItem())
                )
            );
        }).test(httpClient -> {
            ReceivedResponse response = httpClient.get();
            Assert.assertEquals("text/event-stream;charset=UTF-8", response.getHeaders().get("Content-Type"));

            String expectedOutput = Arrays.asList(0, 1, 2, 3, 4)
                .stream()
                .map(i -> "event: counter\ndata: event " + i + "\n")
                .collect(joining("\n"))
                + "\n";

            Assert.assertEquals(expectedOutput, response.getBody().getText());
        });

    }

    /**
     * Test streaming to a Websocket.
     *
     * Note: Ratpack doesn't yet support consuming the incoming data as a stream.
     */
    @Test
    public void testWebsocket() {
        Iterable<Integer> ints = createIntRange(3);

        EmbeddedApp.fromHandler(ctx -> {
            Observable<String> observable = Observable.from(ints).map(Object::toString);
            WebSockets.websocketBroadcast(ctx, RxReactiveStreams.toPublisher(observable));
        }).test(httpClient -> {
            URI wsAddress = getWsAddress(httpClient);
            RecordingWebSocketClient wsClient = new RecordingWebSocketClient(wsAddress);

            try {
                Assert.assertTrue(wsClient.connectBlocking());
                Assert.assertEquals("0", wsClient.next());
                Assert.assertEquals("1", wsClient.next());
                Assert.assertEquals("2", wsClient.next());
                wsClient.waitForClose();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private URI getWsAddress(TestHttpClient httpClient) {
        URI httpAddress = httpClient.getApplicationUnderTest().getAddress();
        URI wsAddress;
        try {
            wsAddress = new URI("ws", null, httpAddress.getHost(), httpAddress.getPort(), httpAddress.getPath(), null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return wsAddress;
    }

}
