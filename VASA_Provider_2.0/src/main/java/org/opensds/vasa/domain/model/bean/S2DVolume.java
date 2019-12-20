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

package org.opensds.vasa.domain.model.bean;

public class S2DVolume {
    private String id;

    private String name;

    private String description;

    private String created_at;

    private int size;

    private String status;

    private String volume_type;

    private String source_volid;

    private String snapshot_id;

    private S2DVolumeMetaData metadata;

    private String raw_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVolume_type() {
        return volume_type;
    }

    public void setVolume_type(String volume_type) {
        this.volume_type = volume_type;
    }

    public String getSource_volid() {
        return source_volid;
    }

    public void setSource_volid(String source_volid) {
        this.source_volid = source_volid;
    }

    public String getSnapshot_id() {
        return snapshot_id;
    }

    public void setSnapshot_id(String snapshot_id) {
        this.snapshot_id = snapshot_id;
    }

    public S2DVolumeMetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(S2DVolumeMetaData metadata) {
        this.metadata = metadata;
    }

    public String getRaw_id() {
        return raw_id;
    }

    public void setRaw_id(String raw_id) {
        this.raw_id = raw_id;
    }
}
