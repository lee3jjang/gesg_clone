package com.gof.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EBaseMatCd {
	   M0001 (0.083) {
		   public String getKTBCode() { return "KTB1M";   }
	   }
	 , M0003 (0.25){
		   public String getKTBCode() {   return "KTB3M";   }
	   }
	 , M0006 (0.5){
		   public String getKTBCode() {   return "KTB6M";  }
	   }
	 , M0009 (0.75){
		   public String getKTBCode() {   return "KTB9M";   }
	   }
	 , M0012 (1.0){
		   public String getKTBCode() {   return "KTB1Y";   }
	   }
	 , M0018 (1.5)
	 , M0024 (2.0){
		   public String getKTBCode() {	   return "KTB2Y";   }
	   }
	 , M0036 (3.0){
		   public String getKTBCode() {	   return "KTB3Y";   }
	   }
	 , M0048 (4.0){
		   public String getKTBCode() {   return "KTB4Y";   }
	   }
	 , M0060 (5.0){
		   public String getKTBCode() {	   return "KTB5Y";   }
	   }
	 , M0084 (7.0){
		   public String getKTBCode() {	   return "KTB7Y";   }
	   }
	 , M0120 (10.0){
		   public String getKTBCode() {	   return "KTB10Y";   }
	   }
	 ;
	
	
	private double yearFrac;

	private EBaseMatCd(double yearFrac) {
		this.yearFrac = yearFrac;
	}

	public double getYearFrac() {
		return yearFrac;
	}

	public static List<String> names(){
		return Arrays.stream(values()).map(s->s.name()).collect(Collectors.toList());
	}

	public static double[] yearFracs(){
//		return Arrays.stream(values()).map(s->s.getYearFrac()).toArray(Double[]::new);
		return Arrays.stream(values()).mapToDouble(s->s.getYearFrac()).toArray();
		
	}
	
	public static boolean isContain(String matCd) {
		for(EBaseMatCd aa : values()){
			if(aa.name().equals(matCd)){
				return true;
			}
		}
		return false;
	}
	
	public static String getBaseMatCd(String matCd) {
		return EBaseMatCd.getBaseMatCdEnum(matCd).name();
	}
	
	
	public String getKTBCode() {
		return "";
	}
	
	public static EBaseMatCd getBaseMatCdEnum(String matCd) {
		double matYearFrac = Double.parseDouble(matCd.split("M")[1]) /12  ;
		
		if(matYearFrac<= 0.1) {
			return EBaseMatCd.M0001;
		}

		EBaseMatCd rst = EBaseMatCd.M0120;
		for(EBaseMatCd aa : EBaseMatCd.values()) {
			if( aa.getYearFrac() <  matYearFrac) {
				continue;
			}
			else {
				return aa ;
			}
		}
		return  rst;
	}
	
	public static EBaseMatCd getBaseMatCdEnum(int ordinal) {
		for(EBaseMatCd aa : EBaseMatCd.values()) {
			if( aa.ordinal()==ordinal) {
				return aa;
			}
		}
		return EBaseMatCd.M0120;

	}
}
