package rx.reactivestreams.test;

import static rx.RxReactiveStreams.*;

import java.util.NoSuchElementException;

import org.testng.Assert;
import org.testng.annotations.Test;

import rx.*;
import rx.observers.TestSubscriber;
import rx.reactivestreams.test.PublisherAsCompletableTest.*;
import rx.subjects.PublishSubject;

public class PublisherAsSingleTest {

    @Test(expectedExceptions = { NullPointerException.class })
    public void nullCheck() {
        toPublisher((Single<Object>)null);
    }

    @Test
    public void just() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        
        toSingle(toPublisher(Observable.just(1))).subscribe(ts);

        ts.assertValue(1);
        ts.assertCompleted();
        ts.assertNoErrors();
    }

    @Test
    public void empty() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        
        toSingle(new PublisherEmpty()).subscribe(ts);

        ts.assertNoValues();
        ts.assertNotCompleted();
        ts.assertError(NoSuchElementException.class);
    }

    @Test
    public void range() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        
        toSingle(toPublisher(Observable.range(1, 2))).subscribe(ts);

        ts.assertNoValues();
        ts.assertNotCompleted();
        ts.assertError(IndexOutOfBoundsException.class);
    }

    @Test
    public void error() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        
        toSingle(new PublisherFail()).subscribe(ts);
        
        ts.assertNoValues();
        ts.assertNotCompleted();
        ts.assertError(RuntimeException.class);
        Assert.assertEquals(ts.getOnErrorEvents().get(0).getMessage(), "Forced failure");
    }
    
    @Test
    public void cancellation() {
        PublishSubject<Object> ps = PublishSubject.create();

        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        
        toSingle(toPublisher(ps)).subscribe(ts);
        
        Assert.assertTrue(ps.hasObservers());

        ts.unsubscribe();
        
        ts.assertNoValues();
        ts.assertNotCompleted();
        ts.assertNoErrors();
        
        Assert.assertFalse(ps.hasObservers());
    }
}
