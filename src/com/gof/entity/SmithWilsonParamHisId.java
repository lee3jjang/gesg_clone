package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.gof.interfaces.EntityIdentifier;


@Embeddable
public class SmithWilsonParamHisId implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = -5314286751496179811L;
	
	@Column(name="APPL_ST_YYMM", nullable=false)			//TODO column name change !!!
	private String applStYymm;
	
	@Column(name="CUR_CD", nullable=false)		
	private String curCd;


	public String getApplStYymm() {
		return applStYymm;
	}

	public void setApplStYymm(String applStYymm) {
		this.applStYymm = applStYymm;
	}

	public String getCurCd() {
		return curCd;
	}

	public void setCurCd(String curCd) {
		this.curCd = curCd;
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

