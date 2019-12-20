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

package org.opensds.platform.common;

public class SDKResult<T> extends SDKErrorCode
{
    /**
     * UID
     */
    private static final long serialVersionUID = 1L;

    private T result;

    public T getResult()
    {
        return result;
    }

    public void setResult(T result)
    {
        this.result = result;
    }

	@Override
	public String toString() {
		return "SDKResult [result=" + result + "]";
	}
}
