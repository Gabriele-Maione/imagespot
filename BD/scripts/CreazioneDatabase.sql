CREATE EXTENSION IF NOT EXISTS  pgcrypto;

--Definizione del dominio per gli indirizzi email
CREATE DOMAIN emailt AS VARCHAR(64)
    CONSTRAINT email_check CHECK ((VALUE)::TEXT ~~ '_%@_%._%'::TEXT);

--Definizione del dominio per le risoluzioni
CREATE DOMAIN resolutiont AS VARCHAR(16)
    CONSTRAINT resolution_check CHECK ((VALUE)::TEXT ~~ '_%x_%'::TEXT);

--Definizione del tipo enumerato per lo stato dei post
CREATE TYPE statust AS ENUM ('Public', 'Private');

--tabella Account
CREATE TABLE IF NOT EXISTS ACCOUNT
(
    username VARCHAR(16)  NOT NULL
        PRIMARY KEY,
    email    emailt       NOT NULL
        UNIQUE,
    name     VARCHAR(16)  NOT NULL,
    password VARCHAR(128) NOT NULL,
    gender   VARCHAR(30),
    bio      TEXT,
    avatar   BYTEA,
    salt     VARCHAR(32)  NOT NULL
);

--tabella Device
CREATE TABLE IF NOT EXISTS DEVICE
(
    iddevice   SERIAL
        PRIMARY KEY,
    brand      VARCHAR(64) NOT NULL,
    model      VARCHAR(64) NOT NULL,
    devicetype VARCHAR(30) NOT NULL
);

--tabella Post
CREATE TABLE IF NOT EXISTS POST
(
    idimage      SERIAL
        PRIMARY KEY,
    photo        VARCHAR(100)                        NOT NULL,
    resolution   resolutiont                         NOT NULL,
    size         INTEGER                             NOT NULL,
    extension    VARCHAR(10)                         NOT NULL,
    posting_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    description  TEXT,
    status       statust                             NOT NULL,
    device       INTEGER                             NOT NULL
        REFERENCES device,
    profile      VARCHAR(16)
        REFERENCES account,
    preview      VARCHAR(100)                        NOT NULL
);

--tabella Bookmark
CREATE TABLE IF NOT EXISTS BOOKMARK
(
    username VARCHAR(16)                         NOT NULL
        REFERENCES account,
    idimage  INTEGER                             NOT NULL
        REFERENCES post
            ON DELETE CASCADE,
    date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (username, idimage)
);

--tabella Tagged_User
CREATE TABLE IF NOT EXISTS TAGGED_USER
(
    nickname VARCHAR(16) NOT NULL
        REFERENCES account,
    idimage  INTEGER     NOT NULL
        REFERENCES post
            ON DELETE CASCADE,
    PRIMARY KEY (nickname, idimage)
);

--tabella Following
CREATE TABLE IF NOT EXISTS FOLLOWING
(
    nickname    VARCHAR(16) NOT NULL
        REFERENCES account,
    idfollowing VARCHAR(16) NOT NULL
        REFERENCES account,
    followdate  TIMESTAMP   NOT NULL,
    PRIMARY KEY (nickname, idfollowing)
);

--tabella User_Device
CREATE TABLE IF NOT EXISTS USER_DEVICE
(
    device  INTEGER     NOT NULL
        REFERENCES device,
    profile VARCHAR(16) NOT NULL
        REFERENCES account,
    PRIMARY KEY (device, profile)
);

--tabella Collection
CREATE TABLE IF NOT EXISTS COLLECTION
(
    idcollection SERIAL
        PRIMARY KEY,
    name         VARCHAR(64) DEFAULT 'New Album'::CHARACTER varying NOT NULL,
    description  TEXT,
    owner        VARCHAR(16)                                        NOT NULL
        REFERENCES account
);

--tabella Collection_Post
CREATE TABLE IF NOT EXISTS COLLECTION_POST
(
    collection SERIAL
        REFERENCES collection
            ON DELETE CASCADE,
    post       SERIAL
        REFERENCES post,
    date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (collection, post)
);

--tabella Subject
CREATE TABLE IF NOT EXISTS SUBJECT
(
    subject_id SERIAL
        PRIMARY KEY,
    name       VARCHAR(32) NOT NULL,
    category   VARCHAR(32) NOT NULL
);

--tabella Subject_Post
CREATE TABLE IF NOT EXISTS SUBJECT_POST
(
    subject INTEGER NOT NULL
        REFERENCES subject
            ON DELETE CASCADE,
    post    INTEGER NOT NULL
        REFERENCES post
            ON UPDATE cascade ON DELETE CASCADE,
    PRIMARY KEY (subject, post)
);

--tabella Location
CREATE TABLE IF NOT EXISTS LOCATION
(
    idlocation        SERIAL
        PRIMARY KEY,
    country           VARCHAR(64),
    state             VARCHAR(64),
    city              VARCHAR(64),
    postcode          VARCHAR(64),
    latitude          NUMERIC(9, 6) NOT NULL,
    longitude         NUMERIC(9, 6) NOT NULL,
    formatted_address VARCHAR(200)  NOT NULL,
    road              VARCHAR(64),
    post              INTEGER
        REFERENCES post
            ON UPDATE cascade ON DELETE CASCADE
);

--View per le statistiche della collezione
CREATE OR REPLACE VIEW collection_stats(idcollection, posts, members) AS
SELECT collection.idcollection,
       count(post.idimage)          AS posts,
       count(DISTINCT post.profile) AS members
FROM collection
         LEFT JOIN collection_post ON collection.idcollection = collection_post.collection
         LEFT JOIN post ON collection_post.post = post.idimage
GROUP BY collection.idcollection;

--View per le statistiche pubbliche dell ’ utente
CREATE OR REPLACE VIEW user_stats(username, post, follower, following) AS
SELECT a.username,
       (SELECT count(post.idimage) AS post
        FROM post
        WHERE post.profile::TEXT = a.username::TEXT
          AND post.status = 'Public'::statust)                AS post,
       (SELECT count(*) AS follower
        FROM following
        WHERE following.idfollowing::TEXT = a.username::TEXT) AS follower,
       (SELECT count(*) AS following
        FROM following
        WHERE following.nickname::TEXT = a.username::TEXT)    AS following
FROM account a;

-- funzione per fare il hash della password prima di inserirla
CREATE OR REPLACE FUNCTION hash_password() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
BEGIN
    NEW.salt := gen_salt('bf');
    NEW.password := crypt(NEW.password, NEW.salt);
    RETURN NEW;
END;
$$;

CREATE TRIGGER hash_password_trigger
    BEFORE INSERT OR UPDATE
        OF password
    ON account
    FOR EACH ROW
EXECUTE PROCEDURE hash_password();

-- funzione per inserire un soggetto nel post
CREATE OR REPLACE FUNCTION insert_subject_post(subject_name CHARACTER varying, category_name CHARACTER varying, image_id INTEGER) RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    subjectID INTEGER;
BEGIN
    BEGIN
        SELECT subject_id
        INTO subjectID
        FROM subject
        WHERE name = subject_name
          AND category = category_name;
        IF (subjectID IS NULL) THEN
            INSERT INTO subject (name, category)
            VALUES (subject_name, category_name)
            RETURNING subject_id INTO subjectID;
        END IF;
    END;

    INSERT INTO subject_post (subject, post)
    VALUES (subjectID, image_id);
END;
$$;

--funzione per inserire un nuovo utente durante la registrazione
CREATE OR REPLACE FUNCTION insert_account(p_username CHARACTER varying, p_email emailt, p_name CHARACTER varying, p_password CHARACTER varying) RETURNS INTEGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    username_c INTEGER;
    email_c    INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO username_c
    FROM account
    WHERE username = p_username;

    SELECT COUNT(*)
    INTO email_c
    FROM account
    WHERE email = p_email;

    IF username_c = 0 AND email_c = 0 THEN
        INSERT INTO account (username, name, password, email)
        VALUES (p_username, p_name, p_password, p_email);
        RETURN 1;
    ELSIF username_c > 0 AND email_c > 0 THEN
        RETURN 0;
    ELSIF username_c > 0 THEN
        RETURN -1;
    ELSE
        RETURN -2;
    END IF;
END;
$$;

--funzione per eliminare i post privati dalle collections e bookmarks
CREATE OR REPLACE FUNCTION delete_post_from_collections_and_bookmarks() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
BEGIN
    IF OLD.status = 'Public'::statust AND NEW.status = 'Private'::statust THEN
        -- Delete post from collections
        DELETE FROM collection_post
        WHERE collection_post.post = OLD.idimage;

        -- Delete post from bookmarks
        DELETE FROM bookmark
        WHERE bookmark.idimage = OLD.idimage;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER delete_from_collections_and_bookmarks_trigger
    BEFORE UPDATE
        OF status
    ON post
    FOR EACH ROW
EXECUTE PROCEDURE delete_post_from_collections_and_bookmarks();

--funzione per eliminare un post non presente in nessuna collection, altrimenti imposta il profile a null
CREATE OR REPLACE FUNCTION update_post_profile() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
DECLARE
    post_id INTEGER = NULL;
BEGIN
    SELECT post INTO post_id FROM collection_post WHERE post = OLD.idimage;

    IF (post_id IS NOT NULL) THEN
        UPDATE post
        SET profile = NULL
        WHERE post.idimage = OLD.idimage;
    ELSE
        DELETE FROM post WHERE idimage = old.idimage;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER update_post_profile_trigger
    BEFORE DELETE
    ON post
    FOR EACH ROW
    WHEN (pg_trigger_depth() = 0)
EXECUTE PROCEDURE update_post_profile();

--funzione per eliminare i post senza profile e non associati a nessuna collection
CREATE OR REPLACE FUNCTION delete_orphaned_posts() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
BEGIN
    DELETE FROM post
    WHERE profile IS NULL
      AND idimage NOT IN (
        SELECT post
        FROM collection_post
      );

    RETURN OLD;
END;
$$;

CREATE TRIGGER delete_collection_trigger
    AFTER DELETE
    ON collection
    FOR EACH ROW
EXECUTE PROCEDURE delete_orphaned_posts();

--funzione per eliminare i subject non associati ad alcun post
CREATE OR REPLACE FUNCTION delete_subject() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
BEGIN
    DELETE FROM subject
    WHERE subject_id NOT IN (
        SELECT subject
        FROM subject_post
      );

    RETURN OLD;
END;
$$;

CREATE TRIGGER delete_subject_trigger
    AFTER DELETE
    ON subject_post
    FOR EACH ROW
EXECUTE PROCEDURE delete_subject();

--Insert account
INSERT INTO account (username, email, name, password, gender, bio, avatar) VALUES ('gerry', 'gerryscotti@thewall.com', 'Gerry Scotti', '12345678', NULL, NULL, NULL);
INSERT INTO account (username, email, name, password, gender, bio, avatar) VALUES ('W.W.', 'weneedtocook@jesse.com', 'Gualtiero Bianco', '12345678', NULL, NULL, NULL);
INSERT INTO account (username, email, name, password, gender, bio, avatar) VALUES ('gab', 'gab@gmail.com', 'Gab', '12345678', 'Orco', 'Don''t judge me or my swamp until you''ve walked a mile in my muddy boots!', NULL);
INSERT INTO account (username, email, name, password, gender, bio, avatar) VALUES ('Fel', 'felcon@gmail.com', 'FelCon', '12345678', NULL, NULL, NULL);
INSERT INTO account (username, email, name, password, gender, bio, avatar) VALUES ('saul', 'bettercallsaul@bts.com', 'Saulo Buon Uomo', '12345678', NULL, NULL, NULL);
INSERT INTO account (username, email, name, password, gender, bio, avatar) VALUES ('gabibbo', 'gabibbo@leveline.org', 'Gabibbo', '12345678', 'Pupazzo', 'LE VELINEEE!', NULL);
INSERT INTO account (username, email, name, password, gender, bio, avatar) VALUES ('elonio', 'eloniomuschio@twitter.com', 'Elonio Muschio', '12345678', NULL, NULL, NULL);

--Insert following
INSERT INTO following (nickname, idfollowing, followdate) VALUES ('gab', 'Fel', '2023-09-05 15:48:56.196154');
INSERT INTO following (nickname, idfollowing, followdate) VALUES ('gab', 'gabibbo', '2023-09-05 15:48:56.197805');
INSERT INTO following (nickname, idfollowing, followdate) VALUES ('elonio', 'gab', '2023-09-05 15:48:56.200183');
INSERT INTO following (nickname, idfollowing, followdate) VALUES ('elonio', 'W.W.', '2023-09-05 15:48:56.200393');
INSERT INTO following (nickname, idfollowing, followdate) VALUES ('gabibbo', 'saul', '2023-09-05 15:48:56.201990');
INSERT INTO following (nickname, idfollowing, followdate) VALUES ('gerry', 'gab', '2023-09-05 15:48:56.202946');
INSERT INTO following (nickname, idfollowing, followdate) VALUES ('Fel', 'elonio', '2023-09-05 15:48:56.203291');
INSERT INTO following (nickname, idfollowing, followdate) VALUES ('saul', 'gab', '2023-09-05 15:48:56.203600');
INSERT INTO following (nickname, idfollowing, followdate) VALUES ('saul', 'elonio', '2023-09-05 15:48:56.203754');
INSERT INTO following (nickname, idfollowing, followdate) VALUES ('W.W.', 'gab', '2023-09-05 15:48:56.203121');
INSERT INTO following (nickname, idfollowing, followdate) VALUES ('Fel', 'W.W.', '2023-09-05 15:48:56.203449');

--Insert device
INSERT INTO device (iddevice, brand, model, devicetype) VALUES (1, 'APPLE', 'iphone 12', 'Smartphone');
INSERT INTO device (iddevice, brand, model, devicetype) VALUES (2, 'SAMSUNG', 's21', 'Smartphone');
INSERT INTO device (iddevice, brand, model, devicetype) VALUES (3, 'XIAOMI', 'mi 11', 'Smartphone');
INSERT INTO device (iddevice, brand, model, devicetype) VALUES (4, 'CANON', 'powershot g7x', 'Digital Camera');
INSERT INTO device (iddevice, brand, model, devicetype) VALUES (5, 'SONY', 'cyber-shot', 'Digital Camera');
INSERT INTO device (iddevice, brand, model, devicetype) VALUES (6, 'GOOGLE', 'pixel 4a', 'Smartphone');
INSERT INTO device (iddevice, brand, model, devicetype) VALUES (7, 'GOOGLE', 'pixel 5', 'Smartphone');
INSERT INTO device (iddevice, brand, model, devicetype) VALUES (8, 'SONY', 'cyber-shot rx100', 'Digital Camera');
INSERT INTO device (iddevice, brand, model, devicetype) VALUES (9, 'SONY', 'alpha a7', 'Digital Camera');
INSERT INTO device (iddevice, brand, model, devicetype) VALUES (10, 'NIKON', 'coolpix p900', 'Digital Camera');
INSERT INTO device (iddevice, brand, model, devicetype) VALUES (11, 'FUJIFILM', 'x-t4', 'Digital Camera');

--Insert user_device
INSERT INTO user_device (device, profile) VALUES (2, 'gab');
INSERT INTO user_device (device, profile) VALUES (4, 'gab');
INSERT INTO user_device (device, profile) VALUES (9, 'gab');
INSERT INTO user_device (device, profile) VALUES (1, 'elonio');
INSERT INTO user_device (device, profile) VALUES (11, 'gabibbo');
INSERT INTO user_device (device, profile) VALUES (3, 'gerry');
INSERT INTO user_device (device, profile) VALUES (9, 'gerry');
INSERT INTO user_device (device, profile) VALUES (4, 'gerry');
INSERT INTO user_device (device, profile) VALUES (10, 'W.W.');
INSERT INTO user_device (device, profile) VALUES (2, 'W.W.');
INSERT INTO user_device (device, profile) VALUES (9, 'Fel');
INSERT INTO user_device (device, profile) VALUES (11, 'Fel');
INSERT INTO user_device (device, profile) VALUES (10, 'saul');

--Insert post
INSERT INTO post (idimage, photo, resolution, size, extension, posting_date, description, status, device, profile, preview) VALUES (1, 'https://myapplications.altervista.org/imagespot/upload/64f6514a3f6cc.jpeg', '1080x1513', 257, 'jpeg', '2023-05-06 20:26:15.000000', NULL, 'Public', 2, 'saul', 'https://myapplications.altervista.org/imagespot/upload/64f6514a3f6cc_preview.jpeg');
INSERT INTO post (idimage, photo, resolution, size, extension, posting_date, description, status, device, profile, preview) VALUES (2, 'https://myapplications.altervista.org/imagespot/upload/64f6514e65ddb.jpeg', '1080x810', 375, 'jpeg', '2023-07-01 10:57:02.000000', NULL, 'Public', 4, 'Fel', 'https://myapplications.altervista.org/imagespot/upload/64f6514e65ddb_preview.jpeg');
INSERT INTO post (idimage, photo, resolution, size, extension, posting_date, description, status, device, profile, preview) VALUES (3, 'https://myapplications.altervista.org/imagespot/upload/64f65157afb0b.jpeg', '1080x720', 61, 'jpeg', '2023-06-03 04:33:26.000000', NULL, 'Public', 11, 'saul', 'https://myapplications.altervista.org/imagespot/upload/64f65157afb0b_preview.jpeg');
INSERT INTO post (idimage, photo, resolution, size, extension, posting_date, description, status, device, profile, preview) VALUES (4, 'https://myapplications.altervista.org/imagespot/upload/64f6515a3fbfa.jpeg', '1080x720', 100, 'jpeg', '2023-08-22 23:36:31.000000', NULL, 'Public', 2, 'elonio', 'https://myapplications.altervista.org/imagespot/upload/64f6515a3fbfa_preview.jpeg');
INSERT INTO post (idimage, photo, resolution, size, extension, posting_date, description, status, device, profile, preview) VALUES (5, 'https://myapplications.altervista.org/imagespot/upload/64f6515c5e97e.jpeg', '1080x720', 284, 'jpeg', '2023-06-26 05:28:46.000000', NULL, 'Public', 1, 'Fel', 'https://myapplications.altervista.org/imagespot/upload/64f6515c5e97e_preview.jpeg');
INSERT INTO post (idimage, photo, resolution, size, extension, posting_date, description, status, device, profile, preview) VALUES (6, 'https://myapplications.altervista.org/imagespot/upload/64f6515e75930.jpeg', '1080x1620', 394, 'jpeg', '2023-04-23 05:46:58.000000', NULL, 'Public', 9, 'saul', 'https://myapplications.altervista.org/imagespot/upload/64f6515e75930_preview.jpeg');
INSERT INTO post (idimage, photo, resolution, size, extension, posting_date, description, status, device, profile, preview) VALUES (7, 'https://myapplications.altervista.org/imagespot/upload/64f6516945d25.jpeg', '1080x810', 248, 'jpeg', '2023-07-31 23:24:39.000000', NULL, 'Public', 6, 'elonio', 'https://myapplications.altervista.org/imagespot/upload/64f6516945d25_preview.jpeg');
INSERT INTO post (idimage, photo, resolution, size, extension, posting_date, description, status, device, profile, preview) VALUES (8, 'https://myapplications.altervista.org/imagespot/upload/64f651de8e2ad.jpeg', '1080x1620', 245, 'jpeg', '2023-01-30 02:01:31.000000', NULL, 'Public', 4, 'W.W.', 'https://myapplications.altervista.org/imagespot/upload/64f651de8e2ad_preview.jpeg');
INSERT INTO post (idimage, photo, resolution, size, extension, posting_date, description, status, device, profile, preview) VALUES (9, 'https://myapplications.altervista.org/imagespot/upload/64f6516deb514.jpeg', '1080x721', 105, 'jpeg', '2023-06-26 20:02:42.000000', NULL, 'Public', 7, 'gab', 'https://myapplications.altervista.org/imagespot/upload/64f6516deb514_preview.jpeg');
INSERT INTO post (idimage, photo, resolution, size, extension, posting_date, description, status, device, profile, preview) VALUES (10, 'https://myapplications.altervista.org/imagespot/upload/64f651702a254.jpeg', '1080x1350', 142, 'jpeg', '2023-04-15 16:28:49.000000', NULL, 'Public', 3, 'W.W.', 'https://myapplications.altervista.org/imagespot/upload/64f651702a254_preview.jpeg');

--Insert location
INSERT INTO location (idlocation, country, state, city, postcode, latitude, longitude, formatted_address, road, post) VALUES (1, 'Egypt', 'Cairo', 'Cairo', '11577', 30.062634, 31.249668, 'Ramsis Street, Al Qubaisy, Cairo, 11577, Egypt', 'Ramsis Street', 2);
INSERT INTO location (idlocation, country, state, city, postcode, latitude, longitude, formatted_address, road, post) VALUES (2, 'Thailand', 'Bangkok', 'Bangkok', '10200', 13.754141, 100.502188, 'Bangkok City Hall, Siriphong Road, Phra Nakhon District, Bangkok 10200, Thailand', 'Siriphong Road', 3);
INSERT INTO location (idlocation, country, state, city, postcode, latitude, longitude, formatted_address, road, post) VALUES (3, 'China', 'Hong Kong', 'Hong Kong Island', NULL, 22.284202, 114.158839, 'China, Hong Kong, Hong Kong Island, Central and Western District, Finance Street 8, Hong Kong Station', 'Finance Street', 5);
INSERT INTO location (idlocation, country, state, city, postcode, latitude, longitude, formatted_address, road, post) VALUES (4, 'United States', 'Florida', 'Jacksonville', '32202', 30.332257, -81.655755, '23 Union Street, Jacksonville, FL 32202, United States of America', 'Union Street', 6);
INSERT INTO location (idlocation, country, state, city, postcode, latitude, longitude, formatted_address, road, post) VALUES (5, 'Spain', 'Catalonia', 'Barcelona', '08001', 41.388851, 2.159066, 'Carrer d''Aribau, 66, 08001 Barcelona, Spain', 'Carrer d''Aribau', 7);
INSERT INTO location (idlocation, country, state, city, postcode, latitude, longitude, formatted_address, road, post) VALUES (6, 'United States', 'New York', 'New York', '10007', 40.714210, -74.005624, 'Sun Building, 280 Broadway, New York, NY 10007, United States of America', 'Broadway', 8);
INSERT INTO location (idlocation, country, state, city, postcode, latitude, longitude, formatted_address, road, post) VALUES (7, 'Italy', 'Lombardy', 'Milan', '20122', 45.464197, 9.189261, 'Monumento a Vittorio Emanuele II, 1_33051, 20122 Milan MI, Italy', '1_33051', 9);
INSERT INTO location (idlocation, country, state, city, postcode, latitude, longitude, formatted_address, road, post) VALUES (8, 'France', 'Ile-de-France', 'Paris', '75004', 48.853325, 2.348860, 'Point zéro des Routes de France, Parvis Notre-Dame - Place Jean-Paul II, 75004 Paris, France', 'Parvis Notre-Dame - Place Jean-Paul II', 10);

--Insert bookmark
INSERT INTO bookmark (username, idimage, date) VALUES ('gab', 2, '2023-09-05 12:24:50.863775');
INSERT INTO bookmark (username, idimage, date) VALUES ('gab', 5, '2023-09-05 12:24:50.865569');
INSERT INTO bookmark (username, idimage, date) VALUES ('elonio', 1, '2023-09-05 12:24:50.874443');
INSERT INTO bookmark (username, idimage, date) VALUES ('elonio', 9, '2023-09-05 12:24:50.874616');
INSERT INTO bookmark (username, idimage, date) VALUES ('gabibbo', 3, '2023-09-05 12:24:50.888489');
INSERT INTO bookmark (username, idimage, date) VALUES ('gabibbo', 10, '2023-09-05 12:24:50.888691');
INSERT INTO bookmark (username, idimage, date) VALUES ('gerry', 2, '2023-09-05 12:24:50.893835');
INSERT INTO bookmark (username, idimage, date) VALUES ('W.W.', 9, '2023-09-05 12:24:50.908043');
INSERT INTO bookmark (username, idimage, date) VALUES ('W.W.', 4, '2023-09-05 12:24:50.908320');
INSERT INTO bookmark (username, idimage, date) VALUES ('Fel', 3, '2023-09-05 12:24:50.909500');
INSERT INTO bookmark (username, idimage, date) VALUES ('Fel', 5, '2023-09-05 12:24:50.909734');
INSERT INTO bookmark (username, idimage, date) VALUES ('Fel', 7, '2023-09-05 12:24:50.911078');
INSERT INTO bookmark (username, idimage, date) VALUES ('saul', 1, '2023-09-05 12:24:50.917911');

--Insert subject
INSERT INTO subject (subject_id, name, category) VALUES (1, 'Deep-sea Exploration', 'Underwater');
INSERT INTO subject (subject_id, name, category) VALUES (2, 'Starry Night', 'Astrophotography');
INSERT INTO subject (subject_id, name, category) VALUES (3, 'Model Elegance', 'Fashion');
INSERT INTO subject (subject_id, name, category) VALUES (4, 'Burgers', 'Food');
INSERT INTO subject (subject_id, name, category) VALUES (5, 'Mountain Trek', 'Travel');
INSERT INTO subject (subject_id, name, category) VALUES (6, 'Shipwreck Discovery', 'Underwater');

--Insert subject_post
INSERT INTO subject_post (subject, post) VALUES (1, 1);
INSERT INTO subject_post (subject, post) VALUES (1, 2);
INSERT INTO subject_post (subject, post) VALUES (2, 4);
INSERT INTO subject_post (subject, post) VALUES (3, 10);
INSERT INTO subject_post (subject, post) VALUES (4, 8);
INSERT INTO subject_post (subject, post) VALUES (5, 8);
INSERT INTO subject_post (subject, post) VALUES (6, 1);

--Insert tagged_user
INSERT INTO tagged_user (nickname, idimage) VALUES ('Fel', 2);
INSERT INTO tagged_user (nickname, idimage) VALUES ('W.W.', 2);
INSERT INTO tagged_user (nickname, idimage) VALUES ('gab', 5);
INSERT INTO tagged_user (nickname, idimage) VALUES ('elonio', 5);
INSERT INTO tagged_user (nickname, idimage) VALUES ('gerry', 7);
INSERT INTO tagged_user (nickname, idimage) VALUES ('elonio', 7);
INSERT INTO tagged_user (nickname, idimage) VALUES ('gerry', 8);
INSERT INTO tagged_user (nickname, idimage) VALUES ('W.W.', 8);
INSERT INTO tagged_user (nickname, idimage) VALUES ('W.W.', 9);
INSERT INTO tagged_user (nickname, idimage) VALUES ('gabibbo', 9);

--Insert collection
INSERT INTO collection (idcollection, name, description, owner) VALUES (1, 'Natural Wonders', 'A collection of nature''s most awe-inspiring sights.', 'saul');
INSERT INTO collection (idcollection, name, description, owner) VALUES (2, 'Culinary Delights', 'Delightful culinary experiences.', 'W.W.');
INSERT INTO collection (idcollection, name, description, owner) VALUES (3, 'Sports Action', 'Action-packed moments in the world of sports.', 'gerry');
INSERT INTO collection (idcollection, name, description, owner) VALUES (4, 'Fashion Icons', 'Iconic figures in the world of fashion.', 'Fel');
INSERT INTO collection (idcollection, name, description, owner) VALUES (5, 'Astrophotography Collection', 'A collection of captivating astrophotography.', 'gab');

--Insert collection_post
INSERT INTO collection_post (collection, post, date) VALUES (1, 1, '2023-09-06 16:52:05.114507');
INSERT INTO collection_post (collection, post, date) VALUES (1, 2, '2023-09-06 16:52:05.114707');
INSERT INTO collection_post (collection, post, date) VALUES (1, 4, '2023-09-06 16:52:05.115092');
INSERT INTO collection_post (collection, post, date) VALUES (2, 8, '2023-09-06 16:52:05.115334');
INSERT INTO collection_post (collection, post, date) VALUES (4, 10, '2023-09-06 16:52:05.115603');
INSERT INTO collection_post (collection, post, date) VALUES (5, 3, '2023-09-06 16:52:05.115842');
INSERT INTO collection_post (collection, post, date) VALUES (5, 4, '2023-09-06 16:52:05.120960');
INSERT INTO collection_post (collection, post, date) VALUES (5, 5, '2023-09-06 16:52:05.121109');
