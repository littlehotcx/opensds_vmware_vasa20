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

import java.util.HashMap;
import java.util.Map;


import org.opensds.vasa.common.MagicNumber;

/**
 * Fault handling
 *
 * @author V1R10
 * @version [版本号V001R010C00, 2011-12-14]
 */
public abstract class FaultUtil {
    private static final String VASA_PACKAGE_NAME = "com.vmware.vim.vasa.";


    private static final Map<String, String> FAULT_MAP = new HashMap<String, String>(MagicNumber.INT24) {
        {
            put(VASA_PACKAGE_NAME + "v20.LostEvent", ".xsd.LostEvent");
            put(VASA_PACKAGE_NAME + "v20.InvalidArgument", ".xsd.InvalidArgument");
            put(VASA_PACKAGE_NAME + "v20.InvalidCertificate", ".xsd.InvalidCertificate");
            put(VASA_PACKAGE_NAME + "v20.NotFound", ".xsd.NotFound");
            put(VASA_PACKAGE_NAME + "v20.NotImplemented", ".xsd.NotImplemented");
            put(VASA_PACKAGE_NAME + "v20.StorageFault", ".xsd.StorageFault");
            put(VASA_PACKAGE_NAME + "v20.LostAlarm", ".xsd.LostAlarm");
            put(VASA_PACKAGE_NAME + "v20.InvalidLogin", ".xsd.InvalidLogin");
            put(VASA_PACKAGE_NAME + "v20.InvalidSession", ".xsd.InvalidSession");
            put(VASA_PACKAGE_NAME + "v20.ActivateProviderFailed", ".xsd.ActivateProviderFailed");
            put(VASA_PACKAGE_NAME + "v20.InactiveProvider", ".xsd.InactiveProvider");
            put(VASA_PACKAGE_NAME + "v20.IncompatibleVolume", ".xsd.IncompatibleVolume");
            put(VASA_PACKAGE_NAME + "v20.InvalidProfile", ".xsd.InvalidProfile");
            put(VASA_PACKAGE_NAME + "v20.InvalidStatisticsContext", ".xsd.InvalidStatisticsContext");
            put(VASA_PACKAGE_NAME + "v20.NotCancellable", ".xsd.NotCancellable");
            put(VASA_PACKAGE_NAME + "v20.NotSupported", ".xsd.NotSupported");
            put(VASA_PACKAGE_NAME + "v20.OutOfResource", ".xsd.OutOfResource");
            put(VASA_PACKAGE_NAME + "v20.PermissionDenied", ".xsd.PermissionDenied");
            put(VASA_PACKAGE_NAME + "v20.ResourceInUse", ".xsd.ResourceInUse");
            put(VASA_PACKAGE_NAME + "v20.SnapshotTooMany", ".xsd.SnapshotTooMany");
            put(VASA_PACKAGE_NAME + "v20.Timeout", ".xsd.Timeout");
            put(VASA_PACKAGE_NAME + "v20.TooMany", ".xsd.TooMany");
            put(VASA_PACKAGE_NAME + "v20.VasaProviderBusy", ".xsd.VasaProviderBusy");
        }
    };

    /**
     * Return the VASA API version that this VP is using in the
     * from "Major Number.Minor Number"
     *
     * @return String 返回结果
     */
    public static String getVasaApiVersion() {
        try {
            String version = getVasaApiVersionInClassNameFormat();
            version = version.replaceFirst("_", "");
            version = version.replace('_', '.');
            return version;
        } catch (Exception e) {
//            LogManager.error("Cannot convert VASA API version: " + e);
            return "unknown";
        }
    }

    /**
     * Return the VASA API version that this VP is using * in the format "_#_#"
     *
     * @return String 返回
     */
    private static String getVasaApiVersionInClassNameFormat() {
        return VASAUtil.VASA_API_VERSION;
    }

    /**
     * 方法 ： getXsdExceptionObject
     * 根据传入的类名 返回对应的Exception 对象
     * 传入：com.vmware.vim.vasa.v20.LostEvent ->com.vmware.vim.vasa.v20.xsd.LostEvent
     *
     * @param className 方法参数：className 传入的类名
     * @return object
     */
    public static Object getXsdExceptionObject(String className) {
        try {

            String faultName = FAULT_MAP.get(className);

            if (faultName != null) {
                String fullClassName = VASA_PACKAGE_NAME + getVasaApiVersionInClassNameFormat() + faultName;
                Class faultClass;
                faultClass = Class.forName(fullClassName);
                Object faultObj = faultClass.newInstance();
                return faultObj;
            }
//            LogManager.debug("Could not add FaultMessage for exception: " + className);
            assert Boolean.FALSE;
        } catch (ClassNotFoundException e) {
//            LogManager.debug("Fault occured during Exception wrapping: " + e);
            assert Boolean.FALSE;
        } catch (InstantiationException e) {
//            LogManager.debug("Fault occured during Exception wrapping: " + e);
            assert Boolean.FALSE;
        } catch (IllegalAccessException e) {
//            LogManager.debug("Fault occured during Exception wrapping: " + e);
            assert Boolean.FALSE;
        }
        return null;
    }

    /**
     * 方法 ： wrap
     *
     * @param e 方法参数：e
     */
//    public static void wrap(java.lang.Exception e)
//    {
//        try
//        {
//            Class exceptionClass = e.getClass();
//            String faultName = FAULT_MAP.get(exceptionClass.getName());
//            
//            if (faultName != null)
//            {
//                String fullClassName = VASA_PACKAGE_NAME
//                        + getVasaApiVersionInClassNameFormat() + faultName;
//                Class faultClass = Class.forName(fullClassName);
//                Object faultObj = faultClass.newInstance();
//                
//                Method[] exceptionMethods = exceptionClass.getDeclaredMethods();
//                for (Method setFaultMethod : exceptionMethods)
//                {
//                    //if (setFaultMethod.getName().startsWith("setFaultMessage"))
//                    if (setFaultMethod.getName().startsWith("set"))
//                    {
//                        setFaultMethod.invoke(e, faultObj);
//                        return;
//                    }
//                }
//            }
//            log.debug("Could not add FaultMessage for exception: "
//                    + exceptionClass);
//            assert Boolean.FALSE;
//        }
//        catch (Exception ee)
//        {
//            log.debug("Fault occured during Exception wrapping: " + ee);
//            assert Boolean.FALSE;
//        }
//    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.LostEvent 返回
     */
    public static com.vmware.vim.vasa.v20.LostEvent lostEvent() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return lostEvent("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.LostEvent 返回
     */
    public static com.vmware.vim.vasa.v20.LostEvent lostEvent(String message,
                                                              Throwable cause) {
        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent(
                message,
                (com.vmware.vim.vasa.v20.xsd.LostEvent) getXsdExceptionObject("com.vmware.vim.vasa.v20.LostEvent"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.LostEvent 返回
     */
    public static com.vmware.vim.vasa.v20.LostEvent lostEvent(String message) {
        return FaultUtil.lostEvent(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.LostEvent 返回
     */
    public static com.vmware.vim.vasa.v20.LostEvent lostEvent(Throwable cause) {
        return FaultUtil.lostEvent("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.InvalidArgument 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidArgument invalidArgument() {
//        com.vmware.vim.vasa.v20.InvalidArgument e = new com.vmware.vim.vasa.v20.InvalidArgument();
//        wrap(e);
//        return e;
        return invalidArgument("", null);
    }

    /**
     * method
     *
     * @param message string
     * @param cause   Throwable
     * @return com.vmware.vim.vasa.v20.InvalidArgument 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidArgument invalidArgument(
            String message, Throwable cause) {
        com.vmware.vim.vasa.v20.InvalidArgument e = new com.vmware.vim.vasa.v20.InvalidArgument(
                message,
                (com.vmware.vim.vasa.v20.xsd.InvalidArgument)
                        getXsdExceptionObject("com.vmware.vim.vasa.v20.InvalidArgument"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.InvalidArgument 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidArgument invalidArgument(
            String message) {
        return FaultUtil.invalidArgument(message, null);
    }

    /**
     * method
     *
     * @param cause Throwable
     * @return com.vmware.vim.vasa.v20.InvalidArgument 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidArgument invalidArgument(
            Throwable cause) {
        return FaultUtil.invalidArgument("", cause);
    }

    /**
     * method
     *
     * @return com.vmware.vim.vasa.v20.InvalidCertificate 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidCertificate invalidCertificate() {
//        com.vmware.vim.vasa.v20.InvalidCertificate e = new com.vmware.vim.vasa.v20.InvalidCertificate();
//        wrap(e);
//        return e;
        return invalidCertificate("", null);
    }

    /**
     * method
     *
     * @param message string
     * @param cause   Throwable
     * @return com.vmware.vim.vasa.v20.InvalidCertificate 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidCertificate invalidCertificate(
            String message, Throwable cause) {
        com.vmware.vim.vasa.v20.InvalidCertificate e = new com.vmware.vim.vasa.v20.InvalidCertificate(
                message,
                (com.vmware.vim.vasa.v20.xsd.InvalidCertificate)
                        getXsdExceptionObject("com.vmware.vim.vasa.v20.InvalidCertificate"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.InvalidCertificate 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidCertificate invalidCertificate(
            String message) {
        return FaultUtil.invalidCertificate(message, null);
    }

    /**
     * method
     *
     * @param cause Throwable
     * @return com.vmware.vim.vasa.v20.InvalidCertificate 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidCertificate invalidCertificate(
            Throwable cause) {
        return FaultUtil.invalidCertificate("", cause);
    }

    /**
     * method
     *
     * @return com.vmware.vim.vasa.v20.NotFound 返回
     */
    public static com.vmware.vim.vasa.v20.NotFound notFound() {
//        com.vmware.vim.vasa.v20.NotFound e = new com.vmware.vim.vasa.v20.NotFound();
//        wrap(e);
//        return e;
        return notFound("", null);
    }

    /**
     * method
     *
     * @param message string
     * @param cause   Throwablee
     * @return com.vmware.vim.vasa.v20.NotFound 返回
     */
    public static com.vmware.vim.vasa.v20.NotFound notFound(String message,
                                                            Throwable cause) {
        com.vmware.vim.vasa.v20.NotFound e = new com.vmware.vim.vasa.v20.NotFound(
                message,
                (com.vmware.vim.vasa.v20.xsd.NotFound)
                        getXsdExceptionObject("com.vmware.vim.vasa.v20.NotFound"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.NotFound 返回
     */
    public static com.vmware.vim.vasa.v20.NotFound notFound(String message) {
        return FaultUtil.notFound(message, null);
    }

    /**
     * method
     *
     * @param cause Throwable
     * @return com.vmware.vim.vasa.v20.NotFound 返回
     */
    public static com.vmware.vim.vasa.v20.NotFound notFound(Throwable cause) {
        return FaultUtil.notFound("", cause);
    }

    /**
     * method
     *
     * @return com.vmware.vim.vasa.v20.NotImplemented 返回
     */
    public static com.vmware.vim.vasa.v20.NotImplemented notImplemented() {
//        com.vmware.vim.vasa.v20.NotImplemented e = new com.vmware.vim.vasa.v20.NotImplemented();
//        wrap(e);
//        return e;
        return notImplemented("", null);
    }

    /**
     * method
     *
     * @param message string
     * @param cause   Throwable
     * @return com.vmware.vim.vasa.v20.NotImplemented 返回
     */
    public static com.vmware.vim.vasa.v20.NotImplemented notImplemented(
            String message, Throwable cause) {
        com.vmware.vim.vasa.v20.NotImplemented e = new com.vmware.vim.vasa.v20.NotImplemented(
                message,
                (com.vmware.vim.vasa.v20.xsd.NotImplemented)
                        getXsdExceptionObject("com.vmware.vim.vasa.v20.NotImplemented"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.NotImplemented 返回
     */
    public static com.vmware.vim.vasa.v20.NotImplemented notImplemented(
            String message) {
        return FaultUtil.notImplemented(message, null);
    }

    /**
     * method
     *
     * @param cause Throwable
     * @return com.vmware.vim.vasa.v20.NotImplemented 返回
     */
    public static com.vmware.vim.vasa.v20.NotImplemented notImplemented(
            Throwable cause) {
        return FaultUtil.notImplemented("", cause);
    }

    /**
     * method
     *
     * @return com.vmware.vim.vasa.v20.StorageFault 返回
     */
    public static com.vmware.vim.vasa.v20.StorageFault storageFault() {
//        com.vmware.vim.vasa.v20.StorageFault e = new com.vmware.vim.vasa.v20.StorageFault();
//        wrap(e);
//        return e;
        return storageFault("", null);
    }

    /**
     * method
     *
     * @param message string
     * @param cause   Throwable
     * @return com.vmware.vim.vasa.v20.StorageFault 返回
     */
    public static com.vmware.vim.vasa.v20.StorageFault storageFault(
            String message, Throwable cause) {
        com.vmware.vim.vasa.v20.StorageFault e = new com.vmware.vim.vasa.v20.StorageFault(
                message,
                (com.vmware.vim.vasa.v20.xsd.StorageFault)
                        getXsdExceptionObject("com.vmware.vim.vasa.v20.StorageFault"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.StorageFault 返回
     */
    public static com.vmware.vim.vasa.v20.StorageFault storageFault(
            String message) {
        return FaultUtil.storageFault(message, null);
    }

    /**
     * method
     *
     * @param cause Throwable
     * @return com.vmware.vim.vasa.v20.StorageFault 返回
     */
    public static com.vmware.vim.vasa.v20.StorageFault storageFault(
            Throwable cause) {
        return FaultUtil.storageFault("", cause);
    }

    /**
     * method
     *
     * @return com.vmware.vim.vasa.v20.LostAlarm 返回
     */
    public static com.vmware.vim.vasa.v20.LostAlarm lostAlarm() {
//        com.vmware.vim.vasa.v20.LostAlarm e = new com.vmware.vim.vasa.v20.LostAlarm();
//        wrap(e);
//        return e;
        return lostAlarm("", null);
    }

    /**
     * method
     *
     * @param message string
     * @param cause   Throwable
     * @return com.vmware.vim.vasa.v20.LostAlarm 返回
     */
    public static com.vmware.vim.vasa.v20.LostAlarm lostAlarm(String message,
                                                              Throwable cause) {
        com.vmware.vim.vasa.v20.LostAlarm e = new com.vmware.vim.vasa.v20.LostAlarm(
                message,
                (com.vmware.vim.vasa.v20.xsd.LostAlarm) getXsdExceptionObject("com.vmware.vim.vasa.v20.LostAlarm"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.LostAlarm 返回
     */
    public static com.vmware.vim.vasa.v20.LostAlarm lostAlarm(String message) {
        return FaultUtil.lostAlarm(message, null);
    }

    /**
     * method
     *
     * @param cause Throwable
     * @return com.vmware.vim.vasa.v20.LostAlarm 返回
     */
    public static com.vmware.vim.vasa.v20.LostAlarm lostAlarm(Throwable cause) {
        return FaultUtil.lostAlarm("", cause);
    }

    /**
     * method
     *
     * @return com.vmware.vim.vasa.v20.InvalidLogin 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidLogin invalidLogin() {
//        com.vmware.vim.vasa.v20.InvalidLogin e = new com.vmware.vim.vasa.v20.InvalidLogin();
//        wrap(e);
//        return e;
        return invalidLogin("", null);
    }

    /**
     * method
     *
     * @param message string
     * @param cause   Throwable
     * @return com.vmware.vim.vasa.v20.InvalidLogin 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidLogin invalidLogin(
            String message, Throwable cause) {
        com.vmware.vim.vasa.v20.InvalidLogin e = new com.vmware.vim.vasa.v20.InvalidLogin(
                message,
                (com.vmware.vim.vasa.v20.xsd.InvalidLogin)
                        getXsdExceptionObject("com.vmware.vim.vasa.v20.InvalidLogin"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.InvalidLogin 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidLogin invalidLogin(
            String message) {
        return FaultUtil.invalidLogin(message, null);
    }

    /**
     * method
     *
     * @param cause Throwable
     * @return com.vmware.vim.vasa.v20.InvalidLogin 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidLogin invalidLogin(
            Throwable cause) {
        return FaultUtil.invalidLogin("", cause);
    }

    /**
     * method
     *
     * @return com.vmware.vim.vasa.v20.InvalidSession 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidSession invalidSession() {
//        com.vmware.vim.vasa.v20.InvalidSession e = new com.vmware.vim.vasa.v20.InvalidSession();
//        wrap(e);
//        return e;
        return invalidSession("", null);
    }

    /**
     * invalidSession method
     *
     * @param message string
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.InvalidSession result
     */
    public static com.vmware.vim.vasa.v20.InvalidSession invalidSession(
            String message, Throwable cause) {
        com.vmware.vim.vasa.v20.InvalidSession e = new com.vmware.vim.vasa.v20.InvalidSession(
                message,
                (com.vmware.vim.vasa.v20.xsd.InvalidSession)
                        getXsdExceptionObject("com.vmware.vim.vasa.v20.InvalidSession"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * invalidSession method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.InvalidSession 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidSession invalidSession(
            String message) {
        return FaultUtil.invalidSession(message, null);
    }

    /**
     * invalidSession method
     *
     * @param cause 参数
     * @return com.vmware.vim.vasa.v20.InvalidSession 返回结果
     */
    public static com.vmware.vim.vasa.v20.InvalidSession invalidSession(Throwable cause) {
        return FaultUtil.invalidSession("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.ActivateProviderFailed 返回
     */
    public static com.vmware.vim.vasa.v20.ActivateProviderFailed activateProviderFailed() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return activateProviderFailed("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.ActivateProviderFailed 返回
     */
    public static com.vmware.vim.vasa.v20.ActivateProviderFailed activateProviderFailed(String message,
                                                                                        Throwable cause) {
        com.vmware.vim.vasa.v20.ActivateProviderFailed e = new com.vmware.vim.vasa.v20.ActivateProviderFailed(
                message,
                (com.vmware.vim.vasa.v20.xsd.ActivateProviderFailed) getXsdExceptionObject("com.vmware.vim.vasa.v20.ActivateProviderFailed"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.ActivateProviderFailed 返回
     */
    public static com.vmware.vim.vasa.v20.ActivateProviderFailed activateProviderFailed(String message) {
        return FaultUtil.activateProviderFailed(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.ActivateProviderFailed 返回
     */
    public static com.vmware.vim.vasa.v20.ActivateProviderFailed activateProviderFailed(Throwable cause) {
        return FaultUtil.activateProviderFailed("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.InactiveProvider 返回
     */
    public static com.vmware.vim.vasa.v20.InactiveProvider inactiveProvider() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return inactiveProvider("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.InactiveProvider 返回
     */
    public static com.vmware.vim.vasa.v20.InactiveProvider inactiveProvider(String message,
                                                                            Throwable cause) {
        com.vmware.vim.vasa.v20.InactiveProvider e = new com.vmware.vim.vasa.v20.InactiveProvider(
                message,
                (com.vmware.vim.vasa.v20.xsd.InactiveProvider) getXsdExceptionObject("com.vmware.vim.vasa.v20.InactiveProvider"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.InactiveProvider 返回
     */
    public static com.vmware.vim.vasa.v20.InactiveProvider inactiveProvider(String message) {
        return FaultUtil.inactiveProvider(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.InactiveProvider 返回
     */
    public static com.vmware.vim.vasa.v20.InactiveProvider inactiveProvider(Throwable cause) {
        return FaultUtil.inactiveProvider("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.IncompatibleVolume 返回
     */
    public static com.vmware.vim.vasa.v20.IncompatibleVolume incompatibleVolume() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return incompatibleVolume("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.IncompatibleVolume 返回
     */
    public static com.vmware.vim.vasa.v20.IncompatibleVolume incompatibleVolume(String message,
                                                                                Throwable cause) {
        com.vmware.vim.vasa.v20.IncompatibleVolume e = new com.vmware.vim.vasa.v20.IncompatibleVolume(
                message,
                (com.vmware.vim.vasa.v20.xsd.IncompatibleVolume) getXsdExceptionObject("com.vmware.vim.vasa.v20.IncompatibleVolume"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.IncompatibleVolume 返回
     */
    public static com.vmware.vim.vasa.v20.IncompatibleVolume incompatibleVolume(String message) {
        return FaultUtil.incompatibleVolume(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.IncompatibleVolume 返回
     */
    public static com.vmware.vim.vasa.v20.IncompatibleVolume incompatibleVolume(Throwable cause) {
        return FaultUtil.incompatibleVolume("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.InvalidProfile 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidProfile invalidProfile() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return invalidProfile("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.InvalidProfile 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidProfile invalidProfile(String message,
                                                                        Throwable cause) {
        com.vmware.vim.vasa.v20.InvalidProfile e = new com.vmware.vim.vasa.v20.InvalidProfile(
                message,
                (com.vmware.vim.vasa.v20.xsd.InvalidProfile) getXsdExceptionObject("com.vmware.vim.vasa.v20.InvalidProfile"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.InvalidProfile 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidProfile invalidProfile(String message) {
        return FaultUtil.invalidProfile(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.InvalidProfile 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidProfile invalidProfile(Throwable cause) {
        return FaultUtil.invalidProfile("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.InvalidStatisticsContext 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidStatisticsContext invalidStatisticsContext() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return invalidStatisticsContext("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.InvalidStatisticsContext 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidStatisticsContext invalidStatisticsContext(String message,
                                                                                            Throwable cause) {
        com.vmware.vim.vasa.v20.InvalidStatisticsContext e = new com.vmware.vim.vasa.v20.InvalidStatisticsContext(
                message,
                (com.vmware.vim.vasa.v20.xsd.InvalidStatisticsContext) getXsdExceptionObject("com.vmware.vim.vasa.v20.InvalidStatisticsContext"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.InvalidStatisticsContext 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidStatisticsContext invalidStatisticsContext(String message) {
        return FaultUtil.invalidStatisticsContext(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.InvalidStatisticsContext 返回
     */
    public static com.vmware.vim.vasa.v20.InvalidStatisticsContext invalidStatisticsContext(Throwable cause) {
        return FaultUtil.invalidStatisticsContext("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.NotCancellable 返回
     */
    public static com.vmware.vim.vasa.v20.NotCancellable notCancellable() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return notCancellable("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.NotCancellable 返回
     */
    public static com.vmware.vim.vasa.v20.NotCancellable notCancellable(String message,
                                                                        Throwable cause) {
        com.vmware.vim.vasa.v20.NotCancellable e = new com.vmware.vim.vasa.v20.NotCancellable(
                message,
                (com.vmware.vim.vasa.v20.xsd.NotCancellable) getXsdExceptionObject("com.vmware.vim.vasa.v20.NotCancellable"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.NotCancellable 返回
     */
    public static com.vmware.vim.vasa.v20.NotCancellable notCancellable(String message) {
        return FaultUtil.notCancellable(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.NotCancellable 返回
     */
    public static com.vmware.vim.vasa.v20.NotCancellable notCancellable(Throwable cause) {
        return FaultUtil.notCancellable("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.NotSupported 返回
     */
    public static com.vmware.vim.vasa.v20.NotSupported notSupported() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return notSupported("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.NotSupported 返回
     */
    public static com.vmware.vim.vasa.v20.NotSupported notSupported(String message,
                                                                    Throwable cause) {
        com.vmware.vim.vasa.v20.NotSupported e = new com.vmware.vim.vasa.v20.NotSupported(
                message,
                (com.vmware.vim.vasa.v20.xsd.NotSupported) getXsdExceptionObject("com.vmware.vim.vasa.v20.NotSupported"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.NotSupported 返回
     */
    public static com.vmware.vim.vasa.v20.NotSupported notSupported(String message) {
        return FaultUtil.notSupported(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.NotSupported 返回
     */
    public static com.vmware.vim.vasa.v20.NotSupported notSupported(Throwable cause) {
        return FaultUtil.notSupported("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.OutOfResource 返回
     */
    public static com.vmware.vim.vasa.v20.OutOfResource outOfResource() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return outOfResource("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.OutOfResource 返回
     */
    public static com.vmware.vim.vasa.v20.OutOfResource outOfResource(String message,
                                                                      Throwable cause) {
        com.vmware.vim.vasa.v20.OutOfResource e = new com.vmware.vim.vasa.v20.OutOfResource(
                message,
                (com.vmware.vim.vasa.v20.xsd.OutOfResource) getXsdExceptionObject("com.vmware.vim.vasa.v20.OutOfResource"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.OutOfResource 返回
     */
    public static com.vmware.vim.vasa.v20.OutOfResource outOfResource(String message) {
        return FaultUtil.outOfResource(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.OutOfResource 返回
     */
    public static com.vmware.vim.vasa.v20.OutOfResource outOfResource(Throwable cause) {
        return FaultUtil.outOfResource("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.PermissionDenied 返回
     */
    public static com.vmware.vim.vasa.v20.PermissionDenied permissionDenied() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return permissionDenied("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.PermissionDenied 返回
     */
    public static com.vmware.vim.vasa.v20.PermissionDenied permissionDenied(String message,
                                                                            Throwable cause) {
        com.vmware.vim.vasa.v20.PermissionDenied e = new com.vmware.vim.vasa.v20.PermissionDenied(
                message,
                (com.vmware.vim.vasa.v20.xsd.PermissionDenied) getXsdExceptionObject("com.vmware.vim.vasa.v20.PermissionDenied"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.PermissionDenied 返回
     */
    public static com.vmware.vim.vasa.v20.PermissionDenied permissionDenied(String message) {
        return FaultUtil.permissionDenied(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.PermissionDenied 返回
     */
    public static com.vmware.vim.vasa.v20.PermissionDenied permissionDenied(Throwable cause) {
        return FaultUtil.permissionDenied("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.ResourceInUse 返回
     */
    public static com.vmware.vim.vasa.v20.ResourceInUse resourceInUse() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return resourceInUse("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.ResourceInUse 返回
     */
    public static com.vmware.vim.vasa.v20.ResourceInUse resourceInUse(String message,
                                                                      Throwable cause) {
        com.vmware.vim.vasa.v20.ResourceInUse e = new com.vmware.vim.vasa.v20.ResourceInUse(
                message,
                (com.vmware.vim.vasa.v20.xsd.ResourceInUse) getXsdExceptionObject("com.vmware.vim.vasa.v20.ResourceInUse"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.ResourceInUse 返回
     */
    public static com.vmware.vim.vasa.v20.ResourceInUse resourceInUse(String message) {
        return FaultUtil.resourceInUse(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.ResourceInUse 返回
     */
    public static com.vmware.vim.vasa.v20.ResourceInUse resourceInUse(Throwable cause) {
        return FaultUtil.resourceInUse("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.SnapshotTooMany 返回
     */
    public static com.vmware.vim.vasa.v20.SnapshotTooMany snapshotTooMany() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return snapshotTooMany("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.SnapshotTooMany 返回
     */
    public static com.vmware.vim.vasa.v20.SnapshotTooMany snapshotTooMany(String message,
                                                                          Throwable cause) {
        com.vmware.vim.vasa.v20.SnapshotTooMany e = new com.vmware.vim.vasa.v20.SnapshotTooMany(
                message,
                (com.vmware.vim.vasa.v20.xsd.SnapshotTooMany) getXsdExceptionObject("com.vmware.vim.vasa.v20.SnapshotTooMany"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.SnapshotTooMany 返回
     */
    public static com.vmware.vim.vasa.v20.SnapshotTooMany snapshotTooMany(String message) {
        return FaultUtil.snapshotTooMany(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.SnapshotTooMany 返回
     */
    public static com.vmware.vim.vasa.v20.SnapshotTooMany snapshotTooMany(Throwable cause) {
        return FaultUtil.snapshotTooMany("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.Timeout 返回
     */
    public static com.vmware.vim.vasa.v20.Timeout timeout() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return timeout("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.Timeout 返回
     */
    public static com.vmware.vim.vasa.v20.Timeout timeout(String message,
                                                          Throwable cause) {
        com.vmware.vim.vasa.v20.Timeout e = new com.vmware.vim.vasa.v20.Timeout(
                message,
                (com.vmware.vim.vasa.v20.xsd.Timeout) getXsdExceptionObject("com.vmware.vim.vasa.v20.Timeout"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.Timeout 返回
     */
    public static com.vmware.vim.vasa.v20.Timeout timeout(String message) {
        return FaultUtil.timeout(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.Timeout 返回
     */
    public static com.vmware.vim.vasa.v20.Timeout timeout(Throwable cause) {
        return FaultUtil.timeout("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.TooMany 返回
     */
    public static com.vmware.vim.vasa.v20.TooMany tooMany() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return tooMany("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.TooMany 返回
     */
    public static com.vmware.vim.vasa.v20.TooMany tooMany(String message,
                                                          Throwable cause) {
        com.vmware.vim.vasa.v20.TooMany e = new com.vmware.vim.vasa.v20.TooMany(
                message,
                (com.vmware.vim.vasa.v20.xsd.TooMany) getXsdExceptionObject("com.vmware.vim.vasa.v20.TooMany"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.TooMany 返回
     */
    public static com.vmware.vim.vasa.v20.TooMany tooMany(String message) {
        return FaultUtil.tooMany(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.TooMany 返回
     */
    public static com.vmware.vim.vasa.v20.TooMany tooMany(Throwable cause) {
        return FaultUtil.tooMany("", cause);
    }

    /**
     * 方法
     *
     * @return com.vmware.vim.vasa.v20.VasaProviderBusy 返回
     */
    public static com.vmware.vim.vasa.v20.VasaProviderBusy vasaProviderBusy() {
//        com.vmware.vim.vasa.v20.LostEvent e = new com.vmware.vim.vasa.v20.LostEvent();
//        wrap(e);
//        return e;
        return vasaProviderBusy("", null);
    }

    /**
     * 方法
     *
     * @param message 参数
     * @param cause   参数
     * @return com.vmware.vim.vasa.v20.VasaProviderBusy 返回
     */
    public static com.vmware.vim.vasa.v20.VasaProviderBusy vasaProviderBusy(String message,
                                                                            Throwable cause) {
        com.vmware.vim.vasa.v20.VasaProviderBusy e = new com.vmware.vim.vasa.v20.VasaProviderBusy(
                message,
                (com.vmware.vim.vasa.v20.xsd.VasaProviderBusy) getXsdExceptionObject("com.vmware.vim.vasa.v20.VasaProviderBusy"),
                cause);
        //wrap(e);
        return e;
    }

    /**
     * method
     *
     * @param message string
     * @return com.vmware.vim.vasa.v20.VasaProviderBusy 返回
     */
    public static com.vmware.vim.vasa.v20.VasaProviderBusy vasaProviderBusy(String message) {
        return FaultUtil.vasaProviderBusy(message, null);
    }

    /**
     * method
     *
     * @param cause args
     * @return com.vmware.vim.vasa.v20.VasaProviderBusy 返回
     */
    public static com.vmware.vim.vasa.v20.VasaProviderBusy vasaProviderBusy(Throwable cause) {
        return FaultUtil.vasaProviderBusy("", cause);
    }
}
