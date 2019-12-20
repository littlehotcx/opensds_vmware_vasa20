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

package org.opensds.platform.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextUtil implements ApplicationContextAware
{
    private static ApplicationContext context;// 声明一个静态变量保存
    
    public static void setApplicationContextValue(ApplicationContext context)
        throws BeansException
    {
        ApplicationContextUtil.context = context;
    }
    
    public static ApplicationContext getContext()
    {
        return context;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext context)
    {
        setApplicationContextValue(context);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name)
    {
        return (T)ApplicationContextUtil.getContext().getBean(name);
    }
}
