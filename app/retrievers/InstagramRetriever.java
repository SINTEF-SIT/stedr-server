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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;

import models.Image;
import services.ImageService;
import services.StedrConstants;
import utils.HttpUtils;
import utils.JsonUtils;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Implementation of ImageService using Instagram.
 * 
 * @author Simon Stastny
 * @author Jon-Andre Brurberg
 * 
 */
public class InstagramRetriever implements ImageService {

	// base url for the instagram api
	private static final String API_URL = "https://api.instagram.com/v1/";

	// method name for retrieving photos for tags
	private static final String TAG_METHOD_NAME = "tags/%s/media/recent";

	@Override
	public Collection<Image> getImagesForTag(String tag) {
		Collection<Image> images = Lists.newArrayList();
		
		try {
			tag = URLDecoder.decode(tag,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		// build url for the api call
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(API_URL);
		urlBuilder.append(String.format(TAG_METHOD_NAME, tag));
		urlBuilder.append("?access_token=").append(StedrConstants.INSTAGRAM_ACCESS_TOKEN);
		// get response from the api
		String document = HttpUtils.getDocument(urlBuilder.toString());
		
		// parse the response into JSON document
		JsonParser jp = new JsonParser();
		JsonElement jsonDocument = jp.parse(document);
		// find data elements
		JsonArray dataArray = JsonUtils.findNestedElement(jsonDocument, "data").getAsJsonArray();

		// iterate over and find info about images
		for (JsonElement dataElement : dataArray) {
			Image image = new Image();
			image.url = JsonUtils.findNestedElementAsString(dataElement, "images/standard_resolution/url");
			image.username = JsonUtils.findNestedElementAsString(dataElement, "user/username");
			image.profilePicture = JsonUtils.findNestedElementAsString(dataElement, "user/profile_picture");
			image.fullName = JsonUtils.findNestedElementAsString(dataElement, "user/full_name");
			image.commentCount = JsonUtils.findNestedElementAsInteger(dataElement, "comments/count");
			image.link = JsonUtils.findNestedElementAsString(dataElement, "link");
			image.createdTime = JsonUtils.findNestedElementAsInteger(dataElement, "created_time");
			image.comments = JsonUtils.findComments(dataElement, "comments");
			image.likesCount = JsonUtils.findNestedElementAsInteger(dataElement, "likes/count");
			image.caption = JsonUtils.findNestedElementAsString(dataElement, "caption/text");
			image.tags = JsonUtils.findNestedStringCollection(dataElement, "tags/*");
			images.add(image);
		}

		return Collections2.filter(images, new Image.ContainsKeyword());
	}
}
