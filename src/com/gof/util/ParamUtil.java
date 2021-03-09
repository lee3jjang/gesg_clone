package com.gof.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.dao.DaoUtil;
import com.gof.entity.EsgMeta;

public class ParamUtil {
	
	private final static Logger logger = LoggerFactory.getLogger("ParamUtil");
	
	private static Map<String, List<EsgMeta>> dbParamMap = new HashMap<String, List<EsgMeta>>();
	private static Map<String, String> paramMap = new HashMap<String, String>();

	static {
		Session session = HibernateUtil.getSessionFactory().openSession();
	    session.beginTransaction();
	    
	    Map<String, Object> param = new HashMap<String, Object>();
	    
    	List<EsgMeta> paramList = DaoUtil.getEntities(EsgMeta.class, param);
    	dbParamMap = paramList.stream().filter(s-> s.getUseYn().isTrueFalse()).collect(Collectors.groupingBy(s->s.getGroupId(), Collectors.toList()));

	}
	
	public static List<EsgMeta> getParamList(String groupId) {
		if(!dbParamMap.containsKey(groupId)) {
			groupId ="BASE";
		}
		paramMap = dbParamMap.getOrDefault(groupId, new ArrayList<EsgMeta>()).stream().collect(Collectors.toMap(s->s.getParamKey(), s->s.getParamValue()));
		return dbParamMap.getOrDefault(groupId, new ArrayList<EsgMeta>());
	}
	
	public static Map<String, String> getParamMap() {
		if(paramMap.size()!=0) {
			return paramMap; 
		}
		else {
			logger.info("call size zero param Map");;
			return dbParamMap.getOrDefault("BASE", new ArrayList<EsgMeta>()).stream().collect(Collectors.toMap(s->s.getParamKey(), s->s.getParamValue()));
		}
	}
	
	public static Map<String, List<EsgMeta>>  getDbParamMap() {
		return dbParamMap;
	}
}
