INSERT INTO public.filter (id, name, selection, created_at)
VALUES (NEXTVAL('filter_id_seq'), 'First', 'SELECTION_2', now());

INSERT INTO public.criteria (id, filter_id, amount, title, date, created_at)
VALUES
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), 2, null, null, now()),
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), null, 'Meow', null, now()),
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), null, null, now(), now());

INSERT INTO public.filter (id, name, selection, created_at)
VALUES (NEXTVAL('filter_id_seq'), 'Second', 'SELECTION_3', now());

INSERT INTO public.criteria (id, filter_id, amount, title, date, created_at)
VALUES
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), 8, null, null, now()),
    (NEXTVAL('criteria_id_seq'), CURRVAL('filter_id_seq'), null, 'Woof!', null, now());
