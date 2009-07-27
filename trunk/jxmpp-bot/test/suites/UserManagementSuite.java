package suites;

import mappers.ChatMessageMapperTest;
import mappers.RoomMapperTest;
import mappers.UserMapperTest;
import mappers.UserPermissionsMapperTest;
import mappers.VisitMapperTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import domain.muc.UserPermissionsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { UserPermissionsTest.class, UserMapperTest.class,
	RoomMapperTest.class, UserPermissionsMapperTest.class,
	VisitMapperTest.class, ChatMessageMapperTest.class })
public class UserManagementSuite {
    // sole constructor
}