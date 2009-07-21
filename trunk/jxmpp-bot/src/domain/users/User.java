package domain.users;

import java.util.Date;

import domain.DomainObject;

public class User extends DomainObject {

    public String getRealName() {
	return realName;
    }

    public String getJob() {
	return job;
    }

    public String getPosition() {
	return position;
    }

    public Date getBirthday() {
	return birthday;
    }

    public String getComments() {
	return comments;
    }

    public void setRealName(String realName) {
	this.realName = realName;
    }

    public void setJob(String job) {
	this.job = job;
    }

    public void setPosition(String position) {
	this.position = position;
    }

    public void setBirthday(Date birthday) {
	this.birthday = birthday;
    }

    public void setComments(String comments) {
	this.comments = comments;
    }

    String realName;
    String job;
    String position;
    Date birthday;
    String comments;
}
