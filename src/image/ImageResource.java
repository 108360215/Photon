//	The MIT License (MIT)
//	
//	Copyright (c) 2016 Tzu-Chieh Chang (as known as D01phiN)
//	
//	Permission is hereby granted, free of charge, to any person obtaining a copy
//	of this software and associated documentation files (the "Software"), to deal
//	in the Software without restriction, including without limitation the rights
//	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//	copies of the Software, and to permit persons to whom the Software is
//	furnished to do so, subject to the following conditions:
//	
//	The above copyright notice and this permission notice shall be included in all
//	copies or substantial portions of the Software.
//	
//	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//	SOFTWARE.

package image;

import math.Vector4f;
import math.Vector2f;
import math.Vector3f;

public abstract class ImageResource
{
	private int   m_numComponents;
	private int[] m_dimensions;
	
	protected ImageResource(int numComponents, int... dimensions)
	{
		m_numComponents = numComponents;
		m_dimensions    = new int[dimensions.length];
		
		for(int i = 0; i < dimensions.length; i++)
		{
			m_dimensions[i] = dimensions[i];
		}
	}
	
	public int[] getDimensions()
	{
		return m_dimensions;
	}
	
	public int getNumComponents()
	{
		return m_numComponents;
	}
	
	public abstract void getPixel(int d1, int d2, Vector3f result);
	public abstract void setPixel(int d1, int d2, Vector3f pixel);
}
