package jxmpp.com.code.google.core;

import com.google.inject.Injector;
import jxmpp.com.code.google.core.connectivity.ConnectionDirector;
import jxmpp.com.code.google.core.connectivity.RoomDirector;
import org.apache.log4j.Logger;
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
    private static final Logger log = Logger.getLogger(JxmppBot.class.getName());

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
        roomDirector.run(connectionDirector.getConnection());


        while (true)
        {
            try
            {
                Thread.sleep(100);
            } catch (InterruptedException e)
            {
                log.error(e);
            }
        }
    }

    private void init()
    {
        connectionDirector = injector.getInstance(ConnectionDirector.class);
        roomDirector = injector.getInstance(RoomDirector.class);
    }

    private ConnectionDirector connectionDirector;
    private RoomDirector roomDirector;
    private Injector injector;
}
