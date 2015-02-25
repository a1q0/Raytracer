package com.aikocraft.raytracer.input;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.aikocraft.raytracer.Game;

public class InputManager implements KeyListener, FocusListener, MouseListener, MouseMotionListener {

	public boolean[] keys = new boolean[25565];
	
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	public void keyTyped(KeyEvent e) {

	}

	public void focusLost(FocusEvent e) {
		for (int i = 0; i < keys.length; i++) {
			keys[i] = false;
		}
		
		Input.focused = false;
	}

	public void focusGained(FocusEvent arg0) {
		Input.focused = true;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		Input.mx = arg0.getX();
		Input.my = Game.i.h - arg0.getY();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		Input.mx = arg0.getX();
		Input.my = Game.i.h - arg0.getY();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		Input.clicking = true;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		Input.clicking = false;
	}	
}
