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

package org.opensds.vasa.vasa.internal;

import org.opensds.vasa.common.MagicNumber;


/**
 * 描述事件参数列表可选择项目的类
 *
 * @author g00250185
 * @version V100R001C10
 */
public class EventParamEntity {
    private String eventId;

    private String paramIndex;

    private String paramKey;

    private String language;

    /**
     * default constructor
     *
     * @param eventId    事件ID
     * @param paramIndex 参数中的列表位置 格式:PARM##
     * @param paramKey   当    PARM## 的值
     * @param language   语言 默认en
     */
    public EventParamEntity(String eventId, String paramIndex, String paramKey,
                            String language) {
        super();
        this.eventId = eventId;
        this.paramIndex = paramIndex;
        this.paramKey = paramKey;
        this.language = language;
    }

    public String getEventId() {
        return eventId;
    }

    public String getParamIndex() {
        return paramIndex;
    }

    public String getParamKey() {
        return paramKey;
    }

    public String getLanguage() {
        return language;
    }

    /**
     * override hashcode
     *
     * @return hashcode
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = MagicNumber.INT31;
        int result = 1;
        result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
        result = prime * result
                + ((language == null) ? 0 : language.hashCode());
        result = prime * result
                + ((paramIndex == null) ? 0 : paramIndex.hashCode());
        result = prime * result
                + ((paramKey == null) ? 0 : paramKey.hashCode());
        return result;
    }

    /**
     * override equals
     *
     * @param obj obj
     * @return boolean
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EventParamEntity other = (EventParamEntity) obj;
        return equalsDegred(other);
    }

    private boolean equalsDegred(EventParamEntity other) {
        //icp 加上{ }
        if (eventId == null) {
            if (other.eventId != null) {
                return false;
            }

        } else if (!eventId.equals(other.eventId)) {
            return false;
        }
        if (language == null) {
            if (other.language != null) {
                return false;
            }
        } else if (!language.equals(other.language)) {
            return false;
        }
        if (paramIndex == null) {
            if (other.paramIndex != null) {
                return false;
            }
        } else if (!paramIndex.equals(other.paramIndex)) {
            return false;
        }
        return checkParamKey(other);
    }

    private boolean checkParamKey(EventParamEntity other) {
        if (paramKey == null) {
            if (other.paramKey != null) {
                return false;
            }
        } else if (!paramKey.equals(other.paramKey)) {
            return false;
        }
        return true;
    }
}
