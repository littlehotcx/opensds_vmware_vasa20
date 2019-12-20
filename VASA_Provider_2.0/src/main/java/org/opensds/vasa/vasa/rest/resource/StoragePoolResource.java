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

package org.opensds.vasa.vasa.rest.resource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.common.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.domain.model.VVolModel;
import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.vasa.util.VASAResponseCode;

import org.opensds.vasa.base.common.VASAArrayUtil;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.VasaNasArrayService;
import org.opensds.vasa.vasa.db.model.NStorageContainer;
import org.opensds.vasa.vasa.db.model.NStoragePool;
import org.opensds.vasa.vasa.db.model.NStorageProfile;
import org.opensds.vasa.vasa.db.model.NStorageQos;
import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.service.StorageContainerService;
import org.opensds.vasa.vasa.db.service.StorageManagerService;
import org.opensds.vasa.vasa.db.service.StoragePoolService;
import org.opensds.vasa.vasa.db.service.StorageProfileService;
import org.opensds.vasa.vasa.db.service.StorageQosService;
import org.opensds.vasa.vasa.db.service.VirtualVolumeService;
import org.opensds.vasa.vasa.rest.bean.ResponseHeader;
import org.opensds.vasa.vasa.rest.bean.SetStoragePoolLostRequest;
import org.opensds.vasa.vasa.rest.bean.BindStoragePoolRequest;
import org.opensds.vasa.vasa.rest.bean.DeviceTypeMapper;
import org.opensds.vasa.vasa.rest.bean.QueryStoragePoolResponse;
import org.opensds.vasa.vasa.rest.bean.StoragePoolRestBean;
import org.opensds.vasa.vasa.rest.bean.UnbindStoragePoolRequest;

import com.vmware.vim.vasa.v20.StorageFault;

@Path("vasa/storagePool")
public class StoragePoolResource {
    private Logger LOGGER = LogManager.getLogger(StoragePoolResource.class);
    private StoragePoolService storagePoolService = ApplicationContextUtil.getBean("storagePoolService");
    private StorageContainerService storageContainerService = ApplicationContextUtil.getBean("storageContainerService");
    private StorageManagerService storageManagerService = ApplicationContextUtil.getBean("storageManagerService");
    private StorageProfileService storageProfileService = ApplicationContextUtil.getBean("storageProfileService");
    private VirtualVolumeService virtualVolumeService = ApplicationContextUtil.getBean("virtualVolumeService");
    private StorageQosService storageQosService = ApplicationContextUtil.getBean("storageQosService");
    private VVolModel vvolModel = new VVolModel();
    private VasaNasArrayService vasaNasArrayService = (VasaNasArrayService) ApplicationContextUtil
            .getBean("vasaNasArrayService");

    @GET
    @Path("/queryArrayStoragePool/all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStoragePoolResponse queryAllStoragePoolByArrayId(@QueryParam("arrayId") String arrayId) {
        LOGGER.info("In VASA queryAllStoragePoolByArrayId function,the arrayId=" + arrayId);
        QueryStoragePoolResponse response = new QueryStoragePoolResponse();

        // 校验阵列是否存在
        StorageInfo queryInfo = storageManagerService.queryInfoByArrayId(arrayId);
        if (queryInfo == null || StringUtils.isEmpty(queryInfo.getId()) || 1 == queryInfo.getDeleted()) {
            LOGGER.error("The storage array is not exist.");
            response.setResultCode(VASAResponseCode.storagePoolService.STORAGE_ARRAY_IS_NOT_EXIST);
            response.setResultDescription(VASAResponseCode.storagePoolService.STORAGE_ARRAY_IS_NOT_EXIST_DESCRIPTION);
            return response;
        }

        List<StoragePoolRestBean> storagePoolRestBeans = new ArrayList<StoragePoolRestBean>();
        try {
            // unUse method deviceManager上的修改不能及时同步
            // VASAUtil.queryAndUpdateStoragePoolInfoByArrayId(arrayId);
            SDKResult<List<S2DStoragePool>> storagePoolsFormDevice = vvolModel.getAllStoragePool(arrayId);
            List<S2DStoragePool> s2dStoragePools = storagePoolsFormDevice.getResult();
            storagePoolRestBeans = getStoragePoolInfoFormArray(s2dStoragePools, arrayId, null);
            response.setCount(storagePoolRestBeans.size());
            response.setStoragePools(storagePoolRestBeans);
            response.setResultCode("0");
            response.setResultDescription("queryCountOfUnboundStoragePoolByArrayId successfully.");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOGGER.info("queryCountOfUnboundStoragePoolByArrayId fail , " + e, e);
            response.setResultCode("1");
            response.setResultDescription("queryCountOfUnboundStoragePoolByArrayId fail.");
        }
        LOGGER.info("End queryCountOfUnboundStoragePoolByArrayId function");
        return response;
    }

    private boolean checkArraySupportSmartTier(String containerId, StorageInfo queryInfo, boolean isDorado) {
        List<NStorageProfile> nStorageProfiles = storageProfileService
                .queryOmCreateStorageProfileByContainerId(containerId);
        if (nStorageProfiles.size() == 0) {
            LOGGER.info("The storage container do not have storage profile.");
            return true;
        }
        boolean supportSmartTier = DeviceTypeMapper.isSupportSmartTier(queryInfo.getModel());
        boolean profileSupportSmartTier = false;
        for (NStorageProfile nStorageProfile : nStorageProfiles) {
            if (nStorageProfile.getIsSmartTier()) {
                profileSupportSmartTier = true;
                break;
            }
        }
        if (profileSupportSmartTier && !supportSmartTier) {
            return false;
        }
        return true;
    }

    private boolean checkProfileisDorado(String containerId, StorageInfo queryInfo) {
        List<NStorageProfile> nStorageProfiles = storageProfileService
                .queryOmCreateStorageProfileByContainerId(containerId);
        if (nStorageProfiles.size() == 0) {
            LOGGER.info("The storage container do not have storage profile. the storage profile is not dorado.");
            return false;
        }

        boolean arrayIsDorado = false;
        LOGGER.info("The array model is " + queryInfo.getModel());
        if (queryInfo.getModel().indexOf("Dorado") >= 0) {
            LOGGER.info("checkProfileisDorado has dorado storage profile.");
            return true;
        }
        return false;
    }

    private boolean checkArraySupportLowerQos(String containerId, StorageInfo queryInfo) throws StorageFault {
        List<NStorageProfile> nStorageProfiles = storageProfileService
                .queryOmCreateStorageProfileByContainerId(containerId);
        boolean supportQosLower = DeviceTypeMapper.isSupportQosLower(queryInfo.getModel());
        for (NStorageProfile nStorageProfile : nStorageProfiles) {
            if (nStorageProfile.getControlType().equals(NStorageProfile.ControlType.precision_control)
                    && !StringUtils.isEmpty(nStorageProfile.getControlTypeId())) {
                NStorageQos storageQosByQosId = storageQosService
                        .getStorageQosByQosId(nStorageProfile.getControlTypeId());
                if (storageQosByQosId.getControlPolicy().equals(VASAArrayUtil.ControlPolicy.isLowerBound)
                        && !supportQosLower) {
                    return false;
                }
            }
        }
        return true;
    }

    @GET
    @Path("/queryContainerStoragePool/all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStoragePoolResponse queryAllStoragePoolByContainerId(@QueryParam("containerId") String containerId) {
        LOGGER.info("In VASA queryAllStoragePoolByContainerId function,the containerId=" + containerId);
        QueryStoragePoolResponse response = new QueryStoragePoolResponse();
        List<StoragePoolRestBean> storagePoolRestBeans = new ArrayList<StoragePoolRestBean>();
        response.setStoragePools(storagePoolRestBeans);
        if (null == containerId || "".equalsIgnoreCase(containerId)) {
            response.setResultCode(VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_INVALID);
            response.setResultDescription(VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_INVALID_DESCRIPTION);
            return response;
        }
        try {
            List<NStoragePool> storagePools = storagePoolService.queryStoragePoolByContainerId(containerId);
            response.setCount(storagePools.size());
            if (storagePools.size() == 0) {
                LOGGER.info("the storagePool size is : " + storagePools.size());
                response.setResultCode(VASAResponseCode.common.SUCCESS);
                response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
                return response;
            }

            Set<String> arrayList = new HashSet<String>();
            for (NStoragePool nStoragePool : storagePools) {
                arrayList.add(nStoragePool.getArrayId());
            }

            if (arrayList.size() == 0 || arrayList.size() > 1) {
                response.setResultCode(
                        VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_CANNOT_BIND_DIFFARRAY_STORAGEPOOL);
                response.setResultDescription(
                        VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_CANNOT_BIND_DIFFARRAY_STORAGEPOOL_DESCRIPTION);
                return response;
            }
            String arrayId = storagePools.get(0).getArrayId();
            storagePoolRestBeans = getStoragePoolInfoFormDB(storagePools, arrayId, containerId);
            response.setStoragePools(storagePoolRestBeans);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("Query storage container fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        LOGGER.info("End queryAllStoragePoolByContainerId function : " + response.toString());
        return response;
    }

    @GET
    @Path("/queryStoragePoolNotSupportSmartTierByContainerId")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStoragePoolResponse queryStoragePoolNotSupportSmartTierByContainerId(
            @QueryParam("containerId") String containerId) {
        LOGGER.info("In VASA queryStoragePoolNotSupportSmartTierByContainerId function,the containerId=" + containerId);
        QueryStoragePoolResponse response = new QueryStoragePoolResponse();
        List<StoragePoolRestBean> storagePoolRestBeans = new ArrayList<StoragePoolRestBean>();
        response.setStoragePools(storagePoolRestBeans);
        if (null == containerId || "".equalsIgnoreCase(containerId)) {
            response.setResultCode(VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_INVALID);
            response.setResultDescription(VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_INVALID_DESCRIPTION);
            return response;
        }
        try {
            List<NStoragePool> storagePools = storagePoolService.queryStoragePoolByContainerId(containerId);
            response.setCount(0);
            if (storagePools.size() == 0) {
                response.setResultCode(VASAResponseCode.common.SUCCESS);
                response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
                return response;
            }
            Set<String> arrayList = new HashSet<String>();
            for (NStoragePool nStoragePool : storagePools) {
                arrayList.add(nStoragePool.getArrayId());
            }

            if (arrayList.size() == 0 || arrayList.size() > 1) {
                response.setResultCode(
                        VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_CANNOT_BIND_DIFFARRAY_STORAGEPOOL);
                response.setResultDescription(
                        VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_CANNOT_BIND_DIFFARRAY_STORAGEPOOL_DESCRIPTION);
                return response;
            }
            String arrayId = storagePools.get(0).getArrayId();
            storagePoolRestBeans = getStoragePoolInfoFormDB(storagePools, arrayId, containerId);
            List<StoragePoolRestBean> resultStoragePoolRestBeans = new ArrayList<StoragePoolRestBean>();
            for (StoragePoolRestBean poolRestBean : storagePoolRestBeans) {
                if (!poolRestBean.getTiering()) {
                    resultStoragePoolRestBeans.add(poolRestBean);
                }
            }
            response.setStoragePools(resultStoragePoolRestBeans);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("queryStoragePoolNotSupportSmartTierByContainerId fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    @GET
    @Path("/queryStoragePoolNotSupportStorageMedium")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStoragePoolResponse queryStoragePoolNotSupportStorageMedium(
            @QueryParam("containerId") String containerId) {
        LOGGER.info("In VASA queryStoragePoolNotSupportStorageMedium function,the containerId=" + containerId);
        QueryStoragePoolResponse response = new QueryStoragePoolResponse();
        List<StoragePoolRestBean> storagePoolRestBeans = new ArrayList<StoragePoolRestBean>();
        response.setStoragePools(storagePoolRestBeans);
        if (null == containerId || "".equalsIgnoreCase(containerId)) {
            response.setResultCode(VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_INVALID);
            response.setResultDescription(VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_INVALID_DESCRIPTION);
            return response;
        }
        try {
            List<NStoragePool> storagePools = storagePoolService.queryStoragePoolByContainerId(containerId);
            response.setCount(0);
            if (storagePools.size() == 0) {
                response.setResultCode(VASAResponseCode.common.SUCCESS);
                response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
                return response;
            }
            Set<String> arrayList = new HashSet<String>();
            for (NStoragePool nStoragePool : storagePools) {
                arrayList.add(nStoragePool.getArrayId());
            }

            if (arrayList.size() == 0 || arrayList.size() > 1) {
                response.setResultCode(
                        VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_CANNOT_BIND_DIFFARRAY_STORAGEPOOL);
                response.setResultDescription(
                        VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_CANNOT_BIND_DIFFARRAY_STORAGEPOOL_DESCRIPTION);
                return response;
            }
            String arrayId = storagePools.get(0).getArrayId();
            storagePoolRestBeans = getStoragePoolInfoFormDB(storagePools, arrayId, containerId);
            List<StoragePoolRestBean> resultStoragePoolRestBeans = new ArrayList<StoragePoolRestBean>();
            for (StoragePoolRestBean poolRestBean : storagePoolRestBeans) {
                if (poolRestBean.getTiering()) {
                    resultStoragePoolRestBeans.add(poolRestBean);
                }
            }
            response.setStoragePools(resultStoragePoolRestBeans);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("queryStoragePoolNotSupportStorageMedium fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    @GET
    @Path("/queryStoragePoolMisMatchStorageMediumByContainerId")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStoragePoolResponse queryStoragePoolMisMatchStorageMediumByContainerId(
            @QueryParam("containerId") String containerId, @QueryParam("profileIds") String profileIds, @QueryParam("diskTypeValue") String diskTypeValue, @QueryParam("raidLevelValue") String raidLevelValue) {
        LOGGER.info("In VASA queryStoragePoolMisMatchStorageMediumByContainerId function,the containerId=" + containerId + ",the profileIds=" + profileIds + ", the diskTypeValue=" + diskTypeValue + ", the raidLevelValue=" + raidLevelValue);
        QueryStoragePoolResponse response = new QueryStoragePoolResponse();
        List<StoragePoolRestBean> storagePoolRestBeans = new ArrayList<StoragePoolRestBean>();
        response.setStoragePools(storagePoolRestBeans);
        if (null == containerId || "".equalsIgnoreCase(containerId)) {
            response.setResultCode(VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_INVALID);
            response.setResultDescription(VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_INVALID_DESCRIPTION);
            return response;
        }
        try {
            List<NStoragePool> storagePools = storagePoolService.queryStoragePoolByContainerId(containerId);
            response.setCount(0);
            if (storagePools.size() == 0) {
                response.setResultCode(VASAResponseCode.common.SUCCESS);
                response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
                return response;
            }
            Set<String> arrayList = new HashSet<String>();
            for (NStoragePool nStoragePool : storagePools) {
                arrayList.add(nStoragePool.getArrayId());
            }

            if (arrayList.size() == 0 || arrayList.size() > 1) {
                response.setResultCode(
                        VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_CANNOT_BIND_DIFFARRAY_STORAGEPOOL);
                response.setResultDescription(
                        VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_CANNOT_BIND_DIFFARRAY_STORAGEPOOL_DESCRIPTION);
                return response;
            }
            String arrayId = storagePools.get(0).getArrayId();
            storagePoolRestBeans = getStoragePoolInfoFormDB(storagePools, arrayId, containerId);
            List<StoragePoolRestBean> misMatchStorageMediumStoragePoolRestBeans = new ArrayList<StoragePoolRestBean>();
            List<NStorageProfile> nStorageProfiles = storageProfileService
                    .queryOmCreateStorageProfileByContainerId(containerId);

            Map<String, Set<String>> storageMedium = new HashMap<String, Set<String>>();
            String[] diskTypeValues = {"SSD", "SAS", "NL_SAS"};
            for (String key : diskTypeValues) {
                storageMedium.put(key, new HashSet<String>());
            }
            if (!"".equalsIgnoreCase(diskTypeValue)) {
                storageMedium.get(diskTypeValue).add(raidLevelValue);
            }
            String[] profileId = profileIds.split(",");
            Set<String> profileIdset = new HashSet<String>();
            for (int i = 0; i < profileId.length; i++) {
                profileIdset.add(profileId[i]);
            }
            for (NStorageProfile nStorageProfile : nStorageProfiles) {
                if (profileIdset.contains(nStorageProfile.getProfileId()) || nStorageProfile.getDeleted()) {
                    continue;
                }
                if (nStorageProfile.getIsStorageMedium()) {
                    if ("SSD".equalsIgnoreCase(nStorageProfile.getDiskTypeValue())) {
                        storageMedium.get("SSD").add(nStorageProfile.getRaidLevelValue());
                    } else if ("SAS".equalsIgnoreCase(nStorageProfile.getDiskTypeValue())) {
                        storageMedium.get("SAS").add(nStorageProfile.getRaidLevelValue());
                    } else {
                        storageMedium.get("NL_SAS").add(nStorageProfile.getRaidLevelValue());
                    }
                }
            }
            for (StoragePoolRestBean storagePoolRestBean : storagePoolRestBeans) {
                String disk_type = storagePoolRestBean.getDisk_type();
                String raid_level = storagePoolRestBean.getRaid_level();
                if (!storageMedium.keySet().contains(disk_type)) {
                    misMatchStorageMediumStoragePoolRestBeans.add(storagePoolRestBean);
                } else {
                    if ("SSD".equalsIgnoreCase(disk_type) || "SSD_SED".equalsIgnoreCase(disk_type)) {
                        if (!storageMedium.get("SSD").contains("ALL")) {
                            if (!storageMedium.get("SSD").contains(raid_level)) {
                                misMatchStorageMediumStoragePoolRestBeans.add(storagePoolRestBean);
                            }
                        }
                    }
                    if ("SAS".equalsIgnoreCase(disk_type) || "SAS_SED".equalsIgnoreCase(disk_type)) {
                        if (!storageMedium.get("SAS").contains("ALL")) {
                            if (!storageMedium.get("SAS").contains(raid_level)) {
                                misMatchStorageMediumStoragePoolRestBeans.add(storagePoolRestBean);
                            }
                        }
                    }
                    if ("SATA".equalsIgnoreCase(disk_type) || "NL_SAS".equalsIgnoreCase(disk_type) || "NL_SAS_SED".equalsIgnoreCase(disk_type)) {
                        if (!storageMedium.get("NL_SAS").contains("ALL")) {
                            if (!storageMedium.get("NL_SAS").contains(raid_level)) {
                                misMatchStorageMediumStoragePoolRestBeans.add(storagePoolRestBean);
                            }
                        }
                    }
                }

            }
            if (storageMedium.get("SSD").isEmpty() && storageMedium.get("SAS").isEmpty() && storageMedium.get("NL_SAS").isEmpty()) {
                misMatchStorageMediumStoragePoolRestBeans.clear();
            }
            response.setStoragePools(misMatchStorageMediumStoragePoolRestBeans);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("queryStoragePoolMisMatchStorageMediumByContainerId fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    @GET
    @Path("/queryContainerStoragePool")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStoragePoolResponse queryStoragePoolByContainerId(@QueryParam("containerId") String containerId,
                                                                  @QueryParam("pageIndex") String pageIndex, @QueryParam("pageSize") String pageSize) {
        LOGGER.info("In VASA queryStoragePoolByContainerId function, the containerId=" + containerId + " pageIndex"
                + pageIndex + " pageSize" + pageSize);
        QueryStoragePoolResponse response = new QueryStoragePoolResponse();
        List<StoragePoolRestBean> storagePoolRestBeans = new ArrayList<StoragePoolRestBean>();
        response.setStoragePools(storagePoolRestBeans);
        if (null == containerId || "".equalsIgnoreCase(containerId)) {
            response.setResultCode(VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_INVALID);
            response.setResultDescription(VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_INVALID_DESCRIPTION);
            return response;
        }
        if (null == pageIndex || null == pageSize) {
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        try {
            List<NStoragePool> storagePools = storagePoolService.queryStoragePoolByContainerId(containerId);

            LOGGER.info(
                    "The storagePool size is " + storagePools.size() + " storagePools is " + storagePools.toString());
            response.setCount(storagePools.size());
            if (storagePools.size() == 0) {
                response.setResultCode(VASAResponseCode.common.SUCCESS);
                response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
                return response;
            }

            Set<String> arrayList = new HashSet<String>();
            for (NStoragePool nStoragePool : storagePools) {
                arrayList.add(nStoragePool.getArrayId());
            }

            if (arrayList.size() == 0 || arrayList.size() > 1) {
                response.setResultCode(
                        VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_CANNOT_BIND_DIFFARRAY_STORAGEPOOL);
                response.setResultDescription(
                        VASAResponseCode.storagePoolService.STORAGE_CONTAINERID_CANNOT_BIND_DIFFARRAY_STORAGEPOOL_DESCRIPTION);
                return response;
            }
            String arrayId = storagePools.get(0).getArrayId();
            if (null == pageSize || "".equals(pageSize)) {
                pageSize = "10";
                LOGGER.info("use default storage pageSize=" + pageSize);
            }
            List<NStoragePool> storagePoolsList = storagePoolService
                    .queryStoragePoolByContainerIdAndPageSize(containerId, pageIndex, pageSize);

            LOGGER.info("The storagePoolsList size is " + storagePoolsList.size() + " storagePoolsList="
                    + storagePoolsList.toString());
            storagePoolRestBeans = getStoragePoolInfoFormDB(storagePoolsList, arrayId, containerId);

            response.setStoragePools(storagePoolRestBeans);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("Query storage container fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        LOGGER.info("End queryStoragePoolByContainerId function : " + response.toString());
        return response;
    }

    @GET
    @Path("/queryArrayUnbindStoragePoolCount")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStoragePoolResponse queryCountOfUnboundStoragePoolByArrayId(@QueryParam("arrayId") String arrayId,
                                                                            @QueryParam("pageIndex") String pageIndex, @QueryParam("pageSize") String pageSize,
                                                                            @QueryParam("condition") String condition, @QueryParam("containerId") String containerId) {
        LOGGER.info("In VASA queryCountOfUnboundStoragePoolByArrayId function,the arrayId=" + arrayId);
        QueryStoragePoolResponse response = new QueryStoragePoolResponse();

        List<StoragePoolRestBean> storagePoolRestBeansSupportTier = new ArrayList<StoragePoolRestBean>();
        List<StoragePoolRestBean> storagePoolRestBeansNoSupportTier = new ArrayList<StoragePoolRestBean>();

        List<StoragePoolRestBean> resultStoragePoolRestBeans = new ArrayList<StoragePoolRestBean>();
        int unboundSizeSupportTier = 0;
        int unboundSizeNoSupportTier = 0;
        int total = 0;
        int offSet = Integer.valueOf(pageSize) * (Integer.valueOf(pageIndex) - 1);
        try {
            // 获取array所有已绑定的存储池
            List<NStoragePool> bindStoragePoolByArrayId = storagePoolService.getAllBindStoragePoolByArrayId(arrayId);
            // 获取array所有存储池信息
            SDKResult<List<S2DStoragePool>> storagePoolsFormDevice = vvolModel.getAllStoragePool(arrayId);
            List<S2DStoragePool> s2dStoragePoolsOfAll = storagePoolsFormDevice.getResult();

            // find Block or FS storage pool
            String storageContainerType =
                    storageContainerService.getStorageContainerByContainerId(containerId).getContainerType();
            String poolUsageType = "1";
            if (storageContainerType.equalsIgnoreCase("SAN")) {
                poolUsageType = "1";
            } else if (storageContainerType.equalsIgnoreCase("NAS")) {
                poolUsageType = "2";
            }
            List<S2DStoragePool> s2dStoragePools = new ArrayList<S2DStoragePool>();
            for (S2DStoragePool s2dStoragePool : s2dStoragePoolsOfAll) {
                if (s2dStoragePool.getUsageType().equalsIgnoreCase(poolUsageType)) {
                    s2dStoragePools.add(s2dStoragePool);
                }
            }

            // 转换获取的存储池信息
            List<StoragePoolRestBean> storagePoolRestBeans = getStoragePoolInfoFormArray(s2dStoragePools, arrayId,
                    null);
            // 移除已经绑定的存储池
            LOGGER.info("All storage Pool Size is : " + storagePoolRestBeans.size());
            // 记录已绑定的存储池信息
            List<StoragePoolRestBean> bindStoragePoolRestBeans = new ArrayList<StoragePoolRestBean>();
            for (StoragePoolRestBean storagePoolRestBean : storagePoolRestBeans) {
                for (NStoragePool bindStoragePool : bindStoragePoolByArrayId) {
                    if (bindStoragePool.getRawPoolId().equals(storagePoolRestBean.getId())) {
                        LOGGER.info("Remove bind storagePool, storagePool id = " + bindStoragePool.getRawPoolId());
                        bindStoragePoolRestBeans.add(storagePoolRestBean);
                    }
                }
            }

            // 得到未绑定的存储池列表
            storagePoolRestBeans.removeAll(bindStoragePoolRestBeans);
            LOGGER.info("Unbind storage Pool Size is : " + storagePoolRestBeans.size());
            // 根据分页查询参数，构造返回值
            for (StoragePoolRestBean storagePoolRestBean : storagePoolRestBeans) {
                if (storagePoolRestBean.getTiering()) {
                    // 支持Tier特性的存储池
                    if (unboundSizeSupportTier >= offSet
                            && unboundSizeSupportTier < (Integer.valueOf(pageSize) + offSet)) {
                        storagePoolRestBeansSupportTier.add(storagePoolRestBean);
                    }
                    unboundSizeSupportTier++;
                } else {
                    // 不支持Tier特性的存储池
                    if (unboundSizeNoSupportTier >= offSet
                            && unboundSizeNoSupportTier < (Integer.valueOf(pageSize) + offSet)) {
                        storagePoolRestBeansNoSupportTier.add(storagePoolRestBean);
                    }
                    unboundSizeNoSupportTier++;
                }
                // 不管支不支持Tier特性的存储池
                if (total >= offSet && total < (Integer.valueOf(pageSize) + offSet)) {
                    resultStoragePoolRestBeans.add(storagePoolRestBean);
                }
                total++;
            }


            LOGGER.info("The unbound StoragePool size is : " + total);
            LOGGER.info("The unbound StoragePool support Tier size is : " + unboundSizeSupportTier);
            LOGGER.info("The unbound StoragePool not support Tier size is : " + unboundSizeNoSupportTier);
            if ("all".equals(condition)) {
                response.setCount(total);
                response.setStoragePools(resultStoragePoolRestBeans);
            } else if ("SupportTier".equalsIgnoreCase(condition)) {
                response.setCount(unboundSizeSupportTier);
                response.setStoragePools(storagePoolRestBeansSupportTier);
            } else if ("noSupportTier".equalsIgnoreCase(condition)) {
                response.setCount(unboundSizeNoSupportTier);
                response.setStoragePools(storagePoolRestBeansNoSupportTier);
            }
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.info("queryCountOfUnboundStoragePoolByArrayId fail , " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        LOGGER.info("End queryCountOfUnboundStoragePoolByArrayId function");
        return response;
    }

    @GET
    @Path("/queryArrayUnbindStoragePool")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStoragePoolResponse queryArrayUnbindStoragePool(@QueryParam("arrayId") String arrayId,
                                                                @QueryParam("pageIndex") String pageIndex, @QueryParam("pageSize") String pageSize) {
        LOGGER.info("In VASA queryArrayUnbindStoragePool function,the arrayId=" + arrayId + " pageIndex" + pageIndex);
        QueryStoragePoolResponse response = new QueryStoragePoolResponse();

        // 校验阵列是否存在
        StorageInfo queryInfo = storageManagerService.queryInfoByArrayId(arrayId);
        if (queryInfo == null || StringUtils.isEmpty(queryInfo.getId()) || 1 == queryInfo.getDeleted()) {
            LOGGER.error("The storage array is not exist.");
            response.setResultCode(VASAResponseCode.storagePoolService.STORAGE_ARRAY_IS_NOT_EXIST);
            response.setResultDescription(VASAResponseCode.storagePoolService.STORAGE_ARRAY_IS_NOT_EXIST_DESCRIPTION);
            return response;
        }

        int offSet = Integer.valueOf(pageSize) * (Integer.valueOf(pageIndex) - 1);
        List<StoragePoolRestBean> resultStoragePoolRestBeans = new ArrayList<StoragePoolRestBean>();
        // 分页查询标记位
        int unboundSize = 0;
        try {
            // 获取array所有已绑定的存储池
            List<NStoragePool> bindStoragePoolByArrayId = storagePoolService.getAllBindStoragePoolByArrayId(arrayId);
            LOGGER.debug("The bindStoragePoolByArrayId is : " + bindStoragePoolByArrayId.toString());
            // 获取array所有存储池信息
            SDKResult<List<S2DStoragePool>> storagePoolsFormDevice = vvolModel.getAllStoragePool(arrayId);
            List<S2DStoragePool> s2dStoragePools = storagePoolsFormDevice.getResult();
            LOGGER.debug("The s2dStoragePools is : " + storagePoolsFormDevice.toString());
            // 转换获取的存储池信息
            List<StoragePoolRestBean> storagePoolRestBeans = getStoragePoolInfoFormArray(s2dStoragePools, arrayId,
                    null);
            List<StoragePoolRestBean> ubindStoragePoolRestBeans = new ArrayList<StoragePoolRestBean>();
            // 移除已经绑定的存储池
            for (StoragePoolRestBean storagePoolRestBean : storagePoolRestBeans) {
                for (NStoragePool bindStoragePool : bindStoragePoolByArrayId) {
                    if (!bindStoragePool.getRawPoolId().equals(storagePoolRestBean.getId())) {
                        ubindStoragePoolRestBeans.add(storagePoolRestBean);
                    }
                }
            }

            // 根据分页查询参数，构造返回结果
            for (StoragePoolRestBean storagePoolRestBean : storagePoolRestBeans) {
                if (unboundSize >= offSet && unboundSize < (Integer.valueOf(pageSize) + offSet)) {
                    resultStoragePoolRestBeans.add(storagePoolRestBean);
                }
                unboundSize++;
            }
            response.setCount(unboundSize);
            response.setStoragePools(resultStoragePoolRestBeans);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.info("queryCountOfUnboundStoragePoolByArrayId fail , " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        LOGGER.info("End queryCountOfUnboundStoragePoolByArrayId function");
        return response;
    }

    @GET
    @Path("/queryArrayStoragePool")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStoragePoolResponse queryStoragePoolByArrayId(@QueryParam("arrayId") String arrayId,
                                                              @QueryParam("pageIndex") String pageIndex, @QueryParam("pageSize") String pageSize) {
        LOGGER.info("In VASA queryStoragePoolByArrayId function,the arrayId=" + arrayId + " pageSize=" + pageSize
                + " pageIndex=" + pageIndex);
        QueryStoragePoolResponse response = new QueryStoragePoolResponse();
        if (StringUtils.isEmpty(arrayId) || StringUtils.isEmpty(pageIndex) || StringUtils.isEmpty(pageSize)
                || "0".equals(pageIndex)) {
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }
        try {
            boolean arrayIsOffline = false;
            StorageInfo storageInfo = storageManagerService.queryInfoByArrayId(arrayId);
            if (storageInfo.getDevicestatus().equalsIgnoreCase("OFFLINE")) {
                arrayIsOffline = true;
            }

            SDKResult<List<S2DStoragePool>> storagePoolsFormDevice = vvolModel.getAllStoragePool(arrayId);
            List<S2DStoragePool> s2dStoragePool = storagePoolsFormDevice.getResult();
            response.setCount(s2dStoragePool.size());
            LOGGER.info("The ArrayId = " + arrayId + " total storage pool is " + s2dStoragePool.size());
            // 获取arrayId阵列上的所有storage pool
            List<StoragePoolRestBean> storagePoolRestBeans = getStoragePoolInfoFormArray(s2dStoragePool, arrayId, null);
            // 查询DB，获取array已经绑定的所有storage pool
            List<NStoragePool> nStoragePools = storagePoolService.getAllBindStoragePoolByArrayId(arrayId);
            // 记录结果
            List<StoragePoolRestBean> resultStoragePoolRestBeans = new ArrayList<StoragePoolRestBean>();
            // 分页查询，记录偏移量（直接查询阵列）
            int offSet = Integer.valueOf(pageSize) * (Integer.valueOf(pageIndex) - 1);
            // 分页查询temp 计数位
            int tempCount = 0;
            // 遍历将分页查询结果添加到resultStoragePoolRestBeans
            for (StoragePoolRestBean storagePoolRestBean : storagePoolRestBeans) {
                if (tempCount >= offSet && tempCount < offSet + Integer.valueOf(pageSize)) {
                    // 如果已绑定的存储池需要添加绑定的containerName，未绑定的存储池该项为""
                    for (NStoragePool nStoragePool : nStoragePools) {
                        if (nStoragePool.getArrayId().equals(arrayId)
                                && nStoragePool.getRawPoolId().equals(storagePoolRestBean.getId())) {
                            String containerName = storageContainerService
                                    .getStorageContainerByContainerId(nStoragePool.getContainerId()).getContainerName();
                            storagePoolRestBean.setStorage_container_name(containerName);
                        }
                    }
                    resultStoragePoolRestBeans.add(storagePoolRestBean);
                }
                tempCount++;
            }
            response.setStoragePools(resultStoragePoolRestBeans);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("queryStoragePoolByArrayId fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void queryStoragePool(@QueryParam("storage_array_id") String arrayId, @QueryParam("offset") String offset,
                                 @QueryParam("limit") String limit) {
        LOGGER.info("In VASA QueryStorageContainerList function,the storage_array_id=" + arrayId + " offset" + offset
                + " limit" + limit);
    }

    @POST
    @Path("/bindContainerStoragePool")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader bindStoragePools(BindStoragePoolRequest bindStoragePoolRequest) {
        LOGGER.info("In VASA bindStoragePools function!,arrayId=" + bindStoragePoolRequest.getArrayId() + ",poolIds="
                + bindStoragePoolRequest.getPoolIds() + ",containerId=" + bindStoragePoolRequest.getContainerId());
        ResponseHeader response = new ResponseHeader();
        String containerId = bindStoragePoolRequest.getContainerId();
        String poolIds = bindStoragePoolRequest.getPoolIds();
        String arrayId = bindStoragePoolRequest.getArrayId();
        if (StringUtils.isEmpty(containerId) || StringUtils.isEmpty(poolIds) || StringUtils.isEmpty(arrayId)) {
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        try {
            // 校验阵列是否存在
            StorageInfo queryInfo = storageManagerService.queryInfoByArrayId(arrayId);
            if (queryInfo == null || StringUtils.isEmpty(queryInfo.getId()) || 1 == queryInfo.getDeleted()) {
                LOGGER.error("The storage array is not exist.");
                response.setResultCode(VASAResponseCode.storagePoolService.STORAGE_ARRAY_IS_NOT_EXIST);
                response.setResultDescription(
                        VASAResponseCode.storagePoolService.STORAGE_ARRAY_IS_NOT_EXIST_DESCRIPTION);
                return response;
            }

            // 校验storageContainerId是否存在
            NStorageContainer nStorageContainer = storageContainerService.getStorageContainerByContainerId(containerId);
            if (null == nStorageContainer || StringUtils.isEmpty(nStorageContainer.getContainerId())) {
                LOGGER.error("The storage container is not exist.");
                response.setResultCode(VASAResponseCode.storagePoolService.STORAGE_CONTAINER_IS_NOT_EXIST);
                response.setResultDescription(
                        VASAResponseCode.storagePoolService.STORAGE_CONTAINER_IS_NOT_EXIST_DESCRIPTION);
                return response;
            }
            if (!checkArraySupportLowerQos(containerId, queryInfo)) {
                LOGGER.error("The storage array can not support control lower policy.");
                response.setResultCode(VASAResponseCode.storagePoolService.STORAGE_POOL_NOT_SUPPORT_LOWER_QOS);
                response.setResultDescription(
                        VASAResponseCode.storagePoolService.STORAGE_POOL_NOT_SUPPORT_LOWER_QOS_DESCRIPTION);
                return response;
            }

            boolean isDorado = checkProfileisDorado(containerId, queryInfo);

            /*
             * if(!checkArraySupportSmartTier(containerId,queryInfo,isDorado)){
             * LOGGER.error(
             * "The storage array can not support control smartTier.");
             * response.setResultCode(VASAResponseCode.storagePoolService.
             * STORAGE_POOL_NOT_SUPPORT_SMART_TIER);
             * response.setResultDescription(VASAResponseCode.storagePoolService
             * .STORAGE_POOL_NOT_SUPPORT_SMART_TIER_DESCRIPTION); return
             * response; }
             */

            String[] pools = bindStoragePoolRequest.getPoolIds().split(",");
            List<NStoragePool> storagePoolList = storagePoolService.getAllBindStoragePoolByArrayId(arrayId);
            boolean canBindFlag = false;
            // 获取array已经绑定的存储池raw_id列表
            List<String> bindStoragePoolList = new ArrayList<String>();
            for (NStoragePool nStoragePool : storagePoolList) {
                if (nStoragePool.getDeleted() != true) {
                    bindStoragePoolList.add(nStoragePool.getRawPoolId());
                }
            }
            List<String> alreadyBindPools = new ArrayList<String>();
            for (String poolId : pools) {
                for (String bindedPool : bindStoragePoolList) {
                    if (poolId.equals(bindedPool)) {
                        canBindFlag = true;
                        alreadyBindPools.add(poolId);
                    }
                }
            }

            if (canBindFlag) {
                LOGGER.error("The storage Pools has bind pool Id, please remove the pool id : "
                        + alreadyBindPools.toString());
                response.setResultCode(VASAResponseCode.storagePoolService.STORAGE_POOL_HAS_ALREADY_BIND_POOLS);
                response.setResultDescription(
                        VASAResponseCode.storagePoolService.STORAGE_POOL_NOT_SUPPORT_LOWER_QOS_DESCRIPTION
                                + alreadyBindPools.toString());
                return response;
            }

            // 获取阵列所有存储池
            SDKResult<List<S2DStoragePool>> storagePoolsFormDevice = vvolModel.getAllStoragePool(arrayId);
            List<S2DStoragePool> s2dStoragePools = storagePoolsFormDevice.getResult();

            // 校验不存在的存储池
            List<String> storagePoolIdsFormDevice = new ArrayList<String>();
            for (S2DStoragePool pool : s2dStoragePools) {
                storagePoolIdsFormDevice.add(pool.getID());
            }
            boolean isValid = true;
            List<String> inValidStoragePoolId = new ArrayList<String>();
            for (String pool : pools) {
                if (!storagePoolIdsFormDevice.contains(pool)) {
                    inValidStoragePoolId.add(pool);
                    isValid = false;
                }
            }
            if (!isValid) {
                response.setResultCode(VASAResponseCode.storagePoolService.STORAGE_POOL_IS_NOT_EXIST);
                response.setResultDescription(
                        "The storage Pool Id :" + inValidStoragePoolId.toString() + "is not in array");
                return response;
            }

            List<StoragePoolRestBean> storagePoolRestBeans = getStoragePoolInfoFormArray(s2dStoragePools, arrayId,
                    null);

            for (int i = 0; i < pools.length; i++) {
                // 如果该存储池已经绑定过，则跳过绑定流程
                // if(bindStoragePoolList.contains(pools[i])){
                // continue;
                // }
                NStoragePool storagePool = new NStoragePool();
                storagePool.setId(UUID.randomUUID().toString());
                storagePool.setArrayId(arrayId);
                storagePool.setContainerId(containerId);
                storagePool.setRawPoolId(pools[i]);
                storagePool.setDeleted(false);
                storagePool.setCreatedTime(new Timestamp(System.currentTimeMillis()));
                for (StoragePoolRestBean storagePoolRestBean : storagePoolRestBeans) {
                    if (pools[i].equals(storagePoolRestBean.getId())) {
                        storagePool.setName(storagePoolRestBean.getName());
                        storagePool.setFreeCapacity(storagePoolRestBean.getFree_capacity());
                        storagePool.setTotalCapacity(storagePoolRestBean.getTotal_capacity());
                        storagePool.setDeviceStatus(storagePoolRestBean.getDevice_status());
                        storagePool.setDiskType(storagePoolRestBean.getDisk_type());
                        storagePool.setRaidLevel(storagePoolRestBean.getRaid_level());
                    }
                }
                storagePoolService.bindStoragePools(storagePool);
            }
            String storageContainerType =
                    storageContainerService.getStorageContainerByContainerId(containerId).getContainerType();
            if (storageContainerType.equalsIgnoreCase("NAS")) {

                try {
                    int res = vasaNasArrayService.createGNSshare(arrayId);
                    if (res < 0) {
                        LOGGER.error("create GNS share failed");
                        response.setResultCode(VASAResponseCode.common.ERROR);
                        response.setResultDescription(VASAResponseCode.common.ERROR_DESC + "create GNS share failed");
                    }
                    if (res != 1) {
                        if (vasaNasArrayService.createTempShare(arrayId, pools[0]) != 0) {
                            LOGGER.error("create TEMP share failed");
                            response.setResultCode(VASAResponseCode.common.ERROR);
                            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + "create TEMP share failed");
                        }
                    }

                } catch (Exception e) {
                    LOGGER.error("create GNS TEMP share failed" + e);
                    response.setResultCode(VASAResponseCode.common.ERROR);
                    response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
                }

            }

            List<NStorageProfile> nStorageProfiles = storageProfileService
                    .queryOmCreateStorageProfileByContainerId(containerId);
            for (NStorageProfile storageProfile : nStorageProfiles) {
                if (isDorado) {
                    LOGGER.info("update thinThick = Thin");
                    storageProfile.setThinThick("Thin");
                } else {
                    storageProfile.setThinThick("Thin/Thick");
                }
                storageProfileService.updateStorageProfileByProfileId(storageProfile);
            }
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("bindStoragePools fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    private boolean canRemoveStoragePool(String arrayId, String rawPoolId,
                                         List<StoragePoolRestBean> storagePoolRestBeans) {
        int size = 0;
        try {
            // 第一步：先判断该storagePool下是否存在vvol Lun
            List<NVirtualVolume> virtualVolumeList = virtualVolumeService.getVirtualVolumeByArrayIdAndRawPoolId(arrayId,
                    rawPoolId);
            size = virtualVolumeList.size();
            if (size == 0) {
                LOGGER.info("The arrayId=" + arrayId + " rawPoolId=" + rawPoolId
                        + " can remove from container, the virtualVolume size is " + size);
                return true;
            }
            // 第二步: 如果存在vvol lun，但是storagePool已经从阵列中移除，则可以从storageContainer中移除
            for (StoragePoolRestBean storagePoolRestBean : storagePoolRestBeans) {
                if (storagePoolRestBean.getId().equals(rawPoolId)
                        && storagePoolRestBean.getDevice_status().equalsIgnoreCase("Fault")
                        && storagePoolRestBean.getStatus().equalsIgnoreCase("Abnormal")) {
                    LOGGER.info("The storagePool id=" + rawPoolId + " is fault, the status is "
                            + storagePoolRestBean.getStatus() + " can remove from storageContainer.");
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("canRemoveStoragePool fail. Exception ", e);
        }
        LOGGER.error("The arrayId=" + arrayId + " rawPoolId=" + rawPoolId
                + " can't remove from container, the virtualVolume size is " + size);
        return false;
    }

    @POST
    @Path("/unbindContainerStoragePool")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader unbindStoragePools(UnbindStoragePoolRequest unbindStoragePoolReq) {
        LOGGER.info("In VASA unbindStoragePools function!,arrayId=" + unbindStoragePoolReq.getArrayId() + ",poolIds="
                + unbindStoragePoolReq.getPoolIds() + ",containerId=" + unbindStoragePoolReq.getContainerId());
        ResponseHeader response = new ResponseHeader();
        String arrayId = unbindStoragePoolReq.getArrayId();
        String poolIds = unbindStoragePoolReq.getPoolIds();
        String containerId = unbindStoragePoolReq.getContainerId();
        boolean canUnbind = false;

        if (null == poolIds || null == containerId) {
            LOGGER.error("Parameter invalid.");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        try {
            // 校验storageContainerId是否存在
            NStorageContainer nStorageContainer = storageContainerService.getStorageContainerByContainerId(containerId);
            if (null == nStorageContainer || StringUtils.isEmpty(nStorageContainer.getContainerId())) {
                LOGGER.error("The storage container is not exist.");
                response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
                response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
                return response;
            }

            String[] pools = unbindStoragePoolReq.getPoolIds().split(",");
            List<NStoragePool> poolListByContainerId = storagePoolService
                    .getPoolListByContainerId(unbindStoragePoolReq.getContainerId());
            // 校验ArrayId
            if (StringUtils.isEmpty(arrayId)) {
                arrayId = poolListByContainerId.get(0).getArrayId();
            } else {
                // 校验阵列是否存在
                StorageInfo queryInfo = storageManagerService.queryInfoByArrayId(arrayId);
                if (queryInfo == null || StringUtils.isEmpty(queryInfo.getId()) || 1 == queryInfo.getDeleted()) {
                    LOGGER.error("The storage array is not exist.");
                    response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
                    response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
                    return response;
                }
            }
            // 判断存储池rawid的有效性
            List<String> poolIdListFormContainer = new ArrayList<String>();
            for (NStoragePool nStoragePool : poolListByContainerId) {
                poolIdListFormContainer.add(nStoragePool.getRawPoolId());
            }
            boolean isValid = true;
            List<String> inValidRawPoolIds = new ArrayList<String>();
            for (String rawPoolId : pools) {
                if (!poolIdListFormContainer.contains(rawPoolId)) {
                    isValid = false;
                    inValidRawPoolIds.add(rawPoolId);
                }
            }
            if (!isValid) {
                LOGGER.error("The storagePoolId " + inValidRawPoolIds.toString() + "is not in StorageContainer.");
                response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
                response.setResultDescription(
                        "The storagePoolId " + inValidRawPoolIds.toString() + "is not in StorageContainer.");
                return response;
            }

            // 获取存储池信息
            SDKResult<List<S2DStoragePool>> storagePoolsFormDevice = vvolModel.getAllStoragePool(arrayId);
            List<S2DStoragePool> s2dStoragePools = storagePoolsFormDevice.getResult();

            List<StoragePoolRestBean> storagePoolRestBeans = getStoragePoolInfoFormArray(s2dStoragePools, arrayId,
                    containerId);

            if (null == poolListByContainerId || poolListByContainerId.size() == 0) {
                response.setResultCode(VASAResponseCode.common.SUCCESS);
                response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
                return response;
            }

            for (int i = 0; i < pools.length; i++) {
                if (!canRemoveStoragePool(arrayId, pools[i], storagePoolRestBeans)) {
                    canUnbind = false;
                    break;
                }
                canUnbind = true;
            }
            if (!canUnbind) {
                response.setResultCode(VASAResponseCode.storagePoolService.STORAGE_CONTAINER_CANNOT_REMOVE_STORAGEPOOL);
                response.setResultDescription(
                        VASAResponseCode.storagePoolService.STORAGE_CONTAINER_CANNOT_REMOVE_STORAGEPOOL_DESCRIPTION);
                return response;
            }
            storagePoolService.unbindStoragePools(arrayId, unbindStoragePoolReq.getContainerId(), pools);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("unbindStoragePools fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    @POST
    @Path("/setStoragePoolsLost")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader setStoragePoolsLost(SetStoragePoolLostRequest setStoragePoolLostRequest) {
        LOGGER.info("In VASA setStoragePoolsLost function!,arrayId=" + setStoragePoolLostRequest.getArrayId()
                + ",poolIds=" + setStoragePoolLostRequest.getPoolIds() + ",containerId="
                + setStoragePoolLostRequest.getContainerId());
        ResponseHeader response = new ResponseHeader();
        String arrayId = setStoragePoolLostRequest.getArrayId();
        String poolIds = setStoragePoolLostRequest.getPoolIds();
        String containerId = setStoragePoolLostRequest.getContainerId();

        if (null == poolIds || null == containerId) {
            LOGGER.error("Parameter invalid.");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        try {
            // 校验storageContainerId是否存在
            NStorageContainer nStorageContainer = storageContainerService.getStorageContainerByContainerId(containerId);
            if (null == nStorageContainer || StringUtils.isEmpty(nStorageContainer.getContainerId())) {
                LOGGER.error("The storage container is not exist.");
                response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
                response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
                return response;
            }

            String[] pools = setStoragePoolLostRequest.getPoolIds().split(",");
            List<NStoragePool> poolListByContainerId = storagePoolService
                    .getPoolListByContainerId(setStoragePoolLostRequest.getContainerId());
            // 校验ArrayId
            if (StringUtils.isEmpty(arrayId)) {
                arrayId = poolListByContainerId.get(0).getArrayId();
            } else {
                // 校验阵列是否存在
                StorageInfo queryInfo = storageManagerService.queryInfoByArrayId(arrayId);
                if (queryInfo == null || StringUtils.isEmpty(queryInfo.getId()) || 1 == queryInfo.getDeleted()) {
                    LOGGER.error("The storage array is not exist.");
                    response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
                    response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
                    return response;
                }
            }
            // 判断存储池rawid的有效性
            List<String> poolIdListFormContainer = new ArrayList<String>();
            for (NStoragePool nStoragePool : poolListByContainerId) {
                poolIdListFormContainer.add(nStoragePool.getRawPoolId());
            }
            boolean isValid = true;
            List<String> inValidRawPoolIds = new ArrayList<String>();
            for (String rawPoolId : pools) {
                if (!poolIdListFormContainer.contains(rawPoolId)) {
                    isValid = false;
                    inValidRawPoolIds.add(rawPoolId);
                }
            }
            if (!isValid) {
                LOGGER.error("The storagePoolId " + inValidRawPoolIds.toString() + " is not in StorageContainer.");
                response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
                response.setResultDescription(
                        "The storagePoolId " + inValidRawPoolIds.toString() + "is not in StorageContainer.");
                return response;
            }

            if (null == poolListByContainerId || poolListByContainerId.size() == 0) {
                response.setResultCode(VASAResponseCode.common.SUCCESS);
                response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
                return response;
            }

            storagePoolService.setStoragePoolsLost(arrayId, setStoragePoolLostRequest.getContainerId(), pools);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("setStoragePoolLost fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;

    }

    public List<StoragePoolRestBean> getStoragePoolInfoFormDB(List<NStoragePool> storagePools, String arrayId,
                                                              String containerId) throws StorageFault, SDKException {
        LOGGER.info("In getStoragePoolInfo function");
        List<StoragePoolRestBean> storagePoolRestBeans = new ArrayList<StoragePoolRestBean>();
        String container_name = "";

        if (null != containerId || !"".equals(containerId)) {
            NStorageContainer nStorageContainr = storageContainerService
                    .getStorageContainerByContainerId(storagePools.get(0).getContainerId());
            container_name = nStorageContainr.getContainerName();
        }

        NStoragePool nstoragePool = storagePools.get(0);
        StorageInfo storageInfo = storageManagerService.queryInfoByArrayId(nstoragePool.getArrayId());
        if (storageInfo.getDevicestatus().equalsIgnoreCase("OFFLINE")) {
            for (NStoragePool nStoragePool : storagePools) {
                StoragePoolRestBean storagePoolRestBean = new StoragePoolRestBean();
                storagePoolRestBean.setStorage_array_id(arrayId);
                storagePoolRestBean
                        .setStorage_array_name(storageManagerService.queryInfoByArrayId(arrayId).getStoragename());
                storagePoolRestBean
                        .setStorage_array_ip(storageManagerService.queryInfoByArrayId(arrayId).getIp());
                storagePoolRestBean.setId(nStoragePool.getRawPoolId());
                storagePoolRestBean.setStorage_container_name(container_name);
                storagePoolRestBean.setFree_capacity(nStoragePool.getFreeCapacity());
                storagePoolRestBean.setTotal_capacity(nStoragePool.getTotalCapacity());
                storagePoolRestBean.setName(nStoragePool.getName());
                storagePoolRestBean.setRaid_level(nStoragePool.getRaidLevel());
                storagePoolRestBean.setDevice_status(nStoragePool.getDeviceStatus());
                storagePoolRestBean.setStatus("OFFLINE");
                if (nStoragePool.getDiskType().split("/").length > 1) {
                    storagePoolRestBean.setTiering(true);
                } else {
                    storagePoolRestBean.setTiering(false);
                }
                storagePoolRestBeans.add(storagePoolRestBean);
            }
            return storagePoolRestBeans;
        }

        SDKResult<List<S2DStoragePool>> storagePoolsFormDevice = vvolModel.getAllStoragePool(arrayId);
        List<S2DStoragePool> s2dStoragePool = storagePoolsFormDevice.getResult();

        for (NStoragePool nStoragePool : storagePools) {
            StoragePoolRestBean storagePoolRestBean = new StoragePoolRestBean();
            storagePoolRestBean.setStorage_array_id(arrayId);
            storagePoolRestBean
                    .setStorage_array_name(storageManagerService.queryInfoByArrayId(arrayId).getStoragename());
            storagePoolRestBean
                    .setStorage_array_ip(storageManagerService.queryInfoByArrayId(arrayId).getIp());
            storagePoolRestBean.setId(nStoragePool.getRawPoolId());
            if ("Lost".equalsIgnoreCase(nStoragePool.getDeviceStatus())) {
                storagePoolRestBean.setName(nStoragePool.getName());
                storagePoolRestBean.setFree_capacity(0L);
                storagePoolRestBean.setCapacity_rate(0);
                storagePoolRestBean.setTotal_capacity(0L);
                storagePoolRestBean.setDevice_status("Lost");
                storagePoolRestBean.setStatus("LOST");
                storagePoolRestBeans.add(storagePoolRestBean);
                continue;
            }
            for (S2DStoragePool storagePool : s2dStoragePool) {

                if (storagePool.getID().equalsIgnoreCase(nStoragePool.getRawPoolId())) {
                    Long freeCapacity = Long.valueOf(storagePool.getFreeCapacity()) / 2 * 1024;
                    storagePoolRestBean.setFree_capacity(freeCapacity);
                    Long totalCapacity = Long.valueOf(storagePool.getTotalCapacity()) / 2 * 1024;
                    storagePoolRestBean.setTotal_capacity(totalCapacity);

                    storagePoolRestBean.setName(storagePool.getNAME());

                    String healthStatus = storagePool.getHEALTHSTATUS();
                    if ("1".equalsIgnoreCase(healthStatus)) {
                        storagePoolRestBean.setDevice_status("Normal");
                    } else if ("2".equalsIgnoreCase(healthStatus)) {
                        storagePoolRestBean.setDevice_status("Fault");
                    } else if ("5".equalsIgnoreCase(healthStatus)) {
                        storagePoolRestBean.setDevice_status("Degraded");
                    }

                    String runningstatus = storagePool.getRUNNINGSTATUS();
                    if (runningstatus.equalsIgnoreCase("27")) {
                        storagePoolRestBean.setStatus("ONLINE");
                    } else {
                        storagePoolRestBean.setStatus("OFFLINE");
                    }

                    LOGGER.debug("The diskType : 0 = " + storagePool.getTier0disktype() + " 1="
                            + storagePool.getTier1disktype() + " 2=" + storagePool.getTier2disktype());
                    StringBuilder diskTypeSB = new StringBuilder();
                    Set<String> diskTypeSet = new HashSet<String>();

                    StringBuilder raidLevelSB = new StringBuilder();
                    List<String> raidLevelList = new ArrayList<String>();

                    LOGGER.debug("The diskType : 0 = " + storagePool.getTier0disktype() + " 1="
                            + storagePool.getTier1disktype() + " 2=" + storagePool.getTier2disktype());
                    LOGGER.debug("The raidLevel : 0 = " + storagePool.getTier0RaidLv() + " 1="
                            + storagePool.getTier1RaidLv() + " 2=" + storagePool.getTier2RaidLv());

                    if (!"0".equals(storagePool.getTier0disktype())) {
                        LOGGER.debug("The diskType0 is " + storagePool.getTier0disktype());
                        if ("3".equals(storagePool.getTier0disktype())) {
                            diskTypeSet.add("SSD");
                        }
                        if ("10".equals(storagePool.getTier0disktype())) {
                            diskTypeSet.add("SSD_SED");
                        }
                        setRaidLevel(raidLevelList, storagePool, 0);
                    }
                    if (!"0".equals(storagePool.getTier1disktype())) {
                        LOGGER.debug("The diskType1 is " + storagePool.getTier1disktype());
                        if ("1".equals(storagePool.getTier1disktype())) {
                            diskTypeSet.add("SAS");
                        }
                        if ("8".equals(storagePool.getTier1disktype())) {
                            diskTypeSet.add("SAS_SED");
                        }
                        setRaidLevel(raidLevelList, storagePool, 1);
                    }
                    if (!"0".equals(storagePool.getTier2disktype())) {
                        LOGGER.debug("The diskType2 is " + storagePool.getTier2disktype());
                        if ("2".equals(storagePool.getTier2disktype())) {
                            diskTypeSet.add("SATA");
                        }
                        if ("4".equals(storagePool.getTier2disktype())) {
                            diskTypeSet.add("NL_SAS");
                        }
                        if ("11".equals(storagePool.getTier2disktype())) {
                            diskTypeSet.add("NL_SAS_SED");
                        }
                        setRaidLevel(raidLevelList, storagePool, 2);
                    }
                    Iterator<String> it = diskTypeSet.iterator();
                    while (it.hasNext()) {
                        String str = it.next();
                        diskTypeSB.append(str);
                        if (it.hasNext()) {
                            diskTypeSB.append("/");
                        }
                    }
                    Iterator<String> iterator = raidLevelList.iterator();
                    while (iterator.hasNext()) {
                        String str = iterator.next();
                        raidLevelSB.append(str);
                        if (iterator.hasNext()) {
                            raidLevelSB.append("/");
                        }
                    }
                    storagePoolRestBean.setDisk_type(diskTypeSB.toString());
                    storagePoolRestBean.setRaid_level(raidLevelSB.toString());
                    // smartTier特性待添加
                    if (diskTypeSet.size() > 1) {
                        storagePoolRestBean.setTiering(true);
                    } else {
                        storagePoolRestBean.setTiering(false);
                    }
                }
            }
            storagePoolRestBean.setStorage_container_name(container_name);
            // 存储池在Device Manager上删除，此处无法匹配上storagepool信息
            if (null == storagePoolRestBean.getName()) {
                storagePoolRestBean.setName(nStoragePool.getName());
                storagePoolRestBean.setFree_capacity(0L);
                storagePoolRestBean.setCapacity_rate(0);
                storagePoolRestBean.setTotal_capacity(0L);

                storagePoolRestBean.setDevice_status("Lost");
                storagePoolRestBean.setStatus("LOST");

                String[] pools = new String[1];
                pools[0] = storagePoolRestBean.getId();
                storagePoolService.setStoragePoolsLost(arrayId, containerId, pools);
            }
            storagePoolRestBeans.add(storagePoolRestBean);
        }
        return storagePoolRestBeans;
    }

    public List<StoragePoolRestBean> getStoragePoolInfoFormArray(List<S2DStoragePool> s2dStoragePools, String arrayId,
                                                                 String containerId) throws StorageFault, SDKException {
        LOGGER.info("In getStoragePoolInfoFormArray function, the arrayId=" + arrayId + " containerId=" + containerId);
        List<StoragePoolRestBean> storagePoolRestBeans = new ArrayList<StoragePoolRestBean>();
        String storageContainerName = "";
        if (null == containerId || "".equals(containerId)) {
            storageContainerName = "";
        } else {
            NStorageContainer storageContainer = storageContainerService.getStorageContainerByContainerId(containerId);
            if (null != storageContainer) {
                storageContainerName = storageContainer.getContainerName();
            }
        }
        for (S2DStoragePool s2dStoragePool : s2dStoragePools) {
            // 获取StorageContainer Name
            StoragePoolRestBean storagePoolRestBean = new StoragePoolRestBean();
            if (null != arrayId) {
                storagePoolRestBean.setStorage_array_id(arrayId);
                StorageInfo storageInfo = storageManagerService.queryInfoByArrayId(arrayId);
                if (null != storageInfo) {
                    storagePoolRestBean.setStorage_array_name(storageInfo.getStoragename());
                    storagePoolRestBean.setStorage_array_ip(storageInfo.getIp());
                }
            }

            Long freeCapacity = Long.valueOf(s2dStoragePool.getFreeCapacity()) / 2 * 1024;
            storagePoolRestBean.setFree_capacity(freeCapacity);
            Long totalCapacity = Long.valueOf(s2dStoragePool.getTotalCapacity()) / 2 * 1024;
            storagePoolRestBean.setTotal_capacity(totalCapacity);
            storagePoolRestBean.setName(s2dStoragePool.getNAME());
            storagePoolRestBean.setId(s2dStoragePool.getID());

            int supportSmartTier = 0;
            if (null != s2dStoragePool.getTier1capacity() && !s2dStoragePool.getTier1capacity().equals("0")) {
                supportSmartTier++;
            }
            if (null != s2dStoragePool.getTier2capacity() && !s2dStoragePool.getTier2capacity().equals("0")) {
                supportSmartTier++;
            }
            if (null != s2dStoragePool.getTier0capacity() && !s2dStoragePool.getTier0capacity().equals("0")) {
                supportSmartTier++;
            }
            if (supportSmartTier <= 1) {
                storagePoolRestBean.setTiering(false);
            } else {
                storagePoolRestBean.setTiering(true);
            }

            String healthStatus = s2dStoragePool.getHEALTHSTATUS();
            if ("1".equalsIgnoreCase(healthStatus)) {
                storagePoolRestBean.setDevice_status("Normal");
            } else if ("2".equalsIgnoreCase(healthStatus)) {
                storagePoolRestBean.setDevice_status("Fault");
            } else if ("5".equalsIgnoreCase(healthStatus)) {
                storagePoolRestBean.setDevice_status("Degraded");
            }

            String runningstatus = s2dStoragePool.getRUNNINGSTATUS();
            if (runningstatus.equalsIgnoreCase("27")) {
                storagePoolRestBean.setStatus("ONLINE");
            } else {
                storagePoolRestBean.setStatus("OFFLINE");
            }

            storagePoolRestBean.setStorage_container_name(storageContainerName);

            StringBuilder diskTypeSB = new StringBuilder();
            List<String> diskTypeList = new ArrayList<String>();

            StringBuilder raidLevelSB = new StringBuilder();
            List<String> raidLevelList = new ArrayList<String>();

            LOGGER.debug("The diskType : 0 = " + s2dStoragePool.getTier0disktype() + " 1="
                    + s2dStoragePool.getTier1disktype() + " 2=" + s2dStoragePool.getTier2disktype());
            LOGGER.debug("The raidLevel : 0 = " + s2dStoragePool.getTier0RaidLv() + " 1="
                    + s2dStoragePool.getTier1RaidLv() + " 2=" + s2dStoragePool.getTier2RaidLv());

            if (!"0".equals(s2dStoragePool.getTier0disktype())) {
                LOGGER.debug("The diskType0 is " + s2dStoragePool.getTier0disktype());
                if ("3".equals(s2dStoragePool.getTier0disktype())) {
                    diskTypeList.add("SSD");
                }
                if ("10".equals(s2dStoragePool.getTier0disktype())) {
                    diskTypeList.add("SSD_SED");
                }
                if (!"0".equals(s2dStoragePool.getTier0RaidLv())) {
                    LOGGER.debug("The raidLevel0 is " + s2dStoragePool.getTier0disktype());

                }
                setRaidLevel(raidLevelList, s2dStoragePool, 0);
            }
            if (!"0".equals(s2dStoragePool.getTier1disktype())) {
                LOGGER.debug("The diskType1 is " + s2dStoragePool.getTier1disktype());
                if ("1".equals(s2dStoragePool.getTier1disktype())) {
                    diskTypeList.add("SAS");
                }
                if ("8".equals(s2dStoragePool.getTier1disktype())) {
                    diskTypeList.add("SAS_SED");
                }
                setRaidLevel(raidLevelList, s2dStoragePool, 1);

            }
            if (!"0".equals(s2dStoragePool.getTier2disktype())) {
                LOGGER.debug("The diskType2 is " + s2dStoragePool.getTier2disktype());
                if ("2".equals(s2dStoragePool.getTier2disktype())) {
                    diskTypeList.add("SATA");
                }
                if ("4".equals(s2dStoragePool.getTier2disktype())) {
                    diskTypeList.add("NL_SAS");
                }
                if ("11".equals(s2dStoragePool.getTier2disktype())) {
                    diskTypeList.add("NL_SAS_SED");
                }
                setRaidLevel(raidLevelList, s2dStoragePool, 2);
            }

            Iterator<String> it = diskTypeList.iterator();
            while (it.hasNext()) {
                String str = it.next();
                diskTypeSB.append(str);
                if (it.hasNext()) {
                    diskTypeSB.append("/");
                }
            }
            Iterator<String> iterator = raidLevelList.iterator();
            while (iterator.hasNext()) {
                String str = iterator.next();
                raidLevelSB.append(str);
                if (iterator.hasNext()) {
                    raidLevelSB.append("/");
                }
            }
            storagePoolRestBean.setDisk_type(diskTypeSB.toString());
            storagePoolRestBean.setRaid_level(raidLevelSB.toString());
            storagePoolRestBeans.add(storagePoolRestBean);
        }
        LOGGER.info("End getStoragePoolInfoFormArray function.");
        return storagePoolRestBeans;
    }

    private void setRaidLevel(List<String> raidLevelSet, S2DStoragePool s2dStoragePool, int choose) {
        switch (choose) {
            case 0:
                if (!"0".equals(s2dStoragePool.getTier0RaidLv())) {
                    LOGGER.debug("The raidLevel0 is " + s2dStoragePool.getTier0RaidLv());
                    if ("1".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 10");
                    }
                    if ("2".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 5");
                    }
                    if ("3".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 0");
                    }
                    if ("4".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 1");
                    }
                    if ("5".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 6");
                    }
                    if ("6".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 50");
                    }
                    if ("7".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 3");
                    }
                }
                break;
            case 1:
                if (!"0".equals(s2dStoragePool.getTier1RaidLv())) {
                    LOGGER.debug("The raidLevel1 is " + s2dStoragePool.getTier1RaidLv());
                    if ("1".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 10");
                    }
                    if ("2".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 5");
                    }
                    if ("3".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 0");
                    }
                    if ("4".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 1");
                    }
                    if ("5".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 6");
                    }
                    if ("6".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 50");
                    }
                    if ("7".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 3");
                    }
                }
                break;
            case 2:
                if (!"0".equals(s2dStoragePool.getTier2RaidLv())) {
                    LOGGER.debug("The raidLevel2 is " + s2dStoragePool.getTier2RaidLv());
                    if ("1".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 10");
                    }
                    if ("2".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 5");
                    }
                    if ("3".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 0");
                    }
                    if ("4".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 1");
                    }
                    if ("5".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 6");
                    }
                    if ("6".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 50");
                    }
                    if ("7".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 3");
                    }
                }
            default:
                break;
        }

    }

}
