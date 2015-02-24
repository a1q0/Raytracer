package com.aikocraft.raytracer.graphics;

import com.aikocraft.coffee.math.vectors.Vec3;

public abstract class Geometry {
	public Vec3 color = new Vec3();
	
	public abstract boolean isPointIn(Vec3 p);

}
