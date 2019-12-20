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

package org.opensds.vasa.vasa.rest.bean;

import java.util.Date;

public class VvolOwnController {

    private String vvolName;
    private String vvolType;
    private int vvolSize;
    private String sourceType;
    private String vvolCreateTime;
    private String vvolId;
    private String vvolContainerName;
    private String controller;
    private int childrenNum;
    private String status;
    private String vmId;
    private String parentId;
    private String lunId;
    private String arrayId;

    public String getVvolName() {
        return vvolName;
    }

    public void setVvolName(String vvolName) {
        this.vvolName = vvolName;
    }

    public String getVvolType() {
        return vvolType;
    }

    public void setVvolType(String vvolType) {
        this.vvolType = vvolType;
    }

    public int getVvolSize() {
        return vvolSize;
    }

    public void setVvolSize(int vvolSize) {
        this.vvolSize = vvolSize;
    }

    @Override
    public String toString() {
        return "VvolOwnController [vvolName=" + vvolName + ", vvolType=" + vvolType + ", vvolSize=" + vvolSize
                + ", sourceType=" + sourceType + ", vvolCreateTime=" + vvolCreateTime + ", vvolId=" + vvolId
                + ", vvolContainerName=" + vvolContainerName + ", controller=" + controller + ", childrenNum="
                + childrenNum + ", status=" + status + ", vmId=" + vmId + ", parentId=" + parentId + ", lunId=" + lunId
                + ", arrayId=" + arrayId + "]";
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getVvolCreateTime() {
        return vvolCreateTime;
    }

    public void setVvolCreateTime(String vvolCreateTime) {
        this.vvolCreateTime = vvolCreateTime;
    }

    public String getVvolId() {
        return vvolId;
    }

    public void setVvolId(String vvolId) {
        this.vvolId = vvolId;
    }

    public String getVvolContainerName() {
        return vvolContainerName;
    }

    public void setVvolContainerName(String vvolContainerName) {
        this.vvolContainerName = vvolContainerName;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public int getChildrenNum() {
        return childrenNum;
    }

    public void setChildrenNum(int childrenNum) {
        this.childrenNum = childrenNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVmId() {
        return vmId;
    }

    public void setVmId(String vmId) {
        this.vmId = vmId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getLunId() {
        return lunId;
    }

    public void setLunId(String lunId) {
        this.lunId = lunId;
    }

    public String getArrayId() {
        return arrayId;
    }

    public void setArrayId(String arrayId) {
        this.arrayId = arrayId;
    }


}
