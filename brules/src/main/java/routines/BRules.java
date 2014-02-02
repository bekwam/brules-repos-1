/*
 * Copyright (C) 2011-2014 Bekwam, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package routines;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * BRules is a collection of Java methods used for validation, formatting, and 
 * transformation.
 * 
 * @author Carl2
 * @since 1.0.0
 */
public class BRules {

	private final static String ERROR_MESSAGE_LISTTYPE_ARG = "listType must be 'ul' or 'ol'";		
	private final static String UTF8_CHARSET = "UTF-8";	
	private final static int DEFAULT_PAD_SIZE = 10;
	
    /**
     * isPhoneNum: true if valid in accordance with country specifier; uses
     * strict check
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} string("regionCode") input: The country or region code to use
     * {param} string("phoneNumber") input: The phone number to check
     * 
     * {example} isPhoneNum("US", "(301) 555-5555") # true
     */
	public static boolean isPhoneNum(String _countryCode, String _toValidate) {
		return isPhoneNum(_countryCode, _toValidate, false);
	}

    /**
     * isPhoneNum: true if valid in accordance with country specifier and
     * loose flag (false for strict)
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} string("regionCode") input: The country or region code to use
     * {param} string("phoneNumber") input: The phone number to check
     * {param} boolean: use loose validation (true) or strict (false)
     * 
     * {example} isPhoneNum("US", "(301) 555-5555") # true
     */
	public static boolean isPhoneNum(String _countryCode, String _toValidate, boolean _loose) {
		
		boolean valid = false;

		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		
		try {
			PhoneNumber pn = phoneUtil.parse(_toValidate, _countryCode);
			if( _loose ) {
				valid = phoneUtil.isPossibleNumber(pn);
			}
			else {
				valid = phoneUtil.isValidNumber(pn);
			}
		}
		catch(NumberParseException ignore) {}
		
		return valid;
	}
	
    /**
     * all: true if all arguments are not empty (not null for Objects,
     * not null, empty string, or whitespace for java.lang.String)
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} object() input: a variable number of params to check
     * 
     * {example} all("one", "two", "")  # false
     * {example} all("one") # true
     */
	public static boolean all(Object..._objects) {
		boolean allSet = true;
		if( _objects == null ) return false;
		for( Object obj : _objects) {
			if( obj instanceof String ) {
				if( StringUtils.isEmpty((String)obj) ) {
					allSet = false;
					break;
				}
			}
			else {
				if( obj == null ) {
					allSet = false;
					break;
				}
			}
		}
		return allSet;
	}
	
    /**
     * xor: true if one and only one argument is not empty (not null for Objects,
     * not null, empty string, or whitespace for java.lang.String)
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} object() input: a variable number of params to check
     * 
     * {example} xor("one", "")  # true
     * {example} xor("", "") # false
     */
	public static boolean xor(Object..._objects) {
		boolean onlyOneSet = false;
		if( _objects == null ) return false;
		for( Object obj : _objects) {
			if( obj instanceof String ) {
				if( StringUtils.isNotEmpty((String)obj) ) {
					if( !onlyOneSet ) {
						onlyOneSet = true;
					}
					else {
						onlyOneSet = false;
						break;
					}
				}
			}
			else {
				if( obj != null ) {
					if( !onlyOneSet ) {
						onlyOneSet = true;
					}
					else {
						onlyOneSet = false;
						break;
					}
				}
			}
		}
		return onlyOneSet;
	}
	
	/**
	 * isBlank: true if the string is null, the empty string, or whitespace
	 * 
	 * {talendTypes} String
	 * 
	 * {Category} BRules
	 * 
	 * {param} string("hello") input : string to be tested
	 * 
	 * {example} isBlank("hello") # false
	 * {example} isBlank(null) # true
	 * {example} isBlank("") # true
	 * {example} isBlank("     ") # true
	 * 
	 */
	public static boolean isBlank(String _s) { 
		return StringUtils.isBlank(_s);
	}
	
    /**
     * isXML: true if the passed-in string adheres to XML well-formedness
     * and the specified charset
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} string("<message>hello</message>") input: xml to be tested
     * {param} string("ISO8859_1") input: the charset of the xml
     * 
     * {example} isXML("<message>hello</message>", "ISO8859_1") # true
     * {example} isXML("<message>hello", "ISO8859_1") # false
     */
	public static boolean isXML(String _xml, String _charset) throws Exception {
		
		boolean result = false;
		
		if( StringUtils.isEmpty(_xml) ) return false;
		
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true);

			SAXParser parser = factory.newSAXParser();
			
			InputStream is = new ByteArrayInputStream(_xml.getBytes(_charset));
			
			parser.parse(is, new DefaultHandler());
			
			result = true;
		}
		catch(SAXParseException ignore) {}
		
		return result;
	}
	
    /**
     * isXML: true if the passed-in string adheres to XML well-formedness
     * using a UTF-8 string
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} string("<message>hello</message>") input: xml to be tested
     * 
     * {example} isXML("<message>hello</message>") # true
     * {example} isXML("<message>hello") # false
     */
	public static boolean isXML(String _xml) throws Exception {
		return isXML(_xml, UTF8_CHARSET);
	}
	
	/**
	 * okChars: true if the passed-in string is valid for the specified 
	 * character set
	 * 
	 * The supported character set values are those supported by Java.  The 
	 * particular constants include "ASCII", "Cp1252", "ISO8859_1", "UTF-8".
	 * 
	 * If the string parameter is empty or null, 'true' is returned.
	 * 
	 * {talendTypes} String
	 * 
	 * {Category} BRules
	 * 
	 * {param} string("hello") input: string to be tested
	 * {param} string("charset") input: charset of string
	 * 
	 * {example} okChar("hello", "ISO8859_1") # true
	 */
	public static boolean okChars(String _s, String _charset) {
		if( StringUtils.isEmpty(_s) ) { return true; }
		CharsetEncoder encoder = Charset.forName(_charset).newEncoder();
		return encoder.canEncode(_s);
	}
	
	/**
	 * toCharset: convert a string to the character set used by Windows Latin-1.
	 * Will convert unmappable characters to a space (" ") rather than throwing
	 * an error
	 * 
	 * For example, this will conveniently map a \u2122 (TM) symbol to a 
	 * Windows-recognized hex 99
	 * 
	 * {talendTypes} String
	 * 
	 * {Category} BRules
	 * 
	 * {param} string(_s) input string to convert
	 * {example} toCharset("My Product\u2122") # returns "My Product "
	 */
	public static String toCharset(String _s) {
		return toCharset(_s, "Cp1252");
	}
	
	/**
	 * toCharset: convert a string to the specified character set
	 * Will convert unmappable characters to a space (" ") rather than throwing
	 * an error
	 * 
	 * For example, this will conveniently map a \u2122 (TM) symbol to a 
	 * Windows-recognized hex 99
	 * 
	 * Uses Java names for charsets: Cp1252, US-ASCII
	 * {talendTypes} String
	 * 
	 * {Category} BRules
	 * 
	 * {param} string(_s) input string to convert
	 * {param} string(_charset) character set to use for conversion
	 * {example} toCharset("My Product\u2122", "Cp1252") # returns "My Product "
	 */
	public static String toCharset(String _s, String _charset) {
		String cs = (StringUtils.isEmpty(_charset))?"Cp1252":_charset;
		return toCharset(_s,cs," ");
	}
	
	/**
	 * toCharset: convert a string to the specified character set
	 * 
	 * Will convert unmappable characters to a specified character
	 * 
	 * For example, this will conveniently map a \u2122 (TM) symbol to a 
	 * Windows-recognized hex 99
	 * 
	 * Uses Java names for charsets: Cp1252, US-ASCII, ISO-8859-1, UTF-8, UTF-16
	 * 
	 * {talendTypes} String
	 * 
	 * {Category} BRules
	 * 
	 * {param} string(_s) input string to convert
	 * {param} string(_charset) character set to use for conversion
	 * {param} string(_replaceCh) character to use for unmappables
	 * {example} toCharset("My Product\u2122", "Cp1252", "?") # returns "My Product?"
	 */
	public static String toCharset(String _s, String _charset, String _replaceCh) {
		String s = "";
		String cs = (StringUtils.isEmpty(_charset))?"Cp1252":_charset;
		String rc = (StringUtils.isEmpty(_replaceCh))?" ":_replaceCh;
				
		try {
			CharsetEncoder enc = Charset.forName(cs).newEncoder();
			enc.onUnmappableCharacter(CodingErrorAction.REPLACE);
			enc.replaceWith( rc.getBytes() );
			ByteBuffer buf  = enc.encode(CharBuffer.wrap(_s));		
			s = new String(buf.array(),cs);
		}
		catch (Exception ignore) {}
		
		return s;
	}

    /**
     * isJSON: true if the passed-in string adheres to JSON format
     * 
     * Not supported for JDKs < 6; will throw exception
     * 
     * Empty expressions - {} and [] - will return true
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} string("{name: 'Carl', program: 'BRules'}") input: json to be tested
     * 
     * {example} isJSON("<message>hello</message>") # false
     * {example} isJSON("{name: 'Carl', program: 'BRules'}") # true
     * {example} isJSON("{}") #true
     * {example} isJSON(null) #false
     */
	public static boolean isJSON(String _json) {		
		return BRulesJSON.isJSON(_json);
	}
	
    /**
     * hasJSONPath: true if the passed-in json has elements referenced in path
     * 
     * Not supported for JDKs < 6; will throw exception
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} string("{name: 'Carl', program: 'BRules'}") input: json to be tested
     * 
     * {example} hasJSONPath("<message>hello</message>") # false
     * {example} hasJSONPath("{name: 'Carl', program: 'BRules'}", "$.name") # true
     * {example} isJSON("{}") #true
     * {example} hasJSONPath(null) #false
     */
	public static boolean hasJSONPath(String _json, String _path) throws Exception {
		return BRulesJSON.hasJSONPath(_json, _path);
	}
	
    /**
     * comma: join the string representation of objects together with a
     * comma
     * 
     * Appends an empty string where an element is null
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} object() input: a variable number of strings to join
     * 
     * {example} comma("one", "two", "three")  # "one,two,three"
     */
	public static String comma(Object..._objects) {
		return join(",", _objects);
	}
	
    /**
     * join: join the string representation of objects together with a
     * character
     * 
     * Appends an empty string where an element is null
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} object() input: a variable number of strings to join
     * 
     * {example} join("one", "two", "three")  # "one,two,three"
     */
	public static String join(String _delim, Object..._objects) {
		StringBuffer sb = new StringBuffer();
		boolean firstPass = true;
		if( _objects != null ) {
			for( Object obj : _objects) {
				
				if( !firstPass ) {
					sb.append(_delim);
				} else {
					firstPass = false;
				}
				
				if( obj == null ) {
					sb.append("");
				}
				else {
					sb.append( obj.toString() );
				}
			}
		}
		return sb.toString();
	}

	/**
     * ul: form an html list of the specified css style from
     * the list of objects
     * 
     * null objects returns an empty list (ex, "<ul></ul>")
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} styleClass : a style to apply for the toplevel list element
     * {param} object() input: a variable number of strings to join
     * 
     * {example} ul("infolist", "a", "b")  # "<ul><li>a</li><li>b</li></ul>"
     */
	public static String ul(String _styleClass, Object..._objects) {
		return htmlList(_styleClass, "ul", _objects);
	}
	
    /**
     * ol: form an html list of the specified css style from
     * the list of objects
     * 
     * null objects returns an empty list (ex, "<ol></ol>")
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} styleClass : a style to apply for the toplevel list element
     * {param} object() input: a variable number of strings to join
     * 
     * {example} ol("infolist", "a", "b")  # "<ol><li>a</li><li>b</li></ol>"
     */
	public static String ol(String _styleClass, Object..._objects) {
		return htmlList(_styleClass, "ol", _objects);
	}

	protected static String htmlList(String _styleClass, String _listType, Object..._objects) {
		
		if( _listType == null || 
				!(_listType.equals("ul") || _listType.equals("ol")) ) {
				throw new IllegalArgumentException(ERROR_MESSAGE_LISTTYPE_ARG);
		}
			
		StringBuffer sb = new StringBuffer();
			
		if( _styleClass != null && _styleClass.length() > 0 ) {
			sb.append("<" + _listType + " class=\"" + _styleClass + "\">"  );
		}
		else {
			sb.append("<" + _listType + ">");
		}
			
		if( _objects != null ) {
			for( Object obj : _objects) {
				sb.append("<li>" + ((obj==null)?"":obj.toString()) + "</li>");
			}
		}
			
		sb.append("</" + _listType + ">");
		return sb.toString();
	}
	
    /**
     * p: form an html paragraph of the specified css style from
     * the list of objects
     * 
     * null objects returns an empty list (ex, "<p></p>")
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} styleClass : a style to apply for the toplevel list element
     * {param} object() input: a variable number of strings to join
     * 
     * {example} p("note", "a")  # "<p class=\"note\">a</p>"
     */
	public static String p(String _styleClass, String _text) {
		StringBuffer sb = new StringBuffer();
		if( _styleClass != null && _styleClass.length() > 0 ) {
			sb.append("<p class=\"" + _styleClass + "\">"  );
		}
		else {
			sb.append("<p>");
		}
		sb.append((_text==null)?"":_text);
		sb.append("</p>");
		return sb.toString();
	}

    /**
     * Pad a 10 character string with spaces
     * 
     * If the string exceeds 10 chars, the input string will be returned
     * 
     * The method is deprecated because of the limited utility in a predefined
     * size.
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} pad(stringToPad) input: The string to be divided
     * 
     * {example} pad("100") # "         100"
     */		
	@Deprecated
	public static String pad(String s) {
		return pad(s, DEFAULT_PAD_SIZE);
	}
	
    /**
     * Left pads the input string with spaces
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} string(stringToPad) stringToPad: string to pad 
     * {param} int(numPadChars) numPadChars: number of padded chars
     * 
     * {example} pad("100",6) # "000100"
     * 
     * @param s - input string to pad
     * @param size - number of chars to pad
     * @return padded string or input string
     * @since 1.0.0
     */		
	public static String pad(String s, int size) {
		if( size < 0 ) throw new IllegalArgumentException("size must be > 0");
		if( StringUtils.isEmpty(s) ) return s;
		return StringUtils.leftPad(s, size);
	}	
	
    /**
     * Left pads the input string with the specified character
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} string(stringToPad) stringToPad: string to pad 
     * {param} int(numPadChars) numPadChars: number of padded chars
     * {param} char(charToUse) charToUse: char to use as padding
     * 
     * {example} pad("100", 6, '0') # "000100"
     * 
     * @param s - input string to pad
     * @param size - number of chars to pad
     * @param ch - character to pad with
     * @return padded string or input string
     * @since 1.4.0
     */		
	public static String pad(String s, int size, char ch) {
		if( size < 0 ) throw new IllegalArgumentException("size must be > 0");
		if( StringUtils.isEmpty(s) ) return s;
		return StringUtils.leftPad(s, size, ch);
	}	
	
    /**
     * Left pads the input integer with the specified character
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} int(integerToPad) integerToPad: int to pad 
     * {param} int(numPadChars) numPadChars: number of padded chars
     * {param} char(charToUse) charToUse: char to use as padding
     * 
     * {example} pad(100, 6, '0') # "000100"
     * 
     * @param s - input string to pad
     * @param size - number of chars to pad
     * @param ch - character to pad with
     * @return padded string or input string
     * @since 1.4.0
     */		
	public static String pad(Integer i, int size, char ch) {
		if( i == null ) return null;
		return StringUtils.leftPad(String.valueOf(i), size, ch);
	}	

    /**
     * Left pads the input long with the specified character
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} long(integerToPad) integerToPad: int to pad 
     * {param} long(numPadChars) numPadChars: number of padded chars
     * {param} char(charToUse) charToUse: char to use as padding
     * 
     * {example} pad(100L, 6, '0') # "000100"
     * 
     * @param s - input string to pad
     * @param size - number of chars to pad
     * @param ch - character to pad with
     * @return padded string or input string
     * @since 1.4.0
     */		
	public static String pad(Long lng, int size, char ch) {
		if( lng == null ) return null;
		return StringUtils.leftPad(String.valueOf(lng), size, ch);
	}	
	
	/**
     * Right pads the input string with spaces
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} string(stringToPad) stringToPad: string to pad 
     * {param} int(numPadChars) numPadChars: number of padded chars
     * 
     * {example} pad("100", 6) # "100   "
     * 
     * @param s - input string to pad
     * @param size - number of chars to pad
     * @return padded string or input string
     * @since 1.4.0
     */		
	public static String padRight(String s, int size) {
		if( size < 0 ) throw new IllegalArgumentException("size must be > 0");
		if( StringUtils.isEmpty(s) ) return s;
		return StringUtils.rightPad(s, size);
	}	

	/**
     * Right pads the input string with the specified character
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} string(stringToPad) stringToPad: string to pad 
     * {param} int(numPadChars) numPadChars: number of padded chars
     * {param} char(charToUse) charToUse: char to use as padding
     * 
     * {example} pad("100", 6, '0') # "100000"
     * 
     * @param s - input string to pad
     * @param size - number of chars to pad
     * @param ch - character to pad with
     * @return padded string or input string
     * @since 1.4.0
     */		
	public static String padRight(String s, int size, char ch) {
		if( size < 0 ) throw new IllegalArgumentException("size must be > 0");
		if( StringUtils.isEmpty(s) ) return s;
		return StringUtils.rightPad(s, size, ch);
	}	
	
	/**
     * Take off leading zeros; assumes a number
     * 
     * {talendTypes} String
     * 
     * {Category} BRules
     * 
     * {param} trimLeadingZeros(string) input: The string to be divided
     * 
     */		
	public static String trimLeadingZeros(String num_s) {
		
		if( num_s == null || num_s.length() == 0 ) return "";
		
		try {
			int i = Integer.parseInt(num_s);
			return String.valueOf(i);
		}
		catch(NumberFormatException exc) {
			return num_s;
		}
	}
}
