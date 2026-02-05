-- Executado apenas na primeira inicialização do container (quando o volume está vazio).
CREATE DATABASE artista_db;
CREATE DATABASE artista_test;

GRANT ALL PRIVILEGES ON DATABASE artista_db TO meuusuario;
GRANT ALL PRIVILEGES ON DATABASE artista_test TO meuusuario;
