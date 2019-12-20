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

package org.opensds.vasa.vasa.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.opensds.vasa.vasa.rest.resource.ArrayCrtResource;
import org.opensds.vasa.vasa.rest.resource.CertificateSyncResource;
import org.opensds.vasa.vasa.rest.resource.ClearProfileResource;
import org.opensds.vasa.vasa.rest.resource.FaultDataManagerResource;
import org.opensds.vasa.vasa.rest.resource.QueryKeyStoreResource;
import org.opensds.vasa.vasa.rest.resource.StorageContainerResource;
import org.opensds.vasa.vasa.rest.resource.StorageManagerResource;
import org.opensds.vasa.vasa.rest.resource.StoragePoolResource;
import org.opensds.vasa.vasa.rest.resource.StorageProfileLevelResource;
import org.opensds.vasa.vasa.rest.resource.StorageProfileResource;
import org.opensds.vasa.vasa.rest.resource.UnbindVolumeResource;
import org.opensds.vasa.vasa.rest.resource.UpdateP2VResource;
import org.opensds.vasa.vasa.rest.resource.UserManagementResource;
import org.opensds.vasa.vasa.rest.resource.UserManagerResouce;
import org.opensds.vasa.vasa.rest.resource.VasaPropertyResource;
import org.opensds.vasa.vasa.rest.resource.VasaServiceCenterResource;
import org.opensds.vasa.vasa.rest.resource.VvolOwnControllerResource;

public class VASAApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<Class<?>>();
        set.add(UserManagementResource.class);
        set.add(FaultDataManagerResource.class);
        set.add(UnbindVolumeResource.class);
        set.add(ClearProfileResource.class);
        set.add(UpdateP2VResource.class);
        set.add(QueryKeyStoreResource.class);
        set.add(StorageManagerResource.class);
        set.add(StorageContainerResource.class);
        set.add(StoragePoolResource.class);
        set.add(StorageProfileResource.class);
        set.add(CertificateSyncResource.class);
        set.add(VasaServiceCenterResource.class);
        set.add(UserManagerResouce.class);
        set.add(ArrayCrtResource.class);
        set.add(StorageProfileLevelResource.class);
        set.add(VasaPropertyResource.class);
        set.add(VvolOwnControllerResource.class);
        return set;
    }
}
