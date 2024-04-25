-- Liquibase ChangeLog File
-- Change Set 1: Create Tables
-- Filename: 001_initial_setup.sql
-- Author: Darmokhval
-- ID: 1

-- Create 'brand' table
CREATE TABLE brand (
    id BIGSERIAL PRIMARY KEY,
    brand_name VARCHAR(255) UNIQUE NOT NULL
);

-- Create 'model' table
CREATE TABLE model (
    id BIGSERIAL PRIMARY KEY,
    model_name VARCHAR(255) UNIQUE NOT NULL
);

-- Create 'owner' table
CREATE TABLE owner (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    lastname VARCHAR(255) NOT NULL
);
CREATE INDEX idx_owner_email ON owner(email);

-- Create 'car' table
CREATE TABLE car (
     id BIGSERIAL PRIMARY KEY,
     brand_id BIGINT,
     model_id BIGINT,
     owner_id BIGINT,
     year_of_release INT,
     mileage INT,
     was_in_accident BOOLEAN,
     FOREIGN KEY (brand_id) REFERENCES brand(id),
     FOREIGN KEY (model_id) REFERENCES model(id),
     FOREIGN KEY (owner_id) REFERENCES owner(id) ON DELETE CASCADE
);

CREATE INDEX idx_car_brand_id ON car(brand_id);
CREATE INDEX idx_car_model_id ON car(model_id);
CREATE INDEX idx_car_owner_id ON car(owner_id);

-- Create 'accessory' table
CREATE TABLE accessory (
    id BIGSERIAL PRIMARY KEY,
    accessory_name VARCHAR(255) NOT NULL
);

--create 'car_accessory" join table
CREATE TABLE car_accessory (
    accessory_id BIGINT,
    car_id BIGINT,
    PRIMARY KEY (accessory_id, car_id),
    FOREIGN KEY (accessory_id) REFERENCES accessory(id),
    FOREIGN KEY (car_id) REFERENCES car(id) ON DELETE CASCADE
);

-- Insert data into 'brand'
INSERT INTO brand (brand_name)
VALUES
    ('TOYOTA'),
    ('HONDA'),
    ('FORD'),
    ('BMW'),
    ('MERCEDES_BENZ'),
    ('AUDI'),
    ('VOLKSWAGEN'),
    ('NISSAN'),
    ('CHEVROLET'),
    ('HYUNDAI'),
    ('KIA'),
    ('SUBARU'),
    ('MAZDA'),
    ('LEXUS'),
    ('VOLVO'),
    ('JEEP'),
    ('RAM'),
    ('GMC'),
    ('CADILLAC'),
    ('BUICK'),
    ('TESLA'),
    ('LAND_ROVER'),
    ('PORSCHE'),
    ('JAGUAR'),
    ('MITSUBISHI'),
    ('INFINITI'),
    ('ACURA'),
    ('LINCOLN'),
    ('CHRYSLER'),
    ('DODGE');

-- Insert data into 'model'
INSERT INTO model (model_name)
VALUES
    ('CAMRY'),
    ('COROLLA'),
    ('CIVIC'),
    ('FOCUS'),
    ('E_CLASS'),
    ('A4'),
    ('GOLF'),
    ('ALTIMA'),
    ('CRUZE'),
    ('ACCORD'),
    ('SORENTO'),
    ('FORESTER'),
    ('CX_5'),
    ('RAV4'),
    ('ESCAPE'),
    ('WRANGLER'),
    ('SILVERADO'),
    ('TACOMA'),
    ('SANTA_FE'),
    ('OUTBACK'),
    ('PRIUS'),
    ('F_150'),
    ('CHEROKEE'),
    ('MODEL_3'),
    ('RX_350'),
    ('OPTIMA');

-- Insert data into 'owner'
INSERT INTO owner (name, lastname, email) VALUES
   ('John', 'Doe', 'john.doe@example.com'),
   ('Jane', 'Smith', 'jane.smith@example.com'),
   ('Mike', 'Johnson', 'mike.johnson@example.com');

INSERT INTO car (brand_id, model_id, owner_id, year_of_release, mileage, was_in_accident)
VALUES (1, 1, 1, 2020, 15000, false),
       (1, 2, 2, 2019, 25000, false),
       (2, 3, 3, 2018, 35000, true);

-- Insert data into 'accessory'
INSERT INTO accessory (accessory_name)
VALUES
    ('WINDOW_TINTING'),
    ('FLOOR_MATS'),
    ('SEAT_COVERS'),
    ('DASH_CAM'),
    ('STEERING_WHEEL_COVER'),
    ('USB_CAR_CHARGER'),
    ('EMERGENCY_KIT'),
    ('BACKUP_CAMERAS'),
    ('AIR_COMPRESSOR'),
    ('GPS_NAVIGATOR'),
    ('CAR_ALARM'),
    ('REAR_SEAT_ENTERTAINING_SYSTEM');

INSERT INTO car_accessory (accessory_id, car_id)
VALUES
    (6, 1),
    (7, 1),
    (9, 2),
    (10, 3),
    (8, 3);