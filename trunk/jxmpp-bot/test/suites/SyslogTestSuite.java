package suites;

import mappers.SyslogMessageMapperGetRecordsTest;
import mappers.SyslogMessageMapperTest;
import mappers.SyslogSessionMapperTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import stopwatch.BoundedStopWatchTest;
import syslog.SysLogTest;
import syslog.rotate.CountdownLogRotateStrategyTest;
import domain.syslog.SyslogMessageTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { SyslogMessageTest.class, BoundedStopWatchTest.class,
	SyslogSessionMapperTest.class, SyslogMessageMapperTest.class,
	SyslogMessageMapperGetRecordsTest.class,
	CountdownLogRotateStrategyTest.class, SysLogTest.class })
public class SyslogTestSuite {

}
