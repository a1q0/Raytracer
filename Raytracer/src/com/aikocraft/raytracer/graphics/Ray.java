package com.aikocraft.raytracer.graphics;

import java.util.ArrayList;

import com.aikocraft.coffee.math.vectors.Vec3;

public class Ray {
	public Vec3 pos = new Vec3();
	public Vec3 dir = new Vec3();
	public int maxRecursiveRays = 10;
	public int xp, yp;

	public int color = 0;

	public Ray(Vec3 pos, Vec3 dir) {
		this.pos.set(pos);
		this.dir.set(dir);
	}
	
	public int getColor(int currentRecursive) {	
		maxRecursiveRays = 2;
		
		if (RenderEngine.screenshot)
			maxRecursiveRays = 100;
		
		
		Geometry g = intersectFirst(RenderEngine.geoms);
		
		if (g == null)
			return 0x0;
		
		Vec3 hitPos = getHitPos(g);
		
		if (hitPos == null)
			return 0x0;
		
		Vec3 finalColor = getFinalColor(hitPos, g, currentRecursive);
		
		color = RenderEngine.rgbToHex(Math.min((int) (255 * finalColor.x), 255), 
									   Math.min((int) (255 * finalColor.y), 255), 
									   Math.min((int) (255 * finalColor.z), 255));

		return color;
	}
	

	public boolean intersect(Geometry g) {
		if (g instanceof Sphere)
			return intersectSphere((Sphere) g);
		
		return false;
	}
	
	private float a = -1, b = -1, c = -1, d = -1, t = -1;

	private boolean intersectSphere(Sphere s) {		
		Vec3 dist = dir.copy().normalize().mul(RenderEngine.farPlane-RenderEngine.nearPlane);
		
		a = dist.x*dist.x + dist.y*dist.y + dist.z*dist.z;

		b = 2*dist.x*(pos.x-s.pos.x) 
		  + 2*dist.y*(pos.y-s.pos.y) 
		  + 2*dist.z*(pos.z-s.pos.z);

		c = (s.pos.x*s.pos.x + s.pos.y*s.pos.y + s.pos.z*s.pos.z)
		  + (pos.x*pos.x + pos.y*pos.y + pos.z*pos.z) 
		  + (-2.0f * (s.pos.x*pos.x + s.pos.y*pos.y + s.pos.z*pos.z))
		  - s.r*s.r;
		
		d = (b*b) - (4*a*c);
		
		if (d > 0) {
			t = (float) ((-b-Math.sqrt(d)) / (2*a));
			
			if (t < 0)
				return false;
			
			return true;
		} else 
			return false;
	}
	
	private Vec3 getFinalColor(Vec3 point, Geometry g, int currentRecursive) {
		Vec3 finalLight = new Vec3();
		
		Vec3 surfaceN = ((Sphere) g).getSphereToPointNormal(point);			
		Vec3 viewN = new Vec3(Camera.pos.copy().sub(point).normalize());
		
		for (Light l : RenderEngine.lights) {
			finalLight.add(getLight(point, g, l, surfaceN, viewN));
			currentRecursive++;
			if (currentRecursive <= maxRecursiveRays)
				finalLight.add(getReflectedLight(point, g, l, surfaceN, viewN, currentRecursive));
		}
	
		return finalLight;
	}

	private Vec3 getReflectedLight(Vec3 point, Geometry g, Light l, Vec3 surfaceN, Vec3 viewN, int currentRecursive) {
		Vec3 reflectedLight = new Vec3();
		Vec3 rDir = dir.copy().sub(2).mul(dir.dot(surfaceN)).mul(surfaceN).normalize(); 
		Ray reflectedRay = new Ray(point, rDir);
		
		Geometry g2 = reflectedRay.intersectFirst(RenderEngine.geoms);
		
		if (g2 == null)
			return reflectedLight;

		reflectedLight.add(reflectedRay.getColor(currentRecursive)).mul(g2.color).mul(g.reflectivity);
		
		return reflectedLight;
	}

	private Vec3 getLight(Vec3 point, Geometry g, Light l, Vec3 surfaceN, Vec3 viewN) {
		Vec3 light = new Vec3();
		Vec3 diffuseLight = new Vec3();
		Vec3 specularLight = new Vec3();
		
		Vec3 lightN = l.getPointToLightNormal(point);
		float dot = lightN.dot(surfaceN);
		
		diffuseLight = l.color.copy().mul(g.color).mul(Math.max(0.0f, dot));			
		
		Vec3 reflN = new Vec3(surfaceN).mul(dot).mul(2).sub(lightN);
		specularLight = l.color.copy().mul(g.color).mul((float) Math.pow(Math.max(reflN.dot(viewN), 0), 16));
		
		float sqrDist = point.squaredDistance(l.pos);
		float dist = (float) Math.sqrt(sqrDist);
		
		float att = 1f / (l.quadAtt * sqrDist + l.linAtt * dist + l.constAtt);
		
		boolean inShadow = l.isPointInShadow(g, point);
		
		if (inShadow) {
			light.add(diffuseLight.copy().mul(0.1f).mul(att));				
		} else {
			light.add(diffuseLight.copy().add(specularLight).mul(att));
		}
		
		return light;
	}

	public Vec3 getHitPos(Geometry g) {		
		Sphere s = (Sphere) g;
		
		Vec3 dist = dir.copy().normalize().mul(RenderEngine.farPlane-RenderEngine.nearPlane);
		
		a = dist.x*dist.x + dist.y*dist.y + dist.z*dist.z;

		b = 2*dist.x*(pos.x-s.pos.x) 
			+ 2*dist.y*(pos.y-s.pos.y) 
			+ 2*dist.z*(pos.z-s.pos.z);

		c = s.pos.x*s.pos.x + s.pos.y*s.pos.y + s.pos.z*s.pos.z 
			+ pos.x*pos.x + pos.y*pos.y + pos.z*pos.z 
			+ (-2 * (s.pos.x*pos.x + s.pos.y*pos.y + s.pos.z*pos.z))
			- s.r*s.r;
	
		d = (b*b) - (4*a*c);
	
	
		t = (float) ((-b-Math.sqrt(d)) / (2*a));
		
		if (t < 0)
			return null;
		
		return new Vec3(dist).mul(t).add(pos);
	}

	public boolean intersectFirst(Geometry g1, Geometry g2) {		
		if (getHitDist(g1) < getHitDist(g2))
			return true;
		else 
			return false;
	}
	
	public Geometry intersectFirst(ArrayList<Geometry> geoms) {
		ArrayList<Geometry> intersecting = new ArrayList<Geometry>();
		Geometry first = null;
		
		for (Geometry g : geoms) {
			if (intersect(g))
				intersecting.add(g);
		}
		
		if (intersecting.size() == 0)
			return null;
		
		first = intersecting.get(0);
		
		for (Geometry g : intersecting) {
			if (first == g) 
				continue;
			
			if (!intersectFirst(first, g))
				first = g;
		}
		
		return first;
	}

	private float getHitDist(Geometry g) {
		
		Vec3 p = getHitPos(g);
		
		if (p == null)
			return 10000f;
		
		return p.squaredDistance(pos);
	}
}
