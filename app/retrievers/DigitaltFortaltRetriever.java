/*
	The BSD 3-Clause License,
	http://opensource.org/licenses/BSD-3-Clause
	
	Copyright (c) 2013, SINTEF  
	Copyright (c) 2013, Odd Fredrik Rogstad, Christian Frøystad, Simon Stastny, Knut Nergård
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import models.Place;
import models.Story;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import services.StedrConstants;
import services.StoryService;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * Implementation of StoryService using DigitaltFortalt.
 * 
 * @author Simon Stastny
 * @author Jon-Andre Brurberg
 * 
 */
public class DigitaltFortaltRetriever implements StoryService {

	private static final double DEFAULT_RADIUS = 0.06; // 60 meters 
	
	
	/*New retriever. A problem with the new retriever is that the API provider
	 * doesn't provide content when you find a story. Because of this you have 
	 * to first find IDs and then pull content based on those IDs. It's not
	 * possible as far as I know to ask for multiple IDs because the provider
	 * uses a hash between Content-Provider (H-DF) and ID.
	 * 
	 * Jsoup is used for parsing XML: http://jsoup.org/
	 */
	@Override
	public Collection<Story> getStoriesForPlace(Place place, Double radius) {
			
		if (place == null ||
			place.longitude == null || 
			place.latitude == null || 
			radius < 0) {
			return null;
		}
		ArrayList<String> storyIDs = new ArrayList<String>();
		Elements storyItems = new Elements();
		
		storyIDs=getIDsFromDIMU(place);
		
		if(storyIDs.size()>=1){
			storyItems=getStoriesFromDIMU(storyIDs);
		}
		return Collections2.transform(storyItems, jsonElementToStoryMapping);
	}
	@Override
	public Collection<Story> getAbstractCollectionStories(){
		ArrayList<String> storyIDs = new ArrayList<String>();
		Elements storyItems = new Elements();
		
		storyIDs = getIDsFromDIMU("stedr_collection");
		
		if(storyIDs.size()>=1){
			storyItems=getStoriesFromDIMU(storyIDs);
		}
		return Collections2.transform(storyItems, jsonElementToStoryMapping);
	}

	@Override
	public Collection<Story> getStoriesForCollection(String collectionID){
		return Collections2.transform(getStoriesFromDIMU(getIDsFromDIMU(collectionID)), jsonElementToStoryMapping);
	}
	
	@Override
	public Collection<Story> getStoriesForPlace(Place place) {
		return getStoriesForPlace(place, DEFAULT_RADIUS);
	}
	
	private ArrayList<String> getIDsFromDIMU(Place place){
		Document response;
		ArrayList<String> ids = new ArrayList<String>();
		StringBuffer requestURL = new StringBuffer();		
		requestURL.append("http://api.digitaltmuseum.no/solr/select?q=*:*&");
		requestURL.append("fq=identifier.owner:H-DF&");
		requestURL.append("fq={!bbox%20pt="+place.latitude+","+place.longitude+"%20sfield=artifact.coordinate%20d=0.1}&");
		requestURL.append("api.key=");
		requestURL.append(StedrConstants.DIMU_API_KEY);
		try {
			response = Jsoup.connect(requestURL.toString()).get();
			Elements storyIDs = response.getElementsByAttributeValueContaining("name", "identifier.id");
			for (Element element : storyIDs) {
				ids.add(element.text());
			}
			return ids;
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private ArrayList<String> getIDsFromDIMU(String searchTag){
		Document response;
		ArrayList<String> ids = new ArrayList<String>();
		StringBuffer requestURL = new StringBuffer();		
		requestURL.append("http://api.digitaltmuseum.no/solr/select?q=");
		requestURL.append(searchTag+"&");
		requestURL.append("fq=identifier.owner:H-DF&");
		requestURL.append("api.key=");
		requestURL.append(StedrConstants.DIMU_API_KEY);
		try {
			response = Jsoup.connect(requestURL.toString()).get();
			Elements storyIDs = response.getElementsByAttributeValueContaining("name", "identifier.id");
			for (Element element : storyIDs) {
				ids.add(element.text());
			}
			return ids;
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static Elements getStoriesFromDIMU(ArrayList<String> storyIDs){
		Document response;
		Elements stories = null;
		StringBuffer requestURL = new StringBuffer();
		
		try{
			if(storyIDs.size()>=1){
				requestURL.append("http://api.digitaltmuseum.no/artifact?owner=H-DF&identifier=");
				requestURL.append(storyIDs.get(0));
				requestURL.append("&mapping=ABM&api.key=demo");
				response = Jsoup.connect(requestURL.toString()).get();
				stories = response.getElementsByTag("abm:record");
				Collections2.transform(stories, jsonElementToStoryMapping);
				if(storyIDs.size()>1){
					for(int i = 1; i<storyIDs.size();i++){
						requestURL.setLength(0);
						requestURL.append("http://api.digitaltmuseum.no/artifact?owner=H-DF&identifier=");
						requestURL.append(storyIDs.get(i));
						requestURL.append("&mapping=ABM&api.key=demo");
						response = Jsoup.connect(requestURL.toString()).get();
						stories.addAll(response.getElementsByTag("abm:record"));
						//	Not sure if necessary, meant to prevent flooding of the external server  
						Thread.sleep(250);
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		return stories;
	}	
	
	//Statsny magic
	private static Function<Element, Story> jsonElementToStoryMapping = new Function<Element, Story>() {
		@Override
		public Story apply(Element item) {
			Story story = new Story();
			story.ingress = "";
			List<String> pictures = new ArrayList<String>();
			List<String> videos = new ArrayList<String>();
			List<String> contributors = new ArrayList<String>();
			
			Element fields = item.select("abm|record").first();
			for (Element field : fields.getAllElements()) {
				// story iself
				
				// save the identifier (needed to complete link)
				if(field.tagName().equals("dc:identifier")){
					story.link=field.ownText();
				}
				
				if(field.tagName().equals("europeana:rights")){
					story.license=field.ownText();
				}

				// save title and completes link
				if (field.tagName().equals("dc:title")) {
					story.title = field.ownText();
					story.link = "http://digitaltfortalt.no/things/"+story.title.replaceAll(" ","-").replaceAll("\\.", "")+"/"+story.link;					
				}

				// save ingress
				if (field.tagName().equals("abm:introduction")) {
					story.ingress = story.ingress + field.ownText() + " ";
				}

				// save story. Needs the last condition because the API doesn't differentiate between textual content and image content   
				if (field.tagName().equals("dc:description") && field.attributes().hasKey("xml:lang")) {
					String raw = field.ownText();
					//tries to keep URLs in the text. Maybe there is a better way to do this, but I am not familiar with RegExp
					String clean = raw.replaceAll("<a href=\"", "");
					clean = clean.replaceAll("\".*?\">", " ");
					// this strips down HTML but also new lines: String clean = Jsoup.parse(raw).text();
					clean = clean.replace("</p>", "\n</p>").replaceAll("\\<[^>]*>","");
					clean = clean.replaceAll("&nbsp", "");
					story.fortelling = clean;
				}

				// pictures, sound and videos

				// save pictures
				if (field.tagName().equalsIgnoreCase("abm:imageUri")) {
					pictures.add(field.ownText());
				}

				// save videos
				if (field.tagName().equalsIgnoreCase("abm:videoUri")) {
					videos.add(field.ownText());
				}
				
				// save sounds to the video list to access the video/sound-player
				if (field.tagName().equalsIgnoreCase("abm:soundUri")) {
					videos.add(field.ownText());
				}

				// save author (i.e: the person that wrote the article at Digitalt Fortalt
				if (field.tagName().equalsIgnoreCase("dc:creator") && 
					story.author == null) {	
					story.author = field.ownText();
				}

				// adding the rest of the creators (i.e: the people that have provided the pictures)
				if (field.tagName().equalsIgnoreCase("dc:creator") && 
					!field.ownText().equals(story.author) && !field.ownText().equals("")) {
					contributors.add(field.ownText());
				}
				
				// save license
				if (field.tagName().equals("europeana:license")){
						story.license=field.ownText();
				}

				// save language
				if (field.tagName().equalsIgnoreCase("dc:language")) {
					story.language = field.ownText();
				}

				// save category
				if (field.tagName().equals("abm:category")) {
					story.category = field.ownText();
				}
				
				if (field.tagName().equals("abm:lat")) {
					story.latitude = Double.valueOf(field.ownText());
				}
				if (field.tagName().equals("abm:long")) {
					story.longitude = Double.valueOf(field.ownText());
				}
			}
			
			// found any contributors in additon 
			if (contributors.size()>1) {
				story.contributors = contributors;
			}
			else{
				contributors.add("Unknown");
				story.contributors = contributors;
			}

			// found any pictures?
			if (!pictures.isEmpty()) {
				story.pictures = pictures;
			}
			else{
				pictures.add("http://oi57.tinypic.com/2nv5u03.jpg");
				story.pictures = pictures;
			}

			// found any videos?
			if (!videos.isEmpty()) {
				story.videos = videos;
			}	
			else{
				videos.add("");
				story.videos = videos;
			}
			return story;
		}
	};
}