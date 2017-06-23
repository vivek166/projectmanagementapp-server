package com.synerzip.projectmanagementapp.filter;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

public class UserFilterFactoryByCompany {
	private long companyId;

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public long getCompanyId() {
		return companyId;
	}

	@Key
	public FilterKey getKey() {
		StandardFilterKey key = new StandardFilterKey();
		key.addParameter(companyId);
		return key;
	}

	@Factory
	public Filter getFilter() {
		Query query = (Query) new TermQuery(new Term("companyId", companyId+"".toString()));
		return new CachingWrapperFilter(new QueryWrapperFilter((org.apache.lucene.search.Query) query));
	}
}
