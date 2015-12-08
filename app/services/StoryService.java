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

package services;

import java.util.Collection;

import models.Place;
import models.Story;


/**
 * @author Simon Stastny
 * @author Jon-Andre Brurberg
 */
public interface StoryService {
	
	/**
	 * Queries Story Service with location of the place and returns retrieved stories. Works with default radius.
	 * @param place Place to look up the stories for.
	 * @return Collection of stories.
	 */
	public Collection<Story> getStoriesForPlace(Place place);
	
	
	/**
	 * Queries Story Service with location of the place and returns retrieved stories. Works with specified radius.
	 * @param place Place to look up the stories for.
	 * @param radius Radius from which stories should be retrieved within.
	 * @return Collection of stories.
	 */
	public Collection<Story> getStoriesForPlace(Place place, Double radius);
	/**
	 * Queries Story Service for "Abstract Collections". An "Abstract Collection" is a contains the meta information
	 * of every collection at Digitalt Fortalt. The name "Abstract Collection" means that it is a story which is used
	 * to find all the related Collections, and that is has a description of the Collection without being a Story on 
	 * its own.          
	 * @return Collection of Collections (stories)
	 */
	public Collection<Story> getAbstractCollectionStories();
	
	public Collection<Story> getStoriesForCollection(String collectionID);

}
