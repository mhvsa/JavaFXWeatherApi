package zad1.nbp.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"nazwa_waluty", "przelicznik", "kod_waluty", "kurs_sredni"})
public class Pozycja {
    
    private String nazwa_waluty;
    private String przelicznik;
    private String kod_waluty;
    private String kurs_sredni;

    public String getNazwa_waluty() {
        return nazwa_waluty;
    }

    public void setNazwa_waluty(String nazwa_waluty) {
        this.nazwa_waluty = nazwa_waluty;
    }

    public String getPrzelicznik() {
        return przelicznik;
    }

    public void setPrzelicznik(String przelicznik) {
        this.przelicznik = przelicznik;
    }

    public String getKod_waluty() {
        return kod_waluty;
    }

    public void setKod_waluty(String kod_waluty) {
        this.kod_waluty = kod_waluty;
    }

    public String getKurs_sredni() {
        return kurs_sredni;
    }

    public void setKurs_sredni(String kurs_sredni) {
        this.kurs_sredni = kurs_sredni;
    }
}
