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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.VASAResponseCode;

import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.ArrayPwdAES128Util;
import org.opensds.platform.common.utils.Base64Utils;
import org.opensds.vasa.vasa.db.model.NUser;
import org.opensds.vasa.vasa.db.service.UserManagerService;
import org.opensds.vasa.vasa.rest.bean.ModifyUserInfoRequestBean;
import org.opensds.vasa.vasa.rest.bean.QueryUserListResponse;
import org.opensds.vasa.vasa.rest.bean.ResponseHeader;
import org.opensds.vasa.vasa.rest.bean.UserBean;


@Path("vasa/user_manager")
public class UserManagementResource {
    private static final Logger LOGGER = LogManager.getLogger(UserManagementResource.class);

    private UserManagerService userManagerService = ApplicationContextUtil.getBean("userManagerService");

    @GET
    @Path("list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryUserListResponse queryUserList() {
        LOGGER.info("In queryUserList function");

        QueryUserListResponse response = new QueryUserListResponse();

        List<UserBean> userBeans = new ArrayList<UserBean>();
        try {

            List<NUser> userList = userManagerService.getUserList();
            for (NUser nUser : userList) {
                UserBean userBean = new UserBean();
                List<NUser> userHistoryList = userManagerService.getHistoricRecordByUsername(nUser.getUsername());
                userBean.setId(nUser.getId());
                userBean.setUsername(nUser.getUsername());
                userBean.setIpAddress(nUser.getIpAddress());
                userBean.setLastLogin(nUser.getLastLogin());
                userBean.setLastChangePass(nUser.getLastChangePass());
                userBean.setCreatedTime(nUser.getCreatedTime());
                userBean.setUpdatedTime(nUser.getUpdatedTime());
                userBean.setDeletedTime(nUser.getDeletedTime());
                userBean.setPassword(nUser.getPassword());
                userBean.setHistory(userHistoryList);
                userBeans.add(userBean);
            }
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
            response.setUserBeans(userBeans);

        } catch (Exception e) {
            LOGGER.error("query user list fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        LOGGER.debug("End queryUserList function, the response is : " + response.toString());
        return response;
    }

    @POST
    @Path("modify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader modifyUserPassword(ModifyUserInfoRequestBean modifyUserInfoReqBean) {
        LOGGER.info("In modifyUserPassword function, the request is " + modifyUserInfoReqBean.toString());
        ResponseHeader response = new ResponseHeader();
        if (StringUtils.isEmpty(modifyUserInfoReqBean.getUsername()) || StringUtils.isEmpty(modifyUserInfoReqBean.getPassword())) {
            LOGGER.error("invalid parameter.");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        String username = modifyUserInfoReqBean.getUsername();
        String newPassword = modifyUserInfoReqBean.getPassword();

        try {
            List<NUser> userList = userManagerService.getUserInfoByUsername(username);
            if (userList.size() == 0) {
                response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
                response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
                return response;
            }

            NUser user = userList.get(0);
            user.setDeletedTime(new Timestamp(System.currentTimeMillis()));
            user.setDeleted(true);
            NUser newUser = new NUser();
            newUser.setUsername(modifyUserInfoReqBean.getUsername());

            String decodePassword = "";
            // remove Encryption
//			if(newUser.getUsername().equals("cloud_admin") || newUser.getUsername().equals("vasa_admin")){
//				LOGGER.debug("the username = "+newUser.getUsername()+" the newPassword=" +newPassword);
//				decodePassword = Base64Utils.encode(ArrayPwdAES128Util.encryptPwd((newUser.getUsername() + newPassword).getBytes("UTF-8")));
//			}else if(newUser.getUsername().equals("admin")){
//				String key = ConfigManager.getInstance().getValue("vasa.cert.encryption.key");
//				Encryption encrytion = EncryptionFactory.getEncyption();
//				decodePassword = encrytion.encode(key, newPassword).getEncryptedPassword();
//			}else{
//				decodePassword = newPassword;
//			}
//
            newUser.setPassword(newPassword);

            if (null != modifyUserInfoReqBean.getIpAddress()) {
                newUser.setIpAddress(modifyUserInfoReqBean.getIpAddress());
            }

            newUser.setLastChangePass(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            newUser.setLastLogin(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            newUser.setCreatedTime(new Timestamp(System.currentTimeMillis()));
            newUser.setDeleted(false);
            userManagerService.save(newUser);
            userManagerService.deleteUser(user);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("modify user password fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        LOGGER.info("End modifyUserPassword function.");
        return response;
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader updateUserInfo(ModifyUserInfoRequestBean modifyUserInfoReqBean) {
        LOGGER.info("In updateUserInfo function, the request is " + modifyUserInfoReqBean.toString());
        ResponseHeader response = new ResponseHeader();

        if (null == modifyUserInfoReqBean.getUsername()) {
            LOGGER.error("invalid parameter.");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        try {
            List<NUser> userList = userManagerService.getUserInfoByUsername(modifyUserInfoReqBean.getUsername());
            if (userList.size() == 0) {
                LOGGER.error("invalid parameters,the userlist size=" + userList.size());
                response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
                response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
                return response;
            }
            NUser nUser = userList.get(0);
            nUser.setUsername(modifyUserInfoReqBean.getUsername());

            if (null != modifyUserInfoReqBean.getPassword()) {
                String decodePassword = "";
                // remove Encryption
//				if(nUser.getUsername().equals("cloud_admin") || nUser.getUsername().equals("vasa_admin")){
//					decodePassword = Base64Utils.encode(ArrayPwdAES128Util.encryptPwd((nUser.getUsername() + modifyUserInfoReqBean.getPassword()).getBytes("UTF-8")));
//				}else if(nUser.getUsername().equals("admin")){
//					String key = ConfigManager.getInstance().getValue("vasa.cert.encryption.key");
//					Encryption encrytion = EncryptionFactory.getEncyption();
//					decodePassword = encrytion.encode(key, nUser.getPassword()).getEncryptedPassword();
//				}else{
//					decodePassword = nUser.getPassword();
//				}
                decodePassword = modifyUserInfoReqBean.getPassword();
                nUser.setPassword(decodePassword);
            }

            if (null != modifyUserInfoReqBean.getIpAddress()) {
                nUser.setIpAddress(modifyUserInfoReqBean.getIpAddress());
            }

            nUser.setLastLogin(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            nUser.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
            userManagerService.updateUserInfo(nUser);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("update user info fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        return response;
    }

    @POST
    @Path("verify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader verifyUname2Upass(Map<String, String> params) {
        LOGGER.info("In verifyUname2Upass function, the params is " + params);
        ResponseHeader response = new ResponseHeader();

        String uname = params.get("username");
        String upass = params.get("password");
        try {
            List<NUser> userList = userManagerService.getUserInfoByUsername(uname);
            if (userList.size() == 0) {
                LOGGER.error("get om user size is 0.");
                response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
                response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
                return response;
            }
            NUser nUser = userList.get(0);

            if (nUser.getPassword().equals(upass)) {
                response.setResultCode(VASAResponseCode.common.SUCCESS);
                response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);

                return response;
            }

            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        } catch (Exception e) {
            LOGGER.error("verifyUname2Upass error.", e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        return response;
    }

    @GET
    @Path("helloworld")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void Helloworld() {
        LOGGER.info("In Helloworld function");
        String username1 = "cloud_admin";


        LOGGER.info("the username=" + username1 + " password=******" + " the encode password=******");
    }
}
