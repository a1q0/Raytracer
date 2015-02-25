package com.aikocraft.raytracer.graphics;

import com.aikocraft.coffee.math.vectors.Vec3;

public class Plane extends Geometry {
	public Vec3 pos;
	
	public Plane(Vec3 color, float reflectivity) {
		super(color, reflectivity);
	}

}
