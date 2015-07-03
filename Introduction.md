> # Introduction #

> jxmpp-bot is java-based open-source jabber bot. It can be used as jabber conference bot or standalone application for remote control over PC.

> jxmpp-bot is designed to be very flexible because of it's plugin management system. You can write your own plugins for processing messages, text filtering, task editing and executing and many other things.



> # Architecture overview #
> ![http://jxmpp-bot.googlecode.com/svn/trunk/jxmpp-bot/wiki_images/Project%20Use-Case.png](http://jxmpp-bot.googlecode.com/svn/trunk/jxmpp-bot/wiki_images/Project%20Use-Case.png)

  * **User management**

> Bot's user is a person that uses it remotely or locally. There are two types of users - _registered_ and _anonymous_. Each registered user has it's own record about it's JID (jabber user id) and access level, stored inside bot's local database. Anonymous user has no such a record in database an can use only very basic functions and commands (such as 'ping','time' and so on).
> Registered user must always provide at least one valid JID (jabber user ID) in order to start using bot and execute commands. Registered users can have multiple jids.

  * **User commands management**

> The main purpose of jxmpp-bot is to process commands from remote users. You can send commands directly to bot (using for example IM messenger) or from any other application using xmpp protocol. The bot distincts commands that are sent directly (private messages by it's JID) and group-chat commands (e.g. jabber conference room).
> Using user management system bot distinct whether command was sent by registered user (e.g. it's JID and access level is stored in local bot database) or by anonymous sender.
> NOTE: Anonymous users still can perform several actions.

  * **User permissions management and distinction**

> Each user has it's own access level. Access level is number between 0 and 65535. Top access level is 65535 and assigned only to owner. Users with access level higher than 30000 can change other users' access levels but not more than their own.

> All information about access levels for registered users is stored inside bot's local database. Owner is special user. It's JID is stored directly in bot's configuration file, so there are no ways to changed owner remotely. There can be only one bot's owner with unlimited permissions, but you can always change owner by editing local bot's configuration.

> Users with access level greater than 40000 can register other anonymous users and change their access level. Those "admins" can also revoke registration from registered users by deleting their record from database. Those operations are system one and are designed to be performed remotely (using system commands)

  * **Remote management and administration**

> There is a wide list of management and administrative operations and commands. Almost all of them can be performed remitely by users with access level greater than 50000. Only top critical operations are allowed to be performed only by owner.

> [See list of all system commands](SystemCommands.md)

  * **Plugin management**

> jxmpp-bot uses extensible architecture. All extensions are plugins which are loaded by bot on it's startup. If you need you own function - make plugin which performs it.

> [See current list of all plugins](PluginsList.md)


  * **Local configuration management**

> All top critical configuration parameters are stored inside local configurations file. For example there are stored bot JID, password, conference room name etc. Configuration file also includes owner's JID and additional owner's information. Configuration parameters are loaded during bot initialization and can't be changed during runtime. If you change bot local configuration (which can be done only by editing config file locally) you need to restart bot to affect changes


  * **Data exchange with xmpp server**

> During bot's life-cycle it recieves data from xmpp (jabber) server, processes this data and send response back to server. Response is generated only if recieved data flow contains command or some plugin has performed some operation over this data and created output.


  * **Local data storage**

> Local data storage is used to "store" persistent data which should be saved between bot's restarts/shutdowns (for example registered users info is stored inside local data storage).

> Current implementation is based on SQLite database.