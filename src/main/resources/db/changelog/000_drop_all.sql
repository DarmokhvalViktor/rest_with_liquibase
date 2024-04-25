-- 000_drop_all.sql
-- This script drops all user-created tables and Liquibase internal tables.
DROP TABLE IF EXISTS car_accessory CASCADE;
DROP TABLE IF EXISTS accessory CASCADE;
DROP TABLE IF EXISTS car CASCADE;
DROP TABLE IF EXISTS model CASCADE;
DROP TABLE IF EXISTS brand CASCADE;
DROP TABLE IF EXISTS owner CASCADE;
