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

package org.opensds.vasa.vasa.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vmware.vim.vasa.v20.data.xsd.BaseStorageEntity;
import com.vmware.vim.vasa.v20.data.xsd.HostInitiatorInfo;
import com.vmware.vim.vasa.v20.data.xsd.MountInfo;
import com.vmware.vim.vasa.v20.data.xsd.NameValuePair;

/**
 * 用于axis2转cxf中的list与数组相互转换的工具类
 *
 * @author g00250185
 * @version V100R001C10
 */
public final class ListUtil {
    private ListUtil() {

    }

    /**
     * list2ArrayHostInit
     *
     * @param hostInitiator List<HostInitiatorInfo>
     * @return HostInitiatorInfo[]
     */
    public static HostInitiatorInfo[] list2ArrayHostInit(
            List<HostInitiatorInfo> hostInitiator) {
        if (hostInitiator != null) {
            return hostInitiator.toArray(new HostInitiatorInfo[hostInitiator.size()]);
        }
        return new HostInitiatorInfo[]{};
    }

    /**
     * list ->MountInfo
     *
     * @param mountPoint moutInfo list
     * @return MountInfo[]
     */
    public static MountInfo[] list2ArrayMI(
            List<MountInfo> mountPoint) {
        if (mountPoint != null) {
            return mountPoint.toArray(new MountInfo[mountPoint.size()]);
        }
        return new MountInfo[]{};
    }

    /**
     * list 2 NameValuePair []
     *
     * @param pairs NameValuePair list
     * @return NameValuePair array
     */
    public static NameValuePair[] list2ArrayNV(List<NameValuePair> pairs) {
        if (pairs != null) {
            return pairs.toArray(new NameValuePair[pairs.size()]);
        }
        return new NameValuePair[]{};
    }

    /**
     * clear and add
     *
     * @param list  src list
     * @param <T>   template
     * @param array toaddArray
     */
    public static <T> void clearAndAdd(List<T> list, T[] array) {
        if (array == null) {
            return;
        }
        list.clear();
        list.addAll(Arrays.asList(array));
    }

//    public static void noclearAndAdd(List<String> alternateName,String str){
//        
//    }


    /**
     * list2ArrayBaseEntity
     *
     * @param entities entities
     * @return BaseStorageEntity[]
     */
    public static BaseStorageEntity[] list2ArrayBaseEntity(
            List<BaseStorageEntity> entities) {
        if (entities != null) {
            return entities.toArray(new BaseStorageEntity[entities.size()]);
        }
        return new BaseStorageEntity[]{};
    }

    /**
     * list2ArrayString
     *
     * @param strings list of string
     * @return string[]
     */
    public static String[] list2ArrayString(List<String> strings) {
        if (strings == null) {
            return new String[]{};
        }
        return strings.toArray(new String[strings.size()]);
    }


//    public static List<StorageArray> array2List(StorageArray[] arrays)
//    {
//        if (arrays == null)
//        {
//            return new ArrayList<StorageArray>();
//        }
//        return Arrays.asList(arrays);
//    }


    /**
     * array2list
     *
     * @param objs array of objs
     * @param <T>  template
     * @return list
     */
    public static <T> List<T> array2List(T[] objs) {
        if (objs == null) {
            return new ArrayList<T>(0);
        }
        return Arrays.asList(objs);
    }

}
