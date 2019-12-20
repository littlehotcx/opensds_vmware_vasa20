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

public class DBackingConfig {
    protected Boolean autoTieringEnabled;

    protected String deduplicationBackingIdentifier;
    protected Long deduplicationEfficiency;
    protected Long performanceOptimizationInterval;

    protected String thinProvisionBackingIdentifier;

    public Boolean isAutoTieringEnabled() {
        return this.autoTieringEnabled;
    }

    public void setAutoTieringEnabled(Boolean paramBoolean) {
        this.autoTieringEnabled = paramBoolean;
    }

    public String getDeduplicationBackingIdentifier() {
        return this.deduplicationBackingIdentifier;
    }

    public void setDeduplicationBackingIdentifier(String paramString) {
        this.deduplicationBackingIdentifier = paramString;
    }

    public Long getDeduplicationEfficiency() {
        return this.deduplicationEfficiency;
    }

    public void setDeduplicationEfficiency(Long paramLong) {
        this.deduplicationEfficiency = paramLong;
    }

    public Long getPerformanceOptimizationInterval() {
        return this.performanceOptimizationInterval;
    }

    public void setPerformanceOptimizationInterval(Long paramLong) {
        this.performanceOptimizationInterval = paramLong;
    }

    public String getThinProvisionBackingIdentifier() {
        return this.thinProvisionBackingIdentifier;
    }

    public void setThinProvisionBackingIdentifier(String paramString) {
        this.thinProvisionBackingIdentifier = paramString;
    }
}
