package com.aikocraft.raytracer.graphics;

import com.aikocraft.coffee.math.matrices.Mat4;
import com.aikocraft.coffee.math.vectors.Vec3;
import com.aikocraft.coffee.math.vectors.Vec4;

public class RenderThread extends Thread {
	public int x;
	public int y;
	public int[] pixels;
	
	public int percentage = 0;
	
	public boolean running = true;
	
	public RenderThread(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void run() {	
		synchronized (RenderEngine.i.lock) {
			try {
				RenderEngine.i.lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		pixels = new int[RenderEngine.nph/2*RenderEngine.npw/2];
		
		while (running) {				
			Vec3 rPos = Camera.pos.copy();
			Vec3 rDir = Camera.dir.copy();			
			float fovx = (float) (Math.PI / 4f);
			float fovy = RenderEngine.npw / RenderEngine.nph * fovx;
			Ray r = new Ray(rPos, rDir);
			
			double fovxTan = Math.tan(fovx);
			double fovyTan = Math.tan(fovy);
			
			for (int lxp = 0; lxp < RenderEngine.npw/2; lxp++) {
				for (int lyp = 0; lyp < RenderEngine.nph/2; lyp++) {											
					int xp = lxp + RenderEngine.npw / 2 * x;
					int yp = lyp + RenderEngine.nph / 2 * y;
					
					rDir = Camera.dir.copy();					
					
					float x = (float) (((2f * xp - RenderEngine.npw) / RenderEngine.npw) * fovxTan);
					float y = (float) (((2f * yp - RenderEngine.nph) / RenderEngine.nph) * fovyTan);
									
					rDir.set(new Vec3(x, y, RenderEngine.nearPlane+0.9f));
					
					Mat4 rotMatY = new Mat4().rotationY((float) Math.toRadians(Camera.rot.y));
					Vec4 rDir4 = rotMatY.mul(new Vec4(rDir.x, rDir.y, rDir.z, 0));
					rDir.set(rDir4.x, rDir4.y, rDir4.z);
					
					r = new Ray(rPos, rDir);	
					pixels[lxp + lyp * RenderEngine.npw/2] = r.getColor(); 
				}
			}
			
			synchronized (RenderEngine.i.lock) {
				try {
					RenderEngine.i.lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void onResize() {
		pixels = new int[RenderEngine.nph/2*RenderEngine.npw/2];
	}
}