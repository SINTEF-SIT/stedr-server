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

package retrievers.flickr;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import models.Place;

/**
 * @author Simon Stastny
 * @author Jon-Andre Brurberg
 *
 */

public class PhotoQueryForGroup extends FlickrQuery {
	
	/*
	 * IMPROVE
	 * 500 is maximum for one page, if we need more, we need to use paging.
	 * See http://www.flickr.com/services/api/explore/flickr.groups.pools.getPhotos for details
	 */
	private static final int PAGE_SIZE = 500;
	
	private final String groupId;
	
	public PhotoQueryForGroup(String groupId) {
		super();
		this.groupId = groupId;
	}
	
	public Collection<Place> getPlaces() throws IOException {
		Document doc = Jsoup.connect(makeRequestUrl()).get();
		Elements photoElements = doc.select("photo");
		
		// returns elements mapped onto places
		return Collections2.transform(photoElements, photoElementToPlaceMapping);
	}
	
	public Collection<Place> getPlacesInArea(Double lat, Double lon) throws IOException {
		Document doc = Jsoup.connect(makeRequestUrl(lat,lon)).get();
		Elements photoElements = doc.select("photo");
		
		// returns elements mapped onto places
		return Collections2.transform(photoElements, photoElementToPlaceMapping);
	}
	
	@Override
	protected String getMethodName() {
		return "flickr.photos.search";
	}

	@Override
	protected void setAdditionalParams() {
		addParameter("group_id", groupId);
		addParameter("per_page", PAGE_SIZE);
	}
	
	private static Function<Element, Place> photoElementToPlaceMapping = new Function<Element, Place>() {
		@Override
		public Place apply(Element element) {
			Long dateAddedUnixTimestamp = Long.valueOf(element.attr("dateupload"));

			Place place = new Place();
			place.id = element.attr("id");
			place.title = element.attr("title");
			place.description=element.getElementsByTag("description").text();
			place.owner = element.attr("owner");
			place.ownerName = element.attr("ownername");
			place.dateAdded = new Date(TimeUnit.SECONDS.toMillis(dateAddedUnixTimestamp));
			System.out.println("title: " + place.title);
			System.out.println("tag: " + place.tag);
			
			return place;
		}
	};
}
