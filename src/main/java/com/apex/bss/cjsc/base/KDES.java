package com.apex.bss.cjsc.base;

/**
 * Created by Jinshi on 2016/2/20.
 */

import com.sun.crypto.provider.SunJCE;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Security;

public final class KDES
{
    public static int DES = 1;

    public static int DESEDE = 2;

    public static int BLOWFISH = 3;
    private Cipher p_Cipher;
    private SecretKey p_Key;
    private String p_Algorithm;
    BASE64Decoder decoder;
    BASE64Encoder encoder;

    private void selectAlgorithm(int al)
    {
        switch (al)
        {
            case 1:
            default:
                this.p_Algorithm = "DES";
                break;
            case 2:
                this.p_Algorithm = "DESede";
                break;
            case 3:
                this.p_Algorithm = "Blowfish";
        }
    }

    public KDES(int algorithm)
            throws Exception
    {
        this.decoder = new BASE64Decoder();
        this.encoder = new BASE64Encoder();
        selectAlgorithm(algorithm);
        Security.addProvider(new SunJCE());
        this.p_Cipher = Cipher.getInstance(this.p_Algorithm);
    }

    public KDES()
            throws Exception
    {
        this.decoder = new BASE64Decoder();
        this.encoder = new BASE64Encoder();
        selectAlgorithm(1);
        Security.addProvider(new SunJCE());
        this.p_Cipher = Cipher.getInstance(this.p_Algorithm);
    }

    public void setKey(String key)
    {
        if ((key == null) || (key.equals("")))
            key = "desdesde";
        int n = key.length() % 8;
        if (n != 0)
        {
            for (int i = 8; i > n; --i) {
                key = key + "0";
            }
        }
        this.p_Key = new SecretKeySpec(key.getBytes(), this.p_Algorithm);
    }

    private SecretKey checkKey()
    {
        try
        {
            if (this.p_Key == null)
            {
                KeyGenerator keygen = KeyGenerator.getInstance(this.p_Algorithm);
                this.p_Key = keygen.generateKey();
            }
        }
        catch (Exception localException)
        {
        }
        return this.p_Key;
    }

    public String encode(String data)
            throws Exception
    {
        this.p_Cipher.init(1, checkKey());
        return new String(byte2hex(this.p_Cipher.doFinal(data.getBytes("GBK"))));
    }

    public String decode(String encdata, String enckey)
            throws Exception
    {
        setKey(enckey);
        this.p_Cipher.init(2, this.p_Key);
        return new String(this.p_Cipher.doFinal(hex2byte(encdata)), "GBK");
    }

    public String byte2hex(byte[] b)
    {
        return this.encoder.encode(b);
    }

    public byte[] hex2byte(String hex)
            throws IOException
    {
        return this.decoder.decodeBuffer(hex);
    }
}