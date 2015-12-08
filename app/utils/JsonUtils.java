/*
	The BSD 3-Clause License,
	http://opensource.org/licenses/BSD-3-Clause
	
	Copyright (c) 2013, SINTEF  
	Copyright (c) 2013, Odd Fredrik Rogstad, Christian Frøystad, Simon Stastny, Knut Nergård
	
	All rights reserved.
	
	Redistribution and use in source and binary forms, with or without modification, are 
	permitted provided that the following conditions are met:
	
	1. Redistributions of source code must retain the above copyright notice, this list of 
	conditions and the following disclaimer.
	
	2. Redistributions in binary form must reproduce the above copyright notice, this list of 
	conditions and the following disclaimer in the documentation and/or other materials provided 
	with the distribution.
	
	3. Neither the name of the copyright holder nor the names of its contributors may be used to 
	endorse or promote products derived from this software without specific prior written permission.
	
	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
	ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
	DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS BE LIABLE FOR ANY
	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package utils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import play.api.libs.json.Json;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

/**
 * Utility class implementing some convenience methods to work with JSON.
 * 
 * @author Simon Stastny
 * 
 */
public class JsonUtils {

	/**
	 * Method for convenient lookup of a nested JsonElement in a tree structure.
	 * 
	 * @param element element to search through
	 * @param path path to the element we search for (separated by '/' like in a directory structure)
	 * @return element on the specified path
	 */
	public static JsonElement findNestedElement(JsonElement element, String path) {
		// inspect the path
		List<String> wayDown = Arrays.asList(path.split("/"));

		// go down the rabbit hole
		for (String item : wayDown) {
			if (element == null || element instanceof JsonNull || element.getAsJsonObject() == null) {
				return null;
			} else {
				element = element.getAsJsonObject().get(item);
			}
		}

		return element;
	}
	
	public static String findNestedElementAsString(JsonElement element, String path) {
		JsonElement found = findNestedElement(element, path);
		
		if(found == null) {
			return null;
		} else {
			return found.getAsString();
		}
	}
	
	public static Integer findNestedElementAsInteger(JsonElement element, String path) {
		JsonElement found = findNestedElement(element, path);
		
		if(found == null) {
			return 0;
		} else {
			return found.getAsInt();
		}
	}
	public static Double findNestedElementAsDouble(JsonElement element, String path) {
		JsonElement found = findNestedElement(element, path);
		
		if(found == null) {
			return 0.0;
		} else {
			return found.getAsDouble();
		}
	}

	public static Collection<String> findNestedStringCollection(JsonElement element, String path) {
		// inspect the path
		List<String> wayDown = Arrays.asList(path.split("/"));

		Collection<String> found = Lists.newArrayList();

		// go down the rabbit hole
		for (String item : wayDown) {
			if (item.contains("*")) {
				JsonArray array = element.getAsJsonArray();

				for (int i = 0; i < array.size(); i++) {
					found.add(array.get(i).getAsString());
				}
			} else {
				element = element.getAsJsonObject().get(item);
			}
		}

		return found;
	}
	
	public static Collection<String> findComments(JsonElement element, String path) {
		// inspect the path
		//List<String> wayDown = Arrays.asList(path.split("/"));
		JsonElement comments=findNestedElement(element, path);
		JsonArray dataArray = findNestedElement(comments, "data").getAsJsonArray();
		Collection<String> found = Lists.newArrayList();

		// go down the rabbit hole
		for (JsonElement dataElement : dataArray) {
			JsonObject commentObj = findNestedElement(dataElement, "from").getAsJsonObject();

			String date=new Date(findNestedElementAsInteger(dataElement, "created_time")).toString();
			commentObj.add("text", findNestedElement(dataElement, "text"));
			commentObj.addProperty("date", date);
			
			System.out.println(commentObj.toString());
			
					//found.add(findNestedElementAsString(dataElement, "text").getAsString() + "/%/" + );
			//found.add(commentObj.getAsString());
			found.add("text:"+JsonUtils.findNestedElementAsString(dataElement, "text")
					+ "/**/time:" + JsonUtils.findNestedElementAsString(dataElement, "created_time")
					+ "/**/user:" + JsonUtils.findNestedElementAsString(dataElement, "from/username")
					+ "/**/name:" + JsonUtils.findNestedElementAsString(dataElement, "from/full_name:")
					+ "/**/pict:" + JsonUtils.findNestedElementAsString(dataElement, "from/profile_picture")
			);
		}

		return found;
	}

}
