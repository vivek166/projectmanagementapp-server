package com.synerzip.projectmanagementapp.services;

import java.util.List;

import javax.ws.rs.core.SecurityContext;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;
import com.synerzip.projectmanagementapp.model.Token;
import com.synerzip.projectmanagementapp.model.User;
import com.synerzip.projectmanagementapp.model.UserCredentials;

public interface UserServices {

	User get(long id/*, SecurityContext securityContext*/);

	PageResult gets(int start, int size, String content);

	User add(User user);

	String delete(long id);

	User update(User user, long id);

	List<Project> assigned(long id);

	ProjectEmployee assign(User user);

	PageResult search(int start, int size, String content);

	User patch(User user, long id);
	
    Token userAuthentication(UserCredentials userCredentials);
    
    String token(long id);
}