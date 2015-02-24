package com.aikocraft.raytracer.graphics;

import com.aikocraft.raytracer.input.Input;
import com.aikocraft.coffee.math.vectors.Vec3;

public class Camera {
	public static Vec3 pos = new Vec3(0, 0, 0);
	public static Vec3 rot = new Vec3(0, 0, 0);
	public static Vec3 dir = new Vec3(0, 0, 1);
	
	public static void update() {		
		if (Input.getKey(Input.K_W)) {
			pos.x += Math.sin(Math.toRadians(rot.y)) * 0.05f;
			pos.z += Math.cos(Math.toRadians(rot.y)) * 0.05f;
			
			pos.print();
		}

		if (Input.getKey(Input.K_S)) {
			pos.x += Math.sin(Math.toRadians(rot.y - 180)) * 0.05f;
			pos.z += Math.cos(Math.toRadians(rot.y - 180)) * 0.05f;
		}
		
		if (Input.getKey(Input.K_SHIFT)) {
			pos.y -= 0.05f;
		}

		if (Input.getKey(Input.K_SPACE)) {
			pos.y += 0.05f;
		}

		if (Input.getKey(Input.K_A)) {
			pos.x += Math.sin(Math.toRadians(rot.y - 90)) * 0.05f;
			pos.z += Math.cos(Math.toRadians(rot.y - 90)) * 0.05f;
		}

		if (Input.getKey(Input.K_D)) {
			pos.x += Math.sin(Math.toRadians(rot.y + 90)) * 0.05f;
			pos.z += Math.cos(Math.toRadians(rot.y + 90)) * 0.05f;
		}
		
		if (Input.getKey(Input.K_DOWN)) {
			rot.x += 1;
			
			dir.y = (float) Math.sin(Math.toRadians(rot.x));
		}
		
		if (Input.getKey(Input.K_UP)) {
			rot.x -= 1;			
			
			dir.y = (float) Math.sin(Math.toRadians(rot.x));
		}
		
		if (Input.getKey(Input.K_RIGHT)) {			
			rot.y += 1;
		
			dir.x = (float) -(Math.sin(Math.toRadians(rot.y)));
			dir.z = (float) (Math.cos(Math.toRadians(rot.y)));
		}
		
		if (Input.getKey(Input.K_LEFT)) {
			rot.y -= 1;
			
			dir.x = (float) -(Math.sin(Math.toRadians(rot.y)));
			dir.z = (float) (Math.cos(Math.toRadians(rot.y)));
		}
	}
}
