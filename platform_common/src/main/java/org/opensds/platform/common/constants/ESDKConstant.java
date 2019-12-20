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

package org.opensds.platform.common.constants;

public interface ESDKConstant
{
    String INTERFACE_NAME = "interface_name";
    
    String JSON_OBJECT_CLASS = "JSON_OBJECT_CLASS";
    
    String APP_ID = "appId";
    
    String DEVICE_ID = "devId";
    
    String PROTOCOL_ADAPTER_TYPE_SOAP_CXF = "SOAP_CXF";
    
    String PROTOCOL_ADAPTER_TYPE_TCP = "TCP";
    
    String PROTOCOL_ADAPTER_TYPE_HTTP = "HTTP";
    
    String PROTOCOL_ADAPTER_TYPE_HTTPS = "https";
    
    int PROTOCOL_ADAPTER_TYPE_HTTPS_DEFAULT_PORT = 443;
    
    String PROTOCOL_ADAPTER_TYPE_HTTP_JDK = "HTTP_JDK";
    
    String PROTOCOL_ADAPTER_TYPE_REST = "REST";
    
    String PROTOCOL_ADAPTER_TYPE_REST_HTLS = "RESTHTLS";
    
    String SSL_SECURE_SOCKET_PROTOCOL = "TLS";
    
    String SSL_SECURE_SOCKET_PROTOCOL_TLS = "TLSv1";
    
    String SSL_SECURE_SOCKET_PROTOCOL_TLS11 = "TLSv1.1";
    
    String SSL_SECURE_SOCKET_PROTOCOL_TLSv1_2 = "TLSv1.2";
    
    String NOTIFY_MSG_TYPE_DEV_CALLBACK = "DEV_CALLBACK";
    
    String NOTIFY_MSG_TYPE_ESDK_EVENT = "ESDK_EVENT";
    
    String NOTIFY_ITFNAME_CONNECT = "CONNECT";
    
    String NOTIFY_ITFNAME_DISCONNECT = "DISCONNECT";
    
    String ACCT_INFO_ESDK = "account_info_esdk";
    
    String ACCT_INFO_DEV = "account_info_device";
    
    String AUTH_POLICY = "AuthorizePolicy";
    
    String AUTH_POLICY_SINGLE_ACCT = "SingleAccount";
    
    String AUTH_POLICY_ACCT_MAPPING = "AccountMapping";
    
    String AUTH_POLICY_PASS_THROUGH = "PassThrough";
    
    String HTTP_METHOD_GET = "GET";
    
    String HTTP_METHOD_POST = "POST";
    
    String HTTP_METHOD_PUT = "PUT";
    
    String HTTP_METHOD_DELETE = "DELETE";
    
    String DEVICE_USER_ID = "dev_user_id";
    
    String DEVICE_PLAIN_PWD = "dev_plain_pwd";
    
    String ESDK_USER_ID = "esdk_user_id";
    
    String ESDK_PLAIN_PWD = "esdk_plain_pwd";
    
    //Operation Logging Modules
    String OPERATION_LOG_MODULE_LOGIN = "login";
    
    String OPERATION_LOG_MODULE_CONFIG = "config";
    
    String OPERATION_LOG_MODULE_LOG = "log";
    
    String OPERATION_LOG_MODULE_VERSION = "version";
    
    //eSDK platform supported business types
    String BUSINESS_UC20 = "UC2.0";
    
    String BUSINESS_UC22 = "UC2.2";
    
    String BUSINESS_UC231 = "UC2.3.1";
    
    String BUSINESS_EC30 = "EC3.0";
    
    String BUSINESS_TP = "TP";
    
    String BUSINESS_TPOA = "TPOA";
    
    String BUSINESS_RSE = "RSE";
    
    String BUSINESS_IVS = "IVS";
    
    String BUSINESS_ECMULTIAPP = "ECMULTIAPP";
    
    String RSA2048 = "RSA2048";
    
    String AES128 = "AES128";
    
    int AES128_KEY_LENGTH = 16 * 2;
    
    int AES128_IV_LENGTH = 16 * 2;
    
    String SENSITIVE_INFO_TRANSMISSION_MODE_AES128_CONSULTED = "AES128_consulted";
    
    String SENSITIVE_INFO_TRANSMISSION_MODE_RSA2048 = "RSA2048";
    
    String SENSITIVE_INFO_TRANSMISSION_MODE_AES128_FIXED = "AES128_fixed";
    
    String SENSITIVE_INFO_TRANSMISSION_MODE_PLAINTEXT = "Plaintext";
    
    String ESDK_CLIENT_IP = "esdk_client_ip";
    
    String SDK_SESSION_ID = "SDK_SESSION_ID";
    
    String CURRENT_PRODUCT = "current_product";
}
