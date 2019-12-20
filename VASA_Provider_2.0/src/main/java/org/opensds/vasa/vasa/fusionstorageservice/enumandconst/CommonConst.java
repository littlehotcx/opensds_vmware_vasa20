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

package org.opensds.vasa.vasa.fusionstorageservice.enumandconst;

public interface CommonConst {

    int MAX_THREAD_NUM = 10;

    long WWN_TO_DEVICE_CLEAN_INTERVAL = 3600 * 24 * 2 * 1000l;

    String CREDENTIAL_FILE_NAME = "credential.properties";

    String CREDENTIAL_USERNAME = "username";

    String CREDENTIAL_PASSWORD = "password";

    String CREDENTIAL_SEPARATOR = "_";

    String JOB_DATA_KEY = "ScheduleBackupJob";

    String VM_GROUP_NAME = "virtualMachineGroup";

    String DELETE_BACKUP_JOB_NAME = "deleteBackupJob";

    String BACKUP_NAME_PREFIX = "NGC";

    String RESTORE_NAME_PREFIX = "RESTORE";

    String LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String LONG_DATE_FORMAT2 = "yyyy_MM_dd_HH_mm_ss";

    String TRIGGER_NAME_PREFIX = "CronTrigger_";

    String DELETE_BACKUP_TRIGGER1 = "delete_schedule_backup";

    String DELETE_BACKUP_TRIGGER2 = "delete_restore_backup";

    String VSPHERE_VMDK_SUFFIX = "vmdk";

    String VSPHERE_VMX_SUFFIX = "vmx";

    String VSPHERE_VMSD_SUFFIX = "vmsd";

    String VSPHERE_VSWAP_SUFFIX = "vswap";

    String VSPHERE_NVRAM_SUFFIX = "nvram";

    String VSPHERE_VMSN_SUFFIX = "vmsn";

    String VSPHERE_RDMP_NAME = "rdmp";

    String VSPHERE_TYPE_TASK = "Task";

    String VSPHERE_TYPE_EXTENSION_MANAGER = "ExtensionManager";

    String VSPHERE_TYPE_HOSTSYSTEM = "HostSystem";

    String VSPHERE_TYPE_DATASTORE = "StorageDatastore";

    String VSPHERE_TYPE_FOLDER = "Folder";

    String VSPHERE_TYPE_DATACENTER = "Datacenter";

    String VSPHERE_TYPE_VIRTUAL_MACHINE = "VirtualMachine";

    String VSPHERE_PROPERTY_RUNTIME = "runtime";

    String VSPHERE_PROPERTY_DATASTORE = "datastore";

    String VSPHERE_PROPERTY_INFO = "info";

    String VSPHERE_PROPERTY_CHILDENTITY = "childEntity";

    String VSPHERE_PROPERTY_CONFIG = "config";

    String VSPHERE_PROPERTY_CONFIGMANAGER = "configManager";

    String VSPHERE_PROPERTY_VMFOLDER = "vmFolder";

    String VSPHERE_PROPERTY_INFO_STATE = "info.state";

    String VSPHERE_PROPERTY_INFO_RESULT = "info.result";

    String VSPHERE_PROPERTY_INFO_ERROR = "info.error";

    String VSPHERE_PROPERTY_LAYOUTEX = "layoutEx";

    String VSPHERE_PROPERTY_PARENT = "parent";

    String VSPHERE_PROPERTY_NAME = "name";

    String VSPHERE_PROPERTY_EXTENSIONLIST = "extensionList";

    String VSPHERE_PROPERTY_BROWSER = "browser";

    String VSPHERE_VM_MOREF_OBJCET = "vmMorObject";

    String VSPHERE_VM_OBJECT_CONTENT = "vmObjectContent";

    String VMFS_DATASTORE = "lunDatastore";

    String NFS_DATASTORE = "nfsDatastore";

    String DATA_CACHE_HOST_LUN_KEY = "HostLuns";

    String DATA_CACHE_FILESYSTEM_IN_POOL_KEY = "FileSystemInPool";

    String[] ENCRYPT_FIELDS = new String[]{"username", "password"};

    String HOSTNAME_PREFIX = "NGC_";
}
