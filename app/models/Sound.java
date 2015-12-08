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

package models;

/**
 * 
 * @author Tor Barstad
 *
 */

public class Sound {
	
	public String url;
	public String title;
	public String artworkURL;
	public String avatarURL;
	public String waveformURL;
	public String streamURL;
	public String description;
	public String favoriteCount;
	public String userName;
	
	public Sound(){
		url="";
		title="";
		artworkURL="";
		avatarURL="";
		waveformURL="";
		streamURL="";
		description="";
		favoriteCount="";
		userName="";
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setArtworkURL(String artworkURL){
		this.artworkURL = artworkURL;
	}
	
	public void setAvatarURL(String avatarURL){
		this.avatarURL = avatarURL;
	}
	
	public void setStreamURL(String streamURL){
		this.streamURL = streamURL;
	}
	
	public void setWaveformURL(String waveformURL){
		this.waveformURL = waveformURL;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public void setFavoriteCount(String favoriteCount){
		this.favoriteCount = favoriteCount;
	}
	
	public void setUsername(String userName){
		this.userName = userName;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getUrl(){
		return url;
	}
	
	public String getArtworkURL(){
		return artworkURL;
	}
	
	public String getAvatarURL(){
		return avatarURL;
	}
	
	public String getStreamURL(){
		return streamURL;
	}
	
	public String getWaveformURL(){
		return waveformURL;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getFavoriteCount(){
		return favoriteCount;
	}
	
	public String getUsername(){
		return userName;
	}
	
}