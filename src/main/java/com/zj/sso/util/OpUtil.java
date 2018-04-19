package com.zj.sso.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class OpUtil {
	
	/**
	 * 获取jdbctemplate
	 * @return
	 * @throws IOException
	 */
	public static JdbcTemplate createTemplate() throws IOException{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();  
		InputStream   is   =  loader.getResourceAsStream("application.properties");
		Properties prop = new Properties();  
		prop.load(is);  	
		DriverManagerDataSource d=new DriverManagerDataSource();
		d.setDriverClassName(prop.getProperty("SqlServer.Driver"));
		d.setUrl(prop.getProperty("SqlServer.url"));
		d.setUsername(prop.getProperty("SqlServer.username"));
		d.setPassword(prop.getProperty("SqlServer.pd"));
		JdbcTemplate template=new JdbcTemplate();
		template.setDataSource(d);
		return template;
	}
	
}
