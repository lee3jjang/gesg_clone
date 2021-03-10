package com.gof.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EAS_PARAM_SMITH_WILSON")
public class SmithWilsonParam {
	private String curCd;
	private String irCurveDv;
	private double llp;
	private double ufr;
	private double ufrT;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	
	public String getCurCd() {
		return curCd;
	}
	public void setCurCd(String curCd) {
		this.curCd = curCd;
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


