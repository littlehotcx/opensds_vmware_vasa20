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

package org.opensds.platform.log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;

import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.common.utils.ESDKIOUtils;
import org.opensds.platform.common.utils.FileAttributeUtility;
import org.opensds.platform.common.utils.NumberUtils;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.util.PathUtils;

public class LogFileUploaderTask implements Runnable
{
    private static final Logger LOGGER = LogManager.getLogger(LogFileUploaderTask.class);
    
    private static final String LOG_PRODUCT_SERVER = "eSDK-Server";
    
    private static String SSL_SECURE_SOCKET_PROTOCOL = "TLS";
    
    private ClientConnectionManager conMgr = new PoolingClientConnectionManager();
    
    private HttpClient httpClient = new DefaultHttpClient(conMgr);
    
    private static boolean gzip = Boolean.parseBoolean(ConfigManager.getInstance()
        .getValue("platform.upload.log.file.gzip", "false"));
    
    private long getSleepTime()
    {
        //Random generator = new Random();
    	SecureRandom generator = new SecureRandom();
        double num = generator.nextDouble() / 2;
        
        long result =
            (long)(60L * NumberUtils.parseIntValue(ConfigManager.getInstance()
                .getValue("platform.upload.log.file.interval", "60")) * num);
        
        return result;
    }
    
    @Override
    public void run()
    {
        try
        {
            long sleepTime;
            while (true)
            {
                sleepTime = getSleepTime();
                LOGGER.debug("sleepTime=" + sleepTime);
                TimeUnit.SECONDS.sleep(sleepTime);
                try
                {
                    //upload Logs
                    uploadLogFiles();
                }
                catch (Exception e)
                {
                    LOGGER.error("", e);
                }
            }
        }
        catch (InterruptedException e)
        {
            //InterruptedException Exception happened
            LOGGER.error("", e);
        }
    }
    
    private boolean hasUploadRights()
    {
        String serverUrl = ConfigManager.getInstance().getValue("log.server.url");
        
        httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
        httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
        
        if (serverUrl.toLowerCase().startsWith(ESDKConstant.PROTOCOL_ADAPTER_TYPE_HTTPS))
        {
            try
            {
                URL server = new URL(serverUrl);
                int port = server.getPort();
                port = 0 < port ? port : ESDKConstant.PROTOCOL_ADAPTER_TYPE_HTTPS_DEFAULT_PORT;
                
                SSLContext ctx = SSLContext.getInstance(ESDKConstant.SSL_SECURE_SOCKET_PROTOCOL);
                ctx.init(null, new TrustManager[] {myX509TrustManager}, null);
                org.apache.http.conn.ssl.SSLSocketFactory ssf =
                    new org.apache.http.conn.ssl.SSLSocketFactory(ctx,
                        org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                conMgr.getSchemeRegistry().register(new Scheme(ESDKConstant.PROTOCOL_ADAPTER_TYPE_HTTPS, port, ssf));
            }
            catch (KeyManagementException e)
            {
                LOGGER.error("https error", e);
            }
            catch (NoSuchAlgorithmException e)
            {
                LOGGER.error("https error", e);
            }
            catch (MalformedURLException e)
            {
                LOGGER.error("", e);
            }
        }
        
        HttpPost httpPost = new HttpPost(serverUrl);
        MultipartEntity mutiEntity = new MultipartEntity(HttpMultipartMode.STRICT);
        httpPost.setEntity(mutiEntity);
        try
        {
            mutiEntity.addPart("LogFileInfo",
                new StringBody("{\"product\":\"" + LOG_PRODUCT_SERVER + "\"}", Charset.forName("UTF-8")));
        }
        catch (UnsupportedEncodingException e)
        {
            LOGGER.error("UTF-8 is not supported encode");
        }
        
        HttpResponse httpResponse;
        try
        {
            httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            String content = EntityUtils.toString(httpEntity);
            if (content.contains("\"resultCode\":\"3\""))
            {
                return false;
            }
        }
        catch (ClientProtocolException e)
        {
            LOGGER.error("", e);
            return false;
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
            return false;
        }
        finally
        {
            httpPost.releaseConnection();
        }
        
        return true;
    }
    
    private void uploadLogFiles()
    {
        LOGGER.debug("uploadLogFiles begin");
        if (!hasUploadRights())
        {
            LOGGER.debug("Current machine is not allowed to upload file to server or the server has something wrong.");
            return;
        }
        
        String uploadLogTypes = ConfigManager.getInstance().getValue("platform.upload.log.file.type");
        if (StringUtils.isEmpty(uploadLogTypes))
        {
            uploadLogTypes = "interface";
        }
        
        String[] logTypes = new String[] {"run", "interface", "operation"};
        String logFile;
        File file;
        boolean currentWritingLogFileFlag = false;
        for (String logType : logTypes)
        {
            if (uploadLogTypes.contains(logType))
            {
                //Reset variables
                currentWritingLogFileFlag = false;
                
                //Loop all log files for specified log type
                while (true)
                {
                    logFile = LogFileUploaderHelper.getOldestLogFile(logType);
                    LOGGER.debug("logFile=" + logFile);
                    if (StringUtils.isEmpty(logFile)
                        || (currentWritingLogFileFlag && !LogFileUploaderHelper.isBackLogFile(logFile)))
                    {
                        break;
                    }
                    else
                    {
                        if (!LogFileUploaderHelper.isBackLogFile(logFile))
                        {
                            currentWritingLogFileFlag = true;
                        }
                        file = new File(logFile);
                        if (0 == file.length())
                        {
                            continue;
                        }
                        
                        if (!LogFileUploaderHelper.isBackLogFile(logFile))
                        {
                            logFile = processCurrentLogFile(logType, logFile);
                        }
                        if (StringUtils.isEmpty(logFile))
                        {
                            continue;
                        }
                        logFile = moveFile(logFile);
                        if (!StringUtils.isEmpty(logFile) && doLogFileUpload(logFile, LOG_PRODUCT_SERVER))
                        {
                            LogFileUploaderHelper.backup(logFile, logType);
                        }
                    }
                }
            }
        }
        
        LOGGER.debug("uploadLogFiles end");
    }
    
    private String moveFile(String logFile)
    {
    	LOGGER.debug("start to moveFile: " + logFile);
    	
    	String returnPath = "";
        if (StringUtils.isEmpty(logFile))
        {
            return logFile;
        }
        
        File file = new File(logFile);
        //Move the file to temp folder for uploading
        File destFile = new File(file.getParent() + File.separator + "temp" + File.separator + file.getName());
        try
        {
            if (destFile.exists())
            {
            	LOGGER.debug("file already exists. delete it: " + destFile.getPath());
                boolean result = destFile.delete();
                
                if(!result)
                {
                	LOGGER.debug("delete file failed: " + destFile.getPath());
                	return "";
                }
            }
            FileUtils.moveFile(file, destFile);
            
            // �������.zip��β���ļ����Ƚ�ѹ
            if(destFile.getName().endsWith(".zip"))
            {
            	LOGGER.debug("start to unzip backuplog: " + destFile.getPath());
            	File unzipFile = new File(destFile.getParent());
            	String unzipFilePath = unzip(destFile.getPath(), unzipFile); 
            	
            	LOGGER.debug("unzipFilePath: " + unzipFilePath);
            	/*
                 * CodeDex:  FORTIFY.HW_-_Path_Manipulation      by nWX285177
                 */
            	File unzipFileNew = new File(PathUtils.FilePathFormatWithEncode(unzipFilePath, "UTF-8"));
            	
            	if(unzipFileNew.exists())
            	{
            		LOGGER.debug("start to delete backuplog: " + destFile.getPath());
            		
                    boolean result = destFile.delete();
                    
                    if(!result)
                    {
                    	LOGGER.debug("delete zip file failed: " + destFile.getPath());
                    }
                    else
                    {
                    	LOGGER.debug("delete zip file success: " + destFile.getPath());
                    }
                    
                	returnPath = unzipFilePath;
            	}
            }
            else
            {
                file = destFile;
                returnPath = file.getPath();
            }
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        }
        
        return returnPath;
    }
    
    /**
	 *codedex 	
	 *FORTIFY.HW_-_Create_files_with_appropriate_access_permissions_in_multiuser_system
	 *FORTIFY.Unreleased_Resource--Files    
	 *nwx356892 
	 */
    private String unzip(String file, File unzipFile)
    {
    	String unzipFilePath = "";
        // 
        InputStream input = null;
        OutputStream output = null;
        ZipFile zipFile = null;
        try
        {
        	zipFile = new ZipFile(file);
            // 
            Enumeration<?> zipEnum = zipFile.entries();

            //
            while (zipEnum.hasMoreElements())
            {
                // 
                ZipEntry entry = (ZipEntry)zipEnum.nextElement();
                String entryName = new String(entry.getName().getBytes("ISO8859_1"),Charset.defaultCharset());
                // 
                if (entry.isDirectory())
                {
                	/*
                     * CodeDex:  FORTIFY.HW_-_Path_Manipulation      by nWX285177
                     */
                    new File(PathUtils.FilePathFormatWithEncode(unzipFile.getAbsolutePath() + "/" + entryName, "UTF-8")).mkdir();
                }
                else
                { // 
                    input = zipFile.getInputStream(entry);
                    unzipFilePath = unzipFile.getAbsolutePath() + "/" + entryName;

                    /*
                     * CodeDex:  FORTIFY.Path_Manipulation      by nWX285177
                     */
                    
                    output = FileAttributeUtility.getSafeOutputStream(PathUtils.FilePathFormatWithEncode(unzipFile.getAbsolutePath() + "/" + entryName, "UTF-8"),false);

                    byte[] buffer = new byte[1024 * 8];
                    int readLen = 0;
                    while ((readLen = input.read(buffer, 0, 1024 * 8)) != -1)
                    {
                    	output.write(buffer, 0, readLen);
                    }
                    /*
                     * CodeDex:  RESOURCE_LEAK      nwx356892
                     */
                    
                    input.close();
                    output.flush();
                    output.close();
                }
                
            }
        }
        catch (ZipException e)
        {
        	 LOGGER.error("", e);
        }
        catch (IOException e)
        {
        	 LOGGER.error("", e);
        }
        finally
        {
        	ESDKIOUtils.closeIgnoringException(zipFile);
        	ESDKIOUtils.closeFileStreamNotThrow(input);
        	ESDKIOUtils.closeFileStreamNotThrow(output);
        }
        
        
        return unzipFilePath;
    }
    
    private String processCurrentLogFile(String logType, String logFile)
    {
        File file = new File(logFile);
        //Different appenders for different file types
        RollingFileAppender appender = null;
        if ("interface".equalsIgnoreCase(logType))
        {
        	if(LogManager.getLogger("org.opensds.platform.log.InterfaceLog") != null){
        		LoggerContext context = (LoggerContext)LogManager.getContext(false);
            	appender = (RollingFileAppender)context.getConfiguration().getAppender("org.opensds.platform.log.InterfaceLog");
        		//appender1 = (RollingFileAppender)LogManager.getLogger("org.opensds.platform.log.InterfaceLog").getAppender("FILE1");
        	}
        }
        else if ("operation".equalsIgnoreCase(logType))
        {
            try
            {
                File destDir = new File(file.getParent() + File.separator + "temp" + File.separator + file.getName());
                FileUtils.moveFile(file, destDir);
                FileUtils.moveFile(destDir, file);
                return logFile;
            }
            catch (IOException e)
            {
                return "";
            }
        }
        else
        {
        	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            appender = (RollingFileAppender)ctx.getConfiguration().getAppender("fileLogger");
        }
        
        if(appender == null){
        	return "";
        }
        SizeBasedTriggeringPolicy triggeringPolicy = (SizeBasedTriggeringPolicy)appender.getTriggeringPolicy();
        long origSize = triggeringPolicy.getMaxFileSize();
        
        
        
        //appender.setMaximumFileSize(file.length());
        SizeBasedTriggeringPolicy newTriggeringPolicy = triggeringPolicy.createPolicy(String.valueOf(file.length()));
        RollingFileManager manager = appender.getManager();
		manager.setTriggeringPolicy(newTriggeringPolicy);
        if ("interface".equalsIgnoreCase(logType))
        {
            LOGGER.debug("Rolling the interface log file");
            manager.rollover();
            //Call the rooOver method in order to backup the current log file for uploading
            //appender.rollOver();
        }
        else
        {
            //Call the rooOver method in order to backup the current log file for uploading
            //appender.rollOver();
        	 manager.rollover();
            LOGGER.debug("Log File size reset");
        }
        LOGGER.debug("origSize=" + origSize + ", logType=" + logType);
        manager.setTriggeringPolicy(triggeringPolicy.createPolicy(String.valueOf(origSize)));
        String result = logFile + ".1";
        file = new File(result);
        if (file.exists())
        {
            return result;
        }
        else
        {
            return "";
        }
    }
    
    private boolean doLogFileUpload(String fileNameWithPath, String product)
    {
    	LOGGER.debug("start to upload file by http msg: "+ fileNameWithPath);
    	
        if (StringUtils.isEmpty(fileNameWithPath))
        {
            return true;
        }
        
        String content = "";
        String logServerUrl = ConfigManager.getInstance().getValue("log.server.url");
        
        if (logServerUrl.toLowerCase().startsWith(ESDKConstant.PROTOCOL_ADAPTER_TYPE_HTTPS))
        {
            content = doUploadByHttpsURLConnection(logServerUrl, fileNameWithPath, product);
        }
        else
        {
            content = doUploadByHttpURLConnection(logServerUrl, fileNameWithPath, product);
        }
            
        
        if (content.contains("\"resultCode\":\"0\""))
        {
            return true;
        }
        else
        {
            LOGGER.warn("File file " + fileNameWithPath + " is uploaded to log server failed,"
                + " the response from server is " + content);
        }
        
        LOGGER.debug("end to upload file by http msg: "+ fileNameWithPath);
        return false;
    }
    
    /**
	 *codedex 	
	 *FORTIFY.Denial_of_Service
	 *FORTIFY.Unreleased_Resource--Streams    
	 *nwx356892 
	 */
    private String doUploadByHttpURLConnection(String logServerUrl, String fileNameWithPath, String product)
    {
        String boundary = UUID.randomUUID().toString();
        System.out.println(boundary);
        String crlf = "\r\n";
        String twoHyphens = "--";
        /*
         * CodeDex:  FORTIFY.HW_-_Path_Manipulation      by nWX285177
         */
        File file = new File(PathUtils.FilePathFormatWithEncode(fileNameWithPath, "UTF-8"));
        InputStream responseStream = null;
        BufferedReader responseStreamReader = null;
        StringBuffer buffer = new StringBuffer();
        HttpURLConnection httpUrlConnection = null;
        ByteArrayOutputStream baos = null;
        GZIPOutputStream gzipOut = null;
        DataOutputStream request = null;
        OutputStream out = null;
        InputStream is = null;
        InputStreamReader isr= null;
        try
        {
            URL url = new URL(logServerUrl);
            httpUrlConnection = (HttpURLConnection)url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            if (gzip)
            {
                httpUrlConnection.setRequestProperty("Content-Encoding", "gzip");
            }
            out = httpUrlConnection.getOutputStream();
            request = new DataOutputStream(out);
            //JSON String
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"LogFileInfo\"" + crlf);
            request.writeBytes(crlf);
            request.writeBytes("{\"product\":\"" + product + "\"}");
            request.writeBytes(crlf);
            
            //Content wrapper
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"LogFile\"; filename=\"" + file.getName() + "\""
                + crlf);
            request.writeBytes("Content-Type: text/plain" + crlf);
            request.writeBytes(crlf);
            
            //Write File content
            if (gzip)
            {
                baos = new ByteArrayOutputStream();  
                gzipOut = new GZIPOutputStream(baos);  
                gzipOut.write(FileUtils.readFileToByteArray(file));
                gzipOut.finish();
                request.write(baos.toByteArray());
            }
            else
            {
                request.write(FileUtils.readFileToByteArray(file));
            }
            
            //End content wrapper:
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
            
            //Flush output buffer:
            request.flush();
            
            int responseCode = httpUrlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                //Get Response
            	is = httpUrlConnection.getInputStream();
                responseStream = new BufferedInputStream(is);
                isr = new InputStreamReader(responseStream, "UTF-8");
                responseStreamReader = new BufferedReader(isr);
                
            }
            else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND)
            {
                return HttpURLConnection.HTTP_NOT_FOUND + "";
            }
            else
            {
                return "";
            }
            int line = 0;
        	do{
        		StringBuffer lineBuffer = new StringBuffer();
        		line = ESDKIOUtils.readLine(responseStreamReader, lineBuffer);
        		buffer.append(lineBuffer);
        	}
        	while(line!=-1);
        }
        catch (ClientProtocolException e)
        {
            LOGGER.error("", e);
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        }
        finally
        {
        	ESDKIOUtils.closeFileStreamNotThrow(isr);
        	ESDKIOUtils.closeFileStreamNotThrow(is);
        	ESDKIOUtils.closeFileStreamNotThrow(out);
        	ESDKIOUtils.closeFileStreamNotThrow(request);
        	ESDKIOUtils.closeFileStreamNotThrow(responseStreamReader);
        	ESDKIOUtils.closeFileStreamNotThrow(responseStream);
        	ESDKIOUtils.closeFileStreamNotThrow(baos);
        	ESDKIOUtils.closeFileStreamNotThrow(gzipOut);
        }
        
        if (null != httpUrlConnection)
        {
            httpUrlConnection.disconnect();
        }
        
        return buffer.toString();
    }
    
    private String doUploadByHttpsURLConnection(String logServerUrl, String fileNameWithPath, String product)
    {
        String boundary = UUID.randomUUID().toString();
        System.out.println(boundary);
        String crlf = "\r\n";
        String twoHyphens = "--";
        /*
         * CodeDex:  FORTIFY.HW_-_Path_Manipulation      by nWX285177
         */
        File file = new File(PathUtils.FilePathFormatWithEncode(fileNameWithPath, "UTF-8"));
        InputStream responseStream = null;
        BufferedReader responseStreamReader = null;
        StringBuffer buffer = new StringBuffer();
        HttpsURLConnection httpsUrlConnection = null;
        ByteArrayOutputStream baos = null;
        GZIPOutputStream gzipOut = null;
        DataOutputStream request = null;
        OutputStream out = null;
        InputStream is = null;
        InputStreamReader isr= null;
        try
        {
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session)
                {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            
            SSLContext sslcontext = SSLContext.getInstance(SSL_SECURE_SOCKET_PROTOCOL);
            sslcontext.init(null, new TrustManager[] {myX509TrustManager}, null);
            
            URL url = new URL(logServerUrl);
            httpsUrlConnection = (HttpsURLConnection)url.openConnection();
            httpsUrlConnection.setSSLSocketFactory(sslcontext.getSocketFactory());
            httpsUrlConnection.setUseCaches(false);
            httpsUrlConnection.setDoOutput(true);
            
            httpsUrlConnection.setRequestMethod("POST");
            httpsUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpsUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpsUrlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            if (gzip)
            {
                httpsUrlConnection.setRequestProperty("Content-Encoding", "gzip");
            }
            
            out = httpsUrlConnection.getOutputStream();
            request = new DataOutputStream(out);
            //JSON String
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"LogFileInfo\"" + crlf);
            request.writeBytes(crlf);
            request.writeBytes("{\"product\":\"" + product + "\"}");
            request.writeBytes(crlf);
            
            //Content wrapper
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"LogFile\"; filename=\"" + file.getName() + "\""
                + crlf);
            request.writeBytes("Content-Type: text/plain" + crlf);
            request.writeBytes(crlf);
            
            //Write File content
            if (gzip)
            {
                baos = new ByteArrayOutputStream();  
                gzipOut = new GZIPOutputStream(baos);  
                gzipOut.write(FileUtils.readFileToByteArray(file));
                gzipOut.finish();
                request.write(baos.toByteArray());
            }
            else
            {
                request.write(FileUtils.readFileToByteArray(file));
            }
            
            //End content wrapper:
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
            
            //Flush output buffer:
            request.flush();
            
            int responseCode = httpsUrlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                //Get Response
            	is = httpsUrlConnection.getInputStream();
                responseStream = new BufferedInputStream(is);
                isr = new InputStreamReader(responseStream, "UTF-8");
                responseStreamReader = new BufferedReader(isr);
                
            }
            else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND)
            {
                return HttpURLConnection.HTTP_NOT_FOUND + "";
            }
            else
            {
                return "";
            }
            int line = 0;
        	do{
        		StringBuffer lineBuffer = new StringBuffer();
        		line = ESDKIOUtils.readLine(responseStreamReader, lineBuffer);
        		buffer.append(lineBuffer);
        	}
        	while(line!=-1);
        }
        catch (ClientProtocolException e)
        {
            LOGGER.error("", e);
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("", e);
        }
        catch (KeyManagementException e)
        {
            LOGGER.error("", e);
        }
        finally
        {
        	ESDKIOUtils.closeFileStreamNotThrow(isr);
        	ESDKIOUtils.closeFileStreamNotThrow(is);
        	ESDKIOUtils.closeFileStreamNotThrow(out);
        	ESDKIOUtils.closeFileStreamNotThrow(request);
        	ESDKIOUtils.closeFileStreamNotThrow(responseStreamReader);
        	ESDKIOUtils.closeFileStreamNotThrow(responseStream);
        	ESDKIOUtils.closeFileStreamNotThrow(baos);
        	ESDKIOUtils.closeFileStreamNotThrow(gzipOut);
        }
        
        if (null != httpsUrlConnection)
        {
            httpsUrlConnection.disconnect();
        }
        
        return buffer.toString();
    }
    
    private static TrustManager myX509TrustManager = new X509TrustManager()
    {
        
        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return new java.security.cert.X509Certificate[] {};
        }
        
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException
        {
        }
        
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException
        {
        }
    };
    
}
