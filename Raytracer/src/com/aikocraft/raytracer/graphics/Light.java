package com.aikocraft.raytracer.graphics;

import com.aikocraft.coffee.math.vectors.Vec3;

public class Light {
	public Vec3 pos = new Vec3();
	
	public Vec3 color = new Vec3(1, 1, 1);
	
	public float constAtt;
	public float linAtt;
	public float quadAtt;
	
	public Light(Vec3 pos, float quadAtt, float linAtt, float constAtt) {
		this.pos.set(pos);
		RenderEngine.lights.add(this);
		
		this.quadAtt = quadAtt;
		this.linAtt = linAtt;
		this.constAtt = constAtt;
	}
	
	public Vec3 getPointToLightNormal(Vec3 pos) {
		return this.pos.copy().sub(pos).normalize();
	}
	
	public Vec3 getLightToPointNormal(Vec3 pos) {
		return pos.copy().sub(this.pos).normalize();
	}
	
	public boolean isPointInShadow(Geometry g1, Vec3 point) {
		for (Geometry g2 : RenderEngine.geoms) {
			if (g2 == g1)
				continue;
			
			if (g1 instanceof Sphere && g2 instanceof Sphere) {
				Sphere s1 = (Sphere) g1;
				Sphere s2 = (Sphere) g2;
				
				if (pos.squaredDistance(s2.pos) > pos.squaredDistance(s1.pos))
					continue;
				
				Ray lightRay = new Ray(pos, point.copy().sub(pos).normalize());
				
				if (!lightRay.intersectFirst(g1, g2))
					return true;
			}
		}		
		
		return false;
	}
}
