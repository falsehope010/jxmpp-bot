package suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import plugins.AbstractPluginTest;
import plugins.PluginManagerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { PluginManagerTest.class, AbstractPluginTest.class })
public class PluginsSuite {
    // sole constructor
}
