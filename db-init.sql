create table public.edge
(
    from_id bigint not null,
    to_id   bigint not null,
    primary key (from_id, to_id)
);
