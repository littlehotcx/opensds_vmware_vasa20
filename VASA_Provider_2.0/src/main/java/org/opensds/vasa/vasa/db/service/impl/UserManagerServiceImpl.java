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

import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.ArrayPwdAES128Util;
import org.opensds.vasa.vasa.db.dao.BaseDao;
import org.opensds.vasa.vasa.db.dao.UserManagerDao;
import org.opensds.vasa.vasa.db.model.NUser;
import org.opensds.vasa.vasa.db.service.UserManagerService;

public class UserManagerServiceImpl extends BaseServiceImpl<NUser> implements UserManagerService {
    public UserManagerServiceImpl(BaseDao<NUser> baseDao) {
        super(baseDao);
        // TODO Auto-generated constructor stub
    }

    private static Logger LOGGER = LogManager.getLogger(UserManagerServiceImpl.class);

    private UserManagerDao userManagerDao;

    @Override
    public List<NUser> getUserList() {
        try {
            List<NUser> userList = userManagerDao.getUserList();
            for (NUser nUser : userList) {
                // remove Encryption
//				String decodePassword = "";
//				if(nUser.getUsername().equals("cloud_admin") || nUser.getUsername().equals("vasa_admin")){
//					decodePassword = ArrayPwdAES128Util.decryptPwd(nUser.getUsername(), nUser.getPassword());
//				}else if(nUser.getUsername().equals("admin")){
//					String key = ConfigManager.getInstance().getValue("vasa.cert.encryption.key");
//					Encryption encrytion = EncryptionFactory.getEncyption();
//					String encryptionKey = encrytion.encode(key, "").getEncryptedKey();
//					decodePassword = encrytion.decode(encryptionKey, nUser.getPassword());
//					LOGGER.debug("key="+key+" decodePassword="+decodePassword+" miwen="+nUser.getPassword());
//				}else{
//					decodePassword = nUser.getPassword();
//				}
                nUser.setPassword(nUser.getPassword());
            }
            return userList;
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("The getUserList fail, e", e);
            throw e;
        }
    }

    @Override
    public List<NUser> getHistoricRecordByUsername(String username) {
        try {
            List<NUser> userList = userManagerDao.getHistoricRecordByUsername(username);
            for (NUser user : userList) {
                // remove Encryption
//				String decodePassword = "";
//				if(user.getUsername().equals("cloud_admin") || user.getUsername().equals("vasa_admin")){
//					decodePassword = ArrayPwdAES128Util.decryptPwd(user.getUsername(), user.getPassword());
//				}else if(user.getUsername().equals("admin")){
//					String key = ConfigManager.getInstance().getValue("vasa.cert.encryption.key");
//					Encryption encrytion = EncryptionFactory.getEncyption();
//					String encryptionKey = encrytion.encode(key, "").getEncryptedKey();
//					decodePassword = encrytion.decode(encryptionKey, user.getPassword());
//				}else{
//					decodePassword = user.getPassword();
//				}
                user.setPassword(user.getPassword());
            }
            return userList;
        } catch (Exception e) {
            LOGGER.error("getHistoricRecordByUsername error.", e);
            throw e;
        }
    }

    @Override
    public void updateUserInfo(NUser user) {
        userManagerDao.updateUserInfo(user);
    }

    @Override
    public List<NUser> getUserInfoByUsername(String username) {
        try {
            List<NUser> userList = userManagerDao.getUserInfoByUsername(username);
            for (NUser user : userList) {
                // remove Encryption
//				String decodePassword = "";
//				if(user.getUsername().equals("cloud_admin") || user.getUsername().equals("vasa_admin")){
//					decodePassword = ArrayPwdAES128Util.decryptPwd(user.getUsername(), user.getPassword());
//				}else if(user.getUsername().equals("admin")){
//					String key = ConfigManager.getInstance().getValue("vasa.cert.encryption.key");
//					Encryption encrytion = EncryptionFactory.getEncyption();
//					String encryptionKey = encrytion.encode(key, "").getEncryptedKey();
//					decodePassword = encrytion.decode(encryptionKey, user.getPassword());
//				}else{
//					decodePassword = user.getPassword();
//				}
                user.setPassword(user.getPassword());
            }
            return userList;
        } catch (Exception e) {
            LOGGER.error("getHistoricRecordByUsername error.", e);
            throw e;
        }
    }

    public UserManagerDao getUserManagerDao() {
        return userManagerDao;
    }

    public void setUserManagerDao(UserManagerDao userManagerDao) {
        this.userManagerDao = userManagerDao;
    }

    @Override
    public void deleteUser(NUser user) {
        // TODO Auto-generated method stub
        userManagerDao.deleteUser(user);
    }
}
