package rx.reactivestreams.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import rx.*;
import rx.subjects.PublishSubject;

import static rx.RxReactiveStreams.*;

public class CompletableAsPublisherTest {

    @Test(expectedExceptions = { NullPointerException.class })
    public void nullCheck() {
        toPublisher((Completable)null);
    }
    
    @Test
    public void empty() {
        RsSubscriber<Object> ts = new RsSubscriber<Object>();
        
        toPublisher(Completable.complete()).subscribe(ts);
        
        Assert.assertTrue(ts.complete);
        Assert.assertNull(ts.error);
        Assert.assertTrue(ts.received.isEmpty());
    }

    @Test
    public void error() {
        RsSubscriber<Object> ts = new RsSubscriber<Object>();
        
        toPublisher(Completable.error(new RuntimeException("Forced failure"))).subscribe(ts);
        
        Assert.assertFalse(ts.complete);
        Assert.assertNotNull(ts.error);
        Assert.assertTrue(ts.error instanceof RuntimeException);
        Assert.assertEquals(ts.error.getMessage(), "Forced failure");
        Assert.assertTrue(ts.received.isEmpty());
    }
    
    @Test
    public void cancellation() {
        RsSubscriber<Object> ts = new RsSubscriber<Object>();
        
        PublishSubject<Object> ps = PublishSubject.create();
        
        toPublisher(ps.toCompletable()).subscribe(ts);

        Assert.assertTrue(ps.hasObservers());

        ts.subscription.cancel();
        
        Assert.assertFalse(ts.complete);
        Assert.assertNull(ts.error);
        Assert.assertTrue(ts.received.isEmpty());
        
        Assert.assertFalse(ps.hasObservers());
    }
}
