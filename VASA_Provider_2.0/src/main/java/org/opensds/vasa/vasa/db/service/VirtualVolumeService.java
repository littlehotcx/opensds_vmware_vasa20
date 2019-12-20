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

package org.opensds.vasa.vasa.db.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.db.model.NVvolMetadata;
import org.opensds.vasa.vasa.db.model.NVvolProfile;
import org.opensds.vasa.vasa.rest.bean.VvolOwnController;

import com.vmware.vim.vasa.v20.StorageFault;

public interface VirtualVolumeService {
    Long getVirtualVolumeCount() throws StorageFault;

    int getDiskCountByVmId(String vmId) throws StorageFault;

    List<String> getAllVvolIds(int pageSize, int offset) throws StorageFault;

    NVirtualVolume getVirtualVolumeByVvolId(String vvolid) throws StorageFault;

    long getVMAllDataSizeByVvolID(String vvolid) throws StorageFault;

    long getVMAllDataSizeByVmId(String vvolid) throws StorageFault;

    NVirtualVolume getVirtualVolumeByVvolIdNotIncludeDeleting(String vvolid) throws StorageFault;

    List<NVirtualVolume> getVirtualVolumeByParentId(String parentid) throws StorageFault;

    List<String> getVvolIdByArrayIdAndRawId(String arrayId, String rawId) throws StorageFault;

    List<NVirtualVolume> getAllInactiveSnapshots() throws StorageFault;

    List<NVirtualVolume> getAllSpecifiedVvols(Date timestamp, String status) throws StorageFault;

    List<NVirtualVolume> getAllSpecifiedStatusVvols(String status) throws StorageFault;

    List<NVirtualVolume> getAllVirtualVolumeByContainerId(String containerId) throws StorageFault;

    List<NVirtualVolume> getDeletingVirtualVolumeOrderByDeletedTime() throws StorageFault;

    List<NVirtualVolume> getVirtualVolumeByArrayIdAndRawPoolId(String arrayId, String rawPoolId) throws StorageFault;

    void updateParentIdByVvolId(String vvolId, String parentId) throws StorageFault;

    int getDependenciesCountByVvolId(String vvolid) throws StorageFault;

    void addVirtualVolume(NVirtualVolume vvol) throws StorageFault;

    void updateStatusByVvolId(String vvolid, String status) throws StorageFault;

    void updateStatusAndDeletedTimeByVvolId(String vvolid, String status) throws StorageFault;

    void updateArrayIdAndRawIdByVvolId(String vvolid, String arrayId, String rawId) throws StorageFault;

    void updateSizeByVvolId(String vvolid, long newSizeInMB) throws StorageFault;

    void deleteVirtualVolumeByVvolId(String vvolid) throws StorageFault;

    void addCreateDataIntoDatabase(NVirtualVolume vvol, List<NVvolProfile> listVvolProfile,
                                   List<NVvolMetadata> listVvolMetadata) throws StorageFault;

    void updateDataByVvolId(Map<String, String> map) throws StorageFault;

    List<NVirtualVolume> getAllCreatingDataADayBeforeCreateTime() throws StorageFault;

    //add
    int getDeletingDependenciesCountById(String vvolid) throws StorageFault;

    int getSnapshotAndFastCloneCountByVvolId(String vvolid) throws StorageFault;

    void deleteVirtualVolumeInfo(String vvolId) throws StorageFault;

    //判断卷是否处于绑定状态，是则返回true
    int checkInBindStatus(String vvolId, String arrayId, String rawId) throws Exception;

    //判断卷是存在依赖卷不包含deleting状态，存在则返回true
    boolean checkDependencies(String vvolId) throws Exception;

    //判断卷是否存在deleting状态的依赖卷，存在则返回true
    boolean checkDeletingDependencies(String vvolId);

    //更新Vvol lun的虚拟机属性
    void updateVmIdByVvolId(String vmId, String vvolId) throws StorageFault;

    void updateVmInfoByVvolId(String vmId, String vmName, String vvolId) throws StorageFault;

    List<VvolOwnController> getAllAvailableVvols(Map<String, Object> map);

    int getCloningVvolCountById(String ParentBolId) throws StorageFault;
}
