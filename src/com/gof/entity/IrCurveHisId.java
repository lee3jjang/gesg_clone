package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class IrCurveHisId implements Serializable{
	
	private String baseDate; 
	private String irCurveId;
	private String matCd;
	
	
	@Column(name ="BASE_DATE")
	public String getBaseDate() {
		return baseDate;
	}
	public void setBaseDate(String baseDate) {
		this.baseDate = baseDate;
	}
	
	@Column(name ="IR_CURVE_ID")
	public String getIrCurveId() {
		return irCurveId;
	}
	public void setIrCurveId(String irCurveId) {
		this.irCurveId = irCurveId;
	}
	@Column(name ="MAT_CD")
	public String getMatCd() {
		return matCd;
	}
	public void setMatCd(String matCd) {
		this.matCd = matCd;
	}
	
	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}
	
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	
}
