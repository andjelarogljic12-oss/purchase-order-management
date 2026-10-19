-- Baza za projekat "Narudzbenice" (Java, klijent-server)
-- Pokretanje: otvoriti u SQLyog-u (ili MySQL klijentu), obeleziti sve (Ctrl+A) i izvrsiti (F5).
-- Skripta je ponovljiva: svaki put brise postojece tabele i pravi ih iznova sa pocetnim podacima.

CREATE DATABASE IF NOT EXISTS bazaprojekatnovo CHARACTER SET utf8mb4;
USE bazaprojekatnovo;

-- Brisanje postojecih tabela (prvo one koje zavise od drugih)
DROP TABLE IF EXISTS stavkaNarudzbenice;
DROP TABLE IF EXISTS narudzbenica;
DROP TABLE IF EXISTS materijal;
DROP TABLE IF EXISTS pdvStopa;
DROP TABLE IF EXISTS musterija;
DROP TABLE IF EXISTS korisnik;

-- Tabele
CREATE TABLE korisnik (
  korisnikId BIGINT AUTO_INCREMENT PRIMARY KEY,
  korisnickoIme VARCHAR(50) NOT NULL UNIQUE,
  lozinka VARCHAR(50) NOT NULL
);

CREATE TABLE pdvStopa (
  PdvID INT AUTO_INCREMENT PRIMARY KEY,
  OsnovZaPrimenu VARCHAR(100) NOT NULL,
  Iznos DOUBLE NOT NULL
);

CREATE TABLE musterija (
  MusterijaID INT AUTO_INCREMENT PRIMARY KEY,
  PIB INT NOT NULL UNIQUE,
  NazivFirme VARCHAR(100) NOT NULL,
  VrstaMaterijala VARCHAR(100) NOT NULL,
  PotrebnaKolicina DOUBLE NOT NULL,
  JedinicaMere VARCHAR(5) NOT NULL          -- KG, L, M
);

CREATE TABLE materijal (
  MaterijalID INT AUTO_INCREMENT PRIMARY KEY,
  NazivMaterijala VARCHAR(100) NOT NULL,
  CenaBezPdv DOUBLE NOT NULL,
  CenaSaPdv DOUBLE NOT NULL,
  Stanje DOUBLE NOT NULL,
  JedinicaMere VARCHAR(5) NOT NULL,         -- KG, L, M
  PdvID INT NOT NULL,
  FOREIGN KEY (PdvID) REFERENCES pdvStopa(PdvID)
);

CREATE TABLE narudzbenica (
  SifraNarudzbenice INT AUTO_INCREMENT PRIMARY KEY,
  UkupnaKolicina DOUBLE NOT NULL,
  UkupnaCenaSaPdv DOUBLE NOT NULL,
  UkupnaCenaBezPdv DOUBLE NOT NULL,
  Datum DATE NOT NULL,
  MusterijaID INT NOT NULL,
  FOREIGN KEY (MusterijaID) REFERENCES musterija(MusterijaID)
);

CREATE TABLE stavkaNarudzbenice (
  SifraNarudzbenice INT NOT NULL,
  RedniBr INT NOT NULL,
  MaterijalID INT NOT NULL,
  IznosSaPdv DOUBLE NOT NULL,
  IznosBezPdv DOUBLE NOT NULL,
  Popust DOUBLE NOT NULL,
  Valuta VARCHAR(5) NOT NULL,               -- EUR, DIN
  Kolicina DOUBLE NOT NULL,
  PRIMARY KEY (SifraNarudzbenice, RedniBr),
  FOREIGN KEY (SifraNarudzbenice) REFERENCES narudzbenica(SifraNarudzbenice),
  FOREIGN KEY (MaterijalID) REFERENCES materijal(MaterijalID)
);

-- Pocetni podaci
INSERT INTO korisnik (korisnickoIme, lozinka) VALUES ('admin', 'admin');

INSERT INTO pdvStopa (OsnovZaPrimenu, Iznos) VALUES
('Opšta stopa', 20),
('Posebna stopa', 10);

INSERT INTO materijal (NazivMaterijala, CenaBezPdv, CenaSaPdv, Stanje, JedinicaMere, PdvID) VALUES
('Cement',            15,   18,   5000,  'KG', 1),
('Bela boja',         450,  540,  400,   'L',  1),
('Pesak',             2,    2.4,  20000, 'KG', 1),
('Krečni malter',     8,    9.6,  3000,  'KG', 1),
('Lak za drvo',       700,  840,  150,   'L',  1),
('Bakarna cev',       600,  720,  300,   'M',  1),
('Električni kabl',   120,  144,  1500,  'M',  1);

INSERT INTO musterija (PIB, NazivFirme, VrstaMaterijala, PotrebnaKolicina, JedinicaMere) VALUES
(100000001, 'Gradnja Beograd doo', 'Cement',          2000, 'KG'),
(100000002, 'Moler Plus doo',      'Boja',            150,  'L'),
(100000003, 'Instalater doo',      'Električni kabl', 500,  'M');
