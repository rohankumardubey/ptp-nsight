//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.03 at 07:31:36 PM CST 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="file-source-location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="file-staging-location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{}managed-file" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "fileSourceLocation", "fileStagingLocation", "managedFile" })
@XmlRootElement(name = "managed-files")
public class ManagedFiles {

	@XmlElement(name = "file-source-location")
	protected String fileSourceLocation;
	@XmlElement(name = "file-staging-location")
	protected String fileStagingLocation;
	@XmlElement(name = "managed-file", required = true)
	protected List<ManagedFile> managedFile;

	/**
	 * Gets the value of the fileSourceLocation property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getFileSourceLocation() {
		return fileSourceLocation;
	}

	/**
	 * Gets the value of the fileStagingLocation property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getFileStagingLocation() {
		return fileStagingLocation;
	}

	/**
	 * Gets the value of the managedFile property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the managedFile property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getManagedFile().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ManagedFile }
	 * 
	 * 
	 */
	public List<ManagedFile> getManagedFile() {
		if (managedFile == null) {
			managedFile = new ArrayList<ManagedFile>();
		}
		return this.managedFile;
	}

	/**
	 * Sets the value of the fileSourceLocation property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setFileSourceLocation(String value) {
		this.fileSourceLocation = value;
	}

	/**
	 * Sets the value of the fileStagingLocation property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setFileStagingLocation(String value) {
		this.fileStagingLocation = value;
	}

}
