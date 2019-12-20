/*
 *
 *  * // Copyright 2019 The OpenSDS Authors.
 *  * //
 *  * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  * // not use this file except in compliance with the License. You may obtain
 *  * // a copy of the License at
 *  * //
 *  * //     http://www.apache.org/licenses/LICENSE-2.0
 *  * //
 *  * // Unless required by applicable law or agreed to in writing, software
 *  * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * // License for the specific language governing permissions and limitations
 *  * // under the License.
 *  *
 *
 */

package org.opensds.platform.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public abstract class FileAttributeUtility
{
    private static final Logger LOGGER = LogManager.getLogger(FileAttributeUtility.class);
    
    @SuppressWarnings("unchecked")
    public static FileAttribute<Set<PosixFilePermission>> getDefaultFileAttribute(
            File file, boolean isReadShare)
    {
        Path path = file.toPath();
        // File permissions should be such that only user may read/write file
        FileAttribute<?> fa = null;
        boolean isPosix = FileSystems.getDefault()
                .supportedFileAttributeViews()
                .contains("posix");
        if (isPosix)
        { //linux 平台权限控制
            String permissons = isReadShare ? "rw-r-----" : "rw-------";
            Set<PosixFilePermission> perms = PosixFilePermissions
                    .fromString(permissons);
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions
                    .asFileAttribute(perms);
            fa = attr;
        }
        else
        { // Windows 平台权限控制
              // for not posix must support ACL, or failed.
//            String userName = System.getProperty("user.name");
            UserPrincipal user = null;
            try
            {
                user = path.getFileSystem()
                        .getUserPrincipalLookupService()
                        .lookupPrincipalByName("Everyone");
            }
            catch (IOException e)
            {
                LOGGER.error(e, e);
            }
            AclEntryPermission[] permList = new AclEntryPermission[] {
                    AclEntryPermission.READ_DATA,
                    AclEntryPermission.READ_ATTRIBUTES,
                    AclEntryPermission.READ_NAMED_ATTRS,
                    AclEntryPermission.READ_ACL, AclEntryPermission.WRITE_DATA,
                    AclEntryPermission.DELETE, AclEntryPermission.APPEND_DATA,
                    AclEntryPermission.WRITE_ATTRIBUTES,
                    AclEntryPermission.WRITE_NAMED_ATTRS,
                    AclEntryPermission.WRITE_ACL,
                    AclEntryPermission.SYNCHRONIZE};
            Set<AclEntryPermission> perms = EnumSet
                    .noneOf(AclEntryPermission.class);
            for (AclEntryPermission perm : permList)
            {
                perms.add(perm);
            }
            
            final AclEntry entry = AclEntry.newBuilder()
                    .setType(AclEntryType.ALLOW)
                    .setPrincipal(user)
                    .setPermissions(perms)
                    .setFlags(new AclEntryFlag[] {AclEntryFlag.FILE_INHERIT,
                            AclEntryFlag.DIRECTORY_INHERIT})
                    .build();
            
            FileAttribute<List<AclEntry>> aclattrs = null;
            aclattrs = new FileAttribute<List<AclEntry>>()
            {
                public String name()
                {
                    return "acl:acl";
                } /* Windows ACL */
                
                public List<AclEntry> value()
                {
                    ArrayList<AclEntry> l = new ArrayList<AclEntry>();
                    l.add(entry);
                    return l;
                }
            };
            fa = aclattrs;
        }
        
        return (FileAttribute<Set<PosixFilePermission>>)fa;
    }
    
	public static OutputStream getSafeOutputStream(String filePath, boolean isGroupReadShare) throws IOException {
		 /*
         * CodeDex:  FORTIFY.HW_-_Path_Manipulation      by nWX285177
         */
		File file = new File(PathUtils.FilePathFormatWithEncode(filePath, "UTF-8"));
		Path path = file.toPath();
		FileAttribute<Set<PosixFilePermission>> attr = getDefaultFileAttribute(file, isGroupReadShare);
		EnumSet<StandardOpenOption> localEnumSet = EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		Files.newByteChannel(path, localEnumSet, attr).close();
		OutputStream out = Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		return out;
	}
	public static OutputStream getSafeOutputStreamWithoutClear(String filePath, boolean isGroupReadShare) throws IOException {
		/*
		 * CodeDex:  FORTIFY.HW_-_Path_Manipulation      by nWX285177
		 */
		File file = new File(PathUtils.FilePathFormatWithEncode(filePath, "UTF-8"));
		Path path = file.toPath();
		FileAttribute<Set<PosixFilePermission>> attr = getDefaultFileAttribute(file, isGroupReadShare);
		EnumSet<StandardOpenOption> localEnumSet = EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		Files.newByteChannel(path, localEnumSet, attr).close();
		OutputStream out = Files.newOutputStream(path, StandardOpenOption.WRITE);
		return out;
	}
    
    public static SeekableByteChannel getSeekableByteChannel(File file,
            boolean isGroupReadShare) throws IOException
    {
        Path path = file.toPath();
        FileAttribute<Set<PosixFilePermission>> attr = getDefaultFileAttribute(
                file, isGroupReadShare);
        EnumSet<StandardOpenOption> localEnumSet = EnumSet
                .of(StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        SeekableByteChannel sbc = Files.newByteChannel(path,
                localEnumSet,
                attr);
        return sbc;
    }
    
}