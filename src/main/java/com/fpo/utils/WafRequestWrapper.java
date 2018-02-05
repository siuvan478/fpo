package com.fpo.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class WafRequestWrapper extends HttpServletRequestWrapper {

	private boolean filterXSS = true;

	private boolean filterSQL = true;


	public WafRequestWrapper(HttpServletRequest request, boolean filterXSS, boolean filterSQL ) {
		super(request);
		this.filterXSS = filterXSS;
		this.filterSQL = filterSQL;
	}


	public WafRequestWrapper(HttpServletRequest request ) {
		this(request, true, true);
	}

	@Override
	public String[] getParameterValues(String parameter ) {
		String[] values = super.getParameterValues(parameter);
		if ( values == null ) {
			return null;
		}

		int count = values.length;
		String[] encodedValues = new String[count];
		for ( int i = 0 ; i < count ; i++ ) {
			encodedValues[i] = filterParamString(values[i]);
		}

		return encodedValues;
	}

	@Override
	public String getParameter(String parameter ) {
		return filterParamString(super.getParameter(parameter));
	}

	@Override
	public String getHeader(String name ) {
		return filterParamString(super.getHeader(name));
	}

	@Override
	public Cookie[] getCookies() {
		Cookie[] existingCookies = super.getCookies();
		if ( existingCookies != null ) {
			for ( int i = 0 ; i < existingCookies.length ; ++i ) {
				Cookie cookie = existingCookies[i];
				cookie.setValue(filterParamString(cookie.getValue()));
			}
		}
		return existingCookies;
	}

	protected String filterParamString(String rawValue ) {
		if ( rawValue == null ) {
			return null;
		}
		String tmpStr = rawValue;
		if ( this.filterXSS ) {
			tmpStr = WafHelper.stripXSS(rawValue);
		}
		if ( this.filterSQL ) {
			tmpStr = WafHelper.stripSqlInjection(tmpStr);
		}
		return tmpStr;
	}
}
