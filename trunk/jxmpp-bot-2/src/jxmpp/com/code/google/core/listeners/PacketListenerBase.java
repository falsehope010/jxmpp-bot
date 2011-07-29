package jxmpp.com.code.google.core.listeners;

import jxmpp.com.code.google.core.processors.CommonProcessor;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 29.07.11
 * Time: 17:07
 */
public class PacketListenerBase
{
    public PacketListenerBase(CommonProcessor processor)
    {
        this.processor = processor;
    }

    public CommonProcessor getProcessor()
    {
        return processor;
    }

    private CommonProcessor processor;
}
