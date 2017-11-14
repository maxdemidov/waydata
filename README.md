# waydata

Тестовое задание

используя Scala Play создать REST API c 2 endpoints:
- сохранения координаты(lat, lon) с указанием скорости и момента времени
- выборка статистики о передвижении за промежуток времени: средняя скорость, пройденный путь

для подсчета статистики используйте актеры

# postgres docker

https://hub.docker.com/_/postgres/

- sudo docker run --name postgres -p 5432:5432 -e POSTGRES_PASSWORD=postgres -d postgres
- sudo docker run -it --rm --link postgres:postgres postgres psql -h postgres -U postgres
