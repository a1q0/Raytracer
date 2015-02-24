package com.aikocraft.raytracer.graphics;

import com.aikocraft.coffee.math.vectors.Vec3;

public class Ray {
	public Vec3 pos = new Vec3();
	public Vec3 dir = new Vec3();
	public float precision = 0.1f;
	public float max = 15f;
	private float previewPrecision = 0.05f;
	private float screenshotPrecision = 0.01f; 
	
	public int xp, yp;

	public int color = 0;
	
	public int getColor() {
		if (RenderEngine.screenshot)
			precision = screenshotPrecision;
		else 
			precision = previewPrecision;
		
		double zBuffer = 1000;
		
		for (Geometry g : RenderEngine.geoms) {
			if (this.intersect(g)) {
				Vec3 hitPos = getHitPos(g);
				
				if (zBuffer >= hitPos.squaredDistance(Camera.pos)) {
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
	
	public Ray(Vec3 pos, Vec3 dir) {
		this.pos.set(pos);
		this.dir.set(dir);
	}

	public boolean intersect(Geometry g) {
		if (g instanceof Sphere)
			return intersectSphere((Sphere) g);
		
		return false;
	}

	private boolean intersectSphere(Sphere s) {
		if (RenderEngine.screenshot)
			precision = screenshotPrecision;
		else 
			precision = 0.5f;
		
		for (float t = RenderEngine.nearPlane; t < RenderEngine.farPlane; t+=precision) {
			Vec3 tPos = dir.copy().normalize().mul(t).add(pos);
			
			if (s.isPointIn(tPos)) 
				return true;
		}
		
		return false;
	}
	
	public boolean intersectGeometryAt(Geometry g, float t) {
		if (g instanceof Sphere)
			return intersectSphereAt((Sphere) g, t);
		
		return false;
	}

	private boolean intersectSphereAt(Sphere s, float t) {		
		Vec3 tPos = dir.copy().normalize().mul(t).add(pos);
		
		if (s.isPointIn(tPos)) 
			return true;
		
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
		if (RenderEngine.screenshot)
			precision = screenshotPrecision;
		else 
			precision = 0.5f;		
		
		for (float t = RenderEngine.nearPlane; t < RenderEngine.farPlane; t+=precision) {
			Vec3 tPos = dir.copy().normalize().mul(t).add(pos);
			
			if (g.isPointIn(tPos)) 
				return tPos;
		}
		
		return null;
	}

	public boolean intersectFirst(Geometry g1, Geometry g2) {		
		if (RenderEngine.screenshot)
			precision = screenshotPrecision;
		else 
			precision = 0.9f;
		
		for (float t = RenderEngine.nearPlane; t < RenderEngine.farPlane; t+=precision) {											
			if (intersectGeometryAt(g2, t))
				return false;
			else if (intersectGeometryAt(g1, t))
				return true;
		}		
		
		return false;
	}
}
