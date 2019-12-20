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

package org.opensds.vasa.interfaces.device.bitmap;

import org.opensds.vasa.domain.model.bean.S2DBitmap;
import org.opensds.platform.common.SDKResult;

public interface IBitmapCapability {
    SDKResult<S2DBitmap> getAllocatedBitmap(String arrayId, String vvolId, long segmentStartOffsetBytes, long segmentLengthBytes, long chunkSizeBytes);

    SDKResult<S2DBitmap> getUnsharedBitmap(String arrayId, String vvolId, String baseVvolId, long segmentStartOffsetBytes,
                                           long segmentLengthBytes, long chunkSizeBytes);

    SDKResult<S2DBitmap> getUnsharedChunks(String arrayId, String vvolId, String baseVvolId, long segmentStartOffsetBytes, long segmentLengthBytes);
}
