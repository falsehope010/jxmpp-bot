# Overview #

System logger performs logging of system events such as various errors, connection attempts and failures etc. It has nothing with group-chat / private-chat logging, though it may be used for logging system xmpp messages (e.g. error auth messages).

System log can be configured through [system configuration](BotConfig.md)

System logs are stored inside local database (SQLite). Log records life time depends on several options:

  * **Store system log for specified number of sessions.**

> When bot is launched, we assume that new session is started. User can specify number of sessions to be logged in system configuration.

  * **Store system log records for specified time period**

> If bot is running permanently, user can specify interval of storing system logs. If log record will be older then logging interval it will be cleaned up automatically.

  * **Store only specified ammount of system log records**

> Only latest records will be present in system log. Total count of them is specified in system configuration _Link needed_

  * **Combination of all methods**

> System log cleans up old records and stores logs for only specified number of sessions. Additionally only specified number of system log records will be stored.

# Use-case #

![http://jxmpp-bot.googlecode.com/svn/trunk/jxmpp-bot/wiki_images/Syslog%20Use-Case.png](http://jxmpp-bot.googlecode.com/svn/trunk/jxmpp-bot/wiki_images/Syslog%20Use-Case.png)

# Data model #

![http://jxmpp-bot.googlecode.com/svn/trunk/jxmpp-bot/wiki_images/Syslog%20database.png](http://jxmpp-bot.googlecode.com/svn/trunk/jxmpp-bot/wiki_images/Syslog%20database.png)

# Implementation #

![http://jxmpp-bot.googlecode.com/svn/trunk/jxmpp-bot/wiki_images/Syslog%20Class-Diagram.png](http://jxmpp-bot.googlecode.com/svn/trunk/jxmpp-bot/wiki_images/Syslog%20Class-Diagram.png)

## Creating new log messages ##

You can put message to syslog using this method:
```
 public boolean putMessage(String text, String sender, String category,
	    String type)
```

Message doesn't go directly into database but instead is saved into internal cache. This is due to sqlite's performance reasons (it is much faster to perform batch insert of 500-1000 messages the inserting them one by one).

Syslog is active object. It manages it's own internal timer which is used to determine when internal cache should be flushed and all messages should be actually inserted into database. Syslog makes decision whether the record is old or not by the use of its _ILogRotateStrategy_.

Concrete strategy provides implementation of log rotation procedure. It also manages it's execution timeline. Syslog gets next log rotation date by the use of concrete strategy. This is more flexible then using additional timer inside syslog and perform rotation in fixed intervals. For example strategy can gather some stats about database during rotation and set next execution interval less then previois one if database is growing too fast.


SysLog also performs sessions management. Each time you start syslog (after construction) or restart it, SysLog automatically creates new SyslogSession object and maps it into database. So when you put message, you actually don't need to specify session to which this message belongs.

## Caching ##

During initialization syslog caches multiple database tables into memory:

  1. syslog\_cathegories
  1. sessions
  1. syslog\_types
  1. syslog\_senders

Those are "primitives" which will be uses very often in the process of construction of new log messages, so we need to cache them in memory. When you put message into SysLog using putMessage() it first of all looks through cached values of _categorie_, _message types_, and _senders_ (lets call them attributes). If there is no such an attribute in cache, it inserts it into database and puts into cache.

This way we gather maximum performance without overloading RAM and producing too much I/O (there won't be millions of categories, senders and types I guess - maybe 50-1000).


## Retrieve messages from syslog ##

Syslog provides several criterions of retrieving messages from database. Consumer of Syslog can specify those

  1. Message text pattern
  1. Date interval (e.g. startDate, endDate)
  1. Session(s)
  1. Message type(s)
  1. Message cathegory(s)
  1. Message sender(s)

Syslog will retrieve records from database (actually database is combined with internal cache during retrieval).

```
  public List<Message> getMessages(msgText, startDate, endDate, sessionsList, typesList, cathegoriesList, sendersList)
```

You can pass null values to this method to omit one or many attributes:

  1. If _msgText_ is set to null then text search using message text attribute won't be performed
  1. If _startDate_ is set to null then messages older then _endDate_ will be retrieved
  1. If _endDate_ is set to null then messages newer then _startDate_ will be retrieved
  1. If both _startDate_ and _endDate_ are set to null, then timestamp attribute won't be taken into account during messages retrieval
  1. If _sessionsList_ is set to null then session attribute won't be taken into account during messages retrieval
  1. The same techique is used for types, categories and senders.

For string attributes ( msgText, type, category and sender ) comparison with ignore case mode used. All attibutes during search are combined using **AND** condition.

Examples:

```
getMessages("Adv", null, null, null, null, null, null);
```
This will retrieve all messages which text attribute contains "Adv"

```
DateFormat fmt = DateFormat.getInstance();
Date startDate = fmt.parse("1.11.2008 11:00:00");
Date endDate = fmt.parse("2.11.2008 12:00:00");
getMessages(null, startDate, endDate, null, null, null, null);
```
This will retrieve all messages with timestamp attribute between 1 Nov 2008 11:00:00 and 2 Nov 2008 12:00:00

```
List<String> types = new ArrayList<String>;
types.add("Alerts");
types.add("Warnings");
getMessages(null, null, null, null, types, null, null);
```
This will retrieve all messages with attributes like "Alerts" and "Warnings". By "like" we mean that attribute contains string.


# Log rotation #

Log rotation (cleanup) is performed by SysLog by the use of log rotate strategies. Each strategy must implement ILogRotateStrategy interface.
Strategy provides set of methods to get rotation date. When syslog detects that current system time is greater then strategy's rotation time it initiates log rotation.

After performing rotation syslog calls strategy to update (calculate) next rotation date.

AbstractLogRotateStrategy is skeletal class, which partially implements ILogRotateStrategy. As an example CountdownLogRotateStrategy is created. It allows to keep in database only specified number of syslog messages.

# List of reserved syslog cathegories #

_in progress_

# List of reserved syslog types #

_in progress_

# List of reserved syslog senders #

_in progress_