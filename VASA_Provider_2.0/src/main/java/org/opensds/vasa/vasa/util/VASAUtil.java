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

//*
package org.opensds.vasa.vasa.util;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensds.vasa.common.ArrayErrCodeEnum;
import org.opensds.vasa.common.HealthState;
import org.opensds.vasa.common.MOType;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.common.ProductSpeciality;
import org.opensds.vasa.domain.model.VVolModel;
import org.opensds.vasa.domain.model.bean.DArray;
import org.opensds.vasa.domain.model.bean.DLun;
import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.domain.model.bean.StoragePolicy;
import org.opensds.vasa.vasa.common.VvolConstant;
import org.opensds.vasa.vasa.internal.Event;
import org.opensds.vasa.vasa.internal.EventParamEntity;
import org.opensds.vasa.vasa.service.DiscoverServiceImpl;
import org.opensds.vasa.vasa.service.SecureConnectionService;
import org.opensds.vasa.vasa.service.model.StorageProfileData;

import org.opensds.vasa.base.common.VASAArrayUtil;
import org.opensds.vasa.base.common.VasaConstant;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.ThreadLocalHolder;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.ListUtils;
import org.opensds.platform.common.utils.StringUtils;

import org.opensds.vasa.vasa.db.model.NProfile2VolType;
import org.opensds.vasa.vasa.db.model.NStorageContainer;
import org.opensds.vasa.vasa.db.model.NStoragePool;
import org.opensds.vasa.vasa.db.model.NStorageProfile;
import org.opensds.vasa.vasa.db.model.NStorageProfileLevel;
import org.opensds.vasa.vasa.db.model.NStorageQos;
import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.db.model.NVvolProfile;
import org.opensds.vasa.vasa.db.service.Profile2VolTypeService;
import org.opensds.vasa.vasa.db.service.StorageContainerService;
import org.opensds.vasa.vasa.db.service.StoragePoolService;
import org.opensds.vasa.vasa.db.service.StorageProfileLevelService;
import org.opensds.vasa.vasa.db.service.StorageProfileService;
import org.opensds.vasa.vasa.db.service.VirtualMachineService;
import org.opensds.vasa.vasa.db.service.VirtualVolumeService;
import org.opensds.vasa.vasa.db.service.VvolMetadataService;
import org.opensds.vasa.vasa.db.service.VvolProfileService;
import org.opensds.vasa.vasa.db.service.impl.StorageQosServiceImpl;
import org.opensds.vasa.vasa.rest.bean.DeviceTypeMapper;

import com.vmware.vim.vasa.v20.IncompatibleVolume;
import com.vmware.vim.vasa.v20.InvalidArgument;
import com.vmware.vim.vasa.v20.InvalidProfile;
import com.vmware.vim.vasa.v20.InvalidSession;
import com.vmware.vim.vasa.v20.NotFound;
import com.vmware.vim.vasa.v20.OutOfResource;
import com.vmware.vim.vasa.v20.ResourceInUse;
import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.VasaProviderBusy;
import com.vmware.vim.vasa.v20.data.policy.capability.types.xsd.BuiltinGenericTypesEnum;
import com.vmware.vim.vasa.v20.data.policy.capability.types.xsd.BuiltinTypesEnum;
import com.vmware.vim.vasa.v20.data.policy.capability.types.xsd.DiscreteSet;
import com.vmware.vim.vasa.v20.data.policy.capability.types.xsd.Range;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.CapabilityId;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.CapabilityInstance;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.ConstraintInstance;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.GenericTypeInfo;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.PropertyInstance;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.TypeInfo;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.StorageProfile;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.SubProfile;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpoint;
import com.vmware.vim.vasa.v20.data.vvol.xsd.QueryConstraint;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeInfo;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.AlarmStatusEnum;
import com.vmware.vim.vasa.v20.data.xsd.AlarmTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.BlockEnum;
import com.vmware.vim.vasa.v20.data.xsd.EntityTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.EventTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.HostInitiatorInfo;
import com.vmware.vim.vasa.v20.data.xsd.MountInfo;
import com.vmware.vim.vasa.v20.data.xsd.NameValuePair;
import com.vmware.vim.vasa.v20.data.xsd.StorageAlarm;
import com.vmware.vim.vasa.v20.data.xsd.StorageEvent;
import com.vmware.vim.vasa.v20.data.xsd.UsageContext;


public class VASAUtil {
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(VASAUtil.class);

    private static Object clearVolTypeLock = new Object();

    public static final String VMW_NAMESPACE = "com.vmware.storageprofile.std";

    public static final String VMW_STD_CAPABILITY = "spaceEfficiency";

    public static final String VMW_STD_CAPABILITY_DEFAULTVALUE = "Thick";

    public static final String PATTEN_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static final long MAX_VVOL_SIZE_MB = 67108864L;

    /**
     * 最多纪录多少条event的id
     */
    public static final int MAX_EVENTID_TO_LOG = MagicNumber.INT1000;

    private static SecureRandom rand = new SecureRandom();

//    /**
//     * 使用dst 此时区是否使用了夏令时 0-未使用 1-使用
//     */
//    public static final int ARRAY_USE_DST = 1;
//
    /**
     * VMSG中的参数头
     */
    public static final String VMSG_PARAM = "PARAM";

    /**
     * VMSG中的阵列来源
     */
    public static final String VMSG_ARRAY_SRC = "#ARRAY#";

//    /**
//     * VMSG UNKNOWN
//     */
//    public static final String VMSG_ARRAY_UNKNOWN = "Unknown";
//
    /**
     * 换行符
     */
    public static final String LINE_SEPRATOR = System.getProperty("line.separator");

//    /**
//     * 用户输错密码次数
//     */
//    public static final String TRY_ERROR_COUNT = "try_error_count";
//
//    /**
//     * 用户最近一次输入失败时间
//     */
//    public static final String LAST_TRY_TIME = "last_try_time";
//
//    /**
//     * 用户是否被锁定
//     */
//    public static final String IS_USER_LOCKED = "is_user_locked";
//
//    /**
//     * 用户输错密码次数
//     */
//    public static final String R_TRY_ERROR_COUNT = "r_try_error_count";
//
//    /**
//     * 用户最近一次输入失败时间
//     */
//    public static final String R_LAST_TRY_TIME = "r_last_try_time";
//
//    /**
//     * 用户是否被锁定
//     */
//    public static final String R_IS_USER_LOCKED = "r_is_user_locked";
//
//    /**
//     * 用户设置密码时间
//     */
//    public static final String PASSWORD_TIME = "password_time";
//
//    /**
//     * 用户被锁定默认时间 in millseconds
//     */
//    public static final long DEFAULT_LOCK_TIME = 5 * 60 * 1000;
//
//    /**
//     * adminvasa 的密码
//     */
//    public static final String TRUST_PASSWD_VASA = "trust_passwd_vasa";
//
    /**
     * VASA API 版本_#_#的方式
     */
    public static final String VASA_API_VERSION = "v20";

    /**
     * VASA provider版本号
     */
    public static final String PROVIDER_VERSION = "2.0";

//    /** Provider默认命名空间 */
//    public static final String PROVICDER_DEFAULT_NAMESPACE = "vasa";
//
    /**
     * ISM Provider名称
     */
    public static final String ISMPROVIDER_NAME = "OpenSDS VASA Provider";
//
//    /** ISM Provider服务名称 */
//    public static final String ISM_VASA_PROVIDER_SERVER_NAME = "ism-vasa-provider";
//
    /**
     * Provider语言
     */
    public static final String ISM_VASA_LANGUAGE = "en,zh-CN";

    /**
     * 告警配置文件名称
     */
    public static final String ISM_VASA_EVENT_DESC_FILE_NAME = "event.vmsg,fault.vmsg,alarm.vmsg";
//
//
//
//    /** 默认设备型号 */
//    public static final String DEFAULT_MANUFACTURE = "S5000TV1R5";
//
//    /** 最大的事件流水号 */
//    public static final String LAST_EVENT_SEQUENCE = "last_event_sequence";
//
//    /** 最大的告警流水号 */
//    public static final String LAST_ALARM_SEQUENCE = "last_alarm_sequence";

    /**
     * 最大告警和事件的数量
     */
    public static final int MAX_STORAGE_ALARM_EVENT_NUM = 100;

    /**
     * Thin LUN告警的SN
     */
    public static final long THIN_LUN_ALARM_ID = 9223372036854775807L;

    /**
     * Storage online and offline alarm
     */
    public static final long STORAGE_OFFILINE_ALARM_ID = 9223372036854775806L;
    public static final long STORAGE_ONLINE_ALARM_ID = 9223372036854775805L;

    /**
     * svp的告警，大于等于此值，由于SVP的告警sn可能与阵列告警SN相同，所以会加上这个
     */
    public static final long HVS_SVP_ALARM_SN = 268435456L;

    /**
     * Thin lun扩容告警
     */
    public static final long THIN_LUN_ENHENCE = 35248797319199L;

//    /** lun的storageCapability的ID分隔符 */
//    public static final String LUN_CAPABILITY_PREF = "ismlun";
//
//    /** A控 */
//    public static final String CONTROLLER_A = "0";
//
//    /** B控 */
//    public static final String CONTROLLER_B = "1";
//
//    /** A,B控 */
//    public static final String CONTROLLER_A_AND_B = "2";
//
//    /** LUN状态，正常 */
//    public static final int LUN_HEALTH_NORMAL = 1;
//
//    /** LUN状态，故障 */
//    public static final int LUN_HEALTH_FAULT = 2;
//
//    /** LUN状态，未格式化 */
//    public static final int LUN_HEALTH_NOT_FORMATTING = 3;
//
//    /** LUN状态，正在格式化 */
//    public static final int LUN_HEALTH_FORMATTING = 4;
//
//    /** LUN状态，thinLUN正在格式化 */
//    public static final int LUN_HEALTH_THIN_FORMATTING = 6;
//
//    /** LUN状态，ThinLUN删除中 */
//    public static final int LUN_HEALTH_THIN_DELETING = 8;
//
//    /** 公共属性PORT_TYPE_FC */
//    public static final String PORT_TYPE_FC = "FC";
//
//    /** 公共属性PORT_TYPE_FCOE */
//    public static final String PORT_TYPE_FCOE = "FCOE";
//
//    /** 公共属性PORT_TYPE_ISCSI */
//    public static final String PORT_TYPE_ISCSI = "ISCSI";
//
//    /** 公共属性PORT_TYPE_OTHERS */
//    public static final String PORT_TYPE_OTHERS = "Others";

    // 默认的esx主机命名前缀
    /**
     * 公共属性ESX_LUN_IDENTIFER_NAMESPACE
     */
    public static final String ESX_LUN_IDENTIFER_NAMESPACE = "naa";

    // 启动器类型
    /**
     * 公共属性ISCSI
     */
    public static final int ISCSI = 5;

    /**
     * 公共属性FC
     */
    public static final int FC = 1;

    /**
     * 公共属性FCOE
     */
    public static final int FCOE = 8;

//    // 设置夏令时的开始规则
//    /** 公共属性SET_DST_START_RULE */
//    public static final int SET_DST_START_RULE = 1;
//
//    // 设置夏令时的结束规则
//    /** 公共属性SET_DST_END_RULE */
//    public static final int SET_DST_END_RULE = 2;
//
//    /** 公共属性TRUST_USERNAME */
//    public static final String TRUST_USERNAME = "trustUsername";
//
//    /** 公共属性TRUST_PASSWORD */
//    public static final String TRUST_PASSWORD = "trustPassword";
//
//    /** 公共属性TRUST_STORE_PASSOWED */
//    public static final String TRUST_STORE_PASSOWED = "trustStorePassword";
//
//    /** Windwoss 环境变量 **/
//    public static final String PROGRAMDATA_ENV = "ProgramFiles";
//
//    /** 配置文件的目录 **/
//    public static final String CONFIGFILE_DIR = dealX86FilePath();

    /**
     * 事件刷新间隔 时间
     */
//    private static long defaultRetryTimeInSeconds = MagicNumber.INT20 * MagicNumber.INT60;
    private static int defaultRetryTimeInSeconds = Integer.valueOf(ConfigManager.getInstance().getValue("default_retry_time_in_seconds"));

//    // //写入的文件
//
//    private static final String HISTORY_FILE = Util.getBasePath() + File.separator + "vasa" + File.separator + ".s";
//
//
//    // 注册文件位置
//    private static final String REG_FILE = Util.getBasePath() + File.separator + "vasa" + File.separator + ".r";

    /**
     * 构造ID的原子自增变量
     */
    private static long indx = 0;

    /**
     * 过滤vvol displayname
     */
    private static final String reg1 = ".*[`~!@#$%^&*()=+|{};:'\",<>/\\?\\[\\]\\s]+.*";

    /**
     * 过滤vvol displayname
     */
    private static final String reg2 = ".*[\\\\]+.*";

    /**
     * VASA事件类型和VMWARE事件类型映射<br/>
     * <ul>
     *  <li>key vasa的事件类型</li>
     *  <li>value vmware的事件类型</li>
     * </ul>
     */
    private static Map<String, String> eventMapping = new HashMap<String, String>();

    //这里会不会导致产生多个profile2VolTypeService实例？
    private static Profile2VolTypeService profile2VolTypeService = (Profile2VolTypeService) ApplicationContextUtil
            .getBean("profile2VolTypeService");

    //这里会不会导致产生多个vvolProfileService实例？
    private static VvolProfileService vvolProfileService = (VvolProfileService) ApplicationContextUtil
            .getBean("vvolProfileService");

    //这里会不会导致产生多个vvolMetadataService实例？
    private static VvolMetadataService vvolMetadataService = (VvolMetadataService) ApplicationContextUtil
            .getBean("vvolMetadataService");

    private static VirtualVolumeService virtualVolumeService = (VirtualVolumeService) ApplicationContextUtil
            .getBean("virtualVolumeService");

    private static StorageProfileService storageProfileService = (StorageProfileService) ApplicationContextUtil
            .getBean("storageProfileService");

    private static StoragePoolService storagePoolService = (StoragePoolService) ApplicationContextUtil
            .getBean("storagePoolService");

    private static VirtualMachineService virtualMachineService = (VirtualMachineService) ApplicationContextUtil
            .getBean("virtualMachineService");

//    private static VasaArrayService vasaArrayService = (VasaArrayService) ApplicationContextUtil
//            .getBean("vasaArrayService");

    private static StorageContainerService storageContainerService = (StorageContainerService) ApplicationContextUtil.getBean("storageContainerService");
    /*private static StorageQosService storageQosService = (StorageQosService) ApplicationContextUtil
            .getBean("storageQosService");*/
    private static StorageProfileLevelService storageProfileLevelService = ApplicationContextUtil.getBean("storageProfileLevelService");
    //这里会不会导致产生多个vvolModel实例？？？？？？
    private static VVolModel vvolModel = new VVolModel();

    static {
        eventMapping.put(String.valueOf(VASAEvent.REBIND_EVENT), EventTypeEnum.REBIND.value());
        eventMapping.put(String.valueOf(VASAEvent.CHANGE_WORKING_CONTROLLER_EVENT), EventTypeEnum.REBIND.value());

        // Processor事件
        eventMapping.put(String.valueOf(MagicNumber.LONG35244534595609), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG35248809771087), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.MODCTLIPV4), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.MODCTLIPV6), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.HVSC00_PROCESSOR_RECOVER), EventTypeEnum.CONFIG.value());

        // LUN删除事件
        eventMapping.put(String.valueOf(MagicNumber.LONG35248812654627), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG35244534595588), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG35248797319223), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.DELVOL), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.HVSC00_REMOVE_LUN_FROM_LUNGROUP), EventTypeEnum.CONFIG.value());

        // 增加LUN映射事件
        eventMapping.put(String.valueOf(MagicNumber.LONG35244536037395), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG35248812654624), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.ADDMAP), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.HVSC00_ADD_LUN), EventTypeEnum.CONFIG.value());

        // 增加LUN事件
        eventMapping.put(String.valueOf(MagicNumber.LONG77343883390), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.ADDVOL), EventTypeEnum.CONFIG.value());

        // 删除映射事件
        eventMapping.put(String.valueOf(VASAEvent.DELMAP), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG35244536037398), EventTypeEnum.CONFIG.value());

        // 控制器增加、删除事件
        eventMapping.put(String.valueOf(MagicNumber.LONG77343883312), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG77343883346), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG1103551725575), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG17656624119860), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.HVSC00_RESTART_PROCESSOR), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.HVSC00_START_PROCESSOR), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.HVSC00_ADD_PROCESSOR), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.ADDCTL42952753265), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.DELCTL42952753215), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.ADDCTL42952753265), EventTypeEnum.CONFIG.value());

        // 修改LUN名称
        eventMapping.put(String.valueOf(MagicNumber.LONG35244534595624), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG35248797319202), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.MODVOL), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.VIS_RESIZE_LUN), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.GUI_MOD_VOLSIZE), EventTypeEnum.CONFIG.value());

        // 主机端口事件 
        eventMapping.put(String.valueOf(MagicNumber.LONG17652349861911), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG17656627855440), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.ADDFCPORT), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.HVSC00_ADD_PORT), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG60163817473), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG4026925060), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.DELFCPORT), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG35244536692755), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG35248813899836), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG35248813899874), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.MODFCPORT), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.MODISCSIPORT), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.HVSC00_MODIFY_ISCSI_PORT), EventTypeEnum.CONFIG.value());

        // ThinLun事件
        eventMapping.put(String.valueOf(MagicNumber.LONG35244537806873), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG35244537806879), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAUtil.THIN_LUN_ENHENCE), EventTypeEnum.CONFIG.value());

        // 修改阵列名称
        eventMapping.put(String.valueOf(MagicNumber.LONG35244536102925), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(MagicNumber.LONG35248809771081), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.MODNAME), EventTypeEnum.CONFIG.value());
        eventMapping.put(String.valueOf(VASAEvent.HVSC00_MODIFY_ARRAY_LOCATION), EventTypeEnum.CONFIG.value());
    }

    /**
     * @param eventId 阵列侧的事件编号
     * @return WMware的事件类型
     */
    public static String getVMwareEventType(String eventId) {
        return eventMapping.get(eventId);
    }

    /**
     * <生成唯一标示的UUID>
     *
     * @param deviceContext 方法参数：deviceContext
     * @param entityType    方法参数：entityType
     * @param unique        方法参数：unique
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getUUID(String arrayId, String entityType, String unique) {
        StringBuilder uuID = new StringBuilder();
//        String deviceID = DataUtil.getInstance().getDeviceID(String.valueOf(getIpsHashCode(deviceContext)));
//        if (null == deviceID)
//        {
//            StorageArray storageArray = CommonWorkTaker.getInstance().getDiscoverManager(deviceContext.getDeviceType())
//                .getStorageArrayByDeviceContext(deviceContext);
//            String storageIdentifer = storageArray.getUniqueIdentifier();
//            // storageArary的ID由2部分组成
//            String[] splitIDs = storageIdentifer.split(":");
//            if (splitIDs.length == MagicNumber.INT2)
//            {
//                deviceID = splitIDs[1];
//                DataUtil.getInstance().setIpMapDeviceID(String.valueOf(getIpsHashCode(deviceContext)), deviceID);
//            }
//            LogManager.debug("Query device ID. Device ID is :" + deviceID);
//        }
//
//        // 如果这个里deviceID还为空，表示没有查询到deviceID
//        if (null == deviceID)
//        {
//            deviceID = String.valueOf(getIpsHashCode(deviceContext));
//            LogManager.debug("Device ID is null.ip is :" + deviceContext.getPreferedIP() + ", "
//                + deviceContext.getOtherIP());
//        }

        uuID.append(arrayId + ":");
        uuID.append(entityType + ":");
        uuID.append(unique);
        return uuID.toString();
    }

    /**
     * <得到StorageArray的UUID，同时会保存DeviceID>
     *
     * @param deviceContext 方法参数：deviceContext
     * @param deviceID      方法参数：deviceID
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getStorageArrayUUID(String arrayId) {
        StringBuilder uuID = new StringBuilder();
        uuID.append(EntityTypeEnum.STORAGE_ARRAY.value() + ":");
        uuID.append(arrayId);

        // 缓存得到的deviceID
//        DataUtil.getInstance().setIpMapDeviceID(String.valueOf(getIpsHashCode(deviceContext)), deviceID);

        return uuID.toString();
    }

    /**
     * <厂商信息>
     *
     * @author xKF20991
     * @version [版本号, 2012-2-11]
     * @see [相关类/方法]
     * @since [产品/模块版本]
     */
    public enum Vendor {
        /**
         * 枚举变量
         */
        Huawei("HW000001"),
        /**
         * 枚举变量
         */
        Unknown("UnKnown");

        private String vendorInner;

        private Vendor(String vendor) {
            this.vendorInner = vendor;
        }

        /**
         * @param code 方法参数：code
         * @return String [返回类型说明]
         * @see [类、类#方法、类#成员]
         */
        public static String getVendor(String code) {
            if ("HW000001".equals(code)) {
                return Huawei.name();
            } else {
                return Unknown.name();
            }
        }

        /**
         * 方法 ： getVender
         *
         * @return String 返回结果
         */
        public String getVender() {
            return this.vendorInner;
        }

    }

    // 得到VASA的端口枚举类型

    /**
     * 方法 ： getVASAPortType
     *
     * @param portType 方法参数：portType
     * @return String 返回结果
     */
    public static String getVASAPortType(int portType) {
        switch (portType) {

            case FC:
                return BlockEnum.FC.value();
            case ISCSI:
                return BlockEnum.ISCSI.value();
            case FCOE:
                return BlockEnum.F_CO_E.value();
            default:
                return BlockEnum.OTHER.value();
        }
    }

    /**
     * 方法 ： getVASAPortType
     *
     * @param portType 方法参数：portType
     * @return String 返回结果
     */
    public static String getVASAPortType(MOType portType) {
        if (portType != null) {
            switch (portType) {
                case FC_PORT:
                    return BlockEnum.FC.value();
                case ETH_PORT:
                    return BlockEnum.ISCSI.value();
                case FCOE_PORT:
                    return BlockEnum.F_CO_E.value();
                default:
                    return BlockEnum.OTHER.value();
            }
        } else {
            return BlockEnum.OTHER.value();
        }

    }

    // 将主机启动器类型，转换为ISM类型

    /**
     * 方法 ： convertVasaPortToIsmData
     *
     * @param hii   方法参数：hii
     * @param isXVE 方法参数：isXVE
     * @return int 返回结果
     * @throws StorageFault 异常：StorageFault
     */
    public static int convertVasaPortToIsmData(HostInitiatorInfo hii, boolean isXVE) throws StorageFault {
        // iSCSI
        if (hii.getIscsiIdentifier() != null && hii.getIscsiIdentifier().trim().length() > 0) {
            return doIscsiPort(hii, isXVE);
        }
        // FC
        else if ((hii.getPortWwn() != null && hii.getPortWwn().trim().length() > 0)
                && (hii.getNodeWwn() != null && hii.getNodeWwn().trim().length() > 0)) {
            return doFCPort(hii, isXVE);
        } else {
            // unknown
            throw FaultUtil.storageFault("Invalid Host Initiator format");
        }
    }

    /**
     * <降低复杂度>
     *
     * @param hii
     * @param isXVE
     * @return int [返回类型说明]
     * @throws StorageFault [参数说明]
     * @throws throws       [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private static int doFCPort(HostInitiatorInfo hii, boolean isXVE) throws StorageFault {
        if (hii.getIscsiIdentifier() != null && hii.getIscsiIdentifier().trim().length() > 0) {
            throw FaultUtil.storageFault("Invalid Host Initiator format");
        }

        // FC
        if (isXVE) {
            return MOType.FC_PORT.getValue();
        } else {
            return FC;
        }
    }

    /**
     * <降低复杂度>
     *
     * @param hii
     * @param isXVE
     * @return int [返回类型说明]
     * @throws StorageFault [参数说明]
     * @throws throws       [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private static int doIscsiPort(HostInitiatorInfo hii, boolean isXVE) throws StorageFault {
        if ((hii.getPortWwn() != null && hii.getPortWwn().trim().length() > 0)
                || (hii.getNodeWwn() != null && hii.getNodeWwn().trim().length() > 0)) {
            throw FaultUtil.storageFault("Invalid Host Initiator format");
        }

        if (isXVE) {
            return MOType.ETH_PORT.getValue();
        } else {
            return ISCSI;
        }
    }

    /**
     * <hostInitiator>
     *
     * @param wwn [参数说明]
     * @return String 返回结果
     * @see [类、类#方法、类#成员]
     */
    public static String portWWNDecimal2Hex(String wwn) {
        if (wwn.startsWith("iqn.") || wwn.startsWith("eui.")) {
            return wwn;
        }

        if (wwn.contains(":")) {
            return wwn.replaceAll(":", "");
        } else {
            long portWWN = 0;
            try {
                portWWN = Long.parseLong(wwn);
            } catch (NumberFormatException formantException) {
                /**
                 * S5500T P11G-5529 【R2&R5归一版本+ V100R005C00SPC003B015+VASA】VASA环境下，
                 * 阵列以iscsi组网，vCenter不能从VASA Provider上查询出LUN信息 begin
                 **/
                return wwn;
                /**
                 * S5500T P11G-5529 【R2&R5归一版本+ V100R005C00SPC003B015+VASA】VASA环境下，
                 * 阵列以iscsi组网，vCenter不能从VASA Provider上查询出LUN信息 end
                 **/
            }
            return Long.toHexString(portWWN);
        }
    }

    /**
     * <从阵列查询回来的PORTWWN与NODEWWN是十六进制表示的字符串。需要转换为VASA需要的十进制字符串>
     *
     * @param hexStr 方法参数：hexStr
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String portWWNHex2Decimal(String hexStr) {
        if (null == hexStr || "".equals(hexStr)) {
            return "";
        }

        try {
            long returnValue = Long.parseLong(hexStr, MagicNumber.INT16);
            return String.valueOf(returnValue);
        } catch (NumberFormatException e) {
            LOGGER.error("get decimal format form device error. value is :" + hexStr);
            return hexStr;
        }
    }

//    /**
//     * <获取IP的hash值>
//     * @param context 方法参数：context
//     * 
//     * @return int [返回类型说明]
//     * @see [类、类#方法、类#成员]
//     */
//    public static int getIpsHashCode(DeviceContext context)
//    {
//        // 第一种排序方式，第一个IP与第二个IP
//        StringBuffer orderOne = new StringBuffer();
//        orderOne.append(context.getPreferedIP());
//        orderOne.append(context.getOtherIP());
//        int orderOneHashCode = orderOne.toString().hashCode();
//
//        // 第二种排序方式
//        StringBuffer orderTwo = new StringBuffer();
//        orderTwo.append(context.getOtherIP());
//        orderTwo.append(context.getPreferedIP());
//        int orderTwoHashCode = orderTwo.toString().hashCode();
//
//        return orderOneHashCode + orderTwoHashCode;
//    }


    /**
     * <转换字符串数组为字符>
     *
     * @param target 方法参数：target
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String convertArrayToStr(String[] target) {
        String begin = "[";
        String end = "]";
        StringBuilder sbStr = new StringBuilder();
        if (null != target) {
            sbStr.append(begin);
            for (String str : target) {
                sbStr.append(str + ",");
            }
            if (sbStr.toString().endsWith(",") && sbStr.length() > 1) {
                sbStr.setLength(sbStr.length() - 1);
            }
            sbStr.append(end);
        }

        return sbStr.toString();
    }

    /**
     * <转换字符串数组为字符>
     *
     * @param target 方法参数：target
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String convertArrayToStr(List<String> target) {
        if (null == target) {
            return null;
        }
        return convertArrayToStr(target.toArray(new String[target.size()]));
    }

//    /**
//     * <是否支持此设备类型>
//     * 
//     * @return DeviceType [返回类型说明]
//     * @param deviceType 方法参数：checkedDeviceType
//     */
//    public static DeviceType getDeviceType(String deviceType)
//    {
//        return SupportDeviceType.getDeviceType(deviceType);
//    }

    /**
     * <判断ID是否是有效的>
     *
     * @param checkedId  方法参数：checkedId
     * @param entityType 方法参数：entityType
     * @return boolean 返回结果
     * @see [类、类#方法、类#成员]
     */
    public static boolean isIdValid(String checkedId, String entityType) {
        if (null == checkedId) {
            return false;
        }

        if (checkedId.indexOf(entityType) == -1) {
            return false;
        }

        return true;
    }

    /**
     * <检测arrayID是否是invalid的>
     *
     * @param arrayId 方法参数：arrayId
     * @throws InvalidArgument [参数说明]
     * @throws throws          [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void checkIsArrayIdValid(String[] arrayId) throws InvalidArgument {
        if (Util.isEmpty(arrayId)) {
            return;
        }

        for (String checkId : arrayId) {
            LOGGER.debug("ArrayId:" + checkId);
        }

        // 检测ID是否是有效的，根据构造ID的方法，StorageArray+DeviceID
        for (String checkId : arrayId) {
            if (!VASAUtil.isIdValid(checkId, EntityTypeEnum.STORAGE_ARRAY.value() + ":")) {
                LOGGER.error("InvalidArgument/Some array id is invalid. id is " + checkId);
                throw FaultUtil.invalidArgument();
            }
        }
    }

    /**
     * <判断端口ID是否有效>
     *
     * @param portIds 方法参数：portIds
     * @throws InvalidArgument [参数说明]
     * @throws throws          [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void checkIsPortIdValid(String[] portIds) throws InvalidArgument {
        // 判断ID是否是有效的
        if (Util.isEmpty(portIds)) {
            return;
        }

        for (String checkId : portIds) {
            if (!VASAUtil.isIdValid(checkId, EntityTypeEnum.STORAGE_PORT.value())) {
                LOGGER.error("_StoragePort is invalide:" + checkId);
                throw FaultUtil.invalidArgument();
            }
        }
    }

    /**
     * <判断ProcessorID是否有效>
     *
     * @param requestedProcessorIds 方法参数：requestedProcessorIds
     * @throws InvalidArgument [参数说明]
     * @throws throws          [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void checkIsProcessorIdValide(String[] requestedProcessorIds) throws InvalidArgument {
        if (Util.isEmpty(requestedProcessorIds)) {
            return;
        }

        // 判断ID是否是有效的，根据构造ID的方式
        for (String checkId : requestedProcessorIds) {
            if (checkId.isEmpty()) {
                continue;
            }
            if (!VASAUtil.isIdValid(checkId, EntityTypeEnum.STORAGE_PROCESSOR.value())) {
                LOGGER.error("InvalidArgument/ProcessorID is invalide:" + checkId);
                throw FaultUtil.invalidArgument(checkId);
            }
        }
    }

    /**
     * <判断lunID是否有效>
     *
     * @param lunIds     方法参数：lunIds
     * @param allowEmpty 方法参数：allowEmpty
     * @throws InvalidArgument [参数说明]
     * @throws throws          [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void checkIsLunIdInvalid(String[] lunIds, boolean allowEmpty) throws InvalidArgument {
        if (Util.isEmpty(lunIds)) {
            if (!allowEmpty) {
                LOGGER.error("_StorageLun ids is null.");
                throw FaultUtil.invalidArgument();
            }

            return;
        }

        // 判断ID是否是有效的，根据构造ID的方式
        for (String checkId : lunIds) {
            if (!VASAUtil.isIdValid(checkId, EntityTypeEnum.STORAGE_LUN.value())) {
                LOGGER.error("_StorageLun is invalide:" + checkId);
                throw FaultUtil.invalidArgument();
            }
        }
    }

    /**
     * <判断fileSystemId是否有效>
     *
     * @param fileSystemId 方法参数：fileSystemId
     * @param allowEmpty   方法参数：allowEmpty
     * @throws InvalidArgument [参数说明]
     * @throws throws          [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void checkIsFileSystemIdInvalid(String[] fileSystemId, boolean allowEmpty) throws InvalidArgument {
        if (Util.isEmpty(fileSystemId)) {
            if (!allowEmpty) {
                LOGGER.error("InvalidArgument/_StorageLun ids is null.");
                throw FaultUtil.invalidArgument();
            }

            return;
        }

        // 判断ID是否是有效的，根据构造ID的方式
        for (String checkId : fileSystemId) {
            if (!VASAUtil.isIdValid(checkId, EntityTypeEnum.STORAGE_FILE_SYSTEM.value())) {
                LOGGER.error("InvalidArgument/_FileSystemId is invalide:" + checkId);
                throw FaultUtil.invalidArgument();
            }
        }
    }

    /**
     * <判断uniqueId是否有效>
     *
     * @param uniqueId   方法参数：uniqueId
     * @param allowEmpty 方法参数：allowEmpty
     * @throws InvalidArgument [参数说明]
     * @throws throws          [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void checkIsUniqueIdInvalid(String[] uniqueId, boolean allowEmpty) throws InvalidArgument {
        if (Util.isEmpty(uniqueId)) {
            if (!allowEmpty) {
                LOGGER.error("uniqueId is null.");
                throw FaultUtil.invalidArgument();
            }

            return;
        }

        // 判断ID是否是有效的，根据构造ID的方式
        for (String checkId : uniqueId) {
            if (!VASAUtil.isIdValid(checkId, EntityTypeEnum.STORAGE_LUN.value())
                    && !VASAUtil.isIdValid(checkId, EntityTypeEnum.STORAGE_FILE_SYSTEM.value())) {
                LOGGER.error("uniqueId is invalide:" + checkId);
                throw FaultUtil.invalidArgument();
            }
        }
    }

    /**
     * <判断lunID是否有效>
     *
     * @param capIds 方法参数：capIds
     * @throws InvalidArgument [参数说明]
     * @throws throws          [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void checkIsCapabilityIdInvalid(String[] capIds) throws InvalidArgument {
        if (Util.isEmpty(capIds)) {
            return;
        }

        // 判断ID是否是有效的，根据构造ID的方式
        for (String checkId : capIds) {
            if (!VASAUtil.isIdValid(checkId, EntityTypeEnum.STORAGE_CAPABILITY.value())) {
                LOGGER.error("_StorageCapability is invalide:" + checkId);
                throw FaultUtil.invalidArgument();
            }
        }
    }

    /**
     * <得到usageContext的唯一标示>
     *
     * @param usageContext 方法参数：usageContext
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getUcUUID(UsageContext usageContext) {

        // 这里应该不需要自己针对usagecontext计算uuid 因为自带了该属性值
        // 注释掉 g00250185 2014-1-18
        // long uuid = 0L;
        // HostInitiatorInfo[] hostInitiators =
        // ListUtil.list2ArrayHostInit(usageContext.getHostInitiator());
        // if (null != hostInitiators)
        // {
        // for (HostInitiatorInfo hostInitiator : hostInitiators)
        // {
        // if (null != hostInitiator.getIscsiIdentifier())
        // {
        // uuid = uuid + hostInitiator.getIscsiIdentifier().hashCode();
        // }
        //
        // if (null != hostInitiator.getNodeWwn())
        // {
        // uuid = uuid + hostInitiator.getNodeWwn().hashCode();
        // }
        //
        // if (null != hostInitiator.getPortWwn())
        // {
        // uuid = uuid + hostInitiator.getPortWwn().hashCode();
        // }
        //
        // if (null != hostInitiator.getUniqueIdentifier())
        // {
        // uuid = uuid
        // + hostInitiator.getUniqueIdentifier().hashCode();
        // }
        // }
        // }
        //
        // return String.valueOf(uuid);
        // 注释掉结束 g00250185 2014-1-18
        if (usageContext.getHostGuid() != null) {
            return usageContext.getHostGuid();
        } else {
            return usageContext.getVcGuid();
        }
    }

    /**
     * convertHostInitiators
     *
     * @param hostInitiatorIds hostInitiatorIds
     * @return [参数说明]
     */
    public static List<String> convertHostInitiators(HostInitiatorInfo[] hostInitiatorIds) {
        List<String> hostInitiators = new ArrayList<String>();
        for (HostInitiatorInfo info : hostInitiatorIds) {
            String iscsiIndentifier = info.getIscsiIdentifier();
            String nodeWWN = info.getNodeWwn();
            String portWWN = info.getPortWwn();
            if (null != iscsiIndentifier && iscsiIndentifier.trim().length() > 0) {
                hostInitiators.add(iscsiIndentifier);
            }

            if (null != nodeWWN && nodeWWN.trim().length() > 0) {
                hostInitiators.add(nodeWWN);
            }

            if (null != portWWN && portWWN.trim().length() > 0) {
                hostInitiators.add(portWWN);
            }
        }
        return hostInitiators;
    }

    /**
     * convertHostInitiators
     *
     * @param hostInitiatorIds hostInitiatorIds
     * @return [参数说明]
     */
    public static List<String> convertHostInitiators(List<HostInitiatorInfo> hostInitiatorIds) {
        List<String> hostInitiators = new ArrayList<String>();
        for (HostInitiatorInfo info : hostInitiatorIds) {
            String iscsiIndentifier = info.getIscsiIdentifier();
            String nodeWWN = info.getNodeWwn();
            String portWWN = info.getPortWwn();
            if (null != iscsiIndentifier && iscsiIndentifier.trim().length() > 0) {
                hostInitiators.add(iscsiIndentifier);
            }

            if (null != nodeWWN && nodeWWN.trim().length() > 0) {
                hostInitiators.add(nodeWWN);
            }

            if (null != portWWN && portWWN.trim().length() > 0) {
                hostInitiators.add(portWWN);
            }
        }
        return hostInitiators;
    }

    /**
     * convertMountInfos
     *
     * @param mountInfos mountInfos
     * @return [参数说明]
     */
    public static List<String> convertMountInfos(List<MountInfo> mountInfos) {
        List<String> returnValues = new ArrayList<String>();
        for (MountInfo mountInfo : mountInfos) {
            returnValues.add(new StringBuilder().append(mountInfo.getServerName()).append(":").
                    append(mountInfo.getFilePath()).toString());
        }

        return returnValues;
    }

    /**
     * 生成存储能力的uuid
     *
     * @param type   type
     * @param unique unique
     * @return String
     */
    public static String buildCapabilityUUID(String type, String unique) {
        StringBuilder uuid = new StringBuilder();
        uuid.append(Vendor.Huawei.name());
        uuid.append(":");
        uuid.append(type);
        uuid.append(":");
        uuid.append(unique);

        return uuid.toString();
    }

    /**
     * 生成存储能力名称
     *
     * @param name name
     * @return String
     */
    public static String buildCapabilityName(String name) {
        StringBuilder capName = new StringBuilder();
        capName.append(Vendor.Huawei.name());
        capName.append(":");
        capName.append(name.trim());

        return capName.toString();
    }

    /**
     * 根据唯一标识截取其中的阵列标识
     *
     * @param id uniqueid
     * @return device sn
     */
    public static String getArraySnFromUniqueId(String id) {
        if (id == null || id.length() == 0) {
            return "";
        } else {
            String[] strs = id.split(":");
            if (strs.length > 1) {
                return strs[1];
            }
            return id;
        }
    }

    /**
     * getDefaultRetryTimeInSeconds
     *
     * @return defaultRetryTimeInSeconds
     */
    public static int getDefaultRetryTimeInSeconds() {
        return defaultRetryTimeInSeconds;
    }

    /**
     * setDefaultRetryTimeInSeconds
     *
     * @param defaultInSeconds 默认告警刷新时间
     */
    public static void setDefaultRetryTimeInSeconds(int defaultInSeconds) {
        defaultRetryTimeInSeconds = defaultInSeconds;
    }

//     public static void main(String[] args)
//     {
//    	String id = "D200000100000000";
//    	System.out.println(convertSecondaryId(id));
//     }

    /**
     * parseEventParamEntity
     *
     * @param str      str
     * @param language langguage
     * @return EventParamEntity
     */
    public static EventParamEntity parseEventParamEntity(String str, String language) {
        String[] splits = str.split("_");
        if (splits.length == MagicNumber.INT3) {
            return new EventParamEntity(splits[0], splits[1], splits[MagicNumber.INT2], language);
        }
        return null;
    }


    /**
     * initiatorToString
     *
     * @param infos HostInitiatorInfo[]
     * @return String
     */
    public static String initiatorToString(HostInitiatorInfo[] infos) {
        if (infos == null || infos.length == 0) {
            return "NULL";
        }
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        for (HostInitiatorInfo info : infos) {
            sb.append("iscsiidentifier:").append(info.getIscsiIdentifier()).append(" nodewwn:")
                    .append(info.getNodeWwn()).append(" portwwn:").append(info.getPortWwn()).append(" uniqueidentifier:")
                    .append(info.getUniqueIdentifier()).append(";");
        }
        sb.append(']');
        return sb.toString();
    }

    public static String getArrayID(String arrayUniqueID) {
        if (null == arrayUniqueID || !arrayUniqueID.contains(":")) {
            return null;
        }

        String[] splits = arrayUniqueID.split(":");
        if (splits.length != 2) {
            return null;
        }
        return splits[1];
    }

    /**
     * getUuidOfInitiator
     *
     * @param hostInitiator HostInitiatorInfo
     * @return String
     */
    public static String getUuidOfInitiator(HostInitiatorInfo hostInitiator) {
        int uuid = 0;
        if (null != hostInitiator.getIscsiIdentifier()) {
            uuid = uuid + hostInitiator.getIscsiIdentifier().hashCode();
        }

        if (null != hostInitiator.getNodeWwn()) {
            uuid = uuid + hostInitiator.getNodeWwn().hashCode();
        }

        if (null != hostInitiator.getPortWwn()) {
            uuid = uuid + hostInitiator.getPortWwn().hashCode();
        }

        if (null != hostInitiator.getUniqueIdentifier()) {
            uuid = uuid + hostInitiator.getUniqueIdentifier().hashCode();
        }
        return String.valueOf(uuid);
    }

    /**
     * 判断是否是NULL或者是空串或者是"NULL"
     *
     * @param str str
     * @return boolean
     */
    public static boolean isNull(String str) {
        if (str == null) {
            return true;
        } else {
            String x = str.trim();
            if (x.equals("") || x.equalsIgnoreCase("null")) {
                return true;
            }
        }
        return false;
    }

    /**
     * <分割字符串> <功能详细描述>
     *
     * @param str   方法参数：str
     * @param regex 方法参数：regex
     * @return String[] [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String[] convertToStringArray(String str, String regex) {
        if (null == str || "".equals(str)) {
            return new String[0];
        } else {
            return str.split(regex);
        }
    }

    /**
     * <分割字符串> <功能详细描述>
     *
     * @param str    方法参数：str
     * @param regex  方法参数：regex
     * @param suffix 方法参数：suffix
     * @return String[] [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String[] convertToStringArrayWithSuffix(String str, String regex, String suffix) {
        if (null == str || "".equals(str)) {
            return new String[0];
        } else {

            String[] arrayStr = str.split(regex);
            for (int i = 0; i < arrayStr.length; ++i) {
                arrayStr[i] = new StringBuilder().append(arrayStr[i]).append("(SN:").append(suffix).append(")").toString();
            }

            return arrayStr;
        }
    }

    /**
     * <查询LUN健康状态>
     *
     * @param healthState 健康状态
     * @return String [返回类型说明]
     */
    public static String getStorageLunStatus(HealthState healthState) {
        switch (healthState) {
            case NORMAL:
                return AlarmStatusEnum.GREEN.value();
            case FAULTED:
                return AlarmStatusEnum.RED.value();
            default:
                return AlarmStatusEnum.YELLOW.value();
        }
    }

    public static long findMaxEventSN(List<Event> records, String arrayid) {
        // C99获取告警不一致
        if (DataUtil.getInstance().isDeviceSupportSPeciality(arrayid, ProductSpeciality.HAS_SVP)) {
            if (!records.isEmpty()) {
                return records.get(records.size() - 1).getDeviceSN();
            }
        }

        if (records.isEmpty()) {
            return 0;
        }

        Event maxSNEvent = Collections.max(records, new Comparator<Event>() {
            /**
             * 方法 ： compare
             *
             * @param o1 方法参数：o1
             * @param o2 方法参数：o2
             * @return int 返回结果
             */
            public int compare(Event o1, Event o2) {
                long o1SN = o1.getDeviceSN();
                long o2SN = o2.getDeviceSN();
                return o1SN < o2SN ? -1 : (o1SN == o2SN ? 0 : 1);
            }
        });

        return maxSNEvent.getDeviceSN();
    }

    /**
     * 查询设备相关的LUN信息，保存再缓存中
     *
     * @param arrayid arrayid
     */
    public static void saveStorageLuns(String arrayid) {
        Map<String, String[]> hostInitiatorIds = DataUtil.getInstance().getHostInitiatorIds();
        List<DLun> storageLuns = null;
        List<DLun> arrayStorageLuns = new ArrayList<DLun>(0);

        if (hostInitiatorIds.isEmpty()) {
            LOGGER.info("hostInitiatorIds is empty");
            return;
        }

        Iterator<String[]> iterrator = hostInitiatorIds.values().iterator();
        String[] hostInitiatorId = null;
        while (iterrator.hasNext()) {
            hostInitiatorId = iterrator.next();
            try {
                storageLuns = DiscoverServiceImpl.getInstance().getStorageLuns(arrayid, hostInitiatorId);
                arrayStorageLuns.addAll(storageLuns);
                LOGGER.info("saveStorageLuns getStorage lun success");
            } catch (Exception e) {
                LOGGER.error("pupLateConfigEvents error,StorageFault", e);
            }
        }

        LOGGER.info("Query storageLun size is:" + arrayStorageLuns.size());
//        String deviceID = String.valueOf(VASAUtil.getIpsHashCode(deviceContext));
        DataUtil.getInstance().setStorageLuns(arrayid, arrayStorageLuns);
    }

    public static boolean isIPV4(String addr) {
        if (addr == null) {
            return false;
        }

        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }

        // 判断IP格式和范围
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();

        return ipAddress;
    }

    /**
     * 得到最大的告警流水号
     *
     * @param alarms 方法参数：alarms
     * @return long 返回结果
     */
    public static long getMaxEventSN(List<Event> alarms) {
        long alarmSN = 0L;
        long sn = 0L;
        for (Event event : alarms) {
            if (event.getEventID() == VASAUtil.THIN_LUN_ALARM_ID) {
                continue;
            }

            sn = event.getDeviceSN();
            if (sn >= VASAUtil.HVS_SVP_ALARM_SN) {
                continue;
            }

            if (sn > alarmSN) {
                alarmSN = sn;
            }
        }

        return alarmSN;
    }

    public static Event getMaxStartTimeEvent(List<Event> events) {
        Event maxStartTimeEvent = events.get(0);
        long startTime = 0L;
        for (Event event : events) {
            startTime = event.getStartTime();
            if (startTime > maxStartTimeEvent.getStartTime()) {
                maxStartTimeEvent = event;
            }
        }

        return maxStartTimeEvent;
    }

    /**
     * convertNameValuePairToString
     *
     * @param pairs List<NameValuePair>
     * @return String
     */
    public static String convertNameValuePairToString(List<NameValuePair> pairs) {
        if (pairs == null) {
            return "";
        }
        return convertNameValuePairToString(pairs.toArray(new NameValuePair[pairs.size()]));
    }

    /**
     * convertNameValuePairToString
     *
     * @param pairs NameValuePair[]
     * @return String
     */
    public static String convertNameValuePairToString(NameValuePair[] pairs) {
        StringBuffer sb = new StringBuffer(']');
        if (pairs != null && pairs.length != 0) {
            for (NameValuePair p : pairs) {
                sb.append(p.getParameterName()).append(':').append(p.getParameterValue()).append(';');
            }
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * 将主机信息是指到缓存
     *
     * @param uc UsageContext
     */
    public static void saveHostInitiatorIds(UsageContext uc) {
        try {
            String ucUUID = VASAUtil.getUcUUID(uc);
            String[] hosts = SecureConnectionService.getInstance().getHostInitiatorIds();
            DataUtil.getInstance().setHostInitiatorIds(ucUUID, hosts);
            LOGGER.info("saveHostInitiatorIds:" + VASAUtil.convertArrayToStr(hosts));
        } catch (InvalidSession e) {
            LOGGER.error("getAlarms, set hostInitiatorIds error.InvalidSession", e);
        } catch (StorageFault e) {
            LOGGER.error("getAlarms, set hostInitiatorIds error.StorageFault", e);
        }
    }

    /**
     * <转换Alarm 和 Event的parameters>
     *
     * @param alarmParam 方法参数：alarmParam
     * @param deviceSn   设备序列号
     * @return NameValuePair[] [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static NameValuePair[] convertNameValuePair(String alarmParam, String deviceSn) {
        List<NameValuePair> nameValues = new ArrayList<NameValuePair>(0);

        if (null != alarmParam && !alarmParam.equals("")) {
            String[] params = alarmParam.split(",");
            NameValuePair nameValuePair = null;
            int i = 0;
            for (; i < params.length; i++) {
                nameValuePair = new NameValuePair();
                nameValuePair.setParameterName(VMSG_PARAM + i);
                nameValuePair.setParameterValue(params[i].trim());
                nameValues.add(nameValuePair);
            }
            // nameValues.add(new )
            NameValuePair arraySnPair = new NameValuePair();
            arraySnPair.setParameterName(VMSG_ARRAY_SRC);
            arraySnPair.setParameterValue(deviceSn);
            nameValues.add(arraySnPair);
        }

        return nameValues.toArray(new NameValuePair[nameValues.size()]);
    }

    /**
     * <得到设备ID 阵列ID的构造方式：StorageArray:deviceID>
     *
     * @param event        方法参数：event
     * @param storageArray 方法参数：storageArray
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getDeviceID(Event event, DArray storageArray) {
        String deviceID = "";
        /***
         * S5500T P11G-5549 【2012-10-27基线+ VASA】单边控制器连接网线导致vcenter上的LUN信息不能更新
         * begin
         **/
        if (storageArray != null && storageArray.getUniqueIdentifier() != null) {
            String storageUniqueIdentifer[] = storageArray.getUniqueIdentifier().split(":");

            if (storageUniqueIdentifer.length == MagicNumber.INT2) {
                deviceID = storageUniqueIdentifer[1];
            }
        } else if (event != null) {

            deviceID = event.getDeviceId();
        }
        /***
         * S5500T P11G-5549 【2012-10-27基线+ VASA】单边控制器连接网线导致vcenter上的LUN信息不能更新
         * end
         **/

        return deviceID;
    }

    /**
     * <得到转换后的ID>
     *
     * @param deviceID   方法参数：deviceID
     * @param entityType 方法参数：entityType
     * @param unique     方法参数：unique
     * @return String [返回类型说明]
     */
    public static String getStorageEntityID(String deviceID, String entityType, String unique) {
        StringBuilder uuID = new StringBuilder();

        uuID.append(deviceID + ":");
        uuID.append(entityType + ":");
        uuID.append(unique);
        return uuID.toString();
    }

    /**
     * <检测上报的LUN是否在请求的UC中存在>
     *
     * @param uc    方法参数：uc
     * @param lunId [参数说明]
     * @param event 方法参数：event
     * @return boolean 返回结果
     */
    public static boolean isStorageLunExistInUC(UsageContext uc, String lunId, Event event) {
        // List<StorageLun> storageLuns =
        // DataUtil.getInstance().getStorageLunsByDeviceID(event.getIdentifier().getDeviceID());
        //
        Set<String> luns = DataUtil.getInstance().getLunByUsageContext(uc.getVcGuid());
        //避免vvol场景出现大堆没有映射的thin lun不断走这个分支，导致线程耗时过长
//        if (luns.size() == 0)
//        {
//            // 如果没有尝试一次重新找
//            // refind all luns for this uc
//            // try
//            // {
//            List<DLun> lunsQueryed;
//            try
//            {
//                lunsQueryed = StorageService.getInstance().queryStorageLuns(
//                		StorageService.getInstance().getHostInitiatorIdsFromUsageContext(uc, -1));
//                DataUtil.getInstance().addLunsToUsageContext(lunsQueryed, VASAUtil.getUcUUID(uc));
//                luns = DataUtil.getInstance().getLunByUsageContext(VASAUtil.getUcUUID(uc));
//            }
//            catch (InvalidArgument e)
//            {
//                LOGGER.error("Cannot query the luns,error:", e);
//            }
//            catch (StorageFault e)
//            {
//            	LOGGER.error("Cannot query the luns,error:", e);
//            }
//            // }
//            // catch (Exception e)
//            // {
//            // LogManager.error("Cannot query the luns,error:", e);
//            // }
//
//        }
        LOGGER.debug("Retrive oraginel event getStorageLun size is:" + luns.size());
        // if (storageLuns.isEmpty())
        // {
        // VasaLogManager
        // .debug(log, "isStorageLunExistInUC,storageLun is Empty:" +
        // event.getIdentifier().getDeviceID());
        // return false;
        // }
        //
        // boolean isStorageLunExsit = false;
        // for (StorageLun storageLun : storageLuns)
        // {
        // if (storageLun.getUniqueIdentifier().equalsIgnoreCase(lunId))
        // {
        // isStorageLunExsit = true;
        // break;
        // }
        // }

        return luns.contains(lunId);
    }

    /**
     * getLastCharNoLengthLimit
     *
     * @param target target
     * @return String
     */
    public static String getLastCharNoLengthLimit(String target) {
        if (null == target || target.length() == 0) {
            return "";
        }
        target = target.trim();
        return "" + target.charAt(target.length() - 1);
    }

    /**
     * getFirstCharNoLengthLimit
     *
     * @param target target
     * @return String
     */
    public static String getFirstCharNoLengthLimit(String target) {
        if (null == target || target.length() == 0) {
            return "";
        }
        target = target.trim();
        return "" + target.charAt(0);
    }

    /**
     * <得到端口的ID组成部分，控制器ID：接口卡ID：端口ID>
     *
     * @param controllerID 方法参数：controllerID
     * @param cardID       方法参数：cardID
     * @param portID       方法参数：portID
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getPortCombinationID(String controllerID, String cardID, String portID) {
        StringBuffer uuID = new StringBuffer();

        uuID.append(controllerID + ":");
        uuID.append(cardID + ":");
        uuID.append(portID);
        return uuID.toString();
    }

    /**
     * <得到字符的最后一位>
     *
     * @param target 方法参数：target
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getLastChar(String target) {
        if (null == target) {
            return "";
        }

        if (target.trim().length() == MagicNumber.INT2) {
            return target.trim().substring(1);
        }

        return target;
    }

    /**
     * <转换控制器ID>
     *
     * @param target 方法参数：target
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getContorllerID(String target) {
        if (null == target) {
            return "";
        }

        String controllerID = "";
        target = target.trim();
        if (target.length() == MagicNumber.INT2) {
            String controllerIDForString = target.substring(0, 1);
            if ("A".equals(controllerIDForString)) {
                controllerID = "0";
            } else if ("B".equals(controllerIDForString)) {
                controllerID = "1";
            }
        } else {
            return target;
        }

        return controllerID;
    }

    /**
     * 重新替换参数
     *
     * @param events StorageEvent[]
     */
    public static void resetEventParams(List<StorageEvent> events) {
        if (events == null) {
            return;
        }

        int size = events.size();
        StorageEvent event = null;
        NameValuePair[] pairs = null;
        EventParamEntity entity = null;
        int pairsLength = 0;
        NameValuePair pair = null;
        DataUtil dataUtil = DataUtil.getInstance();
        String result = null;
        for (int i = 0; i < size; i++) {
            event = events.get(i);
            if (!needReplaceParams(event)) {
                continue;
            }
            pairs = ListUtil.list2ArrayNV(event.getParameterList());
            if (pairs != null && pairs.length > 1) {
                pairsLength = pairs.length;
                for (int j = 0; j < pairsLength - 1; j++) {
                    pair = pairs[j];
                    entity = new EventParamEntity(event.getMessageId(), pair.getParameterName(),
                            pair.getParameterValue(), "en");
                    result = dataUtil.getParamKey(entity);
                    if (result != null) {
                        // replace
                        pair.setParameterValue(result);
                        LOGGER.debug("Suc replace param:" + result);
                        // event.setParameterList(pairs);
                    }
                }
            }
        }
    }

    /**
     * 是否需要替换参数
     *
     * @param event StorageEvent
     * @return boolean
     */
    public static boolean needReplaceParams(StorageEvent event) {
        if (event.getEventType().equalsIgnoreCase(EventTypeEnum.CONFIG.value())) {
            return false;
        }
        return true;
    }

    /**
     * 重新设置告警参数
     *
     * @param alarms StorageAlarm[]
     */
    public static void resetAlarmParams(StorageAlarm[] alarms) {
        if (alarms == null) {
            return;
        }

        int size = alarms.length;
        StorageAlarm alarm = null;
        NameValuePair[] pairs = null;
        EventParamEntity entity = null;
        int pairsLength = 0;
        NameValuePair pair = null;
        DataUtil dataUtil = DataUtil.getInstance();
        String result = null;
        for (int i = 0; i < size; i++) {
            alarm = alarms[i];
            if (!needReplaceParams(alarm)) {
                continue;
            }
            pairs = ListUtil.list2ArrayNV(alarm.getParameterList());

            if (pairs != null && pairs.length > 1) {
                pairsLength = pairs.length;
                for (int j = 0; j < pairsLength - 1; j++) {
                    pair = pairs[j];
                    entity = new EventParamEntity(alarm.getMessageId(), pair.getParameterName(),
                            pair.getParameterValue(), "en");
                    result = dataUtil.getParamKey(entity);
                    if (result != null) {
                        // replace
                        pair.setParameterValue(result);
                        LOGGER.debug("Suc replace param: alarm:" + alarm.getMessageId() + " parm:"
                                + pair.getParameterName() + ":" + pair.getParameterValue() + result);
                        // event.setParameterList(pairs);
                    }
                }
            }
        }
    }

    /**
     * 是否需要替换参数
     *
     * @param alarm StorageAlarm
     * @return boolean
     */
    public static boolean needReplaceParams(StorageAlarm alarm) {
        if (alarm.getAlarmType().equalsIgnoreCase(AlarmTypeEnum.OBJECT.value())) {
            return true;
        }
        return false;
    }

    public static TypeInfo convert2VMwareType(int type) {
        if (0 == type) {
            TypeInfo typeInfo = new TypeInfo();
            typeInfo.setTypeName(BuiltinTypesEnum.XSD_LONG.value());
            return typeInfo;
        } else if (1 == type) {
            TypeInfo typeInfo = new TypeInfo();
            typeInfo.setTypeName(BuiltinTypesEnum.XSD_BOOLEAN.value());
            return typeInfo;
        } else if (2 == type) {
            TypeInfo typeInfo = new TypeInfo();
            typeInfo.setTypeName(BuiltinTypesEnum.XSD_STRING.value());
            return typeInfo;
        } else if (3 == type) {
            GenericTypeInfo typeInfo = new GenericTypeInfo();
            typeInfo.setTypeName(BuiltinTypesEnum.XSD_LONG.value());
            typeInfo.setGenericTypeName(BuiltinGenericTypesEnum.VMW_RANGE.value());
            return typeInfo;
        } else if (4 == type) {
            GenericTypeInfo typeInfo = new GenericTypeInfo();
            typeInfo.setTypeName(BuiltinTypesEnum.XSD_STRING.value());
            typeInfo.setGenericTypeName(BuiltinGenericTypesEnum.VMW_SET.value());
            return typeInfo;
        } else {
            TypeInfo typeInfo = new TypeInfo();
            typeInfo.setTypeName(BuiltinTypesEnum.XSD_STRING.value());
            return typeInfo;
        }
    }

    public static String convert2VmwareTypeName(int type) {
        if (0 == type) {
            return BuiltinTypesEnum.XSD_LONG.value();
        } else if (1 == type) {
            return BuiltinTypesEnum.XSD_BOOLEAN.value();
        } else if (2 == type) {
            return BuiltinTypesEnum.XSD_STRING.value();
        } else if (3 == type) {
            return BuiltinGenericTypesEnum.VMW_RANGE.value();
        } else if (4 == type) {
            return BuiltinGenericTypesEnum.VMW_SET.value();
        } else {
            return BuiltinTypesEnum.XSD_STRING.value();
        }
    }

    public static Object convert2VmwareValue(int type, int requirementsTypeHint, String value) {
        if (0 == requirementsTypeHint) {
            Long objValue = Long.valueOf(value);
            return objValue;
        } else if (1 == requirementsTypeHint) {
            Boolean objValue = Boolean.valueOf(value);
            return objValue;
        } else if (2 == requirementsTypeHint) {
            return value;
        } else if (3 == requirementsTypeHint) {
            Range objValue = new Range();
            String[] vals = value.split("-");
            objValue.setMin(Long.valueOf(vals[0]).longValue());
            objValue.setMax(Long.valueOf(vals[1]).longValue());
            return objValue;
        } else if (4 == requirementsTypeHint) {
            DiscreteSet objValue = new DiscreteSet();
            String[] vals = value.split(",");
            for (String val : vals) {
                objValue.getValues().add(val);
            }
            return objValue;
        } else {
            return value;
        }
    }

    public static List<String> getUpdateIps(List<String> newHostIps, List<String> oldHostIdIps) {
        List<String> diffIps = new ArrayList<String>();
        if (newHostIps != null) {
            for (String newIp : newHostIps) {
                if (VASAUtil.isIPV4(newIp)) {
                    boolean isNotFind = true;
                    if (oldHostIdIps != null) {
                        for (String ip : oldHostIdIps) {
                            if (ip.equals(newIp)) {
                                isNotFind = false;
                            }
                        }
                    }
                    if (isNotFind) {
                        diffIps.add(newIp);
                    }
                }
            }
        }
        return diffIps;
    }


    public static StoragePolicy matchContainer(StorageProfile policyProfile, String containerId, String defaultSpaceEfficiency,
                                               StorageProfile profileToInsert) throws NotFound, InvalidProfile {
        SubProfile subProfile = policyProfile.getConstraints().getSubProfiles().get(0);
        if (null == subProfile) {
            return null;
        }
        return matchContainer(policyProfile, subProfile, containerId, defaultSpaceEfficiency, profileToInsert);
    }

    public static StoragePolicy matchContainer(StorageProfile policyProfile, String containerId, StorageProfile profileToInsert) throws NotFound, InvalidProfile {
        SubProfile subProfile = policyProfile.getConstraints().getSubProfiles().get(0);
        if (null == subProfile) {
            LOGGER.error("matchContainer false! subProfile is null.");
            return null;
        }
        return matchContainer(policyProfile, subProfile, containerId, null, profileToInsert);
    }

    private static int convertIOType2Numerical(String ioType) {
        if ("Read/Write I/Os".equalsIgnoreCase(ioType)) {
            return 2;
        }

        if ("Write I/O".equalsIgnoreCase(ioType)) {
            return 1;
        } else if ("Read I/O".equalsIgnoreCase(ioType)) {
            return 0;
        } else {
            return 2;
        }
    }

    public static JSONObject convertStoragePolicy2QosSpec(StoragePolicy profile) {
        JSONObject qos_specs = new JSONObject();
        boolean isQoSNull = true;
        try {
            qos_specs.put("name", buildDisplayName("policy"));
            qos_specs.put("IOType", convertIOType2Numerical(profile.getQosControllerType()));

            boolean isUp = true;
            if ("Control lower bound".equalsIgnoreCase(profile.getQosControllerPolicy())) {
                isUp = false;
            }

            if (null != profile.getQosControllerType() && !"".equals(profile.getQosControllerType())) {
                //qos_specs.put("IOType", convertIOType2Numerical(profile.getQosControllerType()));
                isQoSNull = false;
            }

            /**
             *  修改findbugs问题：EC_UNRELATED_TYPES start
             */
            if (0 != profile.getQosControllerObjectMinIOPS() && !"".equals(String.valueOf(profile.getQosControllerObjectMinIOPS()))) {
                qos_specs.put("minIOPS", profile.getQosControllerObjectMinIOPS());
                isQoSNull = false;
            }
            if (0 != profile.getQosControllerObjectMaxIOPS() && isUp
                    && !"".equals(String.valueOf(profile.getQosControllerObjectMaxIOPS()))) {
                qos_specs.put("maxIOPS", profile.getQosControllerObjectMaxIOPS());
                isQoSNull = false;
            }
            if (0 != profile.getQosControllerObjectMinBandwidth()
                    && !"".equals(String.valueOf(profile.getQosControllerObjectMinBandwidth()))) {
                qos_specs.put("minBandWidth", profile.getQosControllerObjectMinBandwidth());
                isQoSNull = false;
            }
            if (0 != profile.getQosControllerObjectMaxBandwidth() && isUp
                    && !"".equals(String.valueOf(profile.getQosControllerObjectMaxBandwidth()))) {
                qos_specs.put("maxBandWidth", profile.getQosControllerObjectMaxBandwidth());
                isQoSNull = false;
            }
            /**
             *  修改findbugs问题：EC_UNRELATED_TYPES End
             */

            if (null != profile.getQosControllerObjectLatency() && !"".equals(profile.getQosControllerObjectLatency())) {
                qos_specs.put("latency", profile.getQosControllerObjectLatency());
                isQoSNull = false;
            }

            LOGGER.info("Qos Capability is: " + qos_specs.toString());

        } catch (JSONException e) {
            LOGGER.error("convertStoragePolicy2QosSpec() error", e);
        }
        if (isQoSNull) {
            return null;
        } else {
            return qos_specs;
        }
    }

    /*public static void delStoragePolicy(String volTypeId) throws StorageFault
    {
        try{
            SDKResult<SQos> resVolType0 = vvolModel.getQosByVolumeType(volTypeId);
            if( 0 != resVolType0.getErrCode())
            {
                if (404 == resVolType0.getErrCode()){
                    LOGGER.info("Volume type "+volTypeId+" not exists in DJ.");
                    return;
                }
                LOGGER.error("delVolumeType() error.");
                throw FaultUtil.storageFault();
            }
            
            if (null != resVolType0.getResult().getQos_specs().getSpecs()){
                String qosId = resVolType0.getResult().getQos_specs().getId();
                vvolModel.deAssociateQoS(qosId, volTypeId);
                vvolModel.delQos(qosId);
            }
                
            // 删除VolumeType
            vvolModel.delVolumeType(volTypeId);
        }catch (Exception e){
            throw FaultUtil.storageFault();
        }
    }*/

    public static void printVcenterProfile(StorageProfile storageProfile) {
        if (checkProfileNull(storageProfile)) {
            LOGGER.info("StorageProfile is empty.");
        } else {
            StringBuffer sb = new StringBuffer("StorageProfile:\n");
            sb.append("StorageProfileName:").append(storageProfile.getName())
                    .append("\nStorageProfileId:").append(storageProfile.getProfileId())
                    .append("\nConstraints:{");

            //Print subProfiles.
            List<SubProfile> subProfiles = storageProfile.getConstraints().getSubProfiles();
            for (int i = 0; i < subProfiles.size(); i++) {
                SubProfile subProfile = subProfiles.get(i);
                sb.append("\n\tSubProfile").append(i).append(":")
                        .append("\n\t\tSubProfileName:").append(subProfile.getName());
                //Print CapabilityInstances.
                List<CapabilityInstance> capaInstances = subProfile.getCapability();
                for (int j = 0; j < capaInstances.size(); j++) {
                    CapabilityInstance capaInstance = capaInstances.get(j);
                    sb.append("\n\t\tCapabilityInstance").append(j).append(":")
                            .append("\n\t\t\tCapabilityId:").append(capaInstance.getCapabilityId());
                    //Print ConstraintInstances.
                    List<ConstraintInstance> constraintInstances = capaInstance.getConstraint();
                    for (int m = 0; m < constraintInstances.size(); m++) {
                        sb.append("\n\t\t\tConstraintInstance").append(m).append(":");
                        ConstraintInstance constraintInstance = constraintInstances.get(m);
                        //Print propertyInstances.
                        List<PropertyInstance> propertyInstances = constraintInstance.getPropertyInstance();
                        for (int n = 0; n < propertyInstances.size(); n++) {
                            PropertyInstance proInstance = propertyInstances.get(n);
                            String proName = proInstance.getId();
                            Object proValue = proInstance.getValue();
                            sb.append("\n\t\t\t\tpropertyInstance").append(n).append(": Id:").append(proName)
                                    .append(" Value:");
                            if (proValue instanceof DiscreteSet) {
                                sb.append(((DiscreteSet) proValue).getValues());
                            } else if (proValue instanceof Range) {
                                sb.append("Min:").append(((Range) proValue).getMin())
                                        .append(" Max:").append(((Range) proValue).getMax());
                            } else {
                                sb.append(proValue);
                            }
                        }
                    }
                }
            }
            sb.append("\n}");
            LOGGER.info(sb.toString());
        }
    }

    public static List<CapabilityInstance> filterCapabilityInstancesFromSubProfile(SubProfile subProfile) {
        List<CapabilityInstance> capaInstances = subProfile.getCapability();
        List<CapabilityInstance> compareInstances = new ArrayList<CapabilityInstance>();
        for (CapabilityInstance capaInstance1 : capaInstances) {
            if (capaInstance1.getCapabilityId().getNamespace().equalsIgnoreCase("org.opensds.vasaprovider") || capaInstance1.getCapabilityId().getNamespace().equalsIgnoreCase("org.opensds.vasaprovider.level")
                    || capaInstance1.getCapabilityId().getNamespace().equalsIgnoreCase("org.opensds.vasaprovider.capability") || (capaInstance1.getCapabilityId().getNamespace().equalsIgnoreCase(VASAUtil.VMW_NAMESPACE)
                    && capaInstance1.getCapabilityId().getId().equalsIgnoreCase(VASAUtil.VMW_STD_CAPABILITY))) {
                compareInstances.add(capaInstance1);
            }
        }
        return compareInstances;
    }

    public static long maxValue(long lhs, long rhs) {
        return lhs >= rhs ? lhs : rhs;
    }

    public static long minValue(long lhs, long rhs) {
        return lhs <= rhs ? lhs : rhs;
    }

    public static Range rangeIntersection(Range policyRange, Range vvolTypeRange) {
        Range range = new Range();
        range.setMin(maxValue((Long) (policyRange.getMin()), (Long) (vvolTypeRange.getMin())));
        range.setMax(minValue((Long) (policyRange.getMax()), (Long) (vvolTypeRange.getMax())));

        return range;
    }

    public static StoragePolicy convertSubProfile2StoragePolicy(String profileId, List<CapabilityInstance> compareInstances, StorageProfileData profileData,
                                                                String containerId, boolean isUpperBound, long generationId, String defaultSpaceEfficiency) {

        LOGGER.debug("convertSubProfile2StoragePolicy profileId=" + profileId + ",compareInstances=" + compareInstances + ",profileData="
                + profileData + ",containerId=" + containerId + "isUpperBound" + isUpperBound + ",generationId=" + generationId + ",defaultSpaceEfficiency=" + defaultSpaceEfficiency);

        StoragePolicy storagePolicy = new StoragePolicy();
        LOGGER.debug("new StoragePolicy = " + storagePolicy);
        storagePolicy.setContainerId(containerId);
        storagePolicy.setControlType(profileData.getControlType());
        storagePolicy.setControlTypeId(profileData.getControlTypeId());
        String thinThick = "";
        if (isUpperBound) {
            storagePolicy.setQosControllerPolicy(VASAArrayUtil.ControlPolicy.isUpperBound);
        } else {
            storagePolicy.setQosControllerPolicy(VASAArrayUtil.ControlPolicy.isLowerBound);
        }

        for (CapabilityInstance capabilityInstance : compareInstances) {
            PropertyInstance proInstance = capabilityInstance.getConstraint().get(0).getPropertyInstance().get(0);
            String proName = proInstance.getId();
            Object proValue = proInstance.getValue();
            LOGGER.debug("proInstance proName=" + proName + ",proValue" + proValue);
            if (proName.equalsIgnoreCase(VASAUtil.VMW_STD_CAPABILITY) && (proValue instanceof DiscreteSet)) {
                thinThick = (String) ((DiscreteSet) proValue).getValues().get(0);
                storagePolicy.setType(thinThick);
            } else if (proName.equalsIgnoreCase("SmartTier") && (proValue instanceof String)) {
                storagePolicy.setSmartTier((String) proValue);
            } else if (proName.equalsIgnoreCase("DiskType") && (proValue instanceof String)) {
                storagePolicy.setDiskType((String) proValue);
            } else if (proName.equalsIgnoreCase("RaidLevel") && (proValue instanceof String)) {
                storagePolicy.setRaidLevel((String) proValue);
            } else if (proName.equalsIgnoreCase("FlowControlType") && (proValue instanceof String)) {
                storagePolicy.setQosControllerType((String) proValue);
            } else if (proName.equalsIgnoreCase("FlowControlPolicy") && (proValue instanceof String)) {
                storagePolicy.setQosControllerPolicy((String) proValue);
            } else if (proName.equalsIgnoreCase("IOPS") && ((proValue instanceof Long) || (proValue instanceof Range))) {
                if (proValue instanceof Long) {
                    if (isUpperBound) {
                        storagePolicy.setQosControllerObjectMaxIOPS(minValue((Long) (proValue), profileData.getIops()));
                    } else {
                        storagePolicy.setQosControllerObjectMinIOPS(maxValue((Long) (proValue), profileData.getIops()));
                    }
                }
                //这里考虑Range的情况是为了处理问题单DTS2016092207675: 在某些虚拟机上升级之后vCenter不起作用
                if (proValue instanceof Range) {
                    Range iopsRange = (Range) proValue;
                    if (isUpperBound) {
                        storagePolicy.setQosControllerObjectMaxIOPS(minValue((Long) iopsRange.getMax(), profileData.getIops()));
                    } else {
                        storagePolicy.setQosControllerObjectMinIOPS(maxValue((Long) iopsRange.getMin(), profileData.getIops()));
                    }
                }
            } else if (proName.equalsIgnoreCase("Bandwidth") && ((proValue instanceof Long) || (proValue instanceof Range))) {
                if (proValue instanceof Long) {
                    if (isUpperBound) {
                        storagePolicy.setQosControllerObjectMaxBandwidth(minValue((Long) (proValue), profileData.getBandWidth()));
                    } else {
                        storagePolicy.setQosControllerObjectMinBandwidth(maxValue((Long) (proValue), profileData.getBandWidth()));
                    }
                }
                //这里考虑Range的情况是为了处理问题单DTS2016092207675: 在某些虚拟机上升级改变上报的metadata之后vCenter不起作用
                if (proValue instanceof Range) {
                    Range bandwidthRange = (Range) proValue;
                    if (isUpperBound) {
                        storagePolicy.setQosControllerObjectMaxBandwidth(minValue((Long) bandwidthRange.getMax(), profileData.getBandWidth()));
                    } else {
                        storagePolicy.setQosControllerObjectMinBandwidth(maxValue((Long) bandwidthRange.getMin(), profileData.getBandWidth()));
                    }
                }
            } else if (proName.equalsIgnoreCase("Latency") && ((proValue instanceof Long) || (proValue instanceof Range))) {
                //保护下限才有时延的概念
                if (!isUpperBound) {
                    if (proValue instanceof Long) {
                        String maxLatency = String.valueOf(proValue);
                        storagePolicy.setQosControllerObjectLatency(maxLatency);
                    }
                    //这里考虑Range的情况是为了处理问题单DTS2016092207675: 在某些虚拟机上升级改变上报的metadata之后vCenter不起作用
                    if (proValue instanceof Range) {
                        String maxLatency = String.valueOf(((Range) proValue).getMax());
                        storagePolicy.setQosControllerObjectLatency(maxLatency);
                    }
                }
            } else if (proName.equalsIgnoreCase("UseCapabilityControl") && (proValue instanceof String)) {
                if (String.valueOf(proValue).equalsIgnoreCase("true") && StringUtils.isNotEmpty(profileData.getControlTypeId())) {
                    storagePolicy.setQosControllerPolicy(profileData.getControlPolicy());
                    storagePolicy.setQosControllerType(profileData.getFlowControlType());
                    if (profileData.getControlPolicy().equals(VASAArrayUtil.ControlPolicy.isUpperBound)) {
                        storagePolicy.setQosControllerObjectMaxBandwidth(profileData.getBandWidth());
                        storagePolicy.setQosControllerObjectMaxIOPS(profileData.getIops());
                    } else {
                        if (null == profileData.getLatency()) {
                            storagePolicy.setQosControllerObjectLatency(null);
                        } else {
                            storagePolicy.setQosControllerObjectLatency(String.valueOf(profileData.getLatency()));
                        }
                        storagePolicy.setQosControllerObjectMinBandwidth(profileData.getBandWidth());
                        storagePolicy.setQosControllerObjectMinIOPS(profileData.getIops());
                    }
                } else {
                    storagePolicy.setCloseQos(true);
                }
            }
        }
        //deal with update policy
        if (defaultSpaceEfficiency != null) {
            storagePolicy.setType(defaultSpaceEfficiency);
            thinThick = defaultSpaceEfficiency;
        }
        if (profileData.getControlType().equals(NStorageProfile.ControlType.level_control)) {
            String userLevel = profileData.getUserLevel();
            String serviceType = profileData.getServiceType();
//			storagePolicy.setUserLevel(userLevel);
//			storagePolicy.setServiceType(serviceType);
//        	storagePolicy.setProfileLevelId(profileData.getControlTypeId());
            storagePolicy.setControlType(profileData.getControlType());
            storagePolicy.setControlTypeId(profileData.getControlTypeId());
            NStorageProfileLevel userLevelConf = storageProfileLevelService.getUserLevelConf(userLevel);
            //添加设置StoragePolicy值
            storagePolicy.setQosControllerType(VASAArrayUtil.IOTYPE.READANDWRITEIO.getDes());
            String levelProperty = userLevelConf.getLevelProperty();
            String iops = levelProperty.split(",")[0];
            String bandwidth = levelProperty.split(",")[1];
            if ((StringUtils.isNotEmpty(serviceType) && serviceType.equalsIgnoreCase(VASAArrayUtil.ServiceTypePolicy.critical)) || userLevel.equalsIgnoreCase(VASAArrayUtil.UserLevelPolicy.high)) {
                storagePolicy.setQosControllerPolicy(VASAArrayUtil.ControlPolicy.isLowerBound);
                storagePolicy.setQosControllerObjectLatency(null);
                storagePolicy.setQosControllerObjectMaxBandwidth(null);
                storagePolicy.setQosControllerObjectMaxIOPS(null);
                storagePolicy.setQosControllerObjectMinBandwidth(Long.valueOf(bandwidth));
                storagePolicy.setQosControllerObjectMinIOPS(Long.valueOf(iops));
            } else {
                storagePolicy.setQosControllerPolicy(VASAArrayUtil.ControlPolicy.isUpperBound);
                storagePolicy.setQosControllerObjectLatency(null);
                storagePolicy.setQosControllerObjectMaxBandwidth(Long.valueOf(bandwidth));
                storagePolicy.setQosControllerObjectMaxIOPS(Long.valueOf(iops));
                storagePolicy.setQosControllerObjectMinBandwidth(null);
                storagePolicy.setQosControllerObjectMinIOPS(null);
            }
            LOGGER.info("match profileLevel policy,converseData. ProfileData = " + profileData + ",StoragePolicy=" + storagePolicy);
        }
        //policy名字增加System.currentTimeMillis()是为了防止DJ创建volume_type成功，但是并没有插入VASA数据库，造成下次创建全都失败
        //String storagePolicyName = "policy_" + thinThick + "_" + containerId + "_" + profileId+"_"+generationId;
        String storagePolicyName = "policy_" + thinThick + "_" + containerId + "_" + profileId + "_" + generationId + "_" + System.currentTimeMillis();
        storagePolicy.setName(storagePolicyName);
        return storagePolicy;
    }

    public static void reCalculateRealQos(StoragePolicy storagePolicy, long sizeInMB) {
        if (StringUtils.isNotEmpty(storagePolicy.getControlType()) && storagePolicy.getControlType().equalsIgnoreCase(NStorageProfile.ControlType.capability_control) && StringUtils.isNotEmpty(storagePolicy.getControlTypeId())) {
            if (storagePolicy.getQosControllerPolicy().equals(VASAArrayUtil.ControlPolicy.isUpperBound)) {
                if (null != storagePolicy.getQosControllerObjectMaxBandwidth()) {
                    Long qos = calculateQosByCapability(storagePolicy.getQosControllerObjectMaxBandwidth(), sizeInMB);
                    qos = qos < 1 ? 1 : qos;
                    storagePolicy.setQosControllerObjectMaxBandwidth(qos);
                }
                if (null != storagePolicy.getQosControllerObjectMaxIOPS()) {
                    Long qos = calculateQosByCapability(storagePolicy.getQosControllerObjectMaxIOPS(), sizeInMB);
                    qos = qos < 10 ? 10 : qos;
                    storagePolicy.setQosControllerObjectMaxIOPS(qos);
                }
            } else if (storagePolicy.getQosControllerPolicy().equals(VASAArrayUtil.ControlPolicy.isLowerBound)) {
                if (null != storagePolicy.getQosControllerObjectMinBandwidth()) {
                    storagePolicy.setQosControllerObjectMinBandwidth(calculateQosByCapability(storagePolicy.getQosControllerObjectMinBandwidth(), sizeInMB));
                }
                if (null != storagePolicy.getQosControllerObjectMinIOPS()) {
                    storagePolicy.setQosControllerObjectMinIOPS(calculateQosByCapability(storagePolicy.getQosControllerObjectMinIOPS(), sizeInMB));
                }
                //latency not calculete
    			/*if(null != storagePolicy.getQosControllerObjectLatency()) {
    				storagePolicy.setQosControllerObjectLatency(String.valueOf(calculateQosByCapability(Long.valueOf(storagePolicy.getQosControllerObjectLatency()), sizeInMB)));
    			}*/
            }
        }
    }

    public static void checkQosForArray(String arrayId, StoragePolicy storagePolicy) throws InvalidProfile, OutOfResource {

        if (StringUtils.isNotEmpty(storagePolicy.getControlType()) && StringUtils.isNotEmpty(storagePolicy.getControlTypeId())) {
            if (storagePolicy.getQosControllerPolicy().equals(VASAArrayUtil.ControlPolicy.isUpperBound)) {
                if (null != storagePolicy.getQosControllerObjectMaxIOPS()) {
                    Long qosLimitValue = DeviceTypeMapper.getQosLimitValue(arrayId, DeviceTypeMapper.QosType.iops);
                    if (null != qosLimitValue) {
                        if (qosLimitValue > storagePolicy.getQosControllerObjectMaxIOPS()) {
                            LOGGER.error("Current Device IOPS must be equal or granter than " + qosLimitValue);
                            LOGGER.warn("Current iops is:" + storagePolicy.getQosControllerObjectMaxIOPS() + " change it to qosLimitValue:" + qosLimitValue);
                            storagePolicy.setQosControllerObjectMaxIOPS(qosLimitValue);
                            //throw FaultUtil.invalidProfile("Current Device IOPS must be equal or granter than " + qosLimitValue);
                        }
                    }
                }
            } else if (storagePolicy.getQosControllerPolicy().equals(VASAArrayUtil.ControlPolicy.isLowerBound)) {
                if (null != storagePolicy.getQosControllerObjectMinIOPS()) {
                    Long qosLimitValue = DeviceTypeMapper.getQosLimitValue(arrayId, DeviceTypeMapper.QosType.iops);
                    if (null != qosLimitValue) {
                        if (qosLimitValue > storagePolicy.getQosControllerObjectMinIOPS()) {
                            LOGGER.error("Current Device IOPS must be equal or granter than " + qosLimitValue);

                            //if current is less than qosLimitValue,change it to limitValue without exception
                            LOGGER.warn("Current iops is:" + storagePolicy.getQosControllerObjectMaxIOPS() + " change it to qosLimitValue:" + qosLimitValue);
                            storagePolicy.setQosControllerObjectMaxIOPS(qosLimitValue);
                            //throw FaultUtil.invalidProfile("Current Device IOPS must be equal or granter than " + qosLimitValue);
                        }
                    }
                }
            }
        }

        Long iops = (null != storagePolicy.getQosControllerObjectMaxIOPS() ? storagePolicy.getQosControllerObjectMaxIOPS() : storagePolicy.getQosControllerObjectMinIOPS());
        Long bandwidth = (null != storagePolicy.getQosControllerObjectMaxBandwidth() ? storagePolicy.getQosControllerObjectMaxBandwidth() : storagePolicy.getQosControllerObjectMinBandwidth());
        String latency = storagePolicy.getQosControllerObjectLatency();
        if (null != iops && iops > MagicNumber.LONG999999999) {
            LOGGER.error("Current Device IOPS must be equal or less than " + MagicNumber.LONG999999999);
            throw FaultUtil.invalidProfile("Current Device IOPS must be equal or less than " + MagicNumber.LONG999999999);
        }
        if (null != bandwidth && bandwidth > MagicNumber.LONG999999999) {
            LOGGER.error("Current Device bandwidth must be equal or less than " + MagicNumber.LONG999999999);
            throw FaultUtil.invalidProfile("Current Device bandwidth must be equal or less than " + MagicNumber.LONG999999999);
        }
        if (null != latency && Long.valueOf(latency) > MagicNumber.LONG999999999) {
            LOGGER.error("Current Device latency must be equal or less than " + MagicNumber.LONG999999999);
            throw FaultUtil.invalidProfile("Current Device latency must be equal or less than " + MagicNumber.LONG999999999);
        }

    }

    private static Long calculateQosByCapability(Long capabilityQos, long sizeInMB) {
        long qosInGB = 0l;
        long capabilityQosM = capabilityQos * sizeInMB;
        if (capabilityQosM % (MagicNumber.LONG1024 * MagicNumber.LONG1024) != 0) {
            qosInGB = (capabilityQosM / (MagicNumber.LONG1024 * MagicNumber.LONG1024)) + 1;
        } else {
            qosInGB = (capabilityQosM / (MagicNumber.LONG1024 * MagicNumber.LONG1024));
        }
        return qosInGB;
    }

    public static boolean isStrArrayHas(String[] strArray, String str) {
        //str should not be null.
        if (null == strArray) {
            return false;
        }
        if (null == str) {
            return true;
        }
        for (int i = 0; i < strArray.length; i++) {
            if (strArray[i].equals(str)) {
                return true;
            }
        }
        return false;
    }

    private static void deleteQosFromDBByProfileId(String profileId) throws StorageFault, SDKException {
        //1)通过profileId获取StorageProfile并且删除
        long curTime = DateUtil.getUTCDate().getTime();
        NStorageProfile nStorageProfile = new NStorageProfile();
        nStorageProfile = storageProfileService.getStorageProfileByProfileId(profileId);
        String qosId = nStorageProfile.getSmartQosId();
        /*如何只有一个Profile对应一个VVOlProfile的话，就删除StorageProfile*/
        if (curTime - nStorageProfile.getDeletedTime().getTime() > VvolConstant.CLEAR_VVOL_INTERVAL_TIME * 30) {
            if (DiscoverServiceImpl.getInstance().getVvolProfileService().getVvolNumByProfileId(profileId) == 1) {
                storageProfileService.delete(nStorageProfile);
            }
        }

        //2)用过QosId找到StorageQos，删除StorageQos
        StorageQosServiceImpl storageQos = ApplicationContextUtil.getBean("storageQosService");
        NStorageQos nStorageQos = new NStorageQos();
        nStorageQos = storageQos.getStorageQosByQosId(qosId);
        if (curTime - nStorageProfile.getDeletedTime().getTime() > VvolConstant.CLEAR_VVOL_INTERVAL_TIME * 30) {
            storageQos.delete(nStorageQos);
        }

    }

    //该函数涉及对profile2VolType这一DB表的“查询后修改”，需要加互斥锁
    public static void clearUnusedVolType(long nowTime, boolean clearImmediate, String[] containerIds) throws StorageFault {
        //待修改@谢召龙
        long sleepTime = 200;
        LOGGER.info("clearing long unused voltype now.");
        //是否有必要优化成只查询volTypeId和lastUseTime两个字段？？？？？？？？
        List<NProfile2VolType> profile2VolTypes = profile2VolTypeService.getAllProfile2VolType();
        //物理删除数据库
        for (NProfile2VolType nprofile2VolTypes : profile2VolTypes) {
            try {
                deleteQosFromDBByProfileId(nprofile2VolTypes.getProfileId());
            } catch (SDKException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*LOGGER.info("profile2VolTypes size: "+ profile2VolTypes.size());
        for(NProfile2VolType p2v: profile2VolTypes){
            //"5200177B-98B7-4A0D-BFB5-11C02CE223E6" is the magic number Id of default profile from VASA.sql.
            //We won't clear the default profile in period clear routine, but if the clearImmediate flag is true,
            //we need to clear them too.
            if (p2v.getProfileId().equals("5200177B-98B7-4A0D-BFB5-11C02CE223E6") && !clearImmediate){
                continue;
            }
            LOGGER.info("profile2VolType name:"+ p2v.getProfileName()+" id:"+p2v.getProfileId()+" generationId:"+
                    p2v.getGenerationId()+" containerId:"+p2v.getContainerId() + " deprecated:"+p2v.getDeprecated());
            synchronized(clearVolTypeLock){
                if (clearImmediate || nowTime - p2v.getLastUseTime().getTime() > VvolConstant.CLEAR_VOLTYPE_INTERVAL_TIME){
                    int boundVolNum = vvolProfileService.getVvolNumByProfileIdAndGenerationId(p2v.getProfileId(),p2v.getGenerationId());
                    LOGGER.info("boundVolNum: "+ boundVolNum);
                    //由删除constainer触发，不取统计这个profile是否还被卷引用，直接尝试删除。
                    //只清理对应container上的volume type，其他container上的volume type不删
                    if(null != containerIds){
                        if(isStrArrayHas(containerIds, p2v.getContainerId())){
                            try{
                                LOGGER.info("Deleting volume type caused by deleting container. volume type: "+ p2v.getVoltypeName());
                                VASAUtil.delStoragePolicy(p2v.getVoltypeId());
                                profile2VolTypeService.delProfile2VolType(p2v.getProfileId(),p2v.getGenerationId(), p2v.getContainerId(), p2v.getThinThick());
                            }catch(Exception e){
                                LOGGER.error("Failed to delete volume type: "+ p2v.getVoltypeName()+
                                        ". Please check if there are still volumes in the container: "+p2v.getContainerId());
                            } 
                        }else{
                            continue;
                        }
                    }else if(0 == boundVolNum){
                        LOGGER.info("Deleting volume type: "+ p2v.getVoltypeName());
                        VASAUtil.delStoragePolicy(p2v.getVoltypeId());
                        //Delete the record in DB.
                        profile2VolTypeService.delProfile2VolType(p2v.getProfileId(),p2v.getGenerationId(), p2v.getContainerId(),p2v.getThinThick());
                    }
                }
            }
            try{
                Thread.sleep(sleepTime);
            }catch (InterruptedException e) {
                LOGGER.error("clearUnusedVolType InterruptedException.");
            }
        }
        LOGGER.info("clear long unused voltype finished.");*/
    }

    public static void insertProfile2VolTypeIntoDB(String profileId, String profileName, String volTypeId,
                                                   String volTypeName, String containerId, String thinThick, long generationId, String isDeprecated) throws StorageFault {
        NProfile2VolType newProfile2VolType = new NProfile2VolType();
        newProfile2VolType.setContainerId(containerId);
        Date now = DateUtil.getUTCDate();
        newProfile2VolType.setLastUseTime(now);
        newProfile2VolType.setProfileName(profileName);
        newProfile2VolType.SetProfileId(profileId);
        newProfile2VolType.setVoltypeId(volTypeId);
        newProfile2VolType.setVoltypeName(volTypeName);
        newProfile2VolType.setThinThick(thinThick);
        newProfile2VolType.setGenerationId(generationId);
        newProfile2VolType.setDeprecated(isDeprecated);
        profile2VolTypeService.insertProfile2VolType(newProfile2VolType);
    }

    public static boolean queryComplianceResult(List<NVvolProfile> profileCap, String containerId) throws NotFound {
        try {
            //SDKResult<List<S2DVolumeType>> sdkResult = vvolModel.getVolumeTypeByVirtualPool(containerId);
            List<StorageProfileData> storageProfileDatas = storageProfileService.getStorageProfileByContainerId(containerId);
            if (null == storageProfileDatas || storageProfileDatas.size() == 0) {
                LOGGER.error("getVolumeTypeByVirtualPool() error. container id is : " + containerId);
                throw FaultUtil.notFound();
            }

            //List<S2DVolumeType> types = sdkResult.getResult();
            //LOGGER.info("queryComplianceResult, found volume types: "+ types);
            for (StorageProfileData storageProfileData : storageProfileDatas) {
                //deal with no policy
                if (profileCap.size() == 1) {
                    return true;
                }
                //get control upper or low from DB
                boolean isUp = true;
                for (NVvolProfile cap : profileCap) {
                    String key = cap.getCapability();
                    String value = cap.getValue();
                    if ("FlowControlPolicy".equalsIgnoreCase(key) && "Control lower bound".equalsIgnoreCase(value)) {
                        isUp = false;
                    }
                }
                //get control upper or low from DJ
				/*boolean isProfileUp = true;
				if(type.getExtra_specs().getMinIOPS()!=0||type.getExtra_specs().getMinBandWidth()!=0||type.getExtra_specs().getMaxLatency()!=0)
				{
					isProfileUp = false;
				}
				if(isUp != isProfileUp)
				{
					continue;
				}*/
                for (NVvolProfile cap : profileCap) {
                    String key = cap.getCapability();
                    String value = cap.getValue();
                    if ("IOPS".equalsIgnoreCase(key) && value.contains("-")) {
                        cap.setValue(isUp ? value.split("-")[1] : value.split("-")[0]);
                    }
                    if ("Bandwidth".equalsIgnoreCase(key) && value.contains("-")) {
                        cap.setValue(isUp ? value.split("-")[1] : value.split("-")[0]);
                    }
                    if ("Latency".equalsIgnoreCase(key) && value.contains("-")) {
                        cap.setValue(isUp ? value.split("-")[1] : value.split("-")[0]);
                    }
                }
                boolean isMatch = isProfileMatch(profileCap, storageProfileData, isUp);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            LOGGER.error("queryComplianceResult() error. container id is : " + containerId, e);
            throw FaultUtil.notFound();
        }
    }

    //该函数涉及对profile2VolType这一DB表的“查询后修改”，需要加互斥锁
    public static StoragePolicy matchContainer(StorageProfile storageProfile, SubProfile subProfile, String containerId,
                                               String defaultSpaceEfficiency, StorageProfile profileToInsert) throws NotFound, InvalidProfile {
        boolean haveNoProfile = false;
        String profileId = storageProfile.getProfileId();
        String profileName = storageProfile.getName();
        long generationId = storageProfile.getGenerationId();
        if (profileId.equalsIgnoreCase("5200177B-98B7-4A0D-BFB5-11C02CE223E6") && (null != profileToInsert)) {
            //This profile is the defaultProfile in vasa db, not the profile from vcenter.
            profileId = profileToInsert.getProfileId();
            profileName = profileToInsert.getName();
            generationId = profileToInsert.getGenerationId();
            haveNoProfile = true;
        }

        LOGGER.debug("matchContainer profileId=" + profileId + ",profileName=" + profileName + ",generationId=" + generationId);
        synchronized (clearVolTypeLock) {
            try {
                List<CapabilityInstance> compareInstances = filterCapabilityInstancesFromSubProfile(subProfile);
                String thinThick = "Thin";
                boolean isUpperBound = true;
                if (compareInstances.size() == 1 && compareInstances.get(0).getConstraint().get(0).getPropertyInstance().get(0).getId().equalsIgnoreCase(VASAUtil.VMW_STD_CAPABILITY)) {
                    haveNoProfile = true;
                }
                for (CapabilityInstance capabilityInstance : compareInstances) {
                    PropertyInstance proInstance = capabilityInstance.getConstraint().get(0).getPropertyInstance().get(0);
                    String proName = proInstance.getId();
                    Object proValue = proInstance.getValue();
                    if (proName.equalsIgnoreCase(VASAUtil.VMW_STD_CAPABILITY) && (proValue instanceof DiscreteSet)) {
                        thinThick = (String) ((DiscreteSet) proValue).getValues().get(0);
                    }
                    if (proName.equalsIgnoreCase("FlowControlPolicy")) {
                        String controlValue = (String) (proValue);
                        if ("Control lower bound".equalsIgnoreCase(controlValue)) {
                            isUpperBound = false;
                        }
                    }
                    LOGGER.debug("PropertyInstance [proName=" + proName + ",proValue=" + proValue + "]");
                }
                //deal with update virtual volume
                if (defaultSpaceEfficiency != null) {
                    LOGGER.info("The old thinThick is [" + defaultSpaceEfficiency + "] and the policy thinThick is [" + thinThick + "] .");
                    thinThick = defaultSpaceEfficiency;
                }
                setPolicyNoQos(haveNoProfile);
                if (haveNoProfile) {
                    LOGGER.info("matchContainer ,the vCenter StoragePolicy have no policy.");
                    StoragePolicy SPolicy = new StoragePolicy();
                    SPolicy.setType(thinThick);
                    SPolicy.setContainerId(containerId);
                    return SPolicy;
                }
                checkProfileData(compareInstances);
                List<StorageProfileData> storageProfileDatas = storageProfileService.getStorageProfileByContainerId(containerId);
                for (StorageProfileData profileData : storageProfileDatas) {
                    boolean isMatch = matchVolTypeWithGivenSubProfile(compareInstances, profileData, isUpperBound);
                    if (isMatch) {
                        LOGGER.info("Matched profileData: " + profileData);
                        StoragePolicy SPolicy = convertSubProfile2StoragePolicy(profileId, compareInstances, profileData, containerId, isUpperBound, generationId, defaultSpaceEfficiency);
                        LOGGER.info("Creating volumeType from StoragePolicy: " + SPolicy);
                        return SPolicy;
                    }
                }
                throw FaultUtil.invalidProfile();
            } catch (StorageFault e) {
                LOGGER.error("matchContainer() error. container id is : " + containerId, e);
                throw FaultUtil.notFound();
            }
        }
    }

    public static void checkProfileData(List<CapabilityInstance> compareInstances) throws InvalidProfile {
        boolean checkPolicy = false, checkIOType = false, checkLatency = false, checkIOPs = false, checkBandwith = false;
        boolean checkUserLevel = false, checkServiceType = false;
        for (CapabilityInstance capabilityInstance : compareInstances) {
            PropertyInstance proInstance = capabilityInstance.getConstraint().get(0).getPropertyInstance().get(0);
            String proName = proInstance.getId();
            if (proName.equalsIgnoreCase("FlowControlPolicy")) {
                checkPolicy = true;
            }
            if (proName.equalsIgnoreCase("FlowControlType")) {
                checkIOType = true;
            }
            if (proName.equalsIgnoreCase("Bandwidth")) {
                checkBandwith = true;
            }
            if (proName.equalsIgnoreCase("Latency")) {
                checkLatency = true;
            }
            if (proName.equalsIgnoreCase("IOPS")) {
                checkIOPs = true;
            }
            if (proName.equalsIgnoreCase("UserLevel")) {
                checkUserLevel = true;
            }
            if (proName.equalsIgnoreCase("ServiceType")) {
                checkServiceType = true;
            }
        }

        if (checkServiceType && !checkUserLevel) {
            LOGGER.error("checkProfileData failed. UserLevel must not be null.");
            throw FaultUtil.invalidProfile();
        }
        if (checkIOType && (!checkBandwith && !checkLatency && !checkIOPs)) {
            LOGGER.error("checkProfileData failed. IOType and other properties must not be null. checkIOType=" + checkIOType + ",checkBandwith=" + checkBandwith + ",checkLatency=" + checkLatency + ",checkIOPs=" + checkIOPs);
            throw FaultUtil.invalidProfile();
        }
    }

    public static boolean isProfileMatch(List<NVvolProfile> capList, StorageProfileData profileData, boolean isUpperBound) {
        LOGGER.info("isProfileMatch, compare profileData: " + profileData);
        for (NVvolProfile cap : capList) {
            String key = cap.getCapability();
            Object val = cap.getValue();
            if ("IOPS".equalsIgnoreCase(key) || "Bandwidth".equalsIgnoreCase(key) || "Latency".equalsIgnoreCase(key))
                val = Long.parseLong(val == null ? "0" : String.valueOf(val));
            if (VASAUtil.VMW_STD_CAPABILITY.equalsIgnoreCase(key)) {
                DiscreteSet ds = new DiscreteSet();
                ds.getValues().add(val);
                val = ds;
            }
            if (key == null || val == null)
                continue;
            boolean isMatchProperty = matchProperty(key, val, profileData, isUpperBound);
            if (!isMatchProperty) {
                return false;
            }
        }
        return true;
    }

    public static boolean matchVolTypeWithGivenSubProfile(List<CapabilityInstance> compareInstances,
                                                          StorageProfileData profileData, boolean isUpperBound) {
        //一个volType必须同时与policy中的所有属性匹配才算匹配
        LOGGER.info("matchContainer-now compare profileData: " + profileData);
        for (CapabilityInstance capabilityInstance : compareInstances) {
            PropertyInstance proInstance = capabilityInstance.getConstraint().get(0).getPropertyInstance().get(0);
            String proName = proInstance.getId();
            LOGGER.info("matchContainer-PropertyInstance.Id: " + proName + " PropertyInstance.value: " + proInstance.getValue());
            if (!matchProperty(proName, proInstance.getValue(), profileData, isUpperBound)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否需要替换参数
     *
     * @param alarm StorageAlarm
     * @return boolean
     */
    public static boolean matchProperty(String proName, Object proValue, StorageProfileData profileData, boolean isUpperBound) {

        if (proName.equalsIgnoreCase(VASAUtil.VMW_STD_CAPABILITY) && (proValue instanceof DiscreteSet)) {
            return true;
        } else if (proName.equalsIgnoreCase("SmartTier")) {
            if (profileData.getIsSmartTier()) {
                return true;
            } else if ((proValue instanceof String) && "No relocation".equalsIgnoreCase((String) proValue)) {
                return true;
            } else if ((proValue instanceof DiscreteSet) && "No relocation".equalsIgnoreCase((String) ((DiscreteSet) proValue).getValues().get(0))) {
                return true;
            } else {
                return false;
            }
        } else if (proName.equalsIgnoreCase("DiskType")) {
            if (profileData.getIsStorageMedium()) {
                if ((proValue instanceof String) && ((String) proValue).equalsIgnoreCase(profileData.getDiskTypeValue())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else if (proName.equalsIgnoreCase("RaidLevel")) {
            if (profileData.getIsStorageMedium()) {
                if ("ALL".equalsIgnoreCase(profileData.getRaidLevelValue())) {
                    return true;
                } else if ((proValue instanceof String) && (((String) proValue).equalsIgnoreCase(profileData.getRaidLevelValue()))) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        if (profileData.getControlType().equals(NStorageProfile.ControlType.level_control)) {
            return qosLevelMatchProperty(proName, proValue, profileData);
        } else if (profileData.getControlType().equals(NStorageProfile.ControlType.precision_control)) {
            return qosPrecisionMatchProperty(proName, proValue, profileData, isUpperBound);
        } else if (profileData.getControlType().equals(NStorageProfile.ControlType.capability_control)) {
            return qosCapabilityMatchProperty(proName, proValue, profileData);
        } else {
            return false;
        }
    }

    private static boolean qosLevelMatchProperty(String proName, Object proValue, StorageProfileData profileData) {
        if (proName.equalsIgnoreCase("UserLevel") && (proValue instanceof String)) {
            if (null != profileData.getUserLevel() && profileData.getUserLevel().equalsIgnoreCase((String) proValue)) {
                return true;
            } else {
                return false;
            }
        } else if (proName.equalsIgnoreCase("ServiceType") && (proValue instanceof String)) {
            if (null != profileData.getServiceType() && profileData.getServiceType().equalsIgnoreCase((String) proValue)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean qosCapabilityMatchProperty(String proName, Object proValue, StorageProfileData profileData) {
        if (proName.equalsIgnoreCase("UseCapabilityControl") && (proValue instanceof String)) {
            if (StringUtils.isNotEmpty(profileData.getControlTypeId())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    private static boolean qosPrecisionMatchProperty(String proName, Object proValue, StorageProfileData profileData,
                                                     boolean isUpperBound) {
        if (proName.equalsIgnoreCase("FlowControlType") && (proValue instanceof String)) {
            if (null == profileData.getFlowControlType()) {
                return false;
            }
            if (profileData.getFlowControlType().equalsIgnoreCase((String) proValue)) {
                return true;
            } else {
                return false;
            }
            //这里考虑Range的情况是为了处理问题单DTS2016092207675: 在某些虚拟机上升级之后vCenter不起作用
        } else if (proName.equalsIgnoreCase("IOPS") && ((proValue instanceof Long) || (proValue instanceof Range))) {
            long minIops = 0L;
            long maxIops = 0L;
            Long iops = profileData.getIops();
            if (null == iops) {
                return false;
            }
            if (proValue instanceof Long) {
                if (isUpperBound) {
                    maxIops = (Long) proValue;
                    if (profileData.getControlPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isUpperBound)
                            && (maxIops <= iops)) {
                        return true;
                    }
                } else {
                    minIops = (Long) proValue;
                    //如果Policy的min小于volType的min，vCenter就不会让通过。
                    if (profileData.getControlPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isLowerBound)
                            && (minIops >= iops)) {
                        return true;
                    }
                }
            }
            if (proValue instanceof Range) {
                if (isUpperBound) {
                    maxIops = (Long) ((Range) proValue).getMax();
                    if (profileData.getControlPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isUpperBound)
                            && (maxIops <= iops)) {
                        return true;
                    }
                } else {
                    //Range目前都取最大值
                    minIops = (Long) ((Range) proValue).getMax();
                    if (profileData.getControlPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isLowerBound)
                            && (minIops >= iops)) {
                        return true;
                    }
                }
            }
            return false;
            //这里考虑Range的情况是为了处理问题单DTS2016092207675: 在某些虚拟机上升级之后vCenter不起作用
        } else if (proName.equalsIgnoreCase("Bandwidth") && ((proValue instanceof Long) || (proValue instanceof Range))) {
            long minBandwidth = 0L;
            long maxBandwidth = 0L;

            Long bandWidth = profileData.getBandWidth();
            if (null == bandWidth) {
                return false;
            }
            if (proValue instanceof Long) {
                if (isUpperBound) {
                    maxBandwidth = (Long) proValue;
                    if (profileData.getControlPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isUpperBound)
                            && (maxBandwidth <= bandWidth)) {
                        return true;
                    }
                } else {
                    minBandwidth = (Long) proValue;
                    if (profileData.getControlPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isLowerBound)
                            && (minBandwidth >= bandWidth)) {
                        return true;
                    }
                }
            }
            if (proValue instanceof Range) {
                if (isUpperBound) {
                    maxBandwidth = (Long) ((Range) proValue).getMax();
                    if (profileData.getControlPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isUpperBound)
                            && (maxBandwidth <= bandWidth)) {
                        return true;
                    }
                } else {
                    //Range目前都取最大值
                    minBandwidth = (Long) ((Range) proValue).getMax();
                    if (profileData.getControlPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isLowerBound)
                            && (minBandwidth >= bandWidth)) {
                        return true;
                    }
                }

            }

            return false;
            //这里考虑Range的情况是为了处理问题单DTS2016092207675: 在某些虚拟机上升级之后vCenter不起作用
        } else if (proName.equalsIgnoreCase("Latency") && ((proValue instanceof Long) || (proValue instanceof Range))) {
            Long latency = profileData.getLatency();
            if (null == latency) {
                return false;
            }
            if (proValue instanceof Long) {
                if (!isUpperBound) {
                    long maxLatency = (Long) proValue;
                    if (maxLatency <= latency) {
                        return true;
                    }
                }
            }
            if (proValue instanceof Range) {
                if (!isUpperBound) {
                    //Range目前都取最大值
                    long maxLatency = (Long) ((Range) proValue).getMax();
                    if (maxLatency <= latency) {
                        return true;
                    }
                }
            }
            return false;
        } else if (proName.equalsIgnoreCase("FlowControlPolicy") && (proValue instanceof String)) {
            if (null == profileData.getControlPolicy()) {
                return false;
            }
            if (profileData.getControlPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isUpperBound) && isUpperBound) {
                return true;
            } else if (profileData.getControlPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isLowerBound) && !isUpperBound) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static String convertThinValue2String(Boolean value) {
        if (value) {
            return "thin";
        } else {
            return "thick";
        }
    }

    public static String convertProfile2String(StorageProfile profile) {
        if (null == profile) {
            return "";
        }

        String res = "profile id:" + profile.getProfileId() +
                ", generation id:" + profile.getGenerationId() +
                ", name:" + profile.getName() +
                ", description:" + profile.getDescription() +
                ", subProfile:";

        if (VASAUtil.checkProfileNull(profile)) {
            return res;
        } else {
            return res + VASAUtil.convertCapabilityList2String(profile.getConstraints().getSubProfiles().get(0).getCapability());
        }
    }

    public static String convertCapabilityList2String(List<CapabilityInstance> capaInstances) {
        if (null == capaInstances || null == capaInstances.get(0)) {
            return "";
        }

        String begin = " [";
        String end = "] ";
        StringBuilder sbStr = new StringBuilder();
        for (CapabilityInstance capaInstance : capaInstances) {
            sbStr.append(begin);
            sbStr.append(capaInstance.getCapabilityId().getId());
            sbStr.append(",");
            sbStr.append(capaInstance.getCapabilityId().getNamespace());
            sbStr.append(",");
            if (capaInstance.getConstraint() != null && capaInstance.getConstraint().size() != 0
                    && capaInstance.getConstraint().get(0).getPropertyInstance() != null
                    && capaInstance.getConstraint().get(0).getPropertyInstance().size() != 0
                    && capaInstance.getConstraint().get(0).getPropertyInstance().get(0) != null) {
                sbStr.append(capaInstance.getConstraint().get(0).getPropertyInstance().get(0).getId());
                sbStr.append("=");
                sbStr.append(capaInstance.getConstraint().get(0).getPropertyInstance().get(0).getValue().toString());
            }

            sbStr.append(end);
        }

        return sbStr.toString();
    }

    /**
     * 校验规则，如果任何不能为空的项为空，就会返回true
     * <ul>
     *    <li>null == storageProfile</li>
     *    <li>storageProfile.getConstraints() == null</li>
     *    <li>storageProfile.getConstraints().getSubProfiles() == null</li>
     *    <li>storageProfile.getConstraints().getSubProfiles().size() == 0</li>
     *    <li>storageProfile.getConstraints().getSubProfiles().get(0).getCapability()</li>
     *    <li>storageProfile.getConstraints().getSubProfiles().get(0).getCapability().size() == 0</li>
     * </ul>
     *
     * @param storageProfile
     * @return
     */
    public static Boolean checkProfileNull(StorageProfile storageProfile) {
        if (null == storageProfile || storageProfile.getConstraints() == null
                || storageProfile.getConstraints().getSubProfiles() == null || storageProfile.getConstraints().getSubProfiles().size() == 0
                || storageProfile.getConstraints().getSubProfiles().get(0) == null
                || storageProfile.getConstraints().getSubProfiles().get(0).getCapability() == null
                || storageProfile.getConstraints().getSubProfiles().get(0).getCapability().size() == 0) {
            return true;
        }

        return false;
    }


    private static long getQosValue(Object proValue) {
        long qosValue = -1L;
        if (proValue instanceof Long) {
            qosValue = (Long) proValue;
        }
        if (proValue instanceof Range) {
            qosValue = (Long) ((Range) proValue).getMax();
        }
        return qosValue;
    }

    /**
     * 控制上限时,iops和band=0无意义
     * 保护下限时,iops和band大于999999999无意义,latency小于99999999无意义
     *
     * @param storageProfile
     * @return
     * @throws InvalidProfile
     */
    public static boolean checkProfileVaild(StorageProfile storageProfile) throws InvalidProfile {
        boolean isUp = true;
        boolean hasPolicy = false;
        boolean hasIops = false;
        long iopsValue = -1L;
        boolean hasBand = false;
        long bandValue = -1L;
        boolean haslatency = false;
        long latencyValue = -1L;
        if (null != storageProfile && null != storageProfile.getConstraints() && null != storageProfile.getConstraints().getSubProfiles()) {
            List<SubProfile> subProfiles = storageProfile.getConstraints().getSubProfiles();
            for (SubProfile subProfile : subProfiles) {
                List<CapabilityInstance> capaInstances = subProfile.getCapability();
                for (CapabilityInstance capaInstance : capaInstances) {
                    LOGGER.info("checkProfileVaild CapabilityInstanceId = " + capaInstance.getCapabilityId());
                    List<ConstraintInstance> constraintInstances = capaInstance.getConstraint();
                    for (ConstraintInstance constraintInstance : constraintInstances) {
                        List<PropertyInstance> propertyInstances = constraintInstance.getPropertyInstance();
                        for (PropertyInstance propertyInstance : propertyInstances) {
                            String proName = propertyInstance.getId();
                            Object proValue = propertyInstance.getValue();
                            if (proName.equalsIgnoreCase("FlowControlPolicy") && (proValue instanceof String)) {
                                String controlValue = (String) (proValue);
                                if ("Control lower bound".equalsIgnoreCase(controlValue)) {
                                    isUp = false;
                                }
                            }
                            if (proName.equalsIgnoreCase("IOPS") && ((proValue instanceof Long) || (proValue instanceof Range))) {
                                iopsValue = getQosValue(proValue);
                                hasIops = true;
                            }

                            if (proName.equalsIgnoreCase("Bandwidth") && ((proValue instanceof Long) || (proValue instanceof Range))) {
                                bandValue = getQosValue(proValue);
                                hasBand = true;
                            }

                            if (proName.equalsIgnoreCase("Latency") && ((proValue instanceof Long) || (proValue instanceof Range))) {
                                latencyValue = getQosValue(proValue);
                                haslatency = true;
                            }

                            hasPolicy = true;
                        }
                    }
                }
            }
            //检验合法性
            //策略不为空
            if (hasPolicy) {
                if (isUp) {
                    //上限时qosValue <= 0 或者设置时延无意义
                    if ((hasIops && (iopsValue <= 0L || iopsValue > VvolConstant.MAX_VALUE)) ||
                            (hasBand && (bandValue <= 0L || bandValue > VvolConstant.MAX_VALUE)) ||
                            (haslatency && (-1 != latencyValue))) {
                        LOGGER.error("Qos value makes no sense. isUp is: true. iopsValue is: " + iopsValue + " bandValue is: " + bandValue + " latencyValue is: " + latencyValue);
                        throw FaultUtil.invalidProfile("Policy qos value makes no sense");
                    }
                } else {   //设置下限时时延设置为大于阵列所支持的最大值时，无意义，这只为0时也无意义
                    if ((haslatency && (latencyValue > VvolConstant.MAX_VALUE || latencyValue <= 0L)) ||
                            (hasIops && iopsValue <= 0L) ||
                            (hasBand && bandValue <= 0L)) {
                        LOGGER.error("Qos value makes no sense. isUp is: false. iopsValue is: " + iopsValue + " bandValue is: " + bandValue + " latencyValue is: " + latencyValue);
                        throw FaultUtil.invalidProfile("Policy qos value makes no sense");
                    }
                }
            }
        }

        return true;
    }

    public static Boolean checkConstraintsNull(StorageProfile storageProfile) {
        if (null == storageProfile || storageProfile.getConstraints() == null) {
            return true;
        }

        return false;
    }

    /**
     * capabilityId的id或capabilityId的namespace为null，则返回true
     *
     * @param capabilityInstance 不能为Null
     * @return
     */
    public static boolean checkCapabilityNull(CapabilityInstance capabilityInstance) {
        CapabilityId capabilityId = capabilityInstance.getCapabilityId();
        if (StringUtils.isEmpty(capabilityId.getId()) || StringUtils.isEmpty(capabilityId.getNamespace())) {
            return true;
        }
        return false;
    }

    public static String getPreferredThinValue(String vvolType) {
        if (vvolType.equalsIgnoreCase(VirtualVolumeTypeEnum.SWAP.value())
                || vvolType.equalsIgnoreCase(VirtualVolumeTypeEnum.MEMORY.value())) {
            return "Thick";
        } else {
            return "Thin";
        }
    }

    public static String buildDisplayName(String prefix) {
        return prefix + "_" + buildObjectID();
    }

    public synchronized static String buildObjectID() {
        if (indx > MagicNumber.LONG9999999999) {
            indx = 0;
        }
        Date d = new Date();
        Long longtime = d.getTime();
        String objID = Long.toHexString(longtime) + "_" + indx++;
        return objID;
    }

    //最长8位
    public static String generateVvolId() {
        int nextRandomInt = rand.nextInt();

        return String.format("%08x", nextRandomInt);
    }

    public static String getVvolNamePrefix(String vvolType) {
        if (vvolType.equalsIgnoreCase("Config")) {
            return "cfg";
        } else if (vvolType.equalsIgnoreCase("Data")) {
            return "dat";
        } else if (vvolType.equalsIgnoreCase("Swap")) {
            return "swp";
        } else if (vvolType.equalsIgnoreCase("Memory")) {
            return "mem";
        } else {
            return "otr";
        }
    }


    public static String getLongVMName(String vvolName) {
        int dotIndex = vvolName.lastIndexOf(".");
        if (-1 != dotIndex) {
            vvolName = vvolName.substring(0, dotIndex);
        }
        int undexlineIndex = vvolName.lastIndexOf("_");
        if (-1 != undexlineIndex) {
            if (isNumeric(vvolName.substring(undexlineIndex + 1))) {
                vvolName = vvolName.substring(0, undexlineIndex);
            }
        }
        return vvolName;
    }

    public static String getSmartTierValue(StorageProfile policyProfile) {
        String smartTierValue = "No relocation";
        SubProfile subProfile = policyProfile.getConstraints().getSubProfiles().get(0);
        List<CapabilityInstance> filterCapabilityInstancesFromSubProfile = filterCapabilityInstancesFromSubProfile(subProfile);
        for (CapabilityInstance capabilityInstance : filterCapabilityInstancesFromSubProfile) {
            PropertyInstance proInstance = capabilityInstance.getConstraint().get(0).getPropertyInstance().get(0);
            String proName = proInstance.getId();
            Object proValue = proInstance.getValue();
            if (proName.equalsIgnoreCase("SmartTier")) {
                if ((proValue instanceof String) && "No relocation".equalsIgnoreCase((String) proValue)) {
                    smartTierValue = (String) proValue;
                } else if ((proValue instanceof DiscreteSet) && "No relocation".equalsIgnoreCase((String) ((DiscreteSet) proValue).getValues().get(0))) {
                    smartTierValue = (String) ((DiscreteSet) proValue).getValues().get(0);
                }
            }
        }

        return smartTierValue;
    }

    public static String getRegularVMName(List<NameValuePair> metadata) {
        String vmName = "";
        if (metadata == null) {
            return vmName;
        }

        boolean isConfig = false;
        String vvolName = "";
        String vvolNamespace = "";
        String vmId = "";
        boolean isData = false;
        for (NameValuePair pair : metadata) {
            if (pair.getParameterName().equalsIgnoreCase("VMW_VVolType")) {
                if (pair.getParameterValue().equalsIgnoreCase("Config")) {
                    isConfig = true;
                } else if (pair.getParameterValue().equalsIgnoreCase("Data")) {
                    isData = true;
                }
            }

            if (pair.getParameterName().equalsIgnoreCase("VMW_VVolName")) {
                vvolName = pair.getParameterValue();
            }
            if (pair.getParameterName().equalsIgnoreCase("VMW_VVolNamespace")) {
                vvolNamespace = pair.getParameterValue();
            }
            if (pair.getParameterName().equalsIgnoreCase("VMW_VmID")) {
                vmId = pair.getParameterValue();
            }
        }

        //config volume 
        if (isConfig) {
            vmName = vvolName;
            return convertSpecialCharset(vmName);
        } else {
            int dotIndex = vvolNamespace.lastIndexOf(VvolConstant.VVOL_PREFIX);
            if (-1 != dotIndex) {
                String configVolumeId = vvolNamespace.substring(dotIndex).split("/")[0];
                try {
                    if (StringUtils.isEmpty(vmId)) {
                        vmName = vvolMetadataService.getvmNameByVvolId(configVolumeId);
                    } else {
                        vmName = virtualMachineService.getVirtualMachineInfoByVmId(vmId).get(0).getVmName();
                        if ("".equals(vmName) || vmName == null) {
                            vmName = vvolMetadataService.getvmNameByVvolId(configVolumeId);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("getRegularVMName StorageFault/getVolumeById error. vvolId:" + configVolumeId + ", message:" + e.getMessage());
                }
            }
        }
        return convertSpecialCharset(vmName);
//        //data volume
//        else if(isData)
//        {
//            int dotIndex = vvolNamespace.lastIndexOf(VvolConstant.VVOL_PREFIX);
//            if(-1 != dotIndex)
//            {
//                String configVolumeId = vvolNamespace.substring(dotIndex).split("/")[0];
//                try
//                {
//                	vmName = vvolMetadataService.getvmNameByVvolId(configVolumeId);
//                	if("".equals(vmName) || vmName == null){
//                		vmName = virtualMachineService.getVirtualMachineInfoByVmId(vmId).get(0).getVmName();
//                	}
//                }
//                catch (StorageFault e)
//                {
//                    LOGGER.error("getRegularVMName StorageFault/getVolumeById error. vvolId:" + configVolumeId  + ", message:" + e.getMessage());
//                }
//            }
//        }
//        return convertSpecialCharset(vmName);
    }

    public static void printPeWWN(List<ProtocolEndpoint> pEs) {
        String begin = "PE WWN : [\n";
        String end = "\n]";
        StringBuilder sbStr = new StringBuilder();
        sbStr.append(begin);
        if (null != pEs) {
            for (ProtocolEndpoint pe : pEs) {
                sbStr.append("Lun  Id: " + ((pe.getInBandId() == null) ? "null" : pe.getInBandId().getLunId()) + ",");
                sbStr.append("Address: " + ((pe.getInBandId() == null) ? "null" : pe.getInBandId().getIpAddress()) + "\n");
            }
            if (sbStr.toString().endsWith(",") && sbStr.length() > 1) {
                sbStr.setLength(sbStr.length() - 1);
            }
        }
        sbStr.append(end);

        LOGGER.info(sbStr.toString());
    }


    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    public static String getVMName(List<NameValuePair> metadata) throws UnsupportedEncodingException {
        String vmName = null;
        if (metadata == null) {
            return "unknown";
        }

        for (NameValuePair pair : metadata) {
            if (pair.getParameterName().equalsIgnoreCase("VMW_VVolName")) {
                vmName = pair.getParameterValue();
                break;
            }
        }

        if (StringUtils.isEmpty(vmName)) {
            return "unknown";
        }
    
    	/*int endIndex = vmName.length() - 1;
    	while(vmName.getBytes("utf-8").length > 18)
    	{
    		vmName = vmName.substring(0, endIndex);
    		endIndex = vmName.length() - 1;
    	}
    	return vmName;*/

        /**
         *  修改findbugs问题：DM_DEFAULT_ENCODING:Reliance on default encoding Start
         *  指定字符编码集
         */
        /**
         *  修改CodeDEX问题：FORTIFY.Null_Dereference
         *  Modified by wWX315527 2016/11/18
         */
        byte[] result = null;
        if (vmName != null) {
            result = vmName.getBytes("utf-8");
            return new String(result, "utf-8");
        } else {
            return "unknown";
        }
        /**
         *  修改CodeDEX问题：FORTIFY.Null_Dereference
         *  Modified by wWX315527 2016/11/18
         */
        /**
         *  修改findbugs问题：DM_DEFAULT_ENCODING:Reliance on default encoding End
         */
    }

    public static String buildVvolDisplayName(String vvolType, List<NameValuePair> metadata) {

        String prefix = getVvolNamePrefix(vvolType);
        String vmName;
        try {
            vmName = getVMName(metadata);
        } catch (UnsupportedEncodingException e) {
            vmName = "unknown";
        }
        if (vmName.matches(reg1) || vmName.matches(reg2)) {
            //虚拟机超长时只保留12位(31 - 2 - 8 - 3) / 4 = 4 * 3 = 12
            try {
                while (vmName.getBytes("utf-8").length > 12) {
                    vmName = vmName.substring(0, vmName.length() - 1);
                }
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("buildVvolDisplayName error. ", e);
            }
            vmName = convertSpecialCharset(vmName);
        } else {
            try {
                //虚拟机超长时只保留18位。此处不能用vmName.length()，因为中文的一个字符用vmName.length()得到的结果是1，但阵列上一个中文字符按3个英文字符大小处理
                while (vmName.getBytes("utf-8").length > 18) {
                    vmName = vmName.substring(0, vmName.length() - 1);
                }
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("buildVvolDisplayName error. ", e);
            }
        }

        String vvolDisplayName = prefix + "-" + vmName + "-" + generateVvolId();
        return vvolDisplayName;
    }


    public static String convertSpecialCharset(String vvolDisplayName) {
        try {
            if (vvolDisplayName.matches(reg1) || vvolDisplayName.matches(reg2)) {
                //base编码
                vvolDisplayName = base64Encode(vvolDisplayName);
                //替换base64中的特殊编码
                vvolDisplayName = repaceCharset(vvolDisplayName);
            }
            return vvolDisplayName;
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Current environment don't support UTF-8. Esception is: ", e);
        }
        return "unsupport";
    }

    /**
     * 将字符串中的=替换为.  +替换为_  /替换为-
     *
     * @param origin
     * @return
     */

    public static String repaceCharset(String origin) {
        if (null != origin && origin.length() > 0) {
            origin = origin.replace('=', '.');
            origin = origin.replace('+', '_');
            origin = origin.replace('/', '-');
        }

        return origin;
    }

    /**
     * base64解码
     * 一个base64加密后字符串的长度，设字符串长度为n ，长度为 (n/3)向上取整*4
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] base64Decode(String str) throws UnsupportedEncodingException {
        return Base64.decodeBase64(str.getBytes("utf-8"));
    }


    /**
     * base64编码
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    public static String base64Encode(String str) throws UnsupportedEncodingException {
        return new String(Base64.encodeBase64(str.getBytes("utf-8")));
    }

    /**
     * @param containerId: 长度必须是36个字符串，并且由-连接的5个字符串组成
     * @return
     */
    public static Boolean checkContainerIdValid(String containerId) {
        if (null == containerId || containerId.length() != 36) {
            return false;
        }

        if (containerId.split("-").length != 5) {
            return false;
        }

        return true;
    }

    public static Boolean checkProfileIdValid(String profileId) {
        if (null == profileId || profileId.length() != 36) {
            return false;
        }

        if (profileId.split("-").length != 5) {
            return false;
        }

        return true;
    }

    /**
     * vvolId 有以下特点:
     * 1、长度44;
     * 2、以rfc4122.开始
     * 3、有四个-连接5个字符串
     *
     * @param vvolId
     * @return
     */
    public static Boolean checkVvolIdValid(String vvolId) {
        if (null == vvolId || vvolId.length() != 44) {
            return false;
        }

        if (!vvolId.startsWith("rfc4122.")) {
            return false;
        }

        if (vvolId.split("-").length != 5) {
            return false;
        }


        return true;
    }

    public static int getPropertyValueType(Object value) {
        if (value instanceof Long) {
            return 0;
        } else if (value instanceof Boolean) {
            return 1;
        } else if (value instanceof String) {
            return 2;
        } else if (value instanceof Range) {
            return 3;
        } else if (value instanceof DiscreteSet) {
            return 4;
        } else {
            return 2;
        }
    }

    public static String convertPropertyValue(Object value) {
        if (value instanceof Range) {
            long min = (Long) (((Range) value).getMin());
            long max = (Long) (((Range) value).getMax());
            return min + "-" + max;
        } else if (value instanceof DiscreteSet) {
            StringBuilder sb = new StringBuilder();
            List<Object> objs = ((DiscreteSet) value).getValues();
            if (objs != null) {
                for (Object obj : objs) {
                    sb.append(String.valueOf(obj));
                    sb.append(",");
                }

                if (sb.toString().endsWith(",") && sb.length() > 1) {
                    sb.setLength(sb.length() - 1);
                }
            }

            return sb.toString();
        } else {
            return String.valueOf(value);
        }
    }

    public static void checkArrayIdValidAndExist(String uniqueIdentifier) throws InvalidArgument, NotFound {
        if (!VASAUtil.isIdValid(uniqueIdentifier, EntityTypeEnum.STORAGE_ARRAY.value() + ":")) {
            LOGGER.error("InvalidArgument/Invalid array id:" + uniqueIdentifier);
            throw FaultUtil.invalidArgument("Invalid array id:" + uniqueIdentifier);
        }

        String arrayId = uniqueIdentifier.split(":")[1];
        if (!DataUtil.getInstance().getArrayId().contains(arrayId)) {
            LOGGER.error("NotFound/Array not found, array id:" + arrayId);
            throw FaultUtil.notFound("Array not found, array id:" + arrayId);
        }
    }

    public static void checkSearchConstraintKeys(List<String> keys) throws InvalidArgument {
        if (null == keys || keys.size() == 0 || StringUtils.isEmpty(keys.get(0))) {
            throw FaultUtil.invalidArgument("QueryConstraint is null or empty");
        }

        for (String key : keys) {
            if (null == key || key.equalsIgnoreCase("VMW_VvolAllocationType") || key.equalsIgnoreCase("VMW_UnreferencedObj")
                    || key.equalsIgnoreCase("VMW_VvolDescriptor") || key.equalsIgnoreCase("VMW_VvolProfile")) {
                throw FaultUtil.invalidArgument("QueryConstraint invalid key:" + key);
            }
        }
    }

    public static List<String> convertSearchConstraint2KeyArray(List<QueryConstraint> constraints) {
        if (null == constraints || constraints.size() == 0) {
            return null;
        }

        List<String> keys = new ArrayList<String>(0);
        for (QueryConstraint constraint : constraints) {
            keys.add(constraint.getKey());
        }

        return keys;
    }

    public static ProtocolEndpoint getPEByWwn(String pEWwn) {
        Map<String, List<ProtocolEndpoint>> session2pEs = DataUtil.getInstance().getSession2PEs();
        if (null == session2pEs) {
            return null;
        }

        List<ProtocolEndpoint> allPes = new ArrayList<ProtocolEndpoint>();
        for (Map.Entry<String, List<ProtocolEndpoint>> entry : session2pEs.entrySet()) {
            allPes.addAll(entry.getValue());
        }

        for (ProtocolEndpoint pe : allPes) {
            if (pEWwn.equalsIgnoreCase(pe.getInBandId().getLunId())) {
                return pe;
            }
        }

        return null;
    }

    public static String byteArray2String(byte[] bytes) {
        String begin = "[";
        String end = "]";
        StringBuilder sbStr = new StringBuilder();
        if (null != bytes) {
            sbStr.append(begin);
            for (byte by : bytes) {
                sbStr.append(by + ",");
            }
            if (sbStr.toString().endsWith(",") && sbStr.length() > 1) {
                sbStr.setLength(sbStr.length() - 1);
            }
            sbStr.append(end);
        }

        return sbStr.toString();

    }

    public static boolean checkTaskIdValid(String taskId) {
        if (taskId == null) {
            return false;
        }

        if (!taskId.startsWith("resizeVirtualVolume:") && !taskId.startsWith("createVirtualVolume:") &&
                !taskId.startsWith("prepareToSnapshotVirtualVolume:") && !taskId.startsWith("revertVirtualVolume:") &&
                !taskId.startsWith("cloneVirtualVolume:") && !taskId.startsWith("fastCloneVirtualVolume:") &&
                !taskId.startsWith("copyDiffsToVirtualVolume:")) {
            return false;
        }

        return true;
    }

    public static void throwCopyDiffException(long errCode) throws IncompatibleVolume, ResourceInUse,
            InvalidArgument, VasaProviderBusy, NotFound, StorageFault {
        if (ArrayErrCodeEnum.RETURN_CPY_SNAP_NOT_CORRESPOND_TO_SRC.getValue() == errCode) {
            LOGGER.error("IncompatibleVolume/the src lun and base lun are not in a snapshot relationship, errCode:" + errCode);
            throw FaultUtil.incompatibleVolume("the src lun and base lun are not in a snapshot relationship, errCode:" + errCode);
        } else if (ArrayErrCodeEnum.RETURN_CPY_CREATED_TOOMUCH.getValue() == errCode) {
            LOGGER.error("ResourceInUse/The number of created LUN copies has reached the upper limit, errCode:" + errCode);
            throw FaultUtil.resourceInUse("The number of created LUN copies has reached the upper limit, errCode:" + errCode);
        } else if (ArrayErrCodeEnum.RETURN_PARAM_ERROR.getValue() == errCode) {
            LOGGER.error("InvalidArgument/invalid argument, errCode:" + errCode);
            throw FaultUtil.invalidArgument("invalid argument, errCode:" + errCode);
        } else if (ArrayErrCodeEnum.RETURN_SYSTEM_BUSY.getValue() == errCode
                || ArrayErrCodeEnum.RETURN_CPY_PAIR_BUSY.getValue() == errCode) {
            LOGGER.error("VasaProviderBusy/system busy, errCode:" + errCode);
            throw FaultUtil.vasaProviderBusy("system busy, errCode:" + errCode);
        } else if (ArrayErrCodeEnum.RETURN_CPY_PAIRID_NOT_EXIST.getValue() == errCode) {
            LOGGER.error("NotFound/copyDiff id not found, errCode:" + errCode);
            throw FaultUtil.notFound("copyDiff id not found, errCode:" + errCode);
        } else {
            LOGGER.error("StorageFault/errCode:" + errCode);
            throw FaultUtil.storageFault("storageFault, errCode:" + errCode);
        }
    }

    public static String convertSecondaryId(String oldSecondaryId) {
        int len = oldSecondaryId.length();
        return "0x" + oldSecondaryId.substring(0, len - 4);
    }

    /**
     * 从存储池中找containerId
     *
     * @param containerId
     * @throws NotFound
     * @throws StorageFault
     */
    public static void checkContainerIdExist(String containerId) throws NotFound, StorageFault {
//    	try 
//    	{

        NStorageContainer t = new NStorageContainer();
        LOGGER.info("checkContainerIdExist containerId=" + containerId);
        t.setContainerId(containerId);
        t.setDeleted(false);
        NStorageContainer dataByKey = storageContainerService.getDataByKey(t);
        if (null == dataByKey) {
            LOGGER.error("NotFound/containerId : " + containerId + " not exist!");
            throw FaultUtil.notFound();
        }
			
			/*SDKResult<List<S2DVirtualPool>> vpResult = new VVolModel().getAllVirtualPool();
			if(0 != vpResult.getErrCode())
			{
				LOGGER.error("StorageFault/getAllVirtualPool error.");
				throw FaultUtil.storageFault("getAllVirtualPool error.");
			}
			
			Boolean isFind = false;
			for(S2DVirtualPool vPool : vpResult.getResult())
			{
				if(vPool.getId().equalsIgnoreCase(containerId))
				{
					isFind = true;
					break;
				}
			}
			
			if(!isFind)
			{
				LOGGER.error("NotFound/containerId : " + containerId + " not exist!");
				throw FaultUtil.notFound();
			}
		} 
    	catch (SDKException e) 
    	{
			LOGGER.error("StorageFault/getAllVirtualPool error.");
			throw FaultUtil.storageFault("getAllVirtualPool error.");
		}*/
    }

    /*当所有resourceId都不存在的时候返回false**/
    public static Boolean checkResourceIdExist(String[] resourceIds) throws StorageFault {
//    	try 
//    	{
			/*SDKResult<List<S2DVirtualPool>> vpResult = new VVolModel().getAllVirtualPool();
			if(0 != vpResult.getErrCode())
			{
			}*/
        List<NStorageContainer> all = storageContainerService.getAll();
        if (null == all || all.size() == 0) {
            LOGGER.error("getAllVirtualPool error.");
            throw FaultUtil.storageFault("getAllVirtualPool error.");
        }
        for (String resourceId : resourceIds) {
            for (NStorageContainer container : all) {
                if (container.getContainerId().equalsIgnoreCase(resourceId)) {
                    return true;
                }
            }
        }

        return false;
    }
//    	catch (SDKException e) 
//    	{
//			LOGGER.error("getAllVirtualPool error.");
//			throw FaultUtil.storageFault("getAllVirtualPool error.");
//		}
//    }
    
    
    /*public static int getVolumeSizeById(String vvolId)
    {
        int size = 0;
        try{
            SDKResult<S2DVolume> result = vvolModel.getVolumeById(vvolId.substring(vvolId.indexOf('.') + 1));
            if(0 == result.getErrCode() && null != result.getResult())
            {
                
                return result.getResult().getSize();
            }
        }catch(SDKException e)
        {
            LOGGER.error("Get VolumeSizeById failed. vvolid is: " + vvolId + " Exception: ", e);
        }
        return size;
    }*/

    public static void checkEntityType(List<String> entityTypes) throws InvalidArgument {
        if (null == entityTypes) {
            return;
        }

        for (String type : entityTypes) {
            if (!VirtualVolumeTypeEnum.CONFIG.value().equalsIgnoreCase(type)
                    && !VirtualVolumeTypeEnum.DATA.value().equalsIgnoreCase(type)
                    && !VirtualVolumeTypeEnum.SWAP.value().equalsIgnoreCase(type)
                    && !VirtualVolumeTypeEnum.MEMORY.value().equalsIgnoreCase(type)) {
                LOGGER.error("invalid entity type:" + type);
                throw FaultUtil.invalidArgument();
            }
        }
    }

    public static String getThinValueFromStorageProfile(StorageProfile storageProfile) {
        List<CapabilityInstance> capaInstances = storageProfile.getConstraints().getSubProfiles().get(0).getCapability();

        for (CapabilityInstance capaInstance : capaInstances) {
            if (capaInstance.getCapabilityId().getNamespace().equalsIgnoreCase(VASAUtil.VMW_NAMESPACE)
                    && capaInstance.getConstraint().get(0).getPropertyInstance().get(0).getId().equalsIgnoreCase(VASAUtil.VMW_STD_CAPABILITY)) {
                DiscreteSet value = (DiscreteSet) capaInstance.getConstraint().get(0).getPropertyInstance().get(0).getValue();
                String thinValue = (String) value.getValues().get(0);
                return thinValue;
            }
        }

        return null;
    }

    public static Boolean checkVendorSpecificCapaId(String checkedCapaId, List<String> metadataCapaIds) {
        if (metadataCapaIds == null || metadataCapaIds.size() == 0) {
            return false;
        }

        for (String vendorId : metadataCapaIds) {
            if (checkedCapaId.equalsIgnoreCase(vendorId)) {
                return true;
            }
        }

        return false;
    }

    public static List<String> convertSet2List(Set<String> strSet) {
        List<String> strList = new ArrayList<String>();
        if (strSet == null) {
            return strList;
        }

        for (String str : strSet) {
            strList.add(str);
        }

        return strList;
    }

    public static List<String> convertDArrayList2StrList(List<DArray> arrayList) {
        List<String> strList = new ArrayList<String>();
        if (arrayList == null) {
            return strList;
        }

        for (DArray dArray : arrayList) {
            strList.add(getArrayID(dArray.getUniqueIdentifier()));
        }

        return strList;
    }

    public static List<String> getConfigEventUcUUIDs() {
        List<String> returnValues = new ArrayList<String>();
        List<String> ucUUIDs = DataUtil.getInstance().getUsageContextUUIDs();
        if (ucUUIDs == null || ucUUIDs.size() == 0) {
            return returnValues;
        }

//    	Map<String, List<String>> map = DataUtil.getInstance().getSubscribeEventForVcenter();
        List<UsageContext> ucs = DataUtil.getInstance().getUsageContexts();
        for (UsageContext uc : ucs) {
            List<String> subscribedEvents = uc.getSubscribeEvent();
            if (!ListUtils.isEmptyList(subscribedEvents) && !subscribedEvents.contains(EventTypeEnum.CONFIG.value())) {
                continue;
            } else {
                returnValues.add(VASAUtil.getUcUUID(uc));
            }
        }

        return returnValues;
    }

    public static List<String> removeDuplicate(List<String> list) {
        HashSet<String> h = new HashSet<String>(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    public static String UUId2FileSystemName(String uuId) throws StorageFault {
        if (uuId == null) {
            LOGGER.error("error uuId[" + uuId + "].");
            throw FaultUtil.storageFault("error uuId !!");
        }
        LOGGER.info("before replace:" + uuId);
        String temp = uuId.replace('-', '_');
        temp = temp.replace('.', '_');
        LOGGER.info("after replace:" + temp);
        return temp;
    }

    public static String FileSystemName2CmId(String filesystemName) {
        return filesystemName.replace('_', '-');
    }


    public static String replaceSessionId(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return sessionId;
        }

        int length = sessionId.length() / 2;

        StringBuffer rep = new StringBuffer();
        for (int i = 0; i < length; i++) {
            rep.append("*");
        }
        rep.append(sessionId.substring(length));
        return rep.toString();
    }

    public static String convertQueryConstraints2Str(List<QueryConstraint> constraints) {
        if (constraints == null || constraints.size() == 0) {
            return "[]";
        }

        StringBuilder sbStr = new StringBuilder();

        for (QueryConstraint constraint : constraints) {
            sbStr.append("[").append(constraint.getKey()).append("] = [").append(constraint.getValue()).append("]\n");
        }
        if (sbStr.toString().endsWith("\n") && sbStr.length() > 1) {
            sbStr.setLength(sbStr.length() - 1);
        }

        return sbStr.toString();
    }

    public static String convertNameValuePair2Str(List<NameValuePair> pairs) {
        if (pairs == null || pairs.size() == 0) {
            return "[]";
        }

        StringBuilder sbStr = new StringBuilder();

        for (NameValuePair pair : pairs) {
            sbStr.append("[").append(pair.getParameterName()).append("] = [").append(pair.getParameterValue()).append("]\n");
        }
        if (sbStr.toString().endsWith("\n") && sbStr.length() > 1) {
            sbStr.setLength(sbStr.length() - 1);
        }

        return sbStr.toString();
    }

    public static void parseMetaDateForVmID(List<NameValuePair> pairs) {
        boolean isFind = false;
        for (NameValuePair pair : pairs) {
            if (pair.getParameterName().equals(VasaConstant.UNIQURE_VMID) &&
                    pair.getParameterValue() != null && pair.getParameterValue().length() > 4) {
                ThreadLocalHolder.put(VasaConstant.UNIQURE_VMID, pair.getParameterValue());
                isFind = true;
                break;
            }
        }
        if (!isFind) {
            for (NameValuePair pair : pairs) {
                if (pair.getParameterName().equals(VasaConstant.VMW_VVolNamespace)) {
                    ThreadLocalHolder.put(VasaConstant.UNIQURE_VMID, getVmIdByNameSpace(pair.getParameterValue()));
                    return;
                }
            }
        }

    }

    public static String getVvolRearByVvolType(String vvolType) {
        if (VasaConstant.VVOL_TYPE_DATA.equals(vvolType)) {
            return VasaConstant.VVOL_DATA_REAR;
        } else if (VasaConstant.VVOL_TYPE_MEMORY.equals(vvolType)) {
            return VasaConstant.VVOL_MEM_REAR;
        } else if (VasaConstant.VVOL_TYPE_SWAP.equals(vvolType)) {
            return VasaConstant.VVOL_SWAP_REAR;
        } else if (VasaConstant.VVOL_TYPE_CONFIG.equals(vvolType)) {
            return "";
        } else {
            throw new IllegalArgumentException("error vvoltype.");
        }
    }


    static private String getVmIdByNameSpace(String nameSpace) {
        LOGGER.info("============ find vmId vvol nameSpace : " + nameSpace);
        String vmId = null;
        if (nameSpace == null || nameSpace.length() < 44) {
            LOGGER.info("can not find vmId vvol nameSpace : " + nameSpace);
        }
        String configVvolID = nameSpace.substring(nameSpace.length() - 44, nameSpace.length());
        LOGGER.info("config id: " + configVvolID);
        try {
            NVirtualVolume configVvol = virtualVolumeService.getVirtualVolumeByVvolId(configVvolID);
            vmId = configVvol.getVmId();
        } catch (StorageFault storageFault) {
            LOGGER.error("get vvolid from database error" + storageFault.toString());
        }
        return vmId;
    }

    public static void saveNameSpace(List<NameValuePair> pairs) {
        for (NameValuePair pair : pairs) {
            if (pair.getParameterName().equals(VasaConstant.VMW_VVolNamespace)) {
                ThreadLocalHolder.put(VasaConstant.VMW_VVolNamespace, pair.getParameterValue());
                return;
            }
        }
        ThreadLocalHolder.put(VasaConstant.VMW_VVolNamespace, null);
    }

    public static void setSessionId(String sessionId) {
        ThreadLocalHolder.put(VasaConstant.SESSION_ID_FOR_HOSTIPS, sessionId);
    }

    public static String getSessionId() {
        Object ret = ThreadLocalHolder.get(VasaConstant.SESSION_ID_FOR_HOSTIPS);
        if (ret == null) {
            LOGGER.warn("can not find the sessionId !!");
            return null;
        }
        return ret.toString();
    }

    public static String getVmId() {
        Object ret = ThreadLocalHolder.get(VasaConstant.UNIQURE_VMID);
        if (ret == null) {
            LOGGER.warn("can not find the vmId !!");
            return null;
        }
        return ret.toString();
    }

    public static String getVmwNamespace() {
        Object ret = ThreadLocalHolder.get(VasaConstant.VMW_VVolNamespace);
        if (ret == null) {
            LOGGER.warn("can not find the VMW_VVolNamespace !!");
            return null;
        }
        return ret.toString();
    }

    public static String convertVirtualVolumeInfo2Str(VirtualVolumeInfo vvolInfo) {
        if (vvolInfo == null) {
            return "";
        }

        StringBuilder sbStr = new StringBuilder();

        sbStr.append("vvolId = ").append(vvolInfo.getVvolId()).append("\n");

        if (vvolInfo.getMetadata() == null) {
            sbStr.append("[null]");
            return sbStr.toString();
        }

        for (NameValuePair pair : vvolInfo.getMetadata()) {
            sbStr.append("[").append(pair.getParameterName()).append("] = [").append(pair.getParameterValue()).append("]\n");
        }
        if (sbStr.toString().endsWith("\n") && sbStr.length() > 1) {
            sbStr.setLength(sbStr.length() - 1);
        }

        return sbStr.toString();
    }

    public static String[] commandFormat(String[] cmds) {
        if (null == cmds || 0 == cmds.length) {
            return cmds;
        }

        for (String command : cmds) {
            command = PathUtils.JsonPathFormat(command);
        }

        return cmds;
    }

    public static void updateArrayIdByContainerId(String containerId) throws StorageFault {
        NStoragePool t = new NStoragePool();
        t.setContainerId(containerId);
        t.setDeleted(false);
        List<NStoragePool> search = storagePoolService.search(t);
        if (null != search && search.size() != 0) {
            saveArrayId(search.get(0).getArrayId());
        } else {
            LOGGER.error("no pool is in the container. containerId = " + containerId);
            throw FaultUtil.storageFault("no pool is in the container. containerId = " + containerId);
        }
    }

    public static void updateArrayIdByVvolId(String vvolId) throws StorageFault {

        NVirtualVolume virtualVolumeByVvolId = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
        String arrayId = virtualVolumeByVvolId.getArrayId();
        if (null != arrayId && !arrayId.equals("")) {
            saveArrayId(arrayId);
        } else {
            LOGGER.error("virtualVolume have no arrayId, vvolId = " + vvolId);
            throw FaultUtil.storageFault("virtualVolume have no arrayId, vvolId = " + vvolId);
        }
    }

    public static boolean isSamePe(ProtocolEndpoint a, ProtocolEndpoint b) {
        if (a != null && b != null && a.getInBandId() != null && b.getInBandId() != null) {
            if (a.getInBandId().getProtocolEndpointType() != null) {
                if (a.getInBandId().getProtocolEndpointType().equals(b.getInBandId().getProtocolEndpointType())) {
                    if (a.getInBandId().getLunId() != null && b.getInBandId().getLunId() != null) {
                        return a.getInBandId().getLunId().equalsIgnoreCase(b.getInBandId().getLunId());
                    }

                    if (a.getInBandId().getIpAddress() != null && b.getInBandId().getIpAddress() != null) {
                        return a.getInBandId().getIpAddress().equalsIgnoreCase(b.getInBandId().getIpAddress());
                    }
                }
            }
        }
        return false;
    }

    public static void saveArrayId(String arrayId) {
        LOGGER.info("set arrayId = " + arrayId);
        ThreadLocalHolder.put(VasaConstant.REQUEST_ARRAY_ID, arrayId);
    }


    public static void saveCurrEsxIp(String ip) {
        LOGGER.info("set currEsxIP = " + ip);
        ThreadLocalHolder.put(VasaConstant.ESX_IP, ip);
    }

    public static String getCurrEsxIp() throws StorageFault {
        Object ip = ThreadLocalHolder.get(VasaConstant.ESX_IP);
        if (ip == null) {
            LOGGER.error("can not find the esx ip.");
            throw FaultUtil.storageFault("can not find the esx ip.");
        }
        LOGGER.info("get currEsxIP:" + ip.toString());
        return ip.toString();
    }

    public static String getArrayId() throws StorageFault {
        Object object = ThreadLocalHolder.get(VasaConstant.REQUEST_ARRAY_ID);
        if (null == object) {
            LOGGER.error("can not find the arrayId");
            throw FaultUtil.storageFault("can not find the arrayId");
        }
        LOGGER.debug("get arrayId = " + object);
        return object.toString();
    }

    public static void saveContainerType(String containerId) {
        LOGGER.info("set containerId = " + containerId);
        try {
            NStorageContainer container = storageContainerService.getStorageContainerByContainerId(containerId);
            LOGGER.info("current container type is " + container.getContainerType());
            ThreadLocalHolder.put(VasaConstant.CURRENT_CONTAINER_TYPE, container.getContainerType());
        } catch (StorageFault e) {
            LOGGER.error("getStorageContainerByContainerId error,StorageFault", e);
        }
    }

    public static boolean isNasContainer() throws StorageFault {
        Object containerType = ThreadLocalHolder.get(VasaConstant.CURRENT_CONTAINER_TYPE);
        if (null == containerType) {
            LOGGER.error("get container type fail!!");
            throw FaultUtil.storageFault("get container type fail");
        }
        LOGGER.info("the containertype is " + "[" + containerType + "]");
        return containerType.toString().equals(VasaConstant.CONTAINER_TYPE_NAS);
    }

    public static void setPolicyNoQos(boolean b) {
        ThreadLocalHolder.put(VasaConstant.REQUEST_POLICY_NO_QOS, b);
    }

    public static boolean getPolicyNoQos() {
        Object object = ThreadLocalHolder.get(VasaConstant.REQUEST_POLICY_NO_QOS);
        if (null == object) {
            return false;
        }
        return (boolean) object;
    }

    public static SDKResult<List<S2DStoragePool>> queryAndUpdateStoragePoolInfoByArrayId(String arrayId) throws SDKException {
        SDKResult<List<S2DStoragePool>> storagePoolsFormDevice = vvolModel.getAllStoragePool(arrayId);
        List<S2DStoragePool> result = storagePoolsFormDevice.getResult();
        if (null != result) {
            List<NStoragePool> queryStoragePoolByArrayId = storagePoolService.queryStoragePoolByArrayId(arrayId);
            //updatePoolInfo(arrayId, result, queryStoragePoolByArrayId);
        } else {
            if (0 != storagePoolsFormDevice.getErrCode()) {
                LOGGER.error("getAllStoragePool err,arrayId=" + arrayId + ",errMsg=" + storagePoolsFormDevice.getDescription());
            }
        }
        return storagePoolsFormDevice;
    }
    //无效方法，如果result.size和storagePoolSize一致就不会更新数据了
    /*
    private static void updatePoolInfo(String arrayId, List<S2DStoragePool> result,
			List<NStoragePool> queryStoragePoolByArrayId) {
		LOGGER.info("result size = "+result.size()+",queryStoragePoolByArrayId.size()="+queryStoragePoolByArrayId.size());
		if(result.size() == queryStoragePoolByArrayId.size()){
			for(S2DStoragePool s2dStoragePool:result){
				LOGGER.debug("updatePoolInfo s2dStoragePool="+s2dStoragePool);
				storagePoolService.updatePool(s2dStoragePool, arrayId);
			}
		}else{
			for(S2DStoragePool s2dStoragePool:result){
				boolean checkUpdate = false;
				for (NStoragePool nStoragePool : queryStoragePoolByArrayId) {
					if(nStoragePool.getRawPoolId().equals(s2dStoragePool.getID())){
						LOGGER.debug("updatePoolInfo s2dStoragePool="+s2dStoragePool);
						storagePoolService.updatePool(s2dStoragePool, arrayId);
						checkUpdate=true;
						break;
					}
				}
				if(!checkUpdate){
					LOGGER.debug("saveStoragePoolData s2dStoragePool="+s2dStoragePool);
					storagePoolService.saveStorageData(s2dStoragePool, arrayId);
				}
			}
		}
	}
	*/

    public static boolean checkProfileIdInStorageProfile(String profileID) {
        NStorageProfile t = new NStorageProfile();
        t.setProfileId(profileID);
        NStorageProfile dataByKey = storageProfileService.getDataByKey(t);
        if (null == dataByKey) {
            return false;
        } else {
            return true;
        }
    }

    public static void checkProfileNoQos(StorageProfile storagePolicy) {
        if (null == storagePolicy || null == storagePolicy.getConstraints()
                || storagePolicy.getConstraints().getSubProfiles().size() == 0
                || null == storagePolicy.getConstraints().getSubProfiles().get(0)) {
            setPolicyNoQos(true);
            return;
        }

        LOGGER.info("the storagePolicy is not null, the subProfile is " + storagePolicy.getConstraints().getSubProfiles());
        SubProfile subProfile = storagePolicy.getConstraints().getSubProfiles().get(0);
        List<CapabilityInstance> compareInstances = filterCapabilityInstancesFromSubProfile(subProfile);
        if (compareInstances.size() == 1 && compareInstances.get(0).getConstraint().get(0).getPropertyInstance().get(0).getId().equalsIgnoreCase(VASAUtil.VMW_STD_CAPABILITY)) {
            setPolicyNoQos(true);
        }
    }

}
