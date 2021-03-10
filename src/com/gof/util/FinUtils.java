package com.gof.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.renjin.sexp.SEXP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.dao.IrCurveHisDao;
import com.gof.dao.SmithWilsonDao;
import com.gof.entity.BottomupDcnt;
import com.gof.entity.DcntSce;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrSce;
import com.gof.entity.SmithWilsonParam;
import com.gof.entity.SmithWilsonResult;
import com.gof.enums.ECompound;
import com.gof.interfaces.BaseValue;
import com.gof.interfaces.IIntRate;
import com.gof.model.SmithWilsonModel;

public class FinUtils {
	
	private final static Logger logger = LoggerFactory.getLogger("FinUtil");
	
	public static String toEndOfMonth(String baseDate) {
		
		if(baseDate.length()==4) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4)), 12, 1).with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		}
		else if(baseDate.length()==6) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4)), Integer.valueOf(baseDate.substring(4,6)), 1).with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		}
		else if(baseDate.length()==8) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4))
										, Integer.valueOf(baseDate.substring(4,6))
											, Integer.valueOf(baseDate.substring(6,8))).with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		}
		else {
			logger.error("Convert Date Error : {} is not date format", baseDate);
		}
		return null;
	}

	public static String addMonth(String baseDate, int monNum) {
		
		if(baseDate.length()==4) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4)), 1, 1).plusMonths(monNum).format(DateTimeFormatter.ofPattern("yyyy"));
		}
		else if(baseDate.length()==6) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4)), Integer.valueOf(baseDate.substring(4,6)), 1).plusMonths(monNum).format(DateTimeFormatter.ofPattern("yyyyMM"));
		}
		else if(baseDate.length()==8) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4))
										, Integer.valueOf(baseDate.substring(4,6))
											, Integer.valueOf(baseDate.substring(6,8))).plusMonths(monNum).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		}
		else {
			logger.error("Convert Date Error : {} is not date format", baseDate);
		}
		return null;
	}
	
	public static int monthBetween(String bssd, String compareDate) {
		String baseBssd = bssd.substring(0,6) + "01";
		String otherBssd = compareDate.substring(0, 6) +"01";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		
		return (int)ChronoUnit.MONTHS.between(LocalDate.parse(baseBssd, formatter), LocalDate.parse(otherBssd, formatter));
		
	}
	
	public static SmithWilsonResult convertFromIrCurveHis(String bssd, IrCurveHis curveHis) {
		long mothsBetween = monthBetween(bssd, curveHis.getBaseDate());

		SmithWilsonResult rst = new SmithWilsonResult();
		rst.setTimeFactor((double)mothsBetween/12);
		rst.setMonthNum(Integer.parseInt(curveHis.getMatCd().split("M")[1]));
		rst.setSpotAnnual(curveHis.getIntRate());
		rst.setFwdMonthNum(Math.toIntExact(mothsBetween));
		
		return rst;
		
	}
	
	// Forwarding ¿« Base Logic ¿”.	
	public static double getForwardRate(IrCurveHis nearCurve, IrCurveHis farCurve) {
	
		int nearNum  = Integer.valueOf(nearCurve.getMatCd().substring(1)) ;
		int farNum  = Integer.valueOf(farCurve.getMatCd().substring(1)) ;
		
//		String matCd = "M" + String.format("%04d", farNum-nearNum);
		
		double nearIrRate = nearCurve.getIntRate();
		double farIrRate = farCurve.getIntRate();
		
		
		LocalDate asOfDate =  FinUtils.convertToDate(nearCurve.getBaseDate());
		
		LocalDate nearDate =  asOfDate.plusMonths(nearNum);
		LocalDate farDate  =  asOfDate.plusMonths(farNum);
		
		double fwdDf = ECompound.Monthly.getDf(farIrRate, asOfDate, farDate)  / ECompound.Monthly.getDf(nearIrRate, asOfDate, nearDate);    
		
//		logger.info("FwdDf  :  {},{},{}", ECompound.Monthly.getDf(farIrRate, asOfDate, farDate) , ECompound.Monthly.getDf(nearIrRate, asOfDate, nearDate)
//					,ECompound.Monthly.getDf(farIrRate, asOfDate, farDate) / ECompound.Monthly.getDf(nearIrRate, asOfDate, nearDate));
		
		return  ECompound.Monthly.getIntRateFromDf(nearDate, farDate, fwdDf);
	}
	
	
	
	public static double getForwardRate(Map<String, IrCurveHis> curveMap, String matCd, int forwardMonNum) {
		double rst =0.0;
		
//		int nearMatCd  = Integer.valueOf(matCd.substring(1))  ;
//		int farMatCd  = Integer.valueOf(matCd.substring(1)) + forwardMonNum ;
		
		int matNum  = Integer.valueOf(matCd.substring(1)) ;
		int farNum  = Integer.valueOf(matCd.substring(1)) + forwardMonNum ;
		
		String nearMatCd = "M" + String.format("%04d", forwardMonNum);
		String farMatCd =  "M" + String.format("%04d", Integer.valueOf(matCd.substring(1)) + forwardMonNum);
		
		logger.info("finUitl : {},{}, {}", nearMatCd, farMatCd);
		
		double farIrRate = curveMap.get(farMatCd).getIntRate();
		double nearIrRate = curveMap.get(nearMatCd).getIntRate();
		LocalDate asOfDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
		LocalDate nearDate =  asOfDate.plusMonths(forwardMonNum);
		LocalDate farDate =  asOfDate.plusMonths(farNum);
		
		double fwdDf = ECompound.Monthly.getDf(farIrRate, asOfDate, farDate)  / ECompound.Monthly.getDf(nearIrRate, asOfDate, nearDate);    
			
		return fwdDf;
	}
	
	
	
	public static double getForwardRateForPV(Map<String, IrCurveHis> curveMap, String matCd, int forwardMonNum) {
		Map<String, IrCurveHis> fullCurveMap = getLinearInterpolationCurve(curveMap);
		
		String farMatCd   =  "M" + String.format("%04d", Math.min(1200, Integer.valueOf(matCd.substring(1)) + forwardMonNum)); 
		
		return getForwardRate(fullCurveMap.get(matCd), fullCurveMap.get(farMatCd));
	}
	
	public static Map<String, Double> getForwardRateForPV(Map<String, IrCurveHis> curveMap) {
		Map<String, Double> fwdMap = new HashMap<>();
		
		int farMonNum ;
		int nearMonNum;
		double fwdRate =0.0;
		double farDf =0.0;
		double nearDf = 0.0;
		
		for(Map.Entry<String, IrCurveHis> entry: curveMap.entrySet()) {
			farMonNum =  Integer.valueOf(entry.getKey().substring(1));
			nearMonNum = farMonNum -1;
			
			if(entry.getKey().equals("M0001")) {
				fwdRate = entry.getValue().getIntRate();
			}
			else {
				String nearMatCd   =  "M" + String.format("%04d", nearMonNum);
				farDf = Math.pow((1+ entry.getValue().getIntRate() / 12), -1 * farMonNum );
				nearDf = Math.pow((1+ curveMap.get(nearMatCd).getIntRate() / 12), -1 * nearMonNum );
				
				fwdRate = 12 *( Math.pow(farDf/nearDf , -1) -1) ;
			}
			
			fwdMap.put(entry.getKey(), fwdRate);
		}
		return fwdMap;
	}
	
	public static double getForwardRateForTS(Map<String, IrCurveHis> curveMap, String matCd, int forwardMonNum) {
		Map<String, IrCurveHis> fullCurveMap = getLinearInterpolationCurve(curveMap);
		
		String nearMatCd  =  "M" + String.format("%04d", forwardMonNum );
		String farMatCd   =  "M" + String.format("%04d", Integer.valueOf(matCd.substring(1)) + forwardMonNum ); 
		
		return getForwardRate(fullCurveMap.get(nearMatCd), fullCurveMap.get(farMatCd));
	}
	
	
	
	/*public static List<SmithWilsonResult> getForwardRateByMaturity(Map<String, IrCurveHis> curveMap, String matCd) {
		List<SmithWilsonResult> rstList = new ArrayList<SmithWilsonResult>();
		SmithWilsonResult temp;
		double intRate =0.0;
		
		int matNum  = Integer.valueOf(matCd.substring(1)) ;
		
		int farNum  ; 
		
		for(int i =0; i<1200; i++) {
			farNum = matNum + i;
			String farMatCd  =  "M" + String.format("%04d", farNum);
			String nearMatCd =  "M" + String.format("%04d", i);
			
			double nearIntFactor = Math.pow(1+ curveMap.get(nearMatCd).getIntRate(), i/12);
			double farIntFactor  = Math.pow(1+ curveMap.get(farMatCd).getIntRate(), farNum/12);
			
			double intFactor = farIntFactor / nearIntFactor;
			
			intRate= Math.pow(intFactor, 12/matNum) - 1;
			
			temp = new SmithWilsonResult();
//			temp.setBaseDate(baseDate);
			temp.setSpotAnnual(intRate);
			temp.setFwdMonthNum(i);
			
			rstList.add(temp);
		}
			
		return rstList;
	}*/
	
	
	/*public static List<BottomupDcnt> getForwardRateByMaturity1(String bssd, List<BottomupDcnt> curveList, String matCd) {
		Map<String, BottomupDcnt> curveMap = curveList.stream().collect(Collectors.toMap(s->s.getMatCd(), Function.identity()));
		return getForwardRateByMaturity(bssd, curveMap, matCd);
	}*/
	
	public static List<IrCurveHis> getForwardRateByMaturity(String bssd, List<IrCurveHis> curveList, String matCd) {
		Map<String, IrCurveHis> curveMap = curveList.stream().collect(Collectors.toMap(s->s.getMatCd(), Function.identity()));
		return getForwardRateByMaturity(bssd, curveMap, matCd);
	} 
	
	
	
	
	public static Map<String, Double> getForwardRateByMaturityZZ(String bssd, Map<String, Double> curveMap, String matCd) {
		Map<String, Double> rstMap = new HashMap<String, Double>();
		
		double intRate =0.0;
		double nearIntFactor =0.0;
		double farIntFactor  =0.0;
		double intFactor  =0.0;
		int matNum  = Integer.valueOf(matCd.substring(1)) ;
		int farNum  ; 
		
		for(int i =1; i<=1200; i++) {
			farNum = matNum + i;
			String nearMatCd =  "M" + String.format("%04d", i);
			String farMatCd  =  "M" + String.format("%04d", farNum);
			
			if(!curveMap.containsKey(nearMatCd)) {
				return rstMap;
			}
			else {
				nearIntFactor = Math.pow(1+ curveMap.get(nearMatCd), i/12.0);
				
				if(curveMap.containsKey(farMatCd)) {
					farIntFactor  = Math.pow(1+ curveMap.get(farMatCd), farNum/12.0);
					
					intFactor = nearIntFactor==0.0? farIntFactor: farIntFactor / nearIntFactor;
					
					intRate= Math.pow(intFactor, 12.0/matNum) - 1.0 ;
				}
				else {
//					intRate = curveMap.get(nearMatCd);
				}
				
				rstMap.put(FinUtils.addMonth(bssd, i), intRate );
			}
		}
			
		return rstMap;
	}
	
	public static Map<String, Double> getForwardRateByMaturityMatCd(String bssd, Map<String, Double> curveMap, String matCd) {
		Map<String, Double> rstMap = new HashMap<String, Double>();
		
		double intRate =0.0;
		double nearIntFactor =0.0;
		double farIntFactor  =0.0;
		double intFactor  =0.0;
		int matNum  = Integer.valueOf(matCd.substring(1)) ;
		int farNum  ; 
		
		for(int i =1; i<=1200; i++) {
			farNum = matNum + i;
			String nearMatCd =  "M" + String.format("%04d", i);
			String farMatCd  =  "M" + String.format("%04d", farNum);
			
			if(!curveMap.containsKey(nearMatCd)) {
				return rstMap;
			}
			else {
				nearIntFactor = Math.pow(1+ curveMap.getOrDefault(nearMatCd,0.0), i/12.0);
				
				if(curveMap.containsKey(farMatCd)) {
					farIntFactor  = Math.pow(1+ curveMap.get(farMatCd), farNum/12.0);
					
					intFactor = nearIntFactor==0.0? farIntFactor: farIntFactor / nearIntFactor;
					
					intRate= Math.pow(intFactor, 12.0/matNum) - 1.0 ;
				}
				else {
//					intRate = curveMap.get(nearMatCd);
				}
				
				rstMap.put(nearMatCd, intRate );
			}
		}
			
		return rstMap;
	}
	public static List<IrCurveHis> getForwardRateByMaturity(String bssd, Map<String, IrCurveHis> curveMap, String matCd) {
		List<IrCurveHis> rstList = new ArrayList<IrCurveHis>();
		IrCurveHis temp;
		double intRate =0.0;
		double nearIntFactor =0.0;
		double farIntFactor  =0.0;
		double intFactor  =0.0;
		int matNum  = Integer.valueOf(matCd.substring(1)) ;
		int farNum  ; 
		
		for(int i =1; i<=1200; i++) {
			farNum = matNum + i;
			String nearMatCd =  "M" + String.format("%04d", i);
			String farMatCd  =  "M" + String.format("%04d", farNum);
			
			if(!curveMap.containsKey(nearMatCd)) {
				return rstList;
			}
			else {
				nearIntFactor = Math.pow(1+ curveMap.get(nearMatCd).getIntRate(), i/12.0);
				
				if(curveMap.containsKey(farMatCd)) {
					farIntFactor  = Math.pow(1+ curveMap.get(farMatCd).getIntRate(), farNum/12.0);
					
					intFactor = nearIntFactor==0.0? farIntFactor: farIntFactor / nearIntFactor;
					
					intRate= Math.pow(intFactor, 12.0/matNum) - 1.0 ;
				}
				else {
					intRate = curveMap.get(nearMatCd).getIntRate();
				}
//			logger.info("aaaa : {},{},{},{},{}", curveMap.get(nearMatCd).getIntRate(), curveMap.get(farMatCd).getIntRate(), intFactor, intRate);
				
				temp = new IrCurveHis();
				
				temp.setBaseDate(bssd);
				temp.setMatCd(matCd);
				temp.setIntRate(intRate);
				temp.setForwardNum(i);
				
				rstList.add(temp);
			}
		}
			
		return rstList;
	}
	
	
	public static List<IrSce> getForwardRateByMaturity2(String bssd, Map<String, IrCurveHis> curveMap, String matCd) {
		List<IrSce> rstList = new ArrayList<IrSce>();
		IrSce temp;
		double intRate =0.0;
		double nearIntFactor =0.0;
		double farIntFactor  =0.0;
		double intFactor  =0.0;
		int matNum  = Integer.valueOf(matCd.substring(1)) ;
		int farNum  ; 
		
		for(int i =1; i<=1200; i++) {
			farNum = matNum + i;
			String nearMatCd =  "M" + String.format("%04d", i);
			String farMatCd  =  "M" + String.format("%04d", farNum);
			
			if(!curveMap.containsKey(nearMatCd)) {
				return rstList;
			}
			else {
				nearIntFactor = Math.pow(1+ curveMap.get(nearMatCd).getIntRate(), i/12.0);
				
				if(curveMap.containsKey(farMatCd)) {
					farIntFactor  = Math.pow(1+ curveMap.get(farMatCd).getIntRate(), farNum/12.0);
					
					intFactor = nearIntFactor==0.0? farIntFactor: farIntFactor / nearIntFactor;
					
					intRate= Math.pow(intFactor, 12.0/matNum) - 1.0 ;
				}
				else {
					intRate = curveMap.get(nearMatCd).getIntRate();
				}
//			logger.info("aaaa : {},{},{},{},{}", curveMap.get(nearMatCd).getIntRate(), curveMap.get(farMatCd).getIntRate(), intFactor, intRate);
				
				temp = new IrSce();
				
				temp.setBaseDate(FinUtils.addMonth(bssd, i));
				temp.setSceNo("0");
				temp.setMatCd(matCd);
				temp.setRfIr(intRate);
				
				rstList.add(temp);
			}
		}
			
		return rstList;
	}
	
	public static List<DcntSce> getForwardRateByMaturity1(String bssd, Map<String, BottomupDcnt> curveMap, String matCd, boolean isRiskFree) {
		List<DcntSce> rstList = new ArrayList<DcntSce>();
		DcntSce temp;
		
		double intRate =0.0;
		double nearIntFactor =0.0;
		double farIntFactor  =0.0;
		double intFactor  =0.0;
		int matNum  = Integer.valueOf(matCd.substring(1)) ;
		int farNum  ; 
		
		for(int i =1; i<=1200; i++) {
			farNum = matNum + i;
			String nearMatCd =  "M" + String.format("%04d", i);
			String farMatCd  =  "M" + String.format("%04d", farNum);
			
			if(!curveMap.containsKey(nearMatCd)) {
				return rstList;
			}
			else {
				if(isRiskFree) {
					nearIntFactor = Math.pow(1+ curveMap.get(nearMatCd).getRfRate(), i/12.0);
				}
				else {
					nearIntFactor = Math.pow(1+ curveMap.get(nearMatCd).getRiskAdjRfRate(), i/12.0);
				}
				
				if(curveMap.containsKey(farMatCd)) {
					if(isRiskFree) {
						farIntFactor  = Math.pow(1+ curveMap.get(farMatCd).getRfRate(), farNum/12.0);
					}
					else {
						farIntFactor  = Math.pow(1+ curveMap.get(farMatCd).getRiskAdjRfRate(), farNum/12.0);
					}
					
					intFactor = nearIntFactor==0.0? farIntFactor: farIntFactor / nearIntFactor;
					
					intRate= Math.pow(intFactor, 12.0/matNum) - 1.0 ;
				}
				else {
					if(isRiskFree) {
						intRate = curveMap.get(nearMatCd).getRfRate();
					}
					else {
						intRate = curveMap.get(nearMatCd).getRiskAdjRfRate();
					}
				}
//			logger.info("aaaa : {},{},{},{},{}", curveMap.get(nearMatCd).getIntRate(), curveMap.get(farMatCd).getIntRate(), intFactor, intRate);
				
				temp = new DcntSce();
				
				temp.setBaseYymm(FinUtils.addMonth(bssd, i));
				temp.setSceNo("0");
				temp.setMatCd(matCd);
				temp.setRiskAdjRfRate(intRate); 
				
				rstList.add(temp);
			}
		}
			
		return rstList;
	}
	
	
	public static List<DcntSce> getForwardRateByMaturity3(String bssd, Map<String, IIntRate> curveMap, String matCd, boolean isRiskFree) {
		List<DcntSce> rstList = new ArrayList<DcntSce>();
		DcntSce temp;
		
		double intRate =0.0;
		double nearIntFactor =0.0;
		double farIntFactor  =0.0;
		double intFactor  =0.0;
		int matNum  = Integer.valueOf(matCd.substring(1)) ;
		int farNum  ; 
		
		for(int i =1; i<=1200; i++) {
			farNum = matNum + i;
			String nearMatCd =  "M" + String.format("%04d", i);
			String farMatCd  =  "M" + String.format("%04d", farNum);
			
			if(!curveMap.containsKey(nearMatCd)) {
				return rstList;
			}
			else {
				if(isRiskFree) {
					nearIntFactor = Math.pow(1+ curveMap.get(nearMatCd).getIntRate(), i/12.0);
				}
				else {
					nearIntFactor = Math.pow(1+ curveMap.get(nearMatCd).getIntRate(), i/12.0);
				}
				
				if(curveMap.containsKey(farMatCd)) {
					if(isRiskFree) {
						farIntFactor  = Math.pow(1+ curveMap.get(farMatCd).getIntRate(), farNum/12.0);
					}
					else {
						farIntFactor  = Math.pow(1+ curveMap.get(farMatCd).getIntRate(), farNum/12.0);
					}
					
					intFactor = nearIntFactor==0.0? farIntFactor: farIntFactor / nearIntFactor;
					
					intRate= Math.pow(intFactor, 12.0/matNum) - 1.0 ;
				}
				else {
					if(isRiskFree) {
						intRate = curveMap.get(nearMatCd).getIntRate();
					}
					else {
						intRate = curveMap.get(nearMatCd).getIntRate();
					}
				}
//			logger.info("aaaa : {},{},{},{},{}", curveMap.get(nearMatCd).getIntRate(), curveMap.get(farMatCd).getIntRate(), intFactor, intRate);
				
				temp = new DcntSce();
				
				temp.setBaseYymm(FinUtils.addMonth(bssd, i));
				temp.setSceNo("0");
				temp.setMatCd(matCd);
				temp.setRiskAdjRfRate(intRate); 
				
				rstList.add(temp);
			}
		}
			
		return rstList;
	}
	
	public static Map<String, IrCurveHis> getLinearInterpolationCurve(Map<String, IrCurveHis> curveMap) {
		double rst =0.0;
		
		Map<String, IrCurveHis> rstMap = new HashMap<String, IrCurveHis>();
		IrCurveHis baseIrCurvHis = new IrCurveHis();
		
		if(curveMap.values().stream().findFirst().isPresent()) {
			baseIrCurvHis = curveMap.values().stream().findFirst().get();
//			logger.info("Interpol : {},{},{}", baseIrCurvHis.getIrCurveId(), baseIrCurvHis.getMatCd(), baseIrCurvHis.getBaseDate());
		}
//		logger.info("Interpol : {},{},{}", baseIrCurvHis.getIrCurveId(), baseIrCurvHis.getMatCd(), baseIrCurvHis.getBaseDate());
		
		IrCurveHis temp;
		
		temp = new IrCurveHis();
		temp.setBaseDate(baseIrCurvHis.getBaseDate());
		temp.setIrCurveId(baseIrCurvHis.getIrCurveId());
		temp.setIrCurve(baseIrCurvHis.getIrCurve());
		temp.setMatCd("M1200");
		temp.setIntRate(0.045);
		
		curveMap.put("M1200", temp);
		
		temp = new IrCurveHis();
		temp.setBaseDate(baseIrCurvHis.getBaseDate());
		temp.setIrCurveId(baseIrCurvHis.getIrCurveId());
		temp.setIrCurve(baseIrCurvHis.getIrCurve());
		temp.setMatCd("M1201");
		temp.setIntRate(0.045);
		curveMap.put("M1201", temp);
		
		IrCurveHis leftIrCurve ;;
		IrCurveHis rightIrCurve= new IrCurveHis();
		
		int index =1;
		int leftIndex = 1;
		int rightIndex = 1;
		double tempIntRate =0.0;
		String tempMatCd ;
		

		for(int i=1 ; i<= 1201; i++) {
			
			if( curveMap.containsKey("M" + String.format("%04d", i))) {
				leftIrCurve  = rightIrCurve;
				rightIrCurve = curveMap.get("M" + String.format("%04d", i));
				
				if(leftIrCurve.getMatCd()==null) {
					continue;
				}	
				else {
//					logger.info("Interpol : {},{}", leftIrCurve.getIrCurveId(), leftIrCurve.getMatCd());
					leftIndex = Integer.valueOf(leftIrCurve.getMatCd().substring(1));
				}
				
				rightIndex = Integer.valueOf(rightIrCurve.getMatCd().substring(1));
				 
				for(int j = Math.min(index,leftIndex); j < rightIndex; j++) {
					tempIntRate = leftIrCurve.getIntRate() * (rightIndex - j) / (rightIndex - leftIndex)
							+ rightIrCurve.getIntRate() * ( j - leftIndex ) /(rightIndex - leftIndex) ;
					
					tempMatCd = "M" + String.format("%04d", j);

					temp = new IrCurveHis();
					temp.setBaseDate(baseIrCurvHis.getBaseDate());
					temp.setIrCurveId(baseIrCurvHis.getIrCurveId());
					temp.setIrCurve(baseIrCurvHis.getIrCurve());
					temp.setMatCd(tempMatCd);
					temp.setIntRate(tempIntRate);
					
					rstMap.put(tempMatCd, temp);
				}
				index = rightIndex;
			}
		}

		return rstMap;
	}

	public static <E extends BaseValue> Map<String, Double> getVolMap( Map<String, List<E>> rstMap){
		Map<String, Double> rstVol = new HashMap<>();
		double tempSum =0.0;
		double tempSqSum =0.0;
		double tempvol =0.0;
		int cnt = 0;
	
		for(Map.Entry<String, List<E>> entry : rstMap.entrySet()) {
			for(E aa : entry.getValue()) {
				tempSum = tempSum + aa.getBasicValue();
				tempSqSum = tempSqSum + aa.getBasicValue() * aa.getBasicValue();
//                logger.info("Sum : {},{}", tempSum, tempSqSum);
			}
			cnt = entry.getValue().size();
			if(cnt <=1) {
				tempvol =  0.0;	
			}
			else {
				tempvol =  tempSqSum / ( cnt ) -  Math.pow((tempSum/cnt),2); 
			}
//			logger.info("aaaa : {},{}", tempSqSum /3 , Math.pow((tempSum/cnt),2) );
			rstVol.put(entry.getKey(), tempvol< 0? 0.0: Math.sqrt(tempvol));
			
			tempSqSum =0.0;
			tempSum =0.0;
		}
		return rstVol;
	}
	
	
	private static LocalDate convertToDate(String baseDate) {

		if(baseDate.length()==4) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4)), 1, 1);
		}
		else if(baseDate.length()==6) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4)), Integer.valueOf(baseDate.substring(4,6)), 1);
		}
		else if(baseDate.length()==8) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4)), Integer.valueOf(baseDate.substring(4,6)), Integer.valueOf(baseDate.substring(6,8)));
		}
		else {
			logger.error("Convert Date Error : {} is not date format", baseDate);
		}
		return null;
	}
	
	public static List<IrCurveHis> spanFullBucket(String bssd, List<IrCurveHis> curveRst){
		List<IrCurveHis> rstList = new ArrayList<IrCurveHis>();
		if(curveRst.isEmpty()) {
			logger.warn("Curve His Data Error :  His Data of {} is not found at {} ", curveRst ,bssd);
			return curveRst;
		}
		
		IrCurveHis firstIrCurveHis = curveRst.get(0);
//		List<IrCurve> curveMstList = DaoUtil.getEntities(IrCurve.class, new HashMap<String, Object>());
		
		List<IrCurve> curveMstList = IrCurveHisDao.getIrCurveByCrdGrdCd("000");
//		logger.info("size : {}", curveMstList.size());
		String curCd ="";
		for(IrCurve aa : curveMstList) {
			if( aa.getIrCurveId().equals(firstIrCurveHis.getIrCurveId())) {
				curCd = aa.getCurCd(); 
			}
		}
		
		List<SmithWilsonParam> swParam = SmithWilsonDao.getParamList();
		Map<String, SmithWilsonParam> swParamMap = swParam.stream().collect(Collectors.toMap(s ->s.getCurCd(), Function.identity()));
		
		double ufr  = swParamMap.containsKey(curCd) ? 0.045: swParamMap.get(curCd).getUfr();
		double ufrt = swParamMap.containsKey(curCd) ? 60   : swParamMap.get(curCd).getUfrT();
		
		SmithWilsonModel rf      = new SmithWilsonModel(curveRst, ufr, ufrt);
		SEXP rfRst      = rf.getSmithWilsonSEXP(false).getElementAsSEXP(0);			// Spot:  [Time , Month_Seq, spot, spot_annu, df, fwd, fwd_annu] , Forward Matrix: 
		
		for(int i =0; i< 1200; i++) {
			IrCurveHis temp = new IrCurveHis();
			
			temp.setBaseDate(bssd);
			temp.setIrCurveId(firstIrCurveHis.getIrCurveId());
			
			temp.setMatCd("M" + String.format("%04d", i+1));
			temp.setIntRate(rfRst.getElementAsSEXP(3).getElementAsSEXP(i).asReal());
			
			temp.setForwardNum(firstIrCurveHis.getForwardNum());
			temp.setSceNo("0");
			
			rstList.add(temp);
			
		}
		return rstList;
	}

	
	
}
