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

package org.opensds.vasa.vasa.worker;

import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.service.SecureConnectionService;

class CompeteMasterTask extends TimerTask {

    private static Logger LOGGER = LogManager.getLogger(CompeteMasterTask.class);

    private SecureConnectionService secureConnectionService;

    @Override
    public void run() {
        LOGGER.info("compete master task is run .");
        secureConnectionService.queryCurrentMasterOrCompeteIt();
    }

}
