package com.fpo.utils;

public class WafHelper {

	public static String stripXSS(String value ) {
		if( value == null ) {
			return null;
		}

		return new XSS().strip( value );
	}

	public static String stripSqlInjection(String value ) {
		if( value == null ) {
			return null;
		}

		return new SqlInjection().strip( value );
	}

	public static String stripSqlXSS(String value ) {
		if( value == null ) {
			return null;
		}

		return stripXSS( stripSqlInjection( value ) );
	}

}
