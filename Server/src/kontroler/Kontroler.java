/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kontroler;

import db.DbBroker;
import domain.Materijal;
import domain.Musterija;
import domain.Narudzbenica;
import domain.StavkaNarudzbenice;
import java.sql.SQLException;
import java.util.List;

public class Kontroler {

    public void sacuvajNarudzbenicu(Narudzbenica n) throws Exception {
        DbBroker db = new DbBroker();
        try {
            db.otvoriKonekciju();

            int sifra = db.dodajNarudzbenicu(n);
            n.setSifraNarudzbenice(sifra);

            int redniBr = 1;
            for (StavkaNarudzbenice s : n.getStavke()) {
                s.setRedniBr(redniBr++);
                db.dodajStavku(sifra, s);

                int promenjeno = db.smanjiStanje(s.getMaterijal().getMaterijalId(), s.getKolicina());
                if (promenjeno == 0) {
                    throw new Exception("Nema dovoljno materijala: " + s.getMaterijal().getNazivMaterijala());
                }
            }

            db.commit();
        } catch (Exception e) {
            db.rollback();
            throw e;
        } finally {
            db.zatvoriKonekciju();
        }
    }
    
    public List<Materijal> vratiMaterijale () throws SQLException{
        DbBroker db = new DbBroker();
        try {
            db.otvoriKonekciju();
            return db.vratiMaterijale();
        } finally {
            db.zatvoriKonekciju();
        }
    }
    
    public List<Musterija> vratiMusterije() throws SQLException{
        DbBroker db = new DbBroker();
        
        try {
            db.otvoriKonekciju();
            return db.vratiMusterije();
        } finally {
            db.zatvoriKonekciju();
        }
        
    }
    
    
    public List<Narudzbenica> vratiNarudzbenice() throws Exception {
        DbBroker db = new DbBroker();
        try {
            db.otvoriKonekciju();
            return db.vratiNarudzbenice();
        } finally {
            db.zatvoriKonekciju();
        }
    }
    
    
    public void obrisiNarudzbenicu(int sifra) throws Exception {
        DbBroker db = new DbBroker();
        try {
            db.otvoriKonekciju();

            List<StavkaNarudzbenice> stavke = db.vratiStavke(sifra);
            for (StavkaNarudzbenice s : stavke) {
                db.povecajStanje(s.getMaterijal().getMaterijalId(), s.getKolicina());
            }

            db.obrisiStavke(sifra);
            int obrisano = db.obrisiNarudzbenicu(sifra);
            if (obrisano == 0) {
                throw new Exception("Narudžbenica ne postoji (možda je već obrisana).");
            }

            db.commit();
        } catch (Exception e) {
            db.rollback();
            throw e;
        } finally {
            db.zatvoriKonekciju();
        }
    }
    
    
    public List<StavkaNarudzbenice> vratiStavke(int sifra) throws Exception {
        DbBroker db = new DbBroker();
        try {
            db.otvoriKonekciju();
            return db.vratiStavke(sifra);
        } finally {
            db.zatvoriKonekciju();
        }
    }

    public void izmeniNarudzbenicu(Narudzbenica n) throws Exception {
        DbBroker db = new DbBroker();
        try {
            db.otvoriKonekciju();
            int sifra = n.getSifraNarudzbenice();

            List<StavkaNarudzbenice> stare = db.vratiStavke(sifra);
            for (StavkaNarudzbenice s : stare) {
                db.povecajStanje(s.getMaterijal().getMaterijalId(), s.getKolicina());
            }
            db.obrisiStavke(sifra);

            if (db.izmeniNarudzbenicu(n) == 0) {
                throw new Exception("Narudžbenica ne postoji (možda je obrisana).");
            }

            int redniBr = 1;
            for (StavkaNarudzbenice s : n.getStavke()) {
                s.setRedniBr(redniBr++);
                db.dodajStavku(sifra, s);

                int promenjeno = db.smanjiStanje(s.getMaterijal().getMaterijalId(), s.getKolicina());
                if (promenjeno == 0) {
                    throw new Exception("Nema dovoljno materijala: " + s.getMaterijal().getNazivMaterijala());
                }
            }

            db.commit();
        } catch (Exception e) {
            db.rollback();
            throw e;
        } finally {
            db.zatvoriKonekciju();
        }
    }
}
