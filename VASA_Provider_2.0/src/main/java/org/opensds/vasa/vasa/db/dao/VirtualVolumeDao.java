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

package org.opensds.vasa.vasa.db.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.rest.bean.VvolOwnController;

public interface VirtualVolumeDao {
    Long getVirtualVolumeCount();

    int getDiskCountByVmId(String vmId);

    List<String> getAllVvolIds(Map<String, Object> cursors);

    NVirtualVolume getVirtualVolumeByVvolId(String vvolid);

    List<NVirtualVolume> getVirtualVolumeByParentId(String parentid);

    List<String> getVvolIdByArrayIdAndRawId(Map<String, Object> constraints);

    List<NVirtualVolume> getAllInactiveSnapshots();

    List<NVirtualVolume> getAllVirtualVolumeByContainerId(String containerId);

    int getDependenciesCountByVvolId(Map<String, Object> dependencies);

    void addVirtualVolume(NVirtualVolume vvol);

    void updateStatusByVvolId(NVirtualVolume vvol);

    void updateStatusAndDeletedTimeByVvolId(NVirtualVolume vvol);

    void updateParentIdByVvolId(NVirtualVolume vvol);

    void updateArrayIdAndRawIdByVvolId(NVirtualVolume vvol);

    void updateSizeByVvolId(NVirtualVolume vvol);

    void deleteVirtualVolumeByVvolId(String vvolid);

    //add get all specified  VirtualVolumes  and specified status VirtualVolumes
    List<NVirtualVolume> getAllSpecifiedVvols(NVirtualVolume vvol);

    List<NVirtualVolume> getAllSpecifiedStatusVvols(String status);

    List<NVirtualVolume> getVirtualVolumeByArrayIdAndRawPoolId(Map<String, Object> map);

    int getDeletingDependenciesCountById(Map<String, Object> dependencies);

    int getSnapshotAndFastCloneCountByVvolId(Map<String, Object> dependencies);

    void updateDataByVvolId(Map<String, String> map);

    void updateVmIdByVvolId(Map<String, String> map);

    void updateVmInfoByVvolId(Map<String, String> map);

    List<NVirtualVolume> getDeletingVirtualVolumeOrderByDeletedTime();

    List<NVirtualVolume> getAllCreatingDataADayBefore(Date createTime);

    List<VvolOwnController> getAllAvailableVvols(Map<String, Object> map);

    long getVMAllDataSizeByVvolId(String vvolid);

    long getVMAllDataSizeByVmId(String vmid);
}
