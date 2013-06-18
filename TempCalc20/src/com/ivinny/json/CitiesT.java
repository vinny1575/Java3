package com.ivinny.json;

public enum CitiesT {
	//easy way to concatenate the different cities in the json into an enum
	Boston("Boston"),
	Jacksonville("Jacksonville"),
	Atlanta("Atlanta"),
	SanDiego("San Diego"),
	Dallas("Dallas");
	
	private String type;

	CitiesT (String i){
		this.type = i;
	}
	
	public String getStringType(){
		return type;
	}
	
	//setting enum from string
    public static CitiesT fromLetter(String str) {
        for (CitiesT j : values() ){
            if (j.type.equals(str)) return j;
        }
        return null;
    }
    
	public String isCold(double tempFarhenheit){
		
		if (tempFarhenheit < 30.2){
			return "Very Cold";
		}else if (tempFarhenheit < 50.1){
			return "Kinda Warm";
		}else{
			return "Very Warm";
		}
	}
}
