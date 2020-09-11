package se.sigma.boostapp.boost_app_java.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;


@Entity
@Table(name="stepMonth")
public class MonthStep {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@ApiModelProperty(notes = "The database generated monthStep ID")
	private long id;
	
	@Column(name = "user_id")
	//@ApiModelProperty(notes = "User Id")
    private String user_id;
	
	@Column(name = "January")
	//@ApiModelProperty(notes = "January")
    private int january;
	
	@Column(name = "February")
	//@ApiModelProperty(notes = "February")
    private int february;
	
	@Column(name = "March")
	//@ApiModelProperty(notes = "March")
    private int march;
	
	@Column(name = "April")
	//@ApiModelProperty(notes = "April")
    private int april;
	
	@Column(name = "May")
	//@ApiModelProperty(notes = "May")
    private int may;
	
	@Column(name = "June")
	//@ApiModelProperty(notes = "June")
    private int june;
	
	@Column(name = "July")
	//@ApiModelProperty(notes = "July")
    private int july;
	
	@Column(name = "Augusty")
	//@ApiModelProperty(notes = "Augusty")
    private int august;
	
	@Column(name = "September")
	//@ApiModelProperty(notes = "September")
    private int semptember;
	
	@Column(name = "October")
	//@ApiModelProperty(notes = "October")
    private int october;
	
	@Column(name = "November")
	//@ApiModelProperty(notes = "November")
    private int november;
	
	@Column(name = "December")
	//@ApiModelProperty(notes = "December")
    private int december;
	
	@Column(name = "Year")
	//@ApiModelProperty(notes = "Year")
    private int year;


    public MonthStep() {

    }


	public MonthStep(long id, String user_id, int january, int february, int march, int april, int may, int june,
			int july, int august, int semptember, int october, int november, int december, int year) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.january = january;
		this.february = february;
		this.march = march;
		this.april = april;
		this.may = may;
		this.june = june;
		this.july = july;
		this.august = august;
		this.semptember = semptember;
		this.october = october;
		this.november = november;
		this.december = december;
		this.year = year;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getUser_id() {
		return user_id;
	}


	public void setUser_id(String user_id) {
		this.user_id = user_id;
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


	public int getSemptember() {
		return semptember;
	}


	public void setSemptember(int semptember) {
		this.semptember = semptember;
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
 
}

