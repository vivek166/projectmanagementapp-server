package com.synerzip.projectmanagementapp.model;

import java.util.List;

public class PageResult<T> {

	private int totalResult;
	private List<T> data;
	public int getTotalResult() {
		return totalResult;
	}
	public void setTotalResult(int totalResult) {
		this.totalResult = totalResult;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "PageResult [totalResult=" + totalResult + ", data=" + data + "]";
	}
	public PageResult() {
	}
}
