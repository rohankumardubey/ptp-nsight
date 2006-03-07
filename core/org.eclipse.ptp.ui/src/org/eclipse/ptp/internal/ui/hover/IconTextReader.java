/*******************************************************************************
 * Copyright (c) 2005 The Regents of the University of California. 
 * This material was produced under U.S. Government contract W-7405-ENG-36 
 * for Los Alamos National Laboratory, which is operated by the University 
 * of California for the U.S. Department of Energy. The U.S. Government has 
 * rights to use, reproduce, and distribute this software. NEITHER THE 
 * GOVERNMENT NOR THE UNIVERSITY MAKES ANY WARRANTY, EXPRESS OR IMPLIED, OR 
 * ASSUMES ANY LIABILITY FOR THE USE OF THIS SOFTWARE. If software is modified 
 * to produce derivative works, such modified software should be clearly marked, 
 * so as not to confuse it with the version available from LANL.
 * 
 * Additionally, this program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * LA-CC 04-115
 *******************************************************************************/
package org.eclipse.ptp.internal.ui.hover;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.ptp.ui.hover.IIconHoverTag;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Clement chu
 * 
 */
public class IconTextReader extends SubstitutionTextReader {
	private static final String EMPTY_STRING= "";
	private static final Map fgEntityLookup;
	private static final Set fgTags;
	
	private Color red_color = Display.getDefault().getSystemColor(SWT.COLOR_RED);
	private Color green_color = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);

	static {
		fgTags= new HashSet();
		fgTags.add(IIconHoverTag.UNDERLINE_TAG);
		fgTags.add(IIconHoverTag.STRIKE_TAG);
		fgTags.add(IIconHoverTag.BOLD_TAG);
		fgTags.add(IIconHoverTag.ITALIC_TAG);
		fgTags.add(IIconHoverTag.P_TAG);
		fgTags.add(IIconHoverTag.KEY_TAG);
		fgTags.add(IIconHoverTag.HIGHLIGHT_TAG);
		fgTags.add(IIconHoverTag.NEXT_LINE_TAG);
		fgTags.add(IIconHoverTag.INDENT_TAG);

		fgEntityLookup= new HashMap(7);
		fgEntityLookup.put("lt", "<");
		fgEntityLookup.put("gt", ">");
		fgEntityLookup.put("nbsp", " ");
		fgEntityLookup.put("amp", "&");
		fgEntityLookup.put("circ", "^");
		fgEntityLookup.put("tilde", "~");
		fgEntityLookup.put("quot", "\"");		
	}

	private int fCounter= 0;
	private TextPresentation fTextPresentation;
	private int fStrike= 0;
	private int fUnderline= 0;
	private int fBold= 0;
	private int fItalic= 0;
	private int fKey= 0;
	private int fHighlight = 0;
	private int fStartOffset= -1;
	private boolean fInParagraph= false;

	public IconTextReader(Reader reader, TextPresentation presentation) {
		super(new PushbackReader(reader));
		fTextPresentation= presentation;
	}

	public int read() throws IOException {
		int c= super.read();
		if (c != -1)
			++ fCounter;
		return c;
	}

	protected void startBold() {
		if (fBold == 0)
			fStartOffset= fCounter;
		++ fBold;
	}
	protected void stopBold() {
		-- fBold;
		if (fBold == 0) {
			fTextPresentation.addStyleRange(new StyleRange(fStartOffset, fCounter - fStartOffset, null, null, SWT.BOLD));
			fStartOffset= -1;
		}
	}

	protected void startUnderLine() {
		if (fUnderline == 0)
			fStartOffset= fCounter;
		++ fUnderline;
	}
	protected void stopUnderLine() {
		-- fUnderline;
		if (fUnderline == 0) {
			StyleRange styleRange = new StyleRange(fStartOffset, fCounter - fStartOffset, null, null, SWT.NORMAL);
			styleRange.underline = true;
			fTextPresentation.addStyleRange(styleRange);
			fStartOffset= -1;
		}
	}

	protected void startStrike() {
		if (fStrike == 0)
			fStartOffset= fCounter;
		++ fStrike;
	}
	protected void stopStrike() {
		-- fStrike;
		if (fStrike == 0) {
			StyleRange styleRange = new StyleRange(fStartOffset, fCounter - fStartOffset, null, null, SWT.NORMAL);
			styleRange.strikeout = true;
			fTextPresentation.addStyleRange(styleRange);
			fStartOffset= -1;
		}
	}
	
	protected void startItalic() {
		if (fItalic == 0)
			fStartOffset= fCounter;
		++ fItalic;
	}
	protected void stopItalic() {
		-- fItalic;
		if (fItalic == 0) {
			fTextPresentation.addStyleRange(new StyleRange(fStartOffset, fCounter - fStartOffset, null, null, SWT.ITALIC));
			fStartOffset= -1;
		}
	}
	
	protected void startKey() {
		if (fKey == 0)
			fStartOffset= fCounter;
		++ fKey;
	}
	protected void stopKey() {
		-- fKey;
		if (fKey == 0) {
			fTextPresentation.addStyleRange(new StyleRange(fStartOffset, fCounter - fStartOffset, red_color, null, SWT.NORMAL));
			fStartOffset= -1;
		}
	}

	protected void startHighlight() {
		if (fHighlight == 0)
			fStartOffset= fCounter;
		++ fHighlight;
	}
	protected void stopHighlight() {
		-- fHighlight;
		if (fHighlight == 0) {
			fTextPresentation.addStyleRange(new StyleRange(fStartOffset, fCounter - fStartOffset, null, green_color, SWT.NORMAL));
			fStartOffset= -1;
		}
	}
	
	protected String computeSubstitution(int c) throws IOException {
		if (c == '<')
			return  processTag();
		else if (c == '&')
			return processEntity();

		return null;
	}

	private String toText(String html) {
		if (html == null || html.length() == 0)
			return EMPTY_STRING;

		String tag= html;
		boolean isClosing = false;
		if (tag.charAt(0) == IIconHoverTag.CLOSED_TAG) {
			tag= tag.substring(1);
			isClosing = true;
		}
		if (!fgTags.contains(tag)) {
			return EMPTY_STRING;
		}

		if (tag.equals(IIconHoverTag.INDENT_TAG)) {
			return "\t" + EMPTY_STRING;
		}

		if (tag.equals(IIconHoverTag.NEXT_LINE_TAG)) {
			return LINE_DELIM;
		}

		if (tag.equals(IIconHoverTag.BOLD_TAG)) {
			if (isClosing)
				stopBold();
			else
				startBold();

			return EMPTY_STRING;
		}

		if (tag.equals(IIconHoverTag.UNDERLINE_TAG)) {
			if (isClosing)
				stopUnderLine();
			else
				startUnderLine();

			return EMPTY_STRING;
		}

		if (tag.equals(IIconHoverTag.STRIKE_TAG)) {
			if (isClosing)
				stopStrike();
			else
				startStrike();

			return EMPTY_STRING;
		}

		if (tag.equals(IIconHoverTag.ITALIC_TAG)) {
			if (isClosing)
				stopItalic();
			else
				startItalic();

			return EMPTY_STRING;
		}

		if (tag.equals(IIconHoverTag.KEY_TAG)) {
			if (isClosing)
				stopKey();
			else
				startKey();

			return EMPTY_STRING;
		}

		if (tag.equals(IIconHoverTag.HIGHLIGHT_TAG)) {
			if (isClosing)
				stopHighlight();
			else
				startHighlight();

			return EMPTY_STRING;
		}
		
		if (tag.equals(IIconHoverTag.P_TAG)) {
			if (isClosing) {
				boolean inParagraph= fInParagraph;
				fInParagraph= false;
				return inParagraph ? EMPTY_STRING : LINE_DELIM;
			}
			else {
				fInParagraph= true;
				return LINE_DELIM;
			}
		}
		return EMPTY_STRING;
	}

	private String processTag() throws IOException {
		StringBuffer buf= new StringBuffer();
		int ch= nextChar();
		while (ch != -1 && ch != IIconHoverTag.END_TAG) {
			buf.append(Character.toLowerCase((char) ch));
			ch= nextChar();
			if (ch == '"'){
				buf.append(Character.toLowerCase((char) ch));
				ch= nextChar();
				while (ch != -1 && ch != '"'){
					buf.append(Character.toLowerCase((char) ch));
					ch= nextChar();
				}
			}
			if (ch == IIconHoverTag.START_TAG){
				unread(ch);
				return IIconHoverTag.START_TAG + buf.toString();
			}
		}
		if (ch == -1)
			return null;
			
		return toText(buf.toString());
	}

	private void unread(int ch) throws IOException {
		((PushbackReader) getReader()).unread(ch);
	}

	protected String entity2Text(String symbol) {
		if (symbol.length() > 1 && symbol.charAt(0) == '#') {
			int ch;
			try {
				if (symbol.charAt(1) == 'x') {
					ch= Integer.parseInt(symbol.substring(2), 16);
				} else {
					ch= Integer.parseInt(symbol.substring(1), 10);
				}
				return EMPTY_STRING + (char)ch;
			} catch (NumberFormatException e) {
			}
		} else {
			String str= (String) fgEntityLookup.get(symbol);
			if (str != null) {
				return str;
			}
		}
		return "&" + symbol; // not found
	}

	private String processEntity() throws IOException {
		StringBuffer buf= new StringBuffer();
		int ch= nextChar();
		while (Character.isLetterOrDigit((char)ch) || ch == '#') {
			buf.append((char) ch);
			ch= nextChar();
		}

		if (ch == ';')
			return entity2Text(buf.toString());

		buf.insert(0, '&');
		if (ch != -1)
			buf.append((char) ch);
		return buf.toString();
	}
}
