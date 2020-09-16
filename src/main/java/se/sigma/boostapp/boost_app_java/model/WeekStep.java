package se.sigma.boostapp.boost_app_java.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;


@Entity
@Table(name="stepweek")
public class WeekStep {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		//@ApiModelProperty(notes = "The database generated weekStep ID")
		private long id;
		
		@Column(name = "user_id")
		//@ApiModelProperty(notes = "User Id")
	    private String userId;
		
		//@ApiModelProperty(notes = "Week1")
		@Column(name = "Week1")
	    private int week1;
	
		//@ApiModelProperty(notes = "Week2")
		@Column(name = "Week2")
	    private int week2;
		
		//@ApiModelProperty(notes = "Week3")
		@Column(name = "Week3")
	    private int week3;
		
		//@ApiModelProperty(notes = "Week4")
		@Column(name = "Week4")
	    private int week4;
		
		//@ApiModelProperty(notes = "Week5")
		@Column(name = "Week5")
	    private int week5;
		
		//@ApiModelProperty(notes = "Week6")
		@Column(name = "Week6")
	    private int week6;
		
		//@ApiModelProperty(notes = "Week7")
		@Column(name = "Week7")
	    private int week7;
		
		//@ApiModelProperty(notes = "Week8")
		@Column(name = "Week8")
	    private int week8;
		
		//@ApiModelProperty(notes = "Week9")
		@Column(name = "Week9")
	    private int week9;
		
		//@ApiModelProperty(notes = "Week10")
		@Column(name = "Week10")
	    private int week10;
		
		//@ApiModelProperty(notes = "Week11")
		@Column(name = "Week11")
	    private int week11;
		
		//@ApiModelProperty(notes = "Week12")
		@Column(name = "Week12")
	    private int week12;
		
		//@ApiModelProperty(notes = "Week13")
		@Column(name = "Week13")
	    private int week13;
		
		//@ApiModelProperty(notes = "Week14")
		@Column(name = "Week14")
	    private int week14;
		
		//@ApiModelProperty(notes = "Week15")
		@Column(name = "Week15")
	    private int week15;
		
		//@ApiModelProperty(notes = "Week16")
		@Column(name = "Week16")
	    private int week16;
		
		//@ApiModelProperty(notes = "Week17")
		@Column(name = "Week17")
	    private int week17;
		
		//@ApiModelProperty(notes = "Week18")
		@Column(name = "Week18")
	    private int week18;
		
		//@ApiModelProperty(notes = "Week19")
		@Column(name = "Week19")
	    private int week19;
		
		//@ApiModelProperty(notes = "Week20")
		@Column(name = "Week20")
	    private int week20;
		
		//@ApiModelProperty(notes = "Week21")
		@Column(name = "Week21")
	    private int week21;
		
		//@ApiModelProperty(notes = "Week22")
		@Column(name = "Week22")
	    private int week22;
		
		//@ApiModelProperty(notes = "Week23")
		@Column(name = "Week23")
	    private int week23;
		
		//@ApiModelProperty(notes = "Week24")
		@Column(name = "Week24")
	    private int week24;
		
		//@ApiModelProperty(notes = "Week25")
		@Column(name = "Week25")
	    private int week25;
		
		//@ApiModelProperty(notes = "Week26")
		@Column(name = "Week26")
	    private int week26;
		
		//@ApiModelProperty(notes = "Week27")
		@Column(name = "Week27")
	    private int week27;
		
		//@ApiModelProperty(notes = "Week28")
		@Column(name = "Week28")
	    private int week28;
		
		//@ApiModelProperty(notes = "Week29")
		@Column(name = "Week29")
	    private int week29;
		
		//@ApiModelProperty(notes = "Week30")
		@Column(name = "Week30")
	    private int week30;
		
		//@ApiModelProperty(notes = "Week31")
		@Column(name = "Week31")
	    private int week31;
		
		//@ApiModelProperty(notes = "Week32")
		@Column(name = "Week32")
	    private int week32;
		
		//@ApiModelProperty(notes = "Week33")
		@Column(name = "Week33")
	    private int week33;
		
		//@ApiModelProperty(notes = "Week34")
		@Column(name = "Week34")
	    private int week34;
		
		//@ApiModelProperty(notes = "Week35")
		@Column(name = "Week35")
	    private int week35;
		
		//@ApiModelProperty(notes = "Week36")
		@Column(name = "Week36")
	    private int week36;
		
		//@ApiModelProperty(notes = "Week37")
		@Column(name = "Week37")
	    private int week37;
		
		//@ApiModelProperty(notes = "Week38")
		@Column(name = "Week38")
	    private int week38;
		
		//@ApiModelProperty(notes = "Week39")
		@Column(name = "Week39")
	    private int week39;
		
		//@ApiModelProperty(notes = "Week40")
		@Column(name = "Week40")
	    private int week40;
		
		//@ApiModelProperty(notes = "Week41")
		@Column(name = "Week41")
	    private int week41;
		
		//@ApiModelProperty(notes = "Week42")
		@Column(name = "Week42")
	    private int week42;
		
		//@ApiModelProperty(notes = "Week43")
		@Column(name = "Week43")
	    private int week43;
		
		//@ApiModelProperty(notes = "Week44")
		@Column(name = "Week44")
	    private int week44;
		
		//@ApiModelProperty(notes = "Week45")
		@Column(name = "Week45")
	    private int week45;
		
		//@ApiModelProperty(notes = "Week46")
		@Column(name = "Week46")
	    private int week46;
		
		//@ApiModelProperty(notes = "Week47")
		@Column(name = "Week47")
	    private int week47;
		
		//@ApiModelProperty(notes = "Week48")
		@Column(name = "Week48")
	    private int week48;
		
		//@ApiModelProperty(notes = "Week49")
		@Column(name = "Week49")
	    private int week49;
		
		//@ApiModelProperty(notes = "Week50")
		@Column(name = "Week50")
	    private int week50;
		
		//@ApiModelProperty(notes = "Week51")
		@Column(name = "Week51")
	    private int week51;
		
		//@ApiModelProperty(notes = "Week52")
		@Column(name = "Week52")
	    private int week52;
		
		//@ApiModelProperty(notes = "Week53")
		@Column(name = "Week53")
	    private int week53;
		
		//@ApiModelProperty(notes = "Year")
		@Column(name = "Year")
	    private int year;
		
		
		public WeekStep() {
			
		}
		

		public WeekStep(String userId, int year){
			this.userId = userId;
			this.year = year;
		}


		public WeekStep(long id, String userId, int week1, int week2, int week3, int week4, int week5, int week6,
				int week7, int week8, int week9, int week10, int week11, int week12, int week13, int week14, int week15,
				int week16, int week17, int week18, int week19, int week20, int week21, int week22, int week23,
				int week24, int week25, int week26, int week27, int week28, int week29, int week30, int week31,
				int week32, int week33, int week34, int week35, int week36, int week37, int week38, int week39,
				int week40, int week41, int week42, int week43, int week44, int week45, int week46, int week47,
				int week48, int week49, int week50, int week51, int week52, int week53, int year) {
			super();
			this.id = id;
			this.userId = userId;
			this.week1 = week1;
			this.week2 = week2;
			this.week3 = week3;
			this.week4 = week4;
			this.week5 = week5;
			this.week6 = week6;
			this.week7 = week7;
			this.week8 = week8;
			this.week9 = week9;
			this.week10 = week10;
			this.week11 = week11;
			this.week12 = week12;
			this.week13 = week13;
			this.week14 = week14;
			this.week15 = week15;
			this.week16 = week16;
			this.week17 = week17;
			this.week18 = week18;
			this.week19 = week19;
			this.week20 = week20;
			this.week21 = week21;
			this.week22 = week22;
			this.week23 = week23;
			this.week24 = week24;
			this.week25 = week25;
			this.week26 = week26;
			this.week27 = week27;
			this.week28 = week28;
			this.week29 = week29;
			this.week30 = week30;
			this.week31 = week31;
			this.week32 = week32;
			this.week33 = week33;
			this.week34 = week34;
			this.week35 = week35;
			this.week36 = week36;
			this.week37 = week37;
			this.week38 = week38;
			this.week39 = week39;
			this.week40 = week40;
			this.week41 = week41;
			this.week42 = week42;
			this.week43 = week43;
			this.week44 = week44;
			this.week45 = week45;
			this.week46 = week46;
			this.week47 = week47;
			this.week48 = week48;
			this.week49 = week49;
			this.week50 = week50;
			this.week51 = week51;
			this.week52 = week52;
			this.week53 = week53;
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


		public void setUserId(String user_id) {
			this.userId = user_id;
		}


		public int getWeek1() {
			return week1;
		}


		public void setWeek1(int week1) {
			this.week1 = week1;
		}


		public int getWeek2() {
			return week2;
		}


		public void setWeek2(int week2) {
			this.week2 = week2;
		}


		public int getWeek3() {
			return week3;
		}


		public void setWeek3(int week3) {
			this.week3 = week3;
		}


		public int getWeek4() {
			return week4;
		}


		public void setWeek4(int week4) {
			this.week4 = week4;
		}


		public int getWeek5() {
			return week5;
		}


		public void setWeek5(int week5) {
			this.week5 = week5;
		}


		public int getWeek6() {
			return week6;
		}


		public void setWeek6(int week6) {
			this.week6 = week6;
		}


		public int getWeek7() {
			return week7;
		}


		public void setWeek7(int week7) {
			this.week7 = week7;
		}


		public int getWeek8() {
			return week8;
		}


		public void setWeek8(int week8) {
			this.week8 = week8;
		}


		public int getWeek9() {
			return week9;
		}


		public void setWeek9(int week9) {
			this.week9 = week9;
		}


		public int getWeek10() {
			return week10;
		}


		public void setWeek10(int week10) {
			this.week10 = week10;
		}


		public int getWeek11() {
			return week11;
		}


		public void setWeek11(int week11) {
			this.week11 = week11;
		}


		public int getWeek12() {
			return week12;
		}


		public void setWeek12(int week12) {
			this.week12 = week12;
		}


		public int getWeek13() {
			return week13;
		}


		public void setWeek13(int week13) {
			this.week13 = week13;
		}


		public int getWeek14() {
			return week14;
		}


		public void setWeek14(int week14) {
			this.week14 = week14;
		}


		public int getWeek15() {
			return week15;
		}


		public void setWeek15(int week15) {
			this.week15 = week15;
		}


		public int getWeek16() {
			return week16;
		}


		public void setWeek16(int week16) {
			this.week16 = week16;
		}


		public int getWeek17() {
			return week17;
		}


		public void setWeek17(int week17) {
			this.week17 = week17;
		}


		public int getWeek18() {
			return week18;
		}


		public void setWeek18(int week18) {
			this.week18 = week18;
		}


		public int getWeek19() {
			return week19;
		}


		public void setWeek19(int week19) {
			this.week19 = week19;
		}


		public int getWeek20() {
			return week20;
		}


		public void setWeek20(int week20) {
			this.week20 = week20;
		}


		public int getWeek21() {
			return week21;
		}


		public void setWeek21(int week21) {
			this.week21 = week21;
		}


		public int getWeek22() {
			return week22;
		}


		public void setWeek22(int week22) {
			this.week22 = week22;
		}


		public int getWeek23() {
			return week23;
		}


		public void setWeek23(int week23) {
			this.week23 = week23;
		}


		public int getWeek24() {
			return week24;
		}


		public void setWeek24(int week24) {
			this.week24 = week24;
		}


		public int getWeek25() {
			return week25;
		}


		public void setWeek25(int week25) {
			this.week25 = week25;
		}


		public int getWeek26() {
			return week26;
		}


		public void setWeek26(int week26) {
			this.week26 = week26;
		}


		public int getWeek27() {
			return week27;
		}


		public void setWeek27(int week27) {
			this.week27 = week27;
		}


		public int getWeek28() {
			return week28;
		}


		public void setWeek28(int week28) {
			this.week28 = week28;
		}


		public int getWeek29() {
			return week29;
		}


		public void setWeek29(int week29) {
			this.week29 = week29;
		}


		public int getWeek30() {
			return week30;
		}


		public void setWeek30(int week30) {
			this.week30 = week30;
		}


		public int getWeek31() {
			return week31;
		}


		public void setWeek31(int week31) {
			this.week31 = week31;
		}


		public int getWeek32() {
			return week32;
		}


		public void setWeek32(int week32) {
			this.week32 = week32;
		}


		public int getWeek33() {
			return week33;
		}


		public void setWeek33(int week33) {
			this.week33 = week33;
		}


		public int getWeek34() {
			return week34;
		}


		public void setWeek34(int week34) {
			this.week34 = week34;
		}


		public int getWeek35() {
			return week35;
		}


		public void setWeek35(int week35) {
			this.week35 = week35;
		}


		public int getWeek36() {
			return week36;
		}


		public void setWeek36(int week36) {
			this.week36 = week36;
		}


		public int getWeek37() {
			return week37;
		}


		public void setWeek37(int week37) {
			this.week37 = week37;
		}


		public int getWeek38() {
			return week38;
		}


		public void setWeek38(int week38) {
			this.week38 = week38;
		}


		public int getWeek39() {
			return week39;
		}


		public void setWeek39(int week39) {
			this.week39 = week39;
		}


		public int getWeek40() {
			return week40;
		}


		public void setWeek40(int week40) {
			this.week40 = week40;
		}


		public int getWeek41() {
			return week41;
		}


		public void setWeek41(int week41) {
			this.week41 = week41;
		}


		public int getWeek42() {
			return week42;
		}


		public void setWeek42(int week42) {
			this.week42 = week42;
		}


		public int getWeek43() {
			return week43;
		}


		public void setWeek43(int week43) {
			this.week43 = week43;
		}


		public int getWeek44() {
			return week44;
		}


		public void setWeek44(int week44) {
			this.week44 = week44;
		}


		public int getWeek45() {
			return week45;
		}


		public void setWeek45(int week45) {
			this.week45 = week45;
		}


		public int getWeek46() {
			return week46;
		}


		public void setWeek46(int week46) {
			this.week46 = week46;
		}


		public int getWeek47() {
			return week47;
		}


		public void setWeek47(int week47) {
			this.week47 = week47;
		}


		public int getWeek48() {
			return week48;
		}


		public void setWeek48(int week48) {
			this.week48 = week48;
		}


		public int getWeek49() {
			return week49;
		}


		public void setWeek49(int week49) {
			this.week49 = week49;
		}


		public int getWeek50() {
			return week50;
		}


		public void setWeek50(int week50) {
			this.week50 = week50;
		}


		public int getWeek51() {
			return week51;
		}


		public void setWeek51(int week51) {
			this.week51 = week51;
		}


		public int getWeek52() {
			return week52;
		}


		public void setWeek52(int week52) {
			this.week52 = week52;
		}


		public int getWeek53() {
			return week53;
		}


		public void setWeek53(int week53) {
			this.week53 = week53;
		}


		public int getYear() {
			return year;
		}


		public void setYear(int year) {
			this.year = year;
		}



}
