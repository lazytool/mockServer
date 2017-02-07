package com.letv.mocker.mock.vo;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.List;

public class MockResponse {

	@XStreamImplicit(itemFieldName = "mock")
	private List<Mock> mockList;

	public MockResponse() {
		super();
	}

	public MockResponse(List<Mock> mockList) {
		this.mockList = mockList;
	}

	public List<Mock> getMockList() {
		return this.mockList;
	}

	public void setMockList(List<Mock> mockList) {
		this.mockList = mockList;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Mock mock : this.mockList) {
			sb.append("[" + mock.toString() + "]");
		}
		return "MockResponse [mockList=" + sb.toString() + "]";
	}
}
