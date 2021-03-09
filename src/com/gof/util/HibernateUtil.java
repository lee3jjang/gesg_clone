
package com.gof.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.enums.ERunArgument;

public class HibernateUtil {
	private final static Logger logger = LoggerFactory.getLogger("Hiber");
	private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;
    
	public static SessionFactory getSessionFactory() {
		Map<String, String> settings = new HashMap<String,String>();
		return genSessionFactory(settings);
	  }

	
	public static SessionFactory getSessionFactory(Properties prop) {
		
		Map<String, String> settings = new HashMap<String,String>();
		settings.put(Environment.DRIVER, prop.getProperty("driver"));
		settings.put(Environment.URL, prop.getProperty("url"));
		settings.put(Environment.USER, prop.getProperty("username"));
		settings.put(Environment.PASS, prop.getProperty("password"));
		settings.put(Environment.DIALECT, prop.getProperty("dialect"));

		logger.info("getSesson Factory with Arg");
		return genSessionFactory(settings);
		
	  }
	
	public static void shutdown() {
	    if (registry != null) {
	      StandardServiceRegistryBuilder.destroy(registry);
	    }
	  }


	private  static SessionFactory genSessionFactory(Map<String, String> settings) {
	    if (sessionFactory == null) {
	      try {
	        // Create registry
	        registry = new StandardServiceRegistryBuilder()
			        		.configure()
	        				.applySettings(settings)
	        				.build();

	        // Create MetadataSources
	        MetadataSources sources = new MetadataSources(registry);

	        // Create Metadata
	        Metadata metadata = sources.getMetadataBuilder().build();

	        // Create SessionFactory
	        sessionFactory = metadata.getSessionFactoryBuilder().build();
	        logger.info("Generate Session : new Session is generated with {}", settings);

	      } catch (Exception e) {
	        e.printStackTrace();
	        if (registry != null) {
	        	logger.info("Generate Session : already Session is generated with {}", settings);
	          StandardServiceRegistryBuilder.destroy(registry);
	        }
	      }
	    }
	    return sessionFactory;
	  }
	 
	 
	 
	private  static SessionFactory createSessionFactory(Map<String, String> settings) {
	      try {
	        // Create registry
	        registry = new StandardServiceRegistryBuilder()
			        		.configure()
	        				.applySettings(settings)
	        				.build();
	
	        // Create MetadataSources
	        MetadataSources sources = new MetadataSources(registry);
	
	        // Create Metadata
	        Metadata metadata = sources.getMetadataBuilder().build();
	
	        // Create SessionFactory
	        sessionFactory = metadata.getSessionFactoryBuilder().build();
	
	      } catch (Exception e) {
	        e.printStackTrace();
	        if (registry != null) {
	          StandardServiceRegistryBuilder.destroy(registry);
	        }
	      }
	
	    return sessionFactory;
	 }

}
