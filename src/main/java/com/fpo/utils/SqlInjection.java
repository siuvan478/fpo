package com.fpo.utils;

public class SqlInjection implements Istrip {
	public String strip(String value) {
		return value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
	}
}
