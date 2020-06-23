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
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;

class WebPReader extends ImageReader {
  private byte[] fData;
  private int fWidth;
  private int fHeight;

  WebPReader( ImageReaderSpi originatingProvider ) {
    super( originatingProvider );
  }

  @Override
  public void setInput( Object input, boolean seekForwardOnly, boolean ignoreMetadata ) {
    super.setInput( input, seekForwardOnly, ignoreMetadata );
    fData = null;
    fWidth = -1;
    fHeight = -1;
  }

  @Override
  public int getNumImages( boolean allowSearch ) throws IOException {
    return 1;
  }

  private void readHeader() throws IOException {
    if ( fWidth != -1 && fHeight != -1 ) {
      return;
    }

    readData();
    int[] info = WebP.getInfo( fData, 0, fData.length );
    fWidth = info[ 0 ];
    fHeight = info[ 1 ];
  }

  private void readData() throws IOException {
    if ( fData != null ) {
      return;
    }

    ImageInputStream input = ( ImageInputStream ) getInput();
    long length = input.length();
    if ( length > Integer.MAX_VALUE ) {
      throw new IOException( "Cannot read image of size " + length );
    }

    if ( input.getStreamPosition() != 0L ) {
      if ( isSeekForwardOnly() ) {
        throw new IOException();
      }
      else {
        input.seek( 0 );
      }
    }

    byte[] data;
    if ( length > 0 ) {
      data = new byte[ ( int ) length ];
      input.readFully( data );
    }
    else {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[ 4096 ];
      int bytesRead;
      while ( ( bytesRead = input.read( buffer ) ) != -1 ) {
        out.write( buffer, 0, bytesRead );
      }
      out.close();
      data = out.toByteArray();
    }
    fData = data;
  }

  private void checkIndex( int imageIndex ) {
    if ( imageIndex != 0 ) {
      throw new IndexOutOfBoundsException( "Invalid image index: " + imageIndex );
    }
  }

  @Override
  public int getWidth( int imageIndex ) throws IOException {
    checkIndex( imageIndex );
    readHeader();
    return fWidth;
  }

  @Override
  public int getHeight( int imageIndex ) throws IOException {
    checkIndex( imageIndex );
    readHeader();
    return fHeight;
  }

  @Override
  public IIOMetadata getStreamMetadata() throws IOException {
    return null;
  }

  @Override
  public IIOMetadata getImageMetadata( int imageIndex ) throws IOException {
    return null;
  }

  @Override
  public Iterator<ImageTypeSpecifier> getImageTypes( int imageIndex ) throws IOException {
    return Collections.singletonList(
        ImageTypeSpecifier.createFromBufferedImageType( BufferedImage.TYPE_INT_ARGB )
    ).iterator();
  }

  @Override
  public ImageReadParam getDefaultReadParam() {
    return new WebPReadParam();
  }

  @Override
  public BufferedImage read( int imageIndex, ImageReadParam param ) throws IOException {
    checkIndex( imageIndex );
    readData();
    readHeader();
    WebPReadParam readParam = param != null ? (WebPReadParam) param : new WebPReadParam();

    int[] outParams = new int[4];
    int[] pixels = WebP.decode(readParam.getDecoderOptions(), fData, 0, fData.length, outParams);

    int width = outParams[1];
    int height = outParams[2];
    boolean alpha = outParams[3] != 0;

    ColorModel colorModel;
    if ( alpha ) {
      colorModel = new DirectColorModel( 32, 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000 );
    } else {
      colorModel = new DirectColorModel( 24, 0x00ff0000, 0x0000ff00, 0x000000ff, 0x00000000 );
    }

    SampleModel sampleModel = colorModel.createCompatibleSampleModel( width, height );
    DataBufferInt db = new DataBufferInt( pixels, width * height );
    WritableRaster raster = WritableRaster.createWritableRaster(sampleModel, db, null);

    return new BufferedImage( colorModel, raster, false, new Hashtable<Object, Object>() );
  }
}
