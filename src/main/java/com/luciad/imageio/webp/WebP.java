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

import lombok.extern.slf4j.Slf4j;
import org.scijava.nativelib.DefaultJniExtractor;
import org.scijava.nativelib.NativeLibraryUtil;

import java.io.IOException;
import java.nio.ByteOrder;

@Slf4j
final class WebP {
    private static boolean NATIVE_LIBRARY_LOADED = false;

    static synchronized void loadNativeLibrary() {
        if (!NATIVE_LIBRARY_LOADED) {
            NATIVE_LIBRARY_LOADED = true;
            try {
                NativeLibraryUtil.loadNativeLibrary(new DefaultJniExtractor(WebP.class), "webp-imageio");
            } catch (IOException e) {
                log.debug("IOException creating DefaultJniExtractor", e);
            }
        }
    }

    static {
        loadNativeLibrary();
    }

    private WebP() {
    }

    public static int[] decode(WebPDecoderOptions aOptions, byte[] aData, int aOffset, int aLength, int[] aOut) throws IOException {
        if (aOptions == null) {
            throw new NullPointerException("Decoder options may not be null");
        }

        if (aData == null) {
            throw new NullPointerException("Input data may not be null");
        }

        if (aOffset + aLength > aData.length) {
            throw new IllegalArgumentException("Offset/length exceeds array size");
        }

        int[] pixels = decode(aOptions.fPointer, aData, aOffset, aLength, aOut, ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN));
        VP8StatusCode status = VP8StatusCode.getStatusCode(aOut[0]);
        switch (status) {
            case VP8_STATUS_OK:
                break;
            case VP8_STATUS_OUT_OF_MEMORY:
                throw new OutOfMemoryError();
            default:
                throw new IOException("Decode returned code " + status);
        }

        return pixels;
    }

    private static native int[] decode(long aDecoderOptionsPointer, byte[] aData, int aOffset, int aLength, int[] aFlags, boolean aBigEndian);

    public static int[] getInfo(byte[] aData, int aOffset, int aLength) throws IOException {
        int[] out = new int[2];
        int result = getInfo(aData, aOffset, aLength, out);
        if (result == 0) {
            throw new IOException("Invalid WebP data");
        }

        return out;
    }

    private static native int getInfo(byte[] aData, int aOffset, int aLength, int[] aOut);

    public static byte[] encodeRGBA(WebPEncoderOptions aOptions, byte[] aRgbaData, int aWidth, int aHeight, int aStride) {
        return encodeRGBA(aOptions.fPointer, aRgbaData, aWidth, aHeight, aStride);
    }

    private static native byte[] encodeRGBA(long aConfig, byte[] aRgbaData, int aWidth, int aHeight, int aStride);

    public static byte[] encodeRGB(WebPEncoderOptions aOptions, byte[] aRgbaData, int aWidth, int aHeight, int aStride) {
        return encodeRGB(aOptions.fPointer, aRgbaData, aWidth, aHeight, aStride);
    }

    private static native byte[] encodeRGB(long aConfig, byte[] aRgbaData, int aWidth, int aHeight, int aStride);
}
