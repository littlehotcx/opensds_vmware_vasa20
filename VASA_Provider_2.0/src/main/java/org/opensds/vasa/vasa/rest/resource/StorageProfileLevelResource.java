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
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.VASAResponseCode;

import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.vasa.vasa.db.model.NStorageProfileLevel;
import org.opensds.vasa.vasa.db.service.ProfileLevelService;
import org.opensds.vasa.vasa.db.service.StorageProfileLevelService;
import org.opensds.vasa.vasa.rest.bean.ResponseHeader;
import org.opensds.vasa.vasa.rest.bean.StorageProfileLevelResult;

@Path("vasa/storageProfileLevel")
public class StorageProfileLevelResource {

    StorageProfileLevelService storageProfileLevelService = ApplicationContextUtil.getBean("storageProfileLevelService");
    ProfileLevelService profileLevelService = ApplicationContextUtil.getBean("profileLevelService");
    private static Logger logger = LogManager.getLogger(StorageProfileLevelResource.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader updateStorageProfileLevel(NStorageProfileLevel profileLevel) {
        ResponseHeader result = new ResponseHeader();
        logger.info("updateStorageProfileLevel profileLevel=" + profileLevel);
        try {

            if (profileLevel.getId() == 0 || null == profileLevel.getType()) {
                logger.error("updateStorageProfileLevel err. id or type is null.profileLevel=" + profileLevel);
                result.setResultCode(VASAResponseCode.common.ERROR);
                result.setResultDescription(VASAResponseCode.common.ERROR_DESC);
                return result;
            }

            if (StringUtils.isEmpty(profileLevel.getLevelProperty()) && profileLevelService.getConfiguredLevelCount(profileLevel.getLevel()) > 0) {
                logger.error("updateStorageProfileLevel err. Property can not be null while userlevel is using . profileLevel=" + profileLevel);
                result.setResultCode(VASAResponseCode.storageProfileLevel.STORAGE_PROFILE_LEVEL_USING);
                result.setResultDescription(VASAResponseCode.storageProfileLevel.STORAGE_PROFILE_LEVEL_USING_DESCRIPTION);
                return result;
            }

            storageProfileLevelService.updateData(profileLevel);
            result.setResultCode(VASAResponseCode.common.SUCCESS);
            result.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("getConfiguredProfileLevel err.", e);
            result.setResultCode(VASAResponseCode.common.ERROR);
            result.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        return result;
    }

    @GET
    @Path("/configuredProfileLevel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public StorageProfileLevelResult getConfiguredProfileLevel(@QueryParam("ids") String ids, @QueryParam("type") String type) {
        logger.info("getConfiguredProfileLevel ids=" + ids + ",type=" + type);
        StorageProfileLevelResult levelResult = new StorageProfileLevelResult();
        List<NStorageProfileLevel> storageProfileLevels = new ArrayList<>();
        try {
            List<String> levelIds = new ArrayList<>();
            if (!StringUtils.isEmpty(ids)) {
                levelIds = Arrays.asList(ids.split(","));
            }
            if (!StringUtils.isEmpty(type)) {
                NStorageProfileLevel profileLevel = new NStorageProfileLevel();
                profileLevel.setType(type);
                storageProfileLevels = storageProfileLevelService.search(profileLevel);
            } else {
                storageProfileLevels = storageProfileLevelService.getAll();
            }
            for (NStorageProfileLevel nStorageProfileLevel : storageProfileLevels) {
                if (!StringUtils.isEmpty(nStorageProfileLevel.getLevelProperty()) &&
                        (levelIds.size() == 0 || (levelIds.size() > 0 && levelIds.contains(String.valueOf(nStorageProfileLevel.getId()))))) {
                    levelResult.addData(nStorageProfileLevel);
                }
            }
            levelResult.setResultCode(VASAResponseCode.common.SUCCESS);
            levelResult.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            // TODO: handle exception
            logger.error("getConfiguredProfileLevel err.", e);
            levelResult.setResultCode(VASAResponseCode.common.ERROR);
            levelResult.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        return levelResult;

    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public StorageProfileLevelResult getAllProfileLevel(@QueryParam("ids") String ids, @QueryParam("type") String type) {
        logger.info("getAllProfileLevel ids=" + ids + ",type=" + type);
        StorageProfileLevelResult levelResult = new StorageProfileLevelResult();
        List<NStorageProfileLevel> storageProfileLevels = new ArrayList<>();
        try {
            List<String> levelIds = new ArrayList<>();
            if (null != ids && !ids.equals("")) {
                levelIds = Arrays.asList(ids.split(","));
            }
            if (null != type && !type.equals("")) {
                NStorageProfileLevel profileLevel = new NStorageProfileLevel();
                profileLevel.setType(type.trim());
                storageProfileLevels = storageProfileLevelService.search(profileLevel);
            } else {
                storageProfileLevels = storageProfileLevelService.getAll();
            }
            logger.info("getAllProfileLevel storageProfileLevels length=" + storageProfileLevels.size());
            for (NStorageProfileLevel nStorageProfileLevel : storageProfileLevels) {
                if (levelIds.size() == 0 || (levelIds.size() > 0 && levelIds.contains(String.valueOf(nStorageProfileLevel.getId())))) {
                    levelResult.addData(nStorageProfileLevel);
                }
            }
            levelResult.setResultCode(VASAResponseCode.common.SUCCESS);
            levelResult.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("getAllProfileLevel err.", e);
            levelResult.setResultCode(VASAResponseCode.common.ERROR);
            levelResult.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        return levelResult;
    }
}
