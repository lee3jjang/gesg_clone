package com.gof.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import com.gof.enums.EBoolean;

@Entity
@Table(name ="EAS_IR_CURVE")
public class IrCurve implements Serializable{
	private String irCurveId;
	private String irCurveNm;
	private String curCd;
	private String applBizDv;
	private String applMethDv;
	private String creditGrate;
	private String interpolMethod;
	private String refCurveId;
	private EBoolean useYn;
	
//	private List<IrCurveHis> irCurveHis = new ArrayList<IrCurveHis>();
//	private Map<String, IrCurveHis> irCurveHis = new HashMap<String, IrCurveHis>();
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name ="IR_CURVE_ID")
	public String getIrCurveId() {
		return irCurveId;
	}
	public void setIrCurveId(String irCurveId) {
		this.irCurveId = irCurveId;
	}
	
	@Column(name ="IR_CURVE_NM")
	public String getIrCurveNm() {
		return irCurveNm;
	}
	public void setIrCurveNm(String irCurveNm) {
		this.irCurveNm = irCurveNm;
	}
	@Column(name ="CUR_CD")
	public String getCurCd() {
		return curCd;
	}
	public void setCurCd(String curCd) {
		this.curCd = curCd;
	}
	public String getApplBizDv() {
		return applBizDv;
	}
	public void setApplBizDv(String applBizDv) {
		this.applBizDv = applBizDv;
	}
	
	public String getApplMethDv() {
		return applMethDv;
	}
	public void setApplMethDv(String applMethDv) {
		this.applMethDv = applMethDv;
	}
	@Column(name ="CRD_GRD_CD")
	public String getCreditGrate() {
		return creditGrate;
	}
	public void setCreditGrate(String creditGrate) {
		this.creditGrate = creditGrate;
	}
	
	@Column(name ="INTP_METH_CD")
	public String getInterpolMethod() {
		return interpolMethod;
	}
	public void setInterpolMethod(String interpolMethod) {
		this.interpolMethod = interpolMethod;
	}
	
	public String getRefCurveId() {
		return refCurveId;
	}
	public void setRefCurveId(String refCurveId) {
		this.refCurveId = refCurveId;
	}
	
	@Enumerated(EnumType.STRING)
	public EBoolean getUseYn() {
		return useYn;
	}
	public void setUseYn(EBoolean useYn) {
		this.useYn = useYn;
	}
	
//	@OneToMany(mappedBy ="irCurve")
//	@Filter(name ="irCurveEqBaseDate", condition= "BASE_DATE =:baseDate")
//	public List<IrCurveHis> getIrCurveHis() {
//		return irCurveHis;
//	}
//	public void setIrCurveHis(List<IrCurveHis> irCurveHis) {
//		this.irCurveHis = irCurveHis;
//	}
	
//	@OneToMany(mappedBy ="irCurve", fetch=FetchType.LAZY)
////	@Filter(name ="eqBaseDate", condition= "BASE_DATE =:bssd")
//	@MapKey(name="matCd")
//	public Map<String, IrCurveHis> getIrCurveHis() {
//		return irCurveHis;
//	}
//	public void setIrCurveHis(Map<String, IrCurveHis> irCurveHis) {
//		this.irCurveHis = irCurveHis;
//	}
	
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
