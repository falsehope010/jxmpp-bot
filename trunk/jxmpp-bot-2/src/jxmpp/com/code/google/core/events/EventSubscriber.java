package jxmpp.com.code.google.core.events;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 27.07.11
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
public interface EventSubscriber
{
    void processEvent(CompositeEvent event);
}
