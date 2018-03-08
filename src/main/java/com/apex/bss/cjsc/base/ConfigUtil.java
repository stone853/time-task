package com.apex.bss.cjsc.base;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class ConfigUtil
{
	
	private static Logger logger = Logger.getLogger(ConfigUtil.class);
	
	private static Map items = new HashMap();
	
	private static List sysList = new ArrayList();
	
	private static String CONFIG_FILE_NAME = "configuration.xml";
	
//	static
//	{
//		loadConfig();
//	}

	private void initConfig(){
		loadConfig();
	}
	
	/**
	 * ???????????
	 */
	public static void loadConfig()
	{
		InputStream is = ConfigUtil.class.getResourceAsStream("/" + CONFIG_FILE_NAME);
		try
		{
			SAXReader reader = new SAXReader();
			
			Document document = reader.read(is);
			Element systemElement = document.getRootElement();
			List catList = systemElement.elements("category");
			int sysCount = 0;
			for (Iterator catIter = catList.iterator(); catIter.hasNext();)
			{
				Element catElement = (Element) catIter.next();
				String catName = catElement.attributeValue("name");
				if (catName == null || catName.length() == 0)
				{
					continue;
				}
				
				List itemList = catElement.elements("item");
				for (Iterator itemIter = itemList.iterator(); itemIter.hasNext();)
				{
					Element itemElement = (Element) itemIter.next();
					String itemName = itemElement.attributeValue("name");
					String value = itemElement.attributeValue("value");
					items.put(catName + "." + itemName, value);
					if(value.indexOf(".xml") != -1){
						loadConfig(value);
					}
					
					if(catName !=null && catName.equals("sysList")){
						sysCount ++;
					}
				}
			}
			items.put("sysCount", String.valueOf(sysCount)); //配置系统个数
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
					is = null;
				}
				catch (IOException ex)
				{
					logger.error("", ex);
				}
			}
		}
	}
	
	private static void loadConfig(String filename){
		InputStream is = ConfigUtil.class.getResourceAsStream("/xml/" + filename);
		try
		{
			SAXReader reader = new SAXReader();
			
			Document document = reader.read(is);
			Element systemElement = document.getRootElement();
			List catList = systemElement.elements("category");
			for (Iterator catIter = catList.iterator(); catIter.hasNext();)
			{
				Element catElement = (Element) catIter.next();
				String catName = catElement.attributeValue("name");
				//将涉及到的系统加入集合
				String sys = catElement.attributeValue("belong");
				if(sys !=null && sys.length() > 0){
					sysList.add(sys);
				}
				
				if (catName == null || catName.length() == 0)
				{
					continue;
				}
				
				List itemList = catElement.elements("item");
				for (Iterator itemIter = itemList.iterator(); itemIter.hasNext();)
				{
					Element itemElement = (Element) itemIter.next();
					String itemName = itemElement.attributeValue("name");
					String value = itemElement.attributeValue("value");
					if (itemName != null && itemName.length() != 0)
					{
						items.put(catName + "." + itemName, value);
					}
				}
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
					is = null;
				}
				catch (IOException ex)
				{
					logger.error("", ex);
				}
			}
		}
	}
	
	/**
	 * ???????????
	 *
	 * @param name
	 * @return
	 */
	public static String getString(String name)
	{
		String value = (String) items.get(name);
		return (value == null) ? "" : value;
	}
	
	/**
	 * ?????????????????????????
	 *
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static String getString(String name, String defaultValue)
	{
		String value = (String) items.get(name);
		if (value != null && value.length() > 0)
			return value;
		else
			return defaultValue;
	}
	
	/**
	 * ????????????
	 *
	 * @param name
	 * @return
	 */
	public static int getInt(String name)
	{
		String value = getString(name);
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException ex)
		{
			return 0;
		}
	}
	
	public static List getSysList(){
		return sysList;
	}
	
	/**
	 * ????????????
	 *
	 * @param name
	 * @return
	 */
	public static int getInt(String name, int defaultValue)
	{
		String value = getString(name);
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException ex)
		{
		}
		return defaultValue;
	}
	
	/**
	 * ??ò??????????
	 *
	 * @param name
	 * @return
	 */
	public static boolean getBoolean(String name)
	{
		String value = getString(name);
		return Boolean.valueOf(value).booleanValue();
	}
	
	public Map getItems()
	{
		return items;
	}
	
	public static void main(String args[]){
		ConfigUtil c = new ConfigUtil();
	}
}
