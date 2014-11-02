/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rx.reactivestreams.example.ratpack;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Fast and loose websocket client for testing purposes.
 */
public class RecordingWebSocketClient extends WebSocketClient {

    final BlockingQueue<String> received = new LinkedBlockingQueue<>();

    public Exception exception;
    public int closeCode;
    public String closeReason;
    public boolean closeRemote;
    public ServerHandshake serverHandshake;

    private final CountDownLatch closeLatch = new CountDownLatch(1);

    public RecordingWebSocketClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        serverHandshake = handshakedata;
    }

    @Override
    public void onMessage(String message) {
        try {
            received.put(message);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        this.closeCode = code;
        this.closeReason = reason;
        this.closeRemote = remote;
        closeLatch.countDown();
    }

    void waitForClose() throws InterruptedException {
        if (!closeLatch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Socket did not close in time");
        }
    }

    @Override
    public void onError(Exception ex) {
        this.exception = ex;
    }

    public String next() {
        try {
            return received.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
