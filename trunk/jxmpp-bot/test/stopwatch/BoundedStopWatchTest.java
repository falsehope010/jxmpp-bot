package stopwatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BoundedStopWatchTest {

    @Test
    public void testBoundedStopWatch() {

	long bound = 50;

	BoundedStopWatch bsw = new BoundedStopWatch(bound);

	assertNotNull(bsw);

	assertEquals(bsw.getBound(), bound);
    }

    @Test
    public void testBreaksBound() throws InterruptedException {
	BoundedStopWatch bsw = new BoundedStopWatch(100);

	bsw.setBound(100000);
	bsw.start();
	assertFalse("Should't hit since very big bound value is set", bsw
		.breaksBound());
	assertTrue(bsw.isRunning());

	bsw.stop();
	assertFalse(
		"Shouldn't hit since stopped and not reached big bound value",
		bsw.breaksBound());
	assertFalse(bsw.isRunning());

	bsw.setBound(1);
	bsw.start();
	Thread.sleep(50);
	assertTrue(bsw.isRunning());
	assertTrue("Should hit, since broken bound", bsw.breaksBound());

	bsw.stop();
	assertTrue("Should hit, since broken bound and in stopped state", bsw
		.breaksBound());
	assertFalse(bsw.isRunning());
    }

    @Test
    public void testRestart() throws InterruptedException {
	BoundedStopWatch bsw = new BoundedStopWatch(300);
	bsw.start();

	assertTrue(bsw.isRunning());
	assertFalse(bsw.breaksBound());

	Thread.sleep(1000);
	assertTrue(bsw.breaksBound());

	bsw.restart();
	assertTrue(bsw.isRunning());
	assertFalse("Shouldn't hit since restarted", bsw.breaksBound());
    }
}
