# OpenSDS VASA Provider Plugin

**********************************************************************************
OpenSDS VASA Provider Plugin 
**********************************************************************************

I. General Information 

    Name:     OpenSDS_Storage_VASA_Provider_V2
    Category: OpenSDS VASA Provider Plugin
    version : 2.0.0
    
II. Description

	VASA Provider 2.0 (hereafter called VASA Provider) is developed by Opensds and meets the VASA standard. 
	It enables vCenter Server to manage storage devices. VASA Provider supports VMware virtual volume (VVol).

III. Supported Software Version
    
    VMware vRealize Operations  6.5/6.6/6.7/7.0/7.5
    VMware vSphere：6.0/6.5/6.7

    
IV.Software Requirements
    
    JRE：1.8
    Mavent: 3.5.4
    apache-cxf: 3.3.4
    
V. Supported Device

    OceanStor 2100V3：
    V300R006C20/V300R006C30/V300R006C50 
    OceanStor 2200V3/2600V3：
    V300R005C00/V300R006C00/V300R006C10/V300R006C20/V300R006C30/V300R006C50
    OceanStor 5300V3/5500V3/5600V3/5800V3/6800V3：
    V300R003C00/V300R003C10/V300R003C20/V300R006C00/V300R006C01/V300R006C10/V300R006C20/V300R006C30/V300R006C50
    OceanStor 18500V3/18800V3：
    V300R003C00/V300R003C10/V300R003C20/V300R006C00/V300R006C01/V300R006C10/V300R006C20/V300R006C30/V300R006C50
    OceanStor 2600F V3/5500F V3/5600F V3/5800F V3/6800F V3/18500F V3/18800F V3:
    V300R006C00/V300R006C10/V300R006C20/V300R006C30/V300R006C50
    
    OceanStor 5500V5 Elite/5110V5：
    V500R007C00/V500R007C10/V500R007C20/V500R007C30
    OceanStor 5300V5/5500V5/5600V5/5800V5/6800V5：
    V500R007C00/V500R007C10/V500R007C20/V500R007C30
    OceanStor 18500V5/18800V5：
    V500R007C00/V500R007C10/V500R007C20/V500R007C30
    OceanStor 5300F V5/5500F V5/5600F V5/5800F V5/6800F V5/18500F V5/18800F V5：
    V500R007C00/V500R007C10/V500R007C20/V500R007C30
    
    OceanStor Dorado6000 V3：
    V300R001C00/V300R001C01/V300R001C20/V300R001C21/V300R001C30/V300R002C00/V300R002C10
    OceanStor Dorado5000 V3：
    V300R001C01//V300R001C20/V300R001C21/V300R001C30/V300R002C00/V300R002C10
    OceanStor Dorado18000 V3：
    V300R001C30/V300R002C00/V300R002C10
    OceanStor Dorado3000 V3：
    V300R002C10
    OceanStor Dorado3000/5000/6000/8000/18000/ V6
    
    OceanStor Dorado NAS：
    V300R002C10
    
VI. Compile notice

    1) Add the vmware sdk to the vasa20/build floder.
    2) generate source code by command line
    3) package vasa20.jar(see vasa20/readme.md)
    4) package platform jar and vasa.jar
    5) get vasa.jar from VASA_Provider_2.0/target/vasa.jar
   
VII. others 
    
    Project files are encoded in GBK.

  

   