CREATE TABLE IF NOT EXISTS genre (
                              id int4 NOT NULL,
                              "name" varchar NULL
);

CREATE TABLE IF NOT EXISTS mpa (
                            id int4 NOT NULL,
                            "name" varchar NULL
);

CREATE TABLE IF NOT EXISTS frends (
                               user_id int4 NULL,
                               frend_id int4 NULL,
                               id int4 NULL
);

CREATE TABLE IF NOT EXISTS  film (
                             id int4 NOT NULL,
                             "name" varchar NULL,
                             description varchar NULL,
                             duration int4 NULL,
                             releasedate date NULL,
                             rating int4 NULL,
                             genre_id int4 NULL,
                             mpa_id int4 NULL
);

CREATE TABLE IF NOT EXISTS user (
                               id int4 NOT NULL,
                               email varchar NULL,
                               "name" varchar NULL,
                               login varchar NULL,
                               birthday date NULL
);

CREATE TABLE IF NOT EXISTS filmlikes (
                                  id int4 NOT NULL,
                                  film_id int4 NULL,
                                  user_id int4 NULL
);