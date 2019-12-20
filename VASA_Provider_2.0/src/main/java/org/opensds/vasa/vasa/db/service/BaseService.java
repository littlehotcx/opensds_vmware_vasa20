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

package org.opensds.vasa.vasa.db.service;

import java.util.List;

import org.opensds.vasa.vasa.db.model.BaseData;

public interface BaseService<T extends BaseData> {

    public List<T> getAll();

    public List<T> search(T t);

    public T getDataByKey(T t);

    public void save(T t);

    public void delete(T t);

    public void updateData(T t);
}