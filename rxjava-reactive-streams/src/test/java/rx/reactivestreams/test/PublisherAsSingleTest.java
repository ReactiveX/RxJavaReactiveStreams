package rx.reactivestreams.test;

import org.testng.Assert;
import org.testng.annotations.Test;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;
import rx.reactivestreams.test.PublisherAsCompletableTest.PublisherEmpty;
import rx.reactivestreams.test.PublisherAsCompletableTest.PublisherFail;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static rx.RxReactiveStreams.toPublisher;
import static rx.RxReactiveStreams.toSingle;

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
    public void singleJust() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();

        toSingle(toPublisher(Single.just(1))).subscribeOn(Schedulers.computation()).subscribe(ts);

        ts.awaitTerminalEvent(3, TimeUnit.SECONDS);

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
