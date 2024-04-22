-- Liquibase ChangeLog File
-- Change Set 1: Create Tables
-- Filename: 001_initial_setup.sql
-- Author: Darmokhval
-- ID: 1

-- Create 'brand' table
CREATE TABLE brand (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       brand_name VARCHAR(255) NOT NULL
);

-- Create 'model' table
CREATE TABLE model (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       model_name VARCHAR(255) NOT NULL
);

-- Create 'owner' table
CREATE TABLE owner (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       lastname VARCHAR(255) NOT NULL
);

-- Create 'car' table
CREATE TABLE car (
                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     brand_id BIGINT,
                     model_id BIGINT,
                     owner_id BIGINT,
                     year_of_release INT,
                     mileage INT,
                     was_in_accident BOOLEAN,
                     FOREIGN KEY (brand_id) REFERENCES brand(id),
                     FOREIGN KEY (model_id) REFERENCES model(id),
                     FOREIGN KEY (owner_id) REFERENCES owner(id)
);

-- Create 'accessory' table
CREATE TABLE accessory (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           accessory_name VARCHAR(255) NOT NULL,
                           car_id BIGINT,
                           FOREIGN KEY (car_id) REFERENCES car(id)
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

-- Insert data into 'owner'
INSERT INTO owner (name, lastname) VALUES ('John', 'Doe'), ('Jane', 'Smith'), ('Mike', 'Johnson');

INSERT INTO car (brand_id, model_id, owner_id, year_of_release, mileage, was_in_accident)
VALUES (1, 1, 1, 2020, 15000, false),
       (1, 2, 2, 2019, 25000, false),
       (2, 3, 3, 2018, 35000, true);

INSERT INTO accessory (accessory_name, car_id)
VALUES
    ('USB_CAR_CHARGER', 1),
    ('EMERGENCY_KIT', 1),
    ('GPS_NAVIGATOR', 2),
    ('CAR_ALARM', 3),
    ('BACKUP_CAMERAS', 3);