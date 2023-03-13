-- Creating data tables
-------------------------------
CREATE TABLE Adresas (
    adreso_id serial,
    miestas varchar ( 50 ) NOT NULL,
    gatve varchar ( 50 ) NOT NULL,
    namo_numeris varchar ( 10 ) NOT NULL,
    pasto_kodas varchar ( 16 ) NOT NULL,
    PRIMARY KEY (adreso_id)
);

CREATE TABLE Klientas (
    kliento_id serial,
    vardas varchar ( 20 ) NOT NULL,
    pavarde varchar ( 20 ) NOT NULL,
    el_pastas varchar ( 255 ) NOT NULL CHECK (el_pastas LIKE '%@%'),
    adreso_id serial NOT NULL,
    PRIMARY KEY (kliento_id)
);

CREATE TABLE Uzsakymas (
    nr serial,
    sukurimo_data TIMESTAMP NOT NULL DEFAULT NOW(),
    apmokejimas BOOLEAN NOT NULL DEFAULT FALSE,
    kliento_id serial NOT NULL,
    PRIMARY KEY (nr)
);

CREATE TABLE Uzsakymo_elementas (
    id serial,
    uzsakymo_nr serial NOT NULL,
    prekes_kodas int NOT NULL,
    kiekis int NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
);

CREATE TABLE Preke (
    kodas int,
    pavadinimas char ( 50 ),
    tipas char ( 15 ) Check (tipas IN ('vasarine', 'ziemine', 'universali')),
    kaina float NOT NULL CHECK(kaina > 0),
    aukstis int NOT NULL,
    plotis int NOT NULL,
    skersmuo int NOT NULL,
    PRIMARY KEY (kodas),
    likutis int NOT NULL
);

-- Adding foreing keys
------------------------------------------- 
ALTER TABLE Klientas
ADD CONSTRAINT adreso_fk
FOREIGN KEY (adreso_id)
REFERENCES Adresas (adreso_id)
ON DELETE SET NULL;

ALTER TABLE Uzsakymas
ADD CONSTRAINT kliento_fk
FOREIGN KEY (kliento_id)
REFERENCES Klientas (kliento_id)
ON DELETE CASCADE;

ALTER TABLE Uzsakymo_elementas
ADD CONSTRAINT prekes_fk
FOREIGN KEY (prekes_kodas)
REFERENCES Preke (kodas)
ON DELETE CASCADE;

ALTER TABLE Uzsakymo_elementas
ADD CONSTRAINT uzsakymo_fk
FOREIGN KEY (uzsakymo_nr)
REFERENCES Uzsakymas(nr)
ON DELETE CASCADE;

--Creating index
----------------------------------------

CREATE UNIQUE INDEX el_pastas_unique ON Klientas(el_pastas);
CREATE INDEX telefono_nr_index ON Preke(pavadinimas);

--Inserting data
----------------------------------------------------

INSERT INTO Adresas (miestas, gatve, namo_numeris, pasto_kodas)
VALUES ('Siauliai', 'Tilzes', '12', 'LT-12345'),
       ('Telšiai', 'Gedimino', '2', 'LT-29238'),
       ('Alytus', 'Jadvygos', '23', 'LT-12345'),
       ('Vilnius', 'Liepų', '52', 'LT-943827');

INSERT INTO Klientas (vardas, pavarde, el_pastas, adreso_id)
VALUES ('Domas', 'Nemanius', 'Domas@gmail.com', 1),
       ('Petras', 'Gaivelis', 'Petras.Gaiv@gmail.com', 2), 
       ('Jonas', 'Audrelis', 'Audrelis.Jonas@gmail.com', 3), 
       ('Stasė', 'Aldonienė', 'Stasė.12@gmail.com', 4);  


INSERT INTO Uzsakymas (kliento_id)
VALUES (1),
       (2),
       (3),
       (4);

INSERT INTO Preke (kodas, pavadinimas, tipas, kaina, aukstis, plotis, skersmuo, likutis)
VALUES (123, 'Michelin winter', 'ziemine', 50.00, 45, 225, 16, 10),
       (124, 'Arivo summer', 'vasarine', 30.00, 45, 225, 17, 5),
       (125, 'Autogrip', 'universali', 70.00, 45, 225, 18, 12),
       (126, 'Barkley winter', 'ziemine', 50.00, 45, 225, 16, 3);

INSERT INTO Uzsakymo_elementas(uzsakymo_nr, prekes_kodas, kiekis)
VALUES (1, 123, 4),
       (2, 124, 2),
       (3, 125, 4),
       (4, 126, 2);

--Creating virtual tables
-----------------------------------------------

CREATE VIEW ziemines_padangos
            (pavadinimas, aukstis, plotis, skersmuo, kaina)
AS SELECT pavadinimas, aukstis, plotis, skersmuo, kaina
    FROM Preke
    WHERE tipas = 'ziemine';

CREATE VIEW vasarines_padangos
            (pavadinimas, aukstis, plotis, skersmuo, kaina)
AS SELECT pavadinimas, aukstis, plotis, skersmuo, kaina
    FROM Preke
    WHERE tipas = 'vasarine';

CREATE VIEW universalios_padangos
            (pavadinimas, aukstis, plotis, skersmuo, kaina)
AS SELECT pavadinimas, aukstis, plotis, skersmuo, kaina
    FROM Preke
    WHERE tipas = 'universali';

CREATE VIEW Elementu_kiekis
AS SELECT Uzsakymo_nr, COUNT(*) AS "Elementu kiekis"
FROM Uzsakymo_elementas
GROUP BY Uzsakymo_nr;

CREATE VIEW Krepseliu_suma
AS SELECT Uzsakymo_nr, SUM(Uzsakymo_elementas.kiekis*Preke.kaina) AS "Kaina"
    FROM Uzsakymo_elementas, Preke 
    WHERE Uzsakymo_elementas.prekes_kodas = Preke.kodas
    GROUP BY Uzsakymo_elementas.uzsakymo_nr;
-- REFRESH MATERIALIZED VIEW Krepseliu_suma;

--materialized virtual view
----------------------------------------------------------
--CREATE MATERIALIZED VIEW Krepseliu_suma

CREATE MATERIALIZED VIEW uzbaigti_uzsakymai
AS SELECT nr, sukurimo_data, apmokejimas, kliento_id
FROM Uzsakymas
WHERE Uzsakymas.apmokejimas = TRUE;
REFRESH MATERIALIZED VIEW uzbaigti_uzsakymai;

-- CREATE MATERIALIZED VIEW Krepseliu_suma
--     (Uzsakymas, suma)
-- AS SELECT Uzsakymo_nr, SUM(Uzsakymo_elementas.kiekis*Preke.kaina)
--     FROM Uzsakymo_elementas
--     JOIN Preke ON Uzsakymo_elementas.prekes_kodas = Preke.kodas
--     --WHERE Uzsakymo_elementas.uzsakymo_nr = @Uzsakymo_nr
--     GROUP BY Uzsakymo_elementas.uzsakymo_nr;
    
--creating triggers
----------------------------------------------------------

-- CREATE FUNCTION Prekes_Likutis()
-- RETURNS TRIGGER AS $$
-- BEGIN IF ((
--     SELECT COUNT(*) FROM Uzsakymo_elementas, Preke
--     WHERE (Preke.likutis - Uzsakymo_elementas.kiekis) < 0 )) > 0
-- THEN 
--     RAISE EXCEPTION 'Nepakankamas prekės likutis';
-- END IF;
-- RETURN NEW;
-- END; $$
-- LANGUAGE plpgsql;

CREATE FUNCTION Prekes_Likutis()
RETURNS TRIGGER AS $$
BEGIN IF ((
    SELECT COUNT(*) FROM Uzsakymo_elementas, Preke
    WHERE (Preke.kodas = NEW.prekes_kodas AND (Preke.likutis - NEW.kiekis) < 0 ))) > 0
THEN 
    RAISE EXCEPTION 'Nepakankamas prekės likutis';
END IF;
RETURN NEW;
END; $$
LANGUAGE plpgsql;

CREATE TRIGGER Prekes_Likutis_TG 
BEFORE INSERT ON Uzsakymo_elementas
FOR EACH ROW
EXECUTE PROCEDURE Prekes_Likutis();

INSERT INTO Uzsakymo_elementas(uzsakymo_nr, prekes_kodas, kiekis)
VALUES (4, 123, 15);
      
CREATE FUNCTION Klientas_Uzsiregistraves()
RETURNS TRIGGER AS $$
BEGIN IF ((
    SELECT COUNT(*) FROM Klientas
    WHERE Klientas.vardas = NEW.Vardas AND Klientas.pavarde = NEW.pavarde)) > 0
    THEN 
        RAISE EXCEPTION 'Klientas jau yra sistemoje užregistruotas';
END IF; 
RETURN NEW; 
END; $$
LANGUAGE plpgsql;

CREATE TRIGGER Klientas_Uzregistruotas_TG
BEFORE INSERT ON Klientas
FOR EACH ROW 
EXECUTE PROCEDURE Klientas_Uzsiregistraves();

-- INSERT INTO Klientas (vardas, pavarde, el_pastas, adreso_id)
-- VALUES ('Domas', 'Nemanius', 'Domas@gmail.com', 1);

CREATE FUNCTION Yra_Neapmoketas_Uzsakymas()
RETURNS TRIGGER AS $$
BEGIN IF (
    SELECT COUNT(*) FROM Uzsakymas
    WHERE Uzsakymas.kliento_id = NEW.kliento_id AND Uzsakymas.apmokejimas = FALSE) > 0
    THEN 
        RAISE EXCEPTION 'Yra neapmoketu uzsakymu, naujo kurti negalima';
END IF; 
RETURN NEW; 
END; $$
LANGUAGE plpgsql;

CREATE TRIGGER Klientas_Turi_Nesumoketi_Uzsakymu_TG
BEFORE INSERT ON Uzsakymas
FOR EACH ROW 
EXECUTE PROCEDURE Yra_Neapmoketas_Uzsakymas();

-- INSERT INTO Uzsakymas (kliento_id)
-- VALUES (1);

--selecting all tables
----------------------------------------------------------
-- SELECT * FROM Adresas;
-- SELECT * FROM Klientas;
-- SELECT * FROM Uzsakymas;
-- SELECT * FROM Uzsakymo_elementas;
-- SELECT * FROM Preke;

-- SELECT * FROM Krepseliu_suma;


-- deleting tables
--------------------------------------------------
-- DROP VIEW ziemines_padangos;
-- DROP VIEW vasarines_padangos;
-- DROP VIEW universalios_padangos;

-- DROP TABLE Adresas CASCADE;
-- DROP TABLE Klientas CASCADE;
-- DROP TABLE Uzsakymas CASCADE;
-- DROP TABLE Uzsakymo_elementas CASCADE;
-- DROP TABLE Preke CASCADE;

-- DROP FUNCTION Prekes_Likutis;
-- DROP FUNCTION Klientas_Uzsiregistraves;
-- DROP FUNCTION Yra_Neapmoketas_Uzsakymas;
--scp C:\Users\Domas\Desktop\DBVS_2lab\2_dalis\db.sql done8342@uosis.mif.vu.lt:~/Desktop/DBVS
--psql -h pgsql3.mif -d studentu -f db.sql