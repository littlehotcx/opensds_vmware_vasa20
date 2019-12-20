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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.service.DiscoverService;
import org.opensds.vasa.vasa.service.DiscoverServiceImpl;
import org.opensds.vasa.vasa.util.VASAResponseCode;
import org.opensds.vasa.vasa.util.VASAUtil;

import org.opensds.vasa.base.common.VasaSrcTypeConstant;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.VasaNasArrayService;
import org.opensds.vasa.vasa.db.model.FaultData;
import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.db.service.FaultDataManagerService;
import org.opensds.vasa.vasa.db.service.VirtualVolumeService;
import org.opensds.vasa.vasa.db.service.VvolMetadataService;
import org.opensds.vasa.vasa.db.service.VvolPathService;
import org.opensds.vasa.vasa.rest.bean.CountResponseBean;
import org.opensds.vasa.vasa.rest.bean.DelFaultDataResponse;
import org.opensds.vasa.vasa.rest.bean.DelFaultDataResutl;
import org.opensds.vasa.vasa.rest.bean.DeletedFailVolume;
import org.opensds.vasa.vasa.rest.bean.QueryFaultDataResponse;

import com.vmware.vim.vasa.v20.ResourceInUse;
import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.VasaProviderBusy;

@Path("vasa/faultDataManager")
public class FaultDataManagerResource {
    private FaultDataManagerService faultDataManagerService = ApplicationContextUtil
            .getBean("faultDataManagerService");

    private VvolMetadataService vvolMetadataService = (VvolMetadataService) ApplicationContextUtil
            .getBean("vvolMetadataService");

    private VirtualVolumeService virtualVolumeService = (VirtualVolumeService) ApplicationContextUtil
            .getBean("virtualVolumeService");

    private VasaNasArrayService vasaNasArrayService = (VasaNasArrayService) ApplicationContextUtil.getBean("vasaNasArrayService");

    private VvolPathService vvolPathDBService = (VvolPathService) ApplicationContextUtil
            .getBean("vvolPathService");

    private Logger LOGGER = LogManager.getLogger(FaultDataManagerResource.class);

    @GET
    @Path("count")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CountResponseBean getVirtualVolumeCount(@QueryParam("keyword") String keyword,
                                                   @QueryParam("sortName") String sortName,
                                                   @QueryParam("sortType") String sortType,
                                                   @QueryParam("from") String from,
                                                   @QueryParam("searchField") String searchField,
                                                   @QueryParam("perPageRecords") String perPageRecords) {
        LOGGER.info("In getVirtualVolumeCount function.");
        CountResponseBean response = new CountResponseBean();
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

        LOGGER.debug("Start getVirtualVolumeCount...parameters..");

        LOGGER.info("map..." + map);
        LOGGER.info("searchField..." + searchField);
        LOGGER.info("keyword..." + keyword);
        LOGGER.info("perPageRecords..." + perPageRecords);
        LOGGER.info("sortName..." + sortName);
        LOGGER.info("sortType..." + sortType);
        try {
            Long count = (long) faultDataManagerService.queryFaultDataCount(map);
            response.setCount(count);
            response.setResultCode(0L);
            response.setResultDescription("getVirtualVolumeCount successful.");
        } catch (Exception e) {
            LOGGER.error("getVirtualVolumeCount fail : ", e);
            response.setResultCode(1L);
            response.setResultDescription("getVirtualVolumeCount fail.");
        }
        return response;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryFaultDataResponse queryFaultData(
            @QueryParam("keyword") String keyword,
            @QueryParam("sortName") String sortName,
            @QueryParam("sortType") String sortType,
            @QueryParam("from") String from,
            @QueryParam("searchField") String searchField,
            @QueryParam("perPageRecords") String perPageRecords
    ) {
        QueryFaultDataResponse response = new QueryFaultDataResponse();
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

        try {
            List<FaultData> result = faultDataManagerService
                    .queryFaultData(map);
            //按照vmId进行排序
            //Collections.sort(result);
            response.setFaultDatas(result);
            LOGGER.debug("Start faultDataManagerService.queryFaultDataCount(map)..");

            int count = faultDataManagerService.queryFaultDataCount(map);
            response.setCount(count);

            response.setResultCode("0");
            response.setResultDescription("query successfully.");
        } catch (Exception e) {
            response.setResultCode("1");
            response.setResultDescription("query failed.");
            LOGGER.error("queryFaultData error: " + e, e);
        }

        return response;
    }


    public int delVitrualVolume(String vvolId, DiscoverServiceImpl discoverServiceImpl, DiscoverService discoverService) {
        LOGGER.info("the del vvold is : " + vvolId);
        List<String> delVvolList = new ArrayList<String>();
        List<NVirtualVolume> delNVirtualVolume = new ArrayList<NVirtualVolume>();
        delVvolList.add(vvolId);

        try {
            delNVirtualVolume = discoverService.queryVirtualVolumeFromDataBase(delVvolList);
            if (delNVirtualVolume.get(0).getRawId().equalsIgnoreCase("NA")) {
                discoverServiceImpl.deleteVirtualVolumeFromDatabase(vvolId);
                return 0;
            }
        } catch (StorageFault e) {
            LOGGER.error(e);
        }
        if (delNVirtualVolume == null || delNVirtualVolume.size() == 0) {
            return 0;
        }

        try {
            if (delNVirtualVolume.size() > 0) {
                VASAUtil.saveArrayId(delNVirtualVolume.get(0).getArrayId());
            }
            // 判断vvol是否处于绑定状态
            int bindResult = virtualVolumeService.checkInBindStatus(vvolId, delNVirtualVolume.get(0).getArrayId(), delNVirtualVolume.get(0).getRawId());
            if (bindResult == 2) {
                LOGGER.error("the vvol vvolid:" + vvolId + " is in bound state.");
                return 3;
            }

            //判断是否存在增值服务：如lunCopy，如果存在则删除
            List<String> lunCopyTaskIds = DiscoverServiceImpl.getInstance().getLunCopyTaskId(delNVirtualVolume.get(0).getArrayId(), delNVirtualVolume.get(0).getRawId());
            if (lunCopyTaskIds.size() > 0) {
                LOGGER.info("The vvol vvolid :" + vvolId + " exist Value-added services, the size is " + lunCopyTaskIds.size());
                for (String luncopyId : lunCopyTaskIds) {
                    //遍历删除所有luncopy，能删则删，删除不了等下一周期
                    DiscoverServiceImpl.getInstance().deleteLunCopyTaskByLunCopyId(delNVirtualVolume.get(0).getArrayId(), luncopyId);
                }
            }
            boolean result = false;
            if (VasaSrcTypeConstant.SNAPSHOT.equalsIgnoreCase(delNVirtualVolume.get(0).getSourceType()) ||
                    VasaSrcTypeConstant.FAST_CLONE.equalsIgnoreCase(delNVirtualVolume.get(0).getSourceType())) {
                result = discoverServiceImpl.deleteSnapShotFormArray(vvolId);
            } else {
                result = discoverServiceImpl.deleteVirtualVolumeFormArray(vvolId);
            }
            if (result) {
                LOGGER.info("delete vvol success, statusCode = 0,vvolId = " + vvolId);
                return 0;
            } else {
                LOGGER.info("delete vvol error, statusCode = 1,vvolId = " + vvolId);
                return 1;
            }
            //result = discoverServiceImpl.deleteVirtualVolume(delNVirtualVolume.get(0), true);
        } catch (Exception e) {
            LOGGER.error("Del vvol fail , " + e, e);
        }
        return 1;
    }

    public Map<String, String> getSubDelList(NVirtualVolume vvolid, DiscoverService discoverService, List<String> vvolIdList, List<String> delVvolListStatic) {
        List<NVirtualVolume> vvolSunList = new ArrayList<NVirtualVolume>();
        Map<String, String> resultMap = new HashMap<String, String>();
        String stopVvolId = new String();

        try {
            vvolSunList = discoverService.getVirtualVolumeFromDataBaseByParentId(vvolid.getVvolid());
        } catch (StorageFault e) {
            // TODO Auto-generated catch block
            LOGGER.error("Get vvol Sun list fail, " + e);
        }

        LOGGER.info("The vvol Sun list is : " + vvolSunList.toString());

        //鎶婄埗浜插姞鍒板垹闄ゅ垪琛ㄥ唴
        delVvolListStatic.add(vvolid.getVvolid());

        LOGGER.info("DelVvolListStatic : " + delVvolListStatic.toString() + " the size is : " + delVvolListStatic.size());

        //鍒ゆ柇鏄惁鏈夊瀛�
        if (0 != vvolSunList.size()) {
            //鏈夊瀛愮户缁妸瀛╁瓙鍔犺繘鏉�
            for (NVirtualVolume i : vvolSunList) {
                //鍒ゆ柇瀛╁瓙鍦ㄤ笉鍦ㄥ垹闄ゅ垪琛ㄥ唴
                if (vvolIdList.contains(i.getVvolid())) {
                    //鍦ㄥ垹闄ゅ垪琛ㄥ唴锛岀户缁亶鍘嗭紝鐩村埌鎶婃墍鏈夌殑瀛╁瓙閮藉姞瀹�
                    resultMap = getSubDelList(i, discoverService, vvolIdList, delVvolListStatic);
                    LOGGER.info("ResultMap is " + resultMap.toString());
                } else {
                    //涓嶅湪鍒犻櫎鍒楄〃鍐咃紝娓呯┖resultMap骞跺仠姝㈤亶鍘�
                    resultMap.clear();
                    stopVvolId = i.getVvolid();
                    LOGGER.warn("The vvol id :" + stopVvolId + " is not in delList");
                    resultMap.put("1", stopVvolId);
                    return resultMap;
                }
            }
        } else {
            //娌℃湁瀛╁瓙浼氶�鍑�
            resultMap.put("0", vvolid.getVvolid());
            LOGGER.info("No child to exit : " + resultMap.toString());
            return resultMap;
        }
        return resultMap;
    }

    /**
     * 鏂规硶 锛�getResultString
     *
     * @param result 鏂规硶鍙傛暟锛氱粨鏋滅姸鎬佺爜
     *               0 : del success
     *               1 : del fail
     *               2 : time out
     *               3 : in bound state
     *               4 : has dependent volume.
     * @return String 杩斿洖缁撴灉
     */
    public String getResultString(String vvolName, String sourceType, HashMap<String, String> dependMap) {

        String resultString = new String();

        resultString = vvolName + "," + sourceType + ",";
        if (null != dependMap) {
            Iterator<String> iter = dependMap.keySet().iterator();
            while (iter.hasNext()) {
                String dependVvolName = (String) iter.next();
                String dependSourceType = (String) dependMap.get(dependVvolName);
                resultString += dependVvolName + "|";
                if (dependSourceType == null) {
                    dependSourceType = "source";
                }
                resultString += dependSourceType;
                if (iter.hasNext()) {
                    resultString += "|";
                }
            }

        }
        //resultString +=",";
        //resultString += result;
        LOGGER.info("The resultString is : " + resultString);
        return resultString;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DelFaultDataResponse delFaultData(@QueryParam("vvolIds") String vvolIds) {//TO DO
        LOGGER.info("In delFaultArrayData function, the vvolIds is : " + vvolIds);
        List<DelFaultDataResutl> delFaultDataResultList = new ArrayList<DelFaultDataResutl>();
        DelFaultDataResponse response = new DelFaultDataResponse();
        response.setDelFaultDataResultList(delFaultDataResultList);
        List<String> reqDelVvolIdList = new ArrayList<String>();

        DiscoverService discoverService = DiscoverService.getInstance();
        DiscoverServiceImpl discoverServiceImpl = DiscoverServiceImpl.getInstance();

        HashSet<String> alreadyDealSet = new HashSet<String>();
        HashSet<String> deleteSuccessSet = new HashSet<String>();

        try {
            if (StringUtils.isEmpty(vvolIds)) {
                response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
                response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
                return response;
            }
            String[] vvolIdsArray = vvolIds.split(",");
            for (int i = 0; i < vvolIdsArray.length; i++) {
                reqDelVvolIdList.add((String) vvolIdsArray[i]);
            }

            LOGGER.info("The request del vvolIdList is " + reqDelVvolIdList.toString());
            List<NVirtualVolume> needDelVirtualVolumes = discoverService.queryVirtualVolumeFromDataBase(reqDelVvolIdList);

            if (0 == needDelVirtualVolumes.size()) {
                LOGGER.info("The size of vvols is :" + needDelVirtualVolumes.size());
                response.setResultCode("0");
                response.setResultDescription("del successfully.");
                return response;
            }

            for (int i = 0; i < needDelVirtualVolumes.size(); i++) {
                DelFaultDataResutl delFaultDataResult = new DelFaultDataResutl();
                NVirtualVolume vvol = needDelVirtualVolumes.get(i);
                LOGGER.info("In delete vvol cyclical function," +
                        " the vvolId is : " + vvol.getVvolid());
                String vvolName = vvolMetadataService.getvmNameByVvolId(vvol.getVvolid());
                delFaultDataResult.setVvolName(vvolName);
                delFaultDataResult.setVvolid(vvol.getVvolid());
                delFaultDataResult.setVvolSourceType(vvol.getSourceType());
                List<NVirtualVolume> needDelSunList = discoverService.getVirtualVolumeFromDataBaseByParentId(vvol.getVvolid());

                if (alreadyDealSet.contains(vvol.getVvolid())) {
                    LOGGER.info("the vvolid : " + vvol.getVvolid() + "has been already deal");
                    continue;
                }

                if (null == needDelSunList || needDelSunList.size() == 0) {
                    LOGGER.info("The vvolid " + vvol.getVvolid() + " vvol name is " + vvolName + " do not hava sun list.");


                    if (discoverServiceImpl.isNasVvol(vvol.getVvolid())) {
                        LOGGER.info("===========> nas vvol The delete vvolid is " + vvol.getVvolid());
                        VASAUtil.saveArrayId(vvol.getArrayId());
                        int delVitrualVolumeResult = delNasVvol(vvol.getVvolid());
                        createDelResult(deleteSuccessSet, vvol.getVvolid(), delFaultDataResult, delVitrualVolumeResult);
                        alreadyDealSet.add(vvol.getVvolid());
                        //delFaultDataResultList.add(delFaultDataResult);
                    } else {
                        int delVitrualVolumeResult = delVitrualVolume(vvol.getVvolid(), discoverServiceImpl, discoverService);
                        createDelResult(deleteSuccessSet, vvol.getVvolid(), delFaultDataResult, delVitrualVolumeResult);
                        alreadyDealSet.add(vvol.getVvolid());
                        delFaultDataResultList.add(delFaultDataResult);
                    }
                } else {
                    LOGGER.info("The vvolid : " + vvol.getVvolid() + " vvol name is " + vvolName + "  has sunlist:" + needDelSunList.toString());
                    List<String> delVvolListStatic = new ArrayList<String>();
                    Map<String, String> getSubDelListResult = getSubDelList(vvol, discoverService, reqDelVvolIdList, delVvolListStatic);
                    LOGGER.info("The getSubDelListResult is : " + getSubDelListResult.toString());
                    if (getSubDelListResult.containsKey("1")) {
                        LOGGER.info("The vvolid : " + vvol.getVvolid() + " has sun not in delList.");
                        List<NVirtualVolume> delSunVirtualVolumeVvols = new ArrayList<NVirtualVolume>();
                        List<DeletedFailVolume> deletedFailVolumes = new ArrayList<DeletedFailVolume>();
                        delSunVirtualVolumeVvols = discoverService.getVirtualVolumeFromDataBaseByParentId(vvol.getVvolid());
                        alreadyDealSet.add(vvol.getVvolid());

                        for (NVirtualVolume tempVvol : delSunVirtualVolumeVvols) {
                            DeletedFailVolume deletedFailVolume = new DeletedFailVolume();
                            deletedFailVolume.setVvolName(vvolMetadataService.getvmNameByVvolId(tempVvol.getVvolid()));
                            deletedFailVolume.setVvolType(tempVvol.getSourceType());
                            deletedFailVolume.setVvolId(tempVvol.getVvolid());
                            deletedFailVolumes.add(deletedFailVolume);
                        }
                        delFaultDataResult.setResultCode(VASAResponseCode.virtualVolumeService.STORAGE_VOLUME_IS_PARENT_VOLUME);
                        delFaultDataResult.setDeletedFailVolume(deletedFailVolumes);
                        delFaultDataResult.setResultDescription(VASAResponseCode.virtualVolumeService.STORAGE_VOLUME_IS_PARENT_VOLUME_DESCRIPTION);
                        delFaultDataResultList.add(delFaultDataResult);
                    } else {
                        LOGGER.info("The vvolid : " + vvol.getVvolid() + " has sun all in request delList.");
                        List<String> delVvolList = new ArrayList<String>();
                        for (int j = delVvolListStatic.size() - 1; j >= 0; j--) {
                            delVvolList.add(delVvolListStatic.get(j));
                        }
                        LOGGER.info("The delete list is " + delVvolList.toString());
                        delVvolListStatic.clear();
                        List<NVirtualVolume> sonDelList = discoverService.queryVirtualVolumeFromDataBase(delVvolList);

                        for (String delVvol : delVvolList) {
                            DelFaultDataResutl delFaultDataResultTemp = new DelFaultDataResutl();
                            vvolName = vvolMetadataService.getvmNameByVvolId(delVvol);
                            delFaultDataResultTemp.setVvolName(vvolName);
                            delFaultDataResultTemp.setVvolid(delVvol);
                            delFaultDataResultTemp.setVvolSourceType(vvol.getSourceType());
                            LOGGER.info("The delete vvolid is " + delVvol);

                            if (alreadyDealSet.contains(delVvol)) {
                                LOGGER.info("The vvolid " + delVvol + "has already del");
                                continue;
                            }

                            String vvolSourceTypeTemp = new String();
                            for (NVirtualVolume tempVvol : sonDelList) {
                                if (tempVvol.getVvolid().equalsIgnoreCase(delVvol)) {
                                    vvolSourceTypeTemp = tempVvol.getSourceType();
                                }
                            }
                            delFaultDataResultTemp.setVvolSourceType(vvolSourceTypeTemp);

                            int delVitrualVolumeResult = delVitrualVolume(delVvol, discoverServiceImpl, discoverService);
                            createDelResult(deleteSuccessSet, delVvol, delFaultDataResultTemp, delVitrualVolumeResult);
                            alreadyDealSet.add(delVvol);
                            delFaultDataResultList.add(delFaultDataResultTemp);
                        }
                    }
                }
                if (needDelSunList != null) {
                    needDelSunList.clear();
                }
            }

            if (deleteSuccessSet.size() == needDelVirtualVolumes.size()) {
                //鍒犲嵎鍏ㄩ儴鎴愬姛
                response.setResultCode("0");
                LOGGER.info("Del vvol volume all success");
                response.setResultDescription("del successfully");
            } else if (deleteSuccessSet.size() > 0 && deleteSuccessSet.size() < needDelVirtualVolumes.size()) {
                //鍒犲嵎閮ㄥ垎鎴愬姛
                response.setResultCode("2");
                response.setResultDescription("Not all delete success.");
                LOGGER.info("Del vvol volume not all success");
            } else {
                response.setResultCode("1");
                response.setResultDescription("Del vvol volume all fail");
                LOGGER.info("Del vvol volume all fail");
            }
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("delete fault error", e);
        }
        return response;
    }

    private int delNasVvol(String delVvol) {
        int result = -1;
        try {
            LOGGER.info("==========>  delNasVvol " + delVvol);
            String arrayId = VASAUtil.getArrayId();
            NVirtualVolume nVirtualVolume = virtualVolumeService.getVirtualVolumeByVvolId(delVvol);

            try {
                result = DiscoverServiceImpl.getInstance().deleteVirtualVolume(nVirtualVolume, true);
            } catch (VasaProviderBusy vasaProviderBusy) {
                LOGGER.error("==========>  del failed " + vasaProviderBusy);
            } catch (ResourceInUse resourceInUse) {
                LOGGER.error("==========>  del failed " + resourceInUse);
            }
            if (result == 0) {
                LOGGER.info("==========>  del success " + delVvol);
                return result;
            }
            LOGGER.info("==========>  del failed " + delVvol);
        } catch (StorageFault storageFault) {
            LOGGER.error("==========>  del failed " + storageFault);
        }
        return result;
    }

    private void createDelResult(HashSet<String> deleteSuccessSet,
                                 String vvolID, DelFaultDataResutl delFaultDataResult,
                                 int delVitrualVolumeResult) {
        if (delVitrualVolumeResult == 0) {
            delFaultDataResult.setResultCode("0");
            delFaultDataResult.setResultDescription("Delete successfully");
            deleteSuccessSet.add(vvolID);
        } else if (delVitrualVolumeResult == 3) {
            delFaultDataResult.setResultCode(VASAResponseCode.virtualVolumeService.STORAGE_VOLUME_IN_BAND_STATUTE);
            delFaultDataResult.setResultDescription("Del vvol fail,vvol is in bound state.");
        } else {
            delFaultDataResult.setResultCode("1");
            delFaultDataResult.setResultDescription("Delete fail,please try again later.");
        }
    }
}
