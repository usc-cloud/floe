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
package org.soyatec.windowsazure.internal.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Logger {
	private static Log log = LogFactory.getLog(Logger.class);

	/**
	 * Set the info log.
	 * @param info
	 */
	public static void log(String info) {
		log.info(info);
	}

	/**
	 * Set the error log.
	 * @param str
	 * @param e
	 */
	public static void error(String str, Exception e) {
		log.error(str, e);
	}

	/**
	 * Set the debug log.
	 * @param str
	 */
	public static void debug(String str) {
		log.debug(str);
	}
}
