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

import org.opensds.vasa.vasa.db.dao.FileSystemTableDao;
import org.opensds.vasa.vasa.db.dao.FilesystemShareTableDao;
import org.opensds.vasa.vasa.db.dao.ShareClientTableDao;
import org.opensds.vasa.vasa.db.dao.VvolPathDao;
import org.opensds.vasa.vasa.db.model.FileSystemTable;
import org.opensds.vasa.vasa.db.model.FilesystemShareTable;
import org.opensds.vasa.vasa.db.model.ShareClientTable;
import org.opensds.vasa.vasa.db.model.VvolPath;
import org.opensds.vasa.vasa.db.service.VvolPathService;
import org.opensds.vasa.vasa.util.FaultUtil;

import com.vmware.vim.vasa.v20.StorageFault;

public class VvolPathServiceImpl implements VvolPathService {

    VvolPathDao vvolPathDao;
    FileSystemTableDao fileSystemTableDao;
    FilesystemShareTableDao filesystemShareTableDao;
    ShareClientTableDao shareClientTableDao;


    public FileSystemTableDao getFileSystemTableDao() {
        return fileSystemTableDao;
    }

    public void setFileSystemTableDao(FileSystemTableDao fileSystemTableDao) {
        this.fileSystemTableDao = fileSystemTableDao;
    }

    public FilesystemShareTableDao getFilesystemShareTableDao() {
        return filesystemShareTableDao;
    }

    public void setFilesystemShareTableDao(FilesystemShareTableDao filesystemShareTableDao) {
        this.filesystemShareTableDao = filesystemShareTableDao;
    }

    public ShareClientTableDao getShareClientTableDao() {
        return shareClientTableDao;
    }

    public void setShareClientTableDao(ShareClientTableDao shareClientTableDao) {
        this.shareClientTableDao = shareClientTableDao;
    }

    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(VvolPathServiceImpl.class);

    @Override
    public boolean isExistFileSystem(String vvolType, String fileSystemName) {
        return fileSystemTableDao.queryFileSystemTableByName(fileSystemName) != null;
    }

    @Override
    public FileSystemTable queryFileSystemTableByName(String fileSystemName) {
        return fileSystemTableDao.queryFileSystemTableByName(fileSystemName);
    }

    @Override
    public List<FileSystemTable> queryFileSystemTableByFuzzySystemName(String fileSystemName) {
        List<FileSystemTable> allsys = fileSystemTableDao.getAllFileSystemTable();//nd_todo 优化
//        LOGGER.info("all sys:" + allsys.toString());
        LOGGER.info("fuzzy sys name: " + fileSystemName);
        List<FileSystemTable> commsys = new ArrayList<FileSystemTable>();
        for (FileSystemTable sys : allsys) {
            if (sys.getFileSystemName().startsWith(fileSystemName)) {
                commsys.add(sys);
            }
        }
        return commsys;//fileSystemTableDao.getFileSystemTableByFuzzySystemName(fileSystemName);
    }

    @Override
    public void insertFilesystemTable(FileSystemTable recode) {
        if (fileSystemTableDao.queryFileSystemTableByName(recode.getFileSystemName()) != null) {
            return;
        }
        fileSystemTableDao.insertRecord(recode);
    }

    @Override
    public synchronized void updateFileCount(String fileSystemName, int cnt) {
        FileSystemTable temp = fileSystemTableDao.queryFileSystemTableByName(fileSystemName);
        if (temp == null) {
            return;
        }
        int count = Integer.valueOf(Integer.valueOf(temp.getFileCount()) + cnt);
        temp.setFileCount(String.valueOf(count));
        fileSystemTableDao.updateFileCount(temp);
    }

    @Override
    public void updateFileCountAndCapacity(String fileSystemName, int num, long capacity) {
        FileSystemTable temp = fileSystemTableDao.queryFileSystemTableByName(fileSystemName);
        if (temp == null) {
            return;
        }
        Map<String, Object> para = new HashMap<String, Object>();
        para.put("currentCapacity", capacity);
        para.put("fileCount", num);
        para.put("fileSystemName", fileSystemName);

        fileSystemTableDao.updateFileCountAndCapacity(para);
    }


    @Override
    public synchronized void updateCurrCapacity(String fileSystemName, long capacity) {
        FileSystemTable temp = fileSystemTableDao.queryFileSystemTableByName(fileSystemName);
        if (temp == null) {
            return;
        }
        long currentCapacity = Long.valueOf(Long.valueOf(temp.getCurrentCapacity()) + capacity);
        temp.setCurrentCapacity(String.valueOf(currentCapacity));
        fileSystemTableDao.updateFileCurrCapacity(temp);
    }

    @Override
    //这里应该可以不加锁 nd_todo
    public synchronized void updateFilesystemTableFsID(String fileSystemName, String id) {
        FileSystemTable temp = fileSystemTableDao.queryFileSystemTableByName(fileSystemName);
        if (temp == null) {
            return;
        }
        temp.setId(id);
        fileSystemTableDao.updateFsId(temp);
    }

    public void updateStatus(String fileSystemName, String status) {
        FileSystemTable temp = fileSystemTableDao.queryFileSystemTableByName(fileSystemName);
        temp.setStatus(status);
        fileSystemTableDao.updateStatus(temp);
    }

    @Override
    public void deleteFilesystemTableByName(String fileSystemName) {
        fileSystemTableDao.deleteRecodeByFilesystemName(fileSystemName);
    }

    @Override
    public List<VvolPath> queryAllVvolPathByFileSystem(String fileSystemName) {
        LOGGER.info("begin queryAllRecordByFileSystem !! ");
        List<VvolPath> ret = vvolPathDao.queryAllRecordByFileSystem(fileSystemName);
        LOGGER.info("query List<VvolPath>=" + ret.size());
        return ret;
    }

    @Override
    public FilesystemShareTable queryShareByShareName(String shareName) {
        return filesystemShareTableDao.queryRecodeByShareName(shareName);
    }

    @Override
    public void insertShareRecord(FilesystemShareTable record) {
        if (filesystemShareTableDao.queryRecodeByShareName(record.getSharePath()) != null) {
            return;
        }
        filesystemShareTableDao.insertRecord(record);
    }

    @Override
    public void deleteShareByShareName(String shareName) {
        filesystemShareTableDao.deleteRecordByShareName(shareName);
    }


    @Override
    public List<FilesystemShareTable> queryAllShare() {
        return filesystemShareTableDao.queryAllShare();
    }

    @Override
    public synchronized void updateHostId(FilesystemShareTable record) {
        filesystemShareTableDao.updateHostId(record);
    }

    @Override
    public int getCountRecordByFileSystemName(String fileSystemName) {
        return queryAllVvolPathByFileSystem(fileSystemName).size();//nd_todo 优化
    }

    @Override
    public List<ShareClientTable> queryShareClientByShareId(String shareId) {
        return shareClientTableDao.queryShareClientByShareId(shareId);
    }


    @Override
    public void insertShareClientRecord(ShareClientTable record) {
        LOGGER.info("client info: " + record.toString());
        shareClientTableDao.insertRecord(record);
    }

    @Override
    public void deleteShareClientByShareId(String shareId) {
        shareClientTableDao.deleteRecordByShareId(shareId);
    }

    @Override
    public void deleteRecordByProperty(String property) {
        shareClientTableDao.deleteRecordByProperty(property);
    }

    @Override
    public boolean isBindState(String vvolId) {
        LOGGER.info("check bind state vvol: " + vvolId);
        return vvolPathDao.isBindState(vvolId);
    }

    @Override
    public void setBindState(String vvolId, boolean isBind) {
        LOGGER.info("change bind state vvolid: " + vvolId + " bind state isbind:" + isBind);
        try {
            VvolPath vvolPath = vvolPathDao.getVvolPathByVvolId(vvolId);
            vvolPath.setBind(isBind);
            vvolPathDao.setBindState(vvolPath);
        } catch (Exception e) {
            LOGGER.error("======>  " + e);
        }
    }


    //插入一条新的记录
    @Override
    public void insertRecord(VvolPath record) throws StorageFault {
        if (record.getFileSystemName() == null || record.getSharePath() == null
                || record.getVvolid() == null || record.getPath() == null) {
            LOGGER.error("vvolpath invalid :" + record.toString());
            throw FaultUtil.storageFault("vvolpath invalid.");
        }
        vvolPathDao.insertRecord(record);
    }

    public void setVvolPathDao(VvolPathDao base) {
        vvolPathDao = base;
    }

    public VvolPathDao getVvolPathDao() {
        return vvolPathDao;
    }

    //更新sharePath
    @Override
    public void updateSharePath(VvolPath newSharePath) {
        vvolPathDao.updateSharePath(newSharePath);
    }

    @Override
    public VvolPath getVvolPathByVvolId(String vvolId) {
        return vvolPathDao.getVvolPathByVvolId(vvolId);
    }


    @Override
    public void deleteRecordByVvolId(String vvolId) {
        vvolPathDao.deleteVvolPathByVvolId(vvolId);
    }

    @Override
    public void updateVvolId(VvolPath vvolPath) {
        vvolPathDao.updateVvolId(vvolPath);
    }

    @Override
    public void deleteVvolPathByVvolId(String vvolId) {
        vvolPathDao.deleteVvolPathByVvolId(vvolId);
    }
}
