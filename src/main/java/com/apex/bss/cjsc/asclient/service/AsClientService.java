package com.apex.bss.cjsc.asclient.service;

import cjscpool.ASClient;
import cjscpool.ASClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class AsClientService
{
	protected static final Logger log= LoggerFactory.getLogger(AsClientService.class);

	public static final String ERR_NO = "errNo";
	
	public static final String ERR_MSG = "errMsg";
	
	private Map<String,Object> errMap = null;
	
	public Map<String,Object> getErrMap()
	{
		return errMap;
	}
	
	public static void initAsClientPool(){
		ASClient client = ASClientFactory.getASClient();
		ASClientFactory.freeASClient(client);
	}
	
	public String getErrNo(){
		return null != errMap.get(ERR_NO)   ? errMap.get(ERR_NO).toString() : "-2";
	}

	public String getErrMsg(){
		return null != errMap.get(ERR_MSG) ? errMap.get(ERR_MSG).toString() : "";
	}
	
	public Map<String,Object> getSingleResult(int functionNo, Map param)
	{
		Map<String,Object> result = new HashMap<String,Object>();
		ASClient client = getClientInstance();
		try
		{
			client = invoke(client, functionNo, param);
			if (client != null)
			{
				if (client.getErrorNo() == 0)
				{
					result = getInfoBean(client);
					errMap = new HashMap<String,Object>();
				}
				else
				{
					errMap = new HashMap<String,Object>();
					errMap.put(ERR_NO, client.getErrorNo());
					errMap.put(ERR_MSG, client.getErrorMsg().replace("\"", ""));
					result.put("err_no", errMap.get(ERR_NO));
					result.put("err_msg", errMap.get(ERR_MSG));
				}
			}
			else
			{
				errMap = new HashMap<String,Object>();
				errMap.put(ERR_NO, "-98");
				errMap.put(ERR_MSG, "没有可用连接");
				result.put("err_no", errMap.get(ERR_NO));
				result.put("err_msg", errMap.get(ERR_MSG));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			errMap = new HashMap<String,Object>();
			errMap.put(ERR_NO, "-99");
			errMap.put(ERR_MSG, e.getMessage());
			result.put("err_no", errMap.get(ERR_NO));
			result.put("err_msg", errMap.get(ERR_MSG));
		}
		finally
		{
			ASClientFactory.freeASClient(client);
		}

		return result;
	}
	
	public Map<String,Object> saveClientLogger(int functionNo, Map param)
	{
		Map<String,Object> result = new HashMap<String,Object>();
		ASClient client = getClientInstance();
		try
		{
			client = invoke(client, functionNo, param);
			if (client != null)
			{
				if (client.getErrorNo() == 0)
				{
					result = getInfoBean(client);
					errMap = new HashMap<String ,Object>();
				}
				else
				{
					errMap = new HashMap<String ,Object>();
					errMap.put(ERR_NO, client.getErrorNo());
					errMap.put(ERR_MSG, client.getErrorMsg().replace("\"", ""));
				}
			}
			else
			{
				errMap = new HashMap<String ,Object>();
				errMap.put(ERR_NO, "-98");
				errMap.put(ERR_MSG, "没有可用连接");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			errMap = new HashMap<String ,Object>();
			errMap.put(ERR_NO, "-99");
			errMap.put(ERR_MSG, e.getMessage());
		}
		finally
		{
			ASClientFactory.freeASClient(client);
		}
		return result;
	}
	
	public List getResultList(int functionNo, Map param)
	{
		List result = null;
		ASClient client = getClientInstance();
		try
		{
			client = invoke(client, functionNo, param);
			if (client != null)
			{
				if (client.getErrorNo() >= 0)
				{
					result = getInfoList(client);
				}
				else
				{
					errMap = new HashMap<String ,Object>();
					errMap.put(ERR_NO, client.getErrorNo());
					errMap.put(ERR_MSG, client.getErrorMsg().replace("\"", ""));
				}
			}
			else
			{
				errMap = new HashMap<String ,Object>();
				errMap.put(ERR_NO, "-98");
				errMap.put(ERR_MSG, "没有可用连接");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			errMap = new HashMap<String ,Object>();
			errMap.put(ERR_NO, "-99");
			errMap.put(ERR_MSG, e.getMessage());
		}
		finally
		{
			ASClientFactory.freeASClient(client);
		}
		return result;
	}
	
	public Map<String,Object> getResultListMap(int functionNo, Map param)
	{
		List result = null;
		ASClient client = getClientInstance();
		try
		{
			log.debug("入参:"+param.toString());
			client = invoke(client, functionNo, param);
			if (client != null)
			{
				if (client.getErrorNo() >= 0)
				{
					result = getInfoList(client);
					errMap = new HashMap<String,Object>();
					errMap.put(ERR_NO, client.getErrorNo());
					errMap.put(ERR_MSG, client.getErrorMsg());
				}
				else
				{
					errMap = new HashMap<String,Object>();
					errMap.put(ERR_NO, client.getErrorNo());
					errMap.put(ERR_MSG, client.getErrorMsg().replace("\"", ""));
				}
			}
			else
			{
				errMap = new HashMap<String,Object>();
				errMap.put(ERR_NO, "-98");
				errMap.put(ERR_MSG, "没有可用连接");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			errMap = new HashMap<String,Object>();
			errMap.put(ERR_NO, "-99");
			errMap.put(ERR_MSG, e.getMessage());
		}
		finally
		{
			ASClientFactory.freeASClient(client);
		}
		Map<String,Object> datarow = new HashMap<String,Object>();
		datarow.put("list", result);
		datarow.put(ERR_NO, errMap.get(ERR_NO));
		datarow.put(ERR_MSG, errMap.get(ERR_MSG));
		log.debug("出参:"+datarow.toString());
		return datarow;
	}
	
	private ASClient getClientInstance()
	{
		return ASClientFactory.getASClient();
	}
	
	private ASClient invoke(ASClient client, int functionNo, Map param) throws Exception
	{
		if (client != null)
		{
			//记录日志
			
			client.setHead(10, functionNo);
			client = dealParam(client, param);
			client.sendReceive();


		}
		return client;
	}
	
	private ASClient dealParam(ASClient client, Map param)
	{
		Iterator it = param.keySet().iterator();
		int i = 0;
		while (it.hasNext())
		{
			i = i + 1;
			String key = it.next().toString();
			String value = getObjectValueStr(param.get(key));
			client.setField(key);
			client.setValue(value);
		}
		client.setRange(i);
		return client;
	}
	
	private String getObjectValueStr(Object obj)
	{
		String result = "";
		try
		{
			if(obj != null)
			{
				result = obj.toString();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	private Map<String,Object> getInfoBean(ASClient client)
	{
		Map<String,Object> result = null;
		while (client.next())
		{
			result = getSingleBean(client);
			if (result != null)
			{
				break;
			}
		}
		return result;
	}
	
	private List getInfoList(ASClient client)
	{
		List result = new ArrayList();
		while (client.next())
		{
			Map bean = getSingleBean(client);
			if (bean != null)
			{
				result.add(bean);
			}
		}
		return result;
	}
	
	private Map<String,Object> getSingleBean(ASClient client)
	{
		int size = client.getFieldNum();
		Map<String,Object> bean = null;
		if (size > 0)
		{
			bean = new HashMap<>();
		}
		for (int i = 0; i < size; i++)
		{
			short num = (short) i;
			String name = client.getFieldName(num);
			String value = getObjectValueStr(client.fieldByName(name));
			bean.put(name, value);
		}
		return bean;
	}
	
	public static void main(String[] args) {
		AsClientService.initAsClientPool();
	}
}
