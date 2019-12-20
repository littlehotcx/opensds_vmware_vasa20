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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.domain.model.VVolModel;
import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.vasa.service.model.StorageProfileData;
import org.opensds.vasa.vasa.util.VASAResponseCode;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.db.model.NStorageContainer;
import org.opensds.vasa.vasa.db.model.NStoragePool;
import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.service.StorageContainerService;
import org.opensds.vasa.vasa.db.service.StorageManagerService;
import org.opensds.vasa.vasa.db.service.StoragePoolService;
import org.opensds.vasa.vasa.db.service.StorageProfileService;
import org.opensds.vasa.vasa.db.service.VirtualVolumeService;
import org.opensds.vasa.vasa.rest.bean.AddStorageContainerRequest;
import org.opensds.vasa.vasa.rest.bean.AddStorageContainerResult;
import org.opensds.vasa.vasa.rest.bean.ResponseHeader;
import org.opensds.vasa.vasa.rest.bean.CountResponseBean;
import org.opensds.vasa.vasa.rest.bean.DeviceTypeMapper;
import org.opensds.vasa.vasa.rest.bean.IsSupportQosLowerRes;
import org.opensds.vasa.vasa.rest.bean.QueryStorageContainerDataResponse;
import org.opensds.vasa.vasa.rest.bean.StorageContainerInfo;
import org.opensds.vasa.vasa.rest.bean.StorageContainerResponseHeaderBean;
import org.opensds.vasa.vasa.rest.bean.StorageContainerRestBean;

import com.vmware.vim.vasa.v20.StorageFault;

@Path("vasa/storageContainer")
public class StorageContainerResource {
    private Logger LOGGER = LogManager.getLogger(StorageContainerResource.class);
    private StorageContainerService storageContainerService = ApplicationContextUtil.getBean("storageContainerService");
    private StoragePoolService storagePoolService = ApplicationContextUtil.getBean("storagePoolService");
    private StorageProfileService storageProfileService = ApplicationContextUtil.getBean("storageProfileService");
    private VirtualVolumeService virtualVolumeService = ApplicationContextUtil.getBean("virtualVolumeService");
    private StorageManagerService storageManagerService = ApplicationContextUtil.getBean("storageManagerService");

    @GET
    @Path("checkSupportLowerQos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IsSupportQosLowerRes CheckIfSupportLowerQos(@QueryParam("containerId") String containerId) {
        IsSupportQosLowerRes supportQosLowerRes = new IsSupportQosLowerRes();
        try {
            boolean supportQosLower = true;
            List<NStoragePool> poolListByContainerId = storagePoolService.getPoolListByContainerId(containerId);
            if (null != poolListByContainerId && poolListByContainerId.size() > 0) {
                StorageInfo storageInfo = storageManagerService.queryInfoByArrayId(poolListByContainerId.get(0).getArrayId());
                if (null != storageInfo) {
                    LOGGER.info("CheckIfSupportLowerQos storageInfo=" + storageInfo);
                    supportQosLower = DeviceTypeMapper.isSupportQosLower(storageInfo.getModel());
                }
            }
            supportQosLowerRes.setResultCode(VASAResponseCode.common.SUCCESS);
            supportQosLowerRes.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
            supportQosLowerRes.setSupport(supportQosLower);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("CheckIfSupportLowerQos error. ContainerId=" + containerId, e);
            supportQosLowerRes.setResultCode(VASAResponseCode.common.ERROR);
            supportQosLowerRes.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        return supportQosLowerRes;
    }

    @GET
    @Path("count")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CountResponseBean getStorageContainerCount() {
        CountResponseBean response = new CountResponseBean();
        try {
            long count = storageContainerService.getStorageContainerCount();
            response.setCount(count);
            response.setResultCode(0L);
            response.setResultDescription("getStorageContainerCount successfully.");
        } catch (Exception e) {
            response.setResultCode(1L);
            response.setResultDescription("getStorageContainerCount fail.");
        }
        return response;
    }

    @GET
    @Path("all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStorageContainerDataResponse queryAllStorageContainer() {
        LOGGER.info("In VASA queryAllStorageContainer function");
        QueryStorageContainerDataResponse response = new QueryStorageContainerDataResponse();
        try {
            List<NStorageContainer> totalStorageContainers = storageContainerService.getAll();
            List<StorageContainerRestBean> storageContainerRestBeans = new ArrayList<StorageContainerRestBean>();

            for (NStorageContainer sotrageContainer : totalStorageContainers) {
                StorageContainerRestBean storageContainerRestBean = new StorageContainerRestBean();
                storageContainerRestBean.setId(sotrageContainer.getContainerId());
                storageContainerRestBean.setName(sotrageContainer.getContainerName());
                storageContainerRestBean.setDescription(sotrageContainer.getDescription());
                storageContainerRestBean.setContainer_type(sotrageContainer.getContainerType());

                StorageContainerInfo storageContainerInfo = getStorageContainerCapabilityInfo(
                        sotrageContainer.getContainerId());

                storageContainerRestBean.setCapacity_rate(storageContainerInfo.getCapacity_rate());
                storageContainerRestBean.setFree_capacity(storageContainerInfo.getFree_capacity());
                storageContainerRestBean.setTotal_capacity(storageContainerInfo.getTotal_capacity());
                storageContainerRestBean.setStorage_array_ip(storageContainerInfo.getStorage_array_ip());
                // add search storage pool size method
                storageContainerRestBeans.add(storageContainerRestBean);
            }
            response.setStorageContainers(storageContainerRestBeans);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("Query storageContainers error: " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStorageContainerDataResponse queryStorageContainerList(@QueryParam("offset") String offset,
                                                                       @QueryParam("pageSize") String pageSize, @QueryParam("pageIndex") String pageIndex) {
        LOGGER.info("In VASA QueryStorageContainerList function,the parameter is : offset=" + offset + " pageSize="
                + pageSize + " pageIndex=" + pageIndex);
        QueryStorageContainerDataResponse response = new QueryStorageContainerDataResponse();
        if (StringUtils.isEmpty(pageSize) || StringUtils.isEmpty(pageIndex) || "0".equals(pageIndex)) {
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        try {
            List<NStorageContainer> totalStorageContainers = storageContainerService.getAll();
            response.setCount(totalStorageContainers.size());

            List<NStorageContainer> storageContainers = storageContainerService.getStorageContainerByPageSize(offset,
                    pageSize, pageIndex);


            List<StorageContainerRestBean> storageContainerRestBeans = new ArrayList<StorageContainerRestBean>();

            for (NStorageContainer sotrageContainer : storageContainers) {
                StorageContainerRestBean storageContainerRestBean = new StorageContainerRestBean();
                storageContainerRestBean.setId(sotrageContainer.getContainerId());
                storageContainerRestBean.setName(sotrageContainer.getContainerName());
                storageContainerRestBean.setDescription(sotrageContainer.getDescription());
                storageContainerRestBean.setContainer_type(sotrageContainer.getContainerType());

                StorageContainerInfo storageContainerInfo = getStorageContainerCapabilityInfo(
                        sotrageContainer.getContainerId());
                storageContainerRestBean.setCapacity_rate(storageContainerInfo.getCapacity_rate());
                storageContainerRestBean.setFree_capacity(storageContainerInfo.getFree_capacity());
                storageContainerRestBean.setTotal_capacity(storageContainerInfo.getTotal_capacity());
                storageContainerRestBean.setStorage_array_name(storageContainerInfo.getStorage_array_name());
                storageContainerRestBean.setStorage_array_ip(storageContainerInfo.getStorage_array_ip());
                // add search storage pool size method
                storageContainerRestBeans.add(storageContainerRestBean);
            }
            response.setStorageContainers(storageContainerRestBeans);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("Query storageContainers error: " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    @GET
    @Path("queryByContainerId")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStorageContainerDataResponse queryStorageContainerByStorageContainerId(
            @QueryParam("storageContainerId") String storageContainerId) {
        LOGGER.info("In VASA QueryStorageContainerList function,the parameter is : storageContainerId="
                + storageContainerId);
        QueryStorageContainerDataResponse response = new QueryStorageContainerDataResponse();
        if (null == storageContainerId) {
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        try {
            List<NStorageContainer> totalStorageContainers = storageContainerService.getAll();
            NStorageContainer nStorageContainer = storageContainerService
                    .getStorageContainerByContainerId(storageContainerId);

            List<StorageContainerRestBean> storageContainerRestBeans = new ArrayList<StorageContainerRestBean>();
            StorageContainerRestBean storageContainerRestBean = new StorageContainerRestBean();
            storageContainerRestBean.setId(nStorageContainer.getContainerId());
            storageContainerRestBean.setName(nStorageContainer.getContainerName());
            storageContainerRestBean.setDescription(nStorageContainer.getDescription());
            storageContainerRestBean.setContainer_type(nStorageContainer.getContainerType());

            StorageContainerInfo storageContainerInfo = getStorageContainerCapabilityInfo(
                    nStorageContainer.getContainerId());
            storageContainerRestBean.setCapacity_rate(storageContainerInfo.getCapacity_rate());
            storageContainerRestBean.setFree_capacity(storageContainerInfo.getFree_capacity());
            storageContainerRestBean.setTotal_capacity(storageContainerInfo.getTotal_capacity());
            storageContainerRestBean.setStorage_array_ip(storageContainerInfo.getStorage_array_ip());

            // add search storage pool size method
            storageContainerRestBeans.add(storageContainerRestBean);
            response.setCount(totalStorageContainers.size());
            response.setStorageContainers(storageContainerRestBeans);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);

        } catch (Exception e) {
            LOGGER.error("Query storageContainers error: " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AddStorageContainerResult addStorageContainer(AddStorageContainerRequest request) {
        LOGGER.info("In VASA AddStorageContainer function,the parameter : name=" + request.getName() + " description="
                + request.getDescription() + " type=" + request.getType());
        AddStorageContainerResult response = new AddStorageContainerResult();
        NStorageContainer storageContainer = new NStorageContainer();
        if (StringUtils.isEmpty(request.getName())) {
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        boolean hasDifferentType = false;
        List<NStorageContainer> totalStorageContainers = storageContainerService.getAll();
        for (NStorageContainer nStorageContainer : totalStorageContainers) {
            if (!nStorageContainer.getContainerType().equals(request.getType())) {
                hasDifferentType = true;
            }
        }
        if (hasDifferentType) {
            response.setResultCode(VASAResponseCode.common.INVALID_TYPE);
            response.setResultDescription(VASAResponseCode.common.INVALID_TYPE_DESCRIPTION);
            LOGGER.error("NAS and SAN storageContain can not create in same VASA Provider");
            return response;
        }
        try {
            List<NStorageContainer> storageContainers = storageContainerService
                    .getStorageContainerByContainerName(request.getName());
            LOGGER.info("the storageContainer is :" + storageContainers);
            if (storageContainers.size() != 0) {
                LOGGER.info("the storageContainer name : " + request.getName() + " has already exist.");
                response.setResultCode(VASAResponseCode.storageContainerService.STORAGE_CONTAINER_ALREADY_EXIST);
                response.setResultDescription(
                        VASAResponseCode.storageContainerService.STORAGE_CONTAINER_ALREADY_EXIST_DESCRIPTION);
                return response;
            }
            storageContainer.setContainerId(UUID.randomUUID().toString());
            storageContainer.setContainerName(request.getName());
            if (StringUtils.isEmpty(request.getDescription())) {
                storageContainer.setDescription("");
            } else {
                storageContainer.setDescription(request.getDescription());
            }
            storageContainer.setContainerType(request.getType());
            storageContainer.setCreatedTime(new Timestamp(System.currentTimeMillis()));
            storageContainer.setDeleted(false);

            List<StorageContainerResponseHeaderBean> storageContainerRestBeans = new ArrayList<StorageContainerResponseHeaderBean>();
            StorageContainerResponseHeaderBean storageContainerRestBean = new StorageContainerResponseHeaderBean();

            storageContainerRestBean.setId(storageContainer.getContainerId());
            storageContainerRestBean.setName(storageContainer.getContainerName());
            storageContainerRestBean.setDescription(storageContainer.getDescription());
            storageContainerRestBean.setType(storageContainer.getContainerType());
            storageContainerRestBeans.add(storageContainerRestBean);
            storageContainerService.save(storageContainer);

            response.setStorageContainers(storageContainerRestBeans);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);

        } catch (StorageFault e) {
            LOGGER.error("query storage container by name fail." + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader deleteStorageContainer(@QueryParam("containerId") String containerId) {
        LOGGER.info("In DeleteStorageContainer, containerId=" + containerId);
        ResponseHeader response = new ResponseHeader();
        NStorageContainer nStorageContainer = null;

        if (StringUtils.isEmpty(containerId)) {
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        try {
            nStorageContainer = storageContainerService.getStorageContainerByContainerId(containerId);
            if (null == nStorageContainer || StringUtils.isEmpty(nStorageContainer.getContainerId())) {
                LOGGER.error("The storage container is not exist.");
                response.setResultCode(VASAResponseCode.storageContainerService.STORAGE_CONTAINER_IS_NOT_EXIST);
                response.setResultDescription(VASAResponseCode.storageContainerService.STORAGE_CONTAINER_IS_NOT_EXIST_DESCRIPTION);
                return response;
            }
            if (null != nStorageContainer.getDeleted() && true == nStorageContainer.getDeleted()) {
                LOGGER.info("The StorageContainerId has already delete");
                response.setResultCode(VASAResponseCode.common.SUCCESS);
                response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
                return response;
            }

            List<NVirtualVolume> virtualVolumesByContainerId = virtualVolumeService
                    .getAllVirtualVolumeByContainerId(containerId);
            if (null != virtualVolumesByContainerId && virtualVolumesByContainerId.size() > 0) {
                LOGGER.error("delete storage container err. It is being used. containerId=" + containerId);
                response.setResultCode(VASAResponseCode.storageContainerService.STORAGE_CONTAINER_BEING_USED);
                response.setResultDescription(
                        VASAResponseCode.storageContainerService.STORAGE_CONTAINER_BEING_USED_DESCRIPTION);
                return response;
            }

            List<NStoragePool> nStoragePools = storagePoolService.queryStoragePoolByContainerId(containerId);
            if (nStoragePools.size() != 0) {
                response.setResultCode(VASAResponseCode.storageContainerService.STORAGE_POOL_BIND_IN_CONTAINER);
                response.setResultDescription(
                        VASAResponseCode.storageContainerService.STORAGE_POOL_BIND_IN_CONTAINER_DESCRIPTION);
                return response;
            }
            List<StorageProfileData> nStorageProfiles = storageProfileService
                    .getStorageProfileByContainerId(containerId);
            if (nStorageProfiles.size() != 0) {
                response.setResultCode(VASAResponseCode.storageContainerService.STORAGE_PROFILE_BIND_IN_CONTAINER);
                response.setResultDescription(
                        VASAResponseCode.storageContainerService.STORAGE_PROFILE_BIND_IN_CONTAINER_DESCRIPTION);
                return response;
            }
            nStorageContainer.setDeleted(true);
            nStorageContainer.setDeletedTime(new Timestamp(System.currentTimeMillis()));
            storageContainerService.deleteStorageContainerByContainerId(nStorageContainer);
            // storageContainerService.delStorageContainerByContainerId(containerId);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("deleteStorageContainer fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    public void getStorageContainerCapabilityInfoForDB(List<NStoragePool> nStoragePools, StorageContainerInfo result) {
        float capacity_rate = 0f;
        long free_capacity = 0l;
        long total_capacity = 0l;

        for (NStoragePool nstoragePool : nStoragePools) {
            free_capacity += nstoragePool.getFreeCapacity();
            total_capacity += nstoragePool.getTotalCapacity();
        }

        result.setFree_capacity(free_capacity);
        result.setTotal_capacity(total_capacity);
        if (total_capacity != 0l) {
            capacity_rate = free_capacity / total_capacity;
        }
        result.setCapacity_rate(capacity_rate);
    }

    public StorageContainerInfo getStorageContainerCapabilityInfo(String containerId) throws SDKException {
        LOGGER.info("In getStorageContainerCapabilityInfo function.");
        StorageContainerInfo result = new StorageContainerInfo();
        float capacity_rate = 0f;
        long free_capacity = 0l;
        long total_capacity = 0l;
        String array_name = "";
        String array_ip = "";
        result.setCapacity_rate(capacity_rate);
        result.setFree_capacity(free_capacity);
        result.setTotal_capacity(total_capacity);
        result.setStorage_array_name(array_name);
        result.setStorage_array_ip(array_ip);

        List<NStoragePool> nStoragePools = storagePoolService.queryStoragePoolByContainerId(containerId);
        if (nStoragePools.size() == 0) {
            LOGGER.info("The storageContainer's storagePool size is " + nStoragePools.size());
            return result;
        }

        NStoragePool storagePool = nStoragePools.get(0);
        StorageInfo storageInfo = storageManagerService.queryInfoByArrayId(storagePool.getArrayId());
        if (storageInfo.getDevicestatus().equalsIgnoreCase("OFFLINE")) {
            getStorageContainerCapabilityInfoForDB(nStoragePools, result);
            result.setStorage_array_name(storageInfo.getStoragename());
            result.setStorage_array_ip(storageInfo.getIp());
            return result;
        }

        try {
            Set<String> arrayList = new HashSet<String>();
            for (NStoragePool nStoragePool : nStoragePools) {
                arrayList.add(nStoragePool.getArrayId());
            }

            if (arrayList.size() == 0 || arrayList.size() > 1) {
                LOGGER.error("ERROR storagePool has no arrayId");
                return result;
            }

            String arrayId = nStoragePools.get(0).getArrayId();

            VVolModel vvolModel = new VVolModel();
            SDKResult<List<S2DStoragePool>> storagePoolsFormDevice = vvolModel.getAllStoragePool(arrayId);
            List<S2DStoragePool> s2dStoragePools = storagePoolsFormDevice.getResult();

            for (NStoragePool nStoragePool : nStoragePools) {
                for (S2DStoragePool s2dStoragePool2 : s2dStoragePools) {
                    if (!"Lost".equalsIgnoreCase(nStoragePool.getDeviceStatus()) && nStoragePool.getRawPoolId().equals(s2dStoragePool2.getID())) {
                        Long freeCapacity = Long.valueOf(s2dStoragePool2.getFreeCapacity()) / 2 * 1024;
                        free_capacity += freeCapacity;
                        Long totalCapacity = Long.valueOf(s2dStoragePool2.getTotalCapacity()) / 2 * 1024;
                        total_capacity += totalCapacity;
                    }
                }
            }
            result.setStorage_array_name(storageInfo.getStoragename());
            result.setStorage_array_ip(storageInfo.getIp());
            result.setFree_capacity(free_capacity);
            result.setTotal_capacity(total_capacity);
            if (total_capacity != 0l) {
                capacity_rate = free_capacity / total_capacity;
            }
            result.setCapacity_rate(capacity_rate);

        } catch (Exception e) {
            LOGGER.error("get getStorageContainerCapabilityInfo fail, read form db", e);
            getStorageContainerCapabilityInfoForDB(nStoragePools, result);
        }
        return result;

    }
}