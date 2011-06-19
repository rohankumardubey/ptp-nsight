//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.19 at 09:56:31 AM CDT 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for grid-data-type complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="grid-data-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="grabExcessHorizontal" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="grabExcessVertical" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="heightHint" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="horizontalAlign" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="horizontalIndent" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="horizontalSpan" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="minHeight" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="minWidth" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="style" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="verticalAlign" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="verticalIndent" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="verticalSpan" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="widthHint" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grid-data-type")
public class GridDataType {

	@XmlAttribute
	protected Boolean grabExcessHorizontal;
	@XmlAttribute
	protected Boolean grabExcessVertical;
	@XmlAttribute
	protected Integer heightHint;
	@XmlAttribute
	protected String horizontalAlign;
	@XmlAttribute
	protected Integer horizontalIndent;
	@XmlAttribute
	protected Integer horizontalSpan;
	@XmlAttribute
	protected Integer minHeight;
	@XmlAttribute
	protected Integer minWidth;
	@XmlAttribute
	protected String style;
	@XmlAttribute
	protected String verticalAlign;
	@XmlAttribute
	protected Integer verticalIndent;
	@XmlAttribute
	protected Integer verticalSpan;
	@XmlAttribute
	protected Integer widthHint;

	/**
	 * Gets the value of the heightHint property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getHeightHint() {
		return heightHint;
	}

	/**
	 * Gets the value of the horizontalAlign property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getHorizontalAlign() {
		return horizontalAlign;
	}

	/**
	 * Gets the value of the horizontalIndent property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getHorizontalIndent() {
		return horizontalIndent;
	}

	/**
	 * Gets the value of the horizontalSpan property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getHorizontalSpan() {
		return horizontalSpan;
	}

	/**
	 * Gets the value of the minHeight property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getMinHeight() {
		return minHeight;
	}

	/**
	 * Gets the value of the minWidth property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getMinWidth() {
		return minWidth;
	}

	/**
	 * Gets the value of the style property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * Gets the value of the verticalAlign property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getVerticalAlign() {
		return verticalAlign;
	}

	/**
	 * Gets the value of the verticalIndent property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getVerticalIndent() {
		return verticalIndent;
	}

	/**
	 * Gets the value of the verticalSpan property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getVerticalSpan() {
		return verticalSpan;
	}

	/**
	 * Gets the value of the widthHint property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getWidthHint() {
		return widthHint;
	}

	/**
	 * Gets the value of the grabExcessHorizontal property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isGrabExcessHorizontal() {
		if (grabExcessHorizontal == null) {
			return true;
		} else {
			return grabExcessHorizontal;
		}
	}

	/**
	 * Gets the value of the grabExcessVertical property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isGrabExcessVertical() {
		if (grabExcessVertical == null) {
			return false;
		} else {
			return grabExcessVertical;
		}
	}

	/**
	 * Sets the value of the grabExcessHorizontal property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setGrabExcessHorizontal(Boolean value) {
		this.grabExcessHorizontal = value;
	}

	/**
	 * Sets the value of the grabExcessVertical property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setGrabExcessVertical(Boolean value) {
		this.grabExcessVertical = value;
	}

	/**
	 * Sets the value of the heightHint property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setHeightHint(Integer value) {
		this.heightHint = value;
	}

	/**
	 * Sets the value of the horizontalAlign property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setHorizontalAlign(String value) {
		this.horizontalAlign = value;
	}

	/**
	 * Sets the value of the horizontalIndent property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setHorizontalIndent(Integer value) {
		this.horizontalIndent = value;
	}

	/**
	 * Sets the value of the horizontalSpan property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setHorizontalSpan(Integer value) {
		this.horizontalSpan = value;
	}

	/**
	 * Sets the value of the minHeight property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setMinHeight(Integer value) {
		this.minHeight = value;
	}

	/**
	 * Sets the value of the minWidth property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setMinWidth(Integer value) {
		this.minWidth = value;
	}

	/**
	 * Sets the value of the style property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setStyle(String value) {
		this.style = value;
	}

	/**
	 * Sets the value of the verticalAlign property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setVerticalAlign(String value) {
		this.verticalAlign = value;
	}

	/**
	 * Sets the value of the verticalIndent property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setVerticalIndent(Integer value) {
		this.verticalIndent = value;
	}

	/**
	 * Sets the value of the verticalSpan property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setVerticalSpan(Integer value) {
		this.verticalSpan = value;
	}

	/**
	 * Sets the value of the widthHint property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setWidthHint(Integer value) {
		this.widthHint = value;
	}

}
