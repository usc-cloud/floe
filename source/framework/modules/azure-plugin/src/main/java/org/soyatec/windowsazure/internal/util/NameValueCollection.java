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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.map.MultiValueMap;

public class NameValueCollection extends MultiValueMap {

	/**
	 * Get the single value.
	 * @param key
	 * @return single value
	 */
	public String getSingleValue(String key) {
		List list = (List) this.get(key);
		if (list != null && !list.isEmpty()) {
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Object object = iterator.next();
				if (object != null) {
					return object.toString();
				}
			}
		}
		return null;
	}

	/**
	 * Get the object of key.
	 * @param key
	 * @return object
	 */
	public Object get(Object key) {
		Object result = super.get(key);
		if (result == null && key instanceof String) {
			result = super.get(((String) key).toLowerCase());
		}
		return result;
	}

	/**
	 * Get the multiple values as string
	 * @param key
	 * @return multiple values as string
	 */
	public String getMultipleValuesAsString(String key) {

		StringBuilder buf = new StringBuilder();
		List list = (List) get(key);
		if (list != null && !list.isEmpty()) {
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Object object = iterator.next();
				if (object != null) {
					if (buf.length() > 0) {
						buf.append(',');
					}
					buf.append(object.toString());
				}
			}
		}
		if (buf.length() == 0) {
			return null;
		} else {
			return buf.toString();
		}
	}
}
