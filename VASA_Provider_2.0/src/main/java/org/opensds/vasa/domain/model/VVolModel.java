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

package org.opensds.vasa.domain.model;

import java.util.List;

import org.opensds.vasa.domain.model.bean.S2DBitmap;
import org.opensds.vasa.domain.model.bean.S2DLunCopy;
import org.opensds.vasa.domain.model.bean.S2DPassThroughSnapshot;
import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.domain.model.bean.S2DVvolBind;
import org.opensds.vasa.interfaces.device.bitmap.IBitmapCapability;
import org.opensds.vasa.interfaces.device.luncopy.ILunCopyCapability;
import org.opensds.vasa.interfaces.device.snapshot.ISnapshotextendCapability;
import org.opensds.vasa.interfaces.device.storagepool.IStoragePoolCapability;
import org.opensds.vasa.interfaces.device.vvolbind.IVvolBindCapability;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.nemgr.itf.IDeviceManager;
import org.opensds.vasa.vasa20.device.array.lun.IDeviceLunService;
import org.opensds.vasa.vasa20.device.array.lun.LunCreateResBean;

public class VVolModel {
    private static IDeviceManager deviceManager = (IDeviceManager) ApplicationContextUtil.getBean("deviceManager");

    public static IDeviceManager getDeviceManager() {
        return deviceManager;
    }

    public static void setDeviceManager(IDeviceManager deviceManager) {
        VVolModel.deviceManager = deviceManager;
    }
	
	/*public SDKResult<List<S2DVolumeType>> getAllVolumeType() throws SDKException
	{
		IVolumeTypeCapability volumeTypeCapability = 
				getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
						IVolumeTypeCapability.class);
		SDKResult<List<S2DVolumeType>> result = volumeTypeCapability.getAllVolumeType();
		return result;
	}*/

    public SDKResult<List<S2DStoragePool>> getAllStoragePool(String arrayId) throws SDKException {
        IStoragePoolCapability storagePoolCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId, IStoragePoolCapability.class);
        SDKResult<List<S2DStoragePool>> result = storagePoolCapability.getAllStoragePool();
        return result;
    }

    public SDKResult<S2DStoragePool> getStoragePoolByPoolId(String arrayId, String poolId) throws SDKException {
        IStoragePoolCapability storagePoolCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId, IStoragePoolCapability.class);
        SDKResult<S2DStoragePool> result = storagePoolCapability.getStoragePoolByPoolId(arrayId, poolId);
        return result;
    }
	
	/*public SDKResult<List<S2DVolumeType>> getVolumeTypeByVirtualPool(String poolId) throws SDKException
	{
		IVolumeTypeCapability volumeTypeCapability = 
				getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
						IVolumeTypeCapability.class);
		SDKResult<List<S2DVolumeType>> result = volumeTypeCapability.getVolumeTypeByVirtualPool(poolId);
		return result;
	}*/

    /*public SDKResult<List<S2DVirtualPool>> getAllVirtualPool() throws SDKException
    {
        IVirtualPoolCapability virtualPoolCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVirtualPoolCapability.class);
        SDKResult<List<S2DVirtualPool>> result = virtualPoolCapability.getAllVirtualPool();
        return result;
    }

    public SDKResult<S2DVirtualPool> getVirtualPoolById(String poolId) throws SDKException
    {
        IVirtualPoolCapability virtualPoolCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVirtualPoolCapability.class);
        SDKResult<S2DVirtualPool> result = virtualPoolCapability.getVirtualPoolById(poolId);
        return result;
    }

    public SDKResult<S2DVirtualPoolSpaceStats> getVirtualPoolSpaceStatsById(String poolId) throws SDKException
    {
        IVirtualPoolCapability virtualPoolCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVirtualPoolCapability.class);
        SDKResult<S2DVirtualPoolSpaceStats> result = virtualPoolCapability.getVirtualPoolSpaceStatsById(poolId);
        return result;
    }

    public SDKResult<List<S2DVirtualPool>> getVirtualPoolByArrayId(String arrayId) throws SDKException
    {
        IVirtualPoolCapability virtualPoolCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVirtualPoolCapability.class);
        SDKResult<List<S2DVirtualPool>> result = virtualPoolCapability.getVirtualPoolByArrayId(arrayId);
        return result;
    }


    public SDKResult<List<S2DVolume>> getAllVolume() throws SDKException
    {
        IVolumeCapability volumeCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeCapability.class);
        SDKResult<List<S2DVolume>> result = volumeCapability.getAllVolume();
        return result;
    }

    public SDKResult<S2DVolume> getVolumeById(String volumeId) throws SDKException
    {
        IVolumeCapability volumeCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeCapability.class);
        SDKResult<S2DVolume> result = volumeCapability.getVolumeById(volumeId);
        return result;
    }

    public SDKResult<S2DVolume> createVolume(String name, String description, int sizeInGB, long sizeInMB,
            String volumeType,String vmName) throws SDKException
    {
        IVolumeCapability volumeCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeCapability.class);
        SDKResult<S2DVolume> result = volumeCapability.createVolume(name, description, sizeInGB, sizeInMB, volumeType,vmName);
        return result;
    }

    public SDKResult<S2DVolume> createVolumeFromSrcVolume(String name, String description,int sizeInGB, long sizeInMB,
            String volumeType, String volumeId,String vmName) throws SDKException
    {
        IVolumeCapability volumeCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeCapability.class);
        SDKResult<S2DVolume> result = volumeCapability.createVolumeFromSrcVolume(name, description, sizeInGB, sizeInMB, volumeType, volumeId,vmName);
        return result;
    }

    public SDKResult<S2DVolume> cloneVolumeFromRawVvol(String name, String description, String sourceVvolId, long sizeInMB) throws SDKException
    {
        IVolumeCapability volumeCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeCapability.class);
        SDKResult<S2DVolume> result = volumeCapability.cloneVolumeFromRawVvol(name, description, sourceVvolId, sizeInMB);
        return result;
    }

    public SDKResult<S2DVolume> cloneVolumeFromSnapshotVvol(String name, String description, String snapshotId, long sizeInMB) throws SDKException
    {
        IVolumeCapability volumeCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeCapability.class);
        SDKResult<S2DVolume> result = volumeCapability.cloneVolumeFromSnapshotVvol(name, description, snapshotId, sizeInMB);
        return result;
    }

    public SDKResult<S2DVolume> fastCloneVolumeFromRawVvol(String name, String description, String sourceVvolId,String vmName) throws SDKException
    {
        IVolumeCapability volumeCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeCapability.class);
        SDKResult<S2DVolume> result = volumeCapability.fastCloneVolumeFromRawVvol(name, description, sourceVvolId,vmName);
        return result;
    }

    public SDKResult<S2DVolume> fastCloneVolumeFromSnapshotVvol(String name, String description, String snapshotId,String vmName) throws SDKException
    {
        IVolumeCapability volumeCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeCapability.class);
        SDKResult<S2DVolume> result = volumeCapability.fastCloneVolumeFromSnapshotVvol(name, description, snapshotId,vmName);
        return result;
    }

    public SDKErrorCode resizeVolume(String id, int newSize) throws SDKException
    {
        IVolumeCapability volumeCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeCapability.class);
        SDKErrorCode errCode = volumeCapability.resizeVolume(id, newSize);
        return errCode;
    }

    public SDKErrorCode deleteVolume(String id) throws SDKException
    {
        IVolumeCapability volumeCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeCapability.class);
        SDKErrorCode errCode = volumeCapability.deleteVolume(id);
        return errCode;
    }

    public SDKErrorCode deleteVolumeForcely(String id) throws SDKException
    {
        IVolumeCapability volumeCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeCapability.class);
        SDKErrorCode errCode = volumeCapability.deleteVolumeForcely(id);
        return errCode;
    }
    */
    public SDKResult<S2DVvolBind> bind(String arrayId, String hostId, String vvolId, int bindType) throws SDKException {
        IVvolBindCapability vvolBindCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IVvolBindCapability.class);
        SDKResult<S2DVvolBind> result = vvolBindCapability.bind(arrayId, hostId, vvolId, bindType);
        return result;
    }

    public SDKResult<List<S2DVvolBind>> getVVOLBind(String arrayId, String vvolId) throws SDKException {
        IVvolBindCapability vvolBindCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IVvolBindCapability.class);
        SDKResult<List<S2DVvolBind>> result = vvolBindCapability.getVVOLBind(arrayId, vvolId);
        return result;
    }

    public SDKErrorCode unbindVvolFromAllHost(String arrayId, String vvolId) throws SDKException {
        IVvolBindCapability vvolBindCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IVvolBindCapability.class);
        SDKErrorCode result = vvolBindCapability.unbindVvolFromAllHost(arrayId, vvolId);
        return result;
    }

    public SDKErrorCode unbindAllVvolFromHost(String arrayId, String hostId) throws SDKException {
        IVvolBindCapability vvolBindCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IVvolBindCapability.class);
        SDKErrorCode result = vvolBindCapability.unbindAllVvolFromHost(arrayId, hostId);
        return result;
    }

    public SDKErrorCode unbindVvolFromPELun(String arrayId, String vvolSecondaryId, String PELunId, int bindType) throws SDKException {
        IVvolBindCapability vvolBindCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IVvolBindCapability.class);
        SDKErrorCode result = vvolBindCapability.unbindVvolFromPELun(arrayId, vvolSecondaryId, PELunId, bindType);
        return result;
    }

    public SDKErrorCode unbindVvolFromPELunAndHost(String arrayId,
                                                   String hostId, String vvolSecondaryId, String PELunId, int bindType) throws SDKException {
        IVvolBindCapability vvolBindCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IVvolBindCapability.class);
        SDKErrorCode result = vvolBindCapability.unbindVvolFromPELunAndHost(arrayId, hostId, vvolSecondaryId, PELunId, bindType);
        return result;
    }
	
	/*public SDKResult<List<S2DSnapshot>> getAllSnapshot() throws SDKException
	{
		ISnapshotCapability snapshotCapability = 
				getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
						ISnapshotCapability.class);
		SDKResult<List<S2DSnapshot>> result = snapshotCapability.getAllSnapshot();
		return result;
	}
	
	public SDKResult<S2DSnapshot> getSnapshotById(String snapshotId) throws SDKException
	{
		ISnapshotCapability snapshotCapability = 
				getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
						ISnapshotCapability.class);
		SDKResult<S2DSnapshot> result = snapshotCapability.getSnapshotById(snapshotId);
		return result;
	}
	
	public SDKResult<S2DSnapshot> createSnapshot(String vvolId, String name, String description) throws SDKException
	{
		ISnapshotCapability snapshotCapability = 
				getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
						ISnapshotCapability.class);
		SDKResult<S2DSnapshot> result = snapshotCapability.createSnapshot(vvolId, name, description);
		return result;
	}*/

    public SDKErrorCode activateSnapshot(String arrayId, List<String> snapshotIds) throws SDKException {
        ISnapshotextendCapability snapshotCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ISnapshotextendCapability.class);
        SDKErrorCode result = snapshotCapability.activateSnapshot(arrayId, snapshotIds);
        return result;
    }

    /*public SDKErrorCode deleteSnapshot(String snapshotId) throws SDKException
    {
        ISnapshotCapability snapshotCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        ISnapshotCapability.class);
        SDKErrorCode result = snapshotCapability.deleteSnapshot(snapshotId);
        return result;
    }

    public SDKErrorCode deleteSnapshotForcely(String snapshotId) throws SDKException
    {
        ISnapshotCapability snapshotCapability =
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        ISnapshotCapability.class);
        SDKErrorCode result = snapshotCapability.deleteSnapshotForcely(snapshotId);
        return result;
    }
    */
    public SDKResult<S2DPassThroughSnapshot> getSnapshotById(String arrayId, String rawId) throws SDKException {
        ISnapshotextendCapability snapshotCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ISnapshotextendCapability.class);
        SDKResult<S2DPassThroughSnapshot> result = snapshotCapability.getSnapshotById(arrayId, rawId);
        return result;
    }

    public SDKErrorCode rollbackSnapshot(String arrayId, String rawId) throws SDKException {
        ISnapshotextendCapability snapshotCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ISnapshotextendCapability.class);
        SDKErrorCode result = snapshotCapability.rollbackSnapshot(arrayId, rawId);
        return result;
    }

    public SDKResult<S2DBitmap> getAllocatedBitmap(String arrayId, String vvolId, long segmentStartOffsetBytes,
                                                   long segmentLengthBytes, long chunkSizeBytes) throws SDKException {

        IBitmapCapability bitmapCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IBitmapCapability.class);
        SDKResult<S2DBitmap> result = bitmapCapability.getAllocatedBitmap(arrayId, vvolId, segmentStartOffsetBytes, segmentLengthBytes, chunkSizeBytes);
        return result;
    }

    public SDKResult<S2DBitmap> getUnsharedBitmap(String arrayId, String vvolId, String baseVvolId, long segmentStartOffsetBytes,
                                                  long segmentLengthBytes, long chunkSizeBytes) throws SDKException {
        IBitmapCapability bitmapCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IBitmapCapability.class);
        SDKResult<S2DBitmap> result = bitmapCapability.getUnsharedBitmap(arrayId, vvolId, baseVvolId, segmentStartOffsetBytes,
                segmentLengthBytes, chunkSizeBytes);
        return result;
    }

    public SDKResult<S2DBitmap> getUnsharedChunks(String arrayId, String vvolId, String baseVvolId,
                                                  long segmentStartOffsetBytes, long segmentLengthBytes) throws SDKException {
        IBitmapCapability bitmapCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IBitmapCapability.class);
        SDKResult<S2DBitmap> result = bitmapCapability.getUnsharedChunks(arrayId, vvolId, baseVvolId, segmentStartOffsetBytes, segmentLengthBytes);
        return result;
    }

    public SDKResult<S2DLunCopy> createLuncopy(String arrayId, String name, String description, String sourceLun,
                                               String targetLun, String baseLun, boolean isDiffsLunCopy) throws SDKException {
        ILunCopyCapability luncopyCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ILunCopyCapability.class);
        SDKResult<S2DLunCopy> result = luncopyCapability.createLuncopy(arrayId, name, description, sourceLun, targetLun, baseLun, isDiffsLunCopy);
        return result;
    }

    public SDKResult<S2DLunCopy> getLuncopyById(String arrayId, String luncopyId) throws SDKException {
        ILunCopyCapability luncopyCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ILunCopyCapability.class);
        SDKResult<S2DLunCopy> result = luncopyCapability.getLuncopyById(arrayId, luncopyId);
        return result;
    }

    public SDKErrorCode startLuncopy(String arrayId, String luncopyId, boolean isDiffsLunCopy) throws SDKException {
        ILunCopyCapability luncopyCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ILunCopyCapability.class);
        SDKErrorCode result = luncopyCapability.startLuncopy(arrayId, luncopyId, isDiffsLunCopy);
        return result;
    }

    public SDKErrorCode stopLuncopy(String arrayId, String luncopyId, boolean isDiffsLunCopy) throws SDKException {
        ILunCopyCapability luncopyCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ILunCopyCapability.class);
        SDKErrorCode result = luncopyCapability.stopLuncopy(arrayId, luncopyId, isDiffsLunCopy);
        return result;
    }

    public SDKErrorCode deleteLuncopy(String arrayId, String luncopyId, boolean isDiffsLunCopy) throws SDKException {
        ILunCopyCapability luncopyCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ILunCopyCapability.class);
        SDKErrorCode result = luncopyCapability.deleteLuncopy(arrayId, luncopyId, isDiffsLunCopy);
        return result;
    }

   /* public SDKResult<JSONObject> createVolumeType(StoragePolicy profile) throws SDKException
    {
        IVolumeTypeCapability volumeTypeCapability = 
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeTypeCapability.class);
        return volumeTypeCapability.createVolumeType(profile);
    }	
    
    public void delVolumeType(String volTypeId) throws SDKException
    {
        IVolumeTypeCapability volumeTypeCapability = 
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeTypeCapability.class);
        volumeTypeCapability.delVolumeType(volTypeId);
    }       
    
    public SDKResult<SQos> createQoS(JSONObject qos_specs) throws SDKException
    {
        IVolumeTypeCapability volumeTypeCapability = 
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeTypeCapability.class);
        return volumeTypeCapability.createQoS(qos_specs);
    }       

    public void delQos(String qosId) throws SDKException
    {
        IVolumeTypeCapability volumeTypeCapability = 
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeTypeCapability.class);
        volumeTypeCapability.delQos(qosId);
    }    
    
    public SDKResult<String> associateQoS(String qosId, String volTypeId) throws SDKException
    {
        IVolumeTypeCapability volumeTypeCapability = 
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeTypeCapability.class);
        return volumeTypeCapability.associateQoS(qosId, volTypeId);
    }
    
    public void deAssociateQoS(String qosId, String volTypeId) throws SDKException
    {
        IVolumeTypeCapability volumeTypeCapability = 
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeTypeCapability.class);
        volumeTypeCapability.disassociateQoS(qosId, volTypeId);
    }
    
    public void convertQos2ExtraSpecs(SQos qosInfo, S2DExtraSpecsInfo specsInfo) throws SDKException
    {
        IVolumeTypeCapability volumeTypeCapability = 
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeTypeCapability.class);
        volumeTypeCapability.convertQos2ExtraSpecs(qosInfo, specsInfo);
    }
    public SDKResult<SQos> getQosByVolumeType(String volTypeId) throws SDKException
    {
        IVolumeTypeCapability volumeTypeCapability = 
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeTypeCapability.class);
        return volumeTypeCapability.getQosByVolumeType(volTypeId);
    } 
    public SDKResult<Object> setVolumeRetype(String volId,String newType,String migrationPolicy) throws SDKException
    {
        IVolumeTypeCapability volumeTypeCapability = 
                getDeviceManager().getDeviceServiceProxy(ConfigManager.getInstance().getValue("esdk.vasa_dj_device"),
                        IVolumeTypeCapability.class);
       return volumeTypeCapability.setVolumeRetype(volId,newType,migrationPolicy);
    }*/

    public SDKResult<LunCreateResBean> getLunInfoById(String arrayId, String lunId) throws SDKException {

        IDeviceLunService deviceLunService = getDeviceManager().getDeviceServiceProxy(arrayId, IDeviceLunService.class);
        return deviceLunService.queryLunInfo(arrayId, lunId);
    }


}
