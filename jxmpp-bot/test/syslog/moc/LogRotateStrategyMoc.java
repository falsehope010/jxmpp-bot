package syslog.moc;

import java.util.Date;

import syslog.rotate.ILogRotateStrategy;

public class LogRotateStrategyMoc implements ILogRotateStrategy {

	@Override
	public boolean rotate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Date getRotationDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateRotationDate() {
		// TODO Auto-generated method stub

	}

}
