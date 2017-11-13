DROP TABLE way_point;
CREATE TABLE way_point
(
    created_on        TIMESTAMP     PRIMARY KEY NOT NULL,
    speed             NUMERIC                   NOT NULL,
    latitude          NUMERIC                   NOT NULL,
    longitude         NUMERIC                   NOT NULL,
    UNIQUE(created_on)
);
--CREATE INDEX user__created_on_idx ON "way_point" (created_on);

INSERT INTO way_point(created_on, speed, latitude, longitude)
VALUES(
  to_timestamp(1),
  34.5,
  51.123339,
  31.381227
);
INSERT INTO way_point(created_on, speed, latitude, longitude)
VALUES(
  NOW(),
  70.5,
  51.523339,
  31.331227
);