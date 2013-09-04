/* Class to hold the data of all the fields mentioned in parser configuration file.
 * Parser Config Files specify what fields to extract from the specified Source File 
 * in XML Format.
 * For a CSV File Extractor the config file will have the name of the column
 * and the name to which the column name has to be transformed and likewise
 * for XLS, XLSX files. For XML file the header.
 */
package edu.usc.pgroup.floe.applications.iipipeline.pellets.parsing;
public class ParserExtractorData 
{
	private String headerName;
	private String headerTransform;
	private int colNum;	
	public String getHeaderName()
	{
		return this.headerName;
	}
	public String getHeaderTransform()
	{
		return this.headerTransform;
	}
	public int getColNum()
	{
		return this.colNum;
	}
	public void setColNum(int inpCol)
	{
		this.colNum = inpCol;
	}
	public void setHeaderName(String inpHead)
	{
		this.headerName = inpHead;		
	}
	public void setHeaderTransform(String inpTransform)
	{
		this.headerTransform = inpTransform;
	}
}
