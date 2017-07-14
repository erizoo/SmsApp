package erizo.by.smsapp.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("age")
    @Expose
    private int age;
    @SerializedName("sex")
    @Expose
    private String sex;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("income")
    @Expose
    private int income;
    @SerializedName("createdTimestamp")
    @Expose
    private String createdTimestamp;
    @SerializedName("modifiedTimestamp")
    @Expose
    private String modifiedTimestamp;

    public User() {
    }

    public User(int id, String lastName, String firstName, int age, String sex, String city, int income, String createdTimestamp, String modifiedTimestamp) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
        this.sex = sex;
        this.city = city;
        this.income = income;
        this.createdTimestamp = createdTimestamp;
        this.modifiedTimestamp = modifiedTimestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public String getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(String createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    public void setModifiedTimestamp(String modifiedTimestamp) {
        this.modifiedTimestamp = modifiedTimestamp;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                ", city='" + city + '\'' +
                ", income=" + income +
                ", createdTimestamp='" + createdTimestamp + '\'' +
                ", modifiedTimestamp='" + modifiedTimestamp + '\'' +
                '}';
    }
}
