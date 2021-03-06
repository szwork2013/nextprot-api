package org.nextprot.api.user.dao;

import java.util.List;

import org.nextprot.api.user.domain.UserApplication;

public interface UserApplicationDao {

    List<UserApplication> getUserApplicationListByOwnerId(long userId);

	long createUserApplication(UserApplication userApplication);

    void updateUserApplication(UserApplication userApplication);

	void deleteUserApplication(UserApplication userApplication);

	UserApplication getUserApplicationById(long id);
	
}
