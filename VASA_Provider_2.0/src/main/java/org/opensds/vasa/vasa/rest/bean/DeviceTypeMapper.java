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

package org.opensds.vasa.vasa.rest.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.common.DeviceType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.service.StorageManagerService;

public class DeviceTypeMapper extends PropertyPlaceholderConfigurer {

    public static Map<String, HashMap<String, String>> DeviceTypeMap = new HashMap<>();
    public static Map<String, HashMap<String, String>> DevicePerfMap = new HashMap<>();
    public static Map<String, HashMap<String, String>> DeviceQosLimitMap = new HashMap<>();
    public static Map<String, String> arrayToDeviceType = new HashMap<>();
    private static String OCEANSTOR_MODEL_PREX = "OceanStor ";
    public static String DORADO_MODEL_PREX = "Dorado ";
    private static Logger LOGGER = LogManager.getLogger(DeviceTypeMapper.class);

    public class QosType {
        public static final String iops = "iops";
    }


    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
            throws BeansException {
        // TODO Auto-generated method stub
        super.processProperties(beanFactoryToProcess, props);
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            LOGGER.info("keyStr: " + keyStr + " value: " + value);
            if (keyStr.startsWith("performance_")) {
                loadDeviceInfoPropertiesMap(keyStr, value, DevicePerfMap);
            } else if (keyStr.startsWith("qos_")) {
                loadDeviceInfoPropertiesMap(keyStr, value, DeviceQosLimitMap);
            } else {
                loadDeviceInfoPropertiesMap(keyStr, value, DeviceTypeMap);
                LOGGER.info("DeviceTypeMap map: " + DeviceTypeMap);
            }
        }
    }

    private static synchronized void loadStorageDevice() {
        StorageManagerService storageManagerService = ApplicationContextUtil.getBean("storageManagerService");
        List<StorageInfo> queryInfo = storageManagerService.queryInfo();
        for (StorageInfo storageInfo : queryInfo) {
            if (1 == storageInfo.getDeviceType()) {
                arrayToDeviceType.put(storageInfo.getId(), DeviceType.FusionStorage.toString());
            } else {
                String model = storageInfo.getModel();
                String deviceTypeValue = getTypeName(model);
                String arrayDeviceType = getArrayDeviceType(deviceTypeValue);
                arrayToDeviceType.put(storageInfo.getId(), arrayDeviceType);
            }
        }
    }

    public static String getDeviceType(String arrayId) {
        if (arrayToDeviceType.containsKey(arrayId)) {
            return arrayToDeviceType.get(arrayId);
        } else {
            loadStorageDevice();
            if (arrayToDeviceType.containsKey(arrayId)) {
                return arrayToDeviceType.get(arrayId);
            }
        }
        return null;
    }

    public static Long getQosLimitValue(String arrayId, String qosType) {
        String key = "qos_" + qosType + "_limit";
        if (arrayToDeviceType.containsKey(arrayId)) {
            return getLimitValue(arrayId, key);
        } else {
            loadStorageDevice();
            if (arrayToDeviceType.containsKey(arrayId)) {
                return getLimitValue(arrayId, key);
            }
        }
        return null;
    }

    private static Long getLimitValue(String arrayId, String key) {
        String deviceType = arrayToDeviceType.get(arrayId);
        if (DeviceQosLimitMap.containsKey(key)) {
            HashMap<String, String> hashMap = DeviceQosLimitMap.get(key);
            if (hashMap.containsKey(deviceType)) {
                return Long.valueOf(hashMap.get(deviceType));
            }
        }
        return null;
    }

    private void loadDeviceInfoPropertiesMap(String key, String value, Map<String, HashMap<String, String>> map) {
        HashMap<String, String> performanceMap = new HashMap<>();
        loadProperties(value, performanceMap);
        map.put(key, performanceMap);
    }

    private void loadProperties(String value, Map<String, String> map) {
        String[] productmodeAndname = value.split(",");
        for (String productmode : productmodeAndname) {
            String[] modeAndname = productmode.split(":");
            if (modeAndname.length == 2) {
                map.put(modeAndname[0], modeAndname[1]);
            }
        }
    }

    public static boolean isSupportQosLower(String model) {
        if (StringUtils.isNotEmpty(model)) {
            String deviceTypeValue = getTypeName(model);
            String arrayDeviceType = getArrayDeviceType(deviceTypeValue);
            LOGGER.info("isSupportQosLower deviceTypeValue=" + deviceTypeValue + ",arrayDeviceType=" + arrayDeviceType);
            if (null != arrayDeviceType) {
                HashMap<String, String> qosLowerSuppMap = DevicePerfMap.get("performance_qos_lower");
                String isSupport = qosLowerSuppMap.get(arrayDeviceType);
                if (null == isSupport || isSupport.equals("true")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSupportSmartTier(String model) {
        if (StringUtils.isNotEmpty(model)) {
            String deviceTypeValue = getTypeName(model);
            String arrayDeviceType = getArrayDeviceType(deviceTypeValue);
            if (null != arrayDeviceType) {
                HashMap<String, String> qosLowerSuppMap = DevicePerfMap.get("performance_smarttier");
                String isSupport = qosLowerSuppMap.get(arrayDeviceType);
                if (null == isSupport || isSupport.equals("true")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String thinThickSupport(String model) {
        if (StringUtils.isNotEmpty(model)) {
            String deviceTypeValue = getTypeName(model);
            String arrayDeviceType = getArrayDeviceType(deviceTypeValue);
            if (null != arrayDeviceType) {
                HashMap<String, String> qosLowerSuppMap = DevicePerfMap.get("performance_thinthick");
                String thinThick = qosLowerSuppMap.get(arrayDeviceType);
                if (StringUtils.isNotEmpty(thinThick)) {
                    return thinThick;
                }
            }
        }
        return null;
    }

    private static String getTypeName(String model) {
        String model_prex = "";
        if (null != model) {
            if (model.startsWith(DORADO_MODEL_PREX)) {
                model_prex = DORADO_MODEL_PREX;
            } else {
                model_prex = OCEANSTOR_MODEL_PREX;
            }
        }
        return model.substring(model_prex.length());
    }

    private static String getArrayDeviceType(String deviceTypeValue) {
        for (String array_type : DeviceTypeMap.keySet()) {
            HashMap<String, String> deviceTypes = DeviceTypeMap.get(array_type);
            if (deviceTypes.containsValue(deviceTypeValue)) {
                return array_type;
            }
        }
        return null;
    }

    public static String convertDeviceType(String deviceType) {
        for (String array_type : DeviceTypeMap.keySet()) {
            HashMap<String, String> deviceTypes = DeviceTypeMap.get(array_type);
            if (deviceTypes.containsKey(deviceType)) {
                return deviceTypes.get(deviceType);
            }
        }
        return null;
    }

    public static String getProfuctModeName(String deviceType) {
        for (String array_type : DeviceTypeMap.keySet()) {
            HashMap<String, String> deviceTypes = DeviceTypeMap.get(array_type);
            if (deviceTypes.containsKey(deviceType)) {
                String typeName = deviceTypes.get(deviceType);
                if (array_type.equalsIgnoreCase("dorado_productmode")) {
                    return DORADO_MODEL_PREX + typeName;
                } else {
                    return OCEANSTOR_MODEL_PREX + typeName;
                }
            }
        }
        return null;
    }

}

