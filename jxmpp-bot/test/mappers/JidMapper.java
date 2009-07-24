package mappers;

import database.Database;
import domain.DomainObject;
import exceptions.DatabaseNotConnectedException;

public class JidMapper extends AbstractMapper {

    public JidMapper(Database db) throws DatabaseNotConnectedException,
	    NullPointerException {
	super(db);
    }

    @Override
    public boolean delete(DomainObject obj) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean save(DomainObject obj) {
	// TODO Auto-generated method stub
	return false;
    }

}
