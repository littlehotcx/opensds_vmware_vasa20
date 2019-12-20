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

package org.opensds.vasa.vasa.convert;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.domain.model.bean.DArray;
import org.opensds.vasa.domain.model.bean.DBackingConfig;
import org.opensds.vasa.domain.model.bean.DFileSystem;
import org.opensds.vasa.domain.model.bean.DFileSystemInfo;
import org.opensds.vasa.domain.model.bean.DLun;
import org.opensds.vasa.domain.model.bean.DMountInfo;
import org.opensds.vasa.domain.model.bean.DPort;
import org.opensds.vasa.domain.model.bean.DProcessor;
import org.opensds.vasa.domain.model.bean.DStorageCapability;

import com.vmware.vim.vasa.v20.data.xsd.BackingConfig;
import com.vmware.vim.vasa.v20.data.xsd.FileSystemInfo;
import com.vmware.vim.vasa.v20.data.xsd.MountInfo;
import com.vmware.vim.vasa.v20.data.xsd.StorageArray;
import com.vmware.vim.vasa.v20.data.xsd.StorageCapability;
import com.vmware.vim.vasa.v20.data.xsd.StorageFileSystem;
import com.vmware.vim.vasa.v20.data.xsd.StorageLun;
import com.vmware.vim.vasa.v20.data.xsd.StoragePort;
import com.vmware.vim.vasa.v20.data.xsd.StorageProcessor;

public class VasaServicePortTypeConvert {

    private static Logger LOGGER = LogManager
            .getLogger(VasaServicePortTypeConvert.class);

    public static StorageArray queryArraysModal2Soap(DArray modalItem) {
        if (null == modalItem) {
            return null;
        }
        LOGGER.info("DArray:" + modalItem.toString());
        StorageArray soapItem = new StorageArray();

        soapItem.setArrayName(modalItem.getArrayName());
        soapItem.setFirmware(modalItem.getFirmware());
        soapItem.setModelId(modalItem.getModelId());
        soapItem.setPriority(modalItem.getPriority());
        soapItem.setUniqueIdentifier(modalItem.getUniqueIdentifier());
        soapItem.setVendorId(modalItem.getVendorId());

        List<String> soapAlterNames = soapItem.getAlternateName();
        soapAlterNames.addAll(modalItem.getAlternateName());

        List<String> soapSupportedBlocks = soapItem.getSupportedBlock();
        soapSupportedBlocks.addAll(modalItem.getSupportedBlock());

        List<String> soapSupportedFileSystems = soapItem.getSupportedFileSystem();
        soapSupportedFileSystems.addAll(modalItem.getSupportedFileSystem());

        List<String> soapSupportProfiles = soapItem.getSupportedProfile();
        soapSupportProfiles.addAll(modalItem.getSupportedProfile());

        return soapItem;
    }

    public static List<StorageLun> queryStorageLunsModal2Soap(List<DLun> modalItem) {
        if (null == modalItem || 0 == modalItem.size()) {
            return new ArrayList<StorageLun>();
        }
        List<StorageLun> soapItem = new ArrayList<StorageLun>();
        for (DLun dlun : modalItem) {
            soapItem.add(queryStorageLunsModal2Soap(dlun));
        }

        return soapItem;
    }

    public static StorageLun queryStorageLunsModal2Soap(DLun modalItem) {
        if (null == modalItem) {
            return null;
        }
        StorageLun soapItem = new StorageLun();
        soapItem.setUniqueIdentifier(modalItem.getUniqueIdentifier());
        soapItem.setBackingConfig(queryStorageLunsModal2Soap(modalItem.getBackingConfig()));
        soapItem.setCapacityInMB(modalItem.getCapacityInMB());
        soapItem.setDisplayName(modalItem.getDisplayName());
        soapItem.setDrsManagementPermitted(modalItem.isDrsManagementPermitted());
        soapItem.setEsxLunIdentifier(modalItem.getEsxLunIdentifier());
        soapItem.setThinProvisioned(modalItem.isThinProvisioned());
        soapItem.setThinProvisioningStatus(modalItem.getThinProvisioningStatus());
        soapItem.setUsedSpaceInMB(modalItem.getUsedSpaceInMB());

        List<String> alterId = soapItem.getAlternateIdentifier();
        alterId.addAll(modalItem.getAlternateIdentifier());

        return soapItem;
    }

    public static BackingConfig queryStorageLunsModal2Soap(DBackingConfig modalItem) {
        if (null == modalItem) {
            return null;
        }
        BackingConfig soapItem = new BackingConfig();
        soapItem.setAutoTieringEnabled(modalItem.isAutoTieringEnabled());
        soapItem.setDeduplicationBackingIdentifier(modalItem.getDeduplicationBackingIdentifier());
        soapItem.setDeduplicationEfficiency(modalItem.getDeduplicationEfficiency());
        soapItem.setPerformanceOptimizationInterval(modalItem.getPerformanceOptimizationInterval());
        soapItem.setThinProvisionBackingIdentifier(modalItem.getThinProvisionBackingIdentifier());

        return soapItem;
    }

    public static List<StorageFileSystem> queryStorageFileSystemsModal2Soap1(List<DFileSystem> modalItem) {
        List<StorageFileSystem> soapItem = new ArrayList<StorageFileSystem>();
        if (null == modalItem || modalItem.size() == 0) {
            return soapItem;
        }

        for (DFileSystem dfs : modalItem) {
            soapItem.add(queryStorageFileSystemsModal2Soap(dfs));
        }

        return soapItem;
    }

    public static StorageFileSystem queryStorageFileSystemsModal2Soap(DFileSystem modalItem) {
        if (null == modalItem) {
            return null;
        }
        StorageFileSystem soapItem = new StorageFileSystem();
        soapItem.setBackingConfig(queryStorageLunsModal2Soap(modalItem.getBackingConfig()));
        soapItem.setFileSystem(modalItem.getFileSystem());
        soapItem.setFileSystemVersion(modalItem.getFileSystemVersion());
        soapItem.setNativeSnapshotSupported(modalItem.isNativeSnapshotSupported());
        soapItem.setThinProvisioningStatus(modalItem.getThinProvisioningStatus());
        soapItem.setUniqueIdentifier(modalItem.getUniqueIdentifier());
        List<FileSystemInfo> fsInfo = soapItem.getFileSystemInfo();
        fsInfo.addAll(queryStorageFileSystemsModal2Soap(modalItem.getFileSystemInfo()));

        return soapItem;
    }

    public static List<FileSystemInfo> queryStorageFileSystemsModal2Soap(List<DFileSystemInfo> modalItem) {
        List<FileSystemInfo> soapItem = new ArrayList<FileSystemInfo>();
        if (null == modalItem || modalItem.size() == 0) {
            return soapItem;
        }
        for (DFileSystemInfo dfsInfo : modalItem) {
            soapItem.add(queryStorageFileSystemsModal2Soap(dfsInfo));
        }

        return soapItem;
    }

    public static FileSystemInfo queryStorageFileSystemsModal2Soap(DFileSystemInfo modalItem) {
        if (null == modalItem) {
            return null;
        }
        FileSystemInfo soapItem = new FileSystemInfo();

        soapItem.setFileServerName(modalItem.getFileServerName());
        soapItem.setFileSystemPath(modalItem.getFileSystemPath());
        soapItem.setIpAddress(modalItem.getIpAddress());
        return soapItem;
    }

    public static StorageProcessor queryStorageProcessorsModal2Soap(DProcessor modalItem) {
        if (null == modalItem) {
            return null;
        }
        StorageProcessor soapItem = new StorageProcessor();
        soapItem.setUniqueIdentifier(modalItem.getUniqueIdentifier());
        List<String> sps = soapItem.getSpIdentifier();
        sps.addAll(modalItem.getSpIdentifier());

        return soapItem;
    }

    public static List<StorageProcessor> queryStorageProcessorsModal2Soap(List<DProcessor> modalItem) {
        List<StorageProcessor> soapItem = new ArrayList<StorageProcessor>();
        if (null == modalItem) {
            return soapItem;
        }

        for (DProcessor dp : modalItem) {
            soapItem.add(queryStorageProcessorsModal2Soap(dp));
        }

        return soapItem;
    }

    public static StoragePort queryStoragePortsModal2Soap(DPort modalItem) {
        if (null == modalItem) {
            return null;
        }
        StoragePort soapItem = new StoragePort();

        soapItem.setIscsiIdentifier(modalItem.getIscsiIdentifier());
        soapItem.setNodeWwn(modalItem.getNodeWwn());
        soapItem.setPortType(modalItem.getPortType());
        soapItem.setPortWwn(modalItem.getPortWwn());
        soapItem.setUniqueIdentifier(modalItem.getUniqueIdentifier());
        List<String> alterNames = soapItem.getAlternateName();
        alterNames.addAll(modalItem.getAlternateName());

        return soapItem;
    }

    public static List<StoragePort> queryStoragePortsModal2Soap(List<DPort> modalItem) {
        List<StoragePort> soapItem = new ArrayList<StoragePort>();
        if (null == modalItem || modalItem.size() == 0) {
            return soapItem;
        }

        for (DPort dp : modalItem) {
            soapItem.add(queryStoragePortsModal2Soap(dp));
        }

        return soapItem;
    }

    public static DMountInfo mountInfo2DMountInfo(MountInfo soapItem) {
        if (null == soapItem) {
            return null;
        }

        DMountInfo modalItem = new DMountInfo();
        modalItem.setFilePath(soapItem.getFilePath());
        modalItem.setServerName(soapItem.getServerName());

        return modalItem;
    }

    public static List<DMountInfo> mountInfo2DMountInfo(List<MountInfo> soapItem) {
        List<DMountInfo> modalItem = new ArrayList<DMountInfo>();
        if (null == soapItem || soapItem.size() == 0) {
            return modalItem;
        }

        for (MountInfo mi : soapItem) {
            modalItem.add(mountInfo2DMountInfo(mi));
        }
        return modalItem;
    }

    public static StorageCapability queryStorageCapabilitiesModal2Soap(DStorageCapability modalItem) {
        if (null == modalItem) {
            return null;
        }

        StorageCapability soapItem = new StorageCapability();
        soapItem.setCapabilityDetail(modalItem.getCapabilityDetail());
        soapItem.setCapabilityName(modalItem.getCapabilityName());
        soapItem.setUniqueIdentifier(modalItem.getUniqueIdentifier());

        return soapItem;
    }

    public static List<StorageCapability> queryStorageCapabilitiesModal2Soap(List<DStorageCapability> modalItem) {
        List<StorageCapability> soapItem = new ArrayList<StorageCapability>();
        if (null == modalItem || modalItem.size() == 0) {
            return soapItem;
        }

        for (DStorageCapability sc : modalItem) {
            soapItem.add(queryStorageCapabilitiesModal2Soap(sc));
        }

        return soapItem;
    }
}
