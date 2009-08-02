package muc;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import domain.muc.UserPermissions;

public class Repository {
    public Repository(Database db) {
	this.db = db;

	// TODO load all items from database. On error throw exception
	// This way any service can't be created since repo isn't created
    }

    public List<UserPermissions> getUserPermissions() {
	return new ArrayList<UserPermissions>();

	// TODO:
    }

    private Database db;
}
