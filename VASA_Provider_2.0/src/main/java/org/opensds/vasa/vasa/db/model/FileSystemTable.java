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

public class FileSystemTable {
    private String fileSystemName;
    private String id;
    private String maxCapacity;
    private String currentCapacity;
    private String fileCount;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static String MAX_CAPACITY = "0x7FFFFFFF";

    public static String NORMAL = "NORMAL";

    public static String OFFLINE = "OFFLINE";

    public FileSystemTable(String fileSystemName, String id, String maxCapacity, String currentCapacity,
                           String fileCount) {
        super();
        this.fileSystemName = fileSystemName;
        this.id = id;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
        this.fileCount = fileCount;
        this.status = NORMAL;
    }

    public FileSystemTable() {

    }

    public String getFileSystemName() {
        return fileSystemName;
    }

    public void setFileSystemName(String fileSystemName) {
        this.fileSystemName = fileSystemName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaxCapacity() {
        return maxCapacity;
    }

    public String getFileCount() {
        return fileCount;
    }

    public void setFileCount(String fileCount) {
        this.fileCount = fileCount;
    }

    public void setMaxCapacity(String maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(String currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    @Override
    public String toString() {
        return "FileSystemTable [fileSystemName=" + fileSystemName + ", id=" + id + ", maxCapacity=" + maxCapacity
                + ", currentCapacity=" + currentCapacity + ", fileCount=" + fileCount + "status=" + status + "]";
    }


}
