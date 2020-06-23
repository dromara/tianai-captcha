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

import javax.imageio.ImageReadParam;

public final class WebPReadParam extends ImageReadParam {
  private WebPDecoderOptions fOptions;

  public WebPReadParam() {
    fOptions = new WebPDecoderOptions();
  }

  public void setScaledHeight(int aScaledHeight) {
    fOptions.setScaledHeight(aScaledHeight);
  }

  public void setUseScaling(boolean aUseScaling) {
    fOptions.setUseScaling(aUseScaling);
  }

  public void setUseThreads(boolean aUseThreads) {
    fOptions.setUseThreads(aUseThreads);
  }

  public int getCropHeight() {
    return fOptions.getCropHeight();
  }

  public int getScaledWidth() {
    return fOptions.getScaledWidth();
  }

  public boolean isUseCropping() {
    return fOptions.isUseCropping();
  }

  public void setCropWidth(int aCropWidth) {
    fOptions.setCropWidth(aCropWidth);
  }

  public boolean isBypassFiltering() {
    return fOptions.isBypassFiltering();
  }

  public int getCropLeft() {
    return fOptions.getCropLeft();
  }

  public int getCropWidth() {
    return fOptions.getCropWidth();
  }

  public int getScaledHeight() {
    return fOptions.getScaledHeight();
  }

  public void setBypassFiltering(boolean aBypassFiltering) {
    fOptions.setBypassFiltering(aBypassFiltering);
  }

  public void setUseCropping(boolean aUseCropping) {
    fOptions.setUseCropping(aUseCropping);
  }

  public void setCropHeight(int aCropHeight) {
    fOptions.setCropHeight(aCropHeight);
  }

  public void setFancyUpsampling(boolean aFancyUpsampling) {
    fOptions.setFancyUpsampling(aFancyUpsampling);
  }

  public boolean isUseThreads() {
    return fOptions.isUseThreads();
  }

  public boolean isFancyUpsampling() {
    return fOptions.isFancyUpsampling();
  }

  public boolean isUseScaling() {
    return fOptions.isUseScaling();
  }

  public void setCropLeft(int aCropLeft) {
    fOptions.setCropLeft(aCropLeft);
  }

  public int getCropTop() {
    return fOptions.getCropTop();
  }

  public void setScaledWidth(int aScaledWidth) {
    fOptions.setScaledWidth(aScaledWidth);
  }

  public void setCropTop(int aCropTop) {
    fOptions.setCropTop(aCropTop);
  }

  WebPDecoderOptions getDecoderOptions() {
    return fOptions;
  }
}
