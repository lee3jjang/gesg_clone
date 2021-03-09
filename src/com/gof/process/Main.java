package com.gof.process;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

import org.hibernate.Session;

import com.gof.enums.ERunArgument;

//import com.gof.entity.IrCurve;
//import com.gof.enums.ERunArgument;

//import com.gof.dao.IrCurveHisDao;
//import com.gof.enums.ERunArgument;
//import com.gof.util.HibernateUtil;
//import com.gof.util.ParamUtil;

public class Main {
	private static Map<ERunArgument, String> argMap = new HashMap<>();
	private static String output;
	private static int batchNum = 10;
	private static double dnsErrorTolerance = 0.00001;
	private static double kicsVolAdjust = 0.0032;

	private static double hwErrorTolerance = 0.00000001;
	private static double hw2ErrorTolerance = 0.00000001;
	private static String irSceGenSmithWilsonApply = "Y";
	
	private static long cnt = 0;
	private static long totalSize = 0;

	private static String paramGroup ;
	private static String jobString;
	private static String irSceCurrencyString;
	private static String lqFittingModel;
	
	private static String bssd;
	private static int poolSize;
	private static ExecutorService exe;
	private static Session session;
	private static int flushSize = 100000; 
	
//	private static List<IrCurve> rfCurveList = new ArrayList<IrCurve>();
//	private static List<IrCurve> bottomUpList = new ArrayList<IrCurve>();
//	private static List<IrCurve> kicsList = new ArrayList<IrCurve>(); 
	private static List<String> jobList = new ArrayList<String>();
	private static Set<String> irSceCurrency = new HashSet<>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int x = 4;
		System.out.println("Hello World");
	}
	
	private static void init(String[] args) {
			
			for (String aa : args) {
				for (ERunArgument bb : ERunArgument.values()) {
					if (aa.split("=")[0].toLowerCase().contains(bb.name())) {
						argMap.put(bb, aa.split("=")[1]);
						break;
					}
				}
			}
//			
//			bssd = argMap.get(ERunArgument.time).replace("-", "").replace("/", "").substring(0, 6);
//			Properties properties = new Properties();
//			try {
//				FileInputStream fis = new FileInputStream(argMap.get(ERunArgument.properties));
//				properties.load(new BufferedInputStream(fis));
//	
//			} catch (Exception e) {
//				logger.warn("Error in Properties Loading : {}", e);
//			}
//	
//			session = HibernateUtil.getSessionFactory(properties).openSession();
//	//		Session From DB
//	//		session = HibernateUtil.getSessionFactory().getCurrentSession();
//	//		session = HibernateUtil.getSessionFactory().openSession();
//			logger.info("Session Info : {}", session.getProperties());
//			
//			paramGroup = properties.getOrDefault("paramGroup", "BASE").toString();
//			
//	//		Map<String, String> argumentMap = EsgMstDao.getEsgParam(paramGroup).stream().collect(Collectors.toMap(s->s.getParamKey(), s->s.getParamValue()));
//			Map<String, String> argumentMap = ParamUtil.getParamList(paramGroup).stream().collect(Collectors.toMap(s->s.getParamKey(), s->s.getParamValue()));
//			
//	//		batchNum, dnsErrorTolerance, dnsVolAdjust, hwErrorTolerance, hw2ErrorTolerance, IrSceCurrency, outputDir 
//			output 			  			= argumentMap.getOrDefault("outputDir", properties.get("outputDir").toString()) ;
//			lqFittingModel    			= argumentMap.getOrDefault("lqFittingModel", properties.getOrDefault("lqFittingModel", "POLY_FITTING").toString()) ;
//	//		lqFittingModel    			= argumentMap.getOrDefault("lqFittingModel", properties.getOrDefault("lqFittingModel", "SW_FITTING").toString()) ;
//			irSceGenSmithWilsonApply	= argumentMap.getOrDefault("irSceGenSmithWilsonApply", "Y") ;
//			irSceCurrencyString  		= argumentMap.getOrDefault("IrSceCurrency", properties.getOrDefault("IrSceCurrency", "KRW").toString());
//			
//			batchNum 		  = Integer.parseInt(argumentMap.getOrDefault("batchNum", properties.getOrDefault("batchNum", "10").toString()));
//			dnsErrorTolerance = Double.parseDouble(argumentMap.getOrDefault("dnsErrorTolerance", properties.getOrDefault("dnsErrorTolerance", "0.00001").toString()));
//			kicsVolAdjust     = Double.parseDouble(argumentMap.getOrDefault("kicsVolAdjust", properties.getOrDefault("kicsVolAdjust", "0.0032").toString()));
//			hwErrorTolerance  = Double.parseDouble(argumentMap.getOrDefault("hwErrorTolerance", properties.getOrDefault("hwErrorTolerance", "0.0001").toString()));
//			hw2ErrorTolerance = Double.parseDouble(argumentMap.getOrDefault("hw2ErrorTolerance", properties.getOrDefault("hw2ErrorTolerance", "0.0001").toString()));
//			
//			jobString 			 = properties.get("job").toString();
//			
//			for(Map.Entry<String, String> entry : argumentMap.entrySet()) {
//				if(entry.getKey().contains("JOB")) {
//					jobString = entry.getValue();
//				}
//			}
//			//Add for stepwise run !!!! 20190402
//			if(properties.containsKey("job_batch")) {
//				jobString 			 = properties.get("job_batch").toString();
//			}
//			
//			jobList 	 = Arrays.stream(jobString .split(",")).map(s -> s.trim()).collect(Collectors.toList());
//			irSceCurrency= Arrays.stream(irSceCurrencyString.split(",")).map(s -> s.trim()).collect(Collectors.toSet());
//			
//			logger.info("Prop :{}, {} ", bssd, properties);
//			argMap.entrySet().stream().forEach(s -> logger.info("Effective Arguments Input : {},{}", s.getKey(), s.getValue()));
//			argumentMap.entrySet().forEach(s ->logger.info("Effective Arguments in DB : {},{}", s.getKey(), s.getValue()));
//			
//			jobList.stream().forEach(s -> logger.info("Job List : {}", s));
//			
//			rfCurveList = IrCurveHisDao.getIrCurveByCrdGrdCd("000").stream().filter(s -> irSceCurrency.contains(s.getCurCd())).collect(Collectors.toList());
//			bottomUpList = IrCurveHisDao.getBottomUpIrCurve().stream().filter(s -> irSceCurrency.contains(s.getCurCd())).collect(Collectors.toList());
//			kicsList     = IrCurveHisDao.getIrCurveByGenMethod("5").stream().filter(s -> irSceCurrency.contains(s.getCurCd())).collect(Collectors.toList());	//5 : KICS
//			
//			
//	//		Hibernate Context flush Size
//			flushSize 	 = Integer.parseInt(argumentMap.getOrDefault("flushSize", properties.getOrDefault("flushSize", "100000").toString()));
//			
//	//		병렬처리 서비스 생성 
//			int maxThreadNum = Integer.parseInt(argumentMap.getOrDefault("maxThreadNum", properties.getOrDefault("maxThreadNum", "5").toString()));
//	//		poolSize = Math.min(maxThreadNum, Runtime.getRuntime().availableProcessors()) -1 ;
//			poolSize = maxThreadNum;
//	//		poolSize = 1;
//			logger.info("Number of Thread to Run in case of parallel process : {}" , poolSize);
//			
//			exe = Executors.newFixedThreadPool(poolSize, new ThreadFactory() {
//				@Override
//				public Thread newThread(Runnable r) {
//					Thread t = new Thread(r);
//					t.setDaemon(true);
//					return t;
//				}
//			});
//			
//			smithWilsonSetup();
	}

}
