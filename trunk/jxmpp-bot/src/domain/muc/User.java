package domain.muc;

import java.util.Date;

import utils.HashUtil;
import domain.DomainObject;

/**
 * Represents bot's user. Stores user information and details.
 * 
 * @see UserPermissions
 * @author tillias
 * 
 */
public class User extends DomainObject {

    /**
     * Creates new user and sets all its informational fields to null.
     * <p>
     * User might not want to specify any details, so default constructor is
     * avaliable for this case
     */
    public User() {
	// default constructor avaliable
    }

    public User(String realName, String job, String position, Date birthday,
	    String comments) {
	this.realName = realName;
	this.job = job;
	this.position = position;
	this.birthday = birthday;
	this.comments = comments;
    }

    /**
     * Gets user real name
     * 
     * @return User real name
     */
    public String getRealName() {
	return realName;
    }

    /**
     * Gets user job
     * 
     * @return User job
     */
    public String getJob() {
	return job;
    }

    /**
     * Gets user job position
     * 
     * @return User job position
     */
    public String getPosition() {
	return position;
    }

    /**
     * Gets user birthday
     * 
     * @return User birthday
     */
    public Date getBirthday() {
	return birthday;
    }

    /**
     * Gets user comments
     * 
     * @return User comments
     */
    public String getComments() {
	return comments;
    }

    /**
     * Sets user real name. Parameter can be null
     * 
     * @param realName
     *            User real name
     */
    public void setRealName(String realName) {
	this.realName = realName;
    }

    /**
     * Set user job. Parameter can be null
     * 
     * @param job
     *            User job
     */
    public void setJob(String job) {
	this.job = job;
    }

    /**
     * Sets user job position
     * 
     * @param position
     *            User job position
     */
    public void setPosition(String position) {
	this.position = position;
    }

    /**
     * Sets user birthday. Parameter can be null reference
     * 
     * @param birthday
     *            User birthday
     */
    public void setBirthday(Date birthday) {
	this.birthday = birthday;
    }

    /**
     * Sets user comments. Parameter can be null reference
     * 
     * @param comments
     *            User comments
     */
    public void setComments(String comments) {
	this.comments = comments;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!(obj instanceof User))
	    return false;

	User user = (User) obj;

	boolean areEquals = this.getID() == user.getID();

	if (realName != null)
	    areEquals &= realName.equals(user.realName);
	else
	    areEquals &= user.realName == null;

	if (areEquals) {
	    if (job != null)
		areEquals &= job.equals(user.job);
	    else
		areEquals &= user.job == null;
	} else
	    return false;

	if (areEquals) {
	    if (position != null)
		areEquals &= position.equals(user.position);
	    else
		areEquals &= user.position == null;
	} else
	    return false;

	if (areEquals) {
	    if (birthday != null)
		areEquals &= birthday.equals(user.birthday);
	    else
		areEquals &= user.birthday == null;
	} else
	    return false;

	if (areEquals) {
	    if (comments != null)
		areEquals &= comments.equals(user.comments);
	    else
		areEquals &= user.comments == null;
	} else
	    return false;

	return areEquals;
    }

    @Override
    public int hashCode() {
	if (fHashCode == 0) {
	    int result = HashUtil.SEED;
	    result ^= HashUtil.hashLong(result, getID());
	    result ^= HashUtil.hashString(result, realName);
	    result ^= HashUtil.hashString(result, job);
	    result ^= HashUtil.hashString(result, position);
	    if (birthday != null)
		result ^= birthday.hashCode();
	    result ^= HashUtil.hashString(result, comments);
	    fHashCode = result;
	}

	return fHashCode;
    }

    String realName;
    String job;
    String position;
    Date birthday;
    String comments;

    int fHashCode;
}
