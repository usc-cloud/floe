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
package org.soyatec.windowsazure.management;

/**
 * The enum contains the current upgrade domain states.
 */
public enum CurrentUpgradeDomainState {
	
	Before("Before"), During("During");

	private String literal;

	CurrentUpgradeDomainState(String value) {
		this.literal = value;
	}

	/**
	 * @return the literal
	 */
	public String getLiteral() {
		return literal;
	}

	/**
	 * Get current upgrade domain state of literal.
	 * @param literal
	 * @return currentUpgradeDomainState
	 */
	public static CurrentUpgradeDomainState get(String literal) {
		if (literal == null || literal.equals("")) {
			return null;
		}
		if (literal.equalsIgnoreCase(CurrentUpgradeDomainState.Before
				.getLiteral())) {
			return CurrentUpgradeDomainState.Before;
		} else if (literal.equalsIgnoreCase(CurrentUpgradeDomainState.During
				.getLiteral())) {
			return CurrentUpgradeDomainState.During;
		} else {
			return null;
		}
	}

}
