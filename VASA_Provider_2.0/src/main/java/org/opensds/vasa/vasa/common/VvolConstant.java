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

package org.opensds.vasa.vasa.common;

import org.opensds.platform.common.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VvolConstant {
    private static Logger LOGGER = LogManager
            .getLogger(VvolConstant.class);

    public static final String VVOL_PREFIX = "rfc4122.";
    public static final long MAX_SEND_DJ = parseMaxSendDjTaskNumber();
    public static final int CLEAR_REMAIN_INTERVAL_TIME = parseCreateRemainIntervalTime();
    public static final long CLEAR_VOLTYPE_INTERVAL_TIME = parseClearVolTypeInterval();
    public static final long CLEAR_VVOL_INTERVAL_TIME = parseClearVVolInterval();
    public static final long MIN_VALUE = 0l;
    public static final long MAX_VALUE = 999999999l;

    private static long parseMaxSendDjTaskNumber() {
        long maxSendDjTasks = 32;
        try {
            maxSendDjTasks = Long.parseLong(ConfigManager.getInstance().getValue("dj_task_max_threshold"));
        } catch (Exception e) {
            LOGGER.error("dj_task_max_threshold config is not NumberFormat, not parse. Current dj_task_max_threshold is: " +
                    ConfigManager.getInstance().getValue("dj_task_max_threshold"));
            ;
        }
        return maxSendDjTasks;
    }

    private static int parseCreateRemainIntervalTime() {
        int clearRemainIntervalTime = 3600000;
        try {
            clearRemainIntervalTime = Integer.parseInt(ConfigManager.getInstance().getValue("clear_remain_interval_time"));
        } catch (Exception e) {
            LOGGER.error("clear_remain_interval_time config is not NumberFormat, not parse. Current clear_remain_interval_time is: " +
                    ConfigManager.getInstance().getValue("clear_remain_interval_time"));
        }
        return clearRemainIntervalTime;
    }

    private static long parseClearVVolInterval() {
        long ClearVVolInterval = 24 * 3600 * 1000;
        try {
            ClearVVolInterval = Long.parseLong(ConfigManager.getInstance().getValue("clear_vvol_interval")) * 60 * 1000;
        } catch (Exception e) {
            LOGGER.error("clear_vvol_interval config is not NumberFormat, not parse. Current clear_vvol_interval is: " +
                    ConfigManager.getInstance().getValue("clear_vvol_interval"));
        }
        return ClearVVolInterval;
    }

    private static long parseClearVolTypeInterval() {
        long ClearVolTypeInterval = 7 * 24 * 3600 * 1000;
        try {
            ClearVolTypeInterval = Long.parseLong(ConfigManager.getInstance().getValue("clear_volType_interval")) * 60 * 1000;
        } catch (Exception e) {
            LOGGER.error("clear_volType_interval config is not NumberFormat, not parse. Current clear_volType_interval is: " +
                    ConfigManager.getInstance().getValue("clear_volType_interval"));
        }
        return ClearVolTypeInterval;
    }
}
