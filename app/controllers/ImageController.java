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

package controllers;

import java.util.Collection;

import models.Image;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import retrievers.InstagramRetriever;
import services.ImageService;

/**
 * 
 * @author Simon Stastny
 *
 */
public class ImageController extends Controller {

	/**
	 * Find all photos associated with this tag in the ImageService used.
	 * @param tag Tag to find photos for.
	 * @return JSON file with images.
	 */
	public static Result listImagesForTag(String tag) {
		ImageService imageService = new InstagramRetriever(); // using instagram as the image service
		
		Collection<Image> images = imageService.getImagesForTag(tag);
		
		return ok(Json.toJson(images));
	}
}