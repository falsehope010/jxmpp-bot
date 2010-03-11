package plugins.moc;

import activity.IActive;

public class ActivePluginMoc extends PluginMoc implements IActive {

    @Override
    public boolean isAlive() {
	return isRunning;
    }

    @Override
    public void start() {
	isRunning = true;
    }

    @Override
    public void stop() {
	isRunning = false;
    }

    @Override
    public void run() {
	// method stub
    }

    boolean isRunning;
}
