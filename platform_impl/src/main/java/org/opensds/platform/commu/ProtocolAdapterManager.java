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

package org.opensds.platform.commu;

import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.commu.itf.IProtocolAdapterManager;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;

public class ProtocolAdapterManager implements IProtocolAdapterManager, ESDKConstant
{
    
    @Override
    public ISDKProtocolAdapter getProtocolInstanceByType(String protocolAdapterType, String sap)
    {
        if (PROTOCOL_ADAPTER_TYPE_SOAP_CXF.equals(protocolAdapterType))
        {
            return new CXFSOAPProtocolAdapter(sap);
        }
        else if (PROTOCOL_ADAPTER_TYPE_HTTP.equals(protocolAdapterType))
        {
            return new HttpProtocolAdapter(sap);
        }
        else if (PROTOCOL_ADAPTER_TYPE_HTTP_JDK.equals(protocolAdapterType))
        {
            return new HttpProtocolJDKAdapter(sap);
        }
        else if (PROTOCOL_ADAPTER_TYPE_REST.equals(protocolAdapterType))
        {
            return new RestfulAdapterImplHttpClient(sap);
        }
        else if (PROTOCOL_ADAPTER_TYPE_REST_HTLS.equals(protocolAdapterType))
        {
            return new RestfulAdapterImplHttpClientHTLS(sap);
        }
        
        return null;
    }
    
}
