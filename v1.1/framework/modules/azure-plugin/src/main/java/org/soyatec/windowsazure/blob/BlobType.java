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
package org.soyatec.windowsazure.blob;

/**
 * 
 * Represent the type of Blob.
 *
 */
public enum BlobType {

	BlockBlob("BlockBlob"), PageBlob("PageBlob");

	private String literal;

	BlobType(final String literal) {
		this.literal = literal;
	}

	/**
	 * Get the literal of the data type
	 * 
	 * @return the the literal of the data type string
	 */
	public String getLiteral() {
		return this.literal;
	}

	/**
	 * Return the blobType value if literal equals with BlobType value, else
	 * return null.
	 * 
	 * @param literal
	 * @return blobType value.
	 */
	public static BlobType parse(String literal) {
		for (BlobType b : BlobType.values()) {
			if (b.literal.equalsIgnoreCase(literal))
				return b;
		}
		return null;
	}
}
