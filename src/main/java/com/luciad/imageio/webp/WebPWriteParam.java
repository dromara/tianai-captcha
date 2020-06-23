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

import javax.imageio.ImageWriteParam;
import java.util.Locale;

public class WebPWriteParam extends ImageWriteParam {
  public static final int LOSSY_COMPRESSION = 0;
  public static final int LOSSLESS_COMPRESSION = 1;

  private final boolean fDefaultLossless;
  private WebPEncoderOptions fOptions;

  public WebPWriteParam( Locale aLocale ) {
    super( aLocale );
    fOptions = new WebPEncoderOptions();
    fDefaultLossless = fOptions.isLossless();
    canWriteCompressed = true;
    compressionTypes = new String[]{
        "Lossy",
        "Lossless"
    };
    compressionType = compressionTypes[fDefaultLossless ? LOSSLESS_COMPRESSION : LOSSY_COMPRESSION];
    compressionQuality = fOptions.getCompressionQuality() / 100f;
    compressionMode = MODE_EXPLICIT;
  }

  @Override
  public float getCompressionQuality() {
    return super.getCompressionQuality();
  }

  @Override
  public void setCompressionQuality( float quality ) {
    super.setCompressionQuality( quality );
    fOptions.setCompressionQuality( quality * 100f );
  }

  @Override
  public void setCompressionType( String compressionType ) {
    super.setCompressionType( compressionType );
    for ( int i = 0; i < compressionTypes.length; i++ ) {
      if ( compressionTypes[i].equals( compressionType ) ) {
        fOptions.setLossless( i == LOSSLESS_COMPRESSION );
        break;
      }
    }

  }

  @Override
  public void unsetCompression() {
    super.unsetCompression();
    fOptions.setLossless( fDefaultLossless );
  }

  public void setSnsStrength(int aSnsStrength) {
    fOptions.setSnsStrength(aSnsStrength);
  }

  public void setAlphaQuality(int aAlphaQuality) {
    fOptions.setAlphaQuality(aAlphaQuality);
  }

  public int getSegments() {
    return fOptions.getSegments();
  }

  public int getPreprocessing() {
    return fOptions.getPreprocessing();
  }

  public int getFilterStrength() {
    return fOptions.getFilterStrength();
  }

  public void setEmulateJpegSize(boolean aEmulateJpegSize) {
    fOptions.setEmulateJpegSize(aEmulateJpegSize);
  }

  public int getPartitions() {
    return fOptions.getPartitions();
  }

  public void setTargetPSNR(float aTargetPSNR) {
    fOptions.setTargetPSNR(aTargetPSNR);
  }

  public int getEntropyAnalysisPassCount() {
    return fOptions.getEntropyAnalysisPassCount();
  }

  public int getPartitionLimit() {
    return fOptions.getPartitionLimit();
  }

  public int getFilterType() {
    return fOptions.getFilterType();
  }

  public int getFilterSharpness() {
    return fOptions.getFilterSharpness();
  }

  public int getAlphaQuality() {
    return fOptions.getAlphaQuality();
  }

  public boolean isShowCompressed() {
    return fOptions.isShowCompressed();
  }

  public boolean isReduceMemoryUsage() {
    return fOptions.isReduceMemoryUsage();
  }

  public void setThreadLevel(int aThreadLevel) {
    fOptions.setThreadLevel(aThreadLevel);
  }

  public boolean isAutoAdjustFilterStrength() {
    return fOptions.isAutoAdjustFilterStrength();
  }

  public void setReduceMemoryUsage(boolean aLowMemory) {
    fOptions.setReduceMemoryUsage(aLowMemory);
  }

  public void setFilterStrength(int aFilterStrength) {
    fOptions.setFilterStrength(aFilterStrength);
  }

  public int getTargetSize() {
    return fOptions.getTargetSize();
  }

  public void setEntropyAnalysisPassCount(int aPass) {
    fOptions.setEntropyAnalysisPassCount(aPass);
  }

  public void setFilterSharpness(int aFilterSharpness) {
    fOptions.setFilterSharpness(aFilterSharpness);
  }

  public int getAlphaFiltering() {
    return fOptions.getAlphaFiltering();
  }

  public int getSnsStrength() {
    return fOptions.getSnsStrength();
  }

  public void setPartitionLimit(int aPartitionLimit) {
    fOptions.setPartitionLimit(aPartitionLimit);
  }

  public void setMethod(int aMethod) {
    fOptions.setMethod(aMethod);
  }

  public void setAlphaFiltering(int aAlphaFiltering) {
    fOptions.setAlphaFiltering(aAlphaFiltering);
  }

  public int getMethod() {
    return fOptions.getMethod();
  }

  public void setFilterType(int aFilterType) {
    fOptions.setFilterType(aFilterType);
  }

  public void setPartitions(int aPartitions) {
    fOptions.setPartitions(aPartitions);
  }

  public void setAutoAdjustFilterStrength(boolean aAutofilter) {
    fOptions.setAutoAdjustFilterStrength(aAutofilter);
  }

  public boolean isEmulateJpegSize() {
    return fOptions.isEmulateJpegSize();
  }

  public int getAlphaCompression() {
    return fOptions.getAlphaCompression();
  }

  public void setShowCompressed(boolean aShowCompressed) {
    fOptions.setShowCompressed(aShowCompressed);
  }

  public void setSegments(int aSegments) {
    fOptions.setSegments(aSegments);
  }

  public float getTargetPSNR() {
    return fOptions.getTargetPSNR();
  }

  public int getThreadLevel() {
    return fOptions.getThreadLevel();
  }

  public void setTargetSize(int aTargetSize) {
    fOptions.setTargetSize(aTargetSize);
  }

  public void setAlphaCompression(int aAlphaCompression) {
    fOptions.setAlphaCompression(aAlphaCompression);
  }

  public void setPreprocessing(int aPreprocessing) {
    fOptions.setPreprocessing(aPreprocessing);
  }

  WebPEncoderOptions getEncoderOptions() {
    return fOptions;
  }
}
