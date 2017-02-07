package com.letv.mocker.mock.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Header {

	// 属性注解 <header key="Content-Type"
	// val="text/html; charset=ISO-8859-1"></header>
	@XStreamAsAttribute
	@XStreamAlias("name")
	private String name;

	@XStreamAsAttribute
	@XStreamAlias("value")
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Header() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Header(String name, String value) {
		this.name = name;
		this.value = value;
	}
}
