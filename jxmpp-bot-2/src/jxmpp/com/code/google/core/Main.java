package jxmpp.com.code.google.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 27.07.11
 * Time: 10:53
 * To change this template use File | Settings | File Templates.
 */
public class Main
{
    private static Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args)
    {
        PropertyConfigurator.configure("log4j.properties");

        Injector injector = Guice.createInjector(new BindingModule());
        JxmppBot bot = new JxmppBot(injector);
        try
        {
            bot.run();
        } catch (Exception ex)
        {
            log.error("Error during main lifecycle", ex);
        }
//        XmppConnectionListener listener = container.getInstance(XmppConnectionListener.class);
    }
}
