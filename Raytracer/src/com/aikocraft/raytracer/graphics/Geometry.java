package com.aikocraft.raytracer.graphics;

import com.aikocraft.coffee.math.vectors.Vec3;

public abstract class Geometry {
	public Vec3 color = new Vec3();
	public float reflectivity = 0.000000035f;
	
	public Geometry(Vec3 color, float reflectivity) {
		this.color.set(color);
		this.reflectivity = reflectivity;
		RenderEngine.geoms.add(this);
	}
}
