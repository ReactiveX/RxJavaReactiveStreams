package rx.reactivestreams.test;

import static rx.RxReactiveStreams.*;

import org.reactivestreams.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

public class PublisherAsCompletableTest {

    @Test(expectedExceptions = { NullPointerException.class })
    public void nullCheck() {
        toCompletable(null);
    }
    
    @Test
    public void empty() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        
        toCompletable(new PublisherEmpty()).subscribe(ts);

        ts.assertNoValues();
        ts.assertCompleted();
        ts.assertNoErrors();
    }

    @Test
    public void error() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        
        toCompletable(new PublisherFail()).subscribe(ts);
        
        ts.assertNoValues();
        ts.assertNotCompleted();
        ts.assertError(RuntimeException.class);
        Assert.assertEquals(ts.getOnErrorEvents().get(0).getMessage(), "Forced failure");
    }
    
    @Test
    public void cancellation() {
        PublishSubject<Object> ps = PublishSubject.create();

        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        
        toCompletable(toPublisher(ps)).subscribe(ts);
        
        Assert.assertTrue(ps.hasObservers());

        ts.unsubscribe();
        
        ts.assertNoValues();
        ts.assertNotCompleted();
        ts.assertNoErrors();
        
        Assert.assertFalse(ps.hasObservers());
    }

    static final class PublisherEmpty implements Publisher<Object> {
        @Override
        public void subscribe(Subscriber<? super Object> s) {
            final boolean[] cancelled = { false };
            s.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    
                }
                
                @Override
                public void cancel() {
                    cancelled[0] = true;
                }
            });
            if (!cancelled[0]) {
                s.onComplete();
            }
        }
    }

    static final class PublisherFail implements Publisher<Object> {
        @Override
        public void subscribe(Subscriber<? super Object> s) {
            final boolean[] cancelled = { false };
            s.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    
                }
                
                @Override
                public void cancel() {
                    cancelled[0] = true;
                }
            });
            if (!cancelled[0]) {
                s.onError(new RuntimeException("Forced failure"));
            }
        }
    }

}
