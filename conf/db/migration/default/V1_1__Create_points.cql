CREATE KEYSPACE waydata
  WITH REPLICATION = {
    'class' : 'SimpleStrategy',
    'replication_factor' : 1
  };

DROP TABLE waydata.user_way_points;

CREATE TABLE waydata.user_way_points (
   user_uuid uuid,
   way_uuid uuid,
   created_on timestamp,
   lat float,
   lon float,
   PRIMARY KEY ((user_uuid, way_uuid), created_on)
)
WITH CLUSTERING ORDER BY (created_on DESC);

TRUNCATE user_way_points;



