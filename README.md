# Narudžbenice (Java, klijent-server)

Desktop aplikacija za upravljanje narudžbenicama građevinskog materijala. Napravljena u Javi (NetBeans, Swing) kao klijent-server aplikacija sa MySQL bazom.

## Funkcionalnosti

- Kreiranje narudžbenice sa više stavki (izbor mušterije, materijala i količine)
- Pregled svih sačuvanih narudžbenica
- Izmena postojeće narudžbenice
- Brisanje narudžbenice
- Automatsko ažuriranje stanja materijala: upis smanjuje stanje, brisanje ga vraća, izmena ga ispravlja
- Sve izmene nad bazom izvršavaju se **u jednoj transakciji**: ili se upiše sve ili ništa (`commit` / `rollback`)

## Struktura projekta

| Projekat | Uloga |
|---|---|
| `Common` | Zajedničke klase za klijenta i server: domenske klase (`Musterija`, `Materijal`, `Narudzbenica`, `StavkaNarudzbenice`), enumi (`JedinicaMere`, `Valuta`) i klase za razmenu poruka (`Zahtev`, `Odgovor`) |
| `Server` | Prima zahteve preko soketa, obrađuje ih (`KlijentNit`, `Kontroler`) i komunicira sa bazom (`DbBroker`) |
| `Klijent` | Swing prozori (`FrmGlavna`, `FrmNarudzbenica`, `FrmPregled`) i klasa `Komunikacija` za slanje zahteva serveru |

Tok jednog zahteva: prozor → `Komunikacija` → (soket) → `KlijentNit` → `Kontroler` → `DbBroker` → baza, a odgovor ide istim putem nazad.

## Potrebno za pokretanje

- JDK 21
- Apache NetBeans (razvijano u verziji 19)
- MySQL (razvijano preko XAMPP-a), korisnik `root` bez lozinke
- MySQL Connector/J (`mysql-connector-j-8.2.0.jar`)
- SQLyog ili bilo koji drugi MySQL klijent za pravljenje baze

## Pokretanje

1. Pokreni MySQL (u XAMPP-u dugme **Start** kod MySQL-a).
2. Napravi bazu: otvori `bazaprojekatnovo.sql` u SQLyog-u, obeleži sve (`Ctrl + A`) i izvrši (`F5`).
3. Otvori u NetBeans-u sva tri projekta (`Common`, `Server`, `Klijent`).
4. Desni klik na `Common` → **Clean and Build**.
5. U projektu `Server`, pod **Libraries**, proveri da postoje `Common` i `mysql-connector-j-8.2.0.jar` (dodaj ih ako fale). U projektu `Klijent` mora da postoji `Common`.
6. Pokreni `Server` (desni klik → **Run**). U Output-u treba da piše `Server pokrenut na portu 9000...`.
7. Pokreni `Klijent` (desni klik → **Run**).

## Podešavanja

- Podaci za bazu su u klasi `DbBroker` (`URL`, `KORISNIK`, `LOZINKA`). Podrazumevano: `jdbc:mysql://localhost:3306/bazaprojekatnovo`, korisnik `root`, bez lozinke.
- Port servera je 9000. Isti broj mora da stoji u klasi `Komunikacija` u klijentu.
- Tabela `korisnik` postoji u bazi, ali prijava (login) još nije implementirana.
