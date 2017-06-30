package com.synerzip.projectmanagementapp.services;

import com.synerzip.projectmanagementapp.model.Company;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.User;

public interface CompanyServices {

	Company get(long companyId);

	PageResult gets(int start, int size, int companyId, String content);

	void add(User user);

	String delete(long companyId);

	Company update(Company company, long companyId);

	Company patch(Company project, long companyId);

	PageResult search(int start, int size, String content);
}