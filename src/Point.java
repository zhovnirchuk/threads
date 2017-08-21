
public class Point {
	
	private int coord;
	
	private short density;
	private short color;
	
	private double weight;
	
	private boolean exists;
	
	Point(int coord, short color){
		this.coord = coord;
		this.density = 0;
		this.color = color;
		this.weight = 0.0;
		this.exists = false;
	}
	
	public double getWeight(){
		return this.weight = (double) color / (double) density;
	}
	
	public void setExists(boolean exists){
		this.exists = exists;
	}
	
	public boolean isExists(){
		return this.exists;
	}
	
	public void setColor(short color){
		this.color = color;
	}

	public void incrementDensity() {
		this.density++;
	}
	
	
}
