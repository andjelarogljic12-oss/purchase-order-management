# Orders (Java, client-server)

Desktop application for managing orders of construction materials. Built in Java (NetBeans, Swing) as a client-server application with a MySQL database.

## Features

- Create an order with multiple items (choose a customer, materials and quantities)
- View all saved orders
- Edit an existing order
- Delete an order
- Material stock is updated automatically: saving an order decreases it, deleting restores it, editing corrects it
- Every change to the database runs in **a single transaction**: either everything is saved or nothing is (`commit` / `rollback`)

## Project structure

| Project | Role |
|---|---|
| `Common` | Classes shared by client and server: domain classes (`Musterija`, `Materijal`, `Narudzbenica`, `StavkaNarudzbenice`), enums (`JedinicaMere`, `Valuta`) and message classes (`Zahtev`, `Odgovor`) |
| `Server` | Receives requests over a socket, handles them (`KlijentNit`, `Kontroler`) and talks to the database (`DbBroker`) |
| `Klijent` | Swing windows (`FrmGlavna`, `FrmNarudzbenica`, `FrmPregled`) and the `Komunikacija` class that sends requests to the server |

Flow of a single request: window → `Komunikacija` → (socket) → `KlijentNit` → `Kontroler` → `DbBroker` → database, and the response travels back the same way.

## Requirements

- JDK 21
- Apache NetBeans (developed with version 19)
- MySQL (developed with XAMPP), user `root` with no password
- MySQL Connector/J (`mysql-connector-j-8.2.0.jar`)
- SQLyog or any other MySQL client for creating the database

## Running the project

1. Start MySQL (in XAMPP, click **Start** next to MySQL).
2. Create the database: open `bazaprojekatnovo.sql` in SQLyog, select all (`Ctrl + A`) and execute (`F5`).
3. Open all three projects (`Common`, `Server`, `Klijent`) in NetBeans.
4. Right-click `Common` → **Clean and Build**.
5. In the `Server` project, under **Libraries**, check that `Common` and `mysql-connector-j-8.2.0.jar` are present (add them if missing). In the `Klijent` project, `Common` must be present.
6. Run `Server` (right-click → **Run**). The Output window should show `Server pokrenut na portu 9000...` (server started on port 9000).
7. Run `Klijent` (right-click → **Run**).

## Configuration

- Database settings are in the `DbBroker` class (`URL`, `KORISNIK`, `LOZINKA`). Defaults: `jdbc:mysql://localhost:3306/bazaprojekatnovo`, user `root`, empty password.
- The server port is 9000. The same number must be set in the `Komunikacija` class in the client.
- The `korisnik` table exists in the database, but login is not implemented yet.
