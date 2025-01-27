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

package scene;

import java.util.ArrayList;
import java.util.List;

import core.Camera;
import core.Ray;
import math.Vector3f;
import model.Model;
import model.primitive.Intersection;
import scene.partition.BruteForce;
import scene.partition.PartitionStrategy;
import scene.partition.kdtree.Kdtree;

public class Scene
{
	private List<Model> m_models;
	private Camera      m_camera;
	private PartitionStrategy m_partitioinStrategy;
	
	public Scene()
	{
		m_models = new ArrayList<>();
		m_camera = new Camera();
		
//		m_partitioinStrategy = new BruteForce();
		m_partitioinStrategy = new Kdtree();
	}
	
	public void addModel(Model model)
	{
		m_models.add(model);
		m_partitioinStrategy.addPrimitive(model.getPrimitive());
	}
	
	public boolean findClosestIntersection(Ray ray, Intersection intersection)
	{
		return m_partitioinStrategy.findClosestIntersection(ray, intersection);
	}
	
	public Camera getCamera()
	{
		return m_camera;
	}
	
	public void cookScene()
	{
		m_partitioinStrategy.processData();
	}
}
