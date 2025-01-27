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

package model.primitive;

import java.util.List;

import core.Ray;
import math.Matrix4f;
import math.Vector2f;
import math.Vector3f;
import model.boundingVolume.AABB;
import util.Debug;

public class Triangle extends AtomicPrimitive
{
	private static final float EPSILON = 0.0001f;
	
	// vertex position
	protected Vector3f m_vA;
	protected Vector3f m_vB;
	protected Vector3f m_vC;
	
	// vertex normal
	protected Vector3f m_nA;
	protected Vector3f m_nB;
	protected Vector3f m_nC;
	
	// edge vector
	protected Vector3f m_eAB;
	protected Vector3f m_eAC;
	
	// surface normal
	protected Vector3f m_normal;
	
	// texture coordinates
	protected Vector2f m_txA;
	protected Vector2f m_txB;
	protected Vector2f m_txC;
	
	protected boolean m_hasTexCoord;
	
	// front facing: CCW vertex order
	public Triangle(Vector3f vA, Vector3f vB, Vector3f vC)
	{
		super();
		
		m_eAB = vB.sub(vA);
		m_eAC = vC.sub(vA);
		
		m_normal = m_eAB.cross(m_eAC).normalizeLocal();
		
		m_vA = new Vector3f(vA);
		m_vB = new Vector3f(vB);
		m_vC = new Vector3f(vC);
		
		m_nA = new Vector3f(m_normal);
		m_nB = new Vector3f(m_normal);
		m_nC = new Vector3f(m_normal);
		
		m_txA = new Vector2f();
		m_txB = new Vector2f();
		m_txC = new Vector2f();
		
		m_hasTexCoord = false;
	}
	
	@Override
	public boolean isIntersect(Ray ray, Intersection intersection)
	{
		// test intersection in model space
		Vector3f localRayOrigin = getModel().getTransform().getInverseModelMatrix().mul(ray.getOrigin(), 1.0f);
		Vector3f localRayDir    = getModel().getTransform().getInverseModelMatrix().mul(ray.getDir(), 0.0f).normalizeLocal();
		
		if(rayTriangleIntersection(localRayOrigin, localRayDir, intersection, m_vA, m_vB, m_vC, m_normal, m_eAB, m_eAC))
		{
			// transform back to world space
			intersection.setHitAtomicPrimitive(this);
			intersection.setHitPoint(getModel().getTransform().getModelMatrix().mul(intersection.getHitPoint(), 1.0f));
			intersection.setHitNormal(getModel().getTransform().getModelMatrix().mul(intersection.getHitNormal(), 0.0f).normalizeLocal());
			
			return true;
		}
		
		return false;
	}
	
	// TODO: make local & global intersect method
	
	// Reference: Ingo Wald's PhD paper "Real Time Ray Tracing and Interactive Global Illumination", P.89.
	// This implementation is twice as fast as Moeller-Trumbore's method (stated by others, haven't profiled
	// that myself).
	// FIXME: implement the 2x faster algorithm!
	public static boolean rayTriangleIntersection(Vector3f rayOrigin, Vector3f rayDir, Intersection intersection, Vector3f vA, Vector3f vB, Vector3f vC, Vector3f faceNormal, Vector3f eAB, Vector3f eAC)
	{
		float dist = rayOrigin.sub(vA).dot(faceNormal) / (-rayDir.dot(faceNormal));
		
		// reject by distance
		if(dist < EPSILON || dist > Float.MAX_VALUE || dist != dist) 
			return false;
		
		// projected hit point
		float hitPu, hitPv;
		
		// projected side vector AB and AC
		float abPu, abPv, acPu, acPv;
					
		// find dominant axis
		if(Math.abs(faceNormal.x) > Math.abs(faceNormal.y))
		{
			// X dominant, projection plane is YZ
			if(Math.abs(faceNormal.x) > Math.abs(faceNormal.z))
			{
				hitPu = dist * rayDir.y + rayOrigin.y - vA.y;
				hitPv = dist * rayDir.z + rayOrigin.z - vA.z;
				abPu  = eAB.y;
				abPv  = eAB.z;
				acPu  = eAC.y;
				acPv  = eAC.z;
			}
			// Z dominant, projection plane is XY
			else
			{
				hitPu = dist * rayDir.x + rayOrigin.x - vA.x;
				hitPv = dist * rayDir.y + rayOrigin.y - vA.y;
				abPu  = eAB.x;
				abPv  = eAB.y;
				acPu  = eAC.x;
				acPv  = eAC.y;
			}
		}
		// Y dominant, projection plane is ZX
		else if(Math.abs(faceNormal.y) > Math.abs(faceNormal.z))
		{
			hitPu = dist * rayDir.z + rayOrigin.z - vA.z;
			hitPv = dist * rayDir.x + rayOrigin.x - vA.x;
			abPu  = eAB.z;
			abPv  = eAB.x;
			acPu  = eAC.z;
			acPv  = eAC.x;
		}
		// Z dominant, projection plane is XY
		else
		{
			hitPu = dist * rayDir.x + rayOrigin.x - vA.x;
			hitPv = dist * rayDir.y + rayOrigin.y - vA.y;
			abPu  = eAB.x;
			abPv  = eAB.y;
			acPu  = eAC.x;
			acPv  = eAC.y;
		}
		
		// TODO: check if these operations are possible of producing NaNs
		
		// barycentric coordinate of vertex B in the projected plane
		float baryB = (hitPu*acPv - hitPv*acPu) / (abPu*acPv - abPv*acPu);
		if(baryB < 0.0f) return false;
		
		// barycentric coordinate of vertex C in the projected plane
		float baryC = (hitPu*abPv - hitPv*abPu) / (acPu*abPv - abPu*acPv);
		if(baryC < 0.0f) return false;
		
		if(baryB + baryC > 1.0f) return false;
		
		// so the ray intersects the triangle (TODO: reuse calculated results!)
		
		intersection.setHitPoint(rayDir.mul(dist).addLocal(rayOrigin));
		intersection.setHitNormal(faceNormal);
		
		return true;
	}

	// Reference: Tomas Akenine-Moeller's "Fast 3D Triangle-Box Overlap Testing", which
	// is based on SAT but faster.
	@Override
	public boolean isIntersect(AABB aabb)
	{
		// TODO: transform aabb to local space may be faster
		
		Vector3f tvA = new Vector3f();
		Vector3f tvB = new Vector3f();
		Vector3f tvC = new Vector3f();
		
		getModel().getTransform().getModelMatrix().mul(m_vA, 1.0f, tvA);
		getModel().getTransform().getModelMatrix().mul(m_vB, 1.0f, tvB);
		getModel().getTransform().getModelMatrix().mul(m_vC, 1.0f, tvC);
		
		// move the origin to the AABB's center
		tvA.subLocal(aabb.getCenter());
		tvB.subLocal(aabb.getCenter());
		tvC.subLocal(aabb.getCenter());
		
		Vector3f aabbHalfExtents  = aabb.getMaxVertex().sub(aabb.getCenter());
		Vector3f projection       = new Vector3f();
		Vector3f sortedProjection = new Vector3f();// (min, mid, max)
		
		// test AABB face normals (x-, y- and z-axes)
		projection.set(tvA.x, tvB.x, tvC.x);
		projection.sort(sortedProjection);
		if(sortedProjection.z < -aabbHalfExtents.x || sortedProjection.x > aabbHalfExtents.x)
			return false;
		
		projection.set(tvA.y, tvB.y, tvC.y);
		projection.sort(sortedProjection);
		if(sortedProjection.z < -aabbHalfExtents.y || sortedProjection.x > aabbHalfExtents.y)
			return false;
		
		projection.set(tvA.z, tvB.z, tvC.z);
		projection.sort(sortedProjection);
		if(sortedProjection.z < -aabbHalfExtents.z || sortedProjection.x > aabbHalfExtents.z)
			return false;
		
		Vector3f tNormal = new Vector3f();
		getModel().getTransform().getModelMatrix().mul(m_normal, 0.0f, tNormal);
		tNormal.normalizeLocal();
		
		// test triangle's face normal
		float trigOffset = tvA.dot(tNormal);
		sortedProjection.z = Math.abs(aabbHalfExtents.x * tNormal.x)
				           + Math.abs(aabbHalfExtents.y * tNormal.y)
				           + Math.abs(aabbHalfExtents.z * tNormal.z);
		sortedProjection.x = -sortedProjection.z;
		if(sortedProjection.z < trigOffset || sortedProjection.x > trigOffset)
			return false;
		
		// test 9 edge cross-products (saves in projection)
		float aabbR;
		float trigE;// projected coordinate of a triangle's edge
		float trigV;// the remaining vertex's projected coordinate
		
		// TODO: precompute triangle edges
		
		// (1, 0, 0) cross (edge AB)
		projection.set(0.0f, tvA.z - tvB.z, tvB.y - tvA.y);
		aabbR = aabbHalfExtents.y * Math.abs(projection.y) + aabbHalfExtents.z * Math.abs(projection.z);
		trigE = projection.y*tvA.y + projection.z*tvA.z;
		trigV = projection.y*tvC.y + projection.z*tvC.z;
		if(trigE < trigV) { if(trigE > aabbR || trigV < -aabbR) return false; }
		else              { if(trigV > aabbR || trigE < -aabbR) return false; }
		
		// (0, 1, 0) cross (edge AB)
		projection.set(tvB.z - tvA.z, 0.0f, tvA.x - tvB.x);
		aabbR = aabbHalfExtents.x * Math.abs(projection.x) + aabbHalfExtents.z * Math.abs(projection.z);
		trigE = projection.x*tvA.x + projection.z*tvA.z;
		trigV = projection.x*tvC.x + projection.z*tvC.z;
		if(trigE < trigV) { if(trigE > aabbR || trigV < -aabbR) return false; }
		else              { if(trigV > aabbR || trigE < -aabbR) return false; }
		
		// (0, 0, 1) cross (edge AB)
		projection.set(tvA.y - tvB.y, tvB.x - tvA.x, 0.0f);
		aabbR = aabbHalfExtents.x * Math.abs(projection.x) + aabbHalfExtents.y * Math.abs(projection.y);
		trigE = projection.x*tvA.x + projection.y*tvA.y;
		trigV = projection.x*tvC.x + projection.y*tvC.y;
		if(trigE < trigV) { if(trigE > aabbR || trigV < -aabbR) return false; }
		else              { if(trigV > aabbR || trigE < -aabbR) return false; }
		
		// (1, 0, 0) cross (edge BC)
		projection.set(0.0f, tvB.z - tvC.z, tvC.y - tvB.y);
		aabbR = aabbHalfExtents.y * Math.abs(projection.y) + aabbHalfExtents.z * Math.abs(projection.z);
		trigE = projection.y*tvB.y + projection.z*tvB.z;
		trigV = projection.y*tvA.y + projection.z*tvA.z;
		if(trigE < trigV) { if(trigE > aabbR || trigV < -aabbR) return false; }
		else              { if(trigV > aabbR || trigE < -aabbR) return false; }
		
		// (0, 1, 0) cross (edge BC)
		projection.set(tvC.z - tvB.z, 0.0f, tvB.x - tvC.x);
		aabbR = aabbHalfExtents.x * Math.abs(projection.x) + aabbHalfExtents.z * Math.abs(projection.z);
		trigE = projection.x*tvB.x + projection.z*tvB.z;
		trigV = projection.x*tvA.x + projection.z*tvA.z;
		if(trigE < trigV) { if(trigE > aabbR || trigV < -aabbR) return false; }
		else              { if(trigV > aabbR || trigE < -aabbR) return false; }
		
		// (0, 0, 1) cross (edge BC)
		projection.set(tvB.y - tvC.y, tvC.x - tvB.x, 0.0f);
		aabbR = aabbHalfExtents.x * Math.abs(projection.x) + aabbHalfExtents.y * Math.abs(projection.y);
		trigE = projection.x*tvB.x + projection.y*tvB.y;
		trigV = projection.x*tvA.x + projection.y*tvA.y;
		if(trigE < trigV) { if(trigE > aabbR || trigV < -aabbR) return false; }
		else              { if(trigV > aabbR || trigE < -aabbR) return false; }
		
		// (1, 0, 0) cross (edge CA)
		projection.set(0.0f, tvC.z - tvA.z, tvA.y - tvC.y);
		aabbR = aabbHalfExtents.y * Math.abs(projection.y) + aabbHalfExtents.z * Math.abs(projection.z);
		trigE = projection.y*tvC.y + projection.z*tvC.z;
		trigV = projection.y*tvB.y + projection.z*tvB.z;
		if(trigE < trigV) { if(trigE > aabbR || trigV < -aabbR) return false; }
		else              { if(trigV > aabbR || trigE < -aabbR) return false; }
		
		// (0, 1, 0) cross (edge CA)
		projection.set(tvA.z - tvC.z, 0.0f, tvC.x - tvA.x);
		aabbR = aabbHalfExtents.x * Math.abs(projection.x) + aabbHalfExtents.z * Math.abs(projection.z);
		trigE = projection.x*tvC.x + projection.z*tvC.z;
		trigV = projection.x*tvB.x + projection.z*tvB.z;
		if(trigE < trigV) { if(trigE > aabbR || trigV < -aabbR) return false; }
		else              { if(trigV > aabbR || trigE < -aabbR) return false; }
		
		// (0, 0, 1) cross (edge CA)
		projection.set(tvC.y - tvA.y, tvA.x - tvC.x, 0.0f);
		aabbR = aabbHalfExtents.x * Math.abs(projection.x) + aabbHalfExtents.y * Math.abs(projection.y);
		trigE = projection.x*tvC.x + projection.y*tvC.y;
		trigV = projection.x*tvB.x + projection.y*tvB.y;
		if(trigE < trigV) { if(trigE > aabbR || trigV < -aabbR) return false; }
		else              { if(trigV > aabbR || trigE < -aabbR) return false; }
		
		// no separating axis found
		return true;
	}
	
	public void getVerticesABC(Vector3f vA, Vector3f vB, Vector3f vC)
	{
		vA.set(vA);
		vB.set(m_vB);
		vC.set(m_vC);
	}
	
	public void getNormal(Vector3f normal)
	{
		normal.set(m_normal);
	}
	
	public Vector3f getNormal()
	{
		return m_normal;
	}
	
	@Override
	public AABB calcTransformedAABB()
	{
		float minX = Float.POSITIVE_INFINITY, maxX = Float.NEGATIVE_INFINITY,
			  minY = Float.POSITIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY,
			  minZ = Float.POSITIVE_INFINITY, maxZ = Float.NEGATIVE_INFINITY;
		
		Vector3f tvA = new Vector3f();
		Vector3f tvB = new Vector3f();
		Vector3f tvC = new Vector3f();
		
		Matrix4f modelMatrix = getModel().getTransform().getModelMatrix();
		
		modelMatrix.mul(m_vA, 1.0f, tvA);
		modelMatrix.mul(m_vB, 1.0f, tvB);
		modelMatrix.mul(m_vC, 1.0f, tvC);
		
		     if(tvA.x > maxX) maxX = tvA.x;
		else if(tvA.x < minX) minX = tvA.x;
		     if(tvA.y > maxY) maxY = tvA.y;
		else if(tvA.y < minY) minY = tvA.y;
		     if(tvA.z > maxZ) maxZ = tvA.z;
		else if(tvA.z < minZ) minZ = tvA.z;
		     
		     if(tvB.x > maxX) maxX = tvB.x;
		else if(tvB.x < minX) minX = tvB.x;
		     if(tvB.y > maxY) maxY = tvB.y;
		else if(tvB.y < minY) minY = tvB.y;
		     if(tvB.z > maxZ) maxZ = tvB.z;
		else if(tvB.z < minZ) minZ = tvB.z;
				     
		     if(tvC.x > maxX) maxX = tvC.x;
		else if(tvC.x < minX) minX = tvC.x;
		     if(tvC.y > maxY) maxY = tvC.y;
		else if(tvC.y < minY) minY = tvC.y;
		     if(tvC.z > maxZ) maxZ = tvC.z;
		else if(tvC.z < minZ) minZ = tvC.z;
		     
		AABB aabb = new AABB(new Vector3f(minX, minY, minZ),
				             new Vector3f(maxX, maxY, maxZ));
		aabb.relax();
		
		return aabb;
	}
	
	@Override
	public void getAtomicPrimitives(List<AtomicPrimitive> results)
	{
		results.add(this);
	}

	@Override
	public Vector3f calcGeometricAveragePos()
	{
		Vector3f result = new Vector3f(0, 0, 0);
		
		result.addLocal(m_vA);
		result.addLocal(m_vB);
		result.addLocal(m_vC);
		result.divLocal(3.0f);
		
		return result;
	}

	@Override
	public long calcGeometricWeight()
	{
		return 3L;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "\n"
	         + "A" + m_vA + ", B" + m_vB + ", C" + m_vC + ", N" + m_normal;
	}
	
	public void setVertices(Vector3f vA, Vector3f vB, Vector3f vC)
	{
		vA.set(vA);
		m_vB.set(vB);
		m_vC.set(vC);
	}
	
	public void setNormals(Vector3f nA, Vector3f nB, Vector3f nC)
	{
		m_nA.set(nA);
		m_nB.set(nB);
		m_nC.set(nC);
	}
	
	public void setTexCoords(Vector2f txA, Vector2f txB, Vector2f txC)
	{
		m_txA.set(txA);
		m_txB.set(txB);
		m_txC.set(txC);
		
		m_hasTexCoord = true;
	}
	
	public Vector3f getNa()
	{
		return m_nA;
	}
	
	public Vector3f getNb()
	{
		return m_nB;
	}
	
	public Vector3f getNc()
	{
		return m_nC;
	}
	
	public Vector2f getTxA()
	{
		return m_txA;
	}
	
	public Vector2f getTxB()
	{
		return m_txB;
	}
	
	public Vector2f getTxC()
	{
		return m_txC;
	}

	@Override
	public Interpolator genInterpolator(Intersection intersection)
	{
		Vector3f localHitPoint = getModel().getTransform().getInverseModelMatrix().mul(intersection.getHitPoint(), 1.0f);
		
		// projected hit point
		float hitPu, hitPv;
		
		// projected side vector AB and AC
		float abPu, abPv, acPu, acPv;
					
		// find dominant axis
		if(Math.abs(m_normal.x) > Math.abs(m_normal.y))
		{
			// X dominant, projection plane is YZ
			if(Math.abs(m_normal.x) > Math.abs(m_normal.z))
			{
				hitPu = localHitPoint.y - m_vA.y;
				hitPv = localHitPoint.z - m_vA.z;
				abPu  = m_eAB.y;
				abPv  = m_eAB.z;
				acPu  = m_eAC.y;
				acPv  = m_eAC.z;
			}
			// Z dominant, projection plane is XY
			else
			{
				hitPu = localHitPoint.x - m_vA.x;
				hitPv = localHitPoint.y - m_vA.y;
				abPu  = m_eAB.x;
				abPv  = m_eAB.y;
				acPu  = m_eAC.x;
				acPv  = m_eAC.y;
			}
		}
		// Y dominant, projection plane is ZX
		else if(Math.abs(m_normal.y) > Math.abs(m_normal.z))
		{
			hitPu = localHitPoint.z - m_vA.z;
			hitPv = localHitPoint.x - m_vA.x;
			abPu  = m_eAB.z;
			abPv  = m_eAB.x;
			acPu  = m_eAC.z;
			acPv  = m_eAC.x;
		}
		// Z dominant, projection plane is XY
		else
		{
			hitPu = localHitPoint.x - m_vA.x;
			hitPv = localHitPoint.y - m_vA.y;
			abPu  = m_eAB.x;
			abPv  = m_eAB.y;
			acPu  = m_eAC.x;
			acPv  = m_eAC.y;
		}
		
		// TODO: check if this operation is possible of producing a NaN
		float multiplier = 1.0f / (abPu*acPv - abPv*acPu);
		
		// barycentric coordinate of vertex B in the projected plane
		float baryB = (hitPu*acPv - hitPv*acPu) * multiplier;
		
		// barycentric coordinate of vertex C in the projected plane
		float baryC = (hitPv*abPu - hitPu*abPv) * multiplier;
		
		return new TriangleInterpolator(this, 1.0f - baryB - baryC, baryB, baryC);
	}

	@Override
	public boolean hasTexCoord()
	{
		return m_hasTexCoord;
	}
}
