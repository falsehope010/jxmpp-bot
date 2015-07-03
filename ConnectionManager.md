# Activity diagram #

Add your content here.


# Description #

If connection is established, bot attempts to join groupchat, otherwise error information will be written into bot application [log](SysLogger.md) and bot goes reconnecting. See [reconnect timeout option](BotConfig.md).

If bot has successfully joined groupchat, it starts its main loop.