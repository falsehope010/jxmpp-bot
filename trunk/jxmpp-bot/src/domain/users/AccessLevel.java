package domain.users;

public class AccessLevel {
    public static final int Min = 0;
    public static final int Max = 65535;

    int value;

    public AccessLevel(int value) throws IllegalArgumentException {

	if (value < Min && value > Max) {
	    String msg = "Value must be between AccessLevel.Min and AccessLevel.Max";
	    throw new IllegalArgumentException(msg);
	}
	 
	this.value = value;
    }

    public int getValue() {
	return value;
    }

    public void setValue(int value) throws IllegalArgumentException {
	if (value < Min && value > Max) {
	    String msg = "Value must be between AccessLevel.Min and AccessLevel.Max";
	    throw new IllegalArgumentException(msg);
	}
	this.value = value;
    }

}
