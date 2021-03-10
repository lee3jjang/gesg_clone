package com.gof.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
//import com.gof.entity.IrSce;
import com.gof.enums.EBaseMatCd;
import com.gof.enums.EBoolean;
import com.gof.interfaces.IIntRate;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;


/**
 *  <p> 금리 기간구조를 설명하는 금리이력  정보를 추출함.
 *  <p> 
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class IrCurveHisDao {
	private final static Logger logger = LoggerFactory.getLogger("DAO");
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	private static String baseQuery = "select a from IrCurveHis a where 1=1 ";

	public static List<IrCurveHis> getEntities() {
		return session.createQuery(baseQuery, IrCurveHis.class).getResultList();
	}
	public static List<IrCurve> getIrCurveByCrdGrdCd(String crdCrdCd){
		String query = "select a from IrCurve a "
				+ "		where 1=1 "
				+ "		and a.creditGrate = :crdGrdCd	"
				+ "		and a.useYn		  = :useYn "
				+ "		and a.applMethDv  <> '6' "					//TopDown 은 제외하고 BondSpot 또는 해외 Spap rate 들만..
				+ "		and a.refCurveId  is null"
				;
		
		return   session.createQuery(query, IrCurve.class)
								 .setParameter("crdGrdCd",crdCrdCd )				//월말 기준으로 전환함.
								 .setParameter("useYn", EBoolean.Y )				//통화별로 Y 인 Curve ID 는 1 개임.
								 .getResultList();
								 
	}
	public static List<IrCurve> getBottomUpIrCurve(){
		return getIrCurveByGenMethod("4");
	}
	
	public static List<IrCurve> getIrCurveByGenMethod(String applMethDv){
		String query = "select a from IrCurve a "
				+ "		where 1=1 "
				+ "		and a.applMethDv = :applMethDv"
				+ "		and a.useYn = :useYn"
				;
		
		return   session.createQuery(query, IrCurve.class)
								 .setParameter("applMethDv",applMethDv)			// Bond Gen : 3, BottomUp : 4 , TopDown : 6, KICS : 5 SwapRate : 7
								 .setParameter("useYn", EBoolean.Y)				
								 .getResultList();
	}
	
	public static Map<String, String> getEomMap(String bssd, String irCurveId){
		String query = "select substring(a.baseDate, 0,6), max(a.baseDate) "
				+ "		from IrCurveHis a "
				+ "		where 1=1 "
				+ "		and a.irCurveId = :irCurveId "
				+ "		and a.baseDate <= :bssd	"
				+ "		group by substring(a.baseDate, 0,6)"
				;
		
		List<Object[]> maxDate =  session.createQuery(query)
				 				 .setParameter("irCurveId", irCurveId)			
								 .setParameter("bssd", FinUtils.toEndOfMonth(bssd))				//월말 기준으로 전환함.
								 .getResultList();
//		if(maxDate == null) {
//			logger.warn("IR Curve History Data is not found {} at {}!!!" , irCurveId, FinUtils.toEndOfMonth(bssd));
//			return new hashMap<String, String>;
//		}
		
		Map<String, String> rstMap = new HashMap<String, String>();
		for(Object[] aa : maxDate) {
			rstMap.put(aa[0].toString(), aa[1].toString());
		}
		return rstMap;
	}
	
	public static String getMaxBaseDate (String bssd, String irCurveId) {
		
		String query = "select max(a.baseDate) "
				+ "		from IrCurveHis a "
				+ "		where 1=1 "
				+ "		and a.irCurveId = :irCurveId "
				+ "		and a.baseDate <= :bssd	"
				;
		Object maxDate =  session.createQuery(query)
				 				 .setParameter("irCurveId", irCurveId)			
								 .setParameter("bssd", FinUtils.toEndOfMonth(bssd))				//월말 기준으로 전환함.
								 .uniqueResult();
		if(maxDate==null) {
			logger.warn("IR Curve History Data is not found {} at {}!!!" , irCurveId, FinUtils.toEndOfMonth(bssd));
			return bssd;
		}
		return maxDate.toString();
	}
	
	/** 
	*  <p> 모든 금리곡선 ID 에 대해서 기준년월 기준 가장 최근의 금리 기간구조 추출  
	*  @param bssd 	   기준년월
	*  @return		   최근 금리기간구조                 
	*/ 
	public static List<IrCurveHis> getIrCurveHis(String bssd){
		return getIrCurveHis(bssd, "A100");
	}

	/** 
	*  <p> KRW 국고채의 기준년월 기준 가장 최근의 금리 기간구조 추출 
	*  @param bssd 	   기준년월
	*  @return		   최근 금리기간구조                 
	*/
	public static List<IrCurveHis> getKTBIrCurveHis(String bssd){
		return getIrCurveHis(bssd, "A100");				
	}
	
	public static List<IIntRate> getIrCurveHis1(String bssd, String irCurveId){
		String query = "select a from IrCurveHis a "
				+ "		where 1=1 "
				+ "		and a.irCurveId =:irCurveId "
				+ "		and a.baseDate  = :bssd	"
				+ "     order by a.matCd"
				;
		
		List<IIntRate> curveRst =  session.createQuery(query, IIntRate.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("bssd", getMaxBaseDate(bssd, irCurveId))
				.getResultList();
		
//		logger.info("maxDate : {}, curveSize : {}", getMaxBaseDate(bssd, irCurveId),curveRst.size());
		return curveRst;
	}
	
	public static List<IrCurveHis> getIrCurveHis(String bssd, String irCurveId){
		String query = "select a from IrCurveHis a "
				+ "		where 1=1 "
				+ "		and a.irCurveId =:irCurveId "
				+ "		and a.baseDate  = :bssd	"
				+ "     order by a.matCd"
				;
		
		List<IrCurveHis> curveRst =  session.createQuery(query, IrCurveHis.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("bssd", getMaxBaseDate(bssd, irCurveId))
				.getResultList();
		
//		logger.info("maxDate : {}, curveSize : {}", getMaxBaseDate(bssd, irCurveId),curveRst.size());
		return curveRst;
	}
	
	public static List<IrCurveHis> getIrCurveHisByMaturityHis(String bssd, int monthNum, String irCurveId,String matCd){
		String query = "select a from IrCurveHis a "
				+ "		where 1=1 "
				+ "		and a.irCurveId =:param1"
				+ "		and a.baseDate >=:stBssd"
				+ "		and a.baseDate <=:bssd"
				+ "		and a.matCd =:param2 ";
				;
		
		return   session.createQuery(query, IrCurveHis.class)
				.setParameter("param1", irCurveId)
				.setParameter("stBssd", FinUtils.addMonth(bssd, monthNum))
				.setParameter("bssd", bssd)
				.setParameter("param2", matCd)				
				.getResultList();
	}
	
	/** 
	*  <p> KRW 국고채의 특정 만기코드의 과거 금리이력 정보 추출 (전체) 
	*  @param bssd 	   기준년월
	*  @param matCd1   만기코드 1
	*  @param matCd2   만기코드 2
	*  @return		   특정 만기코드의 과거 이력                  
	*/
	public static List<IrCurveHis> getKTBMaturityHis(String bssd, String matCds){
//	public static List<IrCurveHis> getKTBMaturityHis(String bssd, String matCd1, String matCd2){
		String matCd1 = matCds.split(",")[0].trim();
		String matCd2 ="";
		if(matCds.split(",").length==2) {
			matCd2 =matCds.split(",")[1].trim();
		}
		
		String query = "select new com.gof.entity.IrCurveHis (substr(a.baseDate,1,6), a.matCd, avg(a.intRate)) "
				+ "		from IrCurveHis a "
				+ "		where 1=1 "
				+ "		and a.baseDate <= :bssd	"
				+ "		and a.irCurveId =:param1 "
				+ "     and a.matCd in (:param2, :param3) "
				+ "		group by substr(a.baseDate,1,6), a.matCd "
				;
		
		List<IrCurveHis> curveRst =  session.createQuery(query, IrCurveHis.class)
				.setParameter("param1", "A100")
				.setParameter("param2", matCd1)
				.setParameter("param3", matCd2)
				.setParameter("bssd", FinUtils.addMonth(bssd, 1))
				.getResultList();		
		return curveRst;
	}
	
	public static List<IrCurveHis> getKTBMaturityHis(String bssd, String matCd1, String matCd2){
			
			String query = "select new com.gof.entity.IrCurveHis (substr(a.baseDate,1,6), a.matCd, avg(a.intRate)) "
					+ "		from IrCurveHis a "
					+ "		where 1=1 "
					+ "		and a.baseDate <= :bssd	"
					+ "		and a.irCurveId =:param1 "
					+ "     and a.matCd in (:param2, :param3) "
					+ "		group by substr(a.baseDate,1,6), a.matCd "
					;
			
			List<IrCurveHis> curveRst =  session.createQuery(query, IrCurveHis.class)
					.setParameter("param1", "A100")
					.setParameter("param2", matCd1)
					.setParameter("param3", matCd2)
					.setParameter("bssd", FinUtils.addMonth(bssd, 1))
					.getResultList();		
			return curveRst;
	}
	
	/** 
	*  <p> 입력한 금리곡선의 과거 금리이력 정보 추출 
	*  @param bssd 	   기준년월
	*  @param stBssd  시작년월
	*  @param curveId   금리곡선 ID
	*  @return		   금리기간구조                  
	*/
	public static List<IrCurveHis> getCurveHisBetween(String bssd, String stBssd,  String curveId){
		String query = "select a from IrCurveHis a "
				+ "		where 1=1 "
				+ "		and a.baseDate <= :bssd	"
				+ "		and a.baseDate >= :stBssd "
				+ "		and a.irCurveId =:param1 "
//				+ "		and a.matCd not in (:matCd1, :matCd2, :matCd3) "
				+ "     order by a.baseDate"
				;
		
		List<IrCurveHis> curveRst =  session.createQuery(query, IrCurveHis.class)
				.setParameter("param1", curveId)
				.setParameter("bssd", FinUtils.addMonth(bssd, 1))
				.setParameter("stBssd", stBssd)
//				.setParameter("matCd1", "M0018")
//				.setParameter("matCd2", "M0030")
//				.setParameter("matCd3", "M0048")
				.getResultList();		
		
//		Map<String, Map<String, IrCurveHis>> curveMap = curveRst.stream().collect(Collectors.groupingBy(s -> s.getMatCd()
//				, Collectors.toMap(s-> s.getBaseYymm(), Function.identity(), (s,u)->u)));
//		curveMap.entrySet().forEach(s -> logger.info("aaa : {},{},{}", s.getKey(), s.getValue()));
		return curveRst;
	}
	
	/** 
	*  <p> 국고채와 산금채의 스프레드 비율 정보 추출 
	*  @param bssd 	   기준년월
	*  @param stBssd  시작년월
	*  @return		   스프레드 비율                   
	*/
	public static List<IrCurveHis> getLiquidSpread(String bssd, String stBssd){
		
		String query = " SELECT X.BASE_DATE, X.MAT_CD, SUM(E110)/SUM(A100) "
				+ "		from (	SELECT a.Base_DATE, a.MAT_CD, decode(a.IR_CURVE_ID, :param1, a.INT_RATE, 0) AS A100, decode(a.IR_CURVE_ID, :param2, a.INT_RATE, 0)  AS E110"
				+ "				from ESG.EAS_IR_CURVE_HIS a "
				+ "				where 1=1 "
				+ "				and a.Base_DATE <= :bssd	"
				+ "				and a.Base_DATE >= :stBssd "
				+ "				and a.IR_CURVE_ID in ( :param1 , :param2) "
				+ "			 ) X"
				+ "		group by X.BASE_DATE, X.MAT_CD"
				;
		
		List<IrCurveHis> curveRst = new ArrayList<IrCurveHis>();
		
		List<Object[]> curveTemp =  session.createNativeQuery(query)
				.setParameter("param1", "A100")
				.setParameter("param2", "E110")
				.setParameter("bssd", FinUtils.addMonth(bssd, 1))
				.setParameter("stBssd", stBssd)
				.getResultList();
		logger.info("size : {}" ,curveRst.size());
		for(Object[] aa :curveTemp) {
			curveRst.add(new IrCurveHis(aa[0].toString(), aa[1].toString(), Double.parseDouble(aa[2].toString())));
		}
		curveRst.forEach(s -> logger.info("zzz : {},{},{}", s.getBaseDate(), s.getMatCd(), s.getIntRate()));
		return curveRst;
		
	}
	
	/** 
	*  <p> 기준일자별 금리기간구조의 List 를 추출함. DNS 모형과 같이 Term Structure 가 요구되는 업무에 활용됨  
	*  @param bssd 	   기준년월
	*  @param stBssd  시작년월
	*  @param irCurveId  금리곡선 ID
	*  @return		  기준일자별 금리기간구조                   
	*/
	public static Map<String, List<IrCurveHis>> getIrCurveListTermStructure(String bssd, String stBssd, String irCurveId){
		String query =" select a from IrCurveHis a " 
					+ " where a.irCurveId =:irCurveId "			
					+ "	and a.baseDate >= :stBssd "
					+ "	and a.baseDate <= :bssd "
					+ "	and a.matCd in (:matCdList)"
					+ " order by a.baseDate, a.matCd "
					;
		
		return session.createQuery(query, IrCurveHis.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("stBssd", stBssd)
				.setParameter("bssd", FinUtils.toEndOfMonth(bssd))
				.setParameterList("matCdList", EBaseMatCd.names())
				.stream()
//				.collect(Collectors.groupingBy(s ->s.getBaseDate(), TreeMap::new, Collectors.toList()))
				.collect(Collectors.groupingBy(s ->s.getMatCd(), TreeMap::new, Collectors.toList()))
				;
	}
	
//	public static List<IrSce> getIrCurveSce(String bssd, String irCurveId, String sceNo){
//		String query =" select a from IrSce a " 
//				+ " where a.irCurveId =:irCurveId "			
//				+ "	and a.baseDate = :bssd "
//				+ "	and a.sceNo = :sceNo "
//				;
//		
//		return session.createQuery(query, IrSce.class)
//				.setParameter("irCurveId", irCurveId)
//				.setParameter("bssd", bssd)
//				.setParameter("sceNo", sceNo)
//				.getResultList()
//				;	
//	}
	
//	public static Stream<IrSce> getIrCurveSce(String bssd, String irCurveId){
//		String query =" select a from IrSce a " 
//				+ " where a.irCurveId =:irCurveId "			
//				+ "	and a.baseDate = :bssd "
//				;
//		
//		return session.createQuery(query, IrSce.class)
//				.setParameter("irCurveId", irCurveId)
//				.setParameter("bssd", bssd)
//				.getResultStream()
//				;
//	}
}
