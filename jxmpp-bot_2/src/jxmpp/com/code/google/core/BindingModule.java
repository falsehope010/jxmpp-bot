package jxmpp.com.code.google.core;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import jxmpp.com.code.google.core.configuration.Configuration;
import jxmpp.com.code.google.core.configuration.TestConfiguration;
import jxmpp.com.code.google.core.events.CompositeEventAggregator;
import jxmpp.com.code.google.core.events.EventAggregator;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 28.07.11
 * Time: 12:48
 */
public class BindingModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(EventAggregator.class).to(CompositeEventAggregator.class).in(Singleton.class);
        bind(Configuration.class).to(TestConfiguration.class).in(Singleton.class);
    }
}
