package in.ashokit.service.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import in.ashokit.dao.UserDao;
import in.ashokit.service.UserService;

public class UserServiceTest {

	@Test
	public void getNameTest() {

		// creating mock object
		UserDao userDaoMock = PowerMockito.mock(UserDao.class);

		// defining mock obj behaviour
		PowerMockito.when(userDaoMock.findNameById(101)).thenReturn("Raju");

		// injecting mock obj into service obj
		UserService service = new UserService(userDaoMock);

		String actualName = service.getName(101);
		
		String expectedName = "Raju";
		
		assertEquals(expectedName, actualName);
	}

	@Test
	public void loginTest() {
		UserDao userDaoMock = PowerMockito.mock(UserDao.class);
		PowerMockito.when(userDaoMock.findByEmailAndPwd("admin@gmail.com", "admin")).thenReturn(true);

		UserService service = new UserService(userDaoMock);
		String actualStatus = service.login("admin@gmail.com", "admin");

		String expectedStatus = "SUCCESS";
		assertEquals(expectedStatus, actualStatus);
	}

}
