package hu.petrik.etlap;

public class Etel {
    private int id;
    private String nev;
    private String leiras;
    private int ar;
    private String kategoria;

    public Etel(int id, String nev, String leiras, int ar, String kategoria) {
        this.id = id;
        this.nev = nev;
        this.leiras = leiras;
        this.ar = ar;
        this.kategoria = kategoria;
    }
    public Etel(String nev, String leiras, int ar, String kategoria) {
        this.id = 0;
        this.nev = nev;
        this.leiras = leiras;
        this.ar = ar;
        this.kategoria = kategoria;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNev() {
        return nev;
    }
    public void setNev(String nev) {
        this.nev = nev;
    }
    public String getLeiras() {
        return leiras;
    }
    public void setLeiras(String leiras) {
        this.leiras = leiras;
    }
    public int getAr() {
        return ar;
    }
    public void setAr(int ar) {
        this.ar = ar;
    }
    public String getKategoria() {
        return kategoria;
    }
    public void setKategoria(String kategoria) {
        this.kategoria = kategoria;
    }
    @Override
    public String toString() {
        return "Etel{" +
                "id=" + id +
                ", nev='" + nev + '\'' +
                ", leiras='" + leiras + '\'' +
                ", ar=" + ar +
                ", kategoria='" + kategoria + '\'' +
                '}';
    }
}
