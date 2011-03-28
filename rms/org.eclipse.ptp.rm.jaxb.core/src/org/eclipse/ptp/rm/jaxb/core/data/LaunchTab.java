//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.26 at 05:14:19 PM CDT 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * A recursive description of the UI element associated with the control part,
 * the Launch Tab. As in:
 * org.eclipse.ptp.rm.ui.launch.ExtendableRMLaunchConfigurationDynamicTab,
 * provides a top-level "switch" between controllers.
 * 
 * 
 * <p>
 * Java class for launch-tab complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="launch-tab">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="basic" type="{http://org.eclipse.ptp/rm}tab-controller" minOccurs="0"/>
 *         &lt;element name="advanced" type="{http://org.eclipse.ptp/rm}tab-controller" minOccurs="0"/>
 *         &lt;element name="custom-controller" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "launch-tab", propOrder = { "basic", "advanced", "customController" })
public class LaunchTab {

	protected TabController basic;
	protected TabController advanced;
	@XmlElement(name = "custom-controller")
	protected String customController;

	/**
	 * Gets the value of the advanced property.
	 * 
	 * @return possible object is {@link TabController }
	 * 
	 */
	public TabController getAdvanced() {
		return advanced;
	}

	/**
	 * Gets the value of the basic property.
	 * 
	 * @return possible object is {@link TabController }
	 * 
	 */
	public TabController getBasic() {
		return basic;
	}

	/**
	 * Gets the value of the customController property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCustomController() {
		return customController;
	}

	/**
	 * Sets the value of the advanced property.
	 * 
	 * @param value
	 *            allowed object is {@link TabController }
	 * 
	 */
	public void setAdvanced(TabController value) {
		this.advanced = value;
	}

	/**
	 * Sets the value of the basic property.
	 * 
	 * @param value
	 *            allowed object is {@link TabController }
	 * 
	 */
	public void setBasic(TabController value) {
		this.basic = value;
	}

	/**
	 * Sets the value of the customController property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCustomController(String value) {
		this.customController = value;
	}

}