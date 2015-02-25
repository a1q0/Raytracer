package com.aikocraft.raytracer.graphics;

import com.aikocraft.coffee.math.vectors.Vec3;

public class Ray {
	public Vec3 pos = new Vec3();
	public Vec3 dir = new Vec3();
	public float precision = 0.1f;

	public int xp, yp;

	public int color = 0;

	public Ray(Vec3 pos, Vec3 dir) {
		this.pos.set(pos);
		this.dir.set(dir);
	}
	
	public int getColor() {		
		double zBuffer = 1000;
		
		for (Geometry g : RenderEngine.geoms) {
			if (this.intersect(g)) {
				Vec3 hitPos = getHitPos(g);
				
				if (hitPos == null) {
					continue;
				} else {
				}
				
				float dist = hitPos.squaredDistance(Camera.pos);
				
				if (zBuffer >  dist && dist > 0) {
					zBuffer = hitPos.squaredDistance(Camera.pos);
				
					Vec3 finalLight = getFinalColor(hitPos, g);
					
					color = RenderEngine.rgbToHex(Math.min((int) (255 * finalLight.x), 255), 
												  Math.min((int) (255 * finalLight.y), 255), 
												  Math.min((int) (255 * finalLight.z), 255));
				}
			}
		}

		return color;
	}
	

	public boolean intersect(Geometry g) {
		if (g instanceof Sphere)
			return intersectSphere((Sphere) g);
		
		return false;
	}
	
	private float a = -1, b = -1, c = -1, d = -1;

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
		
		if (d > 0)
			return true;
		else 
			return false;
	}
	
	private Vec3 getFinalColor(Vec3 point, Geometry g) {
		Vec3 finalLight = new Vec3();
		
		Vec3 surfaceN = ((Sphere) g).getSphereToPointNormal(point);			
		Vec3 viewN = new Vec3(Camera.pos.copy().sub(point).normalize());
		
		for (Light l : RenderEngine.lights) {
			finalLight.add(getLight(point, g, l, surfaceN, viewN));
		}
	
		return finalLight;
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
		
		if (inShadow)
			light.add(diffuseLight.copy().mul(0.5f).mul(att));				
		else {
			light.add(diffuseLight.copy().add(specularLight).mul(att));
		}
		
		return light;
	}

	private Vec3 getHitPos(Geometry g) {		
		Sphere s = (Sphere) g;
		
		Vec3 dist = dir.copy().normalize().mul(RenderEngine.farPlane-RenderEngine.nearPlane);
		
		if (a == -1)
			a = dist.x*dist.x + dist.y*dist.y + dist.z*dist.z;

		if (b == -1)
			b = 2*dist.x*(pos.x-s.pos.x) 
				+ 2*dist.y*(pos.y-s.pos.y) 
				+ 2*dist.z*(pos.z-s.pos.z);

		if (c == -1)
			c = s.pos.x*s.pos.x + s.pos.y*s.pos.y + s.pos.z*s.pos.z 
				+ pos.x*pos.x + pos.y*pos.y + pos.z*pos.z 
				+ (-2 * (s.pos.x*pos.x + s.pos.y*pos.y + s.pos.z*pos.z))
				- s.r*s.r;
		
		if (d == -1)
			d = (b*b) - (4*a*c);
		
		float t = (float) ((-b-Math.sqrt(d)) / (2*a));
		
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

	private float getHitDist(Geometry g) {
		reset();
		Vec3 p = getHitPos(g);
		
		if (p == null)
			return 10000f;
		
		return p.squaredDistance(pos);
	}
	
	public void reset() {
		a = -1;
		b = -1;
		c = -1;
		d = -1;
	}
}
