package com.apex.bss.cjsc.base;

/**
 * Created by Jinshi on 2016/2/20.
 */
import org.apache.log4j.Logger;

public class SecurityHelper
{
    private static Logger logger = Logger.getLogger(SecurityHelper.class);

    public static String getMD5of32Str(String inbuf)
    {
        MD5 md5 = new MD5();
        return md5.getMD5ofStr(inbuf);
    }

    public static String getMD5of16Str(String inbuf)
    {
        MD5 md5 = new MD5();
        String str = md5.getMD5ofStr(inbuf);
        if (!StringHelper.isEmpty(str))
        {
            str = str.substring(8, 24);
        }
        return str;
    }

    public static String encode(String originStr, String key)
    {
        try
        {
            KDES kDes = new KDES();
            kDes.setKey(key);
            return kDes.encode(originStr);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return "";
    }

    public static String decode(String originStr, String key)
    {
        try
        {
            KDES kDes = new KDES();
            kDes.setKey(key);
            return kDes.decode(originStr, key);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return "";
    }

    public static void main(String[] args)
    {
        String aa = encode("28213", "");
        System.out.println(aa.length() + "##########" + aa);
        String bb = decode(aa, "");
        System.out.println("@@@@@@" + bb);
    }
}