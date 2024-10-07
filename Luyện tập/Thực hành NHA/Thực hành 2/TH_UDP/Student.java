/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UDP.Total;

/**
 *
 * @author MEDIAMART PHU SON
 */
public class Student {
    private int idSV;
    private String maSV;
    private String hoTen;
    private int namSinh;
    private String queQuan;
    private float gPA;

    // Constructor
    public Student(int idSV, String maSV, String hoTen, int namSinh, String queQuan, float gPA) {
        this.idSV = idSV;
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.namSinh = namSinh;
        this.queQuan = queQuan;
        this.gPA = gPA;
    }

    // Getters and setters
    public int getIdSV() { return idSV; }
    public void setIdSV(int idSV) { this.idSV = idSV; }
    public String getMaSV() { return maSV; }
    public void setMaSV(String maSV) { this.maSV = maSV; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public int getNamSinh() { return namSinh; }
    public void setNamSinh(int namSinh) { this.namSinh = namSinh; }
    public String getQueQuan() { return queQuan; }
    public void setQueQuan(String queQuan) { this.queQuan = queQuan; }
    public float getGPA() { return gPA; }
    public void setGPA(float gPA) { this.gPA = gPA; }

    @Override
    public String toString() {
        return "Student{" +
                "idSV=" + idSV +
                ", maSV='" + maSV + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", namSinh=" + namSinh +
                ", queQuan='" + queQuan + '\'' +
                ", gPA=" + gPA +
                '}';
    }
}