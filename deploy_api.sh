#!/bin/bash
if [ "$1" = "-c" ]; then
    cd api/
    heroku login
    heroku container:login
    read -p "Enter app name to create: " cname

    heroku create --app $cname


    read -p "Enter db add-on name: " db_var;
    heroku pg:psql $db_var --app $exname -f ../postgres_init.sql

    mvn clean compile package spring-boot:repackage
    heroku container:push web --app $cname

    heroku container:release web --app $cname
    cd ..
    heroku logs --tail
else
    cd api/
    read -p "Enter existing app name: " exname
    read -p "Init db add-on[y/n]? " initDB
    if [ $initDB = "y" ] ; then
        read -p "Enter db add-on name: " db_var;
        heroku pg:psql $db_var --app $exname -f ../postgres_init.sql
    fi
    mvn clean compile package spring-boot:repackage
    heroku container:push web --app $exname

    heroku container:release web --app $exname
    cd ..
    heroku logs --tail --app $exname
fi
