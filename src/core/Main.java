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

package core;

import java.math.BigDecimal;
import java.math.RoundingMode;

import math.Rand;
import math.Vector2f;
import math.Vector3f;
import math.material.AbradedOpaque;
import model.RawModel;
import model.boundingVolume.AABB;
import model.primitive.Sphere;
import model.primitive.Triangle;
import ui.Window;
import util.Debug;
import util.Time;

public class Main
{
	public static void main(String[] args)
	{
//		Triangle triangle = new Triangle(new Vector3f(12, 9, 9), new Vector3f(9, 12, 9), new Vector3f(19, 19, 20));
//		AABB aabb = new AABB(new Vector3f(-10, -10, -10), new Vector3f(10, 10, 10));
//		
//		System.out.println(triangle.isIntersect(aabb));
//		System.out.println(aabb.isIntersect(aabb));
		
//		Vector2f hitDist = new Vector2f();
//		AABB aabb = new AABB(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1));
//		Ray ray = new Ray(new Vector3f(0, 0, 0), new Vector3f(-1, -1, -1).normalizeLocal());
//		
//		System.out.println(aabb.isIntersect(ray, hitDist));
//		System.out.println(hitDist);
		
//		AABB aabb = new AABB(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1));
//		Sphere sphere = new Sphere(new Vector3f(1.999f, 0, 0), 1);
//		
//		System.out.println(sphere.isIntersect(aabb));
		
		new Test();
		
		Engine engine = new Engine();
		
		engine.run();
	}
}
