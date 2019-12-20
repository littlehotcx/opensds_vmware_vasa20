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

package org.opensds.vasa.vasa20.device.array.fileSystem;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-03-26
 */
public class FileSystemMigrationUpdateResBean {

//    @JsonProperty
//    private String healthStatus;
//
//    @JsonProperty
//    private String runningStatus;
//
//    @JsonProperty
//    private String sourceFsName;
//
//    @JsonProperty
//    private String sourceFsId;
//
//    @JsonProperty
//    private String targetStoragePoolId;
//
//    @JsonProperty
//    private String targetStoragePoolName;
//
//    @JsonProperty
//    private String migrateSpeed;

    @JsonProperty
    private String migrateProgress;

//    @JsonProperty
//    private int startTime;

//    @JsonProperty
//    private int migrateController;
//
//    @JsonProperty
//    private int migrateTmpFsId;
//
//    @JsonProperty
//    private int detailedStatus;
//
//    @JsonProperty
//    private int suggestion;

    public String getMigrateProgress() {
        return migrateProgress;
    }

    public void setMigrateProgress(String migrateProgress) {
        this.migrateProgress = migrateProgress;
    }

    @Override
    public String toString() {
        return "FileSystemMigrationUpdateResBean{" +
//                "healthStatus='" + healthStatus + '\'' +
//                ", runningStatus='" + runningStatus + '\'' +
//                ", sourceFsName='" + sourceFsName + '\'' +
//                ", sourceFsId='" + sourceFsId + '\'' +
//                ", targetStoragePoolId='" + targetStoragePoolId + '\'' +
//                ", targetStoragePoolName='" + targetStoragePoolName + '\'' +
//                ", migrateSpeed='" + migrateSpeed + '\'' +
                ", migrateProgress=" + migrateProgress +
//                ", startTime=" + startTime +
//                ", migrateController=" + migrateController +
//                ", migrateTmpFsId=" + migrateTmpFsId +
//                ", detailedStatus=" + detailedStatus +
//                ", suggestion=" + suggestion +
                '}';
    }

//    public String getHealthStatus() {
//        return healthStatus;
//    }
//
//    public void setHealthStatus(String healthStatus) {
//        this.healthStatus = healthStatus;
//    }
//
//    public String getRunningStatus() {
//        return runningStatus;
//    }
//
//    public void setRunningStatus(String runningStatus) {
//        this.runningStatus = runningStatus;
//    }
//
//    public String getSourceFsName() {
//        return sourceFsName;
//    }
//
//    public void setSourceFsName(String sourceFsName) {
//        this.sourceFsName = sourceFsName;
//    }
//
//    public String getSourceFsId() {
//        return sourceFsId;
//    }
//
//    public void setSourceFsId(String sourceFsId) {
//        this.sourceFsId = sourceFsId;
//    }
//
//    public String getTargetStoragePoolId() {
//        return targetStoragePoolId;
//    }
//
//    public void setTargetStoragePoolId(String targetStoragePoolId) {
//        this.targetStoragePoolId = targetStoragePoolId;
//    }
//
//    public String getTargetStoragePoolName() {
//        return targetStoragePoolName;
//    }
//
//    public void setTargetStoragePoolName(String targetStoragePoolName) {
//        this.targetStoragePoolName = targetStoragePoolName;
//    }
//
//    public String getMigrateSpeed() {
//        return migrateSpeed;
//    }
//
//    public void setMigrateSpeed(String migrateSpeed) {
//        this.migrateSpeed = migrateSpeed;
//    }
//
//    public int getMigrateProgress() {
//        return migrateProgress;
//    }
//
//    public void setMigrateProgress(int migrateProgress) {
//        this.migrateProgress = migrateProgress;
//    }
//
//    public int getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(int startTime) {
//        this.startTime = startTime;
//    }
//
//    public int getMigrateController() {
//        return migrateController;
//    }
//
//    public void setMigrateController(int migrateController) {
//        this.migrateController = migrateController;
//    }
//
//    public int getMigrateTmpFsId() {
//        return migrateTmpFsId;
//    }
//
//    public void setMigrateTmpFsId(int migrateTmpFsId) {
//        this.migrateTmpFsId = migrateTmpFsId;
//    }
//
//    public int getDetailedStatus() {
//        return detailedStatus;
//    }
//
//    public void setDetailedStatus(int detailedStatus) {
//        this.detailedStatus = detailedStatus;
//    }
//
//    public int getSuggestion() {
//        return suggestion;
//    }
//
//    public void setSuggestion(int suggestion) {
//        this.suggestion = suggestion;
//    }
}
