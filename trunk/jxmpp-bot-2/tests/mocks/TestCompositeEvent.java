package mocks;

import jxmpp.com.code.google.core.events.CompositeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 28.07.11
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
public class TestCompositeEvent implements CompositeEvent
{
    public TestCompositeEvent(int value)
    {
        setValue(value);
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    private int value;
}
