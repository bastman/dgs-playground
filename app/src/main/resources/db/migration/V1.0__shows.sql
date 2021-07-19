CREATE TABLE "public"."shows"
(
    "show_id"      uuid,
    "title"        text NOT NULL,
    "release_year" integer,
    PRIMARY KEY ("show_id")
);
