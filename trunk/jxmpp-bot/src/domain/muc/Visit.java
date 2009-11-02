package domain.muc;

import java.util.Date;

import domain.DomainObject;

/**
 * Represents chat user visit.
 * <p>
 * Stores information about when user has entered chat room (e.g. started visit)
 * and left chat room (e.g. finished visit).
 * 
 * 
 * @author tillias
 * 
 */
public class Visit extends DomainObject {

    /**
     * Creates new instance of Visit and sets it's start date to current date
     * 
     * @param permissions
     *            Permissions object used to determine user who joined chat room
     * @throws NullPointerException
     *             Thrown if parameter passed to constructor is null reference
     * @throws IllegalArgumentException
     *             Thrown if parameter passed to constructor is not valid
     *             persistent domain object
     * @see UserPermissions
     */
    public Visit(UserPermissions permissions) throws NullPointerException,
	    IllegalArgumentException {
	if (permissions == null)
	    throw new NullPointerException();

	if (!permissions.isPersistent())
	    throw new IllegalArgumentException(
		    "Permissions passed to constructor must be persistent"
			    + "domain object.");

	startDate = new Date();
	this.permissions = permissions;
    }

    /**
     * Gets start date of visit. Can't be null
     * 
     * @return Start date of visit
     */
    public Date getStartDate() {
	return startDate;
    }

    /**
     * Gets end date of visit. Might be null if user joined chat room but hasn't
     * left it yet
     * 
     * @return End date of visit
     */
    public Date getEndDate() {
	return endDate;
    }

    /**
     * Sets end date of visit
     * 
     * @param endDate
     *            End date of visit
     */
    public void setEndDate(Date endDate) {
	this.endDate = endDate;
    }

    /**
     * Gets user permissions associated with visit
     * 
     * @return User permissions associated with visit
     * @see UserPermissions
     */
    public UserPermissions getPermissions() {
	return permissions;
    }

    Date startDate;
    Date endDate;
    UserPermissions permissions;
}
