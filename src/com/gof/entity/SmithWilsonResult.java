package com.gof.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmithWilsonResult {
	private final static Logger logger = LoggerFactory.getLogger("SmithWilson");
	
	private String baseYymm;
	private String sceNo;
	private String irCurveId;
	private String matCd;
	
	private double  timeFactor;
	private int		monthNum;
	private double  spotCont;
	private double	spotAnnual;
	private double	discountFactor;
	private double	fwdCont;				//1M forward юс.
	private double	fwdAnnual;
	private int		fwdMonthNum;

	public SmithWilsonResult() {
	}

	
	public String getBaseYymm() {
		return baseYymm;
	}


	public void setBaseYymm(String baseYymm) {
		this.baseYymm = baseYymm;
	}


	public String getIrCurveId() {
		return irCurveId;
	}
	public void setIrCurveId(String irCurveId) {
		this.irCurveId = irCurveId;
	}
	public String getMatCd() {
		return matCd;
	}
	public void setMatCd(String matCd) {
		this.matCd = matCd;
	}


	public String getSceNo() {
		return sceNo;
	}

	public void setSceNo(String sceNo) {
		this.sceNo = sceNo;
	}


	public double getTimeFactor() {
		return timeFactor;
	}

	public void setTimeFactor(double timeFactor) {
		this.timeFactor = timeFactor;
	}

	public int getMonthNum() {
		return monthNum;
	}

	public void setMonthNum(int monthNum) {
		this.monthNum = monthNum;
	}

	public double getSpotCont() {
		return spotCont;
	}

	public void setSpotCont(double spotCont) {
		this.spotCont = spotCont;
	}

	public double getSpotAnnual() {
		return spotAnnual;
	}

	public void setSpotAnnual(double spotAnnual) {
		this.spotAnnual = spotAnnual;
	}

	public double getDiscountFactor() {
		return discountFactor;
	}

	public void setDiscountFactor(double discountFactor) {
		this.discountFactor = discountFactor;
	}

	public double getFwdCont() {
		return fwdCont;
	}

	public void setFwdCont(double fwdCont) {
		this.fwdCont = fwdCont;
	}

	public double getFwdAnnual() {
		return fwdAnnual;
	}

	public void setFwdAnnual(double fwdAnnual) {
		this.fwdAnnual = fwdAnnual;
	}

	public int getFwdMonthNum() {
		return fwdMonthNum;
	}

	public void setFwdMonthNum(int fwdMonthNum) {
		this.fwdMonthNum = fwdMonthNum;
	}

	@Override
	public String toString() {
		return toString(",");
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(timeFactor).append(delimeter)
			   .append(monthNum).append(delimeter)
			   .append(spotCont).append(delimeter)
			   .append(spotAnnual).append(delimeter)
			   .append(discountFactor).append(delimeter)
			   .append(fwdCont).append(delimeter)
			   .append(fwdAnnual).append(delimeter)
			   .append(fwdMonthNum).append("\n")
			   ;
		return builder.toString();
	}
	
	public IrCurveHis convertToIrCurveHis() {
		IrCurveHis rst = new IrCurveHis();
		
		rst.setBaseDate(this.baseYymm);
		rst.setIrCurveId(this.irCurveId);
		rst.setSceNo(this.sceNo);
		rst.setMatCd(this.matCd);
		rst.setIntRate(this.spotAnnual);
		
		return rst;
	}
	
//	public IrCurveHis convertToBottomUp() {
//		BottomupDcnt rst = new BottomupDcnt();
//		
//		rst.setBaseYymm(this.baseYymm);
//		rst.setIrCurveId(this.irCurveId);
//		rst.setSceNo(this.sceNo);
//		rst.setMatCd(this.matCd);
//		rst.setIntRate(this.spotAnnual);
//		
//		return rst;
//	}
//	
//	public IrCurveHis convertToIrCurveHis() {
//		IrCurveHis rst = new IrCurveHis();
//		
//		rst.setBaseDate(this.baseYymm);
//		rst.setIrCurveId(this.irCurveId);
//		rst.setSceNo(this.sceNo);
//		rst.setMatCd(this.matCd);
//		rst.setIntRate(this.spotAnnual);
//		
//		return rst;
//	}
}
