import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

public class Window extends JFrame implements ActionListener{
	static JButton incrRadiusButton = null;
	static JButton decrRadiusButton = null;
	static JButton incrTesselationButton = null;
	static JButton decrTesselationButton = null;
	static JPanel buttonPanel = null;
	private MyCanvas canvas = null;
	
	public static void main(String[] args){
		Window window = new Window();
	}
	
	public Window(){
		createButtonPanel();
		
		this.setTitle("pBengt2 hw1");
		this.setSize(400, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		this.add(buttonPanel);
		this.setResizable(false);		
		this.setVisible(true);	
		
		this.createBufferStrategy(2);
		BufferStrategy strategy = this.getBufferStrategy();
		
		canvas = new MyCanvas();
		this.add(canvas);
	}
	
	private void createButtonPanel(){
		decrRadiusButton = new JButton("- Radius");
		incrRadiusButton = new JButton("+ Radius");		
		decrTesselationButton = new JButton("- Tess");
		incrTesselationButton = new JButton("+ Tess");		
		
		decrRadiusButton.addActionListener(this);
		incrRadiusButton.addActionListener(this);
		decrTesselationButton.addActionListener(this);
		incrTesselationButton.addActionListener(this);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(null);
		buttonPanel.setBackground(Color.black);
		buttonPanel.setBounds(0, 0, 400, 25);
		buttonPanel.setSize(400, 25);
		buttonPanel.add(decrRadiusButton);
		buttonPanel.add(incrRadiusButton);		
		buttonPanel.add(decrTesselationButton);
		buttonPanel.add(incrTesselationButton);	

		decrRadiusButton.setBounds(17, 0, 90, 25);
		incrRadiusButton.setBounds(107, 0, 90, 25);
		decrTesselationButton.setBounds(197, 0, 90, 25);
		incrTesselationButton.setBounds(287, 0, 90, 25);		
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		if (e.getActionCommand() == "+ Radius"){
			if (Circle.radius < 40)
				Circle.radius = Circle.radius+5;
		}
		else if (e.getActionCommand() == "- Radius"){
			if (Circle.radius > 15)
				Circle.radius = Circle.radius-5;
		}
		else if (e.getActionCommand() == "+ Tess"){
			if (Circle.numVertices < 50)
			 Circle.numVertices = Circle.numVertices+5;
		}
		else if (e.getActionCommand() == "- Tess"){
			if (Circle.numVertices > 15)
				Circle.numVertices = Circle.numVertices-5;
		}
	}	
}

class MyCanvas extends JPanel implements MouseListener{
	private Timer time;	
	private int x,y;
	private Shape myShapes[] = new Shape[15];
	private Rectangle backGround;
	private int lastShapeUsed;
	Graphics gg;
	
	public MyCanvas(){
		time = new Timer();			
		this.addMouseListener(this);
		this.setBackground(Color.black);
		this.setSize(400,300);
		x=0;
		y=0;
		lastShapeUsed = 0;
		gg = getGraphics();
		
		backGround = new Rectangle(400, 300, 200, 175);
		backGround.setShapeColor(Color.black);
		backGround.setBorderColor(Color.black);
			
		for (int i=0; i<15; i++){
			myShapes[i] = new Rectangle();
		}
		
		//print timer message
		time.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Timer test");
			}
		}, 0,1000);	
		
		//frame timer
		time.schedule(new TimerTask(){
			@Override
			public void run(){
				frameRefresh();
			}
		}, 0, 17); //60fps		
	}
	
	public void frameRefresh(){
		gg = getGraphics();
		if (gg == null)
			return;		
		backGround.show(gg);
		
		for (int i=0; i<15; i++){
			if (myShapes[i].isActive()){			
				if (myShapes[i].isFalling()){
					myShapes[i].fall(gg);
				}			
				myShapes[i].show(gg);
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e){	
		if (e.getButton() == MouseEvent.BUTTON3){ //right mouse button
			System.out.println("right mouse clicked.");
			if (myShapes[lastShapeUsed] instanceof Rectangle){
				for (int i=0; i<15; i++)
					myShapes[i] = new Circle();		
			}
			else{
				for (int i=0; i<15; i++)
					myShapes[i] = new Rectangle();
			}
		}
		else{ //left mouse button
			System.out.println("mouse clicked.");		
			x = e.getX();
			y = e.getY();
			lastShapeUsed++;
			if (lastShapeUsed == 15)
				lastShapeUsed = 0;			
			myShapes[lastShapeUsed].fixSize(gg);
			myShapes[lastShapeUsed].moveTo(x, y);
			myShapes[lastShapeUsed].show(gg);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e){
		System.out.println("mouse pressed.");
	}
	
	@Override
	public void mouseReleased(MouseEvent e){
		System.out.println("mouse released.");
	}
	
	@Override
	public void mouseEntered(MouseEvent e){
		
	}
	
	@Override
	public void mouseExited(MouseEvent e){
		
	}
	
}

abstract class Shape extends JPanel{
	protected int xMid, yMid;
	protected int collisionHeight;
	protected int collisionWidth;	
	protected double speed;		
	
	protected PolygonVertices verts;
	protected boolean active;
	protected boolean falling;	
	protected Color shapeColor;
	protected Color borderColor;

	void moveTo(int x, int y){
		xMid = x;
		yMid = y;	
		
		if (xMid - collisionWidth < 1)
			xMid = collisionWidth + 1;
		if (xMid + collisionWidth > 393) //393 = right border
			xMid = 393-collisionWidth;
		if (yMid - collisionHeight < 25) //25 = button size
			yMid = 25+collisionHeight;
			
		falling = true;
		active = true;				
	}
	
	void setShapeColor(Color c){
		shapeColor = c;
	}
	
	void setBorderColor(Color c){
		borderColor = c;
	}

	void fall(Graphics gg){
		falling = true;
		
		//gravity = 9.8 m/s^2. time = 1/60th a second.
		//speed = gravity * time = 9.8 * 1/60 = .1633
		speed = speed += .1633;
		
		yMid += speed;
		collisionDetector(gg);
	}	
	
	boolean isFalling(){
		return falling;
	}
	
	boolean isActive(){
		return active;
	}
	
	void show(Graphics gg){
		fixSize(gg);
		
		for (int i=0; i<verts.numVertices; i++){
			verts.xValues[i] += xMid;
			verts.yValues[i] += yMid;
		}
		
		gg.setColor(shapeColor);
		gg.fillPolygon(verts.xValues, verts.yValues, verts.numVertices);
		gg.setColor(borderColor);
		gg.drawPolygon(verts.xValues, verts.yValues, verts.numVertices);	
		
		for (int i=0; i<verts.numVertices; i++){
			verts.xValues[i] -= xMid;
			verts.yValues[i] -= yMid;
		}
	}	
	
	abstract void calculateVertices();
	abstract void fixSize(Graphics gg);
	abstract void collisionDetector(Graphics gg);
}

class Rectangle extends Shape{
	private int width = 30;
	private int height = 20;
	
	public Rectangle(){
		verts = new PolygonVertices();
		xMid = 0;
		yMid = 0;	
		collisionWidth = width/2;
		collisionHeight = height/2;		
		speed = 0;
		falling = false;
		active = false;		
		shapeColor = Color.blue;
		borderColor = Color.green;
		calculateVertices();
	}
	
	public Rectangle(int w, int h, int x, int y){
		verts = new PolygonVertices();
		xMid = x;
		yMid = y;		
		width = w;
		height = h;	
		collisionWidth = width/2;
		collisionHeight = height/2;		
		speed = 0;
		falling = false;
		active = false;		
		shapeColor = Color.blue;
		borderColor = Color.green;
		calculateVertices();
	}

	void calculateVertices(){
		verts.numVertices = 4;
		
		verts.xValues[0] = -(width/2);
		verts.yValues[0] = -(height/2);
		
		verts.xValues[1] = (width/2);
		verts.yValues[1] = -(height/2);
		
		verts.xValues[2] = (width/2);
		verts.yValues[2] = (height/2);
		
		verts.xValues[3] = -(width/2);
		verts.yValues[3] = (height/2);
	}
	
	void collisionDetector(Graphics gg){
		if (yMid > 261){
			yMid = 261;
			speed = speed * -.25;
			if (speed >= - .4){
				falling = false;
				speed = 0;
			}
		}
	}
	
	void fixSize(Graphics gg){
		
	}
}

class Circle extends Shape{
	static int numVertices = 30;
	static int radius = 15;
	private int prevNumVertices = 15;
	private int prevRadius = 15;
	
	public Circle(){
		prevRadius = radius;
		collisionHeight = radius;
		collisionWidth = radius;
		verts = new PolygonVertices();
		
		xMid = 0;
		yMid = 0;
		speed = 0;	
		
		falling = false;
		active = false;	
		shapeColor = Color.red;
		borderColor = Color.orange;	
		
		calculateVertices();
	}

	private void sizeChange(Graphics gg){
		calculateVertices();
		
		prevRadius = radius;
		collisionHeight = radius;
		collisionWidth = radius;		
		prevNumVertices = numVertices;

		fall(gg);
	}
	
	void fixSize(Graphics gg){
		if (prevRadius != radius || prevNumVertices != numVertices)
			sizeChange(gg);		
	}
	
	void calculateVertices(){
		verts.numVertices = numVertices;
		for (int i=0; i < numVertices; i++){
			verts.xValues[i] = (int)(radius * Math.cos(2 * Math.PI * i / numVertices));
			verts.yValues[i] = (int)(radius * Math.sin(2 * Math.PI * i / numVertices));
		}
	}
	
	void collisionDetector(Graphics gg){
		if (prevRadius != radius){
			sizeChange(gg);
			return;
		}		
		
		if (yMid + collisionHeight > 271){
			yMid = 271-collisionHeight;
			speed = speed * -.25;
			if (speed >= - .4){
				falling = false;
				speed = 0;
			}
		}
		
		if (xMid - collisionWidth < 1){
			xMid = collisionWidth+1;
		}
		if (xMid + collisionWidth > 393){
			xMid = 393 - collisionWidth;
		}
	}
}

class PolygonVertices{
	int numVertices;
	int xValues[] = new int[50];
	int yValues[] = new int[50];
	
	PolygonVertices(){
		numVertices = 0;
	}
	
	PolygonVertices(int vertices, int x[], int y[]){
		numVertices = vertices;
		for (int i = 0; i < 50; i++){
			xValues[i] = x[i];
			yValues[i] = y[i];
		}
	}
}
