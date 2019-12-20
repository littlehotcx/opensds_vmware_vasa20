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
package org.opensds.vasa.vasa.util;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.opensds.vasa.common.MagicNumber;

import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.data.xsd.UsageContext;

/**
 * Track the indiviudal Sessions/Contexts connections to SampleVP
 *
 * @author V1R10
 * @version [版本号V001R010C00, 2011-12-14]
 */
public final class SessionContext {
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(SessionContext.class);

    /**
     * seconds
     */
    public static final long DEFAULT_SESSION_TIMEOUT = 1800;

    /**
     * 公共属性INVALID_SESSION_ID
     */
    public static final String INVALID_SESSION_ID = "0";

    /**
     * 修复fortify问题 使用SecureRandom
     */
    private static SecureRandom rand = new SecureRandom();

    private static final int MAX_SESSION_ID = 1000000;

    private String sessionId;

    private String clientAddress;

    private UsageContext usageContext;

    private static final String SESSION_CONTEXT_TAG = "SESSION_CONTEXT_";

    private static final String SESSION_CONTEXT_ID_SET_KEY = "SESSION_ID_SET_KEY";

    /*
     * SessionContext class Constructor
     *
     * @param ca 参数ca
     *
     * @param us 参数us
     */
    private SessionContext(String ca, UsageContext uc) {
        sessionId = generateUniqueRandomId();
        LOGGER.debug("SessionId: " + VASAUtil.replaceSessionId(sessionId) + "added");
        clientAddress = ca;
        usageContext = uc;
    }

    public SessionContext() {

    }


    static class ClearTimeOutSessionTimer extends TimerTask {

        @Override
        public void run() {

            LOGGER.debug("begin clear timeout sessions.");
            for (String sessionId : getSessionContextIDList()) {
                if (!RedisUtil.checkExist(SESSION_CONTEXT_TAG + sessionId)) {
                    LOGGER.info("clear timeout session id :" + sessionId);
                    removeSession(sessionId);
                }
            }

        }

    }

    static {
        new Timer("ClearTimeOutSession-Timer", true).scheduleAtFixedRate(new ClearTimeOutSessionTimer(),
                MagicNumber.INT60 * MagicNumber.INT1000, MagicNumber.INT60 * MagicNumber.INT60 * MagicNumber.INT1000);
    }

    /**
     * 方法 ： equals
     *
     * @param o 方法参数：o
     * @return boolean 返回结果
     */
    @Override
    public boolean equals(Object o) {
        if (null == o) {
            return false;
        }

        if (o instanceof SessionContext) {
            SessionContext sc = (SessionContext) o;
            return this.getSessionId().equals(sc.getSessionId());
        }

        return false;
    }


    private static String generateRandomId() {
        /*
         * pick a random integer evenly distributed between 0 and MAX_SESSION_ID
         * -1
         */
        int nextRandomInt = rand.nextInt(MAX_SESSION_ID);

        /*
         * Add 1 to the random number since 0 is not a valid session id;
         */
        return String.valueOf(nextRandomInt + 1);
    }

    private static String generateUniqueRandomId() {
        String randomId = generateRandomId();
        try {
            while (RedisUtil.checkExist(SESSION_CONTEXT_TAG + randomId)) {
                /*
                 * If this id is already being used, get a different id.
                 */
                randomId = generateRandomId();
            }
        } catch (Exception e) {
            // ignore the failure
            LOGGER.debug("Exception : " + e);
        }
        return randomId;
    }

    /**
     * public methods
     *
     * @return String 返回结果
     */

    public String getSessionId() {
        return sessionId;
    }

    /**
     * 方法 ： getClientAddress
     *
     * @return String 返回结果
     */
    public String getClientAddress() {
        return clientAddress;
    }

    /**
     * 方法 ： getUsageContext
     *
     * @return UsageContext 返回结果
     */
    public UsageContext getUsageContext() {
        return usageContext;
    }

    /**
     * Lookup SessionContext with SessionId. If a SessionContext exists, remove
     * it. Create new SessionContext.
     *
     * @param uc            方法参数：uc
     * @param clientAddress 方法参数：clientAddress
     * @return SessionContext 返回结果
     * @throws StorageFault 异常：StorageFault
     */
    public synchronized static SessionContext createSession(UsageContext uc, String clientAddress) throws StorageFault {

        SessionContext sessionContext = new SessionContext(clientAddress, uc);

        int expiredSeconds = (int) (uc.getSessionTimeoutInSeconds() == null || uc.getSessionTimeoutInSeconds() <= 0
                ? DEFAULT_SESSION_TIMEOUT : uc.getSessionTimeoutInSeconds());

        RedisApi.setStringKeyValue2TTL(SESSION_CONTEXT_TAG + sessionContext.getSessionId(), sessionContext,
                expiredSeconds);

        RedisUtil.RedisSet.addValue(SESSION_CONTEXT_ID_SET_KEY, sessionContext.getSessionId());

        return sessionContext;
    }

    /**
     * Lookup SessionContext from SessionId. Do not create new SessionContext if
     * SessionId is not found.
     *
     * @param sessionId 方法参数：sessionId
     * @return SessionContext 返回结果
     * @throws StorageFault 异常：StorageFault
     */
    public static SessionContext lookupSessionContextBySessionId(String sessionId) throws StorageFault {

        SessionContext sessionContext = RedisApi.getStringOfBeanByKey(SESSION_CONTEXT_TAG + sessionId,
                SessionContext.class);

        refreshSessionTimeOut(sessionContext);

        return sessionContext;
    }

    private static void refreshSessionTimeOut(SessionContext sessionContext) {
        LOGGER.debug("refresh session :" + sessionContext);
        if (sessionContext == null) {
            return;
        }

        Long timeout = sessionContext.getUsageContext().getSessionTimeoutInSeconds();
        RedisApi.setExpired2Seconds(SESSION_CONTEXT_TAG + sessionContext.getSessionId(),
                (int) (timeout == null || timeout <= 0 ? DEFAULT_SESSION_TIMEOUT : timeout));
    }

    /**
     * If any exist, free the resources for the SessionContext associated with
     * the given sessionId.
     *
     * @param sessionId 方法参数：sessionId
     * @throws StorageFault 异常：StorageFault
     */
    public static void removeSession(String sessionId) {
        RedisUtil.clearByKey(SESSION_CONTEXT_TAG + sessionId);

        // session移除时要将session缓存的内存清除
        DataUtil.getInstance().removePEsBySessionId(sessionId);
        DataUtil.getInstance().removeSavedLastAlramId(sessionId);
        DataUtil.getInstance().removeAlarmForVcenter(sessionId);
        DataUtil.getInstance().removeSavedVcenterForMaxAlarmId(sessionId);

        RedisUtil.RedisSet.delValue(SESSION_CONTEXT_ID_SET_KEY, sessionId);

        LOGGER.debug("Session " + VASAUtil.replaceSessionId(sessionId) + " removed.");

    }

    public static List<SessionContext> getSessionContextList() {
        return RedisApi.getListOfBeanStartsWithKey(SESSION_CONTEXT_TAG, SessionContext.class);
    }


    public static Set<String> getSessionContextIDList() {
        return RedisUtil.RedisSet.getValue(SESSION_CONTEXT_ID_SET_KEY);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SessionContext [sessionId=").append(sessionId).append(", clientAddress=").append(clientAddress)
                .append(", usageContext=").append(usageContext).append("]");
        return builder.toString();
    }


}
