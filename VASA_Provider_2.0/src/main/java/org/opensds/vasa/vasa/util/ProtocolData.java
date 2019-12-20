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

public class ProtocolData {
    /**
     * AES 加密后的trustpassword
     */
    private String trustPass;

    /**
     * trustpassword file path
     */
    private String trustPassPath;

    private String keystorePass;

    private String keystorePath;

    public String getTrustPass() {
        return trustPass;
    }

    public void setTrustPass(String trustPass) {
        this.trustPass = trustPass;
    }

    public String getTrustPassPath() {
        return trustPassPath;
    }

    public void setTrustPassPath(String trustPassPath) {
        this.trustPassPath = trustPassPath;
    }

    public String getKeystorePass() {
        return keystorePass;
    }

    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getKeystoreAlg() {
        return keystoreAlg;
    }

    public void setKeystoreAlg(String keystoreAlg) {
        this.keystoreAlg = keystoreAlg;
    }

    public String getTruststoreAlg() {
        return truststoreAlg;
    }

    public void setTruststoreAlg(String truststoreAlg) {
        this.truststoreAlg = truststoreAlg;
    }

    private String keystoreAlg;

    private String truststoreAlg;

    private static ProtocolData data;

    public static ProtocolData getInstance() {
        if (null == data) {
            data = new ProtocolData();
        }
        return data;
    }

}
