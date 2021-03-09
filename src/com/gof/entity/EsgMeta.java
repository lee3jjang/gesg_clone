package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.ParamDef;

import com.gof.enums.EBoolean;


@Entity
@IdClass(EsgMetaId.class)
@Table(name ="EAS_ESG_META")
public class EsgMeta implements Serializable {

	private static final long serialVersionUID = -8105176349509184506L;

	@Id
	private String groupId;
	
	@Id
	private String paramKey;
	
	private String paramValue;
	
	@Enumerated(EnumType.STRING)
	private EBoolean useYn;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public EsgMeta() {}

	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getParamKey() {
		return paramKey;
	}
	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	
	public EBoolean getUseYn() {
		return useYn;
	}
	public void setUseYn(EBoolean useYn) {
		this.useYn = useYn;
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
		builder.append(groupId).append(delimeter)
				.append(paramKey).append(delimeter)
				.append(paramValue).append(delimeter)
				.append(useYn)
				;

		return builder.toString();
	}	
}


