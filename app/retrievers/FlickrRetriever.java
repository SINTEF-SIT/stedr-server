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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import models.Place;
import models.Story;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import retrievers.flickr.AdditionalDataQuery;
import retrievers.flickr.PhotoQueryForGroup;
import services.PlaceService;
import services.StedrConstants;
import services.StoryService;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * Implementation of PlaceService using Flickr.
 * 
 * @author Simon Stastny
 * @author Jon-Andre Brurberg
 * 
 */
public class FlickrRetriever implements PlaceService {
	
	private static Cache<String, Place> placeCache = CacheBuilder.newBuilder()
			.expireAfterWrite(1, TimeUnit.HOURS)
			.maximumSize(2000) // number of items
			.build();
	
	@Override
	public Collection<Place> getAllPlaces() {
		try {
			Collection<Place> rawPlaces = new PhotoQueryForGroup(StedrConstants.STEDR_GROUP_ID).getPlaces();
						
			Collection<Place> places = Lists.newArrayList();
			
			// load  additional data (licenses, location)
			for(Place place : rawPlaces) {
				places.add(placeCache.get(place.id, new PlaceLoader(place)));
			}

			// filter out those what do not have compatible license
			places = Collections2.filter(places, new Place.HasCompatibleLicense());
			
			// kick out places without a location
			places = Collections2.filter(places, new Place.HasLocation());
			
			return places;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return null;
	}

	
	//This should probably be declared deprecated and be reimplementated with Flickrs geolocation-search.   
	@Override
	public Collection<Place> getPlacesInArea(Double latBL, Double lngBL, Double latTR, Double lngTR) {
		return Collections2.filter(getAllPlaces(),new Place.IsInArea(latBL, lngBL, latTR, lngTR));
	}
	
	@Override
	public Place getPlaceInArea(Double lat, Double lon) {
		try {
			Collection<Place> rawPlaces = new PhotoQueryForGroup(StedrConstants.STEDR_GROUP_ID).getPlacesInArea(lat, lon);	

			Collection<Place> places = Lists.newArrayList();
			
			// load  additional data (licenses, location)
			for(Place place : rawPlaces) {
				places.add(placeCache.get(place.id, new PlaceLoader(place)));
			}

			// filter out those what do not have compatible license
			places = Collections2.filter(places, new Place.HasCompatibleLicense());			
			// kick out places without a location
			places = Collections2.filter(places, new Place.HasLocation());
			for (Place place : places) {
				return place;
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} 

		return null;
	}
	
	@Override
	public Collection<Place> getPlacesInCollection(String collectionTag){
		StoryService sS = new DigitaltFortaltRetriever();
		Collection<Story> stories = sS.getStoriesForCollection(collectionTag);
		Collection<Place> places = new ArrayList<Place>();
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
		DecimalFormat df = (DecimalFormat)nf;
		df.applyLocalizedPattern("00.000000");
		for (Story story : stories) {
			places.add(getPlaceInArea(Double.valueOf(df.format(story.latitude)),Double.valueOf(df.format(story.longitude))));
		}
		return places;
	}
	
	private static String loadPictureUrl(Place place, String picSize) {
		StringBuffer sb = new StringBuffer();
		sb.append("http://www.flickr.com/photos/");
		sb.append(place.owner);
		sb.append("/");
		sb.append(place.id);
		sb.append("/sizes/");
		sb.append(picSize);
		sb.append("/in/photostream/");

		Document doc;
		try {
			doc = Jsoup.connect(sb.toString()).get();

			Element image = doc.select("div#allsizes-photo img").get(0);
			return image.attr("src");
				
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	static class PlaceLoader implements Callable<Place>{
		
		private final Place place;

		public PlaceLoader(Place place) {
			super();
			this.place = place;
		}

		@Override
		public Place call() throws Exception {
			// load license and location
			new AdditionalDataQuery(place).load();
			
			// load pics only if this place is actually valid (this is a costly operation)
			if (place.hasCompatibleLicense() && place.hasLocation()) {
				place.pictureUrl = loadPictureUrl(place, "m");
				place.thumbnailUrl = loadPictureUrl(place, "t");
			}

			return place;
		}
		
	}
}
