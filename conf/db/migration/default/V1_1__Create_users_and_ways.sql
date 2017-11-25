CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- waydata_user
DROP TABLE waydata_user;
CREATE TABLE waydata_user
(
    user_uuid         UUID                      PRIMARY KEY NOT NULL,
    user_name         VARCHAR                               NOT NULL,
    user_email        VARCHAR,
    user_phone        VARCHAR,
    user_created      TIMESTAMP WITH TIME ZONE              NOT NULL,
    CHECK (
      (CASE WHEN user_email IS NULL THEN 0 ELSE 1 END)
        +
      (CASE WHEN user_phone IS NULL THEN 0 ELSE 1 END)
        >=
      1
    ),
    UNIQUE(user_email),
    UNIQUE(user_phone)
);
DELETE FROM waydata_user;
INSERT INTO waydata_user(user_uuid, user_email, user_phone, user_name, user_created)
VALUES(
  uuid_generate_v4(),
  'maxim.a.demidov@gmail.com',
  '097-757-41-51',
  'Maxim Demidov',
  NOW()
);
INSERT INTO waydata_user(user_uuid, user_email, user_phone, user_name, user_created)
VALUES(
  uuid_generate_v4(),
  'vnavozenko@gmail.com',
  null,
  'Valeriy Navozenko',
  NOW()
);
SELECT * FROM waydata_user;

-- waydata_way
DROP TABLE waydata_way;
CREATE TABLE waydata_way
(
    way_uuid         UUID                      PRIMARY KEY NOT NULL,
    user_uuid        UUID                                  NOT NULL,
    way_name         VARCHAR                               NOT NULL,
    way_created      TIMESTAMP WITH TIME ZONE              NOT NULL,
    FOREIGN KEY (user_uuid) REFERENCES waydata_user (user_uuid),
    UNIQUE(way_name)
);
DELETE FROM waydata_way;
INSERT INTO waydata_way(way_uuid, user_uuid, way_name, way_created)
VALUES(
  uuid_generate_v4(),
  (SELECT user_uuid FROM waydata_user WHERE user_phone = '097-757-41-51'),
  '21.10.2017 Fri 11:40:53 AM EET',
  NOW()
);
INSERT INTO waydata_way(way_uuid, user_uuid, way_name, way_created)
VALUES(
  uuid_generate_v4(),
  (SELECT user_uuid FROM waydata_user WHERE user_email = 'vnavozenko@gmail.com'),
  '04.11.2017 Sat 01:12:03 PM CET',
  NOW()
);
SELECT * FROM waydata_way;
