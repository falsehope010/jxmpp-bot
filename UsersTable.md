# Use-case #

> ![http://jxmpp-bot.googlecode.com/svn/trunk/jxmpp-bot/wiki_images/UserTable%20Use-Case.png](http://jxmpp-bot.googlecode.com/svn/trunk/jxmpp-bot/wiki_images/UserTable%20Use-Case.png)


# Description #


## Permissions checking ##

When bot recieves xmpp message (e.g. message body, sender JID and some other attributes) from server first of all it checks whether it is system command. If message is system command (this is checked using system commands parser), bot checks whether command sender (remote user) have enough permissions to execute it. There is the place where UsersTable comes to scene. It provides robust access to user's access level based on user JID.

If command was sent by anonymous user, access level of sender is set to **zero**

If system commands parser doesn't recognize message as system command, bot transfers message to plugins manager. Plugins manager in it's turn transfers message to each registered plugin.

Plugin developer may want to put access level restriction on plugin execution, so plugin may need UsersTable to check whether message sender (user) has enough access rights to perform plugin action.


_TODO: Add links to PluginsManager and SystemCommandsParser_


## User management ##

Using Users table we provide almost complete implementation of several system commands, see [User management commands](UserManagementCommands.md) and [Access level management commands](PermissionCheckingCommands.md)

It also provides formatted database description of user (which in conjunction with user vCard info implements Describe user command).

Format:

> {_Field Name_}  :  {_Field Value_}  _**\n**_  {_Field Name_}  :  {_Field Value_}  _**\n**_ ...

## Caching records ##

Users table provides RAM cache of database users table in junction with additional user's information stored in database (e.g. jid collection for each user and so on). Users table should be used by plugins to get user access level and get/set additional user's information.