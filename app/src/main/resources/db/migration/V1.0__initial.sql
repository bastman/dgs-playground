CREATE TABLE "public"."shows"
(
    "show_id"      uuid,
    "title"        text NOT NULL,
    "release_year" integer,
    PRIMARY KEY ("show_id")
);


CREATE TABLE "public"."reviews"
(
    "review_id"    uuid,
    "show_id"      uuid         NOT NULL,
    "submitted_at" timestamp    NOT NULL,
    "username"     varchar(255) NOT NULL,
    "star_rating"  int,
    "comment"      varchar(1024),
    PRIMARY KEY ("review_id"),
    FOREIGN KEY ("show_id") REFERENCES "public"."shows" ("show_id")
);


