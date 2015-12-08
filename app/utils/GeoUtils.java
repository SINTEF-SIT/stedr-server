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

package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Jon-Andre Brurberg
 *
 */
public class GeoUtils {
	
	/**
	 * 
	 * @param latX Latitude of Place/Story/Thing X 
	 * @param lonX Longitude of Place/Story/Thing X
	 * @param latY Latitude of Place/Story/Thing Y
	 * @param lonY Longitude of Place/Story/Thing Y
	 * @param precision Two decimals gives an accuracy of approx. 1 km, three decimals gives an accuracy of approx. 100 meters. Min=2, Def=3, Max = 5. 
	 */
	public static boolean isNearEachOther(Double latX, Double lonX, Double latY, Double lonY, int precision){
		String[] checkList = {decimalToString(latX), decimalToString(lonX), decimalToString(latY), decimalToString(lonY)};
		String localizedPattern = "00.";
		
		if(precision<1)
			precision=1;
		else if(precision>5)
			precision=5;
		
		localizedPattern = "00."+buildDecimals(precision);
		checkList = transformDecimals(checkList, localizedPattern);
		
		if(checkList[0].equals(checkList[2]) && checkList[1].equals(checkList[3]))
			return true;
		else
			return false;
	}
		
	private static String buildDecimals(int numberOfDecimals){
		String localizedPattern = "";
		for(int i = 0; i < numberOfDecimals;i++){
			localizedPattern+="0";
		}
		return localizedPattern;
	}
	
	private static String[] transformDecimals(String[] list, String pattern){
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
		DecimalFormat df = (DecimalFormat)nf;
		df.setRoundingMode(RoundingMode.DOWN);
		df.applyLocalizedPattern(pattern);
		for(int i = 0; i < list.length;i++){
			list[i]=df.format(Double.parseDouble(list[i]));
			System.out.println(Double.parseDouble(list[i]));
		}
		return list;
	}
	
	private static String decimalToString(Double number){
		return (new BigDecimal(number)).toPlainString();
	}
	
}
