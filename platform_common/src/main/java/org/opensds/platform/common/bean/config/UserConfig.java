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

package org.opensds.platform.common.bean.config;

import java.util.List;

/**
 * 用户实体类
 */
public class UserConfig
{
    /*
     * 用户ID
     */
    private String userId;
    
    /*
     * 姓
     */
    private String firstName;
    
    /*
     * 名
     */
    private String lastName;
    
    /*
     * 用户密码
     */
    private String password;
    
    /*
     * 用户类型
     */
    private String userType;
    
    /*
     * 用户状态
     */
    private String status;
    
    /*
     * 用户开通的业务能力
     */
    private String ability;
    
    /*
     * 备注
     */
    private String remark;
    
    /*
     * 保留字段
     */
    private String reserve;
    
    private List<UserConfigHistory> hisList;
    
    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public String getFirstName()
    {
        return firstName;
    }
    
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    
    public String getLastName()
    {
        return lastName;
    }
    
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getUserType()
    {
        return userType;
    }
    
    public void setUserType(String userType)
    {
        this.userType = userType;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public String getRemark()
    {
        return remark;
    }
    
    public void setRemark(String remark)
    {
        this.remark = remark;
    }
    
    public String getReserve()
    {
        return reserve;
    }
    
    public void setReserve(String reserve)
    {
        this.reserve = reserve;
    }
    
    public String getAbility()
    {
        return ability;
    }
    
    public void setAbility(String ability)
    {
        this.ability = ability;
    }
    
    public List<UserConfigHistory> getHisList() {
		return hisList;
	}

	public void setHisList(List<UserConfigHistory> hisList) {
		this.hisList = hisList;
	}

	@Override
    public String toString()
    {
        return "User [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", password=******"
            + ", userType=" + userType + ", status=" + status + ", remark=" + remark + ", reserve="
            + reserve + "]";
    }
}
