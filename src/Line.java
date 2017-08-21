import java.util.ArrayList;


public class Line implements Comparable<Line>{
	
	private ArrayList<Point> points;
	private double weight;
	
	Line( ArrayList<Point> points ){
		this.points = points;
		this.weight = 0.0;
	}
	
	public double getWeight(){
		for(Point p : points)
			this.weight += p.getWeight();
		this.weight /= this.points.size();
		return this.weight;
	}
	
	public void setExists(){
		for(Point p : this.points){
			p.setExists(true);
			p.setColor( (short) 255 );
		}
	}

	@Override
	public int compareTo(Line l) {
		if(this.weight < l.weight) return -1;
		if(this.weight > l.weight) return +1;
		return 0;
	}
	
}