package com.gof.dao;

import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.entity.SmithWilsonParam;
import com.gof.entity.SmithWilsonParamHis;
import com.gof.util.HibernateUtil;

/**
 *  <p> 금리모형의 매개변수 정보를 추출함.
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class SmithWilsonDao {
	private final static Logger logger = LoggerFactory.getLogger(SmithWilsonDao.class);
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	
	public static List<SmithWilsonParam> getParamList() {
		String q = "select a from SmithWilsonParam a "
				+ " where 1=1" 
				;
		
		return session.createQuery(q, SmithWilsonParam.class).list()
		;

	}

	public static List<SmithWilsonParamHis> getParamHisList(String bssd) {
		String maxBssdquery = " SELECT MAX(APPL_ST_YYMM) "  
				+ "				from ESG.EAS_PARAM_SMITH_WILSON_HIS   a "
				+ "				where 1=1 "
				+ "				and a.APPL_ST_YYMM <= :bssd	"
				+ "				and a.APPL_ED_YYMM >= :bssd	"
				;
		Object maxBssd =  session.createNativeQuery(maxBssdquery)
				.setParameter("bssd", bssd)
				.uniqueResult()
				;
		
		String query = " SELECT a from SmithWilsonParamHis   a "
				+ "				where 1=1 "
//				+ "				and a.applyStartYymm = :maxBssd "
				+ "				and a.applStYymm = :maxBssd "
				;
		
		List<SmithWilsonParamHis> rst  =  session.createQuery(query,  SmithWilsonParamHis.class)
											 .setParameter("maxBssd", maxBssd==null? bssd: maxBssd.toString())
											 .getResultList()
											 ;
		return rst;

	}
	
		
}

