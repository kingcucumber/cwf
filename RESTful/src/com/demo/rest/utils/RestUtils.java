package com.demo.rest.utils;

import java.util.Random;

public class RestUtils {

	public static String getRandomString(int length) {
		StringBuffer buffer = new StringBuffer(
				"0123456789abcdefghijklmnopqsrtuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
		StringBuffer sb = new StringBuffer();
		Random rdm = new Random();
		int range = buffer.length();
		for(int i =0 ;i< length ;i++){
			sb.append(buffer.charAt(rdm.nextInt(range)));
		}
		return sb.toString();
	}
}
