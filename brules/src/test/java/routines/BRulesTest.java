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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

import routines.BRules;

public class BRulesTest {

	@Test
	public void validPhoneNumber1() {
		assertTrue( BRules.isPhoneNum("US", "(301) 555-5555") );
	}

	@Test
	public void validPhoneNumber1Loose() {
		assertTrue( BRules.isPhoneNum("US", "(301) 555-5555", true) );
	}

	@Test
	public void invalidPhoneNumber1() {
		assertFalse( BRules.isPhoneNum("US", "101") );
	}
	
	@Test
	public void nullPhoneNumber() {
		assertFalse( BRules.isPhoneNum("US", null) );
	}

	@Test
	public void emptyStringPhoneNumber() {
		assertFalse( BRules.isPhoneNum("US", "") );
	}

	@Test
	public void nullArgAll() {
		assertFalse( BRules.all((Object[])null) );		
	}
	
	@Test
	public void allSet() {
		assertTrue( BRules.all("one") );
		assertTrue( BRules.all("one", "two") );
		assertTrue( BRules.all("one", "two", new Long(0L)) );
	}
	
	@Test
	public void noneSet() {
		assertFalse( BRules.all("") );
		assertFalse( BRules.all("one", "") );
		assertFalse( BRules.all("", "two") );
		assertFalse( BRules.all(new Long(0L), null) );
	}
	
	@Test
	public void nullArgXor() {
		assertFalse( BRules.xor((Object[])null) );		
	}
	
	@Test
	public void onlyOneSet() {
		assertTrue( BRules.xor("one", "") );
		assertTrue( BRules.xor("", "two") );
		assertTrue( BRules.xor("", "", new Long(0L)) );
	}
	
	@Test
	public void noneXorSet() {
		assertFalse( BRules.xor("") );
		assertFalse( BRules.xor("", null) );
		assertFalse( BRules.xor(null, "") );
		assertFalse( BRules.xor(null, null) );
	}
	
	@Test
	public void moreThanOneSet() {
		assertFalse( BRules.xor("one", "two") );
		assertFalse( BRules.xor("one", "two", "three") );
	}
	
	@Test
	public void isXML() throws Exception {
		assertTrue( BRules.isXML("<message>hello</message>", "ISO8859_1") );
		assertTrue( BRules.isXML("<message />", "ISO8859_1") );
		assertTrue( BRules.isXML("<message text='hello' />", "ISO8859_1") );	
		assertTrue( BRules.isXML("<?xml version='1.0' encoding='US-ASCII'?><message>hello</message>", "ISO8859_1") );
	}
	
	@Test
	public void isXMLUTF8() throws Exception {
		assertTrue( BRules.isXML("<message>hello</message>") );
		assertTrue( BRules.isXML("<message />") );
		assertTrue( BRules.isXML("<message text='hello' />") );	
		assertTrue( BRules.isXML("<?xml version='1.0' encoding='UTF-8'?><message>hello</message>") );		
	}
	
	@Test
	public void isNotXML() throws Exception {
		assertFalse( BRules.isXML("<message>hello", "ISO8859_1") );
		assertFalse( BRules.isXML("hello", "ISO8859_1"));
		assertFalse( BRules.isXML(null, "ISO8859_1") );		
	}
	
	@Test(expected=java.io.UnsupportedEncodingException.class)
	public void isXMLBadCharset() throws Exception {
		BRules.isXML("<message>hello</message>", "BADCHARSET");
	}
	
	@Test
	public void okChars() {
		assertTrue(BRules.okChars(null, "ISO8859_1"));
		assertTrue(BRules.okChars("", "ISO8859_1"));
		assertTrue(BRules.okChars("hello", "ASCII"));
		assertTrue(BRules.okChars("€100", "Cp1252"));
		assertTrue(BRules.okChars("©2010", "ISO8859_1"));
		assertTrue(BRules.okChars("\u1F00", "UTF-8"));
	}
	
	@Test
	public void notOkChars() {
		assertFalse(BRules.okChars("\u1F00", "Cp1252"));
		assertFalse(BRules.okChars("€100", "ASCII"));
	}
	
	@Test
	public void isBlank() {
		assertTrue(BRules.isBlank(null));
		assertTrue(BRules.isBlank(""));
		assertTrue(BRules.isBlank("    "));
	}
	
	@Test
	public void isNotBlank() {
		assertFalse(BRules.isBlank("hello"));
	}

	// TODO: restore charset tests with a separate Cp1252 file
	public void testToCharset() {
		String s = "Hello, World!ñ?\u2122";
		String s2 = BRules.toCharset(s);
		Pattern p = Pattern.compile("Hello, World!ñ ™");
		assertTrue( p.matcher(s2).matches() );
	}

	// TODO: restore charset tests with a separate Cp1252 file
	public void testToCharset2() {
		String s = "Hello, World!ñ?\u2122";
		String s2 = BRules.toCharset(s, "ISO-8859-1");
		Pattern p = Pattern.compile("Hello, World!ñ  ");
		assertTrue( p.matcher(s2).matches() );
	}

	// TODO: restore charset tests with a separate Cp1252 file
	public void testToCharset3() {
		String s = "Hello, World!ñ?\u2122";
		String s2 = BRules.toCharset(s, "Cp1252", "?");
		Pattern p = Pattern.compile("Hello, World!ñ[?]™");
		assertTrue( p.matcher(s2).matches() );
	}

	// TODO: restore charset tests with a separate Cp1252 file
	public void testToCharsetNoInput() {
		String s = BRules.toCharset(null);
		assertEquals("", s);
		String s2 = BRules.toCharset("");
		assertEquals("", s2);
	}
	
	// TODO: restore charset tests with a separate Cp1252 file
	public void testToCharsetUnknownCharset() {
		String s = "Hello, World!ñ?\u2122";
		String s2 = BRules.toCharset(s, "UNKNOWN", "?");
		assertEquals("", s2);
	}
	
	@Test
	public void commaNull() {
		assertEquals("", BRules.comma((Object[])null) );
	}
	
	@Test
	public void commaEmptyString() {
		assertEquals("", BRules.comma(""));
	}
	
	@Test
	public void commaOneElem() {
		assertEquals("one", BRules.comma("one"));
	}
	
	@Test
	public void commaThreeElems() {
		assertEquals("one,two,three", BRules.comma("one", "two", "three"));
	}
	
	@Test
	public void joinNull() {
		assertEquals("", BRules.join(";", (Object[])null) );
	}
	
	@Test
	public void joinEmptyString() {
		assertEquals("", BRules.join(";", ""));
	}
	
	@Test
	public void joinOneElem() {
		assertEquals("one", BRules.join(";", "one"));
	}
	
	@Test
	public void joinThreeElems() {
		assertEquals("one;two;three", BRules.join(";", "one", "two", "three"));
	}

	@Test
	public void ulNull() {
		assertEquals("<ul></ul>", BRules.ul("", (Object[])null));
	}

	@Test
	public void ulEmptyString() {
		assertEquals("<ul></ul>", BRules.ul("", (Object[])null));
	}
	
	@Test
	public void ul() {
		assertEquals("<ul><li>one</li><li>two</li><li>three</li></ul>", BRules.ul("", "one", "two", "three"));
	}

	@Test
	public void ol() {
		assertEquals("<ol><li>one</li><li>two</li><li>three</li></ol>", BRules.ol("", "one", "two", "three"));
	}

	@Test
	public void ulArray() {
		Object[] objs = new Object[]{ "one", "two", "three" };
		assertEquals("<ul><li>one</li><li>two</li><li>three</li></ul>", BRules.ul("", objs));		
	}

	@Test
	public void olArray() {
		Object[] objs = new Object[]{ "one", "two", "three" };
		assertEquals("<ol><li>one</li><li>two</li><li>three</li></ol>", BRules.ol("", objs));		
	}

	@Test
	public void ulStyled() {
		assertEquals("<ul class=\"myclass\"><li>one</li><li>two</li><li>three</li></ul>", BRules.ul("myclass", "one", "two", "three"));
	}

	@Test
	public void olStyled() {
		assertEquals("<ol class=\"myclass\"><li>one</li><li>two</li><li>three</li></ol>", BRules.ol("myclass", "one", "two", "three"));
	}

	@Test(expected=java.lang.IllegalArgumentException.class)
	public void htmlListBadListType() {
		BRules.htmlList("", "invalidListType", "one");
	}
	
	@Test
	public void pNull() {
		assertEquals("<p></p>", BRules.p("", null));
	}

	@Test
	public void pEmptyString() {
		assertEquals("<p></p>", BRules.p("", ""));
	}
	
	@Test
	public void p() {
		assertEquals("<p>one</p>", BRules.p("", "one"));
	}

	@Test
	public void pStyled() {
		assertEquals("<p class=\"note\">one</p>", BRules.p("note", "one"));
	}

	@Test
	public void pad() {
		String[] a = {
				null, 
				"", 
				"a", 
				"abc", 
				"12345678901"
				};
		assertEquals("          ", BRules.pad(a[0]));
		assertEquals("          ", BRules.pad(a[1]));
		assertEquals("         a", BRules.pad(a[2]));
		assertEquals("       abc", BRules.pad(a[3]));
		assertEquals("12345678901", BRules.pad(a[4]));
	}
	
	@Test
	public void padToSize() {
		String[] a = {
				null, 
				"", 
				"a", 
				"abc", 
				"12345678901"
				};
		assertEquals("       ", BRules.pad(a[0], 7));
		assertEquals("       ", BRules.pad(a[1], 7));
		assertEquals("      a", BRules.pad(a[2], 7));
		assertEquals("    abc", BRules.pad(a[3], 7));
		assertEquals("12345678901", BRules.pad(a[4], 7));
	}
	
	@Test
	public void trimLeadingZeros() {
		String[] a = {
			null,
			"",
			"00101",
			"0",
			"999",
			"abc"
		};
		
		assertEquals("", BRules.trimLeadingZeros(a[0]));
		assertEquals("", BRules.trimLeadingZeros(a[1]));
		assertEquals("101", BRules.trimLeadingZeros(a[2]));
		assertEquals("0", BRules.trimLeadingZeros(a[3]));
		assertEquals("999", BRules.trimLeadingZeros(a[4]));
		assertEquals("abc", BRules.trimLeadingZeros(a[5]));
	}
}
