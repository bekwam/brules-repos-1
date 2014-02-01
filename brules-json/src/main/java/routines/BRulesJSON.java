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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * A class of JSON utility functions
 * 
 * @author Carl2
 *
 */
public class BRulesJSON {

	private final static String ERROR_MESSAGE_JRE6 = "you must run at least Java 6 to use this method";
		
	private final static String REGEX_EMPTY_OBJECT = "\\{\\s*\\}";
	private final static String REGEX_EMPTY_ARRAY  = "\\[\\s*\\]";
	
    /**
     * Determines whether or not a String is valid JSON
     * 
     * @param _json - candidate json string
     * @return true if valid json
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean isJSON(String _json) {
		
		if( !SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_6) ) 
			throw new UnsupportedOperationException(ERROR_MESSAGE_JRE6);
		
		if( StringUtils.isEmpty(_json) ) return false;
		
		if( _json.matches(REGEX_EMPTY_OBJECT) ) return true;
		
		if( StringUtils.equals(_json, REGEX_EMPTY_ARRAY) ) return true;
		
		boolean testResult = false;
		
		try {
			
			// re-written using reflection to allow JRE 5 for other methods

			Class clazz = Class.forName("javax.script.ScriptEngineManager");
			
			Method getEngineMethod = clazz.getMethod("getEngineByName", String.class);
			
			Object mgr_obj = clazz.newInstance();
			
			Object eng_obj = getEngineMethod.invoke(mgr_obj, "JavaScript");
			
			Class engClazz = Class.forName("javax.script.ScriptEngine");
			
			Method evalMethod = engClazz.getMethod("eval", String.class);
			
			Object result = evalMethod.invoke(eng_obj, _json);
			
			testResult = (result!=null);
			
		}
		catch(ClassNotFoundException exc) {
			throw new UnsupportedOperationException(ERROR_MESSAGE_JRE6);
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		
		return testResult;
	}

	/**
	 * Checks for the presence of a JSON path
	 * 
	 * @param _json json string to test
	 * @param _path json path
	 * @return true if json path exists in input string
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean hasJSONPath(String _json, String _path) throws Exception {
		
		boolean retval = false;
		
		if( !SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_6) ) 
			throw new UnsupportedOperationException(ERROR_MESSAGE_JRE6);
		
		if( StringUtils.isEmpty(_json) ) return false;

		if( StringUtils.isEmpty(_path) )
			throw new IllegalArgumentException("you must specify a json path for _path");

		try {
			
			// re-written using reflection to allow JRE 5 for other methods
			
			Class clazz = Class.forName("javax.script.ScriptEngineManager");
			
			Method getEngineMethod = clazz.getMethod("getEngineByName", String.class);
			
			Object mgr_obj = clazz.newInstance();
			
			Object eng_obj = getEngineMethod.invoke(mgr_obj, "JavaScript");
			
			Class engClazz = Class.forName("javax.script.ScriptEngine");
			
			Method evalMethod = engClazz.getMethod("eval", String.class);
			Method evalMethod_rdr = engClazz.getMethod("eval", Reader.class);
			Method putMethod = engClazz.getMethod("put", String.class, Object.class);
			
			InputStream is = new Object().getClass().getResourceAsStream("/js/jsonpath-0.8.0.js");
			Reader reader = new InputStreamReader(is);
			
			evalMethod_rdr.invoke(eng_obj, reader);
			evalMethod.invoke(eng_obj, "var j=" + _json + ";");
			evalMethod.invoke(eng_obj, "var jsArray = jsonPath(j, '" + _path + "')");
			
			List<String> javaArray = new ArrayList<String>();
			
			putMethod.invoke(eng_obj, "javaArray", javaArray);
			
			evalMethod.invoke(eng_obj, "for( i=0; i<jsArray.length; i++ ) { javaArray.add(jsArray[i]); }");
			
			retval = javaArray != null && javaArray.size()>0;
		}
		catch(ClassNotFoundException exc) {
			throw new UnsupportedOperationException(ERROR_MESSAGE_JRE6);
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}

		return retval;
	}
}
