CREATE EXTENSION pgcrypto;

create domain emailt as varchar(64)
    constraint email_check check ((VALUE)::text ~~ '_%@_%._%'::text);

create domain resolutiont as varchar(16)
    constraint resolution_check check ((VALUE)::text ~~ '_%x_%'::text);

create type statust as enum ('Public', 'Private');

create table if not exists account
(
    username varchar(16)  not null
        primary key,
    email    emailt       not null
        unique,
    name     varchar(16)  not null,
    password varchar(128) not null,
    gender   varchar(30),
    bio      text,
    avatar   bytea,
    salt     varchar(32)  not null
);

create table if not exists device
(
    iddevice   serial
        primary key,
    brand      varchar(64) not null,
    model      varchar(64) not null,
    devicetype varchar(30) not null
);

create table if not exists post
(
    idimage      serial
        primary key,
    photo        varchar(100)                        not null,
    resolution   resolutiont                         not null,
    size         integer                             not null,
    extension    varchar(10)                         not null,
    posting_date timestamp default CURRENT_TIMESTAMP not null,
    description  text,
    status       statust                             not null,
    device       integer                             not null
        references device,
    profile      varchar(16)
        references account,
    preview      varchar(100)                        not null
);

create table if not exists bookmark
(
    username varchar(16)                         not null
        references account,
    idimage  integer                             not null
        references post
            on delete cascade,
    date     timestamp default CURRENT_TIMESTAMP not null,
    primary key (username, idimage)
);

create table if not exists tagged_user
(
    nickname varchar(16) not null
        references account,
    idimage  integer     not null
        references post
            on delete cascade,
    primary key (nickname, idimage)
);

create table if not exists following
(
    nickname    varchar(16) not null
        references account,
    idfollowing varchar(16) not null
        references account,
    followdate  timestamp   not null,
    primary key (nickname, idfollowing)
);

create table if not exists user_device
(
    device  integer     not null
        references device,
    profile varchar(16) not null
        references account,
    primary key (device, profile)
);

create table if not exists collection
(
    idcollection serial
        primary key,
    name         varchar(64) default 'New Album'::character varying not null,
    description  text,
    owner        varchar(16)                                        not null
        references account
);

create table if not exists collection_post
(
    collection serial
        references collection
            on delete cascade,
    post       serial
        references post,
    date       timestamp default CURRENT_TIMESTAMP not null,
    primary key (collection, post)
);

create table if not exists subject
(
    subject_id serial
        primary key,
    name       varchar(32) not null,
    category   varchar(32) not null
);

create table if not exists subject_post
(
    subject integer not null
        references subject
            on delete cascade,
    post    integer not null
        references post
            on update cascade on delete cascade,
    primary key (subject, post)
);

create table if not exists location
(
    idlocation        serial
        primary key,
    country           varchar(64),
    state             varchar(64),
    city              varchar(64),
    postcode          varchar(64),
    latitude          numeric(9, 6) not null,
    longitude         numeric(9, 6) not null,
    formatted_address varchar(200)  not null,
    road              varchar(64),
    post              integer
        references post
            on update cascade on delete cascade
);

create or replace view collection_stats(idcollection, posts, members) as
SELECT collection.idcollection,
       count(post.idimage)          AS posts,
       count(DISTINCT post.profile) AS members
FROM collection
         LEFT JOIN collection_post ON collection.idcollection = collection_post.collection
         LEFT JOIN post ON collection_post.post = post.idimage
GROUP BY collection.idcollection;

create or replace view user_stats(username, post, follower, following) as
SELECT a.username,
       (SELECT count(post.idimage) AS post
        FROM post
        WHERE post.profile::text = a.username::text
          AND post.status = 'Public'::statust)                AS post,
       (SELECT count(*) AS follower
        FROM following
        WHERE following.idfollowing::text = a.username::text) AS follower,
       (SELECT count(*) AS following
        FROM following
        WHERE following.nickname::text = a.username::text)    AS following
FROM account a;

create or replace function hash_password() returns trigger
    language plpgsql
as
$$
BEGIN
    NEW.salt := gen_salt('bf');
    NEW.password := crypt(NEW.password, NEW.salt);
    RETURN NEW;
END;
$$;

create trigger hash_password_trigger
    before insert or update
        of password
    on account
    for each row
execute procedure hash_password();

create or replace function insert_subject_post(subject_name character varying, category_name character varying, image_id integer) returns void
    language plpgsql
as
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

create or replace function insert_account(p_username character varying, p_email emailt, p_name character varying, p_password character varying) returns integer
    language plpgsql
as
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

create or replace function update_user_in_collection() returns trigger
    language plpgsql
as
$$
DECLARE
    post_id INTEGER;
BEGIN
    SELECT post INTO post_id FROM collection_post WHERE post = OLD.idimage;

    IF (post_id IS NOT NULL) THEN
        UPDATE post
        SET profile = NULL
        WHERE idimage = OLD.idimage;
    ELSE
        DELETE FROM post WHERE idimage = OLD.idimage;
    END IF;
    RETURN NEW;
END;
$$;

create or replace function delete_post_from_collections_and_bookmarks() returns trigger
    language plpgsql
as
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

create trigger delete_from_collections_and_bookmarks_trigger
    before update
        of status
    on post
    for each row
execute procedure delete_post_from_collections_and_bookmarks();

create or replace function update_or_delete_user(p_idimage integer) returns void
    language plpgsql
as
$$
DECLARE
    post_id INTEGER;
BEGIN
    SELECT post INTO post_id FROM collection_post WHERE post = p_idimage;

    IF (post_id IS NOT NULL) THEN
        UPDATE post
        SET profile = NULL
        WHERE idimage = p_idimage;
    ELSE
        DELETE FROM post WHERE idimage = p_idimage;
    END IF;
END;
$$;

create or replace function update_post_profile() returns trigger
    language plpgsql
as
$$
DECLARE
    post_id INTEGER = null;
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

create trigger update_post_profile_trigger
    before delete
    on post
    for each row
    when (pg_trigger_depth() = 0)
execute procedure update_post_profile();

create or replace function delete_orphaned_posts() returns trigger
    language plpgsql
as
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

create trigger delete_collection_trigger
    after delete
    on collection
    for each row
execute procedure delete_orphaned_posts();

create or replace function delete_subject() returns trigger
    language plpgsql
as
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

create trigger delete_subject_trigger
    after delete
    on subject_post
    for each row
execute procedure delete_subject();

