
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

package org.opensds.vasa.vasa.pool.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.domain.model.bean.StoragePolicy;

public class PoolFilterSmartTierService extends AbstractPoolFilterService {

    private static Logger LOGGER = LogManager.getLogger(PoolFilterSmartTierService.class);

    @Override
    public void beforCheckMatch() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean checkPoolIsMatch(S2DStoragePool s2dStoragePool,
                                    StoragePolicy storagePolicy, long sizeInMB) {
        // TODO Auto-generated method stub
        if (null != storagePolicy.getSmartTier() && !storagePolicy.getSmartTier().equals("0")) {
            String tier0capacity = s2dStoragePool.getTier0capacity();
            String tier1capacity = s2dStoragePool.getTier1capacity();
            String tier2capacity = s2dStoragePool.getTier2capacity();
            boolean tier0Null = (null == tier0capacity || tier0capacity.equals("0"));
            boolean tier1Null = (null == tier1capacity || tier1capacity.equals("0"));
            boolean tier2Null = (null == tier2capacity || tier2capacity.equals("0"));
            LOGGER.debug("tier0Null=" + tier0Null + ",tier1Null=" + tier1Null + ",tier2Null=" + tier2Null);
            if ((tier0Null && tier1Null) || (tier1Null && tier2Null) || (tier0Null && tier2Null)
                    || (tier0Null && tier1Null && tier2Null)) {
                LOGGER.info("RawPoolId = " + s2dStoragePool.getID() + " can not support smartTier.");

                //vvol nas authentication must return true
                return false;
                //return true;
            }
        } else {
            if (null != storagePolicy.getDiskType()) {
                String diskTypeValue = getDiskTypeFromStoragePool(s2dStoragePool).get("DiskType");
                if (!diskTypeValue.equalsIgnoreCase(storagePolicy.getDiskType())) {
                    LOGGER.info("RawPoolId = " + s2dStoragePool.getID() + " do not match diskType.");
                    return false;
                    //return true;
                }
            }
            if (null != storagePolicy.getRaidLevel()) {
                String raidLevelValue = getDiskTypeFromStoragePool(s2dStoragePool).get("RaidLevel");
                if (!raidLevelValue.equalsIgnoreCase(storagePolicy.getRaidLevel())) {
                    LOGGER.info("RawPoolId = " + s2dStoragePool.getID() + " do not match raidLevel.");
                    return false;
                    //return true;
                }
            }
        }
        return true;
    }

    @Override
    public void afterCheckMatch(List<S2DStoragePool> result, long sizeInMB) {
        // TODO Auto-generated method stub

    }

    private Map<String, String> getDiskTypeFromStoragePool(S2DStoragePool s2dStoragePool) {
        Map<String, String> resultMap = new HashMap<String, String>();

        StringBuilder diskTypeSB = new StringBuilder();
        Set<String> diskTypeSet = new HashSet<String>();

        StringBuilder raidLevelSB = new StringBuilder();
        List<String> raidLevelList = new ArrayList<String>();

        if (!"0".equals(s2dStoragePool.getTier0disktype())) {
            if ("3".equals(s2dStoragePool.getTier0disktype()) || "10".equals(s2dStoragePool.getTier0disktype())) {
                diskTypeSet.add("SSD");
            }
            setRaidLevel(raidLevelList, s2dStoragePool, 0);
        }
        if (!"0".equals(s2dStoragePool.getTier1disktype())) {
            if ("1".equals(s2dStoragePool.getTier1disktype()) || "8".equals(s2dStoragePool.getTier1disktype())) {
                diskTypeSet.add("SAS");
            }
            setRaidLevel(raidLevelList, s2dStoragePool, 1);
        }
        if (!"0".equals(s2dStoragePool.getTier2disktype())) {
            if ("2".equals(s2dStoragePool.getTier2disktype()) || "4".equals(s2dStoragePool.getTier2disktype()) || "11".equals(s2dStoragePool.getTier2disktype())) {
                diskTypeSet.add("NL_SAS");
            }
            setRaidLevel(raidLevelList, s2dStoragePool, 2);
        }
        Iterator<String> it = diskTypeSet.iterator();
        while (it.hasNext()) {
            String str = it.next();
            diskTypeSB.append(str);
            if (it.hasNext()) {
                diskTypeSB.append("/");
            }
        }
        Iterator<String> iterator = raidLevelList.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            raidLevelSB.append(str);
            if (iterator.hasNext()) {
                raidLevelSB.append("/");
            }
        }
        resultMap.put("DiskType", diskTypeSB.toString());
        resultMap.put("RaidLevel", raidLevelSB.toString());
        return resultMap;

    }

    private void setRaidLevel(List<String> raidLevelSet, S2DStoragePool s2dStoragePool, int choose) {
        switch (choose) {
            case 0:
                if (!"0".equals(s2dStoragePool.getTier0RaidLv())) {
                    LOGGER.debug("The raidLevel0 is " + s2dStoragePool.getTier0RaidLv());
                    if ("1".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 10");
                    }
                    if ("2".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 5");
                    }
                    if ("3".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 0");
                    }
                    if ("4".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 1");
                    }
                    if ("5".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 6");
                    }
                    if ("6".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 50");
                    }
                    if ("7".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 3");
                    }
                }
                break;
            case 1:
                if (!"0".equals(s2dStoragePool.getTier1RaidLv())) {
                    LOGGER.debug("The raidLevel1 is " + s2dStoragePool.getTier1RaidLv());
                    if ("1".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 10");
                    }
                    if ("2".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 5");
                    }
                    if ("3".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 0");
                    }
                    if ("4".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 1");
                    }
                    if ("5".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 6");
                    }
                    if ("6".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 50");
                    }
                    if ("7".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 3");
                    }
                }
                break;
            case 2:
                if (!"0".equals(s2dStoragePool.getTier2RaidLv())) {
                    LOGGER.debug("The raidLevel2 is " + s2dStoragePool.getTier2RaidLv());
                    if ("1".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 10");
                    }
                    if ("2".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 5");
                    }
                    if ("3".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 0");
                    }
                    if ("4".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 1");
                    }
                    if ("5".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 6");
                    }
                    if ("6".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 50");
                    }
                    if ("7".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 3");
                    }
                }
            default:
                break;
        }

    }

}
