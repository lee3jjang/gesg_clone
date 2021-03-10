package com.gof.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(SmithWilsonParamHisId.class)
@Table(name="EAS_PARAM_SMITH_WILSON_HIS")
public class SmithWilsonParamHis {
//	private String applyStartYymm;
	private String applStYymm;
	private String curCd;
	private String applyEndYymm;
	private String irCurveDv;
	private double llp;
	private double ufr;
	private double ufrT;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	@Id
	@Column(name="APPL_ST_YYMM", nullable=false)		
//	public String getApplyStartYymm() {
//		return applyStartYymm;
//	}
//	public void setApplyStartYymm(String applyStartYymm) {
//		this.applyStartYymm = applyStartYymm;
//	}
	public String getApplStYymm() {
		return applStYymm;
	}
	public void setApplStYymm(String applStYymm) {
		this.applStYymm = applStYymm;
	}
	
	@Id
	@Column(name="CUR_CD", nullable=false)		
	public String getCurCd() {
		return curCd;
	}
	
	public void setCurCd(String curCd) {
		this.curCd = curCd;
	}
	
	@Column(name ="APPL_ED_YYMM")
	public String getApplyEndYymm() {
		return applyEndYymm;
	}
	public void setApplyEndYymm(String applyEndYymm) {
		this.applyEndYymm = applyEndYymm;
	}
	
	@Column(name ="IR_CURVE_DV")
	public String getIrCurveDv() {
		return irCurveDv;
	}
	
	public void setIrCurveDv(String irCurveDv) {
		this.irCurveDv = irCurveDv;
	}
	@Column(name ="LLP")
	public double getLlp() {
		return llp;
	}
	public void setLlp(double llp) {
		this.llp = llp;
	}
	@Column(name ="UFR")
	public double getUfr() {
		return ufr;
	}
	public void setUfr(double ufr) {
		this.ufr = ufr;
	}
	@Column(name ="UFR_T")
	public double getUfrT() {
		return ufrT;
	}
	public void setUfrT(double ufrT) {
		this.ufrT = ufrT;
	}
	
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public LocalDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	
}



