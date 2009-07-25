package suites;

import mappers.RoomMapperTest;
import mappers.UserMapperTest;
import mappers.UserPermissionsMapperTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import domain.muc.UserPermissionsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { UserPermissionsTest.class, UserMapperTest.class,
	RoomMapperTest.class, UserPermissionsMapperTest.class })
public class UserManagementSuite {
    // sole constructor
}