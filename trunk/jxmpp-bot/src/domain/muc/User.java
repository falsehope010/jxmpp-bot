package domain.muc;

import java.util.Date;

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

    String realName;
    String job;
    String position;
    Date birthday;
    String comments;
}
