package org.nextprot.api.user.service.impl;

import java.util.List;

import org.nextprot.api.user.dao.UserDao;
import org.nextprot.api.user.domain.User;
import org.nextprot.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	@Autowired UserDao userDao;

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<User> getUserList() {
		return userDao.getUserList();
	}

	@Override
	@PreAuthorize("hasRole('ROLE_USER') && isCurrentUser()")
	public void updateUser(User user) {
		userDao.updateUser(user);
	}

	@Override
	//TODO @Cacheable if cached, think about cache evict / invalidate the cache
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User usr = userDao.getUserByUsername(username);
		if(usr == null){
			User user = new User();
			user.setUsername(username);
			userDao.createUser(user);
		}
		return userDao.getUserByUsername(username);
	}

}


