package com.synerzip.projectmanagementapp.filter;

import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.hibernate.search.annotations.Factory;

public class UserFilterFactory {

    @Factory
    public Filter getFilter() {
        Filter userFilterByCompany = new UserFilterByCompany();
        return new CachingWrapperFilter(userFilterByCompany);
    }
}