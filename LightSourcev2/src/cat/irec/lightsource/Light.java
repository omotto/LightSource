package cat.irec.lightsource;

public class Light {

	private String name;
	private int[] coef;
	private int id; 
	
	public Light() {
	}

	public Light(int id, String name, int [] coeff) {
		this.name = name;
		this.id = id;
		this.coef = new int [coeff.length];
		System.arraycopy(coeff, 0, this.coef, 0, coeff.length);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getCoef() {
		return coef;
	}

	public void setCoef(int[] coeff) {
		System.arraycopy(coeff, 0, this.coef, 0, coeff.length);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "Device [id= "+ this.id + ", name= " + this.name + "]";
	}
}

