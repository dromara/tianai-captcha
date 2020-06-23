/*
 * Copyright 2013 Luciad (http://www.luciad.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.luciad.imageio.webp;

enum VP8StatusCode {
  VP8_STATUS_OK,
  VP8_STATUS_OUT_OF_MEMORY,
  VP8_STATUS_INVALID_PARAM,
  VP8_STATUS_BITSTREAM_ERROR,
  VP8_STATUS_UNSUPPORTED_FEATURE,
  VP8_STATUS_SUSPENDED,
  VP8_STATUS_USER_ABORT,
  VP8_STATUS_NOT_ENOUGH_DATA,;

  private static VP8StatusCode[] VALUES = values();

  public static VP8StatusCode getStatusCode( int aValue ) {
    if ( aValue >= 0 && aValue < VALUES.length ) {
      return VALUES[ aValue ];
    }
    else {
      return null;
    }
  }
}
