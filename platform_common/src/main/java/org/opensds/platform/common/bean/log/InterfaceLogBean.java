/*
 *
 *  * // Copyright 2019 The OpenSDS Authors.
 *  * //
 *  * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  * // not use this file except in compliance with the License. You may obtain
 *  * // a copy of the License at
 *  * //
 *  * //     http://www.apache.org/licenses/LICENSE-2.0
 *  * //
 *  * // Unless required by applicable law or agreed to in writing, software
 *  * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * // License for the specific language governing permissions and limitations
 *  * // under the License.
 *  *
 *
 */

package org.opensds.platform.common.bean.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InterfaceLogBean
{
    private static final String DATE_FMT_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss SSS";
    
    /*
     * 唯一标识接口消息所属事务，不存在时为空
     */
    private String transactionId;
   
    /*
     * 填写接口所属的产品，如UC的接口填写UC。包括UC、IVS、TP、FusionSphere、Storage等
     */
    private String product;
    
    /*
     * 接口类型，值为1和2：其中1标识为北向接口；2标识为南向接口
     */
    private String interfaceType;
    
    /*
     * 协议类型，值为SOAP（细分ParlayX）、Rest、COM、Native、HTTP+XML，SMPP
     */
    private String protocolType;
    
    /*
     * 接口名称
     */
    private String name;
    
    /*
     * 源端设备，客户端API类为空，参数不对外体现
     */
    private String sourceAddr;
    
    /*
     * 宿端设备，客户端API类为空，参数不对外体现
     */
    private String targetAddr;
    
    /*
     * 北向接口收到请求的时间，南向接口发起请求的时间
     */
    private Date reqTime;
    
    /*
     * 格式为yyyy-MM-dd HH:mm:ss
     */
    private String reqTimeAsString;
    
    /*
     * 北向接口应答的时间，南向接口收到应答的时间
     */
    private Date respTime;
    
    /*
     * 格式为yyyy-MM-dd HH:mm:ss
     */
    private String respTimeAsString;
    
    /*
     * 请求参数，关键字需要用*替换
     */
    private String reqParams;
    
    /*
     * 接口返回结果码
     */
    private String resultCode;
    
    /*
     * 应答参数，关键字需要用*替换
     */
    private String respParams;

    /*
     * 是否是请求标记位
     */
    private boolean isReq;
    
    public String getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getProduct()
    {
        return product;
    }

    public void setProduct(String product)
    {
        this.product = product;
    }

    public String getInterfaceType()
    {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType)
    {
        this.interfaceType = interfaceType;
    }

    public String getProtocolType()
    {
        return protocolType;
    }

    public void setProtocolType(String protocolType)
    {
        this.protocolType = protocolType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSourceAddr()
    {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr)
    {
        this.sourceAddr = sourceAddr;
    }

    public String getTargetAddr()
    {
        return targetAddr;
    }

    public void setTargetAddr(String targetAddr)
    {
        this.targetAddr = targetAddr;
    }

    public Date getReqTime()
    {
        return reqTime;
    }

    public void setReqTime(Date reqTime)
    {
        this.reqTime = reqTime;
    }

    public String getReqTimeAsString()
    {
        //DATE_FMT_YYYYMMDDHHMMSS
        if (null == reqTimeAsString && null != reqTime)
        {
            DateFormat df = new SimpleDateFormat(DATE_FMT_YYYYMMDDHHMMSS);
            return df.format(reqTime);
        }
        return reqTimeAsString;
    }

    public void setReqTimeAsString(String reqTimeAsString)
    {
        this.reqTimeAsString = reqTimeAsString;
    }

    public Date getRespTime()
    {
        return respTime;
    }

    public void setRespTime(Date respTime)
    {
        this.respTime = respTime;
    }

    public String getRespTimeAsString()
    {
        if (null == respTimeAsString && null != respTime)
        {
            DateFormat df = new SimpleDateFormat(DATE_FMT_YYYYMMDDHHMMSS);
            return df.format(respTime);
        }
        return respTimeAsString;
    }

    public void setRespTimeAsString(String respTimeAsString)
    {
        this.respTimeAsString = respTimeAsString;
    }

    public String getReqParams()
    {
        return reqParams;
    }

    public void setReqParams(String reqParams)
    {
        this.reqParams = reqParams;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public String getRespParams()
    {
        return respParams;
    }

    public void setRespParams(String respParams)
    {
        this.respParams = respParams;
    }

    public boolean isReq()
    {
        return isReq;
    }

    public void setReq(boolean isReq)
    {
        this.isReq = isReq;
    }
}
