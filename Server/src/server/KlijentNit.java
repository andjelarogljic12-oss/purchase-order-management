/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import domain.Narudzbenica;
import domain.Odgovor;
import domain.Zahtev;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import kontroler.Kontroler;

/**
 *
 * @author HP
 */
public class KlijentNit extends Thread {

    private final Socket socket;
    private final Kontroler kontroler = new Kontroler();

    public KlijentNit(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Zahtev zahtev = (Zahtev) in.readObject();
                Odgovor odgovor = obradi(zahtev);
                out.writeObject(odgovor);
                out.flush();
            }

        } catch (Exception e) {
            System.out.println("Klijent se odjavio");
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
            }

        }

    }

    private Odgovor obradi(Zahtev zahtev) {
        try {

            switch (zahtev.getOperacija()) {
                case "SACUVAJ_NARUDZBENICU":
                    kontroler.sacuvajNarudzbenicu((Narudzbenica) zahtev.getPodaci());
                    return new Odgovor(true, "Narudzbenica je sacuvana", null);
                case "VRATI_MATERIJALE":
                    return new Odgovor(true, "OK", new ArrayList<>(kontroler.vratiMaterijale()));
                case "VRATI_MUSTERIJE":
                    return new Odgovor(true, "OK", new ArrayList<>(kontroler.vratiMusterije()));
                case "VRATI_NARUDZBENICE":
                    return new Odgovor(true, "OK", new ArrayList<>(kontroler.vratiNarudzbenice()));
                case "OBRISI_NARUDZBENICU":
                    kontroler.obrisiNarudzbenicu((Integer) zahtev.getPodaci());
                    return new Odgovor(true, "Narudžbenica je obrisana", null);
                case "VRATI_STAVKE":
                    return new Odgovor(true, "OK", new ArrayList<>(kontroler.vratiStavke((Integer) zahtev.getPodaci())));
                case "IZMENI_NARUDZBENICU":
                    kontroler.izmeniNarudzbenicu((Narudzbenica) zahtev.getPodaci());
                    return new Odgovor(true, "Narudžbenica je izmenjena", null);
                default:
                    return new Odgovor(false, "Nepoznata operacija: " + zahtev.getOperacija(), null);

            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Odgovor(false, e.getMessage(), null);
        }
    }

}
