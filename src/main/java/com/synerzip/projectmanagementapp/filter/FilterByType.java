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

public class FilterByType {
	private String type;

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	@Key
	public FilterKey getKey() {
		StandardFilterKey key = new StandardFilterKey();
		key.addParameter(type);
		return key;
	}

	@Factory
	public Filter getFilter() {
		Query query1 = (Query) new TermQuery(new Term("type", type.toString()));
		return new CachingWrapperFilter(new QueryWrapperFilter((org.apache.lucene.search.Query) query1));
	}
}
