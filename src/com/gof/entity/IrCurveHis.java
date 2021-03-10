package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import com.gof.interfaces.IIntRate;
import com.gof.util.FinUtils;

@Entity
@IdClass(IrCurveHisId.class)
@Table(name ="EAS_IR_CURVE_HIS")
@FilterDef(name="irCurveEqBaseDate", parameters= @ParamDef(name ="baseDate",  type="string"))
public class IrCurveHis implements Serializable, IIntRate {
	
	
	private String baseDate; 
	private String irCurveId;
	private String matCd;
	private String sceNo; 
	private Double intRate;

	private int forwardNum;
	private IrCurve irCurve;
	
	public IrCurveHis() {
	
	}
	public IrCurveHis(String baseDate, String matCd, Double intRate) {
		this.baseDate = baseDate;
		this.matCd = matCd;
		this.intRate = intRate;
	}
	
	public IrCurveHis(String bssd, IrCurveHis curveHis) {
		this.baseDate = curveHis.getBaseDate();
		this.irCurveId = curveHis.getIrCurveId();
		this.matCd = curveHis.getMatCd();
		this.sceNo = curveHis.getSceNo();
		this.intRate = curveHis.getIntRate();

		this.forwardNum = (int)FinUtils.monthBetween(bssd, curveHis.getBaseDate());
				
	}
	@Id
	@Column(name ="BASE_DATE")
	public String getBaseDate() {
		return baseDate;
	}
	public void setBaseDate(String baseDate) {
		this.baseDate = baseDate;
	}
	@Id
	@Column(name ="IR_CURVE_ID")
	public String getIrCurveId() {
		return irCurveId;
	}
	public void setIrCurveId(String irCurveId) {
		this.irCurveId = irCurveId;
	}
	
	@Id
	@Column(name ="MAT_CD")
	public String getMatCd() {
		return matCd;
	}
	public void setMatCd(String matCd) {
		this.matCd = matCd;
	}
	@Transient
	public String getSceNo() {
		return sceNo;
	}
	public void setSceNo(String sceNo) {
		this.sceNo = sceNo;
	}
	@Column(name ="INT_RATE")
	public Double getIntRate() {
		return intRate;
	}
	public void setIntRate(Double intRate) {
		this.intRate = intRate;
	}
	
	@ManyToOne
	@JoinColumn(name ="IR_CURVE_ID", insertable=false, updatable= false)
	public IrCurve getIrCurve() {
		return irCurve;
	}
	public void setIrCurve(IrCurve irCurve) {
		this.irCurve = irCurve;
	}

	@Transient	
	public int getForwardNum() {
		return forwardNum;
	}
	public void setForwardNum(int forwardNum) {
		this.forwardNum = forwardNum;
	}
	
	@Transient
	public int getMatNum() {
		return Integer.parseInt(matCd.substring(1));
	}
	@Transient
	public IrCurveHis addForwardTerm(String bssd) {
		return new IrCurveHis(bssd, this);
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

	@Override
	public String toString() {
		return toString(",");
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(baseDate).append(delimeter)
			   .append(sceNo==null? "0":sceNo).append(delimeter)
			   .append(irCurveId).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(intRate).append(delimeter)
			   .append(forwardNum)
//			   .append(lastUpdateDate)
			   ;

		return builder.toString();
	}
//******************************************************Biz Method**************************************
	@Transient
	public String getBaseYymm() {
		return getBaseDate().substring(0,6);
	}
	@Transient
	public boolean isBaseTerm() {
		if(matCd.equals("M0003") 
				|| matCd.equals("M0006") 
				|| matCd.equals("M0009")
				|| matCd.equals("M0012")
				|| matCd.equals("M0024")
				|| matCd.equals("M0036")
				|| matCd.equals("M0060")
				|| matCd.equals("M0084")
				|| matCd.equals("M0120")
				|| matCd.equals("M0240")
				) {
			return true;
		}
		return false;	
			
	}
	
	@Transient
	@Override
	public Double getSpread() {
		return 0.0;
	}
	
	
}
