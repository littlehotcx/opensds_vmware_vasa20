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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.common.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.VASAResponseCode;

import org.opensds.vasa.vasa.rest.bean.QueryUserListResponse;
import org.opensds.vasa.vasa.rest.bean.ResponseHeader;
import org.opensds.vasa.vasa.rest.bean.UserBean;

@Path("vasa/users")
public class UserManagerResouce {
    private static final Logger LOGGER = LogManager.getLogger(UserManagerResouce.class);

    //private UserManagerService userManagerService = ApplicationContextUtil.getBean("userManagerService");

    @GET
    @Path("list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryUserListResponse queryUserList() {
        LOGGER.info("In queryUserList function");
        QueryUserListResponse response = new QueryUserListResponse();

        try {
			/*
			List<NUser> userList = userManagerService.getUserList();
			for(NUser nUser : userList){
				UserBean userBean = new UserBean();
				userBean.setId(nUser.getId());
				userBean.setUsername(nUser.getUsername());
				userBean.setPassword(nUser.getPassword());
				userBean.setSalt(nUser.getSalt());
				userBean.setIpAddress(nUser.getIpAddress());
				userBean.setIterationPara(nUser.getIterationPara());
				userBean.setLastChangePass(nUser.getLastChangePass());
				userBean.setCreatedTime(nUser.getCreatedTime());
				userBean.setUpdatedTime(nUser.getUpdatedTime());
				userBean.setDeletedTime(nUser.getDeletedTime());
				List<NUser> userHistoryList = userManagerService.getHistoricRecordByUsername(nUser.getUsername());
				userBean.setHistory(userHistoryList);
				userBeans.add(userBean);
			}
			response.setResultCode(VASAResponseCode.common.SUCCESS);
			response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
			response.setUserBeans(userBeans);
			*/
        } catch (Exception e) {
            LOGGER.error("query user list fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        LOGGER.debug("End queryUserList function, the response is : " + response.toString());
        return response;
    }

    @GET
    @Path("modify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader modifyUserPassword(
            @QueryParam("username") String username,
            @QueryParam("password") String password) {
        LOGGER.info("In modifyUserPassword function, the request parameter username=" + username + " password=******");
        ResponseHeader response = new ResponseHeader();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            LOGGER.error("invalid parameter.");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        try {
			/*
			List<NUser> userList = userManagerService.getUserInfoByUsername(username);
			if(userList.size() == 0){
				response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
				response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
				return response;
			}
			NUser user = userList.get(0);
			if(username.equals("admin")){
				user.setPassword(password);
			}
			user.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
			userManagerService.updateUserInfo(user);
			response.setResultCode(VASAResponseCode.common.SUCCESS);
			response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
			*/
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
    public ResponseHeader updateUserInfo(UserBean userBean) {
        LOGGER.info("In updateUserInfo function, the request is " + userBean.toString());
        ResponseHeader response = new ResponseHeader();
		/*
		if(null == userBean){
			LOGGER.error("invalid parameters.");
			response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
			response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
			return response;
		}
		*/
        try {
			/*
			List<NUser> userList = userManagerService.getUserInfoByUsername(userBean.getUsername());
			if(userList.size() == 0){
				LOGGER.error("invalid parameters,the userlist size="+userList.size());
				response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
				response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
				return response;
			}
			NUser nUser = userList.get(0);
			nUser.setUsername(userBean.getUsername());
			nUser.setSalt(userBean.getSalt());
			nUser.setIpAddress(userBean.getIpAddress());
			nUser.setIterationPara(userBean.getIterationPara());
			nUser.setLastChangePass(userBean.getLastChangePass());
			nUser.setLastLogin(userBean.getLastLogin());
			nUser.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
			userManagerService.updateUserInfo(nUser);
			response.setResultCode(VASAResponseCode.common.SUCCESS);
			response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
			*/
        } catch (Exception e) {
            LOGGER.error("update user info fail : " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        return response;
    }
}
