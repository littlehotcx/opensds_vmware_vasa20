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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.FaultUtil;

import org.opensds.vasa.vasa.db.dao.VirtualMachineDao;
import org.opensds.vasa.vasa.db.model.NVirtualMachine;
import org.opensds.vasa.vasa.db.service.VirtualMachineService;

import com.vmware.vim.vasa.v20.StorageFault;

public class VirtualMachineServiceImpl implements VirtualMachineService {

    private static Logger LOGGER = LogManager
            .getLogger(VirtualMachineServiceImpl.class);

    private VirtualMachineDao virtualMachineDao;

    @Override
    public void addVirtualMachine(NVirtualMachine nVirtualMachine) throws StorageFault {
        // TODO Auto-generated method stub
        try {
            virtualMachineDao.addVirtualMachine(nVirtualMachine);
        } catch (Exception e) {
            LOGGER.error("addVirtualMachine error. Exception : ", e);
            throw FaultUtil.storageFault("addVirtualMachine error");
        }
    }

    @Override
    public List<NVirtualMachine> getVirtualMachineInfoByVmId(String vmId) throws StorageFault {
        try {
            return virtualMachineDao.getVirtualMachineInfoByVmId(vmId);
        } catch (Exception e) {
            LOGGER.error("getVirtualMachineInfoByVmId error. Exception : ", e);
            throw FaultUtil.storageFault("getVirtualMachineInfoByVmId error");
        }
    }

    @Override
    public void updateVirtualMachine(NVirtualMachine nVirtualMachine) throws StorageFault {
        // TODO Auto-generated method stub
        try {
            virtualMachineDao.updateVirtualMachine(nVirtualMachine);
        } catch (Exception e) {
            LOGGER.error("updateVirtualMachine error. Exception : ", e);
            throw FaultUtil.storageFault("updateVirtualMachine error");
        }
    }

    @Override
    public void deleteVirtualMachine(NVirtualMachine nVirtualMachine) throws StorageFault {
        // TODO Auto-generated method stub
        try {
            virtualMachineDao.deleteVirtualMachine(nVirtualMachine);
        } catch (Exception e) {
            LOGGER.error("deleteVirtualMachine error. Exception : ", e);
            throw FaultUtil.storageFault("deleteVirtualMachine error");
        }
    }

    public VirtualMachineDao getVirtualMachineDao() {
        return virtualMachineDao;
    }

    public void setVirtualMachineDao(VirtualMachineDao virtualMachineDao) {
        this.virtualMachineDao = virtualMachineDao;
    }

}
