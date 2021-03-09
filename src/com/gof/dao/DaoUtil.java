
package com.gof.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.util.HibernateUtil;

/**
 *  <p>���� ���̺� ���� �ܼ��� ���� ������ ��쿡 �����ϴ� �������� DAO (Data Access Object) ��.             
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class DaoUtil {
	private final static Logger logger = LoggerFactory.getLogger("DAO");
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	
	public static <T> List<T> getEntities(String entityName, Map<String, Object> param) {
		try {
			return getEntities(Class.forName("com.gof.entity." + entityName), param);
		} catch (Exception e) {
			logger.error("Class Name Error : {}", e);
		}
		return null;
	}
	public static <T> Stream<T> getEntityStream(String entityName, Map<String, Object> param) {
		try {
			return getEntityStream(Class.forName("com.gof.entity." + entityName), param);
		} catch (Exception e) {
			logger.error("Class Name Error : {}", e);
		}
		return null;
	}
	
	/** 
	*  Entity Class �� ���� Equal ������ �Ű������� �����Ͽ� �����͸� �����ϴ� Method ��  
	*  @param klass 	   Entity Class �� Ŭ���� ��Ÿ
	*  @param <T>		   ��Ÿ Ŭ������ ���� Ŭ������ �ǹ���.
	*  @param param		Equal ������ ���Ǳ����� �����ϴ� Map ���� ( ���� : ("baseDate", "201712") �� (key, value) �����ν� baseDate = '201712' �� �����ϴ� �����͸� �����ϴ� �����) 
	*  @return          �Է��� Entity Class �� ������ �����ϴ� instance �� ������.   
	*/ 
	public static <T> List<T> getEntities(Class klass, Map<String, Object> param) {
		StringBuilder builder = new StringBuilder();
		builder.append("select a from ")
			   .append(klass.getSimpleName())
			   .append(" a where 1=1")
			   ;

		for (Map.Entry<String, Object> entry : param.entrySet()) {
			builder.append(" and ").append(entry.getKey()).append(" = :").append(entry.getKey());
		}
//		logger.info("query : {}", builder.toString());
		Query<T> q = session.createQuery(builder.toString(), klass);

		for (Map.Entry<String, Object> entry : param.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		
		List<T> rst = q.getResultList();
		return rst;
	}

	public static <T> List<T> getEntities(Class klass, Map<String, Object> param, Map<String, Map<String, Object>> filter) {
		Filter f ; 
		for(Map.Entry<String, Map<String, Object>> entry : filter.entrySet()) {
			f = session.enableFilter(entry.getKey());
			for(Map.Entry<String, Object> zz : entry.getValue().entrySet()) {
				f.setParameter(zz.getKey(), zz.getValue());
//				logger.info("filter: {},{},{},{}", klass.getName(), entry.getKey(), zz.getKey(), zz.getValue());
			}
		}

		StringBuilder builder = new StringBuilder();
		builder.append("select a from ")
			   .append(klass.getSimpleName())
			   .append(" a where 1=1")
			   ;

		for (Map.Entry<String, Object> entry : param.entrySet()) {
			builder.append(" and ").append(entry.getKey()).append(" = :").append(entry.getKey());
		}
		
		Query<T> q = session.createQuery(builder.toString(), klass);

		for (Map.Entry<String, Object> entry : param.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		
//		logger.info("Query : {},{}", klass.getSimpleName(), builder.toString());
//		q.stream().forEach(s -> logger.info("GetEntities : {},{},{},{}", s.toString()));

		List<T> rst = q.getResultList();
//		for(Map.Entry<String, Map<String, Object>> entry : filter.entrySet()) {
//			session.disableFilter(entry.getKey());
//		}
		return rst;
	}
	
	public static <T> Stream<T> getEntityStream(Class klass, Map<String, Object> param) {
//		Session session = HibernateUtil.getSessionFactory().openSession();
//		session.beginTransaction();

		StringBuilder builder = new StringBuilder();
		builder.append("select a from ")
			   .append(klass.getSimpleName())
			   .append(" a where 1=1")
			   ;

		for (Map.Entry<String, Object> entry : param.entrySet()) {
			builder.append(" and ").append(entry.getKey()).append(" = :").append(entry.getKey());
		}
		
		Query<T> q = session.createQuery(builder.toString(), klass);
		for (Map.Entry<String, Object> entry : param.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
//		logger.info("Query : {},{}", klass.getSimpleName(), builder.toString());

		return q.stream();
	}
	

}
