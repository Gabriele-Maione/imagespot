# Istruzioni per la connessione al database
Per connettersi al database, utilizzare le seguenti credenziali:
```
Host: dpg-cj5sptqcn0vc7384d0sg-a.frankfurt-postgres.render.com
Port: 5432
Database: imagespot
User: imagespot
Password: sxGcPx536GhTuwFeHtBwNEfqFM4lq4rL
```

### Per semplificare la navigazione nel file SQL e trovare rapidamente le sezioni di interesse, può seguire i link diretti alle varie parti del codice:

- [Creazione dei Domini](https://github.com/Gabriele-Maione/imagespot/blob/master/BD/scripts/CreazioneDatabase.sql#L3)
- [Creazione delle Tabelle](https://github.com/Gabriele-Maione/imagespot/blob/master/BD/scripts/CreazioneDatabase.sql#L14)
- [Trigger e Procedure Implementate](https://github.com/Gabriele-Maione/imagespot/blob/master/BD/scripts/CreazioneDatabase.sql#L190)
- [Popolazione del Database](https://github.com/Gabriele-Maione/imagespot/blob/master/BD/scripts/CreazioneDatabase.sql#L368)

### Come configurare il database in locale:

Eseguire lo script SQL [CreazioneDatabase.sql](https://github.com/Gabriele-Maione/imagespot/blob/master/BD/scripts/CreazioneDatabase.sql) nel proprio ambiente locale.
```
psql -h localhost -a -f ../CreazioneDatabase.sql
```
