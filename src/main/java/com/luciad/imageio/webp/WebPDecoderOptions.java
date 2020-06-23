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

public final class WebPDecoderOptions {
  static {
    WebP.loadNativeLibrary();
  }

  long fPointer;

  public WebPDecoderOptions() {
    fPointer = createDecoderOptions();
    if ( fPointer == 0 ) {
      throw new OutOfMemoryError();
    }
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    deleteDecoderOptions( fPointer );
    fPointer = 0L;
  }

  public int getCropHeight() {
    return getCropHeight( fPointer );
  }

  public void setCropHeight( int aCropHeight ) {
    setCropHeight( fPointer, aCropHeight );
  }

  public int getCropLeft() {
    return getCropLeft( fPointer );
  }

  public void setCropLeft( int aCropLeft ) {
    setCropLeft( fPointer, aCropLeft );
  }

  public int getCropTop() {
    return getCropTop( fPointer );
  }

  public void setCropTop( int aCropTop ) {
    setCropTop( fPointer, aCropTop );
  }

  public int getCropWidth() {
    return getCropWidth( fPointer );
  }

  public void setCropWidth( int aCropWidth ) {
    setCropWidth( fPointer, aCropWidth );
  }

  public boolean isFancyUpsampling() {
    return !isNoFancyUpsampling( fPointer );
  }

  public void setFancyUpsampling( boolean aFancyUpsampling ) {
    setNoFancyUpsampling( fPointer, !aFancyUpsampling );
  }

  public int getScaledHeight() {
    return getScaledHeight( fPointer );
  }

  public void setScaledHeight( int aScaledHeight ) {
    setScaledHeight( fPointer, aScaledHeight );
  }

  public int getScaledWidth() {
    return getScaledWidth( fPointer );
  }

  public void setScaledWidth( int aScaledWidth ) {
    setScaledWidth( fPointer, aScaledWidth );
  }

  public boolean isUseCropping() {
    return isUseCropping( fPointer );
  }

  public void setUseCropping( boolean aUseCropping ) {
    setUseCropping( fPointer, aUseCropping );
  }

  public boolean isUseScaling() {
    return isUseScaling( fPointer );
  }

  public void setUseScaling( boolean aUseScaling ) {
    setUseScaling( fPointer, aUseScaling );
  }

  public boolean isUseThreads() {
    return isUseThreads( fPointer );
  }

  public void setUseThreads( boolean aUseThreads ) {
    setUseThreads( fPointer, aUseThreads );
  }

  public boolean isBypassFiltering() {
    return isBypassFiltering( fPointer );
  }

  public void setBypassFiltering( boolean aBypassFiltering ) {
    setBypassFiltering( fPointer, aBypassFiltering );
  }

  private static native long createDecoderOptions();

  private static native void deleteDecoderOptions( long aPointer );

  private static native int getCropHeight( long aPointer );

  private static native void setCropHeight( long aPointer, int aCropHeight );

  private static native int getCropLeft( long aPointer );

  private static native void setCropLeft( long aPointer, int aCropLeft );

  private static native int getCropTop( long aPointer );

  private static native void setCropTop( long aPointer, int aCropTop );

  private static native int getCropWidth( long aPointer );

  private static native void setCropWidth( long aPointer, int aCropWidth );

  private static native boolean isForceRotation( long aPointer );

  private static native void setForceRotation( long aPointer, boolean aForceRotation );

  private static native boolean isNoEnhancement( long aPointer );

  private static native void setNoEnhancement( long aPointer, boolean aNoEnhancement );

  private static native boolean isNoFancyUpsampling( long aPointer );

  private static native void setNoFancyUpsampling( long aPointer, boolean aFancyUpsampling );

  private static native int getScaledHeight( long aPointer );

  private static native void setScaledHeight( long aPointer, int aScaledHeight );

  private static native int getScaledWidth( long aPointer );

  private static native void setScaledWidth( long aPointer, int aScaledWidth );

  private static native boolean isUseCropping( long aPointer );

  private static native void setUseCropping( long aPointer, boolean aUseCropping );

  private static native boolean isUseScaling( long aPointer );

  private static native void setUseScaling( long aPointer, boolean aUseScaling );

  private static native boolean isUseThreads( long aPointer );

  private static native void setUseThreads( long aPointer, boolean aUseThreads );

  private static native boolean isBypassFiltering( long aPointer );

  private static native void setBypassFiltering( long aPointer, boolean aBypassFiltering );
}
