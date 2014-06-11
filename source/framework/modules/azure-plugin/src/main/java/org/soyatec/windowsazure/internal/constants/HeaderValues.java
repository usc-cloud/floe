/**
 * Copyright  2006-2010 Soyatec
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * $Id$
 */
package org.soyatec.windowsazure.internal.constants;

public final class HeaderValues {
	/**
	 * This is the default content-type xStore uses when no content type is specified.
	 */
	public static final String DefaultContentType = "application/octet-stream";

	/**
	 * The Range header value is "bytes=start-end", both start and end can be empty
	 */
	public static final String RangeHeaderFormat = "bytes={0,number,#}-{1,number,#}"; // MessageFormat.format
	// public static final String RangeHeaderFormat = "bytes=%1$-%2$"; //
	// String.format

	public static final String MatchAny = "*";
	
	public static final String Update = "update";
	
	public static final String Clear = "clear";

}
