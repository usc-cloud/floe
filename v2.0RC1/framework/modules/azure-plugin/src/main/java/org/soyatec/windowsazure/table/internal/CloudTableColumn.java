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
package org.soyatec.windowsazure.table.internal;

import org.soyatec.windowsazure.table.ETableColumnType;
import org.soyatec.windowsazure.table.ICloudTableColumn;

/**
 * 
 * A table column represents all attributes in a Azure Table in azure table
 * service. Azure table composed by more columns. One column have 3 attributes:
 * 
 * <ul>
 * <li><strong>name</strong> Column name</li>
 * <li><strong>value</strong> Column value</li>
 * <li><strong>type</strong> Data type of the column. For more about see
 * {@link ETableColumnType}</li>
 * </ul>
 * 
 */
public class CloudTableColumn implements ICloudTableColumn {

	/**
	 * The column name
	 */
	private String name;

	/**
	 * The column value. Column value is represented by their literal value in
	 * {@link String}.
	 */
	private String value;

	/**
	 * Data type of the column. see {@link ETableColumnType}
	 */
	private ETableColumnType type;

	/**
	 * Create a new table column with give name,value and type.
	 * 
	 * @param name
	 * @param value
	 * @param type
	 */
	public CloudTableColumn(String name, String value, ETableColumnType type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}

	/**
	 * Create a new table column.
	 */
	public CloudTableColumn() {

	}

	/* (non-Javadoc)
	 * @see org.soyatec.windowsazure.table.ICloudTableColumn#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.soyatec.windowsazure.table.ICloudTableColumn#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.soyatec.windowsazure.table.ICloudTableColumn#getValue()
	 */
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.soyatec.windowsazure.table.ICloudTableColumn#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see org.soyatec.windowsazure.table.ICloudTableColumn#getType()
	 */
	public ETableColumnType getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see org.soyatec.windowsazure.table.ICloudTableColumn#setType(org.soyatec.windowsazure.table.ETableColumnType)
	 */
	public void setType(ETableColumnType type) {
		this.type = type;
	}

}
