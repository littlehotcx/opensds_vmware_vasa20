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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.domain.model.VVolModel;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.util.VASAResponseCode;
import org.opensds.vasa.vasa.util.VASAUtil;
import org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemCreateResBean;
import org.opensds.vasa.vasa20.device.array.lun.LunCreateResBean;

import org.opensds.vasa.base.common.VasaSrcTypeConstant;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.VasaNasArrayService;
import org.opensds.vasa.vasa.db.model.VvolPath;
import org.opensds.vasa.vasa.db.service.VirtualVolumeService;
import org.opensds.vasa.vasa.db.service.VvolPathService;
import org.opensds.vasa.vasa.rest.bean.QueryVvolOwnControllerRes;
import org.opensds.vasa.vasa.rest.bean.VvolOwnController;

import com.vmware.vim.vasa.v20.StorageFault;

@Path("vasa/vvolOwnControllerService")
public class VvolOwnControllerResource {

    private Logger LOGGER = LogManager.getLogger(VvolOwnControllerResource.class);

    private VirtualVolumeService virtualVolumeService = ApplicationContextUtil.getBean("virtualVolumeService");

    private VvolPathService vvolPathDBService = (VvolPathService) ApplicationContextUtil.getBean("vvolPathService");

    private VasaNasArrayService vasaNasArrayService = (VasaNasArrayService) ApplicationContextUtil.getBean("vasaNasArrayService");

    private VVolModel vvolModel = new VVolModel();

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryVvolOwnControllerRes queryVvolOwnControllerService(@QueryParam("keyword") String keyword,
                                                                   @QueryParam("sortName") String sortName,
                                                                   @QueryParam("sortType") String sortType,
                                                                   @QueryParam("from") String from,
                                                                   @QueryParam("searchField") String searchField,
                                                                   @QueryParam("perPageRecords") String perPageRecords) {
        QueryVvolOwnControllerRes response = new QueryVvolOwnControllerRes();
        try {
            if (StringUtils.isEmpty(perPageRecords) || StringUtils.isEmpty(from)) {
                response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
                response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
                return response;
            }

            int from_ = 0;
            int perPageRecords_ = 10;
            try {
                from_ = Integer.parseInt(from);
                perPageRecords_ = Integer.parseInt(perPageRecords);
            } catch (Exception e) {
                response.setResultCode("2");
                response.setResultDescription("parameters incorrect.");
                return response;
            }


            Map<String, Object> map = new HashMap<String, Object>();

            map.put("keyword", keyword);
            map.put("searchField", searchField);
            map.put("from", from_);
            map.put("perPageRecords", perPageRecords_);
            map.put("sortName", sortName);
            map.put("sortType", sortType);

            LOGGER.debug("Start queryFaultData...parameters..");

            LOGGER.info("map..." + map);
            LOGGER.info("searchField..." + searchField);
            LOGGER.info("keyword..." + keyword);
            LOGGER.info("perPageRecords..." + perPageRecords);
            LOGGER.info("sortName..." + sortName);
            LOGGER.info("sortType..." + sortType);

            List<VvolOwnController> allVvols = virtualVolumeService.getAllAvailableVvols(map);
            List<VvolOwnController> allAvailableVvols = new ArrayList<VvolOwnController>();
            for (VvolOwnController vvolOwnController : allVvols) {
                if ("Data".equalsIgnoreCase(vvolOwnController.getVvolType())) {
                    allAvailableVvols.add(vvolOwnController);
                }
            }
            List<VvolOwnController> rawAndCloneVirtualVolumes = new ArrayList<VvolOwnController>();
            Map<String, List<VvolOwnController>> parentMap = new HashMap<>();
            for (VvolOwnController vvolOwnController : allAvailableVvols) {
                if (vvolOwnController.getSourceType().equalsIgnoreCase(VasaSrcTypeConstant.RAW) ||
                        vvolOwnController.getSourceType().equalsIgnoreCase(VasaSrcTypeConstant.CLONE)) {
                    rawAndCloneVirtualVolumes.add(vvolOwnController);
                }
                String parentId = vvolOwnController.getParentId();
                if (org.opensds.platform.common.utils.StringUtils.isNotEmpty(parentId)) {
                    List<VvolOwnController> list = parentMap.get(parentId);
                    if (null == list) {
                        list = new ArrayList<>();
                    }
                    list.add(vvolOwnController);
                    parentMap.put(parentId, list);
                }
            }

            VvolOwnController[] vvolOwnControllers = new VvolOwnController[rawAndCloneVirtualVolumes.size()];
            int i = 0;
            for (VvolOwnController vvolOwnController : rawAndCloneVirtualVolumes) {
                vvolOwnController.setChildrenNum(getChildNum(vvolOwnController.getVvolId(), parentMap));
                vvolOwnControllers[i++] = vvolOwnController;
            }

            Arrays.sort(vvolOwnControllers, new Comparator<VvolOwnController>() {

                @Override
                public int compare(VvolOwnController o1, VvolOwnController o2) {
                    // TODO Auto-generated method stub
                    if (o1.getChildrenNum() > o2.getChildrenNum()) {
                        return -1;
                    } else if (o1.getChildrenNum() < o2.getChildrenNum()) {
                        return 1;
                    } else {
                        if (null == o1.getVvolName() && null == o2.getVvolName()) {
                            return o1.getVvolId().compareTo(o2.getVvolId());
                        } else if (null == o1.getVvolName()) {
                            return -1;
                        } else if (null == o2.getVvolName()) {
                            return 1;
                        } else {
                            return o1.getVvolName().compareTo(o2.getVvolName());
                        }
                    }
                }
            });

            response.setCount(vvolOwnControllers.length);
            LOGGER.info("====================size:" + vvolOwnControllers.length);
            List<VvolOwnController> result = new ArrayList<>();
            if (vvolOwnControllers.length > 0) {
                for (i = from_; i < from_ + perPageRecords_ && i <= (vvolOwnControllers.length - 1); i++) {
                    VvolOwnController vvolOwnController = vvolOwnControllers[i];
                    LOGGER.info("============ owning controller vvolID " + vvolOwnController.getVvolId());
                    try {
                        if (isNasVvol(vvolOwnController.getVvolId())) {
                            VvolPath vvolPath = vvolPathDBService.getVvolPathByVvolId(vvolOwnController.getVvolId());
                            VASAUtil.saveArrayId(vvolOwnController.getArrayId());
                            SDKResult<FileSystemCreateResBean> queryFileSystemResult = vasaNasArrayService.queryFileSystemByName(vvolPath.getFileSystemName());
                            if (queryFileSystemResult.getErrCode() == 0) {
                                vvolOwnController.setController(queryFileSystemResult.getResult().getOWNINGCONTROLLER());
                                LOGGER.info("get nas vvol owning controller success! OnwController is " + vvolOwnController.getController());
                            } else {
                                LOGGER.error("get vvol nas owning Controller failed ");
                                throw FaultUtil.storageFault();
                            }
                        } else {
                            SDKResult<LunCreateResBean> lunInfoById = vvolModel.getLunInfoById(vvolOwnController.getArrayId(), vvolOwnController.getLunId());
                            if (lunInfoById.getErrCode() == 0) {
                                vvolOwnController.setController(lunInfoById.getResult().getOWNINGCONTROLLER());
                            } else {
                                LOGGER.error("vvolModel.getLunInfoById error. errCode=" + lunInfoById.getErrCode() + ",message=" + lunInfoById.getDescription());
                            }
                        }

                    } catch (SDKException e) {
                        // TODO Auto-generated catch block
                        LOGGER.error("vvolModel.getLunInfoById error. arrayId=" + vvolOwnController.getArrayId() + ",lunId=" + vvolOwnController.getLunId(), e);
                    }
                    result.add(vvolOwnController);
                }
            }

            response.setResultCode(0l);
            LOGGER.info("Response = " + result);
            response.setVvolOwnControllers(result);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response.setResultCode(-1l);
        }
        return response;
    }

    public int getChildNum(String parent, Map<String, List<VvolOwnController>> parentMap) {
        int i = 0;
        List<VvolOwnController> list = parentMap.get(parent);
        if (null != list) {
            for (VvolOwnController nVirtualVolume : list) {
                i++;
                i += getChildNum(nVirtualVolume.getVvolId(), parentMap);
            }
        }
        return i;
    }

    public VirtualVolumeService getVirtualVolumeService() {
        return virtualVolumeService;
    }

    public void setVirtualVolumeService(VirtualVolumeService virtualVolumeService) {
        this.virtualVolumeService = virtualVolumeService;
    }

    public boolean isNasVvol(String vvolId) throws StorageFault {
        if (virtualVolumeService.getVirtualVolumeByVvolId(vvolId) == null) {
            LOGGER.error("unknow vvolId = " + vvolId);
            throw FaultUtil.storageFault("unknow vvolId = " + vvolId);
        }
        try {
            return vvolPathDBService.getVvolPathByVvolId(vvolId) == null ? false : true;
        } catch (Exception e) {
            LOGGER.error("unknow vvolPath ,vvolId =  " + vvolId);
            throw FaultUtil.storageFault("unknow vvolPath ,vvolId =  " + vvolId);
        }
    }
}
