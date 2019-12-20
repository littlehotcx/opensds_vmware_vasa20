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
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.common.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.service.model.StorageProfileData;
import org.opensds.vasa.vasa.util.VASAResponseCode;

import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.db.model.NProfileLevel;
import org.opensds.vasa.vasa.db.model.NStorageCapabilityQos;
import org.opensds.vasa.vasa.db.model.NStoragePool;
import org.opensds.vasa.vasa.db.model.NStorageProfile;
import org.opensds.vasa.vasa.db.model.NStorageQos;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.service.ProfileLevelService;
import org.opensds.vasa.vasa.db.service.StorageCapabilityQosService;
import org.opensds.vasa.vasa.db.service.StorageContainerService;
import org.opensds.vasa.vasa.db.service.StorageManagerService;
import org.opensds.vasa.vasa.db.service.StoragePoolService;
import org.opensds.vasa.vasa.db.service.StorageProfileService;
import org.opensds.vasa.vasa.db.service.StorageQosService;
import org.opensds.vasa.vasa.rest.bean.CountResponseBean;
import org.opensds.vasa.vasa.rest.bean.CreateStorageProfileBean;
import org.opensds.vasa.vasa.rest.bean.CreateStorageProfileResult;
import org.opensds.vasa.vasa.rest.bean.DeviceTypeMapper;
import org.opensds.vasa.vasa.rest.bean.QueryStorageProfileResponse;
import org.opensds.vasa.vasa.rest.bean.ResponseHeader;
import org.opensds.vasa.vasa.rest.bean.StorageProfileResponseHeaderBean;
import org.opensds.vasa.vasa.rest.bean.StorageProfileRestBean;

import com.vmware.vim.vasa.v20.StorageFault;

@Path("vasa/storageProfile")
public class StorageProfileResource {
    private Logger LOGGER = LogManager.getLogger(StorageProfileResource.class);
    private StorageProfileService storageProfileService = ApplicationContextUtil.getBean("storageProfileService");
    private StorageQosService storageQosSerivce = ApplicationContextUtil.getBean("storageQosService");
    private StorageContainerService storageContainerService = ApplicationContextUtil.getBean("storageContainerService");
    private ProfileLevelService profileLevelService = ApplicationContextUtil.getBean("profileLevelService");
    private StoragePoolService poolService = ApplicationContextUtil.getBean("storagePoolService");
    private StorageManagerService storageManagerService = ApplicationContextUtil.getBean("storageManagerService");
    private StorageCapabilityQosService storageCapabilityQosService = ApplicationContextUtil.getBean("storageCapabilityQosService");

    @GET
    @Path("count")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CountResponseBean getStorageProfileCount() {
        LOGGER.info("In VASA getStorageProfileCount function");
        CountResponseBean response = new CountResponseBean();
        try {
            long count = storageProfileService.getStorageProfileCount();
            response.setCount(count);
            response.setResultCode(0L);
            response.setResultDescription("getStorageProfileCount successfully.");
        } catch (Exception e) {
            response.setResultCode(1L);
            response.setResultDescription("getStorageProfileCount fail.");
        }
        return response;
    }

    private boolean converStorageProfile(List<NStorageProfile> storageProfiles,
                                         List<StorageProfileRestBean> storageProfileRestBeans, boolean isOmCreate) {
        try {
            for (NStorageProfile storageProfile : storageProfiles) {
                if (storageProfile.getOmCreated() == null) {
                    LOGGER.warn("the storageProfile do not have omCreate description.");
                    continue;
                }
                // 如果需要过滤OM Create 的profile 且 storageProfile不是OM创建的，则跳过
                if (isOmCreate && !storageProfile.getOmCreated()) {
                    continue;
                }

                StorageProfileRestBean storageProfileRestBean = new StorageProfileRestBean();
                storageProfileRestBeans.add(storageProfileRestBean);

                storageProfileRestBean.setProfileType(storageProfile.getThinThick());
                storageProfileRestBean.setId(storageProfile.getProfileId());
                storageProfileRestBean.setName(storageProfile.getProfileName().substring(4));
                storageProfileRestBean.setStorageContainerId(storageProfile.getContainerId());
                String containerName = storageContainerService
                        .getStorageContainerByContainerId(storageProfile.getContainerId()).getContainerName();
                storageProfileRestBean.setStorageContainerName(containerName);
                storageProfileRestBean.setControlType(storageProfile.getControlType());

                if (storageProfile.getIsSmartTier()) {
                    storageProfileRestBean.setQosSmartTier("1");
                    storageProfileRestBean.setQosSmartTierValue(storageProfile.getSmartTierValue());
                } else {
                    storageProfileRestBean.setQosSmartTier("0");
                }
                if (storageProfile.getIsStorageMedium()) {
                    storageProfileRestBean.setIsStorageMedium("1");
                    storageProfileRestBean.setDiskTypeValue(storageProfile.getDiskTypeValue());
                    storageProfileRestBean.setRaidLevelValue(storageProfile.getRaidLevelValue());
                } else {
                    storageProfileRestBean.setIsStorageMedium("0");
                }
                if (StringUtils.isEmpty(storageProfile.getControlTypeId())) {
                    continue;
                }

                if (storageProfile.getControlType().equals(NStorageProfile.ControlType.precision_control)) {
                    NStorageQos storageQos = storageQosSerivce.getStorageQosByQosId(storageProfile.getControlTypeId());
                    if (null == storageQos) {
                        LOGGER.error("query storage profile fail.");
                        return false;
                    }
                    storageProfileRestBean.setQoSControl("1");
                    storageProfileRestBean.setQoSControlType(storageQos.getControlType());

                    if ("Read I/O".equalsIgnoreCase(storageQos.getControlType())) {
                        storageProfileRestBean.setQoSControlType("0");
                    } else if ("Write I/O".equalsIgnoreCase(storageQos.getControlType())) {
                        storageProfileRestBean.setQoSControlType("1");
                    } else if ("Read/Write I/Os".equalsIgnoreCase(storageQos.getControlType())) {
                        storageProfileRestBean.setQoSControlType("2");
                    } else {
                        LOGGER.error("the storageProfile qos control type is error.");
                        return false;
                    }

                    if ("Control upper bound".equalsIgnoreCase(storageQos.getControlPolicy())) {
                        storageProfileRestBean.setQoSControlPolicy("QoSUpperLimitControl");
                    } else if ("Control lower bound".equalsIgnoreCase(storageQos.getControlPolicy())) {
                        storageProfileRestBean.setQoSControlPolicy("QoSLowerLimitControl");
                    } else {
                        LOGGER.error("the storageProfile qos control policy is error.");
                        return false;
                    }
                    if (null != storageQos.getBandWidth()) {
                        storageProfileRestBean
                                .setQosControlObjectiveBandwidth(String.valueOf(storageQos.getBandWidth()));
                    }
                    if (null != storageQos.getIops()) {
                        storageProfileRestBean.setQosControlObjectiveIOPS(String.valueOf(storageQos.getIops()));
                    }
                    if (null != storageQos.getLatency()) {
                        storageProfileRestBean.setQosControlObjectiveLatency(String.valueOf(storageQos.getLatency()));
                    }
                }
                if (storageProfile.getControlType().equals(NStorageProfile.ControlType.capability_control)) {
                    NStorageCapabilityQos storageCapabilityQos = storageCapabilityQosService.getStorageCapabilityQosById(storageProfile.getControlTypeId());
                    if (null == storageCapabilityQos) {
                        LOGGER.error("query NStorageCapabilityQos fail.id=" + storageProfile.getControlTypeId());
                        return false;
                    }
                    storageProfileRestBean.setQoSControl("1");
                    storageProfileRestBean.setQoSControlType(storageCapabilityQos.getQosControlType());

                    if ("Read I/O".equalsIgnoreCase(storageCapabilityQos.getQosControlType())) {
                        storageProfileRestBean.setQoSControlType("0");
                    } else if ("Write I/O".equalsIgnoreCase(storageCapabilityQos.getQosControlType())) {
                        storageProfileRestBean.setQoSControlType("1");
                    } else if ("Read/Write I/Os".equalsIgnoreCase(storageCapabilityQos.getQosControlType())) {
                        storageProfileRestBean.setQoSControlType("2");
                    } else {
                        LOGGER.error("the storageProfile qos control type is error.");
                        return false;
                    }

                    if ("Control upper bound".equalsIgnoreCase(storageCapabilityQos.getQosControlPolicy())) {
                        storageProfileRestBean.setQoSControlPolicy("QoSUpperLimitControl");
                    } else if ("Control lower bound".equalsIgnoreCase(storageCapabilityQos.getQosControlPolicy())) {
                        storageProfileRestBean.setQoSControlPolicy("QoSLowerLimitControl");
                    } else {
                        LOGGER.error("the storageProfile qos control policy is error.");
                        return false;
                    }
                    if (null != storageCapabilityQos.getBandwidth()) {
                        storageProfileRestBean.setQosControlObjectiveBandwidth(String.valueOf(storageCapabilityQos.getBandwidth()));
                    }
                    if (null != storageCapabilityQos.getIops()) {
                        storageProfileRestBean.setQosControlObjectiveIOPS(String.valueOf(storageCapabilityQos.getIops()));
                    }
                    if (null != storageCapabilityQos.getLatency()) {
                        storageProfileRestBean.setQosControlObjectiveLatency(String.valueOf(storageCapabilityQos.getLatency()));
                    }
                }
                if (storageProfile.getControlType().equals(NStorageProfile.ControlType.level_control)) {
                    NProfileLevel profileLevel = profileLevelService.getById(storageProfile.getControlTypeId());
                    storageProfileRestBean.setQosUserLevel(profileLevel.getUserLevel());
                    storageProfileRestBean.setQosServiceType(profileLevel.getServiceType());
                }
                LOGGER.info(storageProfileRestBeans.toString());
            }
        } catch (Exception e) {
            LOGGER.error("converStorageProfile fail : ", e);
            return false;
        }
        return true;
    }

    @GET
    @Path("/queryStorageOmProfileByProfileId")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStorageProfileResponse queryStorageOmProfileByProfileId(@QueryParam("profileId") String profileId) {
        LOGGER.info("In queryStorageOmProfileByProfileId function, the profileId = " + profileId);
        QueryStorageProfileResponse response = new QueryStorageProfileResponse();

        if (StringUtils.isEmpty(profileId)) {
            LOGGER.error("the profileid is invalid.");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        try {
            NStorageProfile storageProfile = storageProfileService.getStorageProfileByProfileId(profileId);
            if (storageProfile.getProfileId() == null) {
                LOGGER.error("The storage profile is not exist.");
                response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_IS_NOT_EXIST);
                response.setResultDescription(VASAResponseCode.storageProfileService.STORAGE_PROFILE_IS_NOT_EXIST_DESCRIPTION);
                return response;
            }
            List<StorageProfileRestBean> storageProfiles = new ArrayList<StorageProfileRestBean>();
            List<NStorageProfile> nStorageProfiles = new ArrayList<NStorageProfile>();
            nStorageProfiles.add(storageProfile);
            converStorageProfile(nStorageProfiles, storageProfiles, true);
            response.setStorageProfile(storageProfiles);
            response.setCount(storageProfiles.size());
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("queryStorageOmProfileByProfileId error.", e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        return response;
    }

    @GET
    @Path("/queryStoragOmProfileByContainerId")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStorageProfileResponse queryStoragOmProfileByContainerId(@QueryParam("isOMCreate") String isOMCreate,
                                                                         @QueryParam("containerId") String containerId) {
        LOGGER.info("In VASA queryStoragOmProfile function");

        QueryStorageProfileResponse response = new QueryStorageProfileResponse();
        try {
            List<NStorageProfile> storageProfiles = storageProfileService
                    .queryOmCreateStorageProfileByContainerId(containerId);
            List<StorageProfileRestBean> storageProfileRestBeans = new ArrayList<StorageProfileRestBean>();
            if (!converStorageProfile(storageProfiles, storageProfileRestBeans, true)) {
                response.setResultCode(VASAResponseCode.common.ERROR);
                response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
                return response;
            }
            response.setStorageProfile(storageProfileRestBeans);
            response.setCount(storageProfileRestBeans.size());
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("query storage profile fail, ", e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        LOGGER.info("End VASA queryStoragOmProfile function");
        return response;
    }

    @GET
    @Path("/queryStoragOmProfileByProfileId")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStorageProfileResponse queryStoragOmProfileByProfileId(@QueryParam("isOMCreate") String isOMCreate,
                                                                       @QueryParam("profileId") String profileId) {
        LOGGER.info("In VASA queryStoragOmProfile function");

        QueryStorageProfileResponse response = new QueryStorageProfileResponse();
        try {
            NStorageProfile storageProfile = storageProfileService.getStorageProfileByProfileId(profileId);
			/*
			if (null == storageProfile || StringUtils.isEmpty(storageProfile.getProfileId())) {
				LOGGER.info("The storage Profile : " + profileId + " is not exist.");
				response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_PROFILEID_INVALID);
				response.setResultDescription(
						VASAResponseCode.storageProfileService.STORAGE_PROFILE_PROFILEID_INVALID_DESCRIPTION);
				return response;
			}
			*/
            List<NStorageProfile> storageProfiles = new ArrayList<NStorageProfile>();
            storageProfiles.add(storageProfile);
            List<StorageProfileRestBean> storageProfileRestBeans = new ArrayList<StorageProfileRestBean>();
            if (!converStorageProfile(storageProfiles, storageProfileRestBeans, true)) {
                response.setResultCode(VASAResponseCode.common.ERROR);
                response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
                return response;
            }
            response.setStorageProfile(storageProfileRestBeans);
            response.setCount(storageProfileRestBeans.size());
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("query storage profile fail, ", e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        LOGGER.info("End VASA queryStoragOmProfile function");
        return response;
    }

    @GET
    @Path("/queryStoragOmProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStorageProfileResponse queryStoragOmProfile(@QueryParam("isOMCreate") String isOMCreate,
                                                            @QueryParam("pageIndex") String pageIndex, @QueryParam("pageSize") String pageSize) {
        LOGGER.info("In VASA queryStoragOmProfile function, the request parameter : isOMCreate = " + isOMCreate
                + " pageIndex=" + pageIndex + " pageSize=" + pageSize);

        QueryStorageProfileResponse response = new QueryStorageProfileResponse();
        try {
            List<NStorageProfile> storageProfiles = storageProfileService.getAll();
            List<StorageProfileRestBean> storageProfileRestBeans = new ArrayList<StorageProfileRestBean>();
            if (!converStorageProfile(storageProfiles, storageProfileRestBeans, true)) {
                response.setResultCode(VASAResponseCode.common.ERROR);
                response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
                return response;
            }
            response.setStorageProfile(storageProfileRestBeans);
            response.setCount(storageProfileRestBeans.size());
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("query storage profile fail, ", e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        LOGGER.info("End VASA queryStoragOmProfile function");
        return response;
    }

    @GET
    @Path("/queryAllStorageProfileByPage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStorageProfileResponse queryAllStorageProfileByPage(@QueryParam("pageIndex") String pageIndex,
                                                                    @QueryParam("pageSize") String pageSize) {
        LOGGER.info("In VASA queryAllStorageProfileByPage function, the pageIndex = " + pageIndex + " pageSize="
                + pageSize);
        QueryStorageProfileResponse response = new QueryStorageProfileResponse();
        if (StringUtils.isEmpty(pageSize) || StringUtils.isEmpty(pageIndex) || "0".equals(pageIndex)) {
            LOGGER.error("Invalid parameter.");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        try {
            List<NStorageProfile> storageProfiles = storageProfileService.queryOmCreateStorageProfileByPage(pageIndex,
                    pageSize);
            List<StorageProfileRestBean> storageProfileRestBeans = new ArrayList<StorageProfileRestBean>();
            if (!converStorageProfile(storageProfiles, storageProfileRestBeans, false)) {
                response.setResultCode(VASAResponseCode.common.ERROR);
                response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
                return response;
            }
            response.setStorageProfile(storageProfileRestBeans);
            response.setCount(storageProfileRestBeans.size());
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("query storage profile fail, " + e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        LOGGER.info("End VASA queryStoragOmProfile function");
        return response;
    }

    @GET
    @Path("/queryStorageProfileByProfileId")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStorageProfileResponse queryStorageProfileByprofileId(@QueryParam("profileId") String profileId) {
        LOGGER.info("In VASA queryAllStorageProfileByprofileId function, the profileId = " + profileId);
        QueryStorageProfileResponse response = new QueryStorageProfileResponse();
        if (StringUtils.isEmpty(profileId)) {
            LOGGER.error("Invalid parameter.");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }
        try {
            NStorageProfile storageProfile = storageProfileService.getStorageProfileByProfileId(profileId);
            if (null == storageProfile || StringUtils.isEmpty(storageProfile.getProfileId())) {
                LOGGER.info("The storage Profile : " + profileId + " is not exist.");
                response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_PROFILEID_INVALID);
                response.setResultDescription(
                        VASAResponseCode.storageProfileService.STORAGE_PROFILE_PROFILEID_INVALID_DESCRIPTION);
                return response;
            }
            List<NStorageProfile> storageProfiles = new ArrayList<NStorageProfile>();
            storageProfiles.add(storageProfile);
            List<StorageProfileRestBean> storageProfileRestBeans = new ArrayList<StorageProfileRestBean>();
            if (!converStorageProfile(storageProfiles, storageProfileRestBeans, false)) {
                response.setResultCode(VASAResponseCode.common.ERROR);
                response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
                return response;
            }
            response.setStorageProfile(storageProfileRestBeans);
            response.setCount(storageProfileRestBeans.size());
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("query storage profile fail, " + e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        LOGGER.info("End VASA queryStoragOmProfile function");
        return response;
    }

    @GET
    @Path("/queryAllStorageProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryStorageProfileResponse queryAllStorageProfile(@QueryParam("pageIndex") String pageIndex) {
        LOGGER.info("In VASA queryStoragOmProfile function");
        QueryStorageProfileResponse response = new QueryStorageProfileResponse();
        try {
            List<NStorageProfile> storageProfiles = storageProfileService.getAll();
            List<StorageProfileRestBean> storageProfileRestBeans = new ArrayList<StorageProfileRestBean>();
            if (!converStorageProfile(storageProfiles, storageProfileRestBeans, false)) {
                response.setResultCode(VASAResponseCode.common.ERROR);
                response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
                return response;
            }
            response.setStorageProfile(storageProfileRestBeans);
            response.setCount(storageProfileRestBeans.size());
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("query storage profile fail, " + e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        LOGGER.info("End VASA queryStoragOmProfile function");
        return response;
    }

    @POST
    @Path("/createStorageProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CreateStorageProfileResult createStorageProfile(CreateStorageProfileBean storageProfileBean)
            throws StorageFault {
        LOGGER.info("In VASA createStorageProfile function, the request parametes is : " + storageProfileBean);

        CreateStorageProfileResult response = new CreateStorageProfileResult();
        NStorageProfile storageProfile = new NStorageProfile();

        if (null == storageProfileBean.getName() || "".equals(storageProfileBean.getName())) {
            LOGGER.error("storage profile name is null");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        } else if (!storageProfileBean.getName().matches("^[\\u4E00-\\u9FA5\\uf900-\\ufa2da-zA-Z0-9_\\-]*$")) {
            LOGGER.error("storage profile name is not match,the name is " + storageProfileBean.getName());
            response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_NAME_INVALID);
            response.setResultDescription(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_NAME_INVALID_DESCRIPTION);
            return response;
        } else if (storageProfileBean.getName().length() - 254 > 0) {
            LOGGER.error("storage profile name length > 254.the length is " + storageProfileBean.getName().length());
            response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_NAME_TOOLONG);
            response.setResultDescription(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_NAME_TOOLONG_DESCRIPTION);
            return response;
        }

        if (null == storageProfileBean.getStorageContainerId()) {
            LOGGER.error("storage containerId is null");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        if (storageProfileService.getStorageProfileByProfileName(storageProfileBean.getName()) > 0) {
            LOGGER.error("the storage profile name has already exist.");
            response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_NAME_ALREADY_EXIST);
            response.setResultDescription(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_NAME_ALREADY_EXIST_DESCRIPTION);
            return response;
        }

        String currentContainerProfileControlType = getCurrentContainerProfileControlType(storageProfileBean.getStorageContainerId(), null);
        if (null != currentContainerProfileControlType && !currentContainerProfileControlType.equals(storageProfileBean.getControlType())) {
            LOGGER.error("the storage profile type is not the same as current type.");
            response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_TYPR_INVALID);
            response.setResultDescription(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_TYPR_INVALID_DESCRIPTION);
            return response;
        } else if (null != currentContainerProfileControlType && currentContainerProfileControlType.equalsIgnoreCase(NStorageProfile.ControlType.capability_control)) {
            LOGGER.error(VASAResponseCode.storageProfileService.STORAGE_PROFILE_TYPR_CAPABILITY_ONLY_ONE_DESCRIPTION);
            response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_TYPR_CAPABILITY_ONLY_ONE);
            response.setResultDescription(VASAResponseCode.storageProfileService.STORAGE_PROFILE_TYPR_CAPABILITY_ONLY_ONE_DESCRIPTION);
            return response;
        }

        try {
            storageProfile.setProfileId(UUID.randomUUID().toString());
            storageProfile.setProfileName(storageProfileBean.getName());
            storageProfile.setContainerId(storageProfileBean.getStorageContainerId());
            storageProfile.setThinThick(getThinThickValue(storageProfileBean.getStorageContainerId()));
            storageProfile.setDeprecated("NA");

            storageProfile.setGenerationId(1L);
            storageProfile.setOmCreated(true);

            if ("1".equalsIgnoreCase(storageProfileBean.getQosSmartTier())) {
                storageProfile.setIsSmartTier(true);
                storageProfile.setSmartTierValue(storageProfileBean.getQosSmartTier());
            } else if ("0".equals(storageProfileBean.getQosSmartTier())) {
                storageProfile.setIsSmartTier(false);
                storageProfile.setSmartTierValue("NA");
            } else {
                LOGGER.error(
                        "qosSmartTier parameter is invalid, the qosSmartTier =" + storageProfileBean.getQosSmartTier());
                response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSSMARTTIER_INVALID);
                response.setResultDescription(
                        VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSSMARTTIER_INVALID_DESCRIPTION);
                return response;
            }

            if ("1".equalsIgnoreCase(storageProfileBean.getIsStorageMedium())) {
                storageProfile.setIsStorageMedium(true);
                storageProfile.setDiskTypeValue(storageProfileBean.getDiskTypeValue());
                storageProfile.setRaidLevelValue(storageProfileBean.getRaidLevelValue());
            } else {
                storageProfile.setIsStorageMedium(false);
            }
            if (storageProfileBean.getControlType().equals(NStorageProfile.ControlType.level_control)) {
                NProfileLevel profileLevel = createProfileLevel(storageProfileBean);
                storageProfile.setControlType(NStorageProfile.ControlType.level_control);
                storageProfile.setControlTypeId(profileLevel.getProfileLevelId());
            } else if (storageProfileBean.getControlType().equals(NStorageProfile.ControlType.precision_control)) {
                if ("0".equalsIgnoreCase(storageProfileBean.getQosControl())) {
                    storageProfile.setControlType(NStorageProfile.ControlType.precision_control);
                    storageProfile.setControlTypeId(null);
                } else if ("1".equalsIgnoreCase(storageProfileBean.getQosControl())) {
                    NStorageQos storageQos = new NStorageQos();
                    String qosId = UUID.randomUUID().toString();
                    storageProfile.setControlType(NStorageProfile.ControlType.precision_control);
                    storageProfile.setControlTypeId(qosId);
                    storageQos.setId(qosId);
                    storageQos.setName(storageProfileBean.getName());
                    if ("0".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
                        storageQos.setControlType("Read I/O");
                    } else if ("1".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
                        storageQos.setControlType("Write I/O");
                    } else if ("2".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
                        storageQos.setControlType("Read/Write I/Os");
                    } else {
                        response.setResultCode(
                                VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROLTYPE_ERROR);
                        response.setResultDescription(
                                VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROLTYPE_ERROR_DESCRIPTION);
                        return response;
                    }
                    if ("QoSUpperLimitControl".equalsIgnoreCase(storageProfileBean.getQosControlPolicy())) {
                        if (null != storageProfileBean.getQosControlObjectiveBandwidth()
                                && !"".equals(storageProfileBean.getQosControlObjectiveBandwidth())) {
                            storageQos.setBandWidth(Long.valueOf(storageProfileBean.getQosControlObjectiveBandwidth()));
                        }
                        if (null != storageProfileBean.getQosControlObjectiveIOPS()
                                && !"".equals(storageProfileBean.getQosControlObjectiveIOPS())) {
                            storageQos.setIops(Long.valueOf(storageProfileBean.getQosControlObjectiveIOPS()));
                        }
                        storageQos.setControlPolicy("Control upper bound");
                    } else if ("QoSLowerLimitControl".equalsIgnoreCase(storageProfileBean.getQosControlPolicy())) {
                        if (null != storageProfileBean.getQosControlObjectiveBandwidth()
                                && !"".equals(storageProfileBean.getQosControlObjectiveBandwidth())) {
                            storageQos.setBandWidth(Long.valueOf(storageProfileBean.getQosControlObjectiveBandwidth()));
                        }
                        if (null != storageProfileBean.getQosControlObjectiveIOPS()
                                && !"".equals(storageProfileBean.getQosControlObjectiveIOPS())) {
                            storageQos.setIops(Long.valueOf(storageProfileBean.getQosControlObjectiveIOPS()));
                        }
                        if (null != storageProfileBean.getQosControlObjectiveLatency()
                                && !"".equals(storageProfileBean.getQosControlObjectiveLatency())) {
                            storageQos.setLatency(Long.valueOf(storageProfileBean.getQosControlObjectiveLatency()));
                        }
                        storageQos.setControlPolicy("Control lower bound");
                    } else {
                        response.setResultCode(
                                VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSLOWERLIMITCONTROL_INVALID);
                        response.setResultDescription(
                                VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSLOWERLIMITCONTROL_INVALID_DESCRIPTION);
                        return response;
                    }
                    storageQos.setCreatedTime(new Timestamp(System.currentTimeMillis()));
                    storageQos.setDeleted(false);
                    LOGGER.info("insert Qos into DB:" + storageQos);
                    storageQosSerivce.save(storageQos);
                } else {
                    LOGGER.info("The QosControl is invalid, Qos Contorl is " + storageProfileBean.getQosControl());
                    response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROL_INVALID);
                    response.setResultDescription(
                            VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROL_INVALID_DESCRIPTION);
                    return response;
                }
            } else if (storageProfileBean.getControlType().equals(NStorageProfile.ControlType.capability_control)) {
                NStorageCapabilityQos storageCapabilityQos = new NStorageCapabilityQos();
                if ("0".equalsIgnoreCase(storageProfileBean.getQosControl())) {
                    storageProfile.setControlType(NStorageProfile.ControlType.capability_control);
                    storageProfile.setControlTypeId(null);
                } else if ("1".equalsIgnoreCase(storageProfileBean.getQosControl())) {
                    storageProfile.setControlType(NStorageProfile.ControlType.capability_control);
                    String qosId = UUID.randomUUID().toString();
                    storageProfile.setControlTypeId(qosId);
                    storageCapabilityQos.setId(qosId);
                    storageCapabilityQos.setName(storageProfileBean.getName());
                    if ("0".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
                        storageCapabilityQos.setQosControlType("Read I/O");
                    } else if ("1".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
                        storageCapabilityQos.setQosControlType("Write I/O");
                    } else if ("2".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
                        storageCapabilityQos.setQosControlType("Read/Write I/Os");
                    } else {
                        response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROLTYPE_ERROR);
                        response.setResultDescription(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROLTYPE_ERROR_DESCRIPTION);
                        return response;
                    }
                    if ("QoSUpperLimitControl".equalsIgnoreCase(storageProfileBean.getQosControlPolicy())) {
                        if (null != storageProfileBean.getQosControlObjectiveBandwidth() && !"".equals(storageProfileBean.getQosControlObjectiveBandwidth())) {
                            storageCapabilityQos.setBandwidth(Long.valueOf(storageProfileBean.getQosControlObjectiveBandwidth()));
                        }
                        if (null != storageProfileBean.getQosControlObjectiveIOPS() && !"".equals(storageProfileBean.getQosControlObjectiveIOPS())) {
                            storageCapabilityQos.setIops(Long.valueOf(storageProfileBean.getQosControlObjectiveIOPS()));
                        }
                        storageCapabilityQos.setQosControlPolicy("Control upper bound");
                    } else if ("QoSLowerLimitControl".equalsIgnoreCase(storageProfileBean.getQosControlPolicy())) {
                        if (null != storageProfileBean.getQosControlObjectiveBandwidth() && !"".equals(storageProfileBean.getQosControlObjectiveBandwidth())) {
                            storageCapabilityQos.setBandwidth(Long.valueOf(storageProfileBean.getQosControlObjectiveBandwidth()));
                        }
                        if (null != storageProfileBean.getQosControlObjectiveIOPS() && !"".equals(storageProfileBean.getQosControlObjectiveIOPS())) {
                            storageCapabilityQos.setIops(Long.valueOf(storageProfileBean.getQosControlObjectiveIOPS()));
                        }
                        if (null != storageProfileBean.getQosControlObjectiveLatency() && !"".equals(storageProfileBean.getQosControlObjectiveLatency())) {
                            storageCapabilityQos.setLatency(Long.valueOf(storageProfileBean.getQosControlObjectiveLatency()));
                        }
                        storageCapabilityQos.setQosControlPolicy("Control lower bound");
                    } else {
                        response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSLOWERLIMITCONTROL_INVALID);
                        response.setResultDescription(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSLOWERLIMITCONTROL_INVALID_DESCRIPTION);
                        return response;
                    }
                    storageCapabilityQos.setCreatedTime(new Timestamp(System.currentTimeMillis()));
                    storageCapabilityQos.setDeleted(false);
                    LOGGER.info("insert Capability Qos into DB:" + storageCapabilityQos);
                    storageCapabilityQosService.save(storageCapabilityQos);
                } else {
                    LOGGER.info("The QosControl is invalid, Qos Contorl is " + storageProfileBean.getQosControl());
                    response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROL_INVALID);
                    response.setResultDescription(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROL_INVALID_DESCRIPTION);
                    return response;
                }
            } else {
                LOGGER.error("the storage controlType is invalid.");
                response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
                response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
                return response;
            }
            storageProfile.setOmCreated(true);
            storageProfile.setCreatedTime(new Timestamp(System.currentTimeMillis()));
            storageProfile.setDeleted(false);
            storageProfileService.save(storageProfile);

            List<StorageProfileResponseHeaderBean> storageProfileRestBeans = new ArrayList<StorageProfileResponseHeaderBean>();
            StorageProfileResponseHeaderBean storageProfileRestBean = new StorageProfileResponseHeaderBean();
            storageProfileRestBean.setId(storageProfile.getProfileId());
            storageProfileRestBean.setName(storageProfile.getProfileName().substring(4));
            storageProfileRestBean.setStorageContainerId(storageProfile.getContainerId());
            storageProfileRestBeans.add(storageProfileRestBean);

            response.setStorageProfiles(storageProfileRestBeans);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("create storage profile fail.", e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        LOGGER.info("End VASA createStorageProfile function");
        return response;
    }

    private boolean createStorageQos(CreateStorageProfileBean storageProfileBean, NStorageProfile nStorageProfile, CreateStorageProfileResult response) {
        if ("0".equalsIgnoreCase(storageProfileBean.getQosControl())) {
            return true;
        }
        NStorageQos storageQos = new NStorageQos();
        String qosId = UUID.randomUUID().toString();
        nStorageProfile.setControlTypeId(qosId);
        storageQos.setId(qosId);
        storageQos.setName(storageProfileBean.getName());
        if ("0".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
            storageQos.setControlType("Read I/O");
        } else if ("1".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
            storageQos.setControlType("Write I/O");
        } else if ("2".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
            storageQos.setControlType("Read/Write I/Os");
        } else {
            response.setResultCode(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROLTYPE_ERROR);
            response.setResultDescription(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROLTYPE_ERROR_DESCRIPTION);
            return false;
        }
        if ("QoSUpperLimitControl".equalsIgnoreCase(storageProfileBean.getQosControlPolicy())) {
            if (null != storageProfileBean.getQosControlObjectiveBandwidth()
                    && !"".equals(storageProfileBean.getQosControlObjectiveBandwidth())) {
                storageQos.setBandWidth(Long.valueOf(storageProfileBean.getQosControlObjectiveBandwidth()));
            }
            if (null != storageProfileBean.getQosControlObjectiveIOPS()
                    && !"".equals(storageProfileBean.getQosControlObjectiveIOPS())) {
                storageQos.setIops(Long.valueOf(storageProfileBean.getQosControlObjectiveIOPS()));
            }
            storageQos.setControlPolicy("Control upper bound");
        } else if ("QoSLowerLimitControl".equalsIgnoreCase(storageProfileBean.getQosControlPolicy())) {
            if (null != storageProfileBean.getQosControlObjectiveBandwidth()
                    && !"".equals(storageProfileBean.getQosControlObjectiveBandwidth())) {
                storageQos.setBandWidth(Long.valueOf(storageProfileBean.getQosControlObjectiveBandwidth()));
            }
            if (null != storageProfileBean.getQosControlObjectiveIOPS()
                    && !"".equals(storageProfileBean.getQosControlObjectiveIOPS())) {
                storageQos.setIops(Long.valueOf(storageProfileBean.getQosControlObjectiveIOPS()));
            }
            if (null != storageProfileBean.getQosControlObjectiveLatency()
                    && !"".equals(storageProfileBean.getQosControlObjectiveLatency())) {
                storageQos.setLatency(Long.valueOf(storageProfileBean.getQosControlObjectiveLatency()));
            }
            storageQos.setControlPolicy("Control lower bound");
        } else {
            response.setResultCode(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSLOWERLIMITCONTROL_INVALID);
            response.setResultDescription(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSLOWERLIMITCONTROL_INVALID_DESCRIPTION);
            return false;
        }
        storageQos.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        storageQos.setDeleted(false);
        LOGGER.info("insert Qos into DB:" + storageQos);
        storageQosSerivce.save(storageQos);
        return true;
    }


    private NProfileLevel createProfileLevel(CreateStorageProfileBean storageProfileBean) {
        NProfileLevel profileLevel = new NProfileLevel();
        String profileLevelId = UUID.randomUUID().toString();
        profileLevel.setProfileLevelId(profileLevelId);
        profileLevel.setServiceType(storageProfileBean.getQosServiceType());
        profileLevel.setUserLevel(storageProfileBean.getQosUserLevel());
        profileLevel.setCreatedTime(new Date());
        profileLevelService.save(profileLevel);
        return profileLevel;
    }

    @POST
    @Path("/updateStorageProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader updateStorageProfile(CreateStorageProfileBean storageProfileBean) throws StorageFault {
        LOGGER.info("In VASA updateStorageProfile function,the parameters is : storageProfileId=" + storageProfileBean);

        ResponseHeader response = new ResponseHeader();
        //NStorageProfile storageProfile = new NStorageProfile();
        if (null == storageProfileBean.getId() || "".equalsIgnoreCase(storageProfileBean.getId())) {

            LOGGER.error("StorageProfileId is invalid.");
            response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_PROFILEID_INVALID);
            response.setResultDescription(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_PROFILEID_INVALID_DESCRIPTION);
            return response;
        }

        NStorageProfile storageProfile = storageProfileService.getStorageProfileByProfileId(storageProfileBean.getId());
        if (null == storageProfile) {
            LOGGER.error("StorageProfileId is invalid.");
            response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_PROFILEID_INVALID);
            response.setResultDescription(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_PROFILEID_INVALID_DESCRIPTION);
            return response;
        }

        if (null == storageProfileBean.getName() || "".equals(storageProfileBean.getName())) {
            LOGGER.info("storage profile name is null");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        } else if (!storageProfileBean.getName().matches("^[\\u4E00-\\u9FA5\\uf900-\\ufa2da-zA-Z0-9_\\-]*$")) {
            LOGGER.info("storage profile name is not match,the name is " + storageProfileBean.getName());
            response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_NAME_INVALID);
            response.setResultDescription(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_NAME_INVALID_DESCRIPTION);
            return response;
        } else if (storageProfileBean.getName().length() - 254 > 0) {
            LOGGER.info("storage profile name length > 254.the length is " + storageProfileBean.getName().length());
            response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_NAME_TOOLONG);
            response.setResultDescription(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_NAME_TOOLONG_DESCRIPTION);
            return response;
        } // else if()//添加参数校验

        if (null == storageProfileBean.getStorageContainerId()) {
            LOGGER.error("storage containerId is null");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        String currentContainerProfileControlType = getCurrentContainerProfileControlType(storageProfileBean.getStorageContainerId(), storageProfileBean.getId());
        if (null != currentContainerProfileControlType && !currentContainerProfileControlType.equals(storageProfileBean.getControlType())) {

            LOGGER.error("the storage profile type is not the same as current type.");
            response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_TYPR_INVALID);
            response.setResultDescription(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_TYPR_INVALID_DESCRIPTION);
            return response;
        } else if (null != currentContainerProfileControlType && currentContainerProfileControlType.equalsIgnoreCase(NStorageProfile.ControlType.capability_control)) {
            LOGGER.error(VASAResponseCode.storageProfileService.STORAGE_PROFILE_TYPR_CAPABILITY_ONLY_ONE_DESCRIPTION);
            response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_TYPR_CAPABILITY_ONLY_ONE);
            response.setResultDescription(VASAResponseCode.storageProfileService.STORAGE_PROFILE_TYPR_CAPABILITY_ONLY_ONE_DESCRIPTION);
            return response;
        }

        String old_control_type = storageProfile.getControlType();
        String old_control_id = storageProfile.getControlTypeId();
        try {
            storageProfile.setProfileName(storageProfileBean.getName());
            storageProfile.setContainerId(storageProfileBean.getStorageContainerId());
            storageProfile.setThinThick(getThinThickValue(storageProfileBean.getStorageContainerId()));

            if ("1".equalsIgnoreCase(storageProfileBean.getQosSmartTier())) {
                storageProfile.setIsSmartTier(true);
                storageProfile.setSmartTierValue(storageProfileBean.getQosSmartTier());
            } else if ("0".equals(storageProfileBean.getQosSmartTier())) {
                storageProfile.setIsSmartTier(false);
                storageProfile.setSmartTierValue("NA");
            } else {
                response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSSMARTTIER_INVALID);
                response.setResultDescription(
                        VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSSMARTTIER_INVALID_DESCRIPTION);
                return response;
            }
            if ("1".equalsIgnoreCase(storageProfileBean.getIsStorageMedium())) {
                storageProfile.setIsStorageMedium(true);
                storageProfile.setDiskTypeValue(storageProfileBean.getDiskTypeValue());
                storageProfile.setRaidLevelValue(storageProfileBean.getRaidLevelValue());
            } else {
                storageProfile.setIsStorageMedium(false);
            }
			
			/*if(storageProfile.getControlType().equals(NStorageProfile.ControlType.precision_control) && !StringUtils.isEmpty(storageProfile.getControlTypeId())){
				oldStorageQos = storageQosSerivce.getStorageQosByQosId(storageProfile.getControlTypeId());
			}
			if(storageProfile.getControlType().equals(NStorageProfile.ControlType.level_control)) {
				old_profile_level = storageProfile.getControlTypeId();
			}*/

            if (storageProfileBean.getControlType().equals(NStorageProfile.ControlType.level_control)) {
                NProfileLevel profileLevel = new NProfileLevel();
                String profileLevelId = UUID.randomUUID().toString();
                profileLevel.setProfileLevelId(profileLevelId);
                profileLevel.setServiceType(storageProfileBean.getQosServiceType());
                profileLevel.setUserLevel(storageProfileBean.getQosUserLevel());
                profileLevel.setCreatedTime(new Date());
                profileLevelService.save(profileLevel);
                storageProfile.setControlType(NStorageProfile.ControlType.level_control);
                storageProfile.setControlTypeId(profileLevelId);
            }
            if (storageProfileBean.getControlType().equals(NStorageProfile.ControlType.precision_control)) {
                NStorageQos newStorageQos = new NStorageQos();
                if ("0".equalsIgnoreCase(storageProfileBean.getQosControl())) {
                    storageProfile.setControlType(NStorageProfile.ControlType.precision_control);
                    storageProfile.setControlTypeId(null);
                } else if ("1".equalsIgnoreCase(storageProfileBean.getQosControl())) {
                    storageProfile.setControlType(NStorageProfile.ControlType.precision_control);
                    String qosId = UUID.randomUUID().toString();
                    storageProfile.setControlTypeId(qosId);
                    newStorageQos.setId(qosId);
                    newStorageQos.setName(storageProfileBean.getName());
                    if ("0".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
                        newStorageQos.setControlType("Read I/O");
                    } else if ("1".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
                        newStorageQos.setControlType("Write I/O");
                    } else if ("2".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
                        newStorageQos.setControlType("Read/Write I/Os");
                    } else {
                        response.setResultCode(
                                VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROLTYPE_ERROR);
                        response.setResultDescription(
                                VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROLTYPE_ERROR_DESCRIPTION);
                        return response;
                    }
                    if ("QoSUpperLimitControl".equalsIgnoreCase(storageProfileBean.getQosControlPolicy())) {
                        if (null != storageProfileBean.getQosControlObjectiveBandwidth()
                                && !"".equalsIgnoreCase(storageProfileBean.getQosControlObjectiveBandwidth())) {
                            newStorageQos
                                    .setBandWidth(Long.valueOf(storageProfileBean.getQosControlObjectiveBandwidth()));
                        }
                        if (null != storageProfileBean.getQosControlObjectiveIOPS()
                                && !"".equalsIgnoreCase(storageProfileBean.getQosControlObjectiveIOPS())) {
                            newStorageQos.setIops(Long.valueOf(storageProfileBean.getQosControlObjectiveIOPS()));
                        }
                        newStorageQos.setControlPolicy("Control upper bound");
                    } else if ("QoSLowerLimitControl".equalsIgnoreCase(storageProfileBean.getQosControlPolicy())) {
                        if (null != storageProfileBean.getQosControlObjectiveBandwidth()
                                && !"".equalsIgnoreCase(storageProfileBean.getQosControlObjectiveBandwidth())) {
                            newStorageQos
                                    .setBandWidth(Long.valueOf(storageProfileBean.getQosControlObjectiveBandwidth()));
                        }
                        if (null != storageProfileBean.getQosControlObjectiveIOPS()
                                && !"".equalsIgnoreCase(storageProfileBean.getQosControlObjectiveIOPS())) {
                            newStorageQos.setIops(Long.valueOf(storageProfileBean.getQosControlObjectiveIOPS()));
                        }
                        if (null != storageProfileBean.getQosControlObjectiveLatency()
                                && !"".equalsIgnoreCase(storageProfileBean.getQosControlObjectiveLatency())) {
                            newStorageQos.setLatency(Long.valueOf(storageProfileBean.getQosControlObjectiveLatency()));
                        }
                        newStorageQos.setControlPolicy("Control lower bound");
                    } else {
                        response.setResultCode(
                                VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSLOWERLIMITCONTROL_INVALID);
                        response.setResultDescription(
                                VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSLOWERLIMITCONTROL_INVALID_DESCRIPTION);
                        return response;
                    }
                    newStorageQos.setCreatedTime(new Timestamp(System.currentTimeMillis()));
                    newStorageQos.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
                    newStorageQos.setDeleted(false);
                    LOGGER.info("Insert new StorageQos into DB : " + newStorageQos.toString());
                    storageQosSerivce.save(newStorageQos);
                } else {
                    LOGGER.info("The QosControl is invalid, Qos Contorl is " + storageProfileBean.getQosControl());
                    response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROL_INVALID);
                    response.setResultDescription(
                            VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROL_INVALID_DESCRIPTION);
                    return response;
                }
            }

            //Capability control
            if (storageProfileBean.getControlType().equals(NStorageProfile.ControlType.capability_control)) {
                NStorageCapabilityQos newStorageCapabilityQos = new NStorageCapabilityQos();
                if ("0".equalsIgnoreCase(storageProfileBean.getQosControl())) {
                    storageProfile.setControlType(NStorageProfile.ControlType.capability_control);
                    storageProfile.setControlTypeId(null);
                } else if ("1".equalsIgnoreCase(storageProfileBean.getQosControl())) {
                    storageProfile.setControlType(NStorageProfile.ControlType.capability_control);
                    String qosId = UUID.randomUUID().toString();
                    storageProfile.setControlTypeId(qosId);
                    newStorageCapabilityQos.setId(qosId);
                    newStorageCapabilityQos.setName(storageProfileBean.getName());
                    if ("0".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
                        newStorageCapabilityQos.setQosControlType("Read I/O");
                    } else if ("1".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
                        newStorageCapabilityQos.setQosControlType("Write I/O");
                    } else if ("2".equalsIgnoreCase(storageProfileBean.getQosControlType())) {
                        newStorageCapabilityQos.setQosControlType("Read/Write I/Os");
                    } else {
                        response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROLTYPE_ERROR);
                        response.setResultDescription(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROLTYPE_ERROR_DESCRIPTION);
                        return response;
                    }
                    if ("QoSUpperLimitControl".equalsIgnoreCase(storageProfileBean.getQosControlPolicy())) {
                        if (null != storageProfileBean.getQosControlObjectiveBandwidth() && !"".equalsIgnoreCase(storageProfileBean.getQosControlObjectiveBandwidth())) {
                            newStorageCapabilityQos.setBandwidth(Long.valueOf(storageProfileBean.getQosControlObjectiveBandwidth()));
                        }
                        if (null != storageProfileBean.getQosControlObjectiveIOPS() && !"".equalsIgnoreCase(storageProfileBean.getQosControlObjectiveIOPS())) {
                            newStorageCapabilityQos.setIops(Long.valueOf(storageProfileBean.getQosControlObjectiveIOPS()));
                        }
                        newStorageCapabilityQos.setQosControlPolicy("Control upper bound");
                    } else if ("QoSLowerLimitControl".equalsIgnoreCase(storageProfileBean.getQosControlPolicy())) {
                        if (null != storageProfileBean.getQosControlObjectiveBandwidth() && !"".equalsIgnoreCase(storageProfileBean.getQosControlObjectiveBandwidth())) {
                            newStorageCapabilityQos.setBandwidth(Long.valueOf(storageProfileBean.getQosControlObjectiveBandwidth()));
                        }
                        if (null != storageProfileBean.getQosControlObjectiveIOPS() && !"".equalsIgnoreCase(storageProfileBean.getQosControlObjectiveIOPS())) {
                            newStorageCapabilityQos.setIops(Long.valueOf(storageProfileBean.getQosControlObjectiveIOPS()));
                        }
                        if (null != storageProfileBean.getQosControlObjectiveLatency() && !"".equalsIgnoreCase(storageProfileBean.getQosControlObjectiveLatency())) {
                            newStorageCapabilityQos.setLatency(Long.valueOf(storageProfileBean.getQosControlObjectiveLatency()));
                        }
                        newStorageCapabilityQos.setQosControlPolicy("Control lower bound");
                    } else {
                        response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSLOWERLIMITCONTROL_INVALID);
                        response.setResultDescription(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSLOWERLIMITCONTROL_INVALID_DESCRIPTION);
                        return response;
                    }
                    newStorageCapabilityQos.setCreatedTime(new Timestamp(System.currentTimeMillis()));
                    newStorageCapabilityQos.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
                    newStorageCapabilityQos.setDeleted(false);
                    LOGGER.info("Insert new StorageQos into DB : " + newStorageCapabilityQos.toString());
                    storageCapabilityQosService.save(newStorageCapabilityQos);
                } else {
                    LOGGER.info("The QosControl is invalid, Qos Contorl is " + storageProfileBean.getQosControl());
                    response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROL_INVALID);
                    response.setResultDescription(VASAResponseCode.storageProfileService.STORAGE_PROFILE_QOSCONTROL_INVALID_DESCRIPTION);
                    return response;
                }
            }


            storageProfile.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
            storageProfileService.updateStorageProfileByProfileId(storageProfile);
            LOGGER.info("Update storage profile , the data is : " + storageProfile.toString());
            if (!StringUtils.isEmpty(old_control_id)) {
                if (old_control_type.equalsIgnoreCase(NStorageProfile.ControlType.precision_control)) {
                    storageQosSerivce.deleteStorageQosById(old_control_id);
                }
                if (old_control_type.equalsIgnoreCase(NStorageProfile.ControlType.level_control)) {
                    profileLevelService.deleteById(old_control_id);
                }
                if (old_control_type.equalsIgnoreCase(NStorageProfile.ControlType.capability_control)) {
                    storageCapabilityQosService.deleteStorageCapabilityQosById(old_control_id);
                }
            }
            // storageProfileService.updateData(storageProfile);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("update storage profile service fail.", e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        LOGGER.info("End updateStorageProfile function.");
        return response;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader deleteStorageProfile(@QueryParam("profileId") String profileId) {
        LOGGER.info("In deleteStorageProfile function,the parameter is : profileId=" + profileId);
        ResponseHeader response = new ResponseHeader();
        if (StringUtils.isEmpty(profileId)) {
            response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_PROFILEID_INVALID);
            response.setResultDescription(
                    VASAResponseCode.storageProfileService.STORAGE_PROFILE_PROFILEID_INVALID_DESCRIPTION);
            return response;
        }
        try {
            NStorageProfile storageProfile = storageProfileService.getStorageProfileByProfileId(profileId);
            if (null == storageProfile || StringUtils.isEmpty(storageProfile.getProfileId())) {
                LOGGER.info("The storage Profile : " + profileId + " is not exist.");
                response.setResultCode(VASAResponseCode.storageProfileService.STORAGE_PROFILE_PROFILEID_INVALID);
                response.setResultDescription(
                        VASAResponseCode.storageProfileService.STORAGE_PROFILE_PROFILEID_INVALID_DESCRIPTION);
                return response;
            }

            if (null != storageProfile.getDeleted() && storageProfile.getDeleted() == true) {
                LOGGER.info("The storage Profile : " + profileId + "hava already delete..");
                response.setResultCode(VASAResponseCode.common.SUCCESS);
                response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
                return response;
            }
            if (!StringUtils.isEmpty(storageProfile.getControlTypeId())) {
                if (storageProfile.getControlType().equals(NStorageProfile.ControlType.precision_control)) {
                    storageQosSerivce.deleteStorageQosById(storageProfile.getControlTypeId());
                } else if (storageProfile.getControlType().equals(NStorageProfile.ControlType.level_control)) {
                    profileLevelService.deleteById(storageProfile.getControlTypeId());
                } else if (storageProfile.getControlType().equals(NStorageProfile.ControlType.capability_control)) {
                    storageCapabilityQosService.deleteStorageCapabilityQosById(storageProfile.getControlTypeId());
                }
            }
            storageProfile.setDeleted(true);
            storageProfile.setDeletedTime(new Timestamp(System.currentTimeMillis()));
            storageProfileService.deleteStorageProfileByProfileId(storageProfile);
            // storageProfileService.delete(storageProfile);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.info("delete storage profile fail : " + e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        LOGGER.info("End deleteStorageProfile function.");
        return response;
    }

    private String getCurrentContainerProfileControlType(String containerId, String profileId) throws StorageFault {
        List<StorageProfileData> storageProfilesByContainerId = storageProfileService.getStorageProfileByContainerId(containerId);
        if (null != storageProfilesByContainerId && storageProfilesByContainerId.size() != 0) {
            for (StorageProfileData storageProfileData : storageProfilesByContainerId) {
                if (null != profileId) {
                    if (storageProfileData.getProfileId().equals(profileId)) {
                        continue;
                    }
                }
                return storageProfileData.getControlType();
            }
        }
        return null;
    }

    private String getThinThickValue(String containerId) throws StorageFault {
        String thinThickValue = "Thin/Thick";
        List<NStoragePool> poolListByContainerId = poolService.getPoolListByContainerId(containerId);
        if (poolListByContainerId.size() > 0) {
            StorageInfo queryInfoByArrayId = storageManagerService.queryInfoByArrayId(poolListByContainerId.get(0).getArrayId());
            String thinThick = DeviceTypeMapper.thinThickSupport(queryInfoByArrayId.getModel());
            if (!StringUtils.isEmpty(thinThick)) {
                thinThickValue = thinThick;
            }
        }
        return thinThickValue;
    }

}
