package com.aikocraft.raytracer.graphics;

import java.util.ArrayList;

import com.aikocraft.raytracer.Game;
import com.aikocraft.raytracer.input.Input;
import com.aikocraft.coffee.math.matrices.Mat4;
import com.aikocraft.coffee.math.vectors.Vec3;
import com.aikocraft.coffee.math.vectors.Vec4;
//import static com.aikocraft.coffee.math.MathUtils.*;

public class Camera {
	public static Vec3 pos = new Vec3(0, 0, 0);
	public static Vec3 rot = new Vec3(0, 0, 0);
	public static Vec3 dir = new Vec3(0, 0, 1);
	
	public static float fovx = (float) (Math.PI / 4f);
	public static float fovy = RenderEngine.npw / RenderEngine.nph * fovx;
	
	private static double fovxTan = Math.tan(fovx);
	private static double fovyTan = Math.tan(fovy);
	
	private static Geometry dragging = null;
	
	public static void update() {
		mouse();
		
		if (Input.getKey(Input.K_W)) {
			pos.x += Math.sin(Math.toRadians(rot.y)) * 0.05f;
			pos.z += Math.cos(Math.toRadians(rot.y)) * 0.05f;
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

	private static void mouse() {
		if (Input.clicking && dragging == null) {
			Ray r = Camera.getRay(Input.mx/Game.i.res, Input.my/Game.i.res);
			
			ArrayList<Geometry> intersecting = new ArrayList<Geometry>();
			Geometry selected = null;
			
			for (Geometry g : RenderEngine.geoms) {
				if (r.intersect(g))
					intersecting.add(g);
			}
			
			if (intersecting.size() == 0)
				return;
			
			selected = intersecting.get(0);
			
			for (Geometry g : intersecting) {
				if (selected == g) 
					continue;
				
				if (!r.intersectFirst(selected, g))
					selected = g;
			}
			
			if (selected == null)
				return;
			
			dragging = selected;			
		} else if (Input.clicking && dragging != null){
			Ray r = Camera.getRay(Input.mx/Game.i.res, Input.my/Game.i.res);
			
			Vec3 hitPos = r.getHitPos(dragging);
			
			if (hitPos == null) {
				dragging = null;
			} else {
//				((Sphere) dragging).pos.x = (float) (hitPos.x + ((Sphere) dragging).r * sin(rad(rot.y)));
//				((Sphere) dragging).pos.z = (float) (hitPos.z + ((Sphere) dragging).r * cos(rad(rot.y)));
				((Sphere) dragging).pos.y = hitPos.y;	
			}
		} else if (!Input.clicking && dragging != null) {
			dragging = null;
		}		
	}

	public static Ray getRay(int xp, int yp) {
		Vec3 dir = Camera.dir.copy();
		Vec3 pos = Camera.pos.copy();
		
		float xR = (float) (((2f * xp - RenderEngine.npw) / RenderEngine.npw) * fovxTan);
		float yR = (float) (((2f * yp - RenderEngine.nph) / RenderEngine.nph) * fovyTan);
						
		dir.set(new Vec3(xR, yR, 0.7f)).normalize();
		
		Mat4 rotMatY = new Mat4().rotationY((float) Math.toRadians(Camera.rot.y));
		Vec4 rDir4 = rotMatY.mul(new Vec4(dir.x, dir.y, dir.z, 0));
		dir.set(rDir4.x, rDir4.y, rDir4.z).normalize();
		
		return new Ray(pos, dir);
	}
}
