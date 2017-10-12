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

package com.xh.util.vfs;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * This class supports overriding of config builders by supplying a Properties containing a variable in the format of
 * vfs.[scheme].config.parser where [scheme] is one of the VFS schemes (file, http, sftp, etc...)
 *
 * @author cboyden
 */
public class WebFileSystemConfigBuilderFactory {

  /**
   * This factory returns a FileSystemConfigBuilder. Custom FileSystemConfigBuilders can be created by implementing the
   * {@link IKettleFileSystemConfigBuilder} or overriding the {@link KettleGenericFileSystemConfigBuilder}
   *
   * @see org.apache.commons.vfs.FileSystemConfigBuilder
   *
   * @param varSpace
   *          A Kettle variable space for resolving VFS config parameters
   * @param scheme
   *          The VFS scheme (FILE, HTTP, SFTP, etc...)
   * @return A FileSystemConfigBuilder that can translate Kettle variables into VFS config parameters
   * @throws IOException
   */
  public static IWebFileSystemConfigBuilder getConfigBuilder( Properties varSpace, String scheme ) throws IOException {
    IWebFileSystemConfigBuilder result = null;

    // Attempt to load the Config Builder from a variable: vfs.config.parser = class
    String parserClass = varSpace.getProperty( "vfs." + scheme + ".config.parser" );

    if ( parserClass != null ) {
      try {
        Class<?> configBuilderClass =
          WebFileSystemConfigBuilderFactory.class.getClassLoader().loadClass( parserClass );
        Method mGetInstance = configBuilderClass.getMethod( "getInstance" );
        if ( ( mGetInstance != null )
          && ( IWebFileSystemConfigBuilder.class.isAssignableFrom( mGetInstance.getReturnType() ) ) ) {
          result = (IWebFileSystemConfigBuilder) mGetInstance.invoke( null );
        } else {
          result = (IWebFileSystemConfigBuilder) configBuilderClass.newInstance();
        }
      } catch ( Exception e ) {
        // Failed to load custom parser. Throw exception.
        throw new IOException( "Custom Vfs Settings Parser Failed To Load" );
      }
    } else {
      // No custom parser requested, load default
      if ( scheme.equalsIgnoreCase( "sftp" ) ) {
        result = WebSftpFileSystemConfigBuilder.getInstance();
      } else {
        result = WebGenericFileSystemConfigBuilder.getInstance();
      }
    }

    return result;
  }

}
