/**
 * Copyright 2010-2011 CA
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
package org.soyatec.windowsazure.authenticate;

import org.apache.http.HttpRequest;
import org.soyatec.windowsazure.internal.ResourceUriComponents;

/**
 * The credentials for SAS
 * 
 */
public class SharedAccessSignatureCredentials extends SharedKeyCredentials {
	protected String sas;

	public SharedAccessSignatureCredentials() {

	}

	public SharedAccessSignatureCredentials(String sas) {
		if (sas != null) {
			if (sas.startsWith("?")) {
				sas = sas.substring(1);
			}
			this.sas = sas;
		}
	}

	@Override
	public void signRequest(HttpRequest request, ResourceUriComponents uriComponents) {

	}

	public String getSas() {
		return sas;
	}

}
