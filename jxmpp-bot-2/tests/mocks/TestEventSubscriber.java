package mocks;

import jxmpp.com.code.google.core.events.CompositeEvent;
import jxmpp.com.code.google.core.events.EventSubscriber;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 28.07.11
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public class TestEventSubscriber implements EventSubscriber
{
    @Override
    public void processEvent(CompositeEvent event)
    {
        if (event == null)
            return;

        if (event instanceof TestCompositeEvent)
        {
            TestCompositeEvent te = (TestCompositeEvent) event;
            setReceivedValue(te.getValue());
        }
        if (event instanceof Test2CompositeEvent)
        {
            Test2CompositeEvent te = (Test2CompositeEvent) event;
            setReceivedValue(te.getValue());
        }
    }

    public int getReceivedValue()
    {
        return receivedValue;
    }

    public void setReceivedValue(int receivedValue)
    {
        this.receivedValue = receivedValue;
    }

    private int receivedValue;
}
