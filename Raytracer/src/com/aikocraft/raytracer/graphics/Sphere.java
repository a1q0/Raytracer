package com.aikocraft.raytracer.graphics;

import com.aikocraft.coffee.math.vectors.Vec3;

public class Sphere extends Geometry {
	public Vec3 pos = new Vec3();
	public float r;
	
	public Sphere(Vec3 pos, float r, Vec3 color, float reflectivity) {
		super(color, reflectivity);
		this.pos.set(pos);
		this.r = r;
	}

	public Vec3 getSphereToPointNormal(Vec3 pos) {
		return pos.copy().sub(this.pos).normalize();
	}

	public Vec3 getPointToSphereNormal(Vec3 pos) {
		return this.pos.copy().sub(pos).normalize();
	}
}
