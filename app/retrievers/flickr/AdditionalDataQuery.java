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
import models.Place;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author Simon Stastny
 * @author Jon-Andre Brurberg
 *
 */

public class AdditionalDataQuery extends FlickrQuery {

	private Place place;

	public AdditionalDataQuery(Place place) {
		super();
		this.place = place;
	}

	@Override
	protected String getMethodName() {
		return "flickr.photos.getInfo";
	}

	@Override
	protected void setAdditionalParams() {
		addParameter("photo_id", place.id);
	}

	public void load() {
		Document doc;
		try {
			doc = Jsoup.connect(makeRequestUrl()).get();

			Elements photos = doc.select("photo");

			if (!photos.isEmpty()) {
				Element photo = photos.get(0);
				place.license = Integer.valueOf(photo.attr("license"));
			}

			Elements locations = doc.select("location");

			if (!locations.isEmpty()) {
				Element location = locations.get(0);

				place.latitude = Double.valueOf(location.attr("latitude"));
				place.longitude = Double.valueOf(location.attr("longitude"));
			}
			
			Elements machineTags = doc.select("tags");
			
			if (!machineTags.isEmpty()) {
				Element machineTag = machineTags.get(0);
				for (Element element : machineTag.children()) {
					if(element.attr("raw").contains("foursquare:venue="))
						place.foursquareHash = element.attr("raw").substring(element.attr("raw").indexOf("=")+1);
					
					if(element.attr("raw").contains("foursquare:place="))
						place.foursquareName = element.attr("raw").substring(element.attr("raw").indexOf("=")+1);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
