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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BRulesJSONTest {


	@Test
	public void testIsJSONNull() {
		boolean b = BRulesJSON.isJSON(null);
		assertFalse(b);
	}
	
	@Test
	public void testIsJSONEmptyString() {
		boolean b = BRulesJSON.isJSON("");
		assertFalse(b);
	}
	
	@Test
	public void testIsJSONEmpty() {
		boolean b = BRulesJSON.isJSON("{ }");
		assertTrue(b);
		b = BRulesJSON.isJSON("{}");
		assertTrue(b);
		b = BRulesJSON.isJSON("[ ]");
		assertTrue(b);
		b = BRulesJSON.isJSON("[]");
		assertTrue(b);
	}

	
	@Test
	public void testIsJSONSimple() {
		boolean b = BRulesJSON.isJSON("{name: \"Carl\"}");
		assertTrue(b);
	}
	
	@Test
	public void testIsJSONArray() {
		boolean b = BRulesJSON.isJSON("['BRules 1', 'BRules2']");
		assertTrue(b);
	}

	@Test
	public void testIsJSONNested() {
		boolean b = BRulesJSON.isJSON("{ [{\"name\": \"Carl\"}, {\"program\": \"BRules\"}] }");
		assertTrue(b);
	}

	@Test
	public void hasJSONPathNull() throws Exception {
		String json = null;
		String jsonPath = "$.name";
		boolean b = BRulesJSON.hasJSONPath(json, jsonPath);
		assertFalse(b);
	}
	
	@Test
	public void hasJSONPathEmptyString() throws Exception {
		String json = "";
		String jsonPath = "$.name";
		boolean b = BRulesJSON.hasJSONPath(json, jsonPath);
		assertFalse(b);
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void hasJSONPathNullPath() throws Exception {
		String json = "{ \"name\": \"carl\" }";
		String jsonPath = null;
		BRulesJSON.hasJSONPath(json, jsonPath);
	}

	@Test(expected=java.lang.IllegalArgumentException.class)
	public void hasJSONPathEmptyStringPath() throws Exception {
		String json = "{ \"name\": \"carl\" }";
		String jsonPath = "";
		BRulesJSON.hasJSONPath(json, jsonPath);
	}

	@Test
	public void hasJSONHasSingle() throws Exception {
		String json = "{ \"name\": \"carl\", \"program\": \"brules\" }";
		String jsonPath = "$.name";
		boolean b = BRulesJSON.hasJSONPath(json, jsonPath);
		assertTrue(b);
		json = "{\"name\":\"carl\"}";
		b = BRulesJSON.hasJSONPath(json, jsonPath);
		assertTrue(b);
	}

	@Test
	public void hasJSONHasMulti() throws Exception {
		String json = "{items: [{'name': 'carl'}, {'name': 'jim'}]}";
		String jsonPath = "$.items[*].name";
		boolean b = BRulesJSON.hasJSONPath(json, jsonPath);
		assertTrue(b);
		json = "{items: [{\"name\": \"carl\"}, {\"name\": \"jim\"}]}";
		b = BRulesJSON.hasJSONPath(json, jsonPath);
		assertTrue(b);
	}

	@Test
	public void hasJSONNoMatch() throws Exception {
		String json = "{ \"firstName\": \"carl\" }";
		String jsonPath = "$.name";
		boolean b = BRulesJSON.hasJSONPath(json, jsonPath);
		assertFalse(b);
	}

	@Test
	public void hasJSONNoMultiMatch() throws Exception {
		String json = "{items: [{'name': 'carl'}, {'name': 'jim'}]}";
		String jsonPath = "$.a[*].name";
		boolean b = BRulesJSON.hasJSONPath(json, jsonPath);
		assertFalse(b);
	}
	
	@Test
	public void hasJSONInvalidPath() throws Exception {
		String json = "{items: [{'name': 'carl'}, {'name': 'jim'}]}";
		String jsonPath = "$.a[-].name";
		boolean b = BRulesJSON.hasJSONPath(json, jsonPath);
		assertFalse(b);		
	}	
}
