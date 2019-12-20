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

package org.opensds.vasa.vasa.service;

import java.util.List;

import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.util.JaxbUtil;
import org.opensds.vasa.vasa.util.Util;
import org.opensds.vasa.vasa.util.VASAUtil;

import com.vmware.vim.vasa.v20.InvalidProfile;
import com.vmware.vim.vasa.v20.NotFound;
import com.vmware.vim.vasa.v20.NotSupported;
import com.vmware.vim.vasa.v20.OutOfResource;
import com.vmware.vim.vasa.v20.ResourceInUse;
import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.data.policy.capability.provider.xsd.CapabilitySchema;
import com.vmware.vim.vasa.v20.data.policy.compliance.xsd.ComplianceResult;
import com.vmware.vim.vasa.v20.data.policy.compliance.xsd.ComplianceSubject;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.DefaultProfile;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.StorageProfile;
import com.vmware.vim.vasa.v20.data.policy.xsd.ResourceAssociation;
import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskInfo;

public class SPBMService {
    // 单例
    private static SPBMService instance;

    // 日志
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(SPBMService.class);

    private DiscoverService discoverService = DiscoverService.getInstance();

    private SPBMService() {

    }

    /**
     * 单例
     *
     * @return SPBMService [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static synchronized SPBMService getInstance() {
        if (instance == null) {
            instance = new SPBMService();
        }
        return instance;
    }

    /**
     * 方法 ： queryCapabilityProfile
     *
     * @param profileIds 方法参数：profileIds
     * @return List<StorageProfile> 返回结果
     * @throws StorageFault 异常：StorageFault
     * @throws NotFound     异常：NotFound
     */
    public List<StorageProfile> queryCapabilityProfile(String[] profileIds)
            throws NotFound, StorageFault {
        LOGGER.info("queryCapabilityProfile called. request ids size is "
                + (Util.isEmpty(profileIds) ? "0" : profileIds.length) + ", request capabilityid are:"
                + VASAUtil.convertArrayToStr(profileIds));
        List<StorageProfile> returnValues = discoverService.getCapabilityProfileByIds(profileIds);
//    	printResponseQueryCapabilityProfile(returnValues);
        return returnValues;
    }

    /**
     * <打印返回的profile>
     *
     * @param returnValues [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printResponseQueryCapabilityProfile(List<StorageProfile> returnValues) {
        LOGGER.info("queryCapabilityProfile,response size is:" + returnValues.size());
        for (int i = 0; i < returnValues.size(); i++) {
            LOGGER.info("profile[" + i + "]:\n" + JaxbUtil.convertToXml(returnValues.get(i)));
        }
    }

    /**
     * 方法 ： queryCapabilityMetadata
     *
     * @param schemaIds 方法参数：schemaIds
     * @return List<CapabilitySchema> 返回结果
     * @throws StorageFault 异常：StorageFault
     * @throws NotFound     异常：NotFound
     */
    public List<CapabilitySchema> queryCapabilityMetadata(String[] schemaIds) throws NotFound, StorageFault {
        LOGGER.info("queryCapabilityMetadata called. request ids size is "
                + (Util.isEmpty(schemaIds) ? "0" : schemaIds.length) + ", request schemaId are:"
                + VASAUtil.convertArrayToStr(schemaIds));
        List<CapabilitySchema> returnValues = discoverService.getCapabilityMetadataByIds(schemaIds);
        //printResponseQueryCapabilityMetadata(returnValues);
        return returnValues;
    }

    /**
     * <打印返回的CapabilitySchema>
     *
     * @param returnValues [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printResponseQueryCapabilityMetadata(List<CapabilitySchema> returnValues) {
        LOGGER.info("queryCapabilityMetadata,response size is:" + returnValues.size());
        for (int i = 0; i < returnValues.size(); i++) {
            LOGGER.info("CapabilitySchema[" + i + "]:\n" + JaxbUtil.convertToXml(returnValues.get(i)));
        }
    }

    /**
     * 方法 ： queryCapabilityProfileForResource
     *
     * @param resourceIds 方法参数：resourceIds
     * @return List<ResourceAssociation> 返回结果
     * @throws StorageFault 异常：StorageFault
     * @throws NotFound     异常：NotFound
     */
    public List<ResourceAssociation> queryCapabilityProfileForResource(String[] resourceIds) throws StorageFault, NotFound {
        LOGGER.info("queryCapabilityProfileForResource called. request ids size is "
                + (Util.isEmpty(resourceIds) ? "0" : resourceIds.length) + ", request resourceIds are:"
                + VASAUtil.convertArrayToStr(resourceIds));
        List<ResourceAssociation> returnValues = discoverService.queryCapabilityProfileForResource(resourceIds);
        printResponseQueryCapabilityProfileForResource(returnValues);
        return returnValues;
    }

    /**
     * <打印返回的ResourceAssociation>
     *
     * @param returnValues [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printResponseQueryCapabilityProfileForResource(List<ResourceAssociation> returnValues) {
        LOGGER.info("print printResponseQueryCapabilityProfileForResource response begin-----------------");
        LOGGER.info("QueryCapabilityProfileForResource,response size is:" + returnValues.size());
        for (ResourceAssociation association : returnValues) {
            LOGGER.info("resource Id is: " + association.getResourceId() + " | profile Id is: " + association.getProfileId());
        }
        LOGGER.info("print printResponseQueryCapabilityProfileForResource response end-----------------");
    }

    /**
     * 方法 ： queryDefaultProfileForStorageContainer
     *
     * @param containerId 方法参数：containerId
     * @param entityType 方法参数：entityType
     * @throws StorageFault 异常：StorageFault
     * @throws NotFound 异常：NotFound
     * @return List<DefaultProfile> 返回结果
     */
//    public List<DefaultProfile> queryDefaultProfileForStorageContainer(String containerId, List<String> entityType) throws StorageFault
//    {
//    	LOGGER.debug("queryDefaultProfileForStorageContainer called. request container id is : " + containerId);
//    	List<DefaultProfile> returnValues = discoverService.queryDefaultProfileForStorageContainer(containerId, entityType);
//    	printResponseQueryDefaultProfileForStorageContainer(returnValues);
//    	return returnValues;
//    }

    /**
     * <打印返回的profile>
     *
     * @param returnValues [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printResponseQueryDefaultProfileForStorageContainer(List<DefaultProfile> returnValues) {
        LOGGER.debug("print queryDefaultProfileForStorageContainer response begin-----------------");
        LOGGER.debug("queryDefaultProfileForStorageContainer,response size is:" + returnValues.size());
        for (DefaultProfile profile : returnValues) {
            LOGGER.debug("response profile is:" + VASAUtil.convertProfile2String(profile) +
                    "entityType:" + VASAUtil.convertArrayToStr(profile.getEntityType()));
        }
        LOGGER.debug("print queryDefaultProfileForStorageContainer response end-----------------");
    }

    /**
     * 方法 ： queryComplianceResult
     *
     * @param subject 方法参数：subject
     * @return List<DefaultProfile> 返回结果
     * @throws StorageFault 异常：StorageFault
     * @throws NotFound     异常：NotFound
     */
    public List<ComplianceResult> queryComplianceResult(List<ComplianceSubject> subjects) throws NotFound, StorageFault {
        printRequestQueryComplianceResult(subjects);

        if (DiscoverServiceImpl.getInstance().isNasVvol(subjects.get(0).getObjectId().get(0))) {
            LOGGER.error("nas vvol not suppport SPBM");
            throw FaultUtil.storageFault("vvol nas not support SPBM");
        }
        List<ComplianceResult> returnValues = discoverService.queryComplianceResult(subjects);
        printResponseQueryComplianceResult(returnValues);
        return returnValues;
    }

    /**
     * <打印ComplianceSubject>
     *
     * @param subjects [参数说明]
     *                 s
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printRequestQueryComplianceResult(List<ComplianceSubject> subjects) {
        for (ComplianceSubject subject : subjects) {
            LOGGER.info("request profile id is:" + subject.getProfileId() + ", generation id is:" + subject.getGenerationId()
                    + ", objectIds:" + VASAUtil.convertArrayToStr(subject.getObjectId()));
        }
    }

    /**
     * <打印返回的ComplianceResult>
     *
     * @param returnValues [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printResponseQueryComplianceResult(List<ComplianceResult> returnValues) {
        for (ComplianceResult result : returnValues) {
            LOGGER.info("response object id is:" + result.getObjectId() + ", profile id is:" + result.getProfileId()
                    + ", complianceStatus:" + result.getComplianceStatus() + ", profileMismatch:" + result.isProfileMismatch());
        }
    }

    public TaskInfo updateStorageProfileForVirtualVolume(String vvolId, StorageProfile newProfile)
            throws NotFound, StorageFault, NotSupported, ResourceInUse, InvalidProfile, OutOfResource {
        LOGGER.info("updateStorageProfileForVirtualVolume called. vvolId is:" + vvolId
                + ", newProfile is:\n" + VASAUtil.convertProfile2String(newProfile));

        if (null == newProfile) {
            LOGGER.error("InvalidProfile/newProfile is null");
            throw FaultUtil.invalidProfile("newProfile is null");
        }

        //校验profile的有效性
        VASAUtil.checkProfileVaild(newProfile);

        TaskInfo result = discoverService.updateStorageProfileForVirtualVolume(vvolId, newProfile);
        return result;
    }
}
