/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package javax.servlet.http;

import java.io.*;
import java.util.*;

/**
 * <p> This class represents a part or form item that was received within a
 * <code>multipart/form-data</code> POST request.
 * 
 * @since Servlet 3.0
 */
public interface Part {

    /**
     * Gets the content of this part as an <tt>InputStream</tt>
     * 
     * @return The content of this part as an <tt>InputStream</tt>
     * @throws IOException If an error occurs in retrieving the contet
     * as an <tt>InputStream</tt>
     */
    public InputStream getInputStream() throws IOException;

    /**
     * Gets the content type of this part.
     *
     * @return The content type of this part.
     */
    public String getContentType();

    /**
     * Gets the name of this part
     *
     * @return The name of this part as a <tt>String</tt>
     */
    public String getName();

    /**
     * Returns the size of this fille.
     *
     * @return a <code>long</code> specifying the size of this part, in bytes.
     */
    public long getSize();

    /**
     * A convenience method to write this uploaded item to disk.
     * 
     * <p>This method is not guaranteed to succeed if called more than once for
     * the same part. This allows a particular implementation to use, for
     * example, file renaming, where possible, rather than copying all of the
     * underlying data, thus gaining a significant performance benefit.
     *
     * @param fileName the name of the file to which the stream will be
     * written. The file is created relative to the location as
     * specified in the MultipartConfig
     *
     * @throws IOException if an error occurs.
     */
    public void write(String fileName) throws IOException;

    /**
     * Deletes the underlying storage for a file item, including deleting any
     * associated temporary disk file.
     *
     * @throws IOException if an error occurs.
     */
    public void delete() throws IOException;

    /**
     *
     * Returns the value of the specified mime header
     * as a <code>String</code>. If the Part did not include a header
     * of the specified name, this method returns <code>null</code>.
     * If there are multiple headers with the same name, this method
     * returns the first header in the part.
     * The header name is case insensitive. You can use
     * this method with any request header.
     *
     * @param name		a <code>String</code> specifying the
     *				header name
     *
     * @return			a <code>String</code> containing the
     *				value of the requested
     *				header, or <code>null</code>
     *				if the part does not
     *				have a header of that name
     */
    public String getHeader(String name);

    /**
     * Gets the values of the Part header with the given name.
     *
     * <p>Any changes to the returned <code>Collection</code> must not 
     * affect this <code>Part</code>.
     *
     * <p>Part header names are case insensitive.
     *
     * @param name the header name whose values to return
     *
     * @return a (possibly empty) <code>Collection</code> of the values of
     * the header with the given name
     */
    public Collection<String> getHeaders(String name);

    /**
     * Gets the header names of this Part.
     *
     * <p>Some servlet containers do not allow
     * servlets to access headers using this method, in
     * which case this method returns <code>null</code>
     *
     * <p>Any changes to the returned <code>Collection</code> must not 
     * affect this <code>Part</code>.
     *
     * @return a (possibly empty) <code>Collection</code> of the header
     * names of this Part
     */
    public Collection<String> getHeaderNames();

}
