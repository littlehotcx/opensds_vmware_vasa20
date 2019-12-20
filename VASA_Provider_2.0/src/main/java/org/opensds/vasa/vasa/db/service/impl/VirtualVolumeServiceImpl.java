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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.domain.model.VVolModel;
import org.opensds.vasa.domain.model.bean.S2DVvolBind;
import org.opensds.vasa.vasa.rest.bean.VvolOwnController;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.springframework.transaction.annotation.Transactional;

import org.opensds.vasa.base.common.VasaSrcTypeConstant;

import org.opensds.platform.common.SDKResult;
import org.opensds.vasa.vasa.db.dao.VirtualVolumeDao;
import org.opensds.vasa.vasa.db.dao.VvolMetadataDao;
import org.opensds.vasa.vasa.db.dao.VvolProfileDao;
import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.db.model.NVvolMetadata;
import org.opensds.vasa.vasa.db.model.NVvolProfile;
import org.opensds.vasa.vasa.db.service.VirtualVolumeService;

import com.vmware.vim.vasa.v20.StorageFault;

public class VirtualVolumeServiceImpl implements VirtualVolumeService {
    private static Logger LOGGER = LogManager.getLogger(VirtualVolumeServiceImpl.class);

    private VirtualVolumeDao virtualVolumeDao;

    private VvolProfileDao vvolProfileDao;

    private VvolMetadataDao vvolMetadataDao;

    public VirtualVolumeDao getVirtualVolumeDao() {
        return virtualVolumeDao;
    }

    public void setVirtualVolumeDao(VirtualVolumeDao virtualVolumeDao) {
        this.virtualVolumeDao = virtualVolumeDao;
    }

    public VvolProfileDao getVvolProfileDao() {
        return vvolProfileDao;
    }

    public void setVvolProfileDao(VvolProfileDao vvolProfileDao) {
        this.vvolProfileDao = vvolProfileDao;
    }

    public VvolMetadataDao getVvolMetadataDao() {
        return vvolMetadataDao;
    }

    public void setVvolMetadataDao(VvolMetadataDao vvolMetadataDao) {
        this.vvolMetadataDao = vvolMetadataDao;
    }

    @Override
    public NVirtualVolume getVirtualVolumeByVvolId(String vvolid) throws StorageFault {
        try {
            return virtualVolumeDao.getVirtualVolumeByVvolId(vvolid);
        } catch (Exception e) {
            LOGGER.error("getVirtualVolumeByVvolId error. vvolid:" + vvolid + " Exception : ", e);
            throw FaultUtil.storageFault("getVirtualVolumeByVvolId error");
        }
    }

    @Override
    public long getVMAllDataSizeByVvolID(String vvolid) throws StorageFault {

        try {
            return virtualVolumeDao.getVMAllDataSizeByVvolId(vvolid);
        } catch (Exception e) {
            LOGGER.error("getVMAllDataSize error. vvolid:" + vvolid + " Exception : ", e);
            throw FaultUtil.storageFault("getVMAllDataSize error");
        }

    }

    @Override
    public long getVMAllDataSizeByVmId(String vmId) throws StorageFault {

        try {
            return virtualVolumeDao.getVMAllDataSizeByVmId(vmId);
        } catch (Exception e) {
            LOGGER.error("getVMAllDataSizeByVmID error. vvolid:" + vmId + " Exception : ", e);
            throw FaultUtil.storageFault("getVMAllDataSizeByVmId error");
        }

    }

    @Override
    public NVirtualVolume getVirtualVolumeByVvolIdNotIncludeDeleting(String vvolid) throws StorageFault {
        try {
            NVirtualVolume vvol = virtualVolumeDao.getVirtualVolumeByVvolId(vvolid);
            if (vvol == null) {
                return null;
            }
            if (vvol.getStatus().equalsIgnoreCase("deleting") || vvol.getStatus().equalsIgnoreCase("error_deleting")) {
                return null;
            }
            return vvol;
        } catch (Exception e) {
            LOGGER.error("getVirtualVolumeByVvolId error. vvolid:" + vvolid + " Exception : ", e);
            throw FaultUtil.storageFault("getVirtualVolumeByVvolId error");
        }
    }

    @Override
    public List<NVirtualVolume> getVirtualVolumeByParentId(String parentid) throws StorageFault {
        // TODO Auto-generated method stub
        try {
            return virtualVolumeDao.getVirtualVolumeByParentId(parentid);
        } catch (Exception e) {
            LOGGER.error("getVirtualVolumeByVvolId error. vvolid:" + parentid + " , Exception ", e);
            throw FaultUtil.storageFault("getVirtualVolumeByVvolId error");
        }
    }

    @Override
    public List<String> getVvolIdByArrayIdAndRawId(String arrayId, String rawId) throws StorageFault {
        try {
            Map<String, Object> constraints = new HashMap<String, Object>();
            constraints.put("arrayId", arrayId);
            constraints.put("rawId", rawId);
            List<String> availableStatuses = new ArrayList<String>();
            availableStatuses.add("available");
            availableStatuses.add("active");
            constraints.put("status", availableStatuses);
            return virtualVolumeDao.getVvolIdByArrayIdAndRawId(constraints);
        } catch (Exception e) {
            // TODO 后续删除异常信息e
            LOGGER.error("getVvolIdByArrayIdAndRawId error. arrayId:" + arrayId + ", rawId:" + rawId + " Exception ",
                    e);
            throw FaultUtil.storageFault("getVvolIdByArrayIdAndRawId error.");
        }
    }

    @Override
    public List<NVirtualVolume> getAllInactiveSnapshots() throws StorageFault {
        try {
            return virtualVolumeDao.getAllInactiveSnapshots();
        } catch (Exception e) {
            LOGGER.error("getAllInactiveSnapshots error.");
            throw FaultUtil.storageFault("getAllInactiveSnapshots error.");
        }
    }

    @Override
    public int getDependenciesCountByVvolId(String vvolid) throws StorageFault {
        try {
            Map<String, Object> dependencies = new HashMap<String, Object>();
            dependencies.put("vvolid", vvolid);
            List<String> sourceTypeList = new ArrayList<String>();
            sourceTypeList.add(VasaSrcTypeConstant.SNAPSHOT);
            sourceTypeList.add(VasaSrcTypeConstant.FAST_CLONE);
            dependencies.put("sourceType", sourceTypeList);
            return virtualVolumeDao.getDependenciesCountByVvolId(dependencies);
        } catch (Exception e) {
            LOGGER.error("getDependenciesCountByVvolId error. vvolid is: " + vvolid + " Exception ", e);
            throw FaultUtil.storageFault("getDependenciesCountByVvolId error.vvolid is: " + vvolid);
        }
    }

    @Override
    public void addVirtualVolume(NVirtualVolume vvol) throws StorageFault {
        try {
            virtualVolumeDao.addVirtualVolume(vvol);
        } catch (Exception e) {
            LOGGER.error("addVirtualVolume error. vvol:" + vvol);
            throw FaultUtil.storageFault("addVirtualVolume error.");
        }

    }

    @Override
    public void updateStatusByVvolId(String vvolid, String status) throws StorageFault {
        try {
            NVirtualVolume vvol = new NVirtualVolume();
            vvol.setVvolid(vvolid);
            vvol.setStatus(status);
            virtualVolumeDao.updateStatusByVvolId(vvol);
        } catch (Exception e) {
            LOGGER.error("updateStatusByVvolId error. vvolid:" + vvolid + ", status:" + status);
            throw FaultUtil.storageFault("updateStatusByVvolId error.");
        }
    }

    @Override
    public void updateParentIdByVvolId(String vvolId, String parentId) throws StorageFault {
        try {
            NVirtualVolume vvol = new NVirtualVolume();
            vvol.setVvolid(vvolId);
            vvol.setParentId(parentId);
            virtualVolumeDao.updateParentIdByVvolId(vvol);
        } catch (Exception e) {
            LOGGER.error("updateParentIdByVvolId error. vvolid:" + vvolId + ", parentId:" + parentId);
            throw FaultUtil.storageFault("updateStatusByVvolId error.");
        }
    }

    @Override
    public void updateArrayIdAndRawIdByVvolId(String vvolid, String arrayId, String rawId) throws StorageFault {
        try {
            NVirtualVolume vvol = new NVirtualVolume();
            vvol.setVvolid(vvolid);
            vvol.setArrayId(arrayId);
            vvol.setRawId(rawId);
            vvol.setLunId(rawId);
            virtualVolumeDao.updateArrayIdAndRawIdByVvolId(vvol);
        } catch (Exception e) {
            LOGGER.error("updateArrayIdAndRawIdByVvolId error. vvolid:" + vvolid + ", arrayId:" + arrayId + ", rawId:"
                    + rawId);
            throw FaultUtil.storageFault("updateArrayIdAndRawIdByVvolId error.");
        }
    }

    @Override
    public void updateSizeByVvolId(String vvolid, long newSizeInMB) throws StorageFault {
        try {
            NVirtualVolume vvol = new NVirtualVolume();
            vvol.setVvolid(vvolid);
            vvol.setSize(newSizeInMB);
            virtualVolumeDao.updateSizeByVvolId(vvol);
        } catch (Exception e) {
            LOGGER.error("updateSizeByVvolId error. vvolid:" + vvolid + ", newSizeInMB:" + newSizeInMB);
            throw FaultUtil.storageFault("updateSizeByVvolId error.");
        }
    }

    @Override
    public void deleteVirtualVolumeByVvolId(String vvolid) throws StorageFault {
        try {
            virtualVolumeDao.deleteVirtualVolumeByVvolId(vvolid);
        } catch (Exception e) {
            LOGGER.error("deleteVirtualVolumeByVvolId error. vvolid:" + vvolid);
            throw FaultUtil.storageFault("deleteVirtualVolumeByVvolId error.");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteVirtualVolumeInfo(String vvolId) throws StorageFault {
        vvolMetadataDao.deleteVvolMetadataByVvolId(vvolId);
        virtualVolumeDao.deleteVirtualVolumeByVvolId(vvolId);
        vvolProfileDao.deleteVvolProfileByVvolId(vvolId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCreateDataIntoDatabase(NVirtualVolume vvol, List<NVvolProfile> listVvolProfile,
                                          List<NVvolMetadata> listVvolMetadata) throws StorageFault {
        try {
            if (vvol != null) {
                virtualVolumeDao.addVirtualVolume(vvol);
            }

            if (listVvolProfile != null && listVvolProfile.size() != 0) {
                for (NVvolProfile vvolProfile : listVvolProfile) {
                    vvolProfileDao.addVvolProfile(vvolProfile);
                }
            }
            if (listVvolMetadata != null && listVvolMetadata.size() != 0) {
                for (NVvolMetadata vvolMetadata : listVvolMetadata) {
                    vvolMetadataDao.addVvolMetadata(vvolMetadata);
                }
            }
        } catch (Exception e) {
            LOGGER.error("addCreateDataIntoDatabase error.Exception ", e);
            throw FaultUtil.storageFault("addCreateDataIntoDatabase error.");
        }
    }

    @Override
    public List<NVirtualVolume> getAllSpecifiedVvols(Date timestamp, String status) throws StorageFault {
        try {
            NVirtualVolume vvol = new NVirtualVolume();
            vvol.setStatus(status);
            vvol.setCreationTime(timestamp);
            return virtualVolumeDao.getAllSpecifiedVvols(vvol);
        } catch (Exception e) {
            LOGGER.error("getAllSpecifiedVvols error. status:" + status + ", timestamp:" + timestamp, e);
            throw FaultUtil.storageFault("getAllSpecifiedVvols error.");
        }
    }

    @Override
    public List<NVirtualVolume> getAllSpecifiedStatusVvols(String status) throws StorageFault {
        try {
            return virtualVolumeDao.getAllSpecifiedStatusVvols(status);
        } catch (Exception e) {
            LOGGER.error("getAllSpecifiedStatusVvols error. status:" + status, e);
            throw FaultUtil.storageFault("getAllSpecifiedStatusVvols error.");
        }
    }

    @Override
    public int getDeletingDependenciesCountById(String vvolid)
            throws StorageFault {
        try {
            Map<String, Object> dependencies = new HashMap<String, Object>();
            dependencies.put("vvolid", vvolid);
            List<String> sourceTypeList = new ArrayList<String>();
            sourceTypeList.add(VasaSrcTypeConstant.SNAPSHOT);
            sourceTypeList.add(VasaSrcTypeConstant.FAST_CLONE);
            dependencies.put("sourceType", sourceTypeList);
            return virtualVolumeDao.getDeletingDependenciesCountById(dependencies);
        } catch (Exception e) {
            LOGGER.error("getDeletingDependenciesCountById error. vvolid:" + vvolid, e);
            throw FaultUtil.storageFault("getDeletingDependenciesCountById error.");
        }
    }

    @Override
    public int getSnapshotAndFastCloneCountByVvolId(String vvolid)
            throws StorageFault {
        try {
            Map<String, Object> dependencies = new HashMap<String, Object>();
            dependencies.put("vvolid", vvolid);
            List<String> sourceTypeList = new ArrayList<String>();
            sourceTypeList.add(VasaSrcTypeConstant.SNAPSHOT);
            sourceTypeList.add(VasaSrcTypeConstant.FAST_CLONE);
            dependencies.put("sourceType", sourceTypeList);
            return virtualVolumeDao.getSnapshotAndFastCloneCountByVvolId(dependencies);
        } catch (Exception e) {
            LOGGER.error("getSnapshotAndFastCloneCountByVvolId error. vvolid:" + vvolid, e);
            throw FaultUtil.storageFault("getSnapshotAndFastCloneCountByVvolId error.");
        }
    }

    @Override
    public List<String> getAllVvolIds(int pageSize, int offset) throws StorageFault {
        try {
            Map<String, Object> cursors = new HashMap<String, Object>();
            cursors.put("pageSize", pageSize);
            cursors.put("from", offset);
            return virtualVolumeDao.getAllVvolIds(cursors);
        } catch (Exception e) {
            LOGGER.error("getAllVvols error. Exception", e);
            throw FaultUtil.storageFault("getAllVvols error.");
        }
    }

    public void updateDataByVvolId(Map<String, String> map) throws StorageFault {
        virtualVolumeDao.updateDataByVvolId(map);
    }

    public List<NVirtualVolume> getAllVirtualVolumeByContainerId(String containerId) throws StorageFault {
        try {
            return virtualVolumeDao.getAllVirtualVolumeByContainerId(containerId);
        } catch (Exception e) {
            LOGGER.error("getAllVirtualVolumeByContainerId error. Exception", e);
            throw FaultUtil.storageFault("getAllVirtualVolumeByContainerId error.");
        }
    }

    @Override
    public void updateStatusAndDeletedTimeByVvolId(String vvolid, String status) throws StorageFault {
        try {
            NVirtualVolume vvol = new NVirtualVolume();
            vvol.setVvolid(vvolid);
            vvol.setStatus(status);
            vvol.setDeletedTime(new Timestamp(System.currentTimeMillis()));
            LOGGER.info("In updateStatusAndDeletedTimeByVvolId function, the vvol=" + vvol.toString());
            virtualVolumeDao.updateStatusAndDeletedTimeByVvolId(vvol);
        } catch (Exception e) {
            LOGGER.error("updateStatusAndDeletedTimeByVvolId error. Exception", e);
            throw FaultUtil.storageFault("updateStatusAndDeletedTimeByVvolId error.");
        }
    }

    @Override
    public List<NVirtualVolume> getDeletingVirtualVolumeOrderByDeletedTime() throws StorageFault {
        try {
            return virtualVolumeDao.getDeletingVirtualVolumeOrderByDeletedTime();
        } catch (Exception e) {
            LOGGER.error("getDeletingVirtualVolumeOrderByDeletedTime error. Exception", e);
            throw FaultUtil.storageFault("getDeletingVirtualVolumeOrderByDeletedTime error.");
        }
    }

    @Override
    public int checkInBindStatus(String vvolId, String arrayId, String rawId) {
        // 判断当前vvol卷是否处于绑定状态
        try {
            VVolModel vvolMode = new VVolModel();
            SDKResult<List<S2DVvolBind>> bindResult = vvolMode.getVVOLBind(arrayId, rawId);
            LOGGER.debug("getVVOLBind bindResult=" + bindResult);
            if (0 != bindResult.getErrCode()) {
                LOGGER.error("get bind status error, the bindResult Code is : " + bindResult.getErrCode());
                return 1;
            }

            if (0 == bindResult.getErrCode() && bindResult.getResult() != null && bindResult.getResult().size() != 0) {
                LOGGER.info("ResourceInUse/the vvol vvolid:" + vvolId + " is in bound state.");
                return 2;
            }
            return 0;
        } catch (Exception e) {
            LOGGER.error("checkInBindStatus fail. Exception ", e);
            return 1;
        }
    }

    @Override
    public boolean checkDependencies(String vvolId) throws Exception {
        // 判断当前卷是否是其他快照或者fast-clone的原卷，并且目标卷不处于deleting状态
        int snapNum = getDependenciesCountByVvolId(vvolId);
        if (snapNum > 0) {
            LOGGER.info("ResourceInUse/the vvol vvolid:" + vvolId + " has snapshot or fast-clone in use");
            return true;
        }
        return false;
    }

    @Override
    public boolean checkDeletingDependencies(String vvolId) {
        try {
            int deletingDependenciesNum = getDeletingDependenciesCountById(vvolId);
            if (deletingDependenciesNum > 0) {
                LOGGER.info("The vvolId = " + vvolId + " deleting Dependencies number is " + deletingDependenciesNum);
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("checkDeletingDependencies fail. Exception ", e);
        }
        return false;
    }

    @Override
    public List<NVirtualVolume> getAllCreatingDataADayBeforeCreateTime() throws StorageFault {
        try {
            Date beginDate = new Date();
            Calendar date = Calendar.getInstance();
            date.setTime(beginDate);
            date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
            return virtualVolumeDao.getAllCreatingDataADayBefore(date.getTime());
        } catch (Exception e) {
            LOGGER.error("getAllCreatingDataADayBeforeCreateTime fail. Exception ", e);
            throw FaultUtil.storageFault("getAllCreatingDataADayBeforeCreateTime fail");
        }
    }

    @Override
    public List<NVirtualVolume> getVirtualVolumeByArrayIdAndRawPoolId(String arrayId, String rawPoolId)
            throws StorageFault {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("arrayId", arrayId);
            map.put("rawPoolId", rawPoolId);
            return virtualVolumeDao.getVirtualVolumeByArrayIdAndRawPoolId(map);
        } catch (Exception e) {
            LOGGER.error("getVirtualVolumeByArrayIdAndRawPoolId fail. Exception ", e);
            throw FaultUtil.storageFault("getVirtualVolumeByArrayIdAndRawPoolId fail");
        }
    }

    @Override
    public void updateVmIdByVvolId(String vmId, String vvolid) throws StorageFault {
        try {
            LOGGER.info("In updateVmIdByVvolId function. the vmId=" + vmId + " the vvolId=" + vvolid);
            Map<String, String> map = new HashMap<String, String>();
            map.put("vmId", vmId);
            map.put("vvolid", vvolid);
            virtualVolumeDao.updateVmIdByVvolId(map);
        } catch (Exception e) {
            LOGGER.error("updateVmIdByVvolId fail. Exception ", e);
            throw FaultUtil.storageFault("updateVmIdByVvolId fail");
        }
    }

    @Override
    public void updateVmInfoByVvolId(String vmId, String vmName, String vvolid) throws StorageFault {
        try {
            LOGGER.info("In updateVmInfoByVvolId function. the vmId=" + vmId + " the vmName=" + vmName + " the vvolId=" + vvolid);
            Map<String, String> map = new HashMap<String, String>();
            map.put("vmId", vmId);
            map.put("vmName", vmName);
            map.put("vvolid", vvolid);
            virtualVolumeDao.updateVmInfoByVvolId(map);
        } catch (Exception e) {
            LOGGER.error("updateVmInfoByVvolId fail. Exception ", e);
            throw FaultUtil.storageFault("updateVmInfoByVvolId fail");
        }
    }

    @Override
    public Long getVirtualVolumeCount() throws StorageFault {
        try {
            LOGGER.info("In getVirtualVolumeCount function.");
            return virtualVolumeDao.getVirtualVolumeCount();
        } catch (Exception e) {
            LOGGER.error("getVirtualVolumeCount fail. Exception ", e);
            throw FaultUtil.storageFault("updateVmInfoByVvolId fail");
        }
    }

    @Override
    public int getDiskCountByVmId(String vmId) throws StorageFault {
        try {
            LOGGER.info("In getDiskCountByVmId function.");
            int ret = virtualVolumeDao.getDiskCountByVmId(vmId);
            LOGGER.info("getDiskCountByVmId result = " + ret);
            return ret;
        } catch (Exception e) {
            LOGGER.error("getDiskCountByVmId fail. Exception ", e);
            throw FaultUtil.storageFault("getDiskCountByVmId fail");
        }
    }

    @Override
    public List<VvolOwnController> getAllAvailableVvols(Map<String, Object> map) {
        // TODO Auto-generated method stub
        return virtualVolumeDao.getAllAvailableVvols(map);
    }

    @Override
    public int getCloningVvolCountById(String ParentBolId) throws StorageFault {
        int ret = -1;
        try {
            LOGGER.info("In getCloningVvolCountById function.");
            ret = virtualVolumeDao.getDiskCountByVmId(ParentBolId);
        } catch (Exception e) {
            LOGGER.error("getCloningVvolCountById fail. Exception ", e);
            throw FaultUtil.storageFault("getCloningVvolCountById fail");
        }
        return ret;
    }
}
