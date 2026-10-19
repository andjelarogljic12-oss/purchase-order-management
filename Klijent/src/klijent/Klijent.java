/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package klijent;

import domain.Materijal;
import domain.Musterija;
import domain.Narudzbenica;
import domain.Odgovor;
import domain.StavkaNarudzbenice;
import domain.Valuta;
import domain.Zahtev;
import java.util.Date;
import java.util.List;

/**
 *
 * @author HP
 */
public class Klijent {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Komunikacija k = new Komunikacija();
            k.poveziSe();
            java.awt.EventQueue.invokeLater(() -> new FrmGlavna(k).setVisible(true));
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Ne mogu da se povežem sa serverom: " + e.getMessage());
        }
    }

}
