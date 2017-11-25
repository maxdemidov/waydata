# waydata

Тестовое задание

используя Scala Play создать REST API c 2 endpoints:
- сохранения координаты(lat, lon) с указанием скорости и момента времени
- выборка статистики о передвижении за промежуток времени: средняя скорость, пройденный путь

для подсчета статистики используйте актеры

# scala/sbt/play/akka/slick example 

https://github.com/git-josip/reactive-play-scala-akka-slick-guice-domain_validation-seed

# postgres docker

https://hub.docker.com/_/postgres/

- with exposing port:
1) sudo docker run --name postgres -p 5432:5432 -e POSTGRES_PASSWORD=postgres -d postgres

- and connecting to psql console:
1) sudo docker run -it --rm --link postgres:postgres postgres psql -h postgres -U postgres

# cassandra docker

https://hub.docker.com/_/cassandra/

- with exposing port:
1) sudo docker run --name cassandra -p 9042:9042 -d cassandra

- and connection via cqlsh:
1) sudo docker exec -it cassandra bash
2) exec cqlsh


# calculate distance between two Latitude/Longitude points

https://www.movable-type.co.uk/scripts/latlong.html

