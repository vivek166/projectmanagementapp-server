package com.synerzip.projectmanagementapp.services;

import com.synerzip.projectmanagementapp.model.Company;
import com.synerzip.projectmanagementapp.model.PageResult;

public interface CompanyServices {

	Company get(long companyId);

	PageResult gets(int start, int size, String content);

	Company add(Company company);

	String delete(long companyId);

	Company update(Company company, long companyId);

	Company patch(Company project, long companyId);

	PageResult search(int start, int size, String content);
}