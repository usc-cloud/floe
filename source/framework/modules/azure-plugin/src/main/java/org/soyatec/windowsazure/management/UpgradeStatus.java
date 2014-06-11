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
 * This class indicates the status of upgrade.
 * 
 * @author yyang
 * 
 */
public class UpgradeStatus {
	/**
	 * The upgrade type designated for this deployment. Possible values are Auto
	 * and Manual.
	 */
	private UpgradeType upgradeType;

	/**
	 * The state of the current upgrade domain. Possible values are Before and
	 * During.
	 */
	private CurrentUpgradeDomainState currentUpgradeDomainState;

	/**
	 * An integer value that identifies the current upgrade domain. Upgrade
	 * domains are identified with a zero-based index: the first upgrade domain
	 * has an ID of 0, the second has an ID of 1, and so on.
	 */
	private int currentUpgradeDomain;

	/**
	 * @return the upgradeType of UpgradeStatus.
	 */
	public UpgradeType getUpgradeType() {
		return upgradeType;
	}

	/**
	 * Set the upgradeType of UpgradeStatus.
	 * 
	 * @param upgradeType
	 */
	public void setUpgradeType(UpgradeType upgradeType) {
		this.upgradeType = upgradeType;
	}

	/**
	 * @return the currentUpgradeDomainState of UpgradeStatus.
	 */
	public CurrentUpgradeDomainState getCurrentUpgradeDomainState() {
		return currentUpgradeDomainState;
	}

	/**
	 * Set the currentUpgradeDomainState of UpgradeStatus.
	 * 
	 * @param currentUpgradeDomainState
	 */
	public void setCurrentUpgradeDomainState(
			CurrentUpgradeDomainState currentUpgradeDomainState) {
		this.currentUpgradeDomainState = currentUpgradeDomainState;
	}

	/**
	 * @return the currentUpgradeDomain of UpgradeStatus.
	 */
	public int getCurrentUpgradeDomain() {
		return currentUpgradeDomain;
	}

	/**
	 * Set the currentUpgradeDomain of UpgradeStatus.
	 * 
	 * @param currentUpgradeDomain
	 */
	public void setCurrentUpgradeDomain(int currentUpgradeDomain) {
		this.currentUpgradeDomain = currentUpgradeDomain;
	}

}
