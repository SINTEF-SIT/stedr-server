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

import java.util.Map;
import java.util.Map.Entry;
import com.google.common.collect.Maps;

import services.StedrConstants;

/**
 * @author Simon Stastny
 * @author Jon-Andre Brurberg
 *
 */

public abstract class FlickrQuery {
	
	protected abstract String getMethodName();
	
	protected static final String API_KEY = StedrConstants.FLICKR_API_KEY;
	protected static final String REST_API_URL = "https://api.flickr.com/services/rest/";
	
	private Map<String, Object> params = Maps.newConcurrentMap();
	
	protected void addParameter(String key, Object value) {
		params.put(key, value);
	}
	
	protected String makeRequestUrl() {
		StringBuffer sb = new StringBuffer();

		sb.append(REST_API_URL);
		sb.append("?method=").append(getMethodName());
		sb.append("&api_key=").append(API_KEY);
		sb.append("&extras=description");
		sb.append(",date_upload");
		sb.append(",owner_name");
		sb.append("&format=rest");
		
		setAdditionalParams();
		
		for (Entry<String, Object> param : params.entrySet()) {
			sb.append("&").append(param.getKey()).append("=").append(param.getValue());
		}
		return sb.toString();
	}
	
	protected String makeRequestUrl(Double lat, Double lon) {
		StringBuffer sb = new StringBuffer();

		sb.append(REST_API_URL);
		sb.append("?method=").append(getMethodName());
		sb.append("&api_key=").append(API_KEY);
		sb.append("&lat=").append(lat);
		sb.append("&lon=").append(lon);
		sb.append("&radius=").append(0.01);
		sb.append("&extras=description");
		sb.append(",date_upload");
		sb.append(",owner_name");
		sb.append(",machine_tags");
		sb.append("&format=rest");
		
		setAdditionalParams();
		
		for (Entry<String, Object> param : params.entrySet()) {
			sb.append("&").append(param.getKey()).append("=").append(param.getValue());
		}
		return sb.toString();
	}
	
	protected abstract void setAdditionalParams();
}
