package io.github.u2ware.apps.user;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UserDetailsDelegate;

import io.github.u2ware.apps.ApplicationTests;

public class UserRestRepositoryTests extends ApplicationTests {

	private @Autowired UserRestRepository userRestRepository;

	@Test
	public void contextLoads() throws Exception {

		userRestRepository.save(user("abcd"));
		userRestRepository.save(user("efgh"));

		super.performRead(uri("/users/search/findByUsername"), new UserDetailsDelegate("u1"), params("username=abcd"),
				status().is4xxClientError());
		super.performRead(uri("/users/search/findByUsername"), new UserDetailsDelegate("u1", "ROLE_ADMIN"),
				params("username=efgh"), status().is4xxClientError());
		super.performRead(uri("/users/search/findByUsername"), new UserDetailsDelegate("u1", "ROLE_ADMIN"),
				params("username=abcd"), status().is2xxSuccessful());

		super.performRead(uri("/users"), new UserDetailsDelegate("u1"), status().is2xxSuccessful());
		super.performRead(uri("/users/abcd"), new UserDetailsDelegate("u1"), status().is2xxSuccessful());
		super.performRead(uri("/users/efgh"), new UserDetailsDelegate("u1"), status().is4xxClientError()); // 401
		super.performRead(uri("/users/ijkl"), new UserDetailsDelegate("u1"), status().is4xxClientError()); // 404

		userRestRepository.deleteAll();
	}

	protected User user(String username, String... roles) {
		User u = new User();
		u.setUsername(username);
		u.setPassword(username);
		u.setNickname(username);
		u.setAuthoritiesValue(roles);
		return u;
	}

}