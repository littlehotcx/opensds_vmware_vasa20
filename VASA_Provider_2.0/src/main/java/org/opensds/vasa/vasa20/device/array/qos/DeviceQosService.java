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

  package org.opensds.vasa.vasa20.device.array.qos;

  import java.util.ArrayList;
  import java.util.List;

  import org.apache.logging.log4j.LogManager;
  import org.apache.logging.log4j.Logger;

  import org.opensds.vasa.base.bean.terminal.ResBean;
  import org.opensds.vasa.base.common.VASAArrayUtil;
  import org.opensds.platform.common.SDKErrorCode;
  import org.opensds.platform.common.SDKResult;
  import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
  import org.opensds.platform.exception.ProtocolAdapterException;
  import org.opensds.vasa.vasa20.device.AbstractVASACapability;
  import org.opensds.vasa.vasa20.device.VASARestReqMessage;

  public class DeviceQosService extends AbstractVASACapability implements IDeviceQosService {

      public DeviceQosService(ISDKProtocolAdapter protocolAdapter) {
          super(protocolAdapter);
          // TODO Auto-generated constructor stub
      }

      private static final Logger LOGGER = LogManager.getLogger(DeviceQosService.class);

      public String createQos_Url = "ioclass";

      public String activeQos_Url = "ioclass/active";

      public String deactiveQos_Url = "ioclass/active";

      public String updateQos_Url = "ioclass/";

      private String delQos_Url = "ioclass/";

      private String searchQos_url = "ioclass/";

      @Override
      public SDKResult<QosCreateResBean> createQos(String arrayId, String name,
                                                   String description,
                                                   String iotype,
                                                   Long maxbandwidth,
                                                   Long minbandwidth,
                                                   Long maxiops,
                                                   Long miniops,
                                                   Long latency,
                                                   String classtype, String lunId) {
          VASARestReqMessage req = new VASARestReqMessage();
          req.setHttpMethod("POST");
          req.setMediaType("json");
          req.setArrayId(arrayId);
          QosCreateReqBean reqPayload = new QosCreateReqBean();
          reqPayload.setNAME(name);
          reqPayload.setDESCRIPTION(description);
          if (null == iotype) {
              //设置默认值为读写IO
              reqPayload.setIOTYPE(VASAArrayUtil.IOTYPE.READANDWRITEIO.getIdentifier());
          } else {
              String ioTypeInt = VASAArrayUtil.getIOType(iotype);
              reqPayload.setIOTYPE(ioTypeInt);
          }
          List<String> lUNLIST = new ArrayList<>();
          if (null != lunId && !lunId.equalsIgnoreCase("")) {
              lUNLIST.add(lunId);
          }
          reqPayload.setLUNLIST(lUNLIST);
          reqPayload.setMAXBANDWIDTH(maxbandwidth);
          reqPayload.setMINBANDWIDTH(minbandwidth);
          reqPayload.setMAXIOPS(maxiops);
          reqPayload.setMINIOPS(miniops);
          reqPayload.setLATENCY(latency);
          if (null != classtype) {
              String classTypeInt = VASAArrayUtil.getClassType(classtype);
              reqPayload.setCLASSTYPE(classTypeInt);
          }
          LOGGER.debug("createQos parameters = " + reqPayload);
          req.setPayload(reqPayload);
          SDKResult<QosCreateResBean> sdkResult = new SDKResult<QosCreateResBean>();
          try {
              ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, createQos_Url,
                      "org.opensds.vasa.vasa20.device.array.qos.QosCreateResBean");
              sdkResult.setErrCode(res.getErrorCode());
              sdkResult.setDescription(res.getDescription());
              if (0 == res.getErrorCode()) {
                  QosCreateResBean result = (QosCreateResBean) res.getResData();
                  sdkResult.setResult(result);
              }

          } catch (ProtocolAdapterException e) {
              sdkResult.setErrCode(e.getErrorCode());
              sdkResult.setDescription("createQos fail ! QosCreateReqBean=" + reqPayload);
          }
          return sdkResult;
      }

      @Override
      public SDKErrorCode activeQos(String arrayId, String qosId) {
          // TODO Auto-generated method stub
          VASARestReqMessage req = new VASARestReqMessage();
          req.setHttpMethod("PUT");
          req.setMediaType("json");
          req.setArrayId(arrayId);
          QosActiveReqBean reqBean = new QosActiveReqBean();
          reqBean.setID(qosId);
          req.setPayload(reqBean);
          LOGGER.debug("begin active qos, qosId=" + qosId);
          SDKErrorCode result = new SDKErrorCode();
          try {
              ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, activeQos_Url,
                      null);
              result.setErrCode(response.getErrorCode());
              result.setDescription(response.getDescription());
          } catch (ProtocolAdapterException e) {
              LOGGER.error("activeQos fail ! qosId=" + qosId, e);
              result.setErrCode(e.getErrorCode());
              result.setDescription("activeQos fail ! qosId=" + qosId);
          }
          LOGGER.debug("end active qos, result=" + result);
          return result;
      }

      @Override
      public SDKErrorCode deactiveQos(String arrayId, String qosId) {
          // TODO Auto-generated method stub
          VASARestReqMessage req = new VASARestReqMessage();
          req.setHttpMethod("PUT");
          req.setMediaType("json");
          req.setArrayId(arrayId);
          QosDeactiveReqBean reqBean = new QosDeactiveReqBean();
          reqBean.setID(qosId);
          req.setPayload(reqBean);

          SDKErrorCode result = new SDKErrorCode();
          try {
              ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, deactiveQos_Url,
                      null);
              result.setErrCode(response.getErrorCode());
              result.setDescription(response.getDescription());
          } catch (ProtocolAdapterException e) {
              LOGGER.error("deactiveQos fail ! qosId=" + qosId, e);
              result.setErrCode(e.getErrorCode());
              result.setDescription("deactiveQos fail ! qosId=" + qosId);
          }
          return result;
      }

      @Override
      public SDKErrorCode updateQos(String arrayId, String qosId, List<String> luns) {
          // TODO Auto-generated method stub
          VASARestReqMessage req = new VASARestReqMessage();
          req.setHttpMethod("PUT");
          req.setMediaType("json");
          req.setArrayId(arrayId);
          QosUpdateReqBean reqPayload = new QosUpdateReqBean();
          reqPayload.setLUNLIST(luns);
          req.setPayload(reqPayload);
          SDKErrorCode result = new SDKErrorCode();
          try {
              ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, updateQos_Url + qosId,
                      "org.opensds.vasa.vasa20.device.array.qos.QosCreateResBean");
              result.setErrCode(response.getErrorCode());
              result.setDescription(response.getDescription());
          } catch (ProtocolAdapterException e) {
              LOGGER.error("updateQos fail ! reqPayload=" + reqPayload, e);
              result.setErrCode(e.getErrorCode());
              result.setDescription("updateQos fail ! qosId=" + qosId + ",luns=" + luns);
          }
          return result;
      }

      @Override
      public SDKErrorCode delQos(String arrayId, String qosId) {
          // TODO Auto-generated method stub
          VASARestReqMessage req = new VASARestReqMessage();
          req.setHttpMethod("DELETE");
          req.setArrayId(arrayId);
          SDKErrorCode result = new SDKErrorCode();
          try {
              ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, delQos_Url + qosId,
                      null);
              result.setErrCode(response.getErrorCode());
              result.setDescription(response.getDescription());
          } catch (ProtocolAdapterException e) {
              LOGGER.error("delQos fail ! qosId=" + qosId, e);
              result.setErrCode(e.getErrorCode());
              result.setDescription("delQos fail ! qosId=" + qosId);
          }
          return result;
      }

      @Override
      public SDKResult<QosCreateResBean> queryQos(String arrayId, String qosId) {
          // TODO Auto-generated method stub
          VASARestReqMessage req = new VASARestReqMessage();
          req.setHttpMethod("GET");
          req.setArrayId(arrayId);
          ResBean response = new ResBean();
          LOGGER.debug("queryQos qosId=" + qosId);
          SDKResult<QosCreateResBean> sdkResult = new SDKResult<>();
          try {
              response = (ResBean) protocolAdapter.syncSendMessage(req, searchQos_url + qosId, "org.opensds.vasa.vasa20.device.array.qos.QosCreateResBean");
              sdkResult.setErrCode(response.getErrorCode());
              sdkResult.setDescription(response.getDescription());
              if (0 == response.getErrorCode()) {
                  QosCreateResBean result = (QosCreateResBean) response.getResData();
                  sdkResult.setResult(result);
              }
          } catch (ProtocolAdapterException e) {
              LOGGER.error("queryQos fail ! qosId=" + qosId, e);
              sdkResult.setErrCode(e.getErrorCode());
              sdkResult.setDescription("queryQos fail ! qosId=" + qosId);
          }
          LOGGER.debug("queryQos qosId=" + qosId + ",result=" + sdkResult);
          return sdkResult;
      }


  }
