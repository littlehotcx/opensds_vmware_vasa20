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

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-03-25
 */
public class FileSystemMigrationReqBean {
    private int type;

    private int sourceFsID;

    private String presentMigrateSnapname;

    private int breakAfterMigrate;

    private int createSnapAfterMigrate;

    private String migrateTmpFsName;

    private int migrateType;

    private String targetStoragePoolID;

    //   private String MIGRATECONTROLLER;

    private String migrateSpeed;

    @Override
    public String toString() {
        return "FileSystemMigrationReqBean [type=" + type + ", sourceFsID=" + sourceFsID + ", presentMigrateSnapname="
                + presentMigrateSnapname + ", breakAfterMigrate=" + breakAfterMigrate + ", createSnapAfterMigrate="
                + createSnapAfterMigrate + ", migrateTmpFsName=" + migrateTmpFsName + ", migrateType=" + migrateType
                + ", targetStoragePoolID=" + targetStoragePoolID + ", migrateSpeed=" + migrateSpeed + "]";
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSourceFsID() {
        return sourceFsID;
    }

    public void setSourceFsID(int sourceFsID) {
        this.sourceFsID = sourceFsID;
    }

    public String getPresentMigrateSnapname() {
        return presentMigrateSnapname;
    }

    public void setPresentMigrateSnapname(String presentMigrateSnapname) {
        this.presentMigrateSnapname = presentMigrateSnapname;
    }

    public int getBreakAfterMigrate() {
        return breakAfterMigrate;
    }

    public void setBreakAfterMigrate(int breakAfterMigrate) {
        this.breakAfterMigrate = breakAfterMigrate;
    }

    public int getCreateSnapAfterMigrate() {
        return createSnapAfterMigrate;
    }

    public void setCreateSnapAfterMigrate(int createSnapAfterMigrate) {
        this.createSnapAfterMigrate = createSnapAfterMigrate;
    }

    public String getMigrateTmpFsName() {
        return migrateTmpFsName;
    }

    public void setMigrateTmpFsName(String migrateTmpFsName) {
        this.migrateTmpFsName = migrateTmpFsName;
    }

    public int getMigrateType() {
        return migrateType;
    }

    public void setMigrateType(int migrateType) {
        this.migrateType = migrateType;
    }

    public String getTargetStoragePoolID() {
        return targetStoragePoolID;
    }

    public void setTargetStoragePoolID(String targetStoragePoolID) {
        this.targetStoragePoolID = targetStoragePoolID;
    }

    public String getMigrateSpeed() {
        return migrateSpeed;
    }

    public void setMigrateSpeed(String migrateSpeed) {
        this.migrateSpeed = migrateSpeed;
    }


}
