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

package org.opensds.vasa.vasa.db.model;

public class NStorageProfileLevel extends BaseData {

    public static String USER_LEVEL_TYPE = "userLevel";

    private Integer id;
    private String type;
    private String level;
    private String levelProperty;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevelProperty() {
        return levelProperty;
    }

    public void setLevelProperty(String levelProperty) {
        this.levelProperty = levelProperty;
    }

    @Override
    public String toString() {
        return "NStorageProfileLevel [id=" + id + ", type=" + type + ", level=" + level + ", levelProperty="
                + levelProperty + "]";
    }

}
