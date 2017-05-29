package com.synerzip.projectmanagementapp.services;

import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.User;
import com.synerzip.projectmanagementapp.model.UserCredentials;

public interface UserServices {
	User get(String userId);

	PageResult gets(int start, int size, String content);

	User add(User user);

	String delete(String userId);

	User update(User user, String userId);

	User patch(User user, String userId);

	PageResult search(int start, int size, String content);
	
	String userAuthentication(UserCredentials userCredentials);
}
