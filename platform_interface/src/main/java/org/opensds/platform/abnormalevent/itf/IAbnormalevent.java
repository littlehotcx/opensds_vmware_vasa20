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

package org.opensds.platform.abnormalevent.itf;

import org.opensds.platform.common.bean.abnormalevent.AbnormaleventBean;

public interface IAbnormalevent
{
    public final static String WRONG_FORMAT = "wrong.file.format";
    
    public final static String FAIL_TO_AUTHENTICATE = "fail.to.authenticate";
    
    public final static String FAIL_TO_CONNECT = "fail.to.connect";
    
    void occurException(String id,AbnormaleventBean bean);
    
    void endException(String id,AbnormaleventBean bean);
    
    boolean existException(String id);
    
}
