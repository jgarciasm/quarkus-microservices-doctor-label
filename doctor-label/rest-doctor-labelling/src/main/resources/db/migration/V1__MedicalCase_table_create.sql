CREATE TABLE MedicalCase (
    id     SERIAL PRIMARY KEY,
    description      TEXT NOT NULL,
    doctorId     VARCHAR(100) NOT NULL,
    label       INTEGER,
    caseLabelTime          INTEGER
);