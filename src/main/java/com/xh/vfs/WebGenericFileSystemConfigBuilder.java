/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2013 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package com.xh.vfs;

import java.io.IOException;

import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.util.DelegatingFileSystemOptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic FileSystemConfigBuilder that inserts parameters and values as literally specified.
 *
 * Note: ALL parameters are case sensitive! Please see individual FileSystemConfigBuilder for list of available
 * parameters. Please also see Web FileSystemConfigBuilder overrides for additional parameters.
 *
 * @see WebSftpFileSystemConfigBuilder
 *
 * @author cboyden
 */
public class WebGenericFileSystemConfigBuilder extends FileSystemConfigBuilder implements
  IWebFileSystemConfigBuilder {

  private static final WebGenericFileSystemConfigBuilder builder = new WebGenericFileSystemConfigBuilder();
  private static final 	Logger logger = LoggerFactory.getLogger(WebVFS.class);

  @Override
  public String parseParameterName( String parameter, String scheme ) {
    String result = null;

    // Frame the parameter name
    int begin = parameter.indexOf( ".", parameter.indexOf( "." ) + 1 ) + 1; // Get the index of the second "."
                                                                            // (vfs.scheme.parameter)
    int end = -1;

    end = parameter.indexOf( '.', begin );

    if ( end < 0 ) {
      end = parameter.length();
    }

    if ( end > begin ) {
      result = parameter.substring( begin, end );
    }

    return result;
  }

  public static WebGenericFileSystemConfigBuilder getInstance() {
    return builder;
  }

  /**
   * Extract the scheme from a Web VFS configuration paramter (vfs.scheme.parameter)
   *
   * @param fullParameterName
   *          A VFS configuration parameter in the form of 'vfs.scheme.parameter'
   */
  public static String extractScheme( String fullParameterName ) throws IllegalArgumentException {
    String result = null;

    // Verify that this is a Web VFS configuration parameter
    if ( ( fullParameterName != null )
      && ( fullParameterName.length() > 4 ) && ( fullParameterName.startsWith( "vfs." ) ) ) {
      int schemeEnd = fullParameterName.indexOf( ".", 4 );
      if ( schemeEnd > 4 ) {
        result = fullParameterName.substring( 4, schemeEnd );
      } else {
        throw new IllegalArgumentException( "The configuration parameter does not match a valid scheme: "
          + fullParameterName );
      }
    } else {
      throw new IllegalArgumentException( "The configuration parameter does not match a valid scheme: "
        + fullParameterName );
    }

    return result;
  }

  protected WebGenericFileSystemConfigBuilder() {
    super();
  }

  @Override
  protected Class<? extends FileSystem> getConfigClass() {
    return FileSystem.class;
  }

  @Override
  public void setParameter( FileSystemOptions opts, String name, String value, String fullParameterName,
    String vfsUrl ) throws IOException {
    // Use the DelgatingFileSystemOptionsBuilder to insert generic parameters
    // This must be done to assure the correct VFS FileSystem drivers will process the parameters
    String scheme = extractScheme( fullParameterName );
    try {
      DelegatingFileSystemOptionsBuilder delegateFSOptionsBuilder =
        new DelegatingFileSystemOptionsBuilder( WebVFS.getInstance().getFileSystemManager() );
      if ( scheme != null ) {
        delegateFSOptionsBuilder.setConfigString( opts, scheme, name, value );
      } else {
        logger.warn( "Warning: Cannot process VFS parameters if no scheme is specified: " + vfsUrl );
      }
    } catch ( FileSystemException e ) {
      if ( e.getCode().equalsIgnoreCase( "vfs.provider/config-key-invalid.error" ) ) {
        // This key is not supported by the default scheme config builder. This may be a custom key of another config
        // builder
    	  logger.warn( "Warning: The configuration parameter [" + name + "] is not supported by the default configuration builder for scheme: " + scheme );
      } else {
        // An unexpected error has occurred loading in parameters
        throw new IOException( e.getLocalizedMessage() );
      }
    }
  }
}
