package com.synerzip.projectmanagementapp.services;

import java.util.List;

import javax.ws.rs.core.SecurityContext;

import com.google.gson.JsonObject;
import com.synerzip.projectmanagementapp.model.ChangePassword;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;
import com.synerzip.projectmanagementapp.model.Token;
import com.synerzip.projectmanagementapp.model.User;
import com.synerzip.projectmanagementapp.model.UserCredentials;

public interface UserServices {

	User get(long id, long companyId);

	PageResult gets(int start, int size, String content, long companyId);

	User add(User user);

	String delete(long id, long companyId);

	User update(User user, long id);

	List<Project> assigned(long id);

	ProjectEmployee assign(User user);

	PageResult search(int start, int size, String content);

	User patch(User user, long id);

	Token userAuthentication(UserCredentials userCredentials);

	String token(long id);

	List<User> getEmployees(int start, int size, String content, long companyId);

	String assignProject(long userId, long projectId);

	User profile(long userId);
	
	ChangePassword changePassword(String username, ChangePassword data);
}