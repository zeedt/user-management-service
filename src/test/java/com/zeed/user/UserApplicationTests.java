package com.zeed.user;

import com.zeed.generic.RestApiClient;
import com.zeed.user.config.DataSourceConfig;
import com.zeed.user.services.UserService;
import com.zeed.usermanagement.apimodels.ManagedUserModelApi;
import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.models.UserCategory;
import com.zeed.usermanagement.request.UserDetailsRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import({DataSourceConfig.class, RestApiClient.class,UserService.class,UserDetailsRequest.class})
public class UserApplicationTests {

	@Autowired
	public UserDetailsRequest userDetailsRequest;
//
//	@Autowired
//	PasswordEncoder passwordEncoder;

	@Autowired
	UserService userService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void generatePassword (){

//		System.out.println(passwordEncoder.encode("secret"));

	}
	@Test
	public void testUserService () throws Exception {

		ManagedUserModelApi userModelApi = userService.getUserModelByUsername("hjbhjbjh");

		System.out.println(userModelApi);

	}
	@Test
	public void getUserInfo() throws Exception {
		ManagedUserModelApi userModelApi = userDetailsRequest.getManagedUserDetailsEntity("superuser");
		return;
	}

	@Test
	public void addUSer(){
		Date date = new Date(new java.util.Date().getTime());
		ManagedUser managedUser = new ManagedUser();
		managedUser.setPassword("password");
		managedUser.setDateCreated(date);
		managedUser.setEmail("mytest@email.com");
		managedUser.setFirstName("Saheed");
		managedUser.setLastName("Yusuf");
		managedUser.setPhoneNumber("00000000000000");
		managedUser.setUserCategory(UserCategory.ADMIN);
		managedUser.setUserName("zeedoli");
		userService.addManagedUser(managedUser);
	}


}
