package com.linkage.gas_station.util.hessian;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;

import com.caucho.hessian.client.HessianProxyFactory;

public class YeatsHessianProxyFactory extends HessianProxyFactory {   
	  
    private long connectTimeOut = 0;   
  
    private String signature;     
    private String timeStamp;   
    private String machineCode;     
  
    /**  
     * @return signature  
     */  
    public String getSignature() {   
        return signature;   
    }   
  
    /**  
     * @param signature  
     *            要设置的 signature  
     */  
    public void setSignature(String signature) {   
        this.signature = signature;   
    }   
  
    /**  
     * @return connectTimeOut  
     */  
    public long getConnectTimeOut() {   
        return connectTimeOut;   
    }   
  
    /**  
     * @param connectTimeOut  
     *            要设置的 connectTimeOut  
     */  
    public void setConnectTimeOut(long connectTimeOut) {   
        this.connectTimeOut = connectTimeOut;   
    }   
  
    public URLConnection openConnection(URL url) throws IOException {   
        URLConnection conn = super.openConnection(url);  
  
        if (connectTimeOut > 0) {   
            try {   
                // only available for JDK 1.5   
                Method method = conn.getClass().getMethod("setConnectTimeout",   
                        new Class[] { int.class });   
  
                if (method != null)   
                    method.invoke(conn, new Object[] { new Integer(   
                            (int) connectTimeOut) });   
            } catch (Throwable e) {   
            }   
        }   
        conn.setRequestProperty("signature", this.signature);         
        return conn;   
  
    }   
  
    public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	/**  
     * @return timeStamp  
     */  
    public String getTimeStamp() {   
        return timeStamp;   
    }   
  
    /**  
     * @param timeStamp  
     *            要设置的 timeStamp  
     */  
    public void setTimeStamp(String timeStamp) {   
        this.timeStamp = timeStamp;   
    }   
  
} 