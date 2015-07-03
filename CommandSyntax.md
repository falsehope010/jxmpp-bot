# Syntax overview #

You can send commands to jxmpp-bot using four methods:

  1. Send text message using any IM application directly to bot (using bot's JID)
  1. Send private message while you are in jabber conference room (`*`)
  1. Send group-chat message while you are in jabber conference room (`*`)
  1. Send xmpp message using your own software/tool
> (`*`) Bot must be in the same room as you are

Each command has folowing syntax:

> {`*`}   {command\_name}   {param1}   {param2}   ...

If you want to invoke command using group-chat message, you must put `*` char before command name. After command name you must put command parameters (if any needed)

# Default behaviour #

By default if invalid command syntax used:

  * Bot doesn't produce error message if user sent group-char message (e.g. attempted to invoke command inside group-chat)

  * Bot produces error message and optionally help message if user sent private message or send direct xmpp message