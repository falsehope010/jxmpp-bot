import jxmpp.com.code.google.core.events.CompositeEventAggregator;
import jxmpp.com.code.google.core.events.EventAggregator;
import mocks.Test2CompositeEvent;
import mocks.TestCompositeEvent;
import mocks.TestEventSubscriber;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 27.07.11
 * Time: 13:09
 * To change this template use File | Settings | File Templates.
 */
public class CompositeEventAggregatorTests
{
    Mockery mockery = new Mockery();

    @Test
    public void publishEvent_AllSubscribed_Success()
    {
        EventAggregator eventAggregator = new CompositeEventAggregator();

        TestEventSubscriber subscriber1 = new TestEventSubscriber();
        TestEventSubscriber subscriber2 = new TestEventSubscriber();

        Assert.assertEquals(0, subscriber1.getReceivedValue());
        Assert.assertEquals(0, subscriber2.getReceivedValue());

        eventAggregator.subscribe(subscriber1, TestCompositeEvent.class);
        eventAggregator.subscribe(subscriber2, TestCompositeEvent.class);

        eventAggregator.publish(new TestCompositeEvent(5));

        Assert.assertEquals(5, subscriber1.getReceivedValue());
        Assert.assertEquals(5, subscriber2.getReceivedValue());
    }

    @Test
    public void publishTwoEvents_DifferentSubsribers_Success()
    {
        EventAggregator eventAggregator = new CompositeEventAggregator();

        TestEventSubscriber subscriber1 = new TestEventSubscriber();
        TestEventSubscriber subscriber2 = new TestEventSubscriber();

        Assert.assertEquals(0, subscriber1.getReceivedValue());
        Assert.assertEquals(0, subscriber2.getReceivedValue());

        eventAggregator.subscribe(subscriber1, TestCompositeEvent.class);
        eventAggregator.subscribe(subscriber2, Test2CompositeEvent.class);

        eventAggregator.publish(new TestCompositeEvent(5));

        Assert.assertEquals(5, subscriber1.getReceivedValue());
        Assert.assertEquals(0, subscriber2.getReceivedValue());

        eventAggregator.publish(new Test2CompositeEvent(3));

        Assert.assertEquals(5, subscriber1.getReceivedValue());
        Assert.assertEquals(3, subscriber2.getReceivedValue());
    }

    @Test
    public void unsubscribe_NoEventsProcessed_Success()
    {
        EventAggregator eventAggregator = new CompositeEventAggregator();

        TestEventSubscriber subscriber1 = new TestEventSubscriber();

        Assert.assertEquals(0, subscriber1.getReceivedValue());

        eventAggregator.subscribe(subscriber1, TestCompositeEvent.class);
        eventAggregator.publish(new TestCompositeEvent(5));

        Assert.assertEquals(5, subscriber1.getReceivedValue());

        eventAggregator.unsubscribe(subscriber1, TestCompositeEvent.class);
        eventAggregator.publish(new TestCompositeEvent(15));

        Assert.assertEquals(5, subscriber1.getReceivedValue());
    }
}
