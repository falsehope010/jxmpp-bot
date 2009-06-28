package stopwatch;

/**
 * Represents bounded stop watch. During construction you must pass value to which stopwatch
 * will be bounded.
 * <p>
 * Once {@link #start()} called, you can check whether current stopwatch elapsed time breaks bound
 * using {@link #breaksBound()}
 * @see StopWatch
 * @author tillias_work
 *
 */
public class BoundedStopWatch extends StopWatch {
	
	/**
	 * Creates new instance using given bound value
	 * @param boundValue Bound value in milliseconds.
	 */
	public BoundedStopWatch(long boundValue){
		super();
		
		this.boundValue = boundValue;
	}
	
	/**
	 * Gets bound value
	 * @return Current bound value of stopwatch
	 */
	public long getBound(){
		return boundValue;
	}
	
	/**
	 * Sets bound value
	 * @param newValue New bound value of stopwatch
	 */
	public void setBound(long newValue){
		boundValue = newValue;
	}
	
	/**
	 * Gets value indicating whether current elapsed time breaks bound.
	 * If stopwatch isn't running returns false.
	 * @return
	 */
	public boolean breaksBound() {
		boolean result = false;

		if (getElapsedTime() > boundValue) {
			result = true;
		}
		return result;
	}
	
	long boundValue;
}
