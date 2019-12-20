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

package org.opensds.vasa.vasa20.device.array.NFSvvol;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-02-28
 */
public class NFSvvolService extends AbstractVASACapability implements INFSvvolService {

    public NFSvvolService(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    private static Logger LOGGER = LogManager.getLogger(NFSvvolService.class);
    private String createNFSvvol_url = "NFS_VVOL";
    private String queryNFSvvol_url = "NFS_VVOL/info?srcPath=";


    // 正式接口
    @Override
    public SDKResult<NFSvvolCreateResBean> operateNFSvvol(String arrayId, String cmd, String srcPath, String dstPath, long filesize, String vstoreId) {
        LOGGER.debug("operate NFS vvol +" + srcPath + " CMD =" + cmd);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("PUT");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        NFSvvolCreateReqBean reqPayload = new NFSvvolCreateReqBean();
        SDKResult<NFSvvolCreateResBean> sdkResult = new SDKResult<>();
        createNFSshareReqBean(reqPayload, cmd, srcPath, dstPath, filesize, vstoreId);
        req.setPayload(reqPayload);
        try {
            LOGGER.info("nfs vvol req:" + reqPayload.toString());
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, createNFSvvol_url, null);

            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            if (response.getErrorCode() == 0) {
                LOGGER.info("operate NFS vvol success");
                //NFSvvolCreateResBean result = (NFSvvolCreateResBean)response.getResData();
                // sdkResult.setResult(result);
            } else {
                LOGGER.error("operate NFS vvol failed:" + response.getDescription());
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("operate NFS vvol fail! NFSvvolCreateReqBean=" + reqPayload, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("creat NFS vvol fail, NFSshareCreateReqBean=" + reqPayload);
            e.printStackTrace();
        }

        return sdkResult;
    }

    @Override
    // srcPath is FileSystem + file path  e.g.srcPath=/fs/a
    public SDKResult<NFSvvolQueryResBean> queryNFSvvol(String arrayId, String vstoreId, String srcPath) {

        LOGGER.debug("query nfs vvol:" + srcPath);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        SDKResult<NFSvvolQueryResBean> sdkResult = new SDKResult<>();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, queryNFSvvol_url + srcPath, "org.opensds.vasa.vasa20.device.array.NFSvvol.NFSvvolQueryResBean");

            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());

            if (response.getErrorCode() == 0) {
                LOGGER.info("query NFS vvol success");

                sdkResult.setResult((NFSvvolQueryResBean) response.getResData());
            } else {
                LOGGER.info("query NFS vvol failed");
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("query NFS vvol fail! NFSvvolQuery url is=" + srcPath, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("query NFS vvol fail!" + e);
            e.printStackTrace();
        }
        //createNFSQueryReqBean(reqvstoreId,srcPath);

        return sdkResult;
    }

    private void createNFSQueryReqBean(String vstoreId, String srcPath) {


    }

    //临时接口 暂用
    private SDKResult<NFSvvolCreateResBean> createNFSvvol(String arrayId, String cmd, String srcPath, String dstPath, String filesize) {
        String IP = "10.10.205.85";

        String share = "A_vasa_file_system_common_f4e5bade_15a2_4805_bf8e_52318c4ce443";

        String command = "mkdir /mnt/" + share;

        String command0 = "mount -t nfs " + IP + ":/" + share + "  /mnt/" + share;

        String command1 = "cd /mnt/" + share;

        String command2 = "dd if=/dev/zero of=" + "/mnt/" + share + "/" + srcPath + " bs=1M count=" + Integer.valueOf(filesize) * 1024;

        String command3 = "mkdir /mnt/A_vasa_file_system_common_f4e5bade_15a2_4805_bf8e_52318c4ce443/" + srcPath;

        SDKResult<NFSvvolCreateResBean> sdkResult = new SDKResult<>();
        try {

            Runtime run = Runtime.getRuntime();
//            Process aprocess = run.exec(new String[] {"/bin/sh", "-c", "mkdir /mnt/A_vasa_file_system_common_f4e5bade_15a2_4805_bf8e_52318c4ce443"});
//
//            Process aprocess2 = run.exec(new String[] {"/bin/sh", "-c", "mount -t nfs 10.10.205.84:/A_vasa_file_system_common_f4e5bade_15a2_4805_bf8e_52318c4ce443 /mnt/A_vasa_file_system_common_f4e5bade_15a2_4805_bf8e_52318c4ce443"});
//
//            Process aprocess3 = run.exec(new String[] {"/bin/sh", "-c", "mkdir /mnt/A_vasa_file_system_common_f4e5bade_15a2_4805_bf8e_52318c4ce443/test"});
//
//            Process aprocessm = run.exec(new String[] {"/bin/sh", "-c", "cd /mnt/A_vasa_file_system_common_f4e5bade_15a2_4805_bf8e_52318c4ce443/ && dd if=/dev/zero of=test.vmdk bs=1M count=1"});
//            Process aprocess = run.exec(new String[] {"/bin/sh", "-c", "mkdir /mnt/testdd"});
//
//            Process aprocess2 = run.exec(new String[] {"/bin/sh", "-c", "mount -t nfs 10.10.205.84:/vm_config /mnt/testdd/"});
//
//            Process aprocess3 = run.exec(new String[] {"/bin/sh", "-c", "mkdir /mnt/testdd/test2dd"});
//
//            Process aprocess4 = run.exec(new String[] {"/bin/sh", "-c", "dd if=/dev/zero of=test.vmdk bs=1M count=1"});

//            LOGGER.info("suc ===>"+command);

            Process psaaaa = run.exec("/root/huyan/test.sh");
            LOGGER.info("execute shell ===>" + command);


            Runtime runtime = Runtime.getRuntime();
            Process process1 = runtime.exec(new String[]{"/bin/sh", "-c", command});
            LOGGER.info("command0 ===>" + command);

            Process process2 = runtime.exec(new String[]{"/bin/sh", "-c", command0});
            LOGGER.info("command0 ===>" + command0);

            Process process3 = runtime.exec(new String[]{"/bin/sh", "-c", command1});
            LOGGER.info("command0 ===>" + command1);


//            LOGGER.info("command0 ===>"+command);
//            Process psa = Runtime.getRuntime().exec(command);
//            LOGGER.info("command0 ===>"+psa.getErrorStream());
//            LOGGER.info("command mkdirs ===>");
//            File file = new File("/mnt/"+share+"/");
//            file.setWritable(true, false);
//            file.mkdirs();
//
//            LOGGER.info("command0 ===>"+command0);
//            Process ps0 = Runtime.getRuntime().exec(command0);
//            LOGGER.info("command0 ===>"+ps0.getOutputStream());;
//
//            LOGGER.info("command1 ===>"+command1);
//            Process ps1 = Runtime.getRuntime().exec(command1);

            if (srcPath.contains(".vmdk")) {
                LOGGER.info("create data vvol! maka a file " + srcPath);
                Process process4 = runtime.exec(new String[]{"/bin/sh", "-c", command2});
                LOGGER.info("command0 ===>" + command2);
//                Process ps = Runtime.getRuntime().exec(command2);
//                LOGGER.info("command ===>"+command2);
            } else if (srcPath.contains(".vswp")) {
                LOGGER.info("create swap vvol! maka a file " + srcPath);
                Process ps = Runtime.getRuntime().exec(command2);
                LOGGER.info("command ===>" + command2);
            } else {
                LOGGER.info("create config vvol! maka a directory " + srcPath);
//                Process ps = Runtime.getRuntime().exec(command3);
//                LOGGER.info("command ===>"+command3);
//                LOGGER.info("command0 ===>"+ps.getErrorStream());
//
//                File file2 = new File("/mnt/"+share+"/"+srcPath+"/");
//                file2.setWritable(true, false);
//                file2.mkdirs();
                Process process5 = runtime.exec(new String[]{"/bin/sh", "-c", command3});
                LOGGER.info("command0 ===>" + command3);
            }


        } catch (IOException e) {
            LOGGER.error("create vvol file failed " + e);
            sdkResult.setErrCode(0);
            e.printStackTrace();
            return sdkResult;
        }

        sdkResult.setErrCode(0);
        return sdkResult;
    }

    private void createNFSshareReqBean(NFSvvolCreateReqBean reqPayload, String cmd, String srcPath, String dstPath, long filesize, String vstoreId) {
        reqPayload.setCmd(cmd);
        reqPayload.setSrcPath(srcPath);
        if (cmd.equals(operate_create) || cmd.equals(operate_setattr)) {
            if (filesize != 0) {
                reqPayload.setFileSize(filesize * 1024 * 1024);
            }
        }
        if (dstPath != null) {
            reqPayload.setDstPath(dstPath);
        }

        if (vstoreId != null) {
            reqPayload.setVstoreId(vstoreId);
        }

    }
}
