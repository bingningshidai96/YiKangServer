package com.yikang.app.yikangserver.bean;

import java.util.List;

/**
 * Created by LGhui on 2015/7/27.
 */
public class TestBean {
	private String name;
	private List<String> hobbys;

	public TestBean() {
	}

	public TestBean(List<String> hobbys, String name) {
		this.hobbys = hobbys;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getHobbys() {
		return hobbys;
	}

	public void setHobbys(List<String> hobbys) {
		this.hobbys = hobbys;
	}
}
