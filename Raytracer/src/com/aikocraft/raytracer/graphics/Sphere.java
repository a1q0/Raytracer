package com.aikocraft.raytracer.graphics;

import com.aikocraft.coffee.math.vectors.Vec3;

public class Sphere extends Geometry {
	public Vec3 pos = new Vec3();
	public float r;
	
	public Sphere(Vec3 pos, float r, Vec3 color) {
		this.pos.set(pos);
		this.color.set(color);
		this.r = r;
		RenderEngine.geoms.add(this);
	}

	@Override
	public boolean isPointIn(Vec3 p) {
		if (pos.distance(p) < r)
			return true;
		
		return false;
	}

	public Vec3 getSphereToPointNormal(Vec3 pos) {
		return pos.copy().sub(this.pos).normalize();
	}

	public Vec3 getPointToSphereNormal(Vec3 pos) {
		return this.pos.copy().sub(pos).normalize();
	}
}
