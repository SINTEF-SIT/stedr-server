/*
	The BSD 3-Clause License,
	http://opensource.org/licenses/BSD-3-Clause
	
	Copyright (c) 2013, SINTEF  
	Copyright (c) 2014, Tor Barstad, Jon-Andre Brurberg, Øyvind Hellenes, Hallvard Jore Christensen,
	Vegard Storm, Jørgen Rugelsjøen Wikdahl
	
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

package retrievers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;

import models.Sound;
import services.SoundService;
import services.StedrConstants;
import utils.HttpUtils;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
/**
 * 
 * @author Jon-Andre Brurberg
 * @author Tor Barstad
 * @author Vegard Storm
 * @author Shanshan Jiang
 * 
 * Change to retrieve Soundcloud resources as JSON instead of XML
 * changes to handle the special characters like line break
 *
 */

public class SoundcloudRetriever implements SoundService{
	
	public Collection<Sound> getSoundsForTag(String tag){    
		String tagDecoded = "";
	    String tagEncoded = "";
		try {
			tagDecoded =  URLDecoder.decode(tag,"UTF-8"); 
			System.out.println("TagDecoded: " + tagDecoded);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		Collection<Sound> sounds = Lists.newArrayList();
		StringBuilder requestURL = new StringBuilder();
		requestURL.append("https://api.soundcloud.com/");
		requestURL.append("tracks?");
		requestURL.append("client_id=");
		requestURL.append(StedrConstants.SOUNDCLOUD_CLIENT_ID);
		requestURL.append("&q=");
		tagDecoded=tagDecoded.replaceAll(" ","+");
		tagDecoded=tagDecoded.toLowerCase();
		//tag = tag.toLowerCase();
		System.out.println("Tag: " + tag);
		try {
			tagEncoded=URLEncoder.encode(tagDecoded,"UTF-8");			
			//System.out.println("TagEecoded: " + tagEncoded);
		} catch (UnsupportedEncodingException e1) {
			tagEncoded="stedr";
		}
		tagEncoded=tagEncoded.replaceAll("%2B" ,"+");
		requestURL.append(tagEncoded);
		requestURL.append("+stedr");
		// get response from the API
		System.out.println("The request URL: " + requestURL);
		String document = HttpUtils.getDocument(requestURL.toString());
		//System.out.println("The response is: " + document);

		// parse response into JSON document
		JsonParser jp = new JsonParser();
		JsonElement jsonDocument = jp.parse(document);
		
		// find the track elements
		JsonArray trackArray = jsonDocument.getAsJsonArray();
		// iterate over and find info about sound tracks		
		Iterator<JsonElement> trackIter=trackArray.iterator();
	    while (trackIter.hasNext()) {
	      JsonObject track=trackIter.next().getAsJsonObject();		      
	      Sound s = new Sound();

	      String title = getStringWithoutQuotes(track.get("title").toString());
	      if (title!=null) title = convertString(title);
          System.out.println("The title: " + title);
		  String tags = track.get("tag_list").toString();
	      System.out.println("The tags: " + tags);
	      
  		  System.out.println("The track: " + track);
  		  if((title.contains("stedr")) 
					|| (tags.contains("stedr") )){

  		    	  s.setTitle(title);
		    	  
		    	  if (track.get("permalink_url")!=null)
		      		s.setUrl(getStringWithoutQuotes(track.get("permalink_url").toString()));	    	  
		    	  
		    	  if (track.get("artwork_url")!=null)
		      		s.setArtworkURL(getStringWithoutQuotes(track.get("artwork_url").toString()));
		    	  	
		    		s.setDescription(convertString(getStringWithoutQuotes(track.get("description").toString())));			
		    		s.setWaveformURL(getStringWithoutQuotes(track.get("waveform_url").toString()));
		    		
		    		s.setFavoriteCount(track.get("favoritings_count").toString());
		    		s.setStreamURL(getStringWithoutQuotes(track.get("stream_url").toString())+"?client_id="+StedrConstants.SOUNDCLOUD_CLIENT_ID);   			
		    
		    		JsonObject user=track.getAsJsonObject("user");
		    		s.setAvatarURL(getStringWithoutQuotes(user.get("avatar_url").toString()));
		    		s.setUsername(getStringWithoutQuotes(user.get("username").toString()));
		    		
		    		if(s.getTitle().length()>1 && s.getUrl().length() > 22){
		    			sounds.add(s);
		    		}
  		  }
	    }
		return sounds;
	}
	
	// return a string without the quotes at the beginning and end
	private String getStringWithoutQuotes(String str)  {
		if (str==null) return null;
		if (str.startsWith("\"")) {
			String result = str.substring(1, str.length()-1);
			return result;
		}
		else return str;
	}
	
	// converting the special characters in the string
	private String convertString(String str) {
		String result = str;
		// line break and quotation mark
		result=result.replace("\\r\\n", System.getProperty("line.separator"));
		result=result.replace("\\n", System.getProperty("line.separator"));
		result = result.replace("\\", "");
		System.out.println("The result1: " + result);
		return result;
	}
}	        