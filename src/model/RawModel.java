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

package model;

import math.Transform;
import math.material.Material;
import model.primitive.Primitive;

public class RawModel implements Model
{
	private Primitive m_primitive;
	private Material  m_material;
	private Transform m_transform;
	
	public RawModel(Primitive primitive, Material material)
	{
		m_transform = new Transform();
		
		m_primitive = primitive;
		m_primitive.setModel(this);
		
		m_material = material;
	}
	
	@Override
	public Primitive getPrimitive()
	{
		return m_primitive;
	}
	
	@Override
	public Material getMaterial()
	{
		return m_material;
	}

	@Override
	public Transform getTransform()
	{
		return m_transform;
	}
}
