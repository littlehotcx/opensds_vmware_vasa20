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

package org.opensds.vasa.vasa.runnable;

import java.util.ArrayList;
import java.util.List;

import org.opensds.vasa.base.common.VasaConstant;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.VasaNasArrayService;
import org.opensds.vasa.vasa.db.model.EsxHostIp;
import org.opensds.vasa.vasa.db.model.FilesystemShareTable;
import org.opensds.vasa.vasa.db.model.ShareClientTable;
import org.opensds.vasa.vasa.db.service.EsxHostIpService;
import org.opensds.vasa.vasa.db.service.StorageManagerService;
import org.opensds.vasa.vasa.db.service.VvolPathService;
import org.opensds.vasa.vasa.util.VASAUtil;
import org.opensds.vasa.vasa20.device.array.NFSshare.AddAuthClientResBean;

public class UpdateHostIps implements Runnable {

    private List<String> newIps;
    private String currHostId;
    private List<String> addIps = new ArrayList<String>();
    private List<String> deleteIps = new ArrayList<String>();
    private List<FilesystemShareTable> shares;
    private List<FilesystemShareTable> dataFs;
    private List<FilesystemShareTable> commFs;

    private StorageManagerService storageManagerService = (StorageManagerService) ApplicationContextUtil
            .getBean("storageManagerService");
    private VasaNasArrayService vasaNasArrayService = (VasaNasArrayService) ApplicationContextUtil
            .getBean("vasaNasArrayService");
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(UpdateHostIps.class);
    private VvolPathService vvolPathDBService = (VvolPathService) ApplicationContextUtil
            .getBean("vvolPathService");
    private EsxHostIpService esxHostIpService = (EsxHostIpService) ApplicationContextUtil
            .getBean("esxHostIpService");

    public UpdateHostIps(String currHostId, List<String> newIps) {
        super();
        this.newIps = newIps;
        this.currHostId = currHostId;
        addIps = new ArrayList<String>();
        deleteIps = new ArrayList<String>();
        shares = vvolPathDBService.queryAllShare();
        dataFs = new ArrayList<FilesystemShareTable>();
        commFs = new ArrayList<FilesystemShareTable>();
        update(currHostId, newIps, addIps, deleteIps);
    }


    private void updateFs(List<FilesystemShareTable> shares, List<String> addIps, List<String> deleteIps, boolean shouldUpdateHostDb) {
        for (FilesystemShareTable share : shares) {

            String shareId = share.getShareId().split(VasaConstant.SEPARATOR)[0];
            String arrayId = share.getShareId().split(VasaConstant.SEPARATOR)[1];
            List<ShareClientTable> clients = vvolPathDBService.queryShareClientByShareId(share.getShareId());
            for (String ip : addIps) {
                boolean isNotExist = true;
                for (ShareClientTable client : clients) {
                    if (client.getShareProperty().startsWith(ip)) {
                        isNotExist = false;
                        break;
                    }
                }
                if (isNotExist) {
                    SDKResult<AddAuthClientResBean> ret = vasaNasArrayService.createClient(arrayId, ip, shareId);
                    if (ret.getErrCode() == 0) {
                        vvolPathDBService.insertShareClientRecord(
                                new ShareClientTable(ip + VasaConstant.SEPARATOR + ret.getResult().getID() + VasaConstant.SEPARATOR + arrayId
                                        , shareId + VasaConstant.SEPARATOR + arrayId));
                    }
                }
            }

            for (String ip : deleteIps) {
                for (ShareClientTable client : clients) {
                    if (client.getShareProperty().startsWith(ip)) {
                        SDKErrorCode ret = vasaNasArrayService.deleteClient(arrayId, client.getShareProperty().split(VasaConstant.SEPARATOR)[1]);
                        if (ret.getErrCode() == 0) {
                            vvolPathDBService.deleteRecordByProperty(client.getShareProperty());
                        }
                    }
                }
            }
        }

        if (shouldUpdateHostDb) {
            updateEsxHostInfo(addIps, deleteIps, currHostId);
        }

    }

    private void updateEsxHostInfo(List<String> addIps, List<String> deleteIps, String currHostId) {
        for (String addIp : addIps) {
            LOGGER.info("add ip[" + addIp + "],hostId[" + currHostId + "].");
            esxHostIpService.insertRecord(new EsxHostIp(addIp, currHostId));
        }

        for (String delIp : deleteIps) {
            LOGGER.info("delete ip[" + delIp + "],hostId[" + currHostId + "].");
            esxHostIpService.deleteRecord(new EsxHostIp(delIp, currHostId));
        }
    }

    private void update(String currHostId, List<String> newHostIps, List<String> addIps, List<String> deleteIps) {
        if (currHostId == null) {
            return;
        }

        List<String> oldHostIdIps = new ArrayList<String>();
        List<EsxHostIp> esxhostIps = esxHostIpService.queryEsxHostIpByHostId(currHostId);
        for (EsxHostIp host : esxhostIps) {
            oldHostIdIps.add(host.getIp());
        }
        addIps.addAll(VASAUtil.getUpdateIps(newHostIps, oldHostIdIps));
        deleteIps.addAll(VASAUtil.getUpdateIps(oldHostIdIps, newHostIps));

        if (shares == null || shares.isEmpty()
                || (shares.size() <= 2))// "/"共享，但是并未创建common文件系统
        {
            //share为空，则更新数据库中当前的主机的信息
            //优先更新数据库，
            updateEsxHostInfo(addIps, deleteIps, currHostId);
            return;
        } else {
            for (FilesystemShareTable share : shares) {
                if (share.getSharePath().startsWith("/" + VasaConstant.FILE_SYSTEM_TYPE_COMMON)) {
                    commFs.add(share);
                } else if (share.getSharePath().startsWith("/" + VasaConstant.FILE_SYSTEM_PREFIX)) {
                    if ((share.getHostId().indexOf(currHostId) != -1)
                            || share.getHostId().equals(VasaConstant.HOST_ID_CERTIFICATION)) {
                        dataFs.add(share);
                    }
                } else {
                    LOGGER.warn("unknow fs type," + share.getSharePath() + ".");
                }
            }
        }
    }

    /*
     * 分开更新comm和data是为了先将common更新，然后将data的更新放到新的线程里面去
     * 避免因当前esx主机有很多虚拟机需要更新，造成后续的虚拟机创建绑定失败的问题
     */
    public void updateCommFs() {
        if (currHostId == null || commFs.isEmpty()) {
            return;
        }
        if (dataFs.isEmpty()) {
            //只要share不为空，则必须更新esxhost信息
            updateFs(commFs, addIps, deleteIps, true);
        } else {
            updateFs(commFs, addIps, deleteIps, false);
        }
        if (addIps != null && !addIps.isEmpty()) {
            for (FilesystemShareTable share : commFs) {
                if (share.getHostId().equals(VasaConstant.HOST_ID_CERTIFICATION)
                        || share.getHostId().indexOf(currHostId) != -1) {
                    continue;
                } else {
                    share.setHostId(share.getHostId() + VasaConstant.SEPARATOR + currHostId);
                    vvolPathDBService.updateHostId(share);
                }
            }
        }
    }

    @Override
    public void run() {
        if (currHostId == null || dataFs.isEmpty()) {
            return;
        }

        updateFs(dataFs, addIps, deleteIps, true);
    }
}
