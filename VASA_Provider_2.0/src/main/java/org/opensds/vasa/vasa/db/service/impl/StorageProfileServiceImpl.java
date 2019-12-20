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

package org.opensds.vasa.vasa.db.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.service.model.StorageProfileData;
import org.opensds.vasa.vasa.util.FaultUtil;

import org.opensds.vasa.vasa.db.dao.BaseDao;
import org.opensds.vasa.vasa.db.dao.StorageProfileDao;
import org.opensds.vasa.vasa.db.model.NProfileLevel;
import org.opensds.vasa.vasa.db.model.NStorageCapabilityQos;
import org.opensds.vasa.vasa.db.model.NStorageProfile;
import org.opensds.vasa.vasa.db.model.NStorageQos;
import org.opensds.vasa.vasa.db.model.NVvolProfile;
import org.opensds.vasa.vasa.db.service.ProfileLevelService;
import org.opensds.vasa.vasa.db.service.StorageCapabilityQosService;
import org.opensds.vasa.vasa.db.service.StorageProfileLevelService;
import org.opensds.vasa.vasa.db.service.StorageProfileService;
import org.opensds.vasa.vasa.db.service.StorageQosService;
import org.opensds.vasa.vasa.db.service.VvolProfileService;

import com.vmware.vim.vasa.v20.StorageFault;

public class StorageProfileServiceImpl extends BaseServiceImpl<NStorageProfile>
        implements StorageProfileService {

    public StorageProfileServiceImpl(BaseDao<NStorageProfile> baseDao) {
        super(baseDao);
        // TODO Auto-generated constructor stub
    }

    private static Logger LOGGER = LogManager.getLogger(StorageProfileServiceImpl.class);

    private StorageQosService storageQosService;
    private VvolProfileService vvolProfileService;
    private StorageProfileDao storageProfileDao;
    private ProfileLevelService profileLevelService;
    private StorageProfileLevelService storageProfileLevelService;
    private StorageCapabilityQosService storageCapabilityQosService;

	/*@Override
	public StorageProfileData getProfileByProfileId(String profileId,
			String containerId, String thinThick, long generationId,
			String deprecated) throws StorageFault {
		try {
			StorageProfileData result = new StorageProfileData();
			NStorageProfile nStorageProfile = new NStorageProfile();
			nStorageProfile.setProfileId(profileId);
			nStorageProfile.setContainerId(containerId);
			nStorageProfile.setThinThick(thinThick);
			nStorageProfile.setGenerationId(generationId);
			nStorageProfile.setDeprecated(deprecated);
			nStorageProfile.setDeleted(false);
			
			LOGGER.debug("getProfileByProfileId , nStorageProfile = "+ nStorageProfile);
			
			NStorageProfile storageProfileResult = getDataByKey(nStorageProfile);
			if (null != storageProfileResult) {
				NStorageQos search = new NStorageQos();
				NProfileLevel profileLevel = new NProfileLevel();
				if(null != storageProfileResult.getSmartQosId()){
					NStorageQos nStorageQos = new NStorageQos();
					nStorageQos.setId(storageProfileResult.getSmartQosId());
					search = storageQosService.getDataByKey(nStorageQos);
				}
				if(storageProfileResult.getControlType().equals(NStorageProfile.ControlType.level_control)) {
					profileLevel = profileLevelService.getById(storageProfileResult.getControlTypeId());
				}
				StorageProfileData.combineProfileData(storageProfileResult, search, profileLevel, result);
				return result;
			} else {
				return null;
			}
		} catch (Exception e) {
			LOGGER.error("getProfileByProfileId error. profileId:" + profileId);
			throw FaultUtil.storageFault("getProfileByProfileId error");
		}
	}*/

    @Override
    public List<StorageProfileData> getStorageProfileByContainerId(
            String containerId) throws StorageFault {
        // TODO Auto-generated method stub
        try {
            List<StorageProfileData> result = new ArrayList<>();
            List<NStorageProfile> nStorageProfiles = queryOmCreateStorageProfileByContainerId(containerId);
            LOGGER.debug("searchProfiles containerId=" + containerId + ",nStorageProfiles=" + nStorageProfiles);
            if (null != nStorageProfiles && nStorageProfiles.size() != 0) {
                for (NStorageProfile nStorageProfile : nStorageProfiles) {
                    NStorageQos nStorageQos = null;
                    NProfileLevel profileLevel = null;
                    NStorageCapabilityQos storageCapabilityQos = null;
                    if (nStorageProfile.getControlType().equals(NStorageProfile.ControlType.precision_control) && !StringUtils.isEmpty(nStorageProfile.getControlTypeId())) {
                        NStorageQos storageQos = new NStorageQos();
                        storageQos.setId(nStorageProfile.getControlTypeId());
                        nStorageQos = storageQosService.getDataByKey(storageQos);
                    }
                    if (nStorageProfile.getControlType().equals(NStorageProfile.ControlType.level_control)) {
                        profileLevel = profileLevelService.getById(nStorageProfile.getControlTypeId());
                    }
                    if (nStorageProfile.getControlType().equals(NStorageProfile.ControlType.capability_control) && !StringUtils.isEmpty(nStorageProfile.getControlTypeId())) {
                        storageCapabilityQos = storageCapabilityQosService.getStorageCapabilityQosById(nStorageProfile.getControlTypeId());
                    }
                    LOGGER.debug("getStorageProfileByContainerId,containerId=" + containerId + ",nStorageQos=" + nStorageQos + ",profileLevel" + profileLevel);
                    StorageProfileData profileData = new StorageProfileData();
                    result.add(profileData);
                    StorageProfileData.combineProfileData(nStorageProfile,
                            nStorageQos, profileLevel, storageCapabilityQos, profileData);
                }
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("getStorageProfileByContainerId error. containerId:"
                    + containerId, e);
            throw FaultUtil
                    .storageFault("getStorageProfileByContainerId error");
        }

    }

    @Override
    public NStorageProfile getCurrentProfileByVvolid(String vvolId) throws StorageFault {
        try {
            List<NVvolProfile> vvolProfileByVvolId = vvolProfileService.getVvolProfileByVvolId(vvolId);
            if (null == vvolProfileByVvolId || vvolProfileByVvolId.size() == 0) {
                return null;
            } else {
                NVvolProfile nVvolProfile = vvolProfileByVvolId.get(0);
                NStorageProfile storageProfile = new NStorageProfile();
                storageProfile.setProfileId(nVvolProfile.getProfileId());
                return getDataByKey(storageProfile);
            }
        } catch (Exception e) {
            LOGGER.error("getProfileByVvolId error. vvolId:"
                    + vvolId, e);
            throw FaultUtil
                    .storageFault("getProfileByVvolId error");
        }

    }


    @Override
    public String getCurrentProfileRawQosIdByVvolId(String vvolId)
            throws StorageFault {
        // TODO Auto-generated method stub
        try {
            List<NVvolProfile> vvolProfileByVvolId = vvolProfileService.getVvolProfileByVvolId(vvolId);
            if (null == vvolProfileByVvolId || vvolProfileByVvolId.size() == 0) {
                return null;
            } else {
                NVvolProfile nVvolProfile = vvolProfileByVvolId.get(0);
                NStorageProfile storageProfile = new NStorageProfile();
                storageProfile.setProfileId(nVvolProfile.getProfileId());
                NStorageProfile profile = getDataByKey(storageProfile);
                if (null != profile) {
                    NStorageQos search = new NStorageQos();
                    if (null != profile.getSmartQosId()) {
                        NStorageQos nStorageQos = new NStorageQos();
                        nStorageQos.setId(profile.getSmartQosId());
                        search = storageQosService.getDataByKey(nStorageQos);
                        return search.getRawQosId();
                    }
                }
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("getProfileByVvolId error. vvolId:"
                    + vvolId, e);
            throw FaultUtil
                    .storageFault("getProfileByVvolId error");
        }
    }

    @Override
    public NStorageProfile getStorageProfileByProfileId(String profileId) throws StorageFault {
        try {
            return storageProfileDao.getStorageProfileByProfileId(profileId);
        } catch (Exception e) {
            LOGGER.error("GetStorageProfileByProfileId error," + e, e);
            throw FaultUtil.storageFault("GetStorageProfileByProfileId error.");
        }
    }

    @Override
    public int getStorageProfileByProfileName(String profileName) throws StorageFault {
        // TODO Auto-generated method stub
        try {
            return storageProfileDao.getStorageProfileByProfileName(profileName);
        } catch (Exception e) {
            LOGGER.error("GetStorageProfileByProfileId error. vvolid : " + e, e);
            throw FaultUtil.storageFault("GetStorageProfileByProfileId error.");
        }
    }

    @Override
    public void updateStorageProfileByProfileId(NStorageProfile storageProfile) throws StorageFault {
        try {
            LOGGER.info("updateStorageProfileByProfileId");
            storageProfileDao.updateStorageProfileByProfileId(storageProfile);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("updateStorageProfileByProfileId error. vvolid : " + e, e);
            throw FaultUtil.storageFault("updateStorageProfileByProfileId error.");
        }
    }

    public void setVvolProfileService(VvolProfileService vvolProfileService) {
        this.vvolProfileService = vvolProfileService;
    }

    public VvolProfileService getVvolProfileService() {
        return vvolProfileService;
    }


    public StorageProfileDao getStorageProfileDao() {
        return storageProfileDao;
    }

    public void setStorageProfileDao(StorageProfileDao storageProfileDao) {
        this.storageProfileDao = storageProfileDao;
    }

    public StorageQosService getStorageQosService() {
        return storageQosService;
    }

    public void setStorageQosService(StorageQosService storageQosService) {
        this.storageQosService = storageQosService;
    }

    @Override
    public void deleteStorageProfileByProfileId(NStorageProfile storageProfile) throws StorageFault {
        // TODO Auto-generated method stub
        try {
            LOGGER.info("In delete StorageProfile By ProfileId function.");
            storageProfileDao.deleteStorageProfileByProfileId(storageProfile);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("deleteStorageProfileByProfileId error. vvolid : " + e, e);
            throw FaultUtil.storageFault("deleteStorageProfileByProfileId error.");
        }
    }

    @Override
    public List<NStorageProfile> queryOmCreateStorageProfileByContainerId(
            String containerId) {
        // TODO Auto-generated method stub
        return storageProfileDao.queryOmCreateStorageProfileByContainerId(containerId);
    }

    public ProfileLevelService getProfileLevelService() {
        return profileLevelService;
    }

    public void setProfileLevelService(ProfileLevelService profileLevelService) {
        this.profileLevelService = profileLevelService;
    }

    public StorageProfileLevelService getStorageProfileLevelService() {
        return storageProfileLevelService;
    }

    public void setStorageProfileLevelService(StorageProfileLevelService storageProfileLevelService) {
        this.storageProfileLevelService = storageProfileLevelService;
    }

    @Override
    public List<NStorageProfile> queryOmCreateStorageProfileByPage(String pageIndex, String pageSize) throws StorageFault {
        try {
            LOGGER.info("In queryOmCreateStorageProfileByPage function,the pageIndex = " + pageIndex + " pageSize " + pageSize);
            int offSet = Integer.valueOf(pageSize) * (Integer.valueOf(pageIndex) - 1);
            Map<String, String> map = new HashMap<String, String>();
            map.put("offSet", String.valueOf(offSet));
            map.put("pageSize", pageSize);
            return storageProfileDao.queryOmCreateStorageProfileByPage(map);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("deleteStorageContainerByContainerId error. vvolid : ", e);
            throw FaultUtil.storageFault("queryOmCreateStorageProfileByPage error.");
        }
    }

    @Override
    public Long getStorageProfileCount() throws StorageFault {
        try {
            LOGGER.info("In getStorageProfileCount function");

            return storageProfileDao.getStorageProfileCount();

        } catch (Exception e) {
            LOGGER.error("getStorageProfileCount error. vvolid : ", e);
            throw FaultUtil.storageFault("queryOmCreateStorageProfileByPage error.");
        }
    }

    public StorageCapabilityQosService getStorageCapabilityQosService() {
        return storageCapabilityQosService;
    }

    public void setStorageCapabilityQosService(StorageCapabilityQosService storageCapabilityQosService) {
        this.storageCapabilityQosService = storageCapabilityQosService;
    }

}
