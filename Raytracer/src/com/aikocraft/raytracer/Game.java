package com.aikocraft.raytracer;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.aikocraft.raytracer.graphics.Camera;
import com.aikocraft.raytracer.graphics.RenderEngine;
import com.aikocraft.raytracer.input.Input;
import com.aikocraft.raytracer.input.InputManager;

public class Game extends JFrame implements Runnable {
	private static final long serialVersionUID = 2674675373969411773L;

	public static Game i;
	
	
	public int w = 640, h = 640;
	public int res = 16;
	int npw = (int) (w / res), nph = (int) (h / res);
	
	BufferedImage image = new BufferedImage(npw, nph, BufferedImage.TYPE_INT_RGB);
	int[] pixels = ((DataBufferInt) (image.getRaster().getDataBuffer())).getData();
	BufferStrategy bs;
	
	RenderEngine re;
	
	public InputManager im;
	
	public static void main(String[] args) {
		new Game().start();
	}
	
	public Game() {
		i = this;
	}
	
	public void start() {
		this.setTitle("Raytracer");
		this.setSize(w, h);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		
		im = new InputManager();
		this.addKeyListener(im);
		this.addMouseListener(im);
		this.addMouseMotionListener(im);
				
		this.createBufferStrategy(1);
		bs = this.getBufferStrategy();
		
		re = new RenderEngine(w, h, res, npw, nph, pixels);
		
		new Thread(this).start();
	}
	
	public void stop() {
		System.exit(0);
	}
	
	private int frames = 0;
	
	public void loop() {
		long last = System.nanoTime();
		long timer = System.currentTimeMillis();
		
		while (true) {
			if (System.nanoTime() - last > 1000000000.0/60.0) {
				update();
			}		
			
			frames++;
			render();
			
			if (System.currentTimeMillis() - timer >= 1000) {
				this.setTitle("" + frames);
				frames = 0;
				timer += 1000;
			}
		}
	}
	
	public void render() {		
		re.render();
		renderBuffer();
	}
	
	public void renderBuffer() {
		Graphics g = bs.getDrawGraphics();
		
		int[] tmp =  Arrays.copyOf(pixels, pixels.length);		
		
		for (int i = 0; i < npw; i++) {
			for (int j = 0; j < nph; j++) {
				int invj = nph - j - 1;
 				pixels[i + j * npw] = tmp[i + invj * npw];
			}	
		}
		
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null, null);		
		g.dispose();
		bs.show();
	}
	
	public void update() {
		Camera.update();
		
		if (Input.getKey(Input.K_F5))
			decreaseRes();
		if (Input.getKey(Input.K_F6))
			increaseRes();
		if (Input.getKey(Input.K_ENTER))
			screenshot();
		if (Input.getKey(Input.K_L))
			RenderEngine.lights.get(0).pos.set(Camera.pos);
	}

	private void increaseRes() {
		res-=2;
		
		if (res < 4)
			res = 4;
		
		setRes(res);
	}

	private void decreaseRes() {
		res+=2;
		
		if (res > 64)
			res = 64;
		
		setRes(res);		
	}
	
	private void setRes(int newRes) {
		res = newRes;
		
		npw = (int) (w / res);
		nph = (int) (h / res);	
		
		if (npw % 2 == 1)
			npw++;
		if (nph % 2 == 1)
			nph++;
		
		RenderEngine.npw = npw;
		RenderEngine.nph = nph;
		
		image = new BufferedImage(npw, nph, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) (image.getRaster().getDataBuffer())).getData();
		RenderEngine.i.pixels = pixels;
		
		RenderEngine.i.onResize();
	}
	
	private void screenshot() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int viewRes = res;
		setRes(1);
		RenderEngine.screenshot = true;
		render();
		
		File outputfile = new File("Raytracer_Screenshot_0.png");
		int counter = 0;
		while (outputfile.exists()) {
			counter++;
			outputfile = new File("Raytracer_Screenshot_" + counter + ".png");
		}
		
		if (RenderEngine.screenshot) {
			System.out.println("Press enter to save screenshot as : Raytracer_Screenshot_" + counter + ".png");
			System.out.println("width: " + npw + " height:" + nph);
		}
		
		try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		setRes(viewRes);
		
		RenderEngine.screenshot = false;
		
		System.out.println("Screenshot saved");
	}

	@Override
	public void run() {
		loop();
	}
}
