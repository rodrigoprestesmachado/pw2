#! /bin/bash

echo "Compiling CrudWS Angular User Interface"
rm CrudWS/WebContent/index.html
cd angular-crud
ng build --baseHref /CrudWS/
cd dist
cp -r * ../../CrudWS/WebContent/
cd ../../

echo "Compiling and Packaging Java sources for CrudWS project"
cd CrudWS
mvn package -f "pom.xml"
cd ..