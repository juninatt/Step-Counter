package se.sigma.boostapp.boost_app_java.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Table(name="stepmonth")
public class MonthStep {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated monthStep ID")
	private long id;
	
	@Column(name = "user_id")
	@ApiModelProperty(notes = "userId")
    private String userId;
	
	@Column(name = "january")
	@ApiModelProperty(notes = "january")
    private int january;
	
	@Column(name = "february")
	//@ApiModelProperty(notes = "february")
    private int february;
	
	@Column(name = "march")
	//@ApiModelProperty(notes = "march")
    private int march;
	
	@Column(name = "april")
	//@ApiModelProperty(notes = "april")
    private int april;
	
	@Column(name = "may")
	//@ApiModelProperty(notes = "may")
    private int may;
	
	@Column(name = "june")
	//@ApiModelProperty(notes = "june")
    private int june;
	
	@Column(name = "july")
	//@ApiModelProperty(notes = "july")
    private int july;
	
	@Column(name = "august")
	//@ApiModelProperty(notes = "august")
    private int august;
	
	@Column(name = "september")
	//@ApiModelProperty(notes = "september")
    private int september;
	
	@Column(name = "october")
	//@ApiModelProperty(notes = "october")
    private int october;
	
	@Column(name = "november")
	//@ApiModelProperty(notes = "november")
    private int november;
	
	@Column(name = "december")
	//@ApiModelProperty(notes = "december")
    private int december;
	
	@Column(name = "year")
	//@ApiModelProperty(notes = "year")
    private int year;


    public MonthStep() {

    }

	public MonthStep(long id, String userId, int january, int february, int march, int april, int may, int june,
					 int july, int august, int september, int october, int november, int december, int year) {
		super();
		this.id = id;
		this.userId = userId;
		this.january = january;
		this.february = february;
		this.march = march;
		this.april = april;
		this.may = may;
		this.june = june;
		this.july = july;
		this.august = august;
		this.september = september;
		this.october = october;
		this.november = november;
		this.december = december;
		this.year = year;
	}

    public MonthStep(String userId, int year) {
    	this.userId = userId;
    	this.year = year;
    }

    public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public int getJanuary() {
		return january;
	}


	public void setJanuary(int january) {
		this.january = january;
	}


	public int getFebruary() {
		return february;
	}


	public void setFebruary(int february) {
		this.february = february;
	}


	public int getMarch() {
		return march;
	}


	public void setMarch(int march) {
		this.march = march;
	}


	public int getApril() {
		return april;
	}


	public void setApril(int april) {
		this.april = april;
	}


	public int getMay() {
		return may;
	}


	public void setMay(int may) {
		this.may = may;
	}


	public int getJune() {
		return june;
	}


	public void setJune(int june) {
		this.june = june;
	}


	public int getJuly() {
		return july;
	}


	public void setJuly(int july) {
		this.july = july;
	}


	public int getAugust() {
		return august;
	}


	public void setAugust(int august) {
		this.august = august;
	}


	public int getSeptember() {
		return september;
	}


	public void setSeptember(int september) {
		this.september = september;
	}


	public int getOctober() {
		return october;
	}


	public void setOctober(int october) {
		this.october = october;
	}


	public int getNovember() {
		return november;
	}


	public void setNovember(int november) {
		this.november = november;
	}


	public int getDecember() {
		return december;
	}


	public void setDecember(int december) {
		this.december = december;
	}


	public int getYear() {
		return year;
	}


	public void setYear(int year) {
		this.year = year;
	}

	public void setOneMonth(int monthValue, int stepCount){

		switch(monthValue){
			case 1:
				this.setJanuary(this.getJanuary()+stepCount);
				break;
			case 2:
				this.setFebruary(this.getFebruary()+stepCount);
				break;
			case 3:
				this.setMarch(this.getMarch()+stepCount);
				break;
			case 4:
				this.setApril(this.getApril()+stepCount);
				break;
			case 5:
				this.setMay(this.getMay()+stepCount);
				break;
			case 6:
				this.setJune(this.getJune()+stepCount);
				break;
			case 7:
				this.setJuly(this.getJuly()+stepCount);
				break;
			case 8:
				this.setAugust(this.getAugust()+stepCount);
				break;
			case 9:
				this.setSeptember(this.getSeptember()+stepCount);
				break;
			case 10:
				this.setOctober(this.getOctober()+stepCount);
				break;
			case 11:
				this.setNovember(this.getNovember()+stepCount);
				break;
			case 12:
				this.setDecember(this.getDecember()+stepCount);
				break;
		}
	}
 
}