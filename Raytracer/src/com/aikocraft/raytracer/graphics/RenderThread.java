package com.aikocraft.raytracer.graphics;

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
			Ray r;
			
			for (int lxp = 0; lxp < RenderEngine.npw/2; lxp++) {
				for (int lyp = 0; lyp < RenderEngine.nph/2; lyp++) {											
					int xp = lxp + RenderEngine.npw / 2 * x;
					int yp = lyp + RenderEngine.nph / 2 * y;
										
					r = Camera.getRay(xp, yp);	
					pixels[lxp + lyp * RenderEngine.npw/2] = r.getColor(0); 
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