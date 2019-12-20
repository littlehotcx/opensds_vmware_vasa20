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

package org.opensds.vasa.vasa20.device.dj.convert;

import org.opensds.vasa.domain.model.bean.S2DAlarm;
import org.opensds.vasa.vasa20.device.dj.bean.SAlarm;

public class AlarmCapabilityConvert {
    public S2DAlarm convertSouth2Model(SAlarm southItem) {
        if (null == southItem) {
            return null;
        }
        S2DAlarm modelItem = new S2DAlarm();

        //private tag
        modelItem.setClearName(southItem.getClearName());
        modelItem.setClearTime(southItem.getClearTime());
        modelItem.setConfirmTime(southItem.getConfirmTime());
        modelItem.setDescription(southItem.getDescription());
        modelItem.setDetail(southItem.getDetail());
        modelItem.setEventID(southItem.getEventID());
        modelItem.setEventParam(southItem.getEventParam());
        modelItem.setLevel(southItem.getLevel());
        modelItem.setLocation(southItem.getLocation());
        modelItem.setName(southItem.getName());
        modelItem.setPosition(southItem.getPosition());
        modelItem.setRecoverTime(southItem.getRecoverTime());
        modelItem.setRoom(southItem.getRoom());
        modelItem.setSequence(southItem.getSequence());
        modelItem.setSourceID(southItem.getSourceID());
        modelItem.setSourceType(southItem.getSourceType());
        modelItem.setStartTime(southItem.getStartTime());
        modelItem.setStrEventID(southItem.getStrEventID());
        modelItem.setSuggestion(southItem.getSuggestion());
        modelItem.setType(southItem.getType());

        return modelItem;
    }
}
