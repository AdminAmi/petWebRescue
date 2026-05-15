BEGIN TRANSACTION;

CREATE TABLE IF NOT EXISTS "ljubimac" (
	"id"	INTEGER PRIMARY KEY AUTOINCREMENT,
	"ime"	TEXT NOT NULL,
	"vrsta"	TEXT NOT NULL CHECK("vrsta" IN ('pas','macka')),
	"starost"	TEXT,
	"status"	TEXT DEFAULT 'SLOBODAN',
	"datumPrijema"	TEXT
);
CREATE TABLE IF NOT EXISTS "korisnik" (
	"id"	INTEGER PRIMARY KEY AUTOINCREMENT,
	"user"	TEXT NOT NULL UNIQUE,
	"pass"	REAL NOT NULL,
	"ime"	TEXT NOT NULL,
	"prezime"	TEXT NOT NULL,
	"tip"	TEXT,
	"adresa"	TEXT,
	"telefon"	TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS "udomljavanje" (
	"idKlijenti"	INTEGER NOT NULL,
	"idLjubimac"	INTEGER NOT NULL,
	"datumUdomljavanja"	TEXT NOT NULL,
	"status"	TEXT NOT NULL,
        PRIMARY KEY("idKlijenti","idLjubimac","datumUdomljavanja"),
	FOREIGN KEY("idLjubimac") REFERENCES "ljubimac"("id"),	
	FOREIGN KEY("idKlijenti") REFERENCES "klijent"("id")
);

INSERT INTO "ljubimac" ("id","ime","vrsta","starost","status","datumPrijema") VALUES (1,'Floki','pas','2','SLOBODAN','2026-05-02');
INSERT INTO "korisnik" ("id","user","pass","ime","prezime","tip","adresa","telefon") VALUES (1,'ami','ami','Amel','Dzanic','ADMINISTRATOR','Bihać','037225225');
INSERT INTO "korisnik" ("id","user","pass","ime","prezime","tip","adresa","telefon") VALUES (2,'test','t','Test','test','ADMINISTRATOR','Bhać','223');
COMMIT;
