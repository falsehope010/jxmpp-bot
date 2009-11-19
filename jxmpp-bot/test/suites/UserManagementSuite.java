package suites;

import mappers.ChatMessageMapperTest;
import mappers.RoomMapperTest;
import mappers.UserMapperTest;
import mappers.UserPermissionsMapperTest;
import mappers.VisitMapperTest;
import muc.services.ChatMessageServiceTest;
import muc.services.IdentityMapTest;
import muc.services.JidRoomKeyTest;
import muc.services.PermissionsServiceTest;
import muc.services.RepositoryTest;
import muc.services.VisitServiceTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import domain.muc.RoomTest;
import domain.muc.UserPermissionsTest;
import domain.muc.UserTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { UserPermissionsTest.class, UserMapperTest.class,
	RoomMapperTest.class, UserPermissionsMapperTest.class,
	VisitMapperTest.class, ChatMessageMapperTest.class,
	IdentityMapTest.class, RepositoryTest.class, JidRoomKeyTest.class,
	RoomTest.class, UserTest.class, UserPermissionsTest.class,
	PermissionsServiceTest.class, VisitServiceTest.class,
	ChatMessageServiceTest.class })
public class UserManagementSuite {
    // nothing goes here
}