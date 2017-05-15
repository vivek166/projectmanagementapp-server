package com.synerzip.projectmanagementapp.model;

import java.util.List;

public class PageResult {

	private int totalResult;
	private List data;

	public int getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(int totalResult) {
		this.totalResult = totalResult;
	}

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}

	public PageResult() {}
}