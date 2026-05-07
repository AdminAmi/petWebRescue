BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "korisnik" (
	"id"	INTEGER,
	"user"	TEXT NOT NULL UNIQUE,
	"pass"	REAL NOT NULL,
	"ime"	TEXT NOT NULL,
	"prezime"	TEXT NOT NULL,
	"tip"	TEXT,
	"adresa"	TEXT,
	"telefon"	TEXT NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "ljubimac" (
	"id"	INTEGER,
	"ime"	TEXT NOT NULL,
	"vrsta"	TEXT NOT NULL CHECK("vrsta" IN ('pas', 'macka')),
	"starost"	TEXT,
	"status"	TEXT DEFAULT 'SLOBODAN',
	"datumPrijema"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "udomljavanje" (
	"idKlijenti"	INTEGER NOT NULL,
	"idLjubimac"	INTEGER NOT NULL,
	"datumUdomljavanja"	TEXT NOT NULL,
	"status"	TEXT NOT NULL,
	PRIMARY KEY("idKlijenti","idLjubimac"),
	FOREIGN KEY("idKlijenti") REFERENCES "klijent"("id"),
	FOREIGN KEY("idLjubimac") REFERENCES "ljubimac"("id")
);
INSERT INTO "korisnik" VALUES (1,'ami','ami','Amel','Dzanic','ADMINISTRATOR','Bihać','037225225');
INSERT INTO "korisnik" VALUES (2,'test','t','Test','test','ADMINISTRATOR','Bhać','223');
INSERT INTO "ljubimac" VALUES (1,'Floki','pas','2','SLOBODAN','2026-05-02');
COMMIT;
