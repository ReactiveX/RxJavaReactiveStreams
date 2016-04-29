package rx.reactivestreams.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import rx.*;
import rx.subjects.PublishSubject;

import static rx.RxReactiveStreams.*;

public class SingleAsPublisherTest {

    @Test(expectedExceptions = { NullPointerException.class })
    public void nullCheck() {
        toPublisher((Single<Object>)null);
    }
    
    @Test
    public void just() {
        RsSubscriber<Object> ts = new RsSubscriber<Object>();
        
        toPublisher(Single.just(1)).subscribe(ts);

        Assert.assertFalse(ts.complete);
        Assert.assertNull(ts.error);
        Assert.assertTrue(ts.received.isEmpty());

        ts.subscription.request(1);
        
        Assert.assertTrue(ts.complete);
        Assert.assertNull(ts.error);
        Assert.assertEquals(ts.received.size(), 1);
        Assert.assertEquals(ts.received.poll(), 1);
    }

    @Test
    public void justNull() {
        RsSubscriber<Object> ts = new RsSubscriber<Object>();
        
        toPublisher(Single.just(null)).subscribe(ts);
        
        Assert.assertFalse(ts.complete);
        Assert.assertNotNull(ts.error);
        Assert.assertTrue(ts.error instanceof NullPointerException);
        Assert.assertTrue(ts.received.isEmpty());
    }

    @Test
    public void error() {
        RsSubscriber<Object> ts = new RsSubscriber<Object>();
        
        toPublisher(Single.error(new RuntimeException("Forced failure"))).subscribe(ts);
        
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
        
        toPublisher(ps.toSingle()).subscribe(ts);

        Assert.assertTrue(ps.hasObservers());

        ts.subscription.cancel();
        
        Assert.assertFalse(ts.complete);
        Assert.assertNull(ts.error);
        Assert.assertTrue(ts.received.isEmpty());
        
        Assert.assertFalse(ps.hasObservers());
    }
}
