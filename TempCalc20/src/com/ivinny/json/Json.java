package com.ivinny.json;

public class Json {
	//simple json string (creating data)
	public static String getJsonString(){
		return "{Cities: " +
				"[{city: Boston, state: MA, temp: 21}," +
				"{city: Jacksonville, state: FL, temp: 60}," +
				"{city: Atlanta, state: GA, temp: 29}," +
				"{city: San_Diego, state: CA, temp: 75}," +
				"{city: Dallas, state: TX, temp: 41}]" +
				"}";
	}
}
