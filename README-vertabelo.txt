
Proces wypuszczania wersji.


1. Zwiększ ostatnia liczbę w numerze wersji w pliku VERSION. Zmień tylko XXX  3.7.1.Final-epXXX 

code VERSION

2. Zaktualizuj wersję w plikach pom.xml

make update-pom-xml

3. Zbuduj 

make clean build

4. Wkomituj zmiany

git commit -m ''  -a

5. Postaw taga takiego jak numer wersji

git tag  3.7.1.Final-epXXX

6. Wypchnij zmiany do repozytorium git

git push --tags

7. Skopiuj artefakty do repozytorium vertabelo-repos

make push

8. Wkomituj artefakty 

cd ~/work/vertabelo-repos
git status
git add  .... # nowe pliki
git commit -m "Wersja modeshape'a  3.7.1.Final-epXXX" 
git push



