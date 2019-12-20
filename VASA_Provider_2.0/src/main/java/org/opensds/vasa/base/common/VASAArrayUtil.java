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

package org.opensds.vasa.base.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VASAArrayUtil {

    public static List<Long> SwitchControlErroCodes = new ArrayList<>();

    static {
        SwitchControlErroCodes.add(1073891076l);
        SwitchControlErroCodes.add(1077936892l);
        SwitchControlErroCodes.add(1077950254l);
        SwitchControlErroCodes.add(1073744699l);
        SwitchControlErroCodes.add(1073744703l);
        SwitchControlErroCodes.add(1073793287l);
    }

    public static Long sectorToByte = 1024 * 1024 * 1024 * 2l;

    public static class RUNNINGSTATUS {
        public static String online = "27";
        public static String offline = "28";
        public static String initing = "53";
    }

    public static class VVOLSTATUS {
        public static String creating = "creating";
        public static String available = "available";
        public static String initing = "initing";
        public static String error_creating = "error_creating";
        public static String deleting = "deleting";
        public static String active = "active";
        public static String inactive = "inactive";
    }

    public static class DATATRANSFERPOLICY {
        public static Map<String, Integer> TRANSFERPOLICY = new HashMap<>();

        static {
            TRANSFERPOLICY.put("no relocation", 0);
            TRANSFERPOLICY.put("automatic relocation", 1);
            TRANSFERPOLICY.put("highest available", 2);
            TRANSFERPOLICY.put("lowest available", 3);
        }
    }

    public static class SnapStatus {
        //未知
        public static String unknown = "0";
        //已激活
        public static String active = "43";
        //正在回滚
        public static String rollBacking = "44";
        //未激活
        public static String inactive = "45";
        //初始化中
        public static String initing = "53";

    }

    public static class LunCopyStatus {
        public static String unknown = "0";
        public static String not_start = "36";
        public static String queuing = "37";
        public static String stop = "38";
        public static String copying = "39";
        public static String complete = "40";
        public static String paused = "41";
    }

    public static class HealthStatus {
        public static String unknown = "0";
        public static String normal = "1";
        public static String faulty = "2";

    }

    public static class ControlPolicy {
        public static String isUpperBound = "Control upper bound";
        public static String isLowerBound = "Control lower bound";
    }

    public static class UserLevelPolicy {
        public static String high = "high";
        public static String medium = "medium";
        public static String low = "low";
    }

    public static class ServiceTypePolicy {
        public static String critical = "critical";
        public static String normal = "normal";
        public static String noncritical = "noncritical";
    }

    public static class QosStatus {
        public static String CREATED = "created";
        public static String ACTIVE = "active";
    }

    public static class SUBTYPE {
        public static Integer commmonLUN = 0;
        public static Integer vvolLUN = 1;
        public static Integer peLUN = 2;
    }

    public enum ALLOCTYPE {
        Thick("Thick", "0"),
        Thin("Thin", "1");
        private String name;
        private String value;

        ALLOCTYPE(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }
	
	/*public enum DATATRANSFERPOLICY{
		NoRelocation("no relocation","0"),
		AutRecolation("automatic relocation","1"),
		HiAvailable("highest available","2"),
		LowAvailable("lowest available","3");
		private String name;
		private String value;
		
		DATATRANSFERPOLICY(String name, String value){
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}*/

    public enum USAGETYPE {
        TRADITIONALLUN("traditional LUN", 0),
        EDEVLUN("eDevLUN ", 1),
        VVOLLUN("VVOL LUN", 2),
        PELUN("PE LUN", 3);
        private String name;
        private Integer value;

        USAGETYPE(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    public enum IOTYPE {
        READIO("Read I/O", "0"),
        WRITEIO("Write I/O", "1"),
        READANDWRITEIO("Read/Write I/Os", "2");
        private String des;
        private String identifier;

        private IOTYPE(String des, String identifier) {
            this.des = des;
            this.identifier = identifier;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }


    }


    public enum CLASSTYPE {
        UPPERTYPE("Control upper bound", "1"),
        LOWERTYPE("Control lower bound", "2");
        private String des;
        private String identifier;

        private CLASSTYPE(String des, String identifier) {
            this.des = des;
            this.identifier = identifier;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

    }

    public static String getClassType(String controlPolicy) {
        // TODO Auto-generated method stub
        for (CLASSTYPE classType : CLASSTYPE.values()) {
            if (classType.getDes().equalsIgnoreCase(controlPolicy)) {
                return classType.getIdentifier();
            }
        }
        return null;
    }

    public static String getIOType(String controlType) {
        for (IOTYPE ioType : IOTYPE.values()) {
            if (ioType.getDes().equalsIgnoreCase(controlType)) {
                return ioType.getIdentifier();
            }
        }
        return null;
    }
}
