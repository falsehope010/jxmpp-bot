package jxmpp.com.code.google.core;

import com.google.inject.Injector;
import jxmpp.com.code.google.core.connectivity.ConnectionDirector;
import jxmpp.com.code.google.core.events.EventAggregator;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 28.07.11
 * Time: 12:52
 * To change this template use File | Settings | File Templates.
 */
public class JxmppBot
{
    public JxmppBot(Injector injector)
    {
        if (injector == null)
            throw new NullPointerException("Illegal null-reference injector");

        this.injector = injector;

        init();
    }

    public void run() throws XMPPException
    {
        connectionDirector.run();
    }

    private void init()
    {
        connectionDirector = injector.getInstance(ConnectionDirector.class);
    }

    private ConnectionDirector connectionDirector;
    private Injector injector;
}
