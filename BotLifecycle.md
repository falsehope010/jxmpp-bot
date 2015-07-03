# Activity diagram #

![http://jxmpp-bot.googlecode.com/svn/trunk/jxmpp-bot/wiki_images/Main%20LifeCycle%20(Activity).png](http://jxmpp-bot.googlecode.com/svn/trunk/jxmpp-bot/wiki_images/Main%20LifeCycle%20(Activity).png)

# Description #

After bot process has been launched [initialization](Initialization.md) occurs. Then using provided initialization information, bot attempts to [connect](ConnectionManager.md) to remote xmpp server and login.

Each iteration bot waits until new message will be recieved from xmpp server. If message has been arrived

