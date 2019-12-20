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

package org.opensds.vasa.domain.model.bean;

public class S2DEnumInfo extends DomainBaseBean {
    private String ENUM_NAME;

    private String ENUM_INDEX;

    private String ENUM_GLOSSARY_CN;

    private String ENUM_GLOSSARY_EN;

    private String ENUM_DESCRIPTION_CN;

    private String ENUM_DESCRIPTION_EN;

    public String getENUM_NAME() {
        return ENUM_NAME;
    }

    public void setENUM_NAME(String eNUM_NAME) {
        ENUM_NAME = eNUM_NAME;
    }

    public String getENUM_INDEX() {
        return ENUM_INDEX;
    }

    public void setENUM_INDEX(String eNUM_INDEX) {
        ENUM_INDEX = eNUM_INDEX;
    }

    public String getENUM_GLOSSARY_CN() {
        return ENUM_GLOSSARY_CN;
    }

    public void setENUM_GLOSSARY_CN(String eNUM_GLOSSARY_CN) {
        ENUM_GLOSSARY_CN = eNUM_GLOSSARY_CN;
    }

    public String getENUM_GLOSSARY_EN() {
        return ENUM_GLOSSARY_EN;
    }

    public void setENUM_GLOSSARY_EN(String eNUM_GLOSSARY_EN) {
        ENUM_GLOSSARY_EN = eNUM_GLOSSARY_EN;
    }

    public String getENUM_DESCRIPTION_CN() {
        return ENUM_DESCRIPTION_CN;
    }

    public void setENUM_DESCRIPTION_CN(String eNUM_DESCRIPTION_CN) {
        ENUM_DESCRIPTION_CN = eNUM_DESCRIPTION_CN;
    }

    public String getENUM_DESCRIPTION_EN() {
        return ENUM_DESCRIPTION_EN;
    }

    public void setENUM_DESCRIPTION_EN(String eNUM_DESCRIPTION_EN) {
        ENUM_DESCRIPTION_EN = eNUM_DESCRIPTION_EN;
    }
}
