package jxmpp.com.code.google.core.listeners;

import com.google.inject.Inject;
import jxmpp.com.code.google.core.events.EventAggregator;
import org.jivesoftware.smack.ConnectionListener;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 27.07.11
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */
public class XmppConnectionListener implements ConnectionListener
{
    @Inject
    public XmppConnectionListener(EventAggregator eventAggregator)
    {
        if (eventAggregator == null)
            throw new NullPointerException("Illegal null-reference eventAggregator");

        this.eventAggregator = eventAggregator;
    }

    public void connectionClosed()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void connectionClosedOnError(Exception e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void reconnectingIn(int i)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void reconnectionSuccessful()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void reconnectionFailed(Exception e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private EventAggregator eventAggregator;
}
