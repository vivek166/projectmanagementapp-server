package com.synerzip.projectmanagementapp.services;

import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.User;
import com.synerzip.projectmanagementapp.model.UserCredentials;

public interface UserServices {
	User get(String userName);

	PageResult gets(int start, int size, String content);

	User add(User user);

	String delete(String userName);

	User update(User user, String userName);

	User patch(User user, String userName);

	PageResult search(int start, int size, String content);

	String userAuthentication(UserCredentials userCredentials);
}
