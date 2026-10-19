/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import domain.JedinicaMere;
import domain.Materijal;
import domain.Musterija;
import domain.Narudzbenica;
import domain.StavkaNarudzbenice;
import domain.Valuta;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author HP
 */
public class DbBroker {
    
    private static final String URL = "jdbc:mysql://localhost:3306/bazaprojekatnovo";
    private static final String KORISNIK = "root";
    private static final String LOZINKA = "";
    
    private Connection connection;
    
    public void otvoriKonekciju() throws SQLException {
        connection = DriverManager.getConnection(URL, KORISNIK, LOZINKA);
        connection.setAutoCommit(false);
    }
    
    public void commit() throws SQLException{
        connection.commit();
    }
    
    public void rollback() throws SQLException{
        connection.rollback();
    }
    
    public void zatvoriKonekciju() throws SQLException{
        if(connection != null){
            connection.close();
            
        }
    }

    public Connection getConnection() {
        return connection;
    }
    
    
    public int dodajNarudzbenicu(Narudzbenica n) throws SQLException {
        String upit = "INSERT INTO narudzbenica "
                + "(UkupnaKolicina, UkupnaCenaSaPdv, UkupnaCenaBezPdv, Datum, MusterijaID) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(upit, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, n.getUkupnaKolicina());
            ps.setDouble(2, n.getUkupnaCenaSaPdv());
            ps.setDouble(3, n.getUkupnaCenaBezPdv());
            ps.setDate(4, new java.sql.Date(n.getDatum().getTime()));
            ps.setInt(5, n.getMusterija().getMusterijaId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Baza nije vratila šifru narudžbenice");
    }

    public void dodajStavku(int sifraNarudzbenice, StavkaNarudzbenice s) throws SQLException {
        String upit = "INSERT INTO stavkaNarudzbenice "
                + "(SifraNarudzbenice, RedniBr, MaterijalID, IznosSaPdv, IznosBezPdv, Popust, Valuta, Kolicina) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(upit)) {
            ps.setInt(1, sifraNarudzbenice);
            ps.setInt(2, s.getRedniBr());
            ps.setInt(3, s.getMaterijal().getMaterijalId());
            ps.setDouble(4, s.getIznosSaPdv());
            ps.setDouble(5, s.getIznosBezPdv());
            ps.setDouble(6, s.getPopust());
            ps.setString(7, s.getValuta().name());
            ps.setDouble(8, s.getKolicina());
            ps.executeUpdate();
        }
    }

    // Smanjuje stanje materijala. Vraća 0 ako nema dovoljno na stanju.
    public int smanjiStanje(int materijalId, double kolicina) throws SQLException {
        String upit = "UPDATE materijal SET Stanje = Stanje - ? "
                + "WHERE MaterijalID = ? AND Stanje >= ?";
        try (PreparedStatement ps = connection.prepareStatement(upit)) {
            ps.setDouble(1, kolicina);
            ps.setInt(2, materijalId);
            ps.setDouble(3, kolicina);
            return ps.executeUpdate();
        }
    }
    
    public List<Materijal> vratiMaterijale() throws SQLException {
        List<Materijal> lista = new ArrayList<>();
        String upit = "SELECT MaterijalID, NazivMaterijala, CenaBezPdv, CenaSaPdv, "
                + "Stanje, JedinicaMere, PdvID FROM materijal ORDER BY NazivMaterijala";
        try (Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(upit)) {
            while (rs.next()) {
                Materijal m = new Materijal();
                m.setMaterijalId(rs.getInt("MaterijalID"));
                m.setNazivMaterijala(rs.getString("NazivMaterijala"));
                m.setCenaBezPdv(rs.getDouble("CenaBezPdv"));
                m.setCenaSaPdv(rs.getDouble("CenaSaPdv"));
                m.setStanje(rs.getDouble("Stanje"));
                m.setJedinicaMere(JedinicaMere.valueOf(rs.getString("JedinicaMere")));
                m.setPdvId(rs.getInt("PdvID"));
                lista.add(m);
            }
        }
        return lista;
    }
    
    public List<Musterija> vratiMusterije() throws SQLException{
        List<Musterija> lista = new ArrayList<>();
        
        String upit = "SELECT MusterijaID, PIB, NazivFirme, VrstaMaterijala, " + 
                "PotrebnaKolicina, JedinicaMere FROM musterija ORDER BY NazivFirme";
        try(Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(upit)){
            
            while (rs.next()){
                Musterija m = new Musterija();
                m.setMusterijaId(rs.getInt("MusterijaID"));
                m.setPib(rs.getInt("PIB"));
                m.setNazivFirme(rs.getString("NazivFirme"));
                m.setVrstaMaterijala(rs.getString("VrstaMaterijala"));
                m.setPotrebnaKolicina(rs.getDouble("PotrebnaKolicina"));
                m.setJedinicaMere(JedinicaMere.valueOf(rs.getString("JedinicaMere")));
                lista.add(m);
            }
        }
        return lista;
    }
    
    
    public List<Narudzbenica> vratiNarudzbenice() throws SQLException {
        List<Narudzbenica> lista = new ArrayList<>();
        String upit = "SELECT n.SifraNarudzbenice, n.UkupnaKolicina, n.UkupnaCenaSaPdv, "
                + "n.UkupnaCenaBezPdv, n.Datum, m.MusterijaID, m.PIB, m.NazivFirme, "
                + "m.VrstaMaterijala, m.PotrebnaKolicina, m.JedinicaMere "
                + "FROM narudzbenica n JOIN musterija m ON n.MusterijaID = m.MusterijaID "
                + "ORDER BY n.SifraNarudzbenice DESC";
        try (Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(upit)) {
            while (rs.next()) {
                Musterija m = new Musterija();
                m.setMusterijaId(rs.getInt("MusterijaID"));
                m.setPib(rs.getInt("PIB"));
                m.setNazivFirme(rs.getString("NazivFirme"));
                m.setVrstaMaterijala(rs.getString("VrstaMaterijala"));
                m.setPotrebnaKolicina(rs.getDouble("PotrebnaKolicina"));
                m.setJedinicaMere(JedinicaMere.valueOf(rs.getString("JedinicaMere")));

                Narudzbenica n = new Narudzbenica();
                n.setSifraNarudzbenice(rs.getInt("SifraNarudzbenice"));
                n.setUkupnaKolicina(rs.getDouble("UkupnaKolicina"));
                n.setUkupnaCenaSaPdv(rs.getDouble("UkupnaCenaSaPdv"));
                n.setUkupnaCenaBezPdv(rs.getDouble("UkupnaCenaBezPdv"));
                n.setDatum(rs.getDate("Datum"));
                n.setMusterija(m);
                lista.add(n);
            }
        }
        return lista;
    }
    
    
    public List<StavkaNarudzbenice> vratiStavke(int sifraNarudzbenice) throws SQLException {
        List<StavkaNarudzbenice> lista = new ArrayList<>();
        String upit = "SELECT s.RedniBr, s.IznosSaPdv, s.IznosBezPdv, s.Popust, s.Valuta, s.Kolicina, "
                + "m.MaterijalID, m.NazivMaterijala, m.CenaBezPdv, m.CenaSaPdv, m.Stanje, "
                + "m.JedinicaMere, m.PdvID "
                + "FROM stavkaNarudzbenice s JOIN materijal m ON s.MaterijalID = m.MaterijalID "
                + "WHERE s.SifraNarudzbenice = ? ORDER BY s.RedniBr";
        try (PreparedStatement ps = connection.prepareStatement(upit)) {
            ps.setInt(1, sifraNarudzbenice);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Materijal m = new Materijal();
                    m.setMaterijalId(rs.getInt("MaterijalID"));
                    m.setNazivMaterijala(rs.getString("NazivMaterijala"));
                    m.setCenaBezPdv(rs.getDouble("CenaBezPdv"));
                    m.setCenaSaPdv(rs.getDouble("CenaSaPdv"));
                    m.setStanje(rs.getDouble("Stanje"));
                    m.setJedinicaMere(JedinicaMere.valueOf(rs.getString("JedinicaMere")));
                    m.setPdvId(rs.getInt("PdvID"));

                    StavkaNarudzbenice s = new StavkaNarudzbenice();
                    s.setRedniBr(rs.getInt("RedniBr"));
                    s.setMaterijal(m);
                    s.setIznosSaPdv(rs.getDouble("IznosSaPdv"));
                    s.setIznosBezPdv(rs.getDouble("IznosBezPdv"));
                    s.setPopust(rs.getDouble("Popust"));
                    s.setValuta(Valuta.valueOf(rs.getString("Valuta")));
                    s.setKolicina(rs.getDouble("Kolicina"));
                    lista.add(s);
                }
            }
        }
        return lista;
    }

    public void povecajStanje(int materijalId, double kolicina) throws SQLException {
        String upit = "UPDATE materijal SET Stanje = Stanje + ? WHERE MaterijalID = ?";
        try (PreparedStatement ps = connection.prepareStatement(upit)) {
            ps.setDouble(1, kolicina);
            ps.setInt(2, materijalId);
            ps.executeUpdate();
        }
    }

    public void obrisiStavke(int sifraNarudzbenice) throws SQLException {
        String upit = "DELETE FROM stavkaNarudzbenice WHERE SifraNarudzbenice = ?";
        try (PreparedStatement ps = connection.prepareStatement(upit)) {
            ps.setInt(1, sifraNarudzbenice);
            ps.executeUpdate();
        }
    }

    public int obrisiNarudzbenicu(int sifraNarudzbenice) throws SQLException {
        String upit = "DELETE FROM narudzbenica WHERE SifraNarudzbenice = ?";
        try (PreparedStatement ps = connection.prepareStatement(upit)) {
            ps.setInt(1, sifraNarudzbenice);
            return ps.executeUpdate();
        }
    }
    
    
    public int izmeniNarudzbenicu(Narudzbenica n) throws SQLException {
        String upit = "UPDATE narudzbenica SET UkupnaKolicina = ?, UkupnaCenaSaPdv = ?, "
                + "UkupnaCenaBezPdv = ?, Datum = ?, MusterijaID = ? "
                + "WHERE SifraNarudzbenice = ?";
        try (PreparedStatement ps = connection.prepareStatement(upit)) {
            ps.setDouble(1, n.getUkupnaKolicina());
            ps.setDouble(2, n.getUkupnaCenaSaPdv());
            ps.setDouble(3, n.getUkupnaCenaBezPdv());
            ps.setDate(4, new java.sql.Date(n.getDatum().getTime()));
            ps.setInt(5, n.getMusterija().getMusterijaId());
            ps.setInt(6, n.getSifraNarudzbenice());
            return ps.executeUpdate();
        }
    }
    
}
