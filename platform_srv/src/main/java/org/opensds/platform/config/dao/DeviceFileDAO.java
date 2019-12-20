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

package org.opensds.platform.config.dao;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;

import org.opensds.platform.common.bean.config.DeviceConfig;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.config.service.PlatformStorageManagerService;
import org.opensds.platform.domain.PlatformStorageInfo;
import org.opensds.platform.config.dao.itf.IDeviceDAO;


public class DeviceFileDAO extends BaseFileDAO implements IDeviceDAO {
    private static final Logger LOGGER = LogManager.getLogger(DeviceFileDAO.class);

	private Document document;

	private String file;

	private PlatformStorageManagerService platformStorageManagerService = ApplicationContextUtil
			.getBean("platformStorageManagerService");

	public DeviceFileDAO(String file) {
		this.file = file;
		if (file != null) {
			try {
				SAXReader reader = new SAXReader();
				reader.setEncoding("UTF-8");
				document = reader.read(new File(file));
			} catch (DocumentException e) {
				LOGGER.error("", e);
			}
		}
	}

	public DeviceFileDAO(InputStream is, String fileName) {
		this.file = fileName;
		try {
			SAXReader reader = new SAXReader();
			reader.setEncoding("UTF-8");
			document = reader.read(is);
		} catch (DocumentException e) {
			LOGGER.error("", e);
		}
	}

	@Override
	public boolean addDevice(DeviceConfig device) {
		if (!readDeviceFile()) {
			return false;
		}

		Element rootElm = document.getRootElement();
		Element deviceEle = new DefaultElement("device");
		deviceEle.add(buildElement("deviceId", device.getDeviceId()));
		deviceEle.add(buildElement("deviceName", device.getDeviceName()));
		deviceEle.add(buildElement("serviceAccessPoint", device.getServiceAccessPoint()));
		deviceEle.add(buildElement("loginUser", device.getLoginUser()));
		deviceEle.add(buildElement("loginPwd", device.getLoginPwd()));
		deviceEle.add(buildElement("deviceType", device.getDeviceType()));
		deviceEle.add(buildElement("deviceVersion", device.getDeviceVersion()));
		deviceEle.add(buildElement("connMode", device.getConnMode()));
		deviceEle.add(buildElement("reserver1", device.getReserver1() == null ? "" : device.getReserver1()));
		deviceEle.add(buildElement("reserver2", device.getReserver2() == null ? "" : device.getReserver2()));

		rootElm.add(deviceEle);

		try {
			writeXmlFile(document, this.file);
		} catch (IOException e) {
			rootElm.remove(deviceEle);
			LOGGER.error("", e);
			return false;
		}

		return true;
	}

	@Override
	public boolean updateDevice(DeviceConfig device) {
		try {
			PlatformStorageInfo storageInfo = new PlatformStorageInfo();
			storageInfo.setId(device.getDeviceId());
			storageInfo.setPassword(device.getLoginPwd());
			platformStorageManagerService.modifyInfo(storageInfo);

			return true;
		} catch (Exception e) {
			LOGGER.error("updateDevice error.", e);
			return false;
		}
	}

	public boolean updateDeviceFromFile(DeviceConfig device) {
		if (!readDeviceFile()) {
			return false;
		}

		try {
			Element rootElm = document.getRootElement();
			Element element = null;
			for (@SuppressWarnings("unchecked")
			Iterator<Element> it = rootElm.elementIterator(); it.hasNext();) {
				element = it.next();
				if (element.element("deviceId").getTextTrim().equals(device.getDeviceId())) {
					element.element("deviceName").setText(device.getDeviceName());
					element.element("serviceAccessPoint").setText(device.getServiceAccessPoint());
					element.element("loginUser").setText(device.getLoginUser());
					element.element("loginPwd").setText(device.getLoginPwd());
					element.element("deviceType").setText(device.getDeviceType());
					element.element("deviceVersion").setText(device.getDeviceVersion());
					element.element("connMode").setText(device.getConnMode());

					// j00191218 DTS2016110805412
					if (!StringUtils.isEmpty(device.getReserver1())) {
						element.element("reserver1").setText(device.getReserver1());
					}

					if (!StringUtils.isEmpty(device.getReserver2())) {
						element.element("reserver2").setText(device.getReserver2());
					}

					LOGGER.debug("reserver1" + element.element("reserver1").getTextTrim() + ", reserver2"
							+ element.element("reserver2").getTextTrim());

					writeXmlFile(document, this.file);
					return true;
				}
			}
		} catch (IOException e) {
			LOGGER.error("", e);
			return false;
		}
		return false;
	}

	@Override
	public boolean deleteDevice(String deviceId) {
		if (!readDeviceFile()) {
			return false;
		}

		try {
			Element rootElm = document.getRootElement();
			Element element = null;
			for (@SuppressWarnings("unchecked")
			Iterator<Element> it = rootElm.elementIterator(); it.hasNext();) {
				element = it.next();
				if (element.element("deviceId").getTextTrim().equals(deviceId)) {
					rootElm.remove(element);
					writeXmlFile(document, this.file);
					return true;
				}
			}
		} catch (IOException e) {
			LOGGER.error("", e);
			return false;
		}
		return false;
	}

	@Override
	public DeviceConfig getDeviceById(String deviceId) {
		List<DeviceConfig> devices = getAllDevices();
		for (DeviceConfig device : devices) {
			if (device.getDeviceId().equals(deviceId)) {
				return device;
			}
		}

		return null;
	}

	public List<DeviceConfig> getAllDevices() {
		LOGGER.debug("In getAllDevices functions.  vasa provider");
		List<DeviceConfig> devices = new ArrayList<DeviceConfig>();

        List<PlatformStorageInfo> devicesList=null;
        try {
            devicesList = platformStorageManagerService.queryAllArrays();
        }
        catch (Exception e)
        {
            LOGGER.error("getdevice error:"+e);
        }

		LOGGER.debug("The devicesList is : " + devicesList.toString());
		DeviceConfig device = null;
		for (PlatformStorageInfo info : devicesList) {
			device = new DeviceConfig();
			device.setDeviceId(info.getId());
			device.setDeviceName(info.getStoragename());
			String ip = info.getIp();
			if(null != ip && !isIPV4(ip)){
				ip = "["+ip+"]";
			}
			device.setServiceAccessPoint("https://" + ip + ":" + info.getPort() + "/");
			device.setLoginUser(info.getUsername());
			device.setLoginPwd(info.getPassword());
			device.setDeviceType("DJ");
			device.setDeviceVersion(info.getProductversion());
			device.setConnMode("1");
			device.setReserver1("");
			device.setReserver2("");

			devices.add(device);
		}

		return devices;
	}

	public List<DeviceConfig> getAllDevicesFromFile() {
		List<DeviceConfig> devices = new ArrayList<DeviceConfig>();
		if (null == file) {
			return devices;
		}

		SAXReader reader = new SAXReader();
		reader.setEncoding("UTF-8");
		try {
			document = reader.read(new File(file));
		} catch (DocumentException e) {
			LOGGER.error("DeviceFileDAO.getAllDevices() error", e);
		}

		Element rootElm = document.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elements = rootElm.elements("device");
		DeviceConfig device = null;
		for (Element ele : elements) {
			device = new DeviceConfig();
			device.setDeviceId(ele.element("deviceId").getTextTrim());
			device.setDeviceName(ele.element("deviceName").getTextTrim());
			device.setServiceAccessPoint(ele.element("serviceAccessPoint").getTextTrim());
			device.setLoginUser(ele.element("loginUser").getTextTrim());
			device.setLoginPwd(ele.element("loginPwd").getTextTrim());
			device.setDeviceType(ele.element("deviceType").getTextTrim());
			device.setDeviceVersion(ele.element("deviceVersion").getTextTrim());
			device.setConnMode(ele.element("connMode").getTextTrim());
			device.setReserver1(ele.element("reserver1").getTextTrim());
			device.setReserver2(ele.element("reserver2").getTextTrim());

			devices.add(device);
		}

		return devices;
	}

	private boolean readDeviceFile() {
		if (null == file) {
			LOGGER.error("Device file is null");
			return false;
		}

		SAXReader reader = new SAXReader();
		reader.setEncoding("UTF-8");
		try {
			document = reader.read(new File(file));
		} catch (DocumentException e) {
			LOGGER.error("", e);
			return false;
		}

		return true;
	}
	
	private boolean isIPV4(String addr) {
		if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
			return false;
		}

		// 判断IP格式和范围
		String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

		Pattern pat = Pattern.compile(rexp);
		Matcher mat = pat.matcher(addr);
		boolean ipAddress = mat.find();

		return ipAddress;
	}
}
