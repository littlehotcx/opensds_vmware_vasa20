/*
 * // Copyright 2019 The OpenSDS Authors.
 * //
 * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 * // not use this file except in compliance with the License. You may obtain
 * // a copy of the License at
 * //
 * //     http://www.apache.org/licenses/LICENSE-2.0
 * //
 * // Unless required by applicable law or agreed to in writing, software
 * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * // License for the specific language governing permissions and limitations
 * // under the License.
 *
 */

package org.opensds.vasa.base.common;

public interface VasaConstant {
    String INITATOR_PREFIX = "APP:";

    String PWD_ESDK = "pwd_esdk";

    String ID_DEV = "id";

    String PWD_DEV = "pwd_dev";

    String APP_ID_DEV = "appid";

    String ACCT_INFO_ESDK = "account_info_esdk";

    String VASA_DEV_LOGIN_STATUS = "vasa_dev_login_status";

    String X_Auth_Token = "X-Auth-Token";

    String PUBLIC_URL = "publicURL";

    String LOGIN = "/v2.0/tokens";

    String ADMIN_URL = "adminURL";

    String ACTION = "/action";

    String SESSION_ID = "Set-Cookie";

    String TOKEN_ID = "Token-ID";

    String REQUEST_ARRAY_ID = "request_array_id";

    String ESX_IP = "current_Esx_host_IP";

    String CURRENT_CONTAINER_TYPE = "current_container_type";

    String CONTAINER_TYPE_NAS = "NAS";

    String UNIQURE_VMID = "VMW_VmID";

    String VMW_VVolNamespace = "VMW_VVolNamespace";

    String VMW_VVolType = "VMW_VVolType";

    String OPT_SNAPSHOT = "snapshot";

    String OPT_CLONE = "clone";

    String SESSION_ID_FOR_HOSTIPS = "session_hostIps";

    String REQUEST_POLICY_NO_QOS = "request_policu_no_qos";

    String VVol_No_Requirements_Policy = "f4e5bade-15a2-4805-bf8e-52318c4ce443";

    String VVOL_TYPE_CONFIG = "Config";

    String VVOL_TYPE_DATA = "Data";

    String VVOL_TYPE_SWAP = "Swap";

    String VVOL_TYPE_MEMORY = "Memory";

    String VVOL_TYPE_OTHER = "Other";

    String FILE_SYSTEM_THIN = "1";


    String FILE_SYSTEM_TYPE_OTHER = "VASA_SYSTEM_OTHER_";

    String FILE_SYSTEM_PREFIX = "VASA_SYSTEM_DATA_";

    String FILE_SYSTEM_TYPE_COMMON = "VASA_SYSTEM_COMMON_";

    String FILE_SYSTEM_TEMP = "VASA_SYSTEM_TEMP_";

    String FILE_SYSTEM_TYPE_SWAP = "file_system_swap";

    int COMMON_FILE_SYSTEM_SIZE = 5;

    String FILE_SYSTEM_TYPE_MEMORY = "file_system_memory";

    String SEPARATOR = ";";

    String TASK_SEPARATOR = ":";

    static final String VVOL_PREFIX = "rfc4122.";

    static final String CLONE_REAR = "_clone";

    static final String VVOL_DATA_REAR = ".vmdk";

    static final String VVOL_SWAP_REAR = ".vswp";

    static final String VVOL_MEM_REAR = ".vmem";

    static final String VVOL_OTHER_REAR = ".other";

    static final String HOST_ID_CERTIFICATION = "NA";

    String VVOL_SNAPSHOT_FLODER = ".snapshot";
}
